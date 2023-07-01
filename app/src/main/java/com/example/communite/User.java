package com.example.communite;

public class User {
    private String name;
    private String email;
    private String profileImage;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String name, String email, String profileImage) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
