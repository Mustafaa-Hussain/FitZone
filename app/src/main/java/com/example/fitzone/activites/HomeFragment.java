package com.example.fitzone.activites;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.example.fitzone.handelers.HandlePost;
import com.example.fitzone.handelers.HandleRequests;
import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForPosts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment implements RecycleViewAdapterForPosts.ItemClickListener {
    RecycleViewAdapterForPosts adapter;
    RecyclerView recyclerView;
    Button share, cancel;

    PopupWindow popupWindow;

    SwipeRefreshLayout swipeRefreshLayout;

    FloatingActionButton addPost;


    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //adding new post
        try {
            String apiToken = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE).getString("apiToken", null);
            addPost = view.findViewById(R.id.add_post_fab);
            addPost.setOnClickListener(v -> {////////

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

            //request and display posts
            HandleRequests handleRequests = new HandleRequests(getActivity());
            handleRequests.getPosts(apiToken, (status, jsonObject) -> {
                if (status) {
                    recyclerView = view.findViewById(R.id.recycleView);

                    try {
                        adapter = new RecycleViewAdapterForPosts(getActivity(), jsonObject.getJSONArray("posts"));
                        adapter.setClickListener(HomeFragment.this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                //request and display posts
                handleRequests.getPosts(apiToken, (status, jsonObject) -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (status) {
                        recyclerView = view.findViewById(R.id.recycleView);

                        try {
                            adapter = new RecycleViewAdapterForPosts(getActivity(), jsonObject.getJSONArray("posts"));
                            adapter.setClickListener(HomeFragment.this);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        try {
            //Toast.makeText(this, "id: " + adapter.getItem(position).getString("id"), Toast.LENGTH_SHORT).show();
            if (view.getId() == R.id.like) {
                //handle like button
                String postID = adapter.getItem(position).getString("id");
                HandlePost handlePost = new HandlePost(getActivity());
                //send request
                handlePost.likeOrDislike(postID);
                handlePost.setLikeButtonState(view, postID);
                handlePost.updatePost();

                int noOfLikesInt = Integer.parseInt(adapter.getItem(position).getString("number_of_likes"));

                RecyclerView.ViewHolder rv_view = recyclerView.findViewHolderForAdapterPosition(position);
                TextView noOfLikes = rv_view.itemView.findViewById(R.id.noOfLikes);

                if (((Button) view.findViewById(R.id.like)).getHint().equals("true"))
                    adapter.getItem(position).put("number_of_likes", --noOfLikesInt);
                else
                    adapter.getItem(position).put("number_of_likes", ++noOfLikesInt);

                noOfLikes.setText(adapter.getItem(position).getString("number_of_likes"));

                //update no. of likes and no. of comments

            } else if (view.getId() == R.id.comment) {
                //handle comment
                Intent intent;
                intent = new Intent(getContext(), Comments.class);
                intent.putExtra("post_id", adapter.getItem(position).getString("id"));
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}