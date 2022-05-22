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
import com.example.fitzone.recycleViewAdapters.ProgramsAdapter;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.programs.ProgramsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProgramsFragment extends Fragment implements ProgramsAdapter.ItemClickListener {

    private ApiInterface apiInterface;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgramsAdapter adapter;
    private ProgramsResponse programData;

    public ProgramsFragment() {
        super(R.layout.fragment_programs);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.programs_swipe_refresh);
        recyclerView = view.findViewById(R.id.programs_recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        //builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);

        swipeRefreshLayout.setRefreshing(true);
        getPrograms();
        swipeRefreshLayout.setOnRefreshListener(this::getPrograms);

    }

    private void getPrograms() {
        if (apiInterface == null)
            return;

        Call<ProgramsResponse> call = apiInterface.getPrograms("Bearer " + getApiToken(getActivity()));
        call.enqueue(new Callback<ProgramsResponse>() {
            @Override
            public void onResponse(Call<ProgramsResponse> call, Response<ProgramsResponse> response) {
                if (response.body() == null)
                    return;

                programData = response.body();

                adapter = new ProgramsAdapter(getActivity(), programData);
                adapter.setClickListener(ProgramsFragment.this);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ProgramsResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        if (programData == null)
            return;

        //this toast for test
        Toast.makeText(getActivity(),
                "program name: " + programData.get(position).getName(),
                Toast.LENGTH_SHORT).show();

        getActivity().
                getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("ProgramsFragments")
                .replace(R.id.fl_home_activity, new TrainingFragment(programData.get(position)))
                .commit();

    }
}
