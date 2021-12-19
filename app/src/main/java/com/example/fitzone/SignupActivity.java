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

public class SignupActivity extends AppCompatActivity {


    //private variable
    private EditText name, email, psw, vPsw;
    private Button signupB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.username);
        email = findViewById(R.id.email);
        psw = findViewById(R.id.password);
        vPsw = findViewById(R.id.vPassword);
    }


    //verify each input fields has correct value
    private boolean verification(){
        boolean flag = true;
        if(name.getText().toString().isEmpty()){
            //make change to name field
            flag = false;
        }
        if(email.getText().toString().isEmpty()){
            //make change to email field
            flag = false;
        }
        if(psw.getText().toString().isEmpty()){
            //make change to pwd field
            flag = false;
        }
        if(vPsw.getText().toString().isEmpty()){
            //make change to vPwd field
            flag = false;
        }
        if(!vPsw.getText().toString().equals(psw.getText().toString())){
            //tell user the two passwords are different
            flag = false;
        }
        return flag;
    }

    //empty all fields
    private void emptyAllFields(){
        name.setText(null);
        email.setText(null);
        psw.setText(null);
        vPsw.setText(null);
    }


    public void signup(View view) {
        if(verification())
        {
            //for test
//            emptyAllFields();
//            alert.append("Your Data is Valid");
            //end for test

            //String url = API_SERVER_URL + "/api/register?username=" + name + "&email=" + email +"&password=" + psw;
            HandleRequests handleRequests = new HandleRequests(SignupActivity.this);

            String nameStr = name.getText().toString();
            String emailStr = email.getText().toString();
            String pwdStr = psw.getText().toString();

            //send data to server
            handleRequests.signupUser(nameStr, emailStr, pwdStr, new HandleRequests.VolleyResponseListener() {
                @Override
                public void onResponse(boolean status, JSONObject jsonObject) {
                    if(status){

                        HandleRequests handleRequests = new HandleRequests(SignupActivity.this);

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

                                    intent = new Intent(SignupActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }
            });
        }
        else
        {
            emptyAllFields();
        }
    }

    public void backToLogin(View view) {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}