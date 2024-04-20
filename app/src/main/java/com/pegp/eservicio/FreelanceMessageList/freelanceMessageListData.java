package com.pegp.eservicio.FreelanceMessageList;

public class freelanceMessageListData {
    String imageLink,name,lastMessage,lastDate,chatID;
    Integer notifCount;

    public freelanceMessageListData(String imageLink, String name, String lastMessage, String lastDate, String chatID,Integer notifCount) {
        this.imageLink = imageLink;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastDate = lastDate;
        this.chatID = chatID;
        this.notifCount = notifCount;
    }

    public Integer getNotifCount() {
        return notifCount;
    }

    public void setNotifCount(Integer notifCount) {
        this.notifCount = notifCount;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }
}
