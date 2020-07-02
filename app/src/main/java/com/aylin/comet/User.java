package com.aylin.comet;

/**
 * Created by Aylin on 11.03.2018.
 */

public  class User {
    public String username;
    public String userId;
    public String email;
    public User(String userId, String username, String email){
        this.userId=userId;
        this.username=username;
        this.email=email;
    }
    public User(){

    }

    public void setName() {
        this.username = username;
    }
    public String getName() {
        return username;
    }
    public void setuserId() {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }
    public void setEmail() {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

}

