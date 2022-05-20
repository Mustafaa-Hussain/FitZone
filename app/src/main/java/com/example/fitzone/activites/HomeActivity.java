package com.example.fitzone.activites;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    Fragment homeFragment,
            profileFragment,
            trainingFragment,
            trophiesFragment;

    String apiToken;

    int role = 0;

    BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        Intent intent = getIntent();
        role = intent.getIntExtra("role", 0);

        apiToken = getApiToken(this);

        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
        trainingFragment = new TrainingFragment();

        trophiesFragment = new TrophiesFragment();


        setFragmentContent(homeFragment);

        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(v -> {
            switch (v.getItemId()) {
                case R.id.home:
                    setFragmentContent(homeFragment);
                    break;
                case R.id.profile:
                    setFragmentContent(profileFragment);
                    break;
                case R.id.training:
                    setFragmentContent(trainingFragment);
                    break;
                case R.id.badges:
                    setFragmentContent(trophiesFragment);
                    break;
            }
            return true;
        });

        bottomNavigationView.setOnItemReselectedListener(v -> {
            switch (v.getItemId()) {
                case R.id.home:
                    setFragmentContent(homeFragment);
                    break;
                case R.id.profile:
                    setFragmentContent(profileFragment);
                    break;
                case R.id.training:
                    setFragmentContent(trainingFragment);
                    break;
                case R.id.badges:
                    setFragmentContent(trophiesFragment);
                    break;
            }
        });
    }

    private void setFragmentContent(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_home_activity, fragment).commit();
    }
}