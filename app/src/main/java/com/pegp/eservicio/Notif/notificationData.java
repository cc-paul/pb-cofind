package com.pegp.eservicio.Notif;

public class notificationData {
    Integer id,receiverID,taskID,isRead;
    String message,dateCreated;

    public notificationData(Integer id, Integer receiverID, Integer taskID, Integer isRead, String message, String dateCreated) {
        this.id = id;
        this.receiverID = receiverID;
        this.taskID = taskID;
        this.isRead = isRead;
        this.message = message;
        this.dateCreated = dateCreated;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(Integer receiverID) {
        this.receiverID = receiverID;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
