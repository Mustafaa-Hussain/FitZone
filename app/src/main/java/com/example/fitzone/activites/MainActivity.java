package com.example.fitzone.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitzone.handelers.HandleRequests;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //for test only
        Intent testIntent;
        testIntent = new Intent(MainActivity.this, LivePreviewActivity.class);
        startActivity(testIntent);
        finish();
        //
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        //very important step
        //set default ip and port in SharedPreferences file
        SharedPreferences serverDataFile = getSharedPreferences("ipAndPort", MODE_PRIVATE);
        SharedPreferences.Editor editor = serverDataFile.edit();
        editor.putString("ip", serverDataFile.getString("ip", "192.168.1.3"));
        editor.putString("port", serverDataFile.getString("port", "8000"));
        editor.commit();


        SharedPreferences apiTokenFile = getSharedPreferences("UserData", MODE_PRIVATE);
        String apiToken = apiTokenFile.getString("apiToken", "");

        if(apiToken.equals("")){
            Intent intent;
            intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            HandleRequests handleRequests = new HandleRequests(MainActivity.this);
            handleRequests.getUserProfile(apiToken, new HandleRequests.VolleyResponseListener() {
                @Override
                public void onResponse(boolean status, JSONObject jsonObject) {
                    Intent localIntent;
                    if(status){
                        localIntent = new Intent(MainActivity.this, HomeActivity.class);
                    }
                    else{
                        localIntent = new Intent(MainActivity.this, Reconnect.class);
                    }
                    startActivity(localIntent);
                    finish();
                }
            });
        }


//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }, 3000);
    }
}