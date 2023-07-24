package com.example.communite;

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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportPageActivity extends AppCompatActivity {

    private DatabaseReference userPostsRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private RecyclerView postsRecyclerView;
    private ReportPostAdapter reportPostAdapter;
    private List<Reports> reportsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userPostsRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("Reports");

        postsRecyclerView = findViewById(R.id.all_users_report_list);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportsList = new ArrayList<>();
        reportPostAdapter = new ReportPostAdapter(reportsList);
        postsRecyclerView.setAdapter(reportPostAdapter);

        userPostsRef.orderByChild("uid").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reportsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Reports reports = snapshot.getValue(Reports.class);
                    reportsList.add(reports);
                }
                reportPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReportPageActivity.this, "Failed to retrieve reports: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ReportPostAdapter extends RecyclerView.Adapter<ReportPostAdapter.PostViewHolder> {

        private List<Reports> reportsList;

        public ReportPostAdapter(List<Reports> reportsList) {
            this.reportsList = reportsList;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_reports_layout, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            Reports reports = reportsList.get(position);

            // Bind post data to the ViewHolder views
            holder.setProfileImage(reports.getProfileimage());
            holder.setProfileName(reports.getFullname());
            holder.setPostDescription(reports.getDescription());
            holder.setPostImage(reports.getPostimage());
            holder.setPostDate(reports.getDate());
            holder.setPostTime(reports.getTime());
        }

        @Override
        public int getItemCount() {
            return reportsList.size();
        }

        public class PostViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView profileImage;
            private TextView profileName, postDescription, postDate, postTime;
            private ImageView postImage;

            public PostViewHolder(@NonNull View itemView) {
                super(itemView);

                // Initialize ViewHolder components
                profileImage = itemView.findViewById(R.id.report_profile_image);
                profileName = itemView.findViewById(R.id.report_user_name);
                postDescription = itemView.findViewById(R.id.report_post_description);
                postDate = itemView.findViewById(R.id.report_date);
                postTime = itemView.findViewById(R.id.report_time);
                postImage = itemView.findViewById(R.id.report_post_image);
            }

            public void setProfileImage(String imageUrl) {
                // Use Picasso or any other image loading library to load the profile image into the CircleImageView
                if (!TextUtils.isEmpty(imageUrl)) {
                    Picasso.get().load(imageUrl).into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded successfully
                        }

                        @Override
                        public void onError(Exception e) {
                            // Handle the error if the image fails to load
                            e.printStackTrace();
                        }
                    });
                } else {
                    // Set a default placeholder image if the profile image URL is empty or null
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
                    Picasso.get().load(imageUrl).into(postImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded successfully
                        }

                        @Override
                        public void onError(Exception e) {
                            // Handle the error if the image fails to load
                            e.printStackTrace();
                        }
                    });
                } else {
                    // Set a default placeholder image if the post image URL is empty or null
                    postImage.setImageResource(R.drawable.select_image);
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
