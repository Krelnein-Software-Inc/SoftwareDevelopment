package com.example.communite;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationItems extends AppCompatActivity {

    String name;
    String notification;
    int image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }

    public NotificationItems(String name, String notification, int image) {
        this.name = name;
        this.notification = notification;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
