package com.example.fitzone.activites;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForProgram;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifImageView;

public class DayActivity extends AppCompatActivity implements RecycleViewAdapterForProgram.ItemClickListener {

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
            myDay.put("TName", getString(R.string.push_ups));
            myDay.put("TReps", 2);
            myDay.put("TSets", 2);
            myTrainings.put(myDay);

            myDay = new JSONObject();
            myDay.put("TName", getString(R.string.squat));
            myDay.put("TReps", 2);
            myDay.put("TSets", 2);
            myTrainings.put(myDay);

            myDay = new JSONObject();
            myDay.put("TName", getString(R.string.push_ups));
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


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.start) {
            PopupWindow startTraining;

            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            View popupView = inflater.inflate(R.layout.begining_trining_structure, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            startTraining = new PopupWindow(popupView, width, height, focusable);


            try {
                //get json object that hold training data
                JSONObject object = adapter.getItem(position);

                //put day gif
                //id   training_gif_file
                GifImageView gifImageView = popupView.findViewById(R.id.training_gif_file);

                //check and assign image to each training
                if (object.getString("TName").equals(getString(R.string.squat)))
                    gifImageView.setImageResource(R.drawable.dynamic_squat);
                else if (object.getString("TName").equals(getString(R.string.push_ups)))
                    gifImageView.setImageResource(R.drawable.dynamic_push_ups);
                else
                    gifImageView.setImageResource(R.drawable.dynamic_squat);


                //put the Day name
                String trainingName = object.getString("TName");
                ((TextView) popupView.findViewById(R.id.start_training_name)).setText(trainingName);

                String TReps = object.getString("TReps"),
                        TSets = object.getString("TSets");

                String noOfTrains = TReps + " X " + TSets;
                ((TextView) popupView.findViewById(R.id.no)).setText(noOfTrains);

                Button begin = popupView.findViewById(R.id.begin);
                begin.setOnClickListener(
                        v -> {
                            try {
                                Intent intent;
                                intent = new Intent(getApplicationContext(), TimerActivity.class);
                                intent.putExtra("TName", object.getString("TName"));
                                intent.putExtra("TReps", object.getInt("TReps"));
                                intent.putExtra("TSets", object.getInt("TSets"));
                                intent.putExtra("setNumber", 1);

                                checkPermission(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }


            // show the popup window
            startTraining.showAtLocation(view, Gravity.CENTER, 0, 0);

            // dismiss the popup window when touched on any point in the screen
            popupView.setOnTouchListener(
                    (v, event) -> {
                        startTraining.dismiss();
                        return true;
                    });
        }
    }


    private void checkPermission(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            } else if (this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
                Toast.makeText(getApplicationContext(), "Camera Permission was granted", Toast.LENGTH_SHORT).show();

                Intent testIntent;
                testIntent = new Intent(getApplicationContext(), LivePreviewActivity.class);
                startActivity(testIntent);
            } else {
                Toast.makeText(getApplicationContext(), "Camera Permission was denied\nthis function cannot work\nPlease enable camera permission in settings", Toast.LENGTH_LONG).show();
            }
        }
    }

}