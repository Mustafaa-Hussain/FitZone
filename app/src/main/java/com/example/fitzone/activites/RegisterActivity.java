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

import com.example.fitzone.handelers.HandleRequests;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.register.RegisterResponse;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {


    //private variable
    private EditText userName, email, password, vPassword;
    private Button signupB;

    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userName = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        vPassword = findViewById(R.id.vPassword);
        signupB = findViewById(R.id.signup_button);

        //retrofit builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(this))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
        signupB.setOnClickListener(this::signup);

    }


    //verify each input fields has correct value
    private boolean verification() {
        boolean flag = true;
        if (userName.getText().toString().isEmpty()) {
            //make change to name field
            flag = false;
        }
        if (email.getText().toString().isEmpty()) {
            //make change to email field
            flag = false;
        }
        if (password.getText().toString().isEmpty()) {
            //make change to pwd field
            flag = false;
        }
        if (vPassword.getText().toString().isEmpty()) {
            //make change to vPwd field
            flag = false;
        }
        if (!vPassword.getText().toString().equals(password.getText().toString())) {
            //tell user the two passwords are different
            flag = false;
        }
        return flag;
    }

    //empty all fields
    private void emptyAllFields() {
        userName.setText(null);
        email.setText(null);
        password.setText(null);
        vPassword.setText(null);
    }


    public void signup(View view) {

        if (verification()) {
            String userName_str = userName.getText().toString();
            String email_str = email.getText().toString();
            String password_str = password.getText().toString();

            Call<RegisterResponse> call = apiInterface.register(userName_str, email_str, password_str);

            call.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                    if (response.body() != null) {
                        boolean resultF = storeApiToken(RegisterActivity.this,
                                response.body().getMessage().getApi_token());

                        if (resultF) {
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Go to login page", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    if (!t.getMessage().isEmpty())
                        Toast.makeText(RegisterActivity.this, "this username or email is taken", Toast.LENGTH_SHORT).show();
                }
            });
        }

//        //old version using volley
//        if (verification()) {
//            //for test
////            emptyAllFields();
////            alert.append("Your Data is Valid");
//            //end for test
//
//            //String url = API_SERVER_URL + "/api/register?username=" + name + "&email=" + email +"&password=" + psw;
//            HandleRequests handleRequests = new HandleRequests(RegisterActivity.this);
//
//            String nameStr = userName.getText().toString();
//            String emailStr = email.getText().toString();
//            String pwdStr = password.getText().toString();
//
//            //send data to server
//            handleRequests.signupUser(nameStr, emailStr, pwdStr, new HandleRequests.VolleyResponseListener() {
//                @Override
//                public void onResponse(boolean status, JSONObject jsonObject) {
//                    if (status) {
//
//                        HandleRequests handleRequests = new HandleRequests(RegisterActivity.this);
//
//                        String apiToken = null;
//                        try {
//                            apiToken = jsonObject.getString("api_token");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            });
//        } else {
//            emptyAllFields();
//        }
    }

    public void backToLogin(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}