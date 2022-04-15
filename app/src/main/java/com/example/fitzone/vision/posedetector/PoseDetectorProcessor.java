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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.fitzone.activites.R;
import com.example.fitzone.activites.TimerActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.vision.common.InputImage;
import com.example.fitzone.vision.GraphicOverlay;
import com.example.fitzone.vision.VisionProcessorBase;
import com.example.fitzone.vision.posedetector.classification.PoseClassifierProcessor;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** A processor to run pose detector. */
public class PoseDetectorProcessor
    extends VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification>{
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

  //training data to focuse on it

  private final String trainingName;
  private final Integer trainingReps;
  private final Integer trainingSets;
  private Integer trainingSetNumber;
  private final Integer lastTime;

  private PoseClassifierProcessor poseClassifierProcessor;
  /** Internal class to hold Pose and classification results. */
  protected static class PoseWithClassification{
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
      Integer lastTime) {

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


    //store beginning time after perform single train
    startTime = System.currentTimeMillis();
  }

  @Override
  public void stop() {
    super.stop();
    detector.close();
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
                  poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode, trainingName);///passing name to handle each training separately
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

  @Override
  protected void onSuccess(
      @NonNull PoseWithClassification poseWithClassification,
      @NonNull GraphicOverlay graphicOverlay) {

    List<String> result = poseWithClassification.classificationResult;

    ((TextView)activity.findViewById(R.id.tx_v_vision_live)).setText(result.get(0));


    int noOfTrains = 0;

    if(!result.get(0).isEmpty()) {
      try {
        noOfTrains = Integer.parseInt(result.get(0).substring(result.get(0).indexOf(':') + 2,
                result.get(0).indexOf("reps") - 1));
      }catch (NumberFormatException e){
        Log.d(TAG, e.getMessage());
      }
    }


    if (noOfTrains == trainingReps){
      ToneGenerator tg = new ToneGenerator(AudioManager.AUDIOFOCUS_REQUEST_GRANTED, 100);
      tg.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT);

      detector.close();
      goToTimerActivity();
    }

//    result.get(1) confidance for the current training
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

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.e(TAG, "Pose detection failed!", e);
  }

  @Override
  protected boolean isMlImageEnabled(Context context) {
    // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
    return true;
  }

  private void goToTimerActivity(){

    currentTakenTime = (int)(System.currentTimeMillis() - startTime)/1000;

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
