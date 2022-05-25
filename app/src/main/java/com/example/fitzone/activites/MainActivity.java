package com.example.fitzone.activites;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitzone.handelers.HandleRequests;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.user_profile_data.UserProfileResponse;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private ApiInterface apiInterface;

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
        editor.putString("ip", serverDataFile.getString("ip", "192.168.1.2"));//default value
        editor.putString("port", serverDataFile.getString("port", "8000"));
        editor.apply();

        String apiToken = getApiToken(this);


        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(this))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);


        if (apiToken.equals("")) {
            Intent intent;
            intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            getUserProfile();
        }
    }

    //get user profile
    private void getUserProfile() {
        if (apiInterface == null)
            return;

        Call<UserProfileResponse> call = apiInterface.getUserProfileData("Bearer " + getApiToken(this));
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.body() == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                int role = response.body().getRole();

                Intent intent = new Intent(MainActivity.this, HomeActivity.class);

                // 2->for admin user
                // and there is identification by apiToken
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Reconnect.class);
                startActivity(intent);
                finish();
            }
        });
    }
}