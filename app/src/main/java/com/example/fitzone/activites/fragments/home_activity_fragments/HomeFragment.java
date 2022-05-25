package com.example.fitzone.activites.fragments.home_activity_fragments;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.fitzone.activites.R;
import com.example.fitzone.activites.fragments.CreateNewPost;
import com.example.fitzone.activites.fragments.MoreAboutUsersFragment;
import com.example.fitzone.handelers.HandlePost;
import com.example.fitzone.handelers.HandleRequests;
import com.example.fitzone.recycleViewAdapters.PostsAdapter;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.all_posts.AllPostsResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements PostsAdapter.ItemClickListener {
    private PostsAdapter adapter;
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
                //adding new post
                addPost();
            });

            swipeRefreshLayout.setRefreshing(true);
            getPosts();

            //get and display posts
            swipeRefreshLayout.setOnRefreshListener(this::getPosts);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    private void addPost() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_home_activity, new CreateNewPost())
                .addToBackStack("HomeFragment")
                .commit();
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

                adapter = new PostsAdapter(getActivity(), response.body().getPosts());
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

            //update no. of likes and no. of comments

        } else if (view.getId() == R.id.comment) {
            //handle comment
            Intent intent;
            intent = new Intent(getContext(), Comments.class);
            intent.putExtra("post_id", adapter.getItem(position).getId());
            startActivity(intent);
            //finish();
        } else if (view.getId() == R.id.more) {

            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack("HomeFragment")
                    .replace(R.id.fl_home_activity, new MoreAboutUsersFragment(adapter.getItem(position).getUsername()))
                    .addToBackStack("HomeFragment")
                    .commit();

        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}