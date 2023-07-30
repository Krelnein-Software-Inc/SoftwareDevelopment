package com.example.communite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private EditText userName, userProfName, userStatus, userAddress, userGender, userOrganization;
    private CircleImageView userProfImage;
    private DatabaseReference settingsUserRef;
    private String currentUserId;
    private StorageReference UserProfileImageRef;

    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ImageView backButton = findViewById(R.id.settings_back_button);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("Users")
                .child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance("gs://communite-f7efa.appspot.com").getReference().child("Profile Images");

        userName = findViewById(R.id.settings_profile_username);
        userProfName = findViewById(R.id.settings_profile_full_name);
        userStatus = findViewById(R.id.settings_user_status);
        userAddress = findViewById(R.id.settings_user_address);
        userGender = findViewById(R.id.settings_user_gender);
        userOrganization = findViewById(R.id.settings_user_organization);
        userProfImage = findViewById(R.id.settings_profile);

        settingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue(String.class);
                    String myUserName = dataSnapshot.child("username").getValue(String.class);
                    String myProfileName = dataSnapshot.child("fullname").getValue(String.class);
                    String myProfileStatus = dataSnapshot.child("status").getValue(String.class);
                    String myAddress = dataSnapshot.child("address").getValue(String.class);
                    String myGender = dataSnapshot.child("gender").getValue(String.class);
                    String myOrganization = dataSnapshot.child("organization").getValue(String.class);

                    if (!TextUtils.isEmpty(myProfileImage)) {
                        Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    }
                    userName.setText(myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userAddress.setText(myAddress);
                    userGender.setText(myGender);
                    userOrganization.setText(myOrganization);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, "Failed to retrieve data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button updateAccountSettingsButton = findViewById(R.id.update_account_button);
        updateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAccountInfo();
            }
        });

        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");

                UploadTask uploadTask = filePath.putFile(resultUri);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    String downloadUrl = downloadUri.toString();

                                    settingsUserRef.child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SettingsActivity.this, "Profile image updated successfully", Toast.LENGTH_SHORT);
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SettingsActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Error Occurred: Image can't be cropped. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validateAccountInfo() {
        String username = userName.getText().toString().trim();
        String profilename = userProfName.getText().toString().trim();
        String status = userStatus.getText().toString().trim();
        String address = userAddress.getText().toString().trim();
        String gender = userGender.getText().toString().trim();
        String organization = userOrganization.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(profilename)) {
            Toast.makeText(this, "Please write your Full Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(status)) {
            Toast.makeText(this, "Please write your Bio", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please write your Address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Please write your Gender", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(organization)) {
            Toast.makeText(this, "Please write your Organization", Toast.LENGTH_SHORT).show();
        } else {
            updateAccountInfo(username, profilename, status, address, gender, organization);
        }
    }

    private void updateAccountInfo(String username, String profilename, String status, String address, String gender, String organization) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("fullname", profilename);
        userMap.put("status", status);
        userMap.put("address", address);
        userMap.put("gender", gender);
        userMap.put("organization", organization);

        settingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "Account Settings updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Error Occurred while updating account information", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Handle the back button press in the action bar (toolbar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
