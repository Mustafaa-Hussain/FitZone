package com.example.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.fitzone.handelers.HandelCommon;
import com.example.fitzone.handelers.HandlePost;
import com.example.fitzone.handelers.HandleRequests;

import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForPosts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends HandelCommon implements RecycleViewAdapterForPosts.ItemClickListener{
    RecycleViewAdapterForPosts adapter;
    RecyclerView recyclerView;
    Button share, cancel;

    PopupWindow popupWindow;

    FloatingActionButton addPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String apiToken = getSharedPreferences("UserData", MODE_PRIVATE).getString("apiToken", null);

        addPost = findViewById(R.id.add_post_fab);
        addPost.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        //handle adding new post

                        // inflate the layout of the popup window
                        LayoutInflater inflater = (LayoutInflater)
                                getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.add_post, null);

                        // create the popup window
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
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

                                    HandleRequests handleRequests = new HandleRequests(HomeActivity.this);
                                    handleRequests.addPost(caption.getText().toString(), content.getText().toString(), apiToken,
                                            new HandleRequests.VolleyResponseListener() {
                                                @Override
                                                public void onResponse(boolean status, JSONObject jsonObject) {
                                                    if (status) {
                                                        Snackbar.make(view, "post shared successfully", Snackbar.LENGTH_LONG)
                                                                .setAction("Action", null).show();

                                                        Intent intent;
                                                        intent = new Intent(HomeActivity.this, HomeActivity.class);
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

        //request and display posts
        HandleRequests handleRequests = new HandleRequests(HomeActivity.this);
        handleRequests.getPosts(apiToken, new HandleRequests.VolleyResponseListener() {
            @Override
            public void onResponse(boolean status, JSONObject jsonObject) {
                if(status){
                    recyclerView = findViewById(R.id.recycleView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                    try {
                        adapter = new RecycleViewAdapterForPosts(HomeActivity.this, jsonObject.getJSONArray("posts"));
                        adapter.setClickListener(HomeActivity.this);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        try {
            //Toast.makeText(this, "id: " + adapter.getItem(position).getString("id"), Toast.LENGTH_SHORT).show();
            if(view.getId() == R.id.like){
                //handle like button
                String postID = adapter.getItem(position).getString("id");
                HandlePost handlePost = new HandlePost(HomeActivity.this);
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
                intent = new Intent(HomeActivity.this, Comments.class);
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
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                askPopup = new PopupWindow(popupView, width, height, true);


                //put the message
                ((TextView)popupView.findViewById(R.id.messageQ)).setText("Info about publisher...");


                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window token
                askPopup.showAtLocation(view, Gravity.CENTER, 0, 0);

                ((LinearLayout)popupView.findViewById(R.id.yes_no_buttons)).setVisibility(View.INVISIBLE);
                //get info about user that post this post

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        askPopup.dismiss();
                        return true;
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}