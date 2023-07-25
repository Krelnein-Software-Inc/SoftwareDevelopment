package com.example.communite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;

    private Toolbar mToolbar;
    private CircleImageView navProfileImage;
    private TextView navProfileUsername;
    private ImageButton AddNewPostButton;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostsRef, LikesRef;


    private String currentUserID;
    Boolean LikeChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Likes");


        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AddNewPostButton = findViewById(R.id.add_new_post_button);

        drawerLayout = findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView = findViewById(R.id.navigation_view);

        postList = findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);


        View navHeaderView = navigationView.inflateHeaderView(R.layout.navigation_header);
        navProfileImage = navHeaderView.findViewById(R.id.nav_profile_image);
        navProfileUsername = navHeaderView.findViewById(R.id.nav_user_full_name);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return UserMenuSelector(item);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            currentUserID = currentUser.getUid();
            UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild("fullname")) {
                            String fullname = dataSnapshot.child("fullname").getValue().toString();
                            navProfileUsername.setText(fullname);
                        }
                        if (dataSnapshot.hasChild("profileimage")) {
                            String image = dataSnapshot.child("profileimage").getValue().toString();
                            Picasso.get().load(image).placeholder(R.drawable.profile).into(navProfileImage);
                        } else {
                            // User data does not exist, redirect to SetupActivity
                            SendUserToSetupActivity();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error occurred: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            AddNewPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendUserToPostActivity();

                }
            });

            DisplayAllUsersPost();
        }
    }

    private void DisplayAllUsersPost() {
        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(PostsRef, Posts.class)
                .build();

        FirebaseRecyclerAdapter<Posts, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder viewHolder, int position, @NonNull Posts model) {

                String PostKey = getRef(position).getKey();

                viewHolder.setFullname(model.getFullname());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setProfileImage(getApplicationContext(), model.getProfileimage());
                viewHolder.setPostImage(getApplicationContext(), model.getPostimage());
                viewHolder.setLikeButtonStatus(PostKey);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(MainActivity.this, ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(clickPostIntent);
                    }
                });

                viewHolder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsIntent = new Intent(MainActivity.this, CommentsActivity.class);
                        commentsIntent.putExtra("PostKey", PostKey);
                        startActivity(commentsIntent);
                    }
                });

                viewHolder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LikeChecker = true;

                        LikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (LikeChecker) {
                                    if (dataSnapshot.child(PostKey).hasChild(currentUserID)) {
                                        LikesRef.child(PostKey).child(currentUserID).removeValue();
                                        LikeChecker = false;
                                    } else {
                                        LikesRef.child(PostKey).child(currentUserID).setValue(true);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle onCancelled event
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                return new PostViewHolder(view);
            }
        };

        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;

        ImageButton LikePostButton, CommentPostButton;
        TextView DisplayNoOfLikes;
        int countLikes;
        String currentUserId;
        DatabaseReference LikesRef;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            LikePostButton=(ImageButton)  mView.findViewById(R.id.like_button);
            CommentPostButton=(ImageButton)  mView.findViewById(R.id.comment_button);
            DisplayNoOfLikes = (TextView)  mView.findViewById(R.id.display_no_of_likes);

            LikesRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setFullname(String fullname) {
            TextView username = mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileImage(Context ctx, String profileimage) {
            CircleImageView image = mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileimage).into(image);
        }

        public void setTime(String time) {
            TextView PostTime = mView.findViewById(R.id.post_time);
            PostTime.setText("   " + time);
        }

        public void setDate(String date) {
            TextView PostDate = mView.findViewById(R.id.post_date);
            PostDate.setText("   " + date);
        }

        public void setDescription(String description) {
            TextView PostDescription = mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setPostImage(Context ctx, String postimage) {
            ImageView PostImage = mView.findViewById(R.id.post_image);
            Picasso.get().load(postimage).into(PostImage);
        }

        public void setLikeButtonStatus(String postKey) {
            String PostKey = postKey;
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PostKey).hasChild(currentUserId)){
                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.like);
                        DisplayNoOfLikes.setText(Integer.toString(countLikes));
                    }
                    else{
                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.dislike);
                        DisplayNoOfLikes.setText(Integer.toString(countLikes));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    private void SendUserToReportPageActivity() {
        Intent SettingsIntent = new Intent(MainActivity.this, ReportPageActivity.class);
        startActivity(SettingsIntent);
    }

    private void SendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }

    private void SendUserToProfileActivity() {
        Intent loginIntent = new Intent(MainActivity.this, ProfileActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToSettingsActivity() {
        Intent SettingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(SettingsIntent);
    }

    private void SendUserToAboutUsActivity() {
        Intent AboutIntent = new Intent(MainActivity.this, AboutUsActivity.class);
        startActivity(AboutIntent);
    }

    private void SendUserToMainActivity() {
        Intent MainIntent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(MainIntent);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean UserMenuSelector(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_post) {
            SendUserToPostActivity();
        } else if (itemId == R.id.nav_profile) {
            SendUserToProfileActivity();
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_home) {
            SendUserToMainActivity();
        } else if (itemId == R.id.nav_report) {
            Toast.makeText(this, "Report", Toast.LENGTH_SHORT).show();
            SendUserToReportPageActivity();
        } else if (itemId == R.id.nav_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            SendUserToSettingsActivity();
        } else if (itemId == R.id.nav_about) {
            Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show();
            SendUserToAboutUsActivity();
        } else if (itemId == R.id.nav_Logout) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        return true;
    }

}


