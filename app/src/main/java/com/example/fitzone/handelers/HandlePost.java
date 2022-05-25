package com.example.fitzone.handelers;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;

import com.example.fitzone.activites.R;

import org.json.JSONObject;

public class HandlePost {
    private final Activity activity;

    public HandlePost(Activity activity) {
        this.activity = activity;
    }

    public void setLikeButtonState(View view, boolean liked) {
        if (liked) {
            ((Button) view).setBackground(activity.getResources().getDrawable(R.drawable.dislike_button));
            ((Button) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.min_one, 0);
        } else {
            ((Button) view).setBackground(activity.getResources().getDrawable(R.drawable.like_button));
            ((Button) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_one, 0);
        }
    }

    public void likeOrDislike(boolean liked, int postId) {

        String apiToken = getApiToken(activity);

        HandleRequests handleRequests = new HandleRequests(activity);

        if (liked) {
            handleRequests.setDislike(apiToken, postId, (status, jsonObject) -> {
                if (status) {
                    handleRequests.getPosts(apiToken, new HandleRequests.VolleyResponseListener() {
                        @Override
                        public void onResponse(boolean status, JSONObject jsonObject) {
                            Intent intent;
                            if (status) {
                                SharedPreferences posts = activity.getSharedPreferences("posts", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = posts.edit();
                                editor.putString("allPosts", jsonObject.toString());
                                editor.commit();
                            }
                        }
                    });
                }
            });
        } else {
            handleRequests.setLike(apiToken, postId, (status, jsonObject) -> {
                if (status) {
                    handleRequests.getPosts(apiToken, (status1, jsonObject1) -> {
                        Intent intent;
                        if (status1) {
                            SharedPreferences posts = activity.getSharedPreferences("posts", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = posts.edit();
                            editor.putString("allPosts", jsonObject1.toString());
                            editor.commit();
                        }
                    });
                }
            });
        }
    }

    public void updatePost() {

        String apiToken = getApiToken(activity);

        HandleRequests handleRequests = new HandleRequests(activity);
        handleRequests.getPosts(apiToken, (status, jsonObject) -> {

            if (status) {
                SharedPreferences posts = activity.getSharedPreferences("posts", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = posts.edit();
                editor.putString("allPosts", jsonObject.toString());
                editor.apply();
            }
        });
    }
}
