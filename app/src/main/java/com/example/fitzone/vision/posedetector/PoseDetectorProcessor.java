/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.fitzone.vision.posedetector;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.fitzone.activites.HomeActivity;
import com.example.fitzone.activites.R;
import com.example.fitzone.activites.TimerActivity;
import com.example.fitzone.common_functions.StaticFunctions;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.get_challenge_by_id.ChalengeByIdResponse;
import com.example.fitzone.retrofit_requists.data_models.increament_reps_by_one.IncrementRepsByOne;
import com.example.fitzone.vision.GraphicOverlay;
import com.example.fitzone.vision.VisionProcessorBase;
import com.example.fitzone.vision.posedetector.classification.PoseClassifierProcessor;
import com.google.android.gms.tasks.Task;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A processor to run pose detector.
 */
public class PoseDetectorProcessor
        extends VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification> {
    private static final String TAG = "PoseDetectorProcessor";

    private final PoseDetector detector;

    private final boolean showInFrameLikelihood;
    private final boolean visualizeZ;
    private final boolean rescaleZForVisualization;
    private final boolean runClassification;
    private final boolean isStreamMode;
    private final Context context;
    private final Activity activity;
    private final Executor classificationExecutor;

    private int currentTakenTime;
    private long startTime;
    private long endTime;

    //training data to focus on it

    private final String trainingName;
    private final Integer trainingReps;
    private final Integer trainingSets;
    private Integer trainingSetNumber;
    private final Integer lastTime;
    private final Integer challengeId;

    private String opponentName;
    private Integer opponentScore;

    private Retrofit retrofit;
    private ApiInterface apiInterfaceListener;
    private ApiInterface apiInterfaceSender;
    private Call<ChalengeByIdResponse> call;

    private boolean status;

    private PoseClassifierProcessor poseClassifierProcessor;

    /**
     * Internal class to hold Pose and classification results.
     */
    protected static class PoseWithClassification {
        private final Pose pose;
        private final List<String> classificationResult;

        public PoseWithClassification(Pose pose, List<String> classificationResult) {
            this.pose = pose;
            this.classificationResult = classificationResult;
        }

        public Pose getPose() {
            return pose;
        }

        public List<String> getClassificationResult() {
            return classificationResult;
        }
    }

    public PoseDetectorProcessor(
            Activity activity,
            PoseDetectorOptionsBase options,
            boolean showInFrameLikelihood,
            boolean visualizeZ,
            boolean rescaleZForVisualization,
            boolean runClassification,
            boolean isStreamMode,
            String trainingName,
            Integer trainingReps,
            Integer trainingSets,
            Integer trainingSetNumber,
            Integer lastTime,
            Integer challengeId) {

        super(activity);
        this.showInFrameLikelihood = showInFrameLikelihood;
        this.visualizeZ = visualizeZ;
        this.rescaleZForVisualization = rescaleZForVisualization;
        detector = PoseDetection.getClient(options);
        this.runClassification = runClassification;
        this.isStreamMode = isStreamMode;
        this.context = activity;
        this.activity = activity;
        classificationExecutor = Executors.newSingleThreadExecutor();

        this.trainingName = trainingName;
        this.trainingReps = trainingReps;
        this.trainingSets = trainingSets;
        this.trainingSetNumber = trainingSetNumber;
        this.lastTime = lastTime;

        this.challengeId = challengeId;

        opponentName = "";
        opponentScore = 0;
        status = false;

        //store beginning time after perform single train
        startTime = System.currentTimeMillis();

        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(activity))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterfaceListener = retrofit.create(ApiInterface.class);
        apiInterfaceSender = retrofit.create(ApiInterface.class);

        if (challengeId != 0)
            ((CardView) activity.findViewById(R.id.opponent_data)).setVisibility(View.VISIBLE);
        runToListenAllTheTime();
    }


    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = null;

    //timer that take a time and wait for it
    private void runToListenAllTheTime() {
        timerRunnable = new Runnable() {
            final long startTime = System.currentTimeMillis();

            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void run() {
                //listen to show status changes or not
                listen();
                timerHandler.postDelayed(this, 2000);
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }


    @Override
    public void stop() {
        super.stop();
        detector.close();
        if (timerRunnable != null)
            timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected Task<PoseWithClassification> detectInImage(InputImage image) {
        return detector
                .process(image)
                .continueWith(
                        classificationExecutor,
                        task -> {
                            Pose pose = task.getResult();
                            List<String> classificationResult = new ArrayList<>();
                            if (runClassification) {
                                if (poseClassifierProcessor == null) {

                                    ///passing name to handle each training separately
                                    poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode, trainingName);
                                }
                                classificationResult = poseClassifierProcessor.getPoseResult(pose);
                            }
                            return new PoseWithClassification(pose, classificationResult);
                        });
    }

    @Override
    protected Task<PoseWithClassification> detectInImage(MlImage image) {
        return detector
                .process(image)
                .continueWith(
                        classificationExecutor,
                        task -> {
                            Pose pose = task.getResult();
                            List<String> classificationResult = new ArrayList<>();
                            if (runClassification) {
                                if (poseClassifierProcessor == null) {
                                    poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode, trainingName);
                                }
                                classificationResult = poseClassifierProcessor.getPoseResult(pose);
                            }
                            return new PoseWithClassification(pose, classificationResult);
                        });
    }

    private int doneNO = 0;

    //listen to any opponent
    private void listen() {
        if (apiInterfaceListener == null)
            return;

        call = apiInterfaceListener.getChallengeById("Bearer " + getApiToken(activity), challengeId);
        call.enqueue(new Callback<ChalengeByIdResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ChalengeByIdResponse> call, Response<ChalengeByIdResponse> response) {
                if (response.body() == null)
                    return;
                opponentName = response.body().getOpponent_name();
                opponentScore = response.body().getOpponent_score();

                ((TextView) activity.findViewById(R.id.tx_v_opponent_name)).setText("Opponent Name: " + opponentName);
                ((TextView) activity.findViewById(R.id.tx_v_opponent_score)).setText("Opponent Score: " + opponentScore);

            }

            @Override
            public void onFailure(Call<ChalengeByIdResponse> call, Throwable t) {
                Log.d("poseDetector", t.getMessage());
            }
        });
    }

    private int noOfTrains = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onSuccess(
            @NonNull PoseWithClassification poseWithClassification,
            @NonNull GraphicOverlay graphicOverlay) {

        List<String> result = poseWithClassification.classificationResult;

        if (!result.isEmpty()) {
            ((TextView) activity.findViewById(R.id.tx_v_vision_live)).setText(result.get(0));
        }

        if (result.isEmpty())
            return;

        if (!result.get(0).isEmpty()) {
            try {
                noOfTrains = Integer.parseInt(result.get(0).substring(result.get(0).indexOf(':') + 2,
                        result.get(0).indexOf("reps") - 1));
                sendNewRepOne(noOfTrains);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }


        if (noOfTrains == trainingReps && doneNO < 1) {
            doneNO++;
            ToneGenerator tg = new ToneGenerator(AudioManager.AUDIOFOCUS_REQUEST_GRANTED, 100);
            tg.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT);
            goToTimerActivity();
        }

//    result.get(1) confidence for the current training
        //---------------
        graphicOverlay.add(
                new PoseGraphic(
                        graphicOverlay,
                        poseWithClassification.pose,
                        showInFrameLikelihood,
                        visualizeZ,
                        rescaleZForVisualization,
                        poseWithClassification.classificationResult));
    }

    private void sendNewRepOne(int noOfTrains) {
        if (apiInterfaceSender == null)
            return;

        if (noOfTrains > StaticFunctions.oldData) {
            StaticFunctions.oldData = noOfTrains;
            Call<IncrementRepsByOne> call = apiInterfaceSender.incrementRepsByOne(
                    "Bearer " + getApiToken(activity),
                    challengeId);
            call.enqueue(new Callback<IncrementRepsByOne>() {
                @Override
                public void onResponse(Call<IncrementRepsByOne> call, Response<IncrementRepsByOne> response) {
                    if (response.body() == null)
                        return;

                    if (response.body().getState() == 2) {
                        Toast.makeText(activity, "the winner is: " + response.body().getWinner(), Toast.LENGTH_LONG).show();


                        doneNO++;
                        ToneGenerator tg = new ToneGenerator(AudioManager.AUDIOFOCUS_REQUEST_GRANTED, 100);
                        tg.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT);
                        Intent intent = new Intent(activity, HomeActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }

                @Override
                public void onFailure(Call<IncrementRepsByOne> call, Throwable t) {

                }
            });
        }
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Pose detection failed!", e);
    }

    @Override
    protected boolean isMlImageEnabled(Context context) {
        // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
        return true;
    }

    private void goToTimerActivity() {
        currentTakenTime = (int) (System.currentTimeMillis() - startTime) / 1000;

        Intent intent = new Intent(activity, TimerActivity.class);
        intent.putExtra("TName", trainingName);
        intent.putExtra("TReps", trainingReps);
        intent.putExtra("TSets", trainingSets);
        intent.putExtra("setNumber", ++trainingSetNumber);
        intent.putExtra("lastTime", currentTakenTime + lastTime);
        activity.startActivity(intent);
        activity.finish();
    }

}
