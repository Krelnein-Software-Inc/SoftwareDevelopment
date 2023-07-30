package com.example.communite;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ClickReportActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView PostDescription;
    private Button DeletePostButton, EditPostButton;
    private DatabaseReference ClickPostRef;
    private FirebaseAuth mAuth;

    private String PostKey, currentUserID, databaseUserID, description, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_report);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getStringExtra("PostKey");
        ClickPostRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Reports").child(PostKey);

        PostImage = findViewById(R.id.report_post_image);
        PostDescription = findViewById(R.id.report_post_description);
        DeletePostButton = findViewById(R.id.report_delete_post_button);
        EditPostButton = findViewById(R.id.report_edit_post_button);

        DeletePostButton.setVisibility(View.INVISIBLE);
        EditPostButton.setVisibility(View.INVISIBLE);

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    description = dataSnapshot.child("description").getValue(String.class);
                    image = dataSnapshot.child("postimage").getValue(String.class);
                    databaseUserID = dataSnapshot.child("uid").getValue().toString();

                    PostDescription.setText(description);
                    if (currentUserID.equals(databaseUserID)) {
                        DeletePostButton.setVisibility(View.VISIBLE);
                        EditPostButton.setVisibility(View.VISIBLE);
                    }
                    EditPostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditCurrentPost(description);
                        }
                    });
                    Picasso.get().load(image).into(PostImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded successfully
                        }

                        @Override
                        public void onError(Exception e) {
                            // Error occurred while loading the image
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });

        DeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteCurrentPost();
            }
        });

        // Initialize the ImageView for the back button
        ImageView backButton = findViewById(R.id.report_update_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Go back when the back button is clicked
            }
        });
    }

    private void EditCurrentPost(String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickReportActivity.this);
        builder.setTitle("Edit Post:");

        final EditText inputField = new EditText(ClickReportActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newDescription = inputField.getText().toString().trim();
                if (!newDescription.isEmpty()) {
                    ClickPostRef.child("description").setValue(newDescription);
                    Toast.makeText(ClickReportActivity.this, "Report updated successfully.", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(ClickReportActivity.this, "Please enter a valid description.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void DeleteCurrentPost() {
        ClickPostRef.removeValue();
        SendUserToReportPageActivity();
        Toast.makeText(this, "Report has been deleted.", Toast.LENGTH_SHORT);
    }

    private void SendUserToReportPageActivity() {
        Intent reportPageIntent = new Intent(ClickReportActivity.this, ReportPageActivity.class);
        reportPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(reportPageIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Redirect the user to the ReportPageActivity when the back button is pressed
        Intent reportPageIntent = new Intent(ClickReportActivity.this, ReportPageActivity.class);
        startActivity(reportPageIntent);
        finish();
    }
}
