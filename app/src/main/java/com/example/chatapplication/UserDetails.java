package com.example.chatapplication;
//cus i wanna store the username in the Realtime DB
public class UserDetails {
    private String id;
    private String username;
    private String imgURL;
    private String bio;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserDetails(){}

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public UserDetails(String id, String username, String imgURL,String bio,String status) {
        this.id = id;
        this.username = username;
        this.imgURL = imgURL;
        this.bio = bio;
        this.status=status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImgURL() {
        return imgURL;
    }


    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
