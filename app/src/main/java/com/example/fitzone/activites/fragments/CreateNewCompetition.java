package com.example.fitzone.activites.fragments;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitzone.activites.R;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.challenges_response.Challenge;
import com.example.fitzone.retrofit_requists.data_models.crate_chalenge.CreateChallengeResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateNewCompetition extends Fragment {
    private RadioButton pushUp, squat;
    private TextView numberOfTrains;
    private Button start;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Retrofit retrofit;
    private ApiInterface apiInterface;

    public CreateNewCompetition() {
        super(R.layout.fragment_create_new_competition);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inflate elements
        pushUp = view.findViewById(R.id.push_pu_rb);
        squat = view.findViewById(R.id.squat_rb);
        numberOfTrains = view.findViewById(R.id.training_number);
        start = view.findViewById(R.id.start_competition);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_create_new_competition);

        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));

        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

        start.setOnClickListener(this::startCompetition);
    }

    private void startCompetition(View view) {
        if (!numberOfTrains.getText().toString().isEmpty()) {
            if (pushUp.isChecked()) {
                sendCompetition("push up", Integer.parseInt(numberOfTrains.getText().toString()));
                Toast.makeText(getActivity(), "push up", Toast.LENGTH_SHORT).show();
            }
            else if (squat.isChecked()) {
                sendCompetition("squat", Integer.parseInt(numberOfTrains.getText().toString()));
                Toast.makeText(getActivity(), "squat", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(getActivity(), "you should enter training number", Toast.LENGTH_SHORT).show();
    }

    private void sendCompetition(String trainingName, int trainingReps) {
        if (apiInterface == null)
            return;

        Call<CreateChallengeResponse> call = apiInterface.createChallenge(
                "Bearer " + getApiToken(getActivity()),
                trainingName,
                trainingReps);
        swipeRefreshLayout.setRefreshing(true);
        call.enqueue(new Callback<CreateChallengeResponse>() {
            @Override
            public void onResponse(Call<CreateChallengeResponse> call, Response<CreateChallengeResponse> response) {
                if (response.body() == null)
                    return;

                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_home_activity, new WaitingFragment(
                                response.body().getId(), trainingName, trainingReps))
                        .commit();

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<CreateChallengeResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}