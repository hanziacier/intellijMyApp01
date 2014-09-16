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

}
