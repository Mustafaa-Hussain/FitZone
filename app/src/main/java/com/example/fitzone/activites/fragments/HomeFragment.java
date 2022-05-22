package com.example.fitzone.activites.fragments;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fitzone.activites.Comments;
import com.example.fitzone.activites.HomeActivity;
import com.example.fitzone.activites.R;
import com.example.fitzone.handelers.HandlePost;
import com.example.fitzone.handelers.HandleRequests;
import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForPosts;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.all_posts.AllPostsResponse;
import com.example.fitzone.retrofit_requists.data_models.user_profile_data.Comment;
import com.example.fitzone.retrofit_requists.data_models.user_profile_data.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements RecycleViewAdapterForPosts.ItemClickListener {
    private RecycleViewAdapterForPosts adapter;
    private RecyclerView recyclerView;
    private Button share, cancel;

    private PopupWindow popupWindow;

    private SwipeRefreshLayout swipeRefreshLayout;

    private FloatingActionButton addPost;

    private HandleRequests handleRequests;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    private String apiToken;
    private Retrofit retrofit;
    private ApiInterface apiInterface;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inflate elements
        recyclerView = view.findViewById(R.id.recycleView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);


        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);


        //adding new post
        try {
            handleRequests = new HandleRequests(getActivity());

            apiToken = getApiToken(getActivity());
            addPost = view.findViewById(R.id.add_post_fab);
            addPost.setOnClickListener(v -> {

                //handle adding new post

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                share.setOnClickListener(v12 -> {

                    TextView caption, content;
                    caption = popupView.findViewById(R.id.caption);
                    content = popupView.findViewById(R.id.content);

                    if (!caption.getText().equals("") && !content.getText().equals("")) {

                        int postType = 0;
                        HandleRequests handleRequests = new HandleRequests(getActivity());
                        handleRequests.addPost(caption.getText().toString(), content.getText().toString(), postType, apiToken,
                                (status, jsonObject) -> {
                                    if (status) {
                                        Snackbar.make(view, "post shared successfully", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        Intent intent;
                                        intent = new Intent(getActivity(), HomeActivity.class);
                                        startActivity(intent);

                                        popupWindow.dismiss();
                                    } else {
                                        Snackbar.make(view, "something wrong with your internet connection", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                    } else {
                        Snackbar.make(view, "must fill all fields", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                });

                cancel = popupView.findViewById(R.id.cancelPopUp);
                cancel.setOnClickListener(v1 -> {
                    Snackbar.make(view, "cancel post", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    popupWindow.dismiss();
                });

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //                                popupWindow.dismiss();
                        return true;
                    }
                });
            });

            swipeRefreshLayout.setRefreshing(true);
            getPosts();

            //get and display posts
            swipeRefreshLayout.setOnRefreshListener(this::getPosts);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get and display post
    private void getPosts() {
        if (apiInterface == null)
            return;

        Call<AllPostsResponse> call = apiInterface.getAllPosts("Bearer " + getApiToken(getActivity()));
        call.enqueue(new Callback<AllPostsResponse>() {
            @Override
            public void onResponse(Call<AllPostsResponse> call, Response<AllPostsResponse> response) {
                if (response.body() == null)
                    return;


                ArrayList posts = new ArrayList();
                ArrayList comments = new ArrayList();

                //create posts List
                for (com.example.fitzone.retrofit_requists.data_models.all_posts.Post post : response.body().getPosts()) {
                    for (com.example.fitzone.retrofit_requists.data_models.all_posts.Comment comment : post.getComments()) {
                        comments.add(new Comment(comment.getContent(),
                                comment.getCreated_at(),
                                comment.getId(),
                                comment.getPost_id(),
                                comment.getUpdated_at(),
                                comment.getUser_avatar(),
                                comment.getUser_id(),
                                comment.getUsername()));
                    }

                    posts.add(new Post(post.getCaption(),
                            comments,
                            post.getContent(),
                            post.getCreated_at(),
                            post.getId(),
                            post.getLiked(),
                            post.getLikes(),
                            post.getNumber_of_comments(),
                            post.getNumber_of_likes(),
                            post.getType(),
                            post.getUpdated_at(),
                            post.getUser_avatar(),
                            post.getUser_id(),
                            post.getUsername()));
                }

                adapter = new RecycleViewAdapterForPosts(getActivity(), posts);
                adapter.setClickListener(HomeFragment.this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<AllPostsResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.like) {
            //handle like button
            int postID = adapter.getItem(position).getId();
            HandlePost handlePost = new HandlePost(getActivity());
            //send request
            handlePost.likeOrDislike(postID);
            handlePost.setLikeButtonState(view, postID);
            handlePost.updatePost();

            int noOfLikesInt = adapter.getItem(position).getNumber_of_likes();

            RecyclerView.ViewHolder rv_view = recyclerView.findViewHolderForAdapterPosition(position);
            TextView noOfLikes = rv_view.itemView.findViewById(R.id.noOfLikes);

            if (((Button) view.findViewById(R.id.like)).getHint().equals("true"))
                adapter.getItem(position).setNumber_of_likes(--noOfLikesInt);
            else
                adapter.getItem(position).setNumber_of_likes(++noOfLikesInt);

            noOfLikes.setText(adapter.getItem(position).getNumber_of_likes());

            //update no. of likes and no. of comments

        } else if (view.getId() == R.id.comment) {
            //handle comment
            Intent intent;
            intent = new Intent(getContext(), Comments.class);
            intent.putExtra("post_id", adapter.getItem(position).getId());
            startActivity(intent);
            //finish();
        } else if (view.getId() == R.id.more) {
            PopupWindow askPopup;

            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater)

                    getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.ask_if_yes_or_no, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            askPopup = new PopupWindow(popupView, width, height, true);


            //put the message
            ((TextView) popupView.findViewById(R.id.messageQ)).setText(R.string.more_mesage);


            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window token
            askPopup.showAtLocation(view, Gravity.CENTER, 0, 0);

            ((LinearLayout) popupView.findViewById(R.id.yes_no_buttons)).setVisibility(View.GONE);
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
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}