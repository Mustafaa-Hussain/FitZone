package com.example.fitzone.activites;

import static com.example.fitzone.common_functions.StaticFunctions.getHostUrl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class TrainingVideoView extends AppCompatActivity {

    private JzvdStd videoView;
    private TextView trainingName;
    private Button backToDayActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_video_view);

        //inflate elements
        videoView = findViewById(R.id.video_view);
        trainingName = findViewById(R.id.training_video_text);
        backToDayActivity = findViewById(R.id.training_video_back_button);

        backToDayActivity.setOnClickListener(view -> finish());

        Intent intent = getIntent();
        String exerciseName = intent.getStringExtra("exercise_name");
        String exerciseVideoUrl = intent.getStringExtra("exercise_video_url");

        trainingName.setText(exerciseName);

        videoView.setUp(getHostUrl(this) + exerciseVideoUrl, exerciseName);

        if (exerciseName.equals("squat"))
            videoView.posterImageView.setImageDrawable(getDrawable(R.drawable.static_squat));
        else
            videoView.posterImageView.setImageDrawable(getDrawable(R.drawable.static_pushups));

    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }
}