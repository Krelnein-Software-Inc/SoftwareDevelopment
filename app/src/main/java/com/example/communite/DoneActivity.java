package com.example.communite;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class DoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Add any additional functionality if needed
    }
}
