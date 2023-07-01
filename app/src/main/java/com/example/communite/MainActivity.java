package com.example.communite;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

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

    private Toolbar mToolbar;
    private CircleImageView navProfileImage;
    private TextView navProfileUsername;
    private ImageButton AddNewPostButton;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance("https://communite-f7efa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Users");

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AddNewPostButton =(ImageButton) findViewById(R.id.add_new_post_button);

        drawerLayout = findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.navigation_view);
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
                            Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(navProfileImage);
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
        }
    }

    private void SendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }

    private void SendUserToMainActivity() {
        // Already in MainActivity, no need to redirect
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
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
        }else if (itemId == R.id.nav_profile) {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_home) {

        } else if (itemId == R.id.nav_report) {
            Toast.makeText(this, "Report", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_Logout) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        return true;
    }
}
