package com.example.fitzone.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.fitzone.handelers.HandelCommon;
import com.example.fitzone.handelers.HandleRequests;

import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForComments;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Comments extends HandelCommon {

    RecycleViewAdapterForComments adapter;
    RecyclerView recyclerView;

    FloatingActionButton addComment;

    Intent intent;

    Button share, cancel;
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        SharedPreferences apiTokenFile = getSharedPreferences("UserData", Comments.MODE_PRIVATE);
        String apiToken = apiTokenFile.getString("apiToken", null);

        intent = getIntent();
        int postID = Integer.parseInt(intent.getStringExtra("post_id"));

        addComment = findViewById(R.id.add_comment);
        addComment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //handle adding new post

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.add_comment, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                popupWindow = new PopupWindow(popupView, width, height, true);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window token
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                share = popupView.findViewById(R.id.shareComment);
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText content;
                        content = popupView.findViewById(R.id.commentContent);

                        if(!content.getText().equals("")){

                            String apiToken = getSharedPreferences("UserData", MODE_PRIVATE).getString("apiToken", null);

                            HandleRequests handleRequests = new HandleRequests(Comments.this);
                            handleRequests.addComment(content.getText().toString(), postID, apiToken,
                                    new HandleRequests.VolleyResponseListener() {
                                        @Override
                                        public void onResponse(boolean status, JSONObject jsonObject) {
                                            if (status) {
                                                Snackbar.make(view, "post shared successfully", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();

                                                intent = new Intent(Comments.this, Comments.class);
                                                intent.putExtra("post_id", String.valueOf(postID));
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

                cancel = popupView.findViewById(R.id.cancelPopUpCommentWindow);
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

        HandleRequests handleRequests = new HandleRequests(Comments.this);
        handleRequests.getPost(apiToken, postID, new HandleRequests.VolleyResponseListener() {
            @Override
            public void onResponse(boolean status, JSONObject jsonObject) {

                if(status){
                    recyclerView = findViewById(R.id.recycleView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Comments.this));

                    try {
                        JSONArray myComments = jsonObject.getJSONArray("comments");

                        if(!myComments.toString().equals("[]")) {
                            adapter = new RecycleViewAdapterForComments(Comments.this, myComments);
                        }
                        else {
                            Snackbar.make(findViewById(R.id.recycleView), "this post doesn't have comments", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }
}