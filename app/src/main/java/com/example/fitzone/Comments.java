package com.example.fitzone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class Comments extends AppCompatActivity {

    RecycleViewAdapterForComments adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

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