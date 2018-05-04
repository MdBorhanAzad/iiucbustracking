package com.example.fazlulhoque.iiucbustracking.Student;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Fazlul Hoque on 12/10/2017.
 */

public class StudentUser {
    private String email,password,name,phone,DeviceToken;
   //  DeviceTOken= FirebaseInstanceId.getInstance().getToken();

    StudentUser(){
    }

    public StudentUser(String email, String password,String DeviceToken) {
        this.email = email;
        this.password = password;
        this.DeviceToken=DeviceToken;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public  String getDeviceToken(){
        return DeviceToken;
    }

    public void setDeviceToken(String DeviceToken)
    {
       this.DeviceToken=DeviceToken;
    }


}
