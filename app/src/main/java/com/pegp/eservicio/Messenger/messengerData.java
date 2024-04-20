package com.pegp.eservicio.Messenger;

public class messengerData {
    Integer id,senderID;
    String imageLink,message;

    public messengerData(Integer id, String imageLink, String message,Integer senderID) {
        this.id = id;
        this.imageLink = imageLink;
        this.message = message;
        this.senderID = senderID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSenderID() {
        return senderID;
    }

    public void setSenderID(Integer senderID) {
        this.senderID = senderID;
    }
}
