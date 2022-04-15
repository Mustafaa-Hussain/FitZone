package com.example.fitzone.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.handelers.HandelCommon;

import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForDaysPrograms;

import org.json.JSONArray;

public class Training extends HandelCommon implements RecycleViewAdapterForDaysPrograms.ItemClickListener {

    RecycleViewAdapterForDaysPrograms adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        //token not used now
        SharedPreferences apiTokenFile = getSharedPreferences("UserData", Comments.MODE_PRIVATE);
        String apiToken = apiTokenFile.getString("apiToken", null);

        JSONArray myDays = new JSONArray();
        myDays.put("Day-1 Sat");
        myDays.put("Day-2 Sun");
        myDays.put("Day-3 Mon");
        myDays.put("Day-4 Tues");
        myDays.put("Day-5 Wed");

        recyclerView = findViewById(R.id.recycleViewOfPrograms);
        recyclerView.setLayoutManager(new GridLayoutManager(Training.this, 2));
        adapter = new RecycleViewAdapterForDaysPrograms(Training.this, myDays);
        adapter.setClickListener(Training.this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        RecyclerView.ViewHolder rv_view = recyclerView.findViewHolderForAdapterPosition(position);
        ImageView imv = rv_view.itemView.findViewById(R.id.dayStatus);

        imv.setImageResource(R.drawable.checked_box);

        String day = ((TextView)rv_view.itemView.findViewById(R.id.dayText)).getText().toString();

        Intent intent;
        intent = new Intent(Training.this, DayActivity.class);
        intent.putExtra("day", day);
        startActivity(intent);

//        imv.setImageResource(R.drawable.unchecked_box);
    }
}