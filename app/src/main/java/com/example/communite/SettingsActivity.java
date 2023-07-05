package com.example.communite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    //Declaration
    TextView btnReset, btnBack;
    EditText edtEmail;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String strEmail;
    TextView Change;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Initializaton
        Change = findViewById(R.id.Settings_text);
        btnBack = findViewById(R.id.Back_button);
        btnReset = findViewById(R.id.Reset_button);
        edtEmail = findViewById(R.id.login_email);
        progressBar = findViewById(R.id.Progressbar2);

        mAuth = FirebaseAuth.getInstance();

        //Reset Button Listener
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = edtEmail.getText().toString().trim();
                if (!TextUtils.isEmpty(strEmail)) {
                    ResetPassword();
                } else {
                    edtEmail.setError("Email field can't be empty");
                }
            }
        });


        //Back Button Code
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void ResetPassword() {
        progressBar.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(strEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SettingsActivity.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        btnReset.setVisibility(View.VISIBLE);
                    }
                });
    }
}