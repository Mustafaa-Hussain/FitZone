package com.example.fitzone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class SetServerIP extends AppCompatActivity {

    private EditText ip, port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_server_ip);

        ip = findViewById(R.id.serverIp);
        port = findViewById(R.id.serverPort);

        SharedPreferences sharedPreferences = getSharedPreferences("ipAndPort", MODE_PRIVATE);
        ip.setText(sharedPreferences.getString("ip", null));
        port.setText(sharedPreferences.getString("port", null));
    }

    public void setIpaAndPort(View view) {
        //store ip in shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences("ipAndPort", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ip", ip.getText().toString());
        editor.putString("port", port.getText().toString());
        editor.commit();

        Intent intent = new Intent(SetServerIP.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}