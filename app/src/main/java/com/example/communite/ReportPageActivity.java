package com.example.communite;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ReportPageActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_progress);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottom_pending) {
                    startActivity(new Intent(ReportPageActivity.this, ReportPageActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.bottom_progress) {
                    startActivity(new Intent(ReportPageActivity.this, InProgressActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.bottom_done) {
                    startActivity(new Intent(ReportPageActivity.this, DoneActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() != R.id.bottom_pending) {
            startActivity(new Intent(ReportPageActivity.this, ReportPageActivity.class));
        } else {
            super.onBackPressed();
        }
    }

    public void onBackButtonClick(View view) {
        onBackPressed();
    }
}
