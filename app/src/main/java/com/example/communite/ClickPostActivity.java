package com.example.communite;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView PostDescription;
    private Button DeletePostButton, EditPostButton;
    private DatabaseReference ClickPostRef;
    private String PostKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        PostKey = getIntent().getStringExtra("PostKey");
        ClickPostRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Posts").child(PostKey);

        PostImage = findViewById(R.id.click_post_image);
        PostDescription = findViewById(R.id.click_post_description);
        DeletePostButton = findViewById(R.id.delete_post_button);
        EditPostButton = findViewById(R.id.click_edit_post_button);

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String image = dataSnapshot.child("postimage").getValue(String.class);

                    PostDescription.setText(description);
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
    }
}
