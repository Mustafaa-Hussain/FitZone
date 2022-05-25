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
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitzone.activites.R;
import com.example.fitzone.activites.fragments.home_activity_fragments.HomeFragment;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.send_post_response.SendPostResponse;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddNewTextPost extends Fragment {
    private Button share, cancel;
    private TextView caption, content;
    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private SwipeRefreshLayout swipeRefreshLayout;

    public AddNewTextPost() {
        super(R.layout.fragment_add_new_text_post);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //inflate elements
        caption = view.findViewById(R.id.caption);
        content = view.findViewById(R.id.content);
        share = view.findViewById(R.id.sharePost);
        cancel = view.findViewById(R.id.cancelPopUp);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_Add_new_post);

        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

        share.setOnClickListener(this::sharePost);

        cancel.setOnClickListener(v1 -> {
            Snackbar.make(view, "cancel post", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        });

    }

    private void sharePost(View view) {
        if (!caption.getText().equals("") && !content.getText().equals("")) {
            if (apiInterface == null)
                return;

            Call<SendPostResponse> call = apiInterface.sendTextPost("Bearer " + getApiToken(getActivity()),
                    caption.getText().toString(),
                    content.getText().toString(),
                    0);

            swipeRefreshLayout.setRefreshing(true);

            call.enqueue(new Callback<SendPostResponse>() {
                @Override
                public void onResponse(Call<SendPostResponse> call, Response<SendPostResponse> response) {
                    Toast.makeText(getActivity(), "added successfully", Toast.LENGTH_SHORT).show();

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fl_home_activity, new HomeFragment())
                            .commit();

                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<SendPostResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            Snackbar.make(view, "must fill all fields", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }
}