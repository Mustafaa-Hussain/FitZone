package com.example.fitzone.activites.fragments;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitzone.activites.HomeActivity;
import com.example.fitzone.activites.LivePreviewActivity;
import com.example.fitzone.activites.R;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.get_challenge_by_id.ChalengeByIdResponse;

import java.util.HashMap;
import java.util.Observable;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WaitingFragment extends Fragment {
    private String trainingName;
    private int trainingReps;
    private int trainingId;
    private boolean status;

    private TextView timerTextView;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = null;

    private TextView trainingData;

    private Retrofit retrofit;
    private ApiInterface apiInterface;


    public WaitingFragment(int trainingId, String trainingName, int trainingReps) {
        super(R.layout.fragment_waiting);
        this.trainingName = trainingName;
        this.trainingReps = trainingReps;
        this.trainingId = trainingId;

        this.status = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inflate elements
        GifImageView gifImageView = view.findViewById(R.id.training_gif_file);
        trainingData = view.findViewById(R.id.training_name);
        timerTextView = view.findViewById(R.id.timer);

        trainingData.setText(trainingName);

        //check and assign image to each training
        if (trainingName.equals("squat"))
            gifImageView.setImageResource(R.drawable.dynamic_squat);
        else if (trainingName.equals("push up"))
            gifImageView.setImageResource(R.drawable.dynamic_push_ups);
        else
            gifImageView.setImageResource(R.drawable.dynamic_squat);

        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

        waitTime(5 * 60);
    }

    //timer that take a time and wait for it
    private void waitTime(int limit) {
        timerRunnable = new Runnable() {
            int seconds, minutes, newSeconds;
            final long startTime = System.currentTimeMillis();

            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                seconds = (int) (millis / 1000);

                if (seconds >= limit) {
                    timerHandler.removeCallbacks(this);

                    Toast.makeText(getActivity(), "there is no one", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                    return;
                }


                winnerStatus.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        timerHandler.removeCallbacks(timerRunnable);
                        goToLivePreviewActivity();
                    }
                });

                //listen to show status changes or not
                listen();

                newSeconds = limit - seconds;

                minutes = newSeconds / 60;
                newSeconds = newSeconds % 60;

                timerTextView.setText(String.format("%d:%02d", minutes, newSeconds));
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void goToLivePreviewActivity() {
        Intent intent = new Intent(getActivity(), LivePreviewActivity.class);
        intent.putExtra("TName", trainingName);
        intent.putExtra("TReps", trainingReps);
        intent.putExtra("challengeId", trainingId);
        intent.putExtra("TSets", 1);
        intent.putExtra("setNumber", 1);
        intent.putExtra("lastTime", 0);

        checkPermission(intent);
    }

    private void checkPermission(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            } else if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
                getActivity().finish();
            }
        } else {
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Camera Permission was granted", Toast.LENGTH_SHORT).show();

                Intent testIntent;
                testIntent = new Intent(getActivity(), LivePreviewActivity.class);
                startActivity(testIntent);
            } else {
                Toast.makeText(getActivity(), "Camera Permission was denied\nthis function cannot work\nPlease enable camera permission in settings", Toast.LENGTH_LONG).show();
            }
        }
    }


    private Call<ChalengeByIdResponse> call;

    private MutableLiveData<Boolean> winnerStatus = new MutableLiveData();

    //listen to any opponent
    private void listen() {
        if (apiInterface == null)
            return;
        call = apiInterface.getChallengeById("Bearer " + getApiToken(getActivity()), trainingId);
        call.enqueue(new Callback<ChalengeByIdResponse>() {
            @Override
            public void onResponse(Call<ChalengeByIdResponse> call, Response<ChalengeByIdResponse> response) {
                if (response.body() == null)
                    return;
                if (response.body().getState() == 1) {
                    winnerStatus.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<ChalengeByIdResponse> call, Throwable t) {
                Log.d("", t.getMessage());
            }
        });
    }

}