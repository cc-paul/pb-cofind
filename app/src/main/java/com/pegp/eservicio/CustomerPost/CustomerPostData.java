package com.pegp.eservicio.CustomerPost;

public class CustomerPostData {
    Integer id,userID,countComment;
    String imageLink,mobileNumber,address,content,dateCreated,imageLinks,fullName,email;

    public CustomerPostData(Integer id, String imageLink, String mobileNumber, String address, String content, String dateCreated,
                            String imageLinks,String fullName,Integer userID,Integer countComment,String email) {
        this.id = id;
        this.imageLink = imageLink;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.content = content;
        this.dateCreated = dateCreated;
        this.imageLinks = imageLinks;
        this.fullName = fullName;
        this.userID = userID;
        this.countComment = countComment;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCountComment() {
        return countComment;
    }

    public void setCountComment(Integer countComment) {
        this.countComment = countComment;
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(String imageLinks) {
        this.imageLinks = imageLinks;
    }
}
