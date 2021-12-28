package com.example.fitzone;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class HandleRequests {

    Context context;
    private String url;
    private String serverIP;
    private String serverPort;
    private static final String API_SERVER_URL = "";
    SharedPreferences serverDataFile;
    SharedPreferences apiTokenFile;

    HandleRequests(Context context) {
        this.url = API_SERVER_URL;
        this.context = context;
        serverDataFile = context.getSharedPreferences("ipAndPort", MODE_PRIVATE);
        apiTokenFile = context.getSharedPreferences("UserData", MODE_PRIVATE);
        setIP("");
        setPort("");
//        Toast.makeText(context, serverIP + ':' + serverPort, Toast.LENGTH_SHORT).show();
    }

    //store or update api token
    private void storeAppToken(String apiToken){
        //store api token in sharedPreferences
        SharedPreferences.Editor editor = apiTokenFile.edit();
        editor.putString("apiToken", apiToken);
        editor.commit();
    }

    //move to next activity
    protected void moveToNextActivity(Context thisContext, Class nextContext){
        Intent intent = new Intent(thisContext, nextContext);
        thisContext.startActivity(intent);
    }

    //set url for server
    public void setIP(String serverIP) {
        this.serverIP = serverDataFile.getString("ip", serverIP);
    }

    //set port for server
    public void setPort(String serverPort) {
        this.serverPort = serverDataFile.getString("port", serverPort);
    }

    public interface VolleyResponseListener{
        void onResponse(boolean status, JSONObject data);
    }

    public void signupUser(String name, String email, String pwd, final VolleyResponseListener volleyResponseListener) {

        final int[] statusCode = {0};
        final boolean[] status = {false};

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
                        if(statusCode[0] == 201) {
                            String apiToken = response.getJSONObject("message").getString("api_token");
                            //store api token in sharedPreferences
                            storeAppToken(apiToken);

                            Toast.makeText(context, "Registration is Successfully", Toast.LENGTH_SHORT).show();
                            status[0] = true;
                            volleyResponseListener.onResponse(status[0], response);
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
                        Toast.makeText(context, "Check Your Internet Connection!...", Toast.LENGTH_SHORT).show();
                    }
                    volleyResponseListener.onResponse(status[0], null);
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


    public void loginUser(String email, String pwd, final VolleyResponseListener volleyResponseListener){

        final int[] statusCode = { 0 };
        boolean status[] = { false };

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
                                    String apiToken = response.getString("api_token");

                                    //store api token in sharedPreferences
                                    storeAppToken(apiToken);

                                    Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show();
                                    status[0] = true;
                                    volleyResponseListener.onResponse(status[0], response);
                                }
                                else{
                                    Toast.makeText(context, "failed " + statusCode[0], Toast.LENGTH_SHORT).show();
                                    volleyResponseListener.onResponse(status[0], null);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(statusCode[0] == 200){
                        Toast.makeText(context, "Username or password is Wrong", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "Check Your Internet Connection!...", Toast.LENGTH_SHORT).show();
                    }
                    volleyResponseListener.onResponse(status[0], null);
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

    public void getPosts(String apiToken, final VolleyResponseListener volleyResponseListener){

        final int[] statusCode = { 0 };
        boolean status[] = { false };
        final JSONObject[] posts = { null };

        String URL = "http://" + serverIP + ':' + serverPort + "/api/posts/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if(statusCode[0] == 201) {
                            status[0] = true;
                            posts[0] = response;
                        }
                        volleyResponseListener.onResponse(status[0], posts[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(statusCode[0] == 403){
                    Toast.makeText(context, "You Are Not logged in", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "Check Your Internet Connection!...", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(context, "You Are Not logged in", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(status[0], null);
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

            //to send api_token
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + apiToken);
                return headerMap;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    public void setLike(String apiToken, String postID,final VolleyResponseListener volleyResponseListener){
        final int[] statusCode = { 0 };
        boolean status[] = { false };
        final JSONObject[] posts = {null};

        String URL = "http://" + serverIP + ':' + serverPort + "/api/posts/" + postID + "/like";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(statusCode[0] == 201) {
                            status[0] = true;
                            posts[0] = response;
                        }
                        volleyResponseListener.onResponse(status[0], posts[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(status[0], null);
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

            //to send api_token
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + apiToken);
                return headerMap;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    public void setDislike(String apiToken, String postID, final VolleyResponseListener volleyResponseListener){
        final int[] statusCode = { 0 };
        boolean status[] = { false };
        final JSONObject[] posts = {null};

        String URL = "http://" + serverIP + ':' + serverPort + "/api/posts/" + postID + "/unlike";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(statusCode[0] == 201) {
                            status[0] = true;
                            posts[0] = response;
                        }
                        volleyResponseListener.onResponse(status[0], posts[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(status[0], null);
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

            //to send api_token
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + apiToken);
                return headerMap;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }


}

