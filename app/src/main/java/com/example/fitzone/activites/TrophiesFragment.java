package com.example.fitzone.activites;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.recycleViewAdapters.BadgesAdapter;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.badges.DataModelBadgesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrophiesFragment extends Fragment implements BadgesAdapter.ItemClickListener{

    private RecyclerView recyclerView;
    private BadgesAdapter badgesAdapter;
    private DataModelBadgesResponse badgesData;

    public TrophiesFragment() {
        super(R.layout.fragment_badges);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inflate elements
        recyclerView = view.findViewById(R.id.badges_fragment_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        //builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<DataModelBadgesResponse> call = apiInterface.getBadges(getApiToken(getActivity()));

        call.enqueue(new Callback<DataModelBadgesResponse>() {
            @Override
            public void onResponse(Call<DataModelBadgesResponse> call, Response<DataModelBadgesResponse> response) {
                badgesData = response.body();
                badgesAdapter = new BadgesAdapter(getActivity(), badgesData);
                badgesAdapter.setClickListener(TrophiesFragment.this);
                recyclerView.setAdapter(badgesAdapter);
            }

            @Override
            public void onFailure(Call<DataModelBadgesResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_home_activity, new BadgesMoreInfo(badgesData.get(position)))
                .addToBackStack("TrophiesFragment")
                .commit();
    }
}