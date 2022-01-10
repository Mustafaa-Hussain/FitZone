package com.example.fitzone;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
                                Snackbar.make(view, "share post", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                                popupWindow.dismiss();
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

        //posts
        JSONObject postsArray;
        SharedPreferences postsT = getSharedPreferences("posts", MODE_PRIVATE);

        try {
            postsArray = new JSONObject(postsT.getString("allPosts", null));

            recyclerView = findViewById(R.id.recycleView);
            recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
            adapter = new RecycleViewAdapterForPosts(HomeActivity.this, postsArray.getJSONArray("posts"));
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                intent.putExtra("post_id", adapter.getItem(position).getString("comments"));
                startActivity(intent);
                //finish();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}