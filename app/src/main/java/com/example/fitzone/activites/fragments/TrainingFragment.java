package com.example.fitzone.activites.fragments;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fitzone.activites.DayActivity;
import com.example.fitzone.activites.R;
import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForDaysPrograms;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.program_routine.ProgramRoutineResponse;
import com.example.fitzone.retrofit_requists.data_models.programs.ProgramsResponseItem;

import org.json.JSONArray;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrainingFragment extends Fragment implements RecycleViewAdapterForDaysPrograms.ItemClickListener {

    private RecycleViewAdapterForDaysPrograms adapter;
    private RecyclerView recyclerView;
    private final ProgramsResponseItem programData;
    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String apiToken;
    private JSONArray myDays;
    private ProgramRoutineResponse daysData;

    public TrainingFragment(ProgramsResponseItem programData) {
        super(R.layout.fragment_training);
        this.programData = programData;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inflate elements
        swipeRefreshLayout = view.findViewById(R.id.training_swipe_refresh);
        recyclerView = view.findViewById(R.id.recycleViewOfPrograms);


        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        apiToken = getApiToken(getActivity());

        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);

        swipeRefreshLayout.setRefreshing(true);
        getPrograms();
        swipeRefreshLayout.setOnRefreshListener(this::getPrograms);
    }

    private void getPrograms() {
        if (apiInterface == null) return;

        Call<ProgramRoutineResponse> call = apiInterface.getProgramRoutine("Bearer " + apiToken, programData.getName());
        call.enqueue(new Callback<ProgramRoutineResponse>() {
            @Override
            public void onResponse(Call<ProgramRoutineResponse> call, Response<ProgramRoutineResponse> response) {
                daysData = response.body();
                if (daysData == null)
                    return;

                myDays = new JSONArray();
                myDays.put("Day: " + daysData.get1().get(0).getDay());
                myDays.put("Day: " + daysData.get3().get(0).getDay());
                myDays.put("Day: " + daysData.get5().get(0).getDay());

                adapter = new RecycleViewAdapterForDaysPrograms(getActivity(), myDays);
                adapter.setClickListener(TrainingFragment.this);
                recyclerView.setAdapter(adapter);

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ProgramRoutineResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                Log.d("TrainingFragment", t.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        String day = "Day: 1";
        try {
            day = adapter.getItem(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        int dayName = Integer.parseInt(day.substring(day.indexOf(':') + 1).trim());

        Toast.makeText(getActivity(), "Day: " + dayName, Toast.LENGTH_SHORT).show();

        Intent intent;
        intent = new Intent(getActivity(), DayActivity.class);
        intent.putExtra("program_name", programData.getName());
        intent.putExtra("day_name", dayName);
        startActivity(intent);
    }
}