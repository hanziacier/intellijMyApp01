package com.example.model;

/**
 * Created by leju on 2014/9/16.
 */
public class User {
    private String userName;
    private String email;
    private String pic;
    private int id;
    public void User(){
        setUser(0,"","","");
    }
    public void setUser(int id,String userName,String email,String pic){
        this.id = id;
        this.email = email;
        this.pic = pic;
        this.userName = userName;
    }
    public String getUserName(){
        return this.userName;
    }
    public int getId(){
        return this.id;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPic(){
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
    public void User(String userName, String email, String pic, int id) {
        this.userName = userName;
        this.email = email;
        this.pic = pic;
        this.id = id;
    }
}
