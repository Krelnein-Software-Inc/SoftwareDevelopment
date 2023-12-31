package com.example.communite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportPageActivity extends AppCompatActivity {

    private DatabaseReference reportsRef, doneRef;
    private RecyclerView postsRecyclerView;
    private ReportPostAdapter reportPostAdapter;
    private FirebaseAuth mAuth;
    private List<Reports> reportsList;
    private Map<String, Integer> accomplishedCountMap;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        reportsRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("Reports");

        doneRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Progress");

        postsRecyclerView = findViewById(R.id.all_users_report_list);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportsList = new ArrayList<>();
        accomplishedCountMap = new HashMap<>();
        reportPostAdapter = new ReportPostAdapter(reportsList);
        postsRecyclerView.setAdapter(reportPostAdapter);

        reportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reportsList.clear();
                accomplishedCountMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Reports reports = snapshot.getValue(Reports.class);
                    reports.setPostKey(snapshot.getKey());
                    reportsList.add(reports);
                }
                reportPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReportPageActivity.this, "Failed to retrieve reports: " + databaseError.getMessage(), Toast.LENGTH_SHORT);
            }
        });

        ImageView reportPageBackButton = findViewById(R.id.report_page_back_button);
        reportPageBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView addReportButton = findViewById(R.id.add_report_button);
        addReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addReportIntent = new Intent(ReportPageActivity.this, AddReportActivity.class);
                startActivity(addReportIntent);
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

            holder.setProfileImage(reports.getProfileimage());
            holder.setProfileName(reports.getFullname());
            holder.setPostDescription(reports.getDescription());
            holder.setPostImage(reports.getPostimage());
            holder.setPostDate(reports.getDate());
            holder.setPostTime(reports.getTime());
            holder.setDoneButtonStatus(reports.getPostKey());

            // Fetch and set the accomplished count for each report
            fetchAccomplishedCount(reports.getPostKey(), holder);
        }

        @Override
        public int getItemCount() {
            return reportsList.size();
        }

        public class PostViewHolder extends RecyclerView.ViewHolder {

            public ImageView doneReportButton;
            public TextView doneText;
            private CircleImageView profileImage;
            private TextView profileName, postDescription, postDate, postTime;
            private ImageView postImage;
            private ImageButton reportCommentButton;
            private TextView accomplishedCountText;

            public PostViewHolder(@NonNull View itemView) {
                super(itemView);

                profileImage = itemView.findViewById(R.id.report_profile_image);
                profileName = itemView.findViewById(R.id.report_user_name);
                postDescription = itemView.findViewById(R.id.report_post_description);
                postDate = itemView.findViewById(R.id.report_date);
                postTime = itemView.findViewById(R.id.report_time);
                postImage = itemView.findViewById(R.id.report_post_image);
                reportCommentButton = itemView.findViewById(R.id.report_comment_button);
                doneReportButton = itemView.findViewById(R.id.done_button);
                doneText = itemView.findViewById(R.id.done_text);
                accomplishedCountText = itemView.findViewById(R.id.accomplished_count_text);

                reportCommentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Reports reports = reportsList.get(position);
                            String postKey = reports.getPostKey();
                            Intent reportCommentsIntent = new Intent(v.getContext(), ReportCommentsActivity.class);
                            reportCommentsIntent.putExtra("PostKey", postKey);
                            v.getContext().startActivity(reportCommentsIntent);
                        }
                    }
                });

                doneReportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Reports reports = reportsList.get(getAdapterPosition());
                        toggleAccomplishedStatus(reports.getPostKey());
                    }
                });
            }

            public void setProfileImage(String imageUrl) {
                if (!TextUtils.isEmpty(imageUrl)) {
                    Picasso.get().load(imageUrl).into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
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
                if (!TextUtils.isEmpty(imageUrl)) {
                    Picasso.get().load(imageUrl).into(postImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    postImage.setImageResource(R.drawable.select_image);
                }
            }

            public void setPostDate(String date) {
                postDate.setText(date);
            }

            public void setPostTime(String time) {
                postTime.setText(time);
            }

            public void setDoneButtonStatus(String postKey) {
                doneRef.child(postKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(currentUserID)) {
                            // User has accomplished the report, set the doneReportButton to "Liked" state
                            doneReportButton.setImageResource(R.drawable.baseline_check_box_24);
                            doneText.setVisibility(View.VISIBLE); // Show the "Accomplished" text
                        } else {
                            // User has not accomplished the report, set the doneReportButton to "Unliked" state
                            doneReportButton.setImageResource(R.drawable.baseline_check_box_outline_blank_24);
                            doneText.setVisibility(View.INVISIBLE); // Hide the "Accomplished" text
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle onCancelled event
                    }
                });
            }

            public void setAccomplishedCount(int count) {
                if (count > 0) {
                    accomplishedCountText.setVisibility(View.VISIBLE);
                    accomplishedCountText.setText(String.valueOf(count));
                } else {
                    accomplishedCountText.setVisibility(View.GONE);
                }
            }
        }

        private void fetchAccomplishedCount(String postKey, PostViewHolder holder) {
            doneRef.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    accomplishedCountMap.put(postKey, count);
                    holder.setAccomplishedCount(count);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle onCancelled event
                }
            });
        }

        private void toggleAccomplishedStatus(String postKey) {
            DatabaseReference reportRef = doneRef.child(postKey).child(currentUserID);
            reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // The user has already accomplished the report, so we remove the accomplished status
                        reportRef.removeValue();
                    } else {
                        // The user has not accomplished the report, so we add the accomplished status
                        reportRef.setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle onCancelled event
                }
            });
        }
    }
}
