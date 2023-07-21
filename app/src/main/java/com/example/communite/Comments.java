package com.example.communite;

public class Comments {
    private String uid;
    private String username;
    private String comment;
    private String date;
    private String time;

    public Comments() {
        // Default constructor required for Firebase
    }

    public Comments(String uid, String username, String comment, String date, String time) {
        this.uid = uid;
        this.username = username;
        this.comment = comment;
        this.date = date;
        this.time = time;
    }

    public Comments(String uid, String userName, String commentText) {

    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
