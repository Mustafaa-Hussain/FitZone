package com.example.fitzone.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;


public class Reconnect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconnect);
    }

    public void goToLoginPage(View view) {
        Intent intent;
        intent = new Intent(Reconnect.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void reLoginByToken(View view) {
        SharedPreferences apiTokenFile = getSharedPreferences("UserData", MODE_PRIVATE);
        String apiToken = apiTokenFile.getString("apiToken", "");

        Intent intent;

        if (apiToken.equals("")) {
            intent = new Intent(Reconnect.this, LoginActivity.class);
        }
        else {
            intent = new Intent(Reconnect.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    public void goToTrainingPage(View view) {
        Intent testIntent;
        testIntent = new Intent(getApplicationContext(), Training.class);
        startActivity(testIntent);
        finish();
    }
}