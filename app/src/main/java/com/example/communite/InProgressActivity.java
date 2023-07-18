package com.example.communite;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class InProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);

    }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            // Add any additional functionality if needed
    }
}