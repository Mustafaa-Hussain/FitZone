package com.example.fitzone.activites;

import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;
import static com.example.fitzone.common_functions.StaticFunctions.storeApiToken;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.login.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {


    //private variable
    private EditText email, pwd;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        pwd = findViewById(R.id.password);
        Button loginBt = findViewById(R.id.login_button);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(this))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);

        loginBt.setOnClickListener(this::login);

    }


    //verify each input fields has correct value
    private boolean verification() {
        boolean flag = true;

        if (email.getText().toString().isEmpty()) {
            //make change in email field
            flag = false;
        }
        if (pwd.getText().toString().isEmpty()) {
            //make change in password field
            flag = false;
        }
        return flag;
    }

    //empty all fields
    private void emptyAllFields() {
        email.setText(null);
        pwd.setText(null);
    }

    public void login(View view) {
        if (verification()) {
            String email_str = email.getText().toString();
            String password_str = pwd.getText().toString();
            Call<LoginResponse> call = apiInterface.login(email_str, password_str);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.body() != null) {

                        storeApiToken(LoginActivity.this,
                                response.body().getApi_token());

                        //Toast success message
                        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                        //go to @link{HomeActivity}
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    if (!t.getMessage().isEmpty())
                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        //old version with volley
//        if (verification()) {
//            //HANDiL LOGIN
//            HandleRequests handleRequests = new HandleRequests(LoginActivity.this);
//
//            String emailS = email.getText().toString();
//            String passwordS = pwd.getText().toString();
//            //send user data to server
//            handleRequests.loginUser(emailS, passwordS, (status, jsonObject) -> {
//                if (status) {
//
//                    HandleRequests handleRequests1 = new HandleRequests(LoginActivity.this);
//
//                    String apiToken = null;
//                    try {
//                        apiToken = jsonObject.getString("api_token");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    handleRequests1.getPosts(apiToken, new HandleRequests.VolleyResponseListener() {
//                        @Override
//                        public void onResponse(boolean status, JSONObject jsonObject) {
//                            if (status) {
//                                Intent intent;
//                                SharedPreferences posts = getSharedPreferences("posts", MODE_PRIVATE);
//                                SharedPreferences.Editor editor = posts.edit();
//                                editor.putString("allPosts", jsonObject.toString());
//                                editor.commit();
//                                intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        }
//                    });
//                }
//            });
//        }

    }

    public void goToSignup(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void changeServerIp(View view) {
        Intent intent = new Intent(LoginActivity.this, SetServerIP.class);
        startActivity(intent);
        finish();
    }

    public void goToTrainingPage(View view) {
        Intent testIntent;
        testIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(testIntent);
    }
}