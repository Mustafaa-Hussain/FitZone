package com.example.fitzone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {



    //private variable
    private EditText email, pwd;
    private TextView alert;
    private Button loginBt;
    private static final String API_SERVER_URL = "http://172.16.203.192:8000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        pwd = findViewById(R.id.password);
        alert = findViewById(R.id.alert);
        loginBt = findViewById(R.id.loginBT);

    }


    //verify each input fields has correct value
    private boolean verification(){
        boolean flag = true;
        alert.setText("");

        if(email.getText().toString().isEmpty()){
            alert.append(getString(R.string.empty_email));
            flag = false;
        }
        if(pwd.getText().toString().isEmpty()){
            alert.append(getString(R.string.empty_psw));
            flag = false;
        }
        return flag;
    }

    //empty all fields
    private void emptyAllFields(){
        email.setText(null);
        pwd.setText(null);
        alert.setText(null);
    }

    public void login(View view) {

        if(verification()){
            //HANDiL LOGIN
            HandelRequests handelRequests = new HandelRequests(LoginActivity.this);

            String emailS = email.getText().toString();
            String passwordS = pwd.getText().toString();
            //send user data to server
            handelRequests.loginUser(emailS, passwordS);
        }

    }

    public void goToSignup(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }
}