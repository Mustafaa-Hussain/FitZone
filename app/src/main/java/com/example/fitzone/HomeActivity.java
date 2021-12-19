package com.example.fitzone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener{
    MyRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //posts
        JSONObject postsArray;
        SharedPreferences postsT = getSharedPreferences("posts", MODE_PRIVATE);
        try {
            postsArray = new JSONObject(postsT.getString("allPosts", null));

            recyclerView = findViewById(R.id.recycleView);
            recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

            adapter = new MyRecyclerViewAdapter(HomeActivity.this, postsArray.getJSONArray("post"));
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
                Toast.makeText(HomeActivity.this, "View Comments of post id: " + adapter.getItem(position).getString("id"), Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}