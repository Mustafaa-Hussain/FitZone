package com.example.fitzone;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class HandelRequests {

    Context context;
    private String url;
    private String serverIP;
    private String serverPort;
    private static final String API_SERVER_URL = "";
    SharedPreferences sharedPreferences;

    HandelRequests(Context context) {
        this.url = API_SERVER_URL;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("ipAndPort", MODE_PRIVATE);
        setIP("");
        setPort("");
//        Toast.makeText(context, serverIP + ':' + serverPort, Toast.LENGTH_SHORT).show();
    }

    //store or update api token
    private void storeAppToken(String apiToken){
        //store api token in sharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("appToken", apiToken);
        editor.commit();
    }

    //move to next activity
    protected void moveToNextActivity(Context thisContext, Class nextContext){
        Intent intent = new Intent(thisContext, nextContext);
        thisContext.startActivity(intent);
    }

    //set url for server
    public void setIP(String serverIP) {
        this.serverIP = sharedPreferences.getString("ip", serverIP);
    }

    //set port for server
    public void setPort(String serverPort) {
        this.serverPort = sharedPreferences.getString("port", serverPort);
    }

    public void signupUser(String name, String email, String pwd) throws JSONException {

        final int[] statusCode = {0};

        try {
            String URL = "http://" + serverIP + ':' + serverPort + "/api/register";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username",name);
            jsonBody.put("email",email);
            jsonBody.put("password",pwd);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(statusCode[0] == 200) {
                            String apiToken = response.getJSONObject("message").getString("api_token");
                            //store api token in sharedPreferences
                            storeAppToken(apiToken);

                            Toast.makeText(context, "Registration is Successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(context , HomeActivity.class);
                            context.startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(statusCode[0] == 200){
                        Toast.makeText(context, "Username or email is Exist", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "Something Wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    statusCode[0] =response.statusCode;
                    return super.parseNetworkResponse(response);
                }
            };

            MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loginUser(String email, String pwd){

        final int[] statusCode = {0};

        try {
            String URL = "http://" + serverIP + ':' + serverPort + "/api/login";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email",email);
            jsonBody.put("password",pwd);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(statusCode[0] == 201) {
                                    String apiToken = response.getString("token");
                                    //store api token in sharedPreferences
                                    storeAppToken(apiToken);

                                    Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(context , HomeActivity.class);
                                    context.startActivity(intent);
                                }
                                else{
                                    Toast.makeText(context, statusCode[0], Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(statusCode[0] == 401){
                        Toast.makeText(context, "Username or password is Wrong", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "Something Wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    statusCode[0] =response.statusCode;
                    return super.parseNetworkResponse(response);
                }
            };

            MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

