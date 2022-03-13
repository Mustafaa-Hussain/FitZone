package com.example.fitzone.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.handelers.HandelCommon;

import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForProgram;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

import pl.droidsonroids.gif.GifImageView;

public class DayActivity extends HandelCommon implements RecycleViewAdapterForProgram.ItemClickListener {

    RecycleViewAdapterForProgram adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        Intent intent;
        intent = getIntent();
        String dayName = intent.getStringExtra("day");

        TextView dayNameText = findViewById(R.id.day_name_text);
        dayNameText.setText(dayName);

        JSONObject myDay = new JSONObject();

        JSONArray myTrainings = new JSONArray();

        try {
            myDay.put("TName", "Push-ups");
            myDay.put("TReps", 15);
            myDay.put("TSets", 3);
            myTrainings.put(myDay);

            myDay = new JSONObject();
            myDay.put("TName", "Squat");
            myDay.put("TReps", 10);
            myDay.put("TSets", 3);
            myTrainings.put(myDay);

            myDay = new JSONObject();
            myDay.put("TName", "Push-ups");
            myDay.put("TReps", 10);
            myDay.put("TSets", 3);
            myTrainings.put(myDay);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(DayActivity.this));
        adapter = new RecycleViewAdapterForProgram(DayActivity.this, myTrainings);
        adapter.setClickListener(DayActivity.this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.start) {
            PopupWindow startTraining;

            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

            View popupView = inflater.inflate(R.layout.begining_trining_structure, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            startTraining = new PopupWindow(popupView, width, height, true);


            try{
                //get json object that hold training data
                JSONObject object = adapter.getItem(position);

                //put day gif
                //id   training_gif_file
                GifImageView gifImageView = popupView.findViewById(R.id.training_gif_file);

                //check and assign image to each training
                if(object.getString("TName").equals("Squat"))
                    gifImageView.setImageResource(R.drawable.dynamic_squat);
                else if(object.getString("TName").equals("Push-ups"))
                    gifImageView.setImageResource(R.drawable.dynamic_push_ups);
                else
                    gifImageView.setImageResource(R.drawable.dynamic_squat);


                //put the Day name
                String trainingName = object.getString("TName");
                ((TextView)popupView.findViewById(R.id.start_training_name)).setText(trainingName);

                String TReps = object.getString("TReps"),
                        TSets = object.getString("TSets");

                String noOfTrains = TReps + " X " + TSets;
                ((TextView)popupView.findViewById(R.id.no)).setText(noOfTrains);

                Button begin = popupView.findViewById(R.id.begin);
                begin.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //for test only
                        try {
                            Toast.makeText(DayActivity.this, "beginning " + object.getString("TName"), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(DayActivity.this, LiveCamera.class);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Button beginWithAss = popupView.findViewById(R.id.begin_without_assistant);
                beginWithAss.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        try {
                            Intent intent;
                            Toast.makeText(DayActivity.this, "beginning " + object.getString("TName"), Toast.LENGTH_SHORT).show();
                            intent = new Intent(DayActivity.this, TimerActivity.class);
                            intent.putExtra("TName", object.getString("TName"));
                            intent.putExtra("TNo", object.getString("TReps") + " X " + object.getString("TSets"));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
            catch (JSONException e){
                System.out.println(e.getMessage());
            }


            // show the popup window
            startTraining.showAtLocation(view, Gravity.CENTER, 0, 0);


            // dismiss the popup window when touched on any point in the screen
            popupView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    startTraining.dismiss();
                    return true;
                }
            });
        }
    }
}