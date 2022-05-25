package com.example.fitzone.activites.fragments.home_activity_fragments;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fitzone.activites.LivePreviewActivity;
import com.example.fitzone.activites.R;
import com.example.fitzone.activites.fragments.CreateNewCompetition;
import com.example.fitzone.recycleViewAdapters.CompetitionsAdapter;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.accept_challenge.AcceptChalengeResponse;
import com.example.fitzone.retrofit_requists.data_models.challenges_response.AllChallengesResponse;
import com.example.fitzone.retrofit_requists.data_models.challenges_response.Challenge;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompetitionFragment extends Fragment implements CompetitionsAdapter.ItemClickListener {
    private Button createCompetition;
    private RecyclerView recyclerView;
    private CompetitionsAdapter adapter;
    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Challenge> challenges;

    public CompetitionFragment() {
        super(R.layout.fragment_competition);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inflate elements
        createCompetition = view.findViewById(R.id.create_competition);
        recyclerView = view.findViewById(R.id.competition_frag_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        createCompetition.setOnClickListener(this::goToCreateNewChallenge);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

        getCompetitions();

        swipeRefreshLayout.setOnRefreshListener(this::getCompetitions);
    }

    private void goToCreateNewChallenge(View view) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_home_activity, new CreateNewCompetition())
                .addToBackStack("CompetitionFragment")
                .commit();
    }

    private void getCompetitions() {
        if (apiInterface == null)
            return;

        Call<AllChallengesResponse> call = apiInterface.getChallenges("Bearer " + getApiToken(getActivity()));
        swipeRefreshLayout.setRefreshing(true);
        call.enqueue(new Callback<AllChallengesResponse>() {
            @Override
            public void onResponse(Call<AllChallengesResponse> call, Response<AllChallengesResponse> response) {
                if (response.body() == null)
                    return;

                challenges = response.body().getChallenges();

                adapter = new CompetitionsAdapter(getActivity(), response.body().getChallenges());

                adapter.setClickListener(CompetitionFragment.this);
                recyclerView.setAdapter(adapter);

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<AllChallengesResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        if (challenges == null)
            return;

        if (apiInterface == null)
            return;

        Call<AcceptChalengeResponse> call = apiInterface.acceptChallenge(
                "Bearer " + getApiToken(getActivity()),
                challenges.get(position).getId());
        call.enqueue(new Callback<AcceptChalengeResponse>() {
            @Override
            public void onResponse(Call<AcceptChalengeResponse> call, Response<AcceptChalengeResponse> response) {
                if (response.body() == null)
                    return;
                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                goToLivePreviewActivity(challenges.get(position).getExercise_name(),
                        challenges.get(position).getReps(),
                        challenges.get(position).getId());
            }

            @Override
            public void onFailure(Call<AcceptChalengeResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToLivePreviewActivity(String trainingName, int trainingReps, int trainingId) {
        Intent intent = new Intent(getActivity(), LivePreviewActivity.class);
        intent.putExtra("TName", trainingName);
        intent.putExtra("TReps", trainingReps);
        intent.putExtra("challengeId", trainingId);
        intent.putExtra("TSets", 1);
        intent.putExtra("setNumber", 1);
        intent.putExtra("lastTime", 0);

        checkPermission(intent);
    }

    private void checkPermission(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            } else if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            }
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Camera Permission was granted", Toast.LENGTH_SHORT).show();

                Intent testIntent;
                testIntent = new Intent(getActivity(), LivePreviewActivity.class);
                startActivity(testIntent);
            } else {
                Toast.makeText(getActivity(), "Camera Permission was denied\nthis function cannot work\nPlease enable camera permission in settings", Toast.LENGTH_LONG).show();
            }
        }
    }


}