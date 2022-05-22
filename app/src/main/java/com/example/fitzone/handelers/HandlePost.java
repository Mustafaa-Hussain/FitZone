package com.example.fitzone.handelers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;

import com.example.fitzone.activites.R;
import com.example.fitzone.retrofit_requists.data_models.user_profile_data.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HandlePost {
    Activity activity;
    Post data;
    public HandlePost(Activity activity){
        this.activity = activity;
    }

    public void setPostData(Post data){
        this.data = data;
    }

    public String getStatus(int id, JSONArray posts){
        String status = "false";

        for(int i = 0; i < posts.length(); i++){
            try {
                if(posts.getJSONObject(i).getString("id").equals(id)){
                    status = posts.getJSONObject(i).getString("liked");
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    public void setLikeButtonState(View view, int postID){
        //get like state
//        Toast.makeText(context, ((Button)view).getHint(), Toast.LENGTH_SHORT).show();

        if(((Button)view).getHint().toString().equals("true")){
            ((Button)view).setBackground(activity.getResources().getDrawable(R.drawable.dislike_button));
            ((Button)view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.min_one, 0);
            ((Button)view).setHint("false");
        }
        else{
            ((Button)view).setBackground(activity.getResources().getDrawable(R.drawable.like_button));
            ((Button)view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_one, 0);
            ((Button)view).setHint("true");
        }
    }

    public void likeOrDislike(int postID){

        SharedPreferences apiTokenFile = activity.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String apiToken = apiTokenFile.getString("apiToken", null);

        HandleRequests handleRequests = new HandleRequests(activity);

        SharedPreferences postsFile = activity.getSharedPreferences("posts", Context.MODE_PRIVATE);

        try {
            JSONObject jsonObject = new JSONObject(postsFile.getString("allPosts", null));

            JSONArray postsArray = jsonObject.getJSONArray("posts");

            if(getStatus(postID, postsArray).equals("true")){
                handleRequests.setDislike(apiToken, postID, new HandleRequests.VolleyResponseListener() {

                    @Override
                    public void onResponse(boolean status, JSONObject jsonObject) {
                        if(status){
                            handleRequests.getPosts(apiToken, new HandleRequests.VolleyResponseListener() {
                                @Override
                                public void onResponse(boolean status, JSONObject jsonObject) {
                                    Intent intent;
                                    if(status){
                                        SharedPreferences posts = activity.getSharedPreferences("posts", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = posts.edit();
                                        editor.putString("allPosts", jsonObject.toString());
                                        editor.commit();
                                    }
                                }
                            });
                        }
                    }
                });
            }
            else{
                handleRequests.setLike(apiToken, postID, new HandleRequests.VolleyResponseListener() {

                    @Override
                    public void onResponse(boolean status, JSONObject jsonObject) {
                        if(status){
                            handleRequests.getPosts(apiToken, new HandleRequests.VolleyResponseListener() {
                                @Override
                                public void onResponse(boolean status, JSONObject jsonObject) {
                                    Intent intent;
                                    if(status){
                                        SharedPreferences posts = activity.getSharedPreferences("posts", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = posts.edit();
                                        editor.putString("allPosts", jsonObject.toString());
                                        editor.commit();
                                    }
                                }
                            });
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePost(){
        SharedPreferences apiTokenFile = activity.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String apiToken = apiTokenFile.getString("apiToken", null);

        HandleRequests handleRequests = new HandleRequests(activity);

        handleRequests.getPosts(apiToken, (status, jsonObject) -> {

            if(status){
                SharedPreferences posts = activity.getSharedPreferences("posts", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = posts.edit();
                editor.putString("allPosts", jsonObject.toString());
                editor.apply();
            }
        });
    }
}
