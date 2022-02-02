package com.example.fitzone.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitzone.handelers.HandleRequests;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.callback.Callback;

import pl.droidsonroids.gif.GifImageView;

public class TimerActivity extends AppCompatActivity {

    TextView timerTextView;
    long startTime = 0;
    int seconds, minutes;

    String trainingNameString;

    Button yes, no;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            seconds = (int) (millis / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Intent intent = getIntent();
        intent = getIntent();
        trainingNameString = intent.getStringExtra("TName");

        TextView trainingName = findViewById(R.id.training_name);
        trainingName.setText(trainingNameString);

        GifImageView gifImageView = findViewById(R.id.training_gif_file);

        //check and assign image to each training
        if(trainingNameString.equals("Squat"))
            gifImageView.setImageResource(R.drawable.dynamic_squat);
        else if(trainingNameString.equals("Push-ups"))
            gifImageView.setImageResource(R.drawable.dynamic_push_ups);
        else
            gifImageView.setImageResource(R.drawable.dynamic_squat);


        timerTextView = (TextView) findViewById(R.id.timer);

        Button b = (Button) findViewById(R.id.start);
        b.setText("start");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("resit")) {
                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("start");
                } else {
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    b.setText("resit");
                }
            }
        });

        Button finish = findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!timerTextView.getText().equals("Timer")){

                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("start");

                    String result = "finished in "+ minutes + " minutes, and " + seconds + " seconds.";
                    Toast.makeText(TimerActivity.this, result, Toast.LENGTH_SHORT).show();//////////

                    String caption, content;
                    caption = trainingNameString;
                    content = result;

                    //popup window to ask user if want to share his time
                    askToShareOrNot(caption, content, v);

                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        Button b = (Button)findViewById(R.id.start);
        b.setText("start");
    }


    public void askToShareOrNot(String caption, String content, View view){

        PopupWindow askPopup;

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.ask_if_yes_or_no, null);///

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        askPopup = new PopupWindow(popupView, width, height, true);


        //put the message
        ((TextView)popupView.findViewById(R.id.messageQ)).setText("You want to share this result with yor friends: " + content + " ?");


        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        askPopup.showAtLocation(view, Gravity.CENTER, 0, 0);

        yes = popupView.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String apiToken = getSharedPreferences("UserData", MODE_PRIVATE).getString("apiToken", null);
                HandleRequests handleRequests = new HandleRequests(TimerActivity.this);

                handleRequests.addPost(caption, content,apiToken,
                    new HandleRequests.VolleyResponseListener() {
                        @Override
                        public void onResponse(boolean status, JSONObject jsonObject) {
                            if (status) {
                                Intent intent;
                                intent = new Intent(TimerActivity.this, DayActivity.class);
                                intent.putExtra("day", caption);
                                startActivity(intent);

                                askPopup.dismiss();
                            }
                            else {
                                Snackbar.make(view, "something wrong with your internet connection", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }
                    });
            }
        });

        no = popupView.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(TimerActivity.this, DayActivity.class);
                intent.putExtra("day", caption);
                startActivity(intent);
                askPopup.dismiss();
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //askPopup.dismiss();
                return true;
            }
        });
    }

}