package com.example.fitzone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //very important step
        //set default ip and port in SharedPreferences file
        SharedPreferences serverDataFile = getSharedPreferences("ipAndPort", MODE_PRIVATE);
        SharedPreferences.Editor editor = serverDataFile.edit();
        editor.putString("ip", serverDataFile.getString("ip", "192.168.1.3"));
        editor.putString("port", serverDataFile.getString("port", "8000"));
        editor.commit();


        SharedPreferences apiTokenFile = getSharedPreferences("UserData", MODE_PRIVATE);
        String apiToken = apiTokenFile.getString("apiToken", null);

        HandleRequests handleRequests = new HandleRequests(MainActivity.this);

        handleRequests.getPosts(apiToken, new HandleRequests.VolleyResponseListener() {
            @Override
            public void onResponse(boolean status, JSONObject jsonObject) {
                Intent intent;
                if(status){

                    SharedPreferences posts = getSharedPreferences("posts", MODE_PRIVATE);
                    SharedPreferences.Editor editor = posts.edit();
                    editor.putString("allPosts", jsonObject.toString());
                    editor.commit();

                    intent = new Intent(MainActivity.this, HomeActivity.class);
                }
                else{
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });

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