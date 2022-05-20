package com.example.fitzone.activites;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitzone.handelers.HandleRequests;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        //very important step
        //set default ip and port in SharedPreferences file
        SharedPreferences serverDataFile = getSharedPreferences("ipAndPort", MODE_PRIVATE);
        SharedPreferences.Editor editor = serverDataFile.edit();
        editor.putString("ip", serverDataFile.getString("ip", "192.168.1.3"));//default value
        editor.putString("port", serverDataFile.getString("port", "8000"));
        editor.apply();

        String apiToken = getApiToken(this);

        if (apiToken.equals("")) {
            Intent intent;
            intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            HandleRequests handleRequests = new HandleRequests(MainActivity.this);
            handleRequests.getUserProfile(apiToken, (status, jsonObject) -> {
                Intent localIntent;
                //user role 0->for regular user and 2->for admin user
                if (status) {
                    int role = 0;
                    try {
                        role = jsonObject.getInt("role");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    localIntent = new Intent(MainActivity.this, HomeActivity.class);
                    // pass role integer to HomeActivity to identify user type

                    // 0->for regular user
                    Toast.makeText(this, "role: " + role, Toast.LENGTH_SHORT).show();

                    // 2->for admin user
                    // and there is identification by apiToken
                    localIntent.putExtra("role", role);
                } else {
                    localIntent = new Intent(MainActivity.this, Reconnect.class);
                }
                startActivity(localIntent);
                finish();
            });
        }
    }
}