package com.example.fitzone.activites;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fitzone.activites.fragments.HomeFragment;
import com.example.fitzone.activites.fragments.ProfileFragment;
import com.example.fitzone.activites.fragments.ProgramsFragment;
import com.example.fitzone.activites.fragments.BadgesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    Fragment homeFragment,
            profileFragment,
            programsFragment,
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
        programsFragment = new ProgramsFragment();

        trophiesFragment = new BadgesFragment();


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
                    setFragmentContent(programsFragment);
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
                    setFragmentContent(programsFragment);
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