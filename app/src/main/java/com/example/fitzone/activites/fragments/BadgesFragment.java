package com.example.fitzone.activites.fragments;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fitzone.activites.R;
import com.example.fitzone.recycleViewAdapters.BadgesAdapter;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.badges.BadgesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BadgesFragment extends Fragment implements BadgesAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private BadgesAdapter badgesAdapter;
    private BadgesResponse badgesData;
    private ApiInterface apiInterface;
    private SwipeRefreshLayout swipeRefreshLayout;

    public BadgesFragment() {
        super(R.layout.fragment_badges);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //inflate elements
        recyclerView = view.findViewById(R.id.badges_fragment_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.badges_swipe_refresh);

        //set layout manager to recycler View
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));


        //builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);

        //set swipe Refresh is true
        swipeRefreshLayout.setRefreshing(true);
        getBadges();
        //get all badges and pass it to recycler view adapter
        swipeRefreshLayout.setOnRefreshListener(this::getBadges);


    }

    //get all badges and pass it to recycler view adapter
    private void getBadges() {
        if (apiInterface == null)
            return;

        Call<BadgesResponse> call = apiInterface.getBadges(getApiToken(getActivity()));
        call.enqueue(new Callback<BadgesResponse>() {
            @Override
            public void onResponse(Call<BadgesResponse> call, Response<BadgesResponse> response) {
                if (response.body() == null)
                    return;

                badgesData = response.body();
                badgesAdapter = new BadgesAdapter(getActivity(), badgesData);
                badgesAdapter.setClickListener(BadgesFragment.this);
                recyclerView.setAdapter(badgesAdapter);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<BadgesResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("TrophiesFragment")
                .replace(R.id.fl_home_activity, new BadgesMoreInfo(badgesData.get(position)))
                .commit();
    }
}