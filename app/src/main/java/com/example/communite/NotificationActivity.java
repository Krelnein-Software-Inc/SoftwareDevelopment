package com.example.communite;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.internal.BaselineLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    AdapterActivity myAdapter;
    List<NotificationItems> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        database = FirebaseDatabase.getInstance().getReference("Posts");
        recyclerView.setHasFixedSize(true);

        ImageView notificationBackButton = findViewById(R.id.notification_back_button);

        RecyclerView recyclerView = findViewById(R.id.all_notification_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Posts posts = dataSnapshot.getValue(Posts.class);
                    items.add(posts);
                }
                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        List<NotificationItems>itemsList = new ArrayList<>();
        itemsList.add(new NotificationItems("Test Notification","Try lang", R.drawable.baseline_person_outline_24));
        itemsList.add(new NotificationItems("Test Notification","Try lang", R.drawable.baseline_person_outline_24));
        itemsList.add(new NotificationItems("Test Notification","Try lang", R.drawable.baseline_person_outline_24));


        recyclerView.setAdapter(new AdapterActivity(getApplicationContext(),itemsList));

        notificationBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
            //test
        });

    }


}
