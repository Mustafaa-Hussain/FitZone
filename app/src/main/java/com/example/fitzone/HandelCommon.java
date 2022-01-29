package com.example.fitzone;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class HandelCommon extends AppCompatActivity {

    public void goToHomePage(View view) {
        Intent intent;
        intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToProfilePage(View view) {
        Intent intent;
        intent = new Intent(this, Profile.class);
        startActivity(intent);
        finish();
    }
    public void goToTrainingPage(View view) {
        Intent intent;
        intent = new Intent(this, Training.class);
        startActivity(intent);
        finish();
    }
}
