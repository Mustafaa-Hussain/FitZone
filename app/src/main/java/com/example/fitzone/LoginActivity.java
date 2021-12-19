package com.example.fitzone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {



    //private variable
    private EditText email, pwd;
    private Button loginBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        pwd = findViewById(R.id.password);
        loginBt = findViewById(R.id.loginBT);

    }


    //verify each input fields has correct value
    private boolean verification(){
        boolean flag = true;

        if(email.getText().toString().isEmpty()){
            //make change in email field
            flag = false;
        }
        if(pwd.getText().toString().isEmpty()){
            //make change in password field
            flag = false;
        }
        return flag;
    }

    //empty all fields
    private void emptyAllFields(){
        email.setText(null);
        pwd.setText(null);
    }

    public void login(View view) {
        if(verification()){
            //HANDiL LOGIN
            HandleRequests handleRequests = new HandleRequests(LoginActivity.this);

            String emailS = email.getText().toString();
            String passwordS = pwd.getText().toString();
            //send user data to server
            handleRequests.loginUser(emailS, passwordS, new HandleRequests.VolleyResponseListener() {
                @Override
                public void onResponse(boolean status, JSONObject jsonObject) {
                    if(status){

                        HandleRequests handleRequests = new HandleRequests(LoginActivity.this);

                        String apiToken = null;
                        try {
                            apiToken = jsonObject.getString("api_token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        handleRequests.getPosts(apiToken, new HandleRequests.VolleyResponseListener() {
                            @Override
                            public void onResponse(boolean status, JSONObject jsonObject) {
                                Intent intent;
                                if(status){

                                    SharedPreferences posts = getSharedPreferences("posts", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = posts.edit();
                                    editor.putString("allPosts", jsonObject.toString());
                                    editor.commit();

                                    intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }
            });
        }

    }

    public void goToSignup(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    public void changeServerIp(View view) {
        Intent intent = new Intent(LoginActivity.this, SetServerIP.class);
        startActivity(intent);
        finish();
    }
}