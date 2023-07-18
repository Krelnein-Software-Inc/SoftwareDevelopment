package com.example.communite;
public class Comments {
    private String uid;
    private String comment;
    private String username;
    private String date;
    private String time;

    public Comments() {
        // Default constructor required for Firebase
    }

    public Comments(String uid, String comment, String username, String date, String time) {
        this.uid = uid;
        this.comment = comment;
        this.username = username;
        this.date = date;
        this.time = time;
    }

    // Getters and setters

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDateTime() {
        return date + " " + time;
    }
}
