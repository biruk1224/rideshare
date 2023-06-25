package com.bgmnt.rideshare.Model;


import java.io.Serializable;

public class User implements Serializable {
    long created_time;
    private String destination;
    private String distance;
    private String email;
    private String fullname;
    private String name;
    private String noofpeople;
    String phoneno;
    private String uid;
    private String username;

    public String getProfilepic() {
        return Profilepic;
    }

    public void setProfilepic(String profilepic) {
        Profilepic = profilepic;
    }

    private String Profilepic;

    public long getCreated_time() {
        return this.created_time;
    }

    public void setCreated_time(long j) {
        this.created_time = j;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getDistance() {
        return this.distance;
    }

    public void setDistance(String str) {
        this.distance = str;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String str) {
        this.username = str;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String str) {
        this.destination = str;
    }

    public String getNoofpeople() {
        return this.noofpeople;
    }

    public void setNoofpeople(String str) {
        this.noofpeople = str;
    }

    public String getFullname() {
        return this.fullname;
    }

    public void setFullname(String str) {
        this.fullname = str;
    }

    public String getPhoneno() {
        return this.phoneno;
    }

    public void setPhoneno(String str) {
        this.phoneno = str;
    }

    public String getUId() {
        return this.uid;
    }

    public void setUid(String str) {
        this.uid = str;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String str) {
        this.email = str;
    }

    public String toString() {
        return "User{uid='" + this.uid + "', username='" + this.username + "', name='" + this.name + "', email='" + this.email + "', fullname='" + this.fullname + "', destination='" + this.destination + "', noofpeople='" + this.noofpeople + "', distance='" + this.distance + "', created_time='" + this.created_time + "', phoneno='" + this.phoneno + "'}";
    }
}