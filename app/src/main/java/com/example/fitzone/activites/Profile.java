package com.example.fitzone.activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.handelers.HandelCommon;
import com.example.fitzone.handelers.HandlePost;
import com.example.fitzone.handelers.HandleRequests;
import com.example.fitzone.OnSwipeTouchListener;

import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForPosts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Profile extends HandelCommon implements RecycleViewAdapterForPosts.ItemClickListener {

    String userId;
    RecycleViewAdapterForPosts adapter;
    RecyclerView recyclerView;
    Button share, cancel, yes, no;

    SharedPreferences apiTokenFile;
    String apiToken;

    PopupWindow popupWindow;

    FloatingActionButton addPost;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        apiTokenFile = getSharedPreferences("UserData", MODE_PRIVATE);
        apiToken = apiTokenFile.getString("apiToken", null);

        LinearLayout userData = findViewById(R.id.userData);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setOnTouchListener(new OnSwipeTouchListener(Profile.this) {

            public void onSwipeTop() {
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
//                userData.setLayoutParams(params);
            }

            public void onSwipeBottom() {
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                userData.setLayoutParams(params);
            }

        });
//path for default user avatar
//storage/avatars/avatar.gif
//request and display user profile

        HandleRequests handleRequests = new HandleRequests(Profile.this);
        handleRequests.getUserProfile(apiToken, new HandleRequests.VolleyResponseListener() {
            @Override
            public void onResponse(boolean status, JSONObject jsonObject) {
//                ((TextView)findViewById(R.id.userName)).setText(jsonObject.toString());
                try {
                    userId = jsonObject.getString("id");
                    ((TextView)findViewById(R.id.userName)).setText(jsonObject.getString("username"));


                    //request and display posts
                    HandleRequests handleRequests = new HandleRequests(Profile.this);
                    handleRequests = new HandleRequests(Profile.this);
                    handleRequests.getPosts(apiToken, new HandleRequests.VolleyResponseListener() {
                        @Override
                        public void onResponse(boolean status, JSONObject jsonObject) {
                            if(status){
                                recyclerView.setLayoutManager(new LinearLayoutManager(Profile.this));
                                try {
                                    JSONArray myPosts = new JSONArray();

                                    for(int i = 0; i < jsonObject.getJSONArray("posts").length(); i++){
                                        if(jsonObject.getJSONArray("posts").getJSONObject(i).getString("user_id").equals(userId)){
                                            myPosts.put(jsonObject.getJSONArray("posts").getJSONObject(i));
                                        }
                                    }
                                    int [] colors ={
                                            getResources().getColor(R.color.regular_post_gray),
                                            getResources().getColor(R.color.achievement_post_yellow),
                                            getResources().getColor(R.color.image_post),
                                            getResources().getColor(R.color.video_post)
                                    };
                                    adapter = new RecycleViewAdapterForPosts(Profile.this, myPosts, colors);
                                    adapter.setClickListener(Profile.this);
                                    recyclerView.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        addPost = findViewById(R.id.add_post_inProfile_fab);
        addPost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //handle adding new post

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.add_post, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                popupWindow = new PopupWindow(popupView, width, height, true);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window token
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                share = popupView.findViewById(R.id.sharePost);
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView caption, content;
                        caption = popupView.findViewById(R.id.caption);
                        content = popupView.findViewById(R.id.content);

                        if(!caption.getText().equals("") && !content.getText().equals("")){

                            int postType = 0;
                            HandleRequests handleRequests = new HandleRequests(Profile.this);
                            handleRequests.addPost(caption.getText().toString(), content.getText().toString(), postType, apiToken,
                                    new HandleRequests.VolleyResponseListener() {
                                        @Override
                                        public void onResponse(boolean status, JSONObject jsonObject) {
                                            if (status) {
                                                Snackbar.make(view, "post shared successfully", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();

                                                Intent intent;
                                                intent = new Intent(Profile.this, Profile.class);
                                                startActivity(intent);

                                                popupWindow.dismiss();
                                            }
                                            else {
                                                Snackbar.make(view, "something wrong with your internet connection", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        }
                                    });
                        }
                        else{
                            Snackbar.make(view, "must fill all fields", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }
                });

                cancel = popupView.findViewById(R.id.cancelPopUp);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(view, "cancel post", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        popupWindow.dismiss();
                    }
                });

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }});

        //handle deleting post


    }

    @Override
    public void onItemClick(View view, int position) {
        try {
//            Toast.makeText(this, view.toString(), Toast.LENGTH_SHORT).show();
            String postID = adapter.getItem(position).getString("id");
            if(view.getId() == R.id.like){
                //handle like button
                HandlePost handlePost = new HandlePost(Profile.this);
                //send request
                handlePost.likeOrDislike(postID);
                handlePost.setLikeButtonState(view, postID);
                handlePost.updatePost();

                int noOfLikesInt = Integer.parseInt(adapter.getItem(position).getString("number_of_likes"));

                RecyclerView.ViewHolder rv_view = recyclerView.findViewHolderForAdapterPosition(position);
                TextView noOfLikes = rv_view.itemView.findViewById(R.id.noOfLikes);

                if(((Button)view.findViewById(R.id.like)).getHint().equals("true"))
                    adapter.getItem(position).put("number_of_likes", --noOfLikesInt);
                else
                    adapter.getItem(position).put("number_of_likes", ++noOfLikesInt);

                noOfLikes.setText(adapter.getItem(position).getString("number_of_likes"));

                //update no. of likes and no. of comments

            }
            else if(view.getId() == R.id.comment){
                //handle comment
                Intent intent;
                intent = new Intent(Profile.this, Comments.class);
                intent.putExtra("post_id", adapter.getItem(position).getString("id"));
                startActivity(intent);
                //finish();
            }
            else if(view.getId() == R.id.more){
                PopupWindow askPopup;

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)

                getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.ask_if_yes_or_no, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                askPopup = new PopupWindow(popupView, width, height, true);


                //put the message
                ((TextView)popupView.findViewById(R.id.messageQ)).setText("You want to remove this post?");

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window token
                askPopup.showAtLocation(view, Gravity.CENTER, 0, 0);

                yes = popupView.findViewById(R.id.yes);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String apiToken = getSharedPreferences("UserData", MODE_PRIVATE).getString("apiToken", null);
                        HandleRequests handleRequests = new HandleRequests(Profile.this);

                        handleRequests.removePost(apiToken, postID,
                                new HandleRequests.VolleyResponseListener() {
                                    @Override
                                    public void onResponse(boolean status, JSONObject jsonObject) {
                                        if (status) {
                                            Intent intent;
                                            Snackbar.make(view, "Post Removed.", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();

                                            intent = new Intent(Profile.this, Profile.class);
                                            startActivity(intent);

                                            askPopup.dismiss();
                                        }
                                        else {
                                            Snackbar.make(view, "Cannot delete this post", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    }
                                });
                    }

                });

                no = popupView.findViewById(R.id.no);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askPopup.dismiss();
                    }
                });

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //askPopup.dismiss();
                        return true;
                    }
                });
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showMyFriends(View view) {
        Intent intent;
        intent = new Intent(Profile.this, FriendsPage.class);
        startActivity(intent);
        finish();
    }

    public void logout(View view) {

        HandleRequests handleRequests = new HandleRequests(Profile.this);
        handleRequests.getUserProfile(apiToken, new HandleRequests.VolleyResponseListener() {
            @Override
            public void onResponse(boolean status, JSONObject jsonObject) {
//                ((TextView)findViewById(R.id.userName)).setText(jsonObject.toString());
                try {
                    userId = jsonObject.getString("id");
                    ((TextView)findViewById(R.id.userName)).setText(jsonObject.getString("username"));


                    //request and display posts
                    HandleRequests handleRequests = new HandleRequests(Profile.this);
                    handleRequests = new HandleRequests(Profile.this);
                    handleRequests.logout(apiToken, new HandleRequests.VolleyResponseListener() {
                        @Override
                        public void onResponse(boolean status, JSONObject jsonObject) {
                            if(status){
                                Toast.makeText(Profile.this, "Logout", Toast.LENGTH_SHORT).show();

                                SharedPreferences.Editor editor = apiTokenFile.edit();
                                editor.putString("apiToken", "");
                                editor.commit();

                                Intent intent;
                                intent = new Intent(Profile.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}