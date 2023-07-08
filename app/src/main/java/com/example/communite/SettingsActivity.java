package com.example.communite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private EditText userName, userProfName, userStatus, userAddress, userGender, userOrganization;
    private Button updateAccountSettingsButton;
    private CircleImageView userProfImage;
    private DatabaseReference settingsUserRef;
    private String currentUserId;

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

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("Users")
                .child(currentUserId);

        userName = findViewById(R.id.settings_profile_username);
        userProfName = findViewById(R.id.settings_profile_full_name);
        userStatus = findViewById(R.id.settings_user_status);
        userAddress = findViewById(R.id.settings_user_address);
        userGender = findViewById(R.id.settings_user_gender);
        userOrganization = findViewById(R.id.settings_user_organization);
        userProfImage = findViewById(R.id.settings_profile);
        updateAccountSettingsButton = findViewById(R.id.update_account_button);
        Change = findViewById(R.id.Settings_text);
        btnBack = findViewById(R.id.Back_button);
        btnReset = findViewById(R.id.Reset_button);
        edtEmail = findViewById(R.id.login_email);
        progressBar = findViewById(R.id.Progressbar2);

        settingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("SettingsActivity", "onDataChange triggered");
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileimage")) {
                        String myProfileImage = dataSnapshot.child("profileimage").getValue(String.class);
                        if (TextUtils.isEmpty(myProfileImage)) {
                            Log.d("SettingsActivity", "Profile image is empty or null");
                        } else {
                            Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                        }
                    }

                    if (dataSnapshot.hasChild("username")) {
                        String myUserName = dataSnapshot.child("username").getValue(String.class);
                        if (TextUtils.isEmpty(myUserName)) {
                            Log.d("SettingsActivity", "Username is empty or null");
                        } else {
                            userName.setText(myUserName);
                        }
                    }

                    if (dataSnapshot.hasChild("fullname")) {
                        String myProfileName = dataSnapshot.child("fullname").getValue(String.class);
                        if (TextUtils.isEmpty(myProfileName)) {
                            Log.d("SettingsActivity", "Profile name is empty or null");
                        } else {
                            userProfName.setText(myProfileName);
                        }
                    }

                    if (dataSnapshot.hasChild("status")) {
                        String myProfileStatus = dataSnapshot.child("status").getValue(String.class);
                        if (TextUtils.isEmpty(myProfileStatus)) {
                            Log.d("SettingsActivity", "Profile status is empty or null");
                        } else {
                            userStatus.setText(myProfileStatus);
                        }
                    }

                    if (dataSnapshot.hasChild("address")) {
                        String myAddress = dataSnapshot.child("address").getValue(String.class);
                        if (TextUtils.isEmpty(myAddress)) {
                            Log.d("SettingsActivity", "Address is empty or null");
                        } else {
                            userAddress.setText(myAddress);
                        }
                    }

                    if (dataSnapshot.hasChild("gender")) {
                        String myGender = dataSnapshot.child("gender").getValue(String.class);
                        if (TextUtils.isEmpty(myGender)) {
                            Log.d("SettingsActivity", "Gender is empty or null");
                        } else {
                            userGender.setText(myGender);
                        }
                    }

                    if (dataSnapshot.hasChild("organization")) {
                        String myOrganization = dataSnapshot.child("organization").getValue(String.class);
                        if (TextUtils.isEmpty(myOrganization)) {
                            Log.d("SettingsActivity", "Organization is empty or null");
                        } else {
                            userOrganization.setText(myOrganization);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, "Failed to retrieve data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        updateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle update account settings button click
                // ...
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
                        Toast.makeText(SettingsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        btnReset.setVisibility(View.VISIBLE);
                    }
                });
    }
}