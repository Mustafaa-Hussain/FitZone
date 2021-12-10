package com.example.fitzone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SetServerIP extends AppCompatActivity {

    private String ip;
    private String port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_server_ip);
    }

    public void setIpaAndPort(View view) {

        ip = ((EditText)findViewById(R.id.serverIp)).getText().toString();
        port = ((EditText)findViewById(R.id.serverPort)).getText().toString();

        //store ip in shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences("ipAndPort", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ip", ip);
        editor.putString("port", port);
        editor.commit();

        Intent intent = new Intent(SetServerIP.this, LoginActivity.class);
        startActivity(intent);
    }
}