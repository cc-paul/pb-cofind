package com.pegp.eservicio.Reservation;

public class ReservationData {
    Integer bidID,taskID,setToDisable,isYours,isLiked,freelancerID,isServiceDone,customerID;
    String imageLink,freelancerName,address,mobileNumber,emailAddress,title,venue,dateSched,timeSched,bidAmount,remarks;
    String imageLinkCustomer,customerName,customerAddress,customerMobileNumber,customerEmailAddress;

    public ReservationData(Integer bidID, Integer taskID, String imageLink, String freelancerName, String address, String mobileNumber, String emailAddress,
                           String title, String venue, String dateSched, String timeSched, String bidAmount, String remarks, Integer setToDisable,Integer isYours,Integer isLiked,
                           Integer freelancerID,Integer isServiceDone,
                           String imageLinkCustomer,String customerName,String customerAddress,String customerMobileNumber,String customerEmailAddress,Integer customerID) {
        this.bidID = bidID;
        this.taskID = taskID;
        this.imageLink = imageLink;
        this.freelancerName = freelancerName;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
        this.title = title;
        this.venue = venue;
        this.dateSched = dateSched;
        this.timeSched = timeSched;
        this.bidAmount = bidAmount;
        this.remarks = remarks;
        this.setToDisable = setToDisable;
        this.isYours = isYours;
        this.isLiked = isLiked;
        this.freelancerID = freelancerID;
        this.isServiceDone = isServiceDone;
        this.imageLinkCustomer = imageLinkCustomer;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerMobileNumber = customerMobileNumber;
        this.customerEmailAddress = customerEmailAddress;
        this.customerID = customerID;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public String getImageLinkCustomer() {
        return imageLinkCustomer;
    }

    public void setImageLinkCustomer(String imageLinkCustomer) {
        this.imageLinkCustomer = imageLinkCustomer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerMobileNumber() {
        return customerMobileNumber;
    }

    public void setCustomerMobileNumber(String customerMobileNumber) {
        this.customerMobileNumber = customerMobileNumber;
    }

    public String getCustomerEmailAddress() {
        return customerEmailAddress;
    }

    public void setCustomerEmailAddress(String customerEmailAddress) {
        this.customerEmailAddress = customerEmailAddress;
    }

    public Integer getIsServiceDone() {
        return isServiceDone;
    }

    public void setIsServiceDone(Integer isServiceDone) {
        this.isServiceDone = isServiceDone;
    }

    public Integer getFreelancerID() {
        return freelancerID;
    }

    public void setFreelancerID(Integer freelancerID) {
        this.freelancerID = freelancerID;
    }

    public Integer getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Integer isLiked) {
        this.isLiked = isLiked;
    }

    public Integer getIsYours() {
        return isYours;
    }

    public void setIsYours(Integer isYours) {
        this.isYours = isYours;
    }

    public Integer getSetToDisable() {
        return setToDisable;
    }

    public void setSetToDisable(Integer setToDisable) {
        this.setToDisable = setToDisable;
    }

    public Integer getBidID() {
        return bidID;
    }

    public void setBidID(Integer bidID) {
        this.bidID = bidID;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getFreelancerName() {
        return freelancerName;
    }

    public void setFreelancerName(String freelancerName) {
        this.freelancerName = freelancerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDateSched() {
        return dateSched;
    }

    public void setDateSched(String dateSched) {
        this.dateSched = dateSched;
    }

    public String getTimeSched() {
        return timeSched;
    }

    public void setTimeSched(String timeSched) {
        this.timeSched = timeSched;
    }

    public String getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(String bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
