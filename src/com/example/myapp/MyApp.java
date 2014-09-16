package com.example.myapp;

import android.app.Application;
import com.example.model.User;

/**
 * Created by leju on 2014/9/16.
 */
public class MyApp extends Application {
    public User user;
    public void onCreate(){
        this.user = new User();
    }
}
