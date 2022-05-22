package com.example.fitzone.activites.fragments;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getHostUrl;
import static com.example.fitzone.common_functions.StaticFunctions.storeApiToken;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.example.fitzone.OnSwipeTouchListener;
import com.example.fitzone.activites.Comments;
import com.example.fitzone.activites.FriendsPage;
import com.example.fitzone.activites.LoginActivity;
import com.example.fitzone.activites.R;
import com.example.fitzone.handelers.HandlePost;
import com.example.fitzone.handelers.HandleRequests;
import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForPosts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment implements RecycleViewAdapterForPosts.ItemClickListener {

    private String userId;
    private RecycleViewAdapterForPosts adapter;
    private RecyclerView recyclerView;
    private Button share, cancel, yes, no, showMyFriends, logout;
    private TextView userName;

    private ImageView profileImage;

    private String apiToken;

    private PopupWindow popupWindow;

    private FloatingActionButton addPost;

    private SwipeRefreshLayout swipeRefreshLayout;

    private HandleRequests handleRequests;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inflate elements
        userName = view.findViewById(R.id.userName);
        profileImage = view.findViewById(R.id.userImage);
        swipeRefreshLayout = view.findViewById(R.id.profile_swipe_refresh);
        showMyFriends = view.findViewById(R.id.myFriends);
        recyclerView = view.findViewById(R.id.recycleView);

        apiToken = getApiToken(getActivity());

        showMyFriends.setOnClickListener(this::showMyFriends);

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(this::logout);

        handleRequests = new HandleRequests(getActivity());

        recyclerView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {

            public void onSwipeTop() {
                //TODO thomething when scroling up
            }

            public void onSwipeBottom() {
                //TODO thomething when scroling down
            }

        });

        swipeRefreshLayout.setRefreshing(true);
        getUserProfile();

        swipeRefreshLayout.setOnRefreshListener(this::getUserProfile);

        addPost = view.findViewById(R.id.add_post_inProfile_fab);
        addPost.setOnClickListener(view12 -> {

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
            popupWindow.showAtLocation(view12, Gravity.CENTER, 0, 0);

            share = popupView.findViewById(R.id.sharePost);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView caption, content;
                    caption = popupView.findViewById(R.id.caption);
                    content = popupView.findViewById(R.id.content);

                    if (!caption.getText().equals("") && !content.getText().equals("")) {

                        int postType = 0;
                        handleRequests = new HandleRequests(getActivity());
                        handleRequests.addPost(caption.getText().toString(), content.getText().toString(), postType, apiToken,
                                new HandleRequests.VolleyResponseListener() {
                                    @Override
                                    public void onResponse(boolean status, JSONObject jsonObject) {
                                        if (status) {
                                            Snackbar.make(view12, "post shared successfully", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();

                                            Intent intent;
                                            intent = new Intent(getActivity(), ProfileFragment.class);
                                            startActivity(intent);

                                            popupWindow.dismiss();
                                        } else {
                                            Snackbar.make(view12, "something wrong with your internet connection", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    }
                                });
                    } else {
                        Snackbar.make(view12, "must fill all fields", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }
            });

            cancel = popupView.findViewById(R.id.cancelPopUp);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(view12, "cancel post", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    popupWindow.dismiss();
                }
            });

            // dismiss the popup window when touched
            popupView.setOnTouchListener((v, event) -> {
                popupWindow.dismiss();
                return true;
            });
        });

        //handle deleting post


    }

    //get and display user data and posts
    private void getUserProfile() {
        handleRequests.getUserProfile(apiToken, (status, jsonObject) -> {
            try {
                if (jsonObject.isNull("id"))
                    return;
                userId = jsonObject.getString("id");
                userName.setText(jsonObject.getString("username"));

                //fill avatar image
                Glide.with(getActivity())
                        .load(getHostUrl(getActivity()) + jsonObject.getString("avatar"))
                        .centerCrop()
                        .circleCrop()
                        .placeholder(R.drawable.loading_spinner)
                        .into(profileImage);

                //request and display posts
                HandleRequests handleRequests12;
                handleRequests12 = new HandleRequests(getActivity());
                handleRequests12.getPosts(apiToken, new HandleRequests.VolleyResponseListener() {
                    @Override
                    public void onResponse(boolean status, JSONObject jsonObject) {
                        if (status) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            try {
                                JSONArray myPosts = new JSONArray();

                                for (int i = 0; i < jsonObject.getJSONArray("posts").length(); i++) {
                                    if (jsonObject.getJSONArray("posts").getJSONObject(i).getString("user_id").equals(userId)) {
                                        myPosts.put(jsonObject.getJSONArray("posts").getJSONObject(i));
                                    }
                                }
                                adapter = new RecycleViewAdapterForPosts(getActivity(), myPosts);
                                adapter.setClickListener(ProfileFragment.this);
                                recyclerView.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        try {
//            Toast.makeText(this, view.toString(), Toast.LENGTH_SHORT).show();
            String postID = adapter.getItem(position).getString("id");
            if (view.getId() == R.id.like) {
                //handle like button
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
                intent = new Intent(getActivity(), Comments.class);
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
                ((TextView) popupView.findViewById(R.id.messageQ)).setText("You want to remove this post?");

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window token
                askPopup.showAtLocation(view, Gravity.CENTER, 0, 0);

                yes = popupView.findViewById(R.id.yes);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String apiToken = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE).getString("apiToken", null);
                        HandleRequests handleRequests = new HandleRequests(getActivity());

                        handleRequests.removePost(apiToken, postID,
                                new HandleRequests.VolleyResponseListener() {
                                    @Override
                                    public void onResponse(boolean status, JSONObject jsonObject) {
                                        if (status) {
                                            Intent intent;
                                            Snackbar.make(view, "Post Removed.", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();

                                            intent = new Intent(getActivity(), ProfileFragment.class);
                                            startActivity(intent);

                                            askPopup.dismiss();
                                        } else {
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
        intent = new Intent(getActivity(), FriendsPage.class);
        startActivity(intent);
    }

    public void logout(View view) {

        HandleRequests handleRequests1 = new HandleRequests(getActivity());
        handleRequests1.logout(apiToken, (status, jsonObject) -> {
            if (status) {
                Toast.makeText(getActivity(), "Logout", Toast.LENGTH_SHORT).show();

                storeApiToken(getActivity(), "");

                Intent intent;
                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }
}