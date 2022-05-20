package com.example.fitzone.activites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.handelers.HandlePost;
import com.example.fitzone.handelers.HandleRequests;
import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForDaysPrograms;
import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForPosts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrainingFragment extends Fragment implements RecycleViewAdapterForDaysPrograms.ItemClickListener {

    RecycleViewAdapterForDaysPrograms adapter;
    RecyclerView recyclerView;

    public TrainingFragment() {
        super(R.layout.fragment_training);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //token not used now
        SharedPreferences apiTokenFile = getActivity().getSharedPreferences("UserData", Comments.MODE_PRIVATE);
        String apiToken = apiTokenFile.getString("apiToken", null);

        JSONArray myDays = new JSONArray();
        myDays.put("Day-1 Sat");
        myDays.put("Day-2 Sun");
        myDays.put("Day-3 Mon");
        myDays.put("Day-4 Tues");
        myDays.put("Day-5 Wed");

        recyclerView = view.findViewById(R.id.recycleViewOfPrograms);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new RecycleViewAdapterForDaysPrograms(getActivity(), myDays);
        adapter.setClickListener(TrainingFragment.this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        RecyclerView.ViewHolder rv_view = recyclerView.findViewHolderForAdapterPosition(position);
        ImageView imv = rv_view.itemView.findViewById(R.id.dayStatus);

        imv.setImageResource(R.drawable.checked_box);

        String day = ((TextView)rv_view.itemView.findViewById(R.id.dayText)).getText().toString();

        Intent intent;
        intent = new Intent(getActivity(), DayActivity.class);
        intent.putExtra("day", day);
        startActivity(intent);

//        imv.setImageResource(R.drawable.unchecked_box);
    }
}