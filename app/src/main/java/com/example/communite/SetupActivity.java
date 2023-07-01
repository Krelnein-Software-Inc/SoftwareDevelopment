package com.example.communite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText UserName, FullName, Address;
    private Button SaveInformationButton;
    private CircleImageView ProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;

    private ProgressDialog loadingBar;

    String currentUserID;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Users");
        UserProfileImageRef = FirebaseStorage.getInstance("gs://communite-f7efa.appspot.com").getReference().child("Profile Images");

        UserName = findViewById(R.id.setup_username);
        FullName = findViewById(R.id.setup_full_name);
        Address = findViewById(R.id.setup_address_name);
        SaveInformationButton = findViewById(R.id.setup_information_button);
        ProfileImage = findViewById(R.id.setup_profile_image);
        loadingBar = new ProgressDialog(this);

        SaveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetUpInformation();
            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(currentUserID).hasChild("profileimage")) {
                        String image = dataSnapshot.child(currentUserID).child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(ProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Image loaded successfully
                            }

                            @Override
                            public void onError(Exception e) {
                                // Error loading image
                                Toast.makeText(SetupActivity.this, "Failed to load profile image.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the onCancelled event if needed
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait while we are updating your profile image");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                UploadTask uploadTask = filePath.putFile(resultUri);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    String downloadUrl = downloadUri.toString();

                                    UsersRef.child(currentUserID).child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SetupActivity.this, "Profile Image stored successfully", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SetupActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });
                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Error Occurred: Image can't be cropped. Please try again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void SaveAccountSetUpInformation() {
        String username = UserName.getText().toString();
        String fullname = FullName.getText().toString();
        String address = Address.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter your Username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "Please enter your Full Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please enter your Address", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait while we are saving your information");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("address", address);
            userMap.put("status", "Student Developer");
            userMap.put("gender", "none");
            userMap.put("organization", "none");

            UsersRef.child(currentUserID).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SetupActivity.this, "Your Account is created Successfully", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                        SendUserToMainActivity();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error Occurred: " + message, Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
