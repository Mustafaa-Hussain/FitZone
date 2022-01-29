package com.example.fitzone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;

public class Training extends HandelCommon implements RecycleViewAdapterForDaysPrograms.ItemClickListener {

    RecycleViewAdapterForDaysPrograms adapter;
    RecyclerView recyclerView;

    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        //token not used now
        SharedPreferences apiTokenFile = getSharedPreferences("UserData", Comments.MODE_PRIVATE);
        String apiToken = apiTokenFile.getString("apiToken", null);

        JSONArray myDays = new JSONArray();
        myDays.put("Saturday");
        myDays.put("Sunday");
        myDays.put("Monday");
        myDays.put("Tuesday");
        myDays.put("Wednesday");

        recyclerView = findViewById(R.id.recycleViewOfPrograms);
        recyclerView.setLayoutManager(new LinearLayoutManager(Training.this));
        adapter = new RecycleViewAdapterForDaysPrograms(Training.this, myDays);
        adapter.setClickListener(Training.this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        RecyclerView.ViewHolder rv_view = recyclerView.findViewHolderForAdapterPosition(position);
        ImageView imv = rv_view.itemView.findViewById(R.id.dayStatus);

        imv.setImageResource(R.drawable.checked_box);

//        imv.setImageResource(R.drawable.unchecked_box);
    }
}