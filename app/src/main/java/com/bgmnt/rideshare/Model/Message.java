package com.bgmnt.rideshare.Model;


public class Message {
    private String key;
    private String message;
    private String messageTime;
    private String name;

    public Message(String str, String str2, String str3) {
        this.message = str;
        this.name = str2;
        this.messageTime = str3;
    }

    public String getMessageTime() {
        return this.messageTime;
    }

    public void setMessageTime(String str) {
        this.messageTime = str;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String str) {
        this.message = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String str) {
        this.key = str;
    }

    public Message() {
    }

    public String toString() {
        return "Message{message='" + this.message + "', name='" + this.name + "', key='" + this.key + "', messageTime='" + this.messageTime + "'}";
    }
}