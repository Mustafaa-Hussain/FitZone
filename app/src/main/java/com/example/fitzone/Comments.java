package com.example.fitzone;

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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

public class Comments extends HandelCommon {

    RecycleViewAdapterForComments adapter;
    RecyclerView recyclerView;

    FloatingActionButton addComment;

    Button share, cancel;
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        addComment = findViewById(R.id.add_comment_fab);
        addComment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //handle adding new post

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.add_comment, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
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
                        Snackbar.make(view, "share comment", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        popupWindow.dismiss();
                    }
                });

                cancel = popupView.findViewById(R.id.cancelPopUp);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(view, "cancel comment", Snackbar.LENGTH_LONG)
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


        Intent intent = getIntent();

        try {
            JSONArray arrayOfComments = new JSONArray(intent.getStringExtra("post_id"));

            if(!intent.getStringExtra("post_id").equals("[]")){
                recyclerView = findViewById(R.id.recycleView);
                recyclerView.setLayoutManager(new LinearLayoutManager(Comments.this));
                adapter = new RecycleViewAdapterForComments(Comments.this, arrayOfComments);
                recyclerView.setAdapter(adapter);
            }
            else{
                Toast.makeText(Comments.this, "This Post Dos not Contain Comments...", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}