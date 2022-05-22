package com.example.fitzone.activites;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForProgram;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.day_training_program.DayTrainingProgram;
import com.example.fitzone.retrofit_requists.data_models.exercise_data.ExerciseData;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DayActivity extends AppCompatActivity implements RecycleViewAdapterForProgram.ItemClickListener {

    private RecycleViewAdapterForProgram adapter;
    private DayTrainingProgram myTraining;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private Button btShowVideo, begin;
    private ExerciseData exerciseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        Intent intent;
        intent = getIntent();
        String programName = intent.getStringExtra("program_name");
        int dayName = intent.getIntExtra("day_name", 1);
        String dayNameStr = "Day: " + dayName;

        TextView dayNameText = findViewById(R.id.day_name_text);
        dayNameText.setText(dayNameStr);


        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(this))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

        Call<DayTrainingProgram> call = apiInterface.getDayRoutineOfProgram("Bearer " + getApiToken(this), programName, dayName);
        call.enqueue(new Callback<DayTrainingProgram>() {
            @Override
            public void onResponse(Call<DayTrainingProgram> call, Response<DayTrainingProgram> response) {
                myTraining = response.body();
                if (myTraining == null)
                    return;

                recyclerView = findViewById(R.id.recycleView);
                recyclerView.setLayoutManager(new LinearLayoutManager(DayActivity.this));
                adapter = new RecycleViewAdapterForProgram(DayActivity.this, myTraining);
                adapter.setClickListener(DayActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<DayTrainingProgram> call, Throwable t) {
                Toast.makeText(DayActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("DayActivity", t.getMessage());
            }
        });

    }

    private void goToTrainingVideoView(View view) {
        Intent intent = new Intent(this, TrainingVideoView.class);
        intent.putExtra("exercise_name", exerciseData.getName());
        intent.putExtra("exercise_video_url", exerciseData.getVideo());
        startActivity(intent);
    }

    //get exercise data {{video URL}}
    private void getExerciseData(View view, int position) {
        if (apiInterface == null)
            return;

        Call<ExerciseData> call = apiInterface.getExerciseData("Bearer " + getApiToken(this), myTraining.get(position).getExercise_name());
        call.enqueue(new Callback<ExerciseData>() {
            @Override
            public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                if (response.body() == null)
                    return;

                exerciseData = response.body();
                makePopupWindow(view, position, response.body().getVideo());
            }

            @Override
            public void onFailure(Call<ExerciseData> call, Throwable t) {
                Toast.makeText(DayActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.start) {
            getExerciseData(view, position);
        }
    }


    //make pop up window
    private void makePopupWindow(View view, int position, String videoUrl) {

        PopupWindow startTraining;

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View popupView = inflater.inflate(R.layout.begining_trining_structure, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        startTraining = new PopupWindow(popupView, width, height, true);


        //put day gif
        //id   training_gif_file
        GifImageView gifImageView = popupView.findViewById(R.id.training_gif_file);

        //check and assign image to each training
        if (myTraining.get(position).getExercise_name().equals("squat")) {

            gifImageView.setImageResource(R.drawable.dynamic_squat);
        } else if (myTraining.get(position).getExercise_name().equals("push up")) {

            gifImageView.setImageResource(R.drawable.dynamic_push_ups);
        } else {

            gifImageView.setImageResource(R.drawable.dynamic_squat);
        }

        //put the Day name
        String trainingName = myTraining.get(position).getExercise_name();
        ((TextView) popupView.findViewById(R.id.start_training_name)).setText(trainingName);

        String TReps = "" + myTraining.get(position).getReps(),
                TSets = "" + myTraining.get(position).getSets();

        String noOfTrains = TReps + " X " + TSets;
        ((TextView) popupView.findViewById(R.id.no)).setText(noOfTrains);

        begin = popupView.findViewById(R.id.begin);
        begin.setOnClickListener(
                v -> {
                    Intent intent;
                    intent = new Intent(getApplicationContext(), TimerActivity.class);
                    intent.putExtra("TName", myTraining.get(position).getExercise_name());
                    intent.putExtra("TReps", myTraining.get(position).getReps());
                    intent.putExtra("TSets", myTraining.get(position).getSets());
                    intent.putExtra("setNumber", 1);

                    checkPermission(intent);
                });


        // show the popup window
        startTraining.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched on any point in the screen
        popupView.setOnTouchListener(
                (v, event) -> {
                    startTraining.dismiss();
                    return true;
                });

        //handle button of showing exercise video
        btShowVideo = popupView.findViewById(R.id.button_watch_an_explainer_video);
        if (!btShowVideo.hasOnClickListeners())
            btShowVideo.setOnClickListener(this::goToTrainingVideoView);
    }

    private void checkPermission(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            } else if (this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            }
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Camera Permission was granted", Toast.LENGTH_SHORT).show();

                Intent testIntent;
                testIntent = new Intent(getApplicationContext(), LivePreviewActivity.class);
                startActivity(testIntent);
            } else {
                Toast.makeText(getApplicationContext(), "Camera Permission was denied\nthis function cannot work\nPlease enable camera permission in settings", Toast.LENGTH_LONG).show();
            }
        }
    }

}