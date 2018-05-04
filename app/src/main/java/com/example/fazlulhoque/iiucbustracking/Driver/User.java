package com.example.fazlulhoque.iiucbustracking.Driver;

/**
 * Created by Fazlul Hoque on 12/6/2017.
 */
 class User {
    private String email,password,name,phone,DeviceToken;

    User(){
    }

    public User(String email, String password, String name, String phone,String DeviceToken) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public  String getDeviceToken(){
        return DeviceToken;
    }

    public void setDeviceToken(String DeviceToken)
    {
        this.DeviceToken=DeviceToken;
    }
}
