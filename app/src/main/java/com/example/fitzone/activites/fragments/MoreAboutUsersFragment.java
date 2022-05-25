package com.example.fitzone.activites.fragments;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;
import static com.example.fitzone.common_functions.StaticFunctions.getHostUrl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.fitzone.activites.Comments;
import com.example.fitzone.activites.R;
import com.example.fitzone.handelers.HandlePost;
import com.example.fitzone.recycleViewAdapters.AnotherProfilePostsAdapter;
import com.example.fitzone.recycleViewAdapters.ProfilePostsAdapter;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.user_data.UserDataResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoreAboutUsersFragment extends Fragment implements AnotherProfilePostsAdapter.ItemClickListener {


    private RecyclerView recyclerView;
    private TextView userName, userLevel;

    private ImageView profileImage;

    private SwipeRefreshLayout swipeRefreshLayout;

    private AnotherProfilePostsAdapter adapter;

    private Retrofit retrofit;
    private ApiInterface apiInterface;

    private String username;

    public MoreAboutUsersFragment(String username) {
        super(R.layout.fragment_more_about_users);
        this.username = username;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //inflate elements
        userName = view.findViewById(R.id.userName);
        userLevel = view.findViewById(R.id.another_user_level);
        profileImage = view.findViewById(R.id.userImage);
        swipeRefreshLayout = view.findViewById(R.id.profile_swipe_refresh);
        recyclerView = view.findViewById(R.id.recycleView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);

        swipeRefreshLayout.setRefreshing(true);
        getUserProfile(username);
        swipeRefreshLayout.setOnRefreshListener(() -> getUserProfile(username));
    }

    //get and display user data and posts
    private void getUserProfile(String username) {

        if (apiInterface == null)
            return;

        Call<UserDataResponse> call = apiInterface.getUserData("Bearer " + getApiToken(getActivity()), username);
        call.enqueue(new Callback<UserDataResponse>() {
            @SuppressLint({"ResourceType", "SetTextI18n"})
            @Override
            public void onResponse(Call<UserDataResponse> call, Response<UserDataResponse> response) {
                if (response.body() == null)
                    return;

                userName.setText(response.body().getUsername());
                userLevel.setText(getActivity().getString(R.string.level) + ' ' + response.body().getLevel());

                //fill avatar image
                Glide.with(getActivity())
                        .load(getHostUrl(getActivity()) + response.body().getAvatar())
                        .centerCrop()
                        .circleCrop()
                        .placeholder(R.drawable.loading_spinner)
                        .into(profileImage);

                adapter = new AnotherProfilePostsAdapter(getActivity(), response.body().getPosts());
                adapter.setClickListener(MoreAboutUsersFragment.this);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<UserDataResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.like) {
            //handle like button
            int postId = adapter.getItem(position).getId();
            boolean liked = adapter.getItem(position).getLiked();

            HandlePost handlePost = new HandlePost(getActivity());
            //send request
            handlePost.likeOrDislike(liked, postId);
            handlePost.setLikeButtonState(view, !liked);
            adapter.getItem(position).setLiked(!liked);

            int noOfLikesInt = adapter.getItem(position).getNumber_of_likes();

            RecyclerView.ViewHolder rv_view = recyclerView.findViewHolderForAdapterPosition(position);
            TextView noOfLikes = rv_view.itemView.findViewById(R.id.noOfLikes);

            if (adapter.getItem(position).getLiked())
                adapter.getItem(position).setNumber_of_likes(++noOfLikesInt);
            else
                adapter.getItem(position).setNumber_of_likes(--noOfLikesInt);

            noOfLikes.setText("" + adapter.getItem(position).getNumber_of_likes());

        } else if (view.getId() == R.id.comment) {
            //handle comment
            Intent intent;
            intent = new Intent(getContext(), Comments.class);
            intent.putExtra("post_id", adapter.getItem(position).getId());
            startActivity(intent);

        }
    }
}