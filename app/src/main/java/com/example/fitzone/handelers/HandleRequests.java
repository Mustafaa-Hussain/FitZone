package com.example.fitzone.handelers;

import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;
import static com.example.fitzone.common_functions.StaticFunctions.storeApiToken;

import android.app.Activity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HandleRequests {


    Activity activity;
    private static String API_SERVER_URL = null;

    //constructor
    public HandleRequests(Activity activity) {
        this.activity = activity;
        API_SERVER_URL = getBaseUrl(activity);
    }

    public interface VolleyResponseListener {
        void onResponse(boolean status, JSONObject data);
    }

    public void getPosts(String apiToken, final VolleyResponseListener volleyResponseListener) {

        final int[] statusCode = {0};
        boolean[] status = {false};
        final JSONObject[] posts = {null};

        String URL = API_SERVER_URL + "posts/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (statusCode[0] == 201) {
                            status[0] = true;
                            posts[0] = response;
                        }
                        volleyResponseListener.onResponse(status[0], posts[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (statusCode[0] == 403) {
                    Toast.makeText(activity, "You Are Not logged in", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Check Your Internet Connection!...", Toast.LENGTH_SHORT).show();
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
                statusCode[0] = response.statusCode;
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

        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);
    }

    public void addPost(String caption, String content, int poetType, String apiToken, final VolleyResponseListener volleyResponseListener) {

        final int[] statusCode = {0};
        boolean status[] = {false};

        try {
            String URL = API_SERVER_URL + "posts";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("caption", caption);
            jsonBody.put("content", content);
            jsonBody.put("type", poetType);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (statusCode[0] == 201) {
                                status[0] = true;
                                volleyResponseListener.onResponse(status[0], response);
                            } else {
                                Toast.makeText(activity, "failed " + statusCode[0], Toast.LENGTH_SHORT).show();
                                volleyResponseListener.onResponse(status[0], null);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (statusCode[0] == 200) {
                        Toast.makeText(activity, "something wrong", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "Check Your Internet Connection!...", Toast.LENGTH_SHORT).show();
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
                    statusCode[0] = response.statusCode;
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

            MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addComment(String content, int postID, String apiToken, final VolleyResponseListener volleyResponseListener) {

        final int[] statusCode = {0};
        boolean status[] = {false};

        try {

            String URL = API_SERVER_URL + "posts/" + postID + "/comments";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("content", content);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (statusCode[0] == 201) {
                                status[0] = true;
                                volleyResponseListener.onResponse(status[0], response);
                            } else {
                                Toast.makeText(activity, "failed " + statusCode[0], Toast.LENGTH_SHORT).show();
                                volleyResponseListener.onResponse(status[0], null);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (statusCode[0] == 200) {
                        Toast.makeText(activity, "something wrong", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "Check Your Internet Connection!...", Toast.LENGTH_SHORT).show();
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
                    statusCode[0] = response.statusCode;
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

            MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void logout(String apiToken, final VolleyResponseListener volleyResponseListener) {
        final int[] statusCode = {0};
        boolean status[] = {false};
        final JSONObject[] posts = {null};
        String URL = API_SERVER_URL + "logout";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        status[0] = true;
                        posts[0] = response;
                        volleyResponseListener.onResponse(status[0], posts[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (statusCode[0] == 200) {
                    Toast.makeText(activity, "You Are Not logged in", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Check Your Internet Connection!...", Toast.LENGTH_SHORT).show();
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
                statusCode[0] = response.statusCode;
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

        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);

    }

    public void getPost(String apiToken, int postID, final VolleyResponseListener volleyResponseListener) {
        final int[] statusCode = {0};
        boolean status[] = {false};
        final JSONObject[] posts = {null};

        String URL = API_SERVER_URL + "posts/" + postID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        status[0] = true;
                        posts[0] = response;
                        volleyResponseListener.onResponse(status[0], posts[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(status[0], null);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode[0] = response.statusCode;
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

        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);

    }

    public void setLike(String apiToken, int postID, final VolleyResponseListener volleyResponseListener) {
        final int[] statusCode = {0};
        boolean status[] = {false};
        final JSONObject[] posts = {null};

        String URL = API_SERVER_URL + "posts/" + postID + "/like";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (statusCode[0] == 201) {
                            status[0] = true;
                            posts[0] = response;
                        }
                        volleyResponseListener.onResponse(status[0], posts[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(status[0], null);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode[0] = response.statusCode;
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

        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);

    }

    public void setDislike(String apiToken, int postID, final VolleyResponseListener volleyResponseListener) {
        final int[] statusCode = {0};
        boolean status[] = {false};
        final JSONObject[] posts = {null};

        String URL = API_SERVER_URL + "posts/" + postID + "/unlike";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (statusCode[0] == 201) {
                            status[0] = true;
                            posts[0] = response;
                        }
                        volleyResponseListener.onResponse(status[0], posts[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(status[0], null);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode[0] = response.statusCode;
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

        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);

    }

    public void removeFriend(String apiToken, String username, final VolleyResponseListener volleyResponseListener) {
        final int[] statusCode = {0};
        boolean status[] = {false};
        final JSONObject[] posts = {null};

        String URL = API_SERVER_URL + "users/" + username + "/remove-friend";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        status[0] = true;
                        posts[0] = response;

                        volleyResponseListener.onResponse(status[0], posts[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                volleyResponseListener.onResponse(status[0], null);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode[0] = response.statusCode;
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

        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);

    }

    public void addFriend(String apiToken, String username, final VolleyResponseListener volleyResponseListener) {
        final int[] statusCode = {0};
        boolean status[] = {false};
        final JSONObject[] posts = {null};

        String URL = API_SERVER_URL + "users/" + username + "/add-friend";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        status[0] = true;
                        posts[0] = response;

                        volleyResponseListener.onResponse(status[0], posts[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                volleyResponseListener.onResponse(status[0], null);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode[0] = response.statusCode;
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

        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);

    }

    public void removePost(String apiToken, int postID, final VolleyResponseListener volleyResponseListener) {
        final int[] statusCode = {0};
        boolean status[] = {false};
        final JSONObject[] posts = {null};

        String URL = API_SERVER_URL + "posts/" + postID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        status[0] = true;
                        posts[0] = response;

                        volleyResponseListener.onResponse(status[0], posts[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyResponseListener.onResponse(status[0], null);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode[0] = response.statusCode;
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

        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);

    }


    public void signupUser(String name, String email, String pwd, final VolleyResponseListener volleyResponseListener) {

        final int[] statusCode = {0};
        final boolean[] status = {false};

        try {
            String URL = API_SERVER_URL + "register";
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
                                    storeApiToken( activity,apiToken);

                                    Toast.makeText(activity, "Registration is Successfully", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(activity, "Username or email is Exist", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(activity, "Check Your Internet Connection!...", Toast.LENGTH_SHORT).show();
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

            MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

