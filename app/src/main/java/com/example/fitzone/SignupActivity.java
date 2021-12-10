package com.example.fitzone;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

public class SignupActivity extends AppCompatActivity {


    //private variable
    private EditText name, email, psw, vPsw;
    private TextView alert;
    private Button signupB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        psw = findViewById(R.id.password);
        vPsw = findViewById(R.id.vPassword);
        alert = findViewById(R.id.alert);
    }


    //verify each input fields has correct value
    private boolean verification(){
        boolean flag = true;
        alert.setText("");
        if(name.getText().toString().isEmpty()){
            alert.append(getString(R.string.empty_name));
            flag = false;
        }
        if(email.getText().toString().isEmpty()){
            alert.append(getString(R.string.empty_email));
            flag = false;
        }
        if(psw.getText().toString().isEmpty()){
            alert.append(getString(R.string.empty_psw));
            flag = false;
        }
        if(vPsw.getText().toString().isEmpty()){
            alert.append(getString(R.string.empty_vPsw));
            flag = false;
        }
        if(!vPsw.getText().toString().equals(psw.getText().toString())){
            alert.append(getString(R.string.mismatch_password));
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


    public void signup(View view) throws JSONException {
        if(verification())
        {
            //for test
//            emptyAllFields();
//            alert.append("Your Data is Valid");
            //end for test

            //String url = API_SERVER_URL + "/api/register?username=" + name + "&email=" + email +"&password=" + psw;
            HandelRequests handelRequests = new HandelRequests(SignupActivity.this);

            String nameStr = name.getText().toString();
            String emailStr = email.getText().toString();
            String pwdStr = psw.getText().toString();

            //send data to server
            handelRequests.signupUser(nameStr, emailStr, pwdStr);
        }
        else
        {
            emptyAllFields();
        }
    }
}