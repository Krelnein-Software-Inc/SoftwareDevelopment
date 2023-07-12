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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private EditText userName, userProfName, userStatus, userAddress, userGender, userOrganization;
    private Button updateAccountSettingsButton;
    private CircleImageView userProfImage;
    private DatabaseReference settingsUserRef;
    private String currentUserId;
    private TextView btnReset, btnBack;
    private EditText edtEmail;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String strEmail;
    private TextView Change;

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

        updateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
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

    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String profilename = userProfName.getText().toString();
        String status = userStatus.getText().toString();
        String address = userAddress.getText().toString();
        String gender = userGender.getText().toString();
        String organization = userOrganization.getText().toString();

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(profilename)){
            Toast.makeText(this, "Please write your Full Name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(status)){
            Toast.makeText(this, "Please write your Bio", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(address)){
            Toast.makeText(this, "Please write your Address", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(gender)){
            Toast.makeText(this, "Please write your Gender", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(organization)){
            Toast.makeText(this, "Please write your Organization", Toast.LENGTH_SHORT).show();
        }
        else{
            UpdateAccountInfo(username, profilename, status, address, gender, organization);
        }
    }


    private void UpdateAccountInfo(String username, String profilename, String status, String address, String gender, String organization) {
        HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", profilename);
            userMap.put("status", status);
            userMap.put("address", address);
            userMap.put("gender", gender);
            userMap.put("organization", organization);

        settingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    SendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Account Settings updated successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SettingsActivity.this, "Error Occurred while updating account information", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}