package com.pegp.eservicio.ScheduleDays;

public class scheduleDaysData {
    Integer id,isPass,hasBid,isBidAccepted;
    String title,scheduleFrom,scheduleTo,remarks,s24HourFrom,s24HourTo,rate,dbDate,formattedDate,bidAmount,emailAddress;
    Boolean isFromNewsFeed;

    public scheduleDaysData(Integer id, String title, String scheduleFrom, String scheduleTo, String remarks, String s24HourFrom, String s24HourTo, String rate, String dbDate, String formattedDate, String bidAmount, Boolean isFromNewsFeed, Integer isPass,Integer hasBid,Integer isBidAccepted, String emailAddress) {
        this.id = id;
        this.title = title;
        this.scheduleFrom = scheduleFrom;
        this.scheduleTo = scheduleTo;
        this.remarks = remarks;
        this.s24HourFrom = s24HourFrom;
        this.s24HourTo = s24HourTo;
        this.rate = rate;
        this.dbDate = dbDate;
        this.formattedDate = formattedDate;
        this.bidAmount = bidAmount;
        this.isFromNewsFeed = isFromNewsFeed;
        this.isPass = isPass;
        this.hasBid = hasBid;
        this.isBidAccepted = isBidAccepted;
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Integer getIsBidAccepted() {
        return isBidAccepted;
    }

    public void setIsBidAccepted(Integer isBidAccepted) {
        this.isBidAccepted = isBidAccepted;
    }

    public Integer getHasBid() {
        return hasBid;
    }

    public void setHasBid(Integer hasBid) {
        this.hasBid = hasBid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScheduleFrom() {
        return scheduleFrom;
    }

    public void setScheduleFrom(String scheduleFrom) {
        this.scheduleFrom = scheduleFrom;
    }

    public String getScheduleTo() {
        return scheduleTo;
    }

    public void setScheduleTo(String scheduleTo) {
        this.scheduleTo = scheduleTo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getS24HourFrom() {
        return s24HourFrom;
    }

    public void setS24HourFrom(String s24HourFrom) {
        this.s24HourFrom = s24HourFrom;
    }

    public String getS24HourTo() {
        return s24HourTo;
    }

    public void setS24HourTo(String s24HourTo) {
        this.s24HourTo = s24HourTo;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDbDate() {
        return dbDate;
    }

    public void setDbDate(String dbDate) {
        this.dbDate = dbDate;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(String bidAmount) {
        this.bidAmount = bidAmount;
    }

    public Boolean getFromNewsFeed() {
        return isFromNewsFeed;
    }

    public void setFromNewsFeed(Boolean fromNewsFeed) {
        isFromNewsFeed = fromNewsFeed;
    }

    public Integer getIsPass() {
        return isPass;
    }

    public void setIsPass(Integer isPass) {
        this.isPass = isPass;
    }
}
