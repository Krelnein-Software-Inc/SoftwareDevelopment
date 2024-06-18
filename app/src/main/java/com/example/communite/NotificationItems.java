package com.example.communite;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationItems implements Parcelable {

    private String name;
    private String notification;
    private int image;

    public NotificationItems(String name, String notification, int image) {
        this.name = name;
        this.notification = notification;
        this.image = image;
    }

    protected NotificationItems(Parcel in) {
        name = in.readString();
        notification = in.readString();
        image = in.readInt();
    }

    public static final Creator<NotificationItems> CREATOR = new Creator<NotificationItems>() {
        @Override
        public NotificationItems createFromParcel(Parcel in) {
            return new NotificationItems(in);
        }

        @Override
        public NotificationItems[] newArray(int size) {
            return new NotificationItems[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(notification);
        dest.writeInt(image);
    }
}
