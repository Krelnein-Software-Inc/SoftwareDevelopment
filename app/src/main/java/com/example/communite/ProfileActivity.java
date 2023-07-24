package com.example.communite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userProfName, userStatus, userOrganization;
    private CircleImageView userProfileImage;
    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private ImageView btnBack;

    private RecyclerView postsRecyclerView;
    private ProfilePostAdapter profilePostAdapter;
    private List<Posts> postList;

    private DatabaseReference userPostsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("Users")
                .child(currentUserId);
        userPostsRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("Posts");

        userProfName = findViewById(R.id.my_profile_full_name);
        userStatus = findViewById(R.id.my_status);
        userOrganization = findViewById(R.id.my_organization);
        userProfileImage = findViewById(R.id.my_profile_pic);
        btnBack = findViewById(R.id.back_button);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to MainActivity
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity to prevent going back to it when back button is pressed
            }
        });

        postsRecyclerView = findViewById(R.id.profile_posts_recycler_view);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        profilePostAdapter = new ProfilePostAdapter(postList);
        postsRecyclerView.setAdapter(profilePostAdapter);

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileimage")) {
                        String myProfileImage = dataSnapshot.child("profileimage").getValue(String.class);
                        if (!TextUtils.isEmpty(myProfileImage)) {
                            Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
                        }
                    }

                    if (dataSnapshot.hasChild("fullname")) {
                        String myProfileName = dataSnapshot.child("fullname").getValue(String.class);
                        if (!TextUtils.isEmpty(myProfileName)) {
                            userProfName.setText(myProfileName);
                        }
                    }

                    if (dataSnapshot.hasChild("status")) {
                        String myProfileStatus = dataSnapshot.child("status").getValue(String.class);
                        if (!TextUtils.isEmpty(myProfileStatus)) {
                            userStatus.setText(myProfileStatus);
                        }
                    }

                    if (dataSnapshot.hasChild("organization")) {
                        String myOrganization = dataSnapshot.child("organization").getValue(String.class);
                        if (!TextUtils.isEmpty(myOrganization)) {
                            userOrganization.setText(myOrganization);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to retrieve data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        userPostsRef.orderByChild("uid").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Posts post = snapshot.getValue(Posts.class);
                    postList.add(post);
                }
                profilePostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to retrieve posts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.PostViewHolder> {

        private List<Posts> postList;

        public ProfilePostAdapter(List<Posts> postList) {
            this.postList = postList;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_posts_layout, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            Posts post = postList.get(position);

            // Bind post data to the ViewHolder views
            holder.setProfileImage(post.getProfileimage());
            holder.setProfileName(post.getFullname());
            holder.setPostDescription(post.getDescription());
            holder.setPostImage(post.getPostimage());
            holder.setPostDate(post.getDate());
            holder.setPostTime(post.getTime());
        }

        @Override
        public int getItemCount() {
            return postList.size();
        }

        public class PostViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView profileImage;
            private TextView profileName, postDescription, postDate, postTime;
            private ImageView postImage;

            public PostViewHolder(@NonNull View itemView) {
                super(itemView);

                // Initialize ViewHolder components
                profileImage = itemView.findViewById(R.id.post_profile_image);
                profileName = itemView.findViewById(R.id.user_profile_name);
                postDescription = itemView.findViewById(R.id.user_profile_description);
                postDate = itemView.findViewById(R.id.user_profile_date);
                postTime = itemView.findViewById(R.id.user_profile_time);
                postImage = itemView.findViewById(R.id.user_profile_image);
            }

            public void setProfileImage(String imageUrl) {
                // Use Picasso or any other image loading library to load the profile image into the CircleImageView
                if (!TextUtils.isEmpty(imageUrl)) {
                    Picasso.get().load(imageUrl).into(profileImage);
                } else {
                    // Set a default placeholder image if the profile image is empty
                    profileImage.setImageResource(R.drawable.profile);
                }
            }

            public void setProfileName(String name) {
                profileName.setText(name);
            }

            public void setPostDescription(String description) {
                postDescription.setText(description);
            }

            public void setPostImage(String imageUrl) {
                // Use Picasso or any other image loading library to load the post image into the ImageView
                if (!TextUtils.isEmpty(imageUrl)) {
                    Picasso.get().load(imageUrl).into(postImage);
                } else {
                    // Set a default placeholder image if the post image is empty
                    postImage.setImageResource(R.drawable.profile);
                }
            }

            public void setPostDate(String date) {
                postDate.setText(date);
            }

            public void setPostTime(String time) {
                postTime.setText(time);
            }
        }
    }
}
