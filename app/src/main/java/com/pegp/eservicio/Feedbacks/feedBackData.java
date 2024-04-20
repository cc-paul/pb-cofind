package com.pegp.eservicio.Feedbacks;

public class feedBackData {
    Integer id,isYours,ableToReply,userID;
    String name,dateCreated,imageLink,feedback,replyName,replyImageLink,reply;
    Double bidAmount;

    public feedBackData(Integer id, String name, String dateCreated, String imageLink, String feedback, Integer isYours,Integer ableToReply,
                        String replyName,String replyImageLink,String reply,Double bidAmount,Integer userID) {
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
        this.imageLink = imageLink;
        this.feedback = feedback;
        this.isYours = isYours;
        this.ableToReply = ableToReply;
        this.replyName = replyName;
        this.replyImageLink = replyImageLink;
        this.reply = reply;
        this.bidAmount = bidAmount;
        this.userID = userID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(Double bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getReplyImageLink() {
        return replyImageLink;
    }

    public void setReplyImageLink(String replyImageLink) {
        this.replyImageLink = replyImageLink;
    }

    public Integer getAbleToReply() {
        return ableToReply;
    }

    public void setAbleToReply(Integer ableToReply) {
        this.ableToReply = ableToReply;
    }

    public Integer getIsYours() {
        return isYours;
    }

    public void setIsYours(Integer isYours) {
        this.isYours = isYours;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
