package com.pegp.eservicio.Profiles;

public class profileData {
    String profileLink,name,mobileNumber,address,services,gender;
    Integer serviceCount,isServiceMade;
    Integer id,countLikers,countMessages,countServices,isLiked;

    public profileData(String profileLink, String name, String mobileNumber, String address, String services, String gender, Integer serviceCount, Integer id,
                       Integer countLikers, Integer countMessages, Integer countServices, Integer isLiked, Integer isServiceMade) {
        this.profileLink = profileLink;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.services = services;
        this.gender = gender;
        this.serviceCount = serviceCount;
        this.id = id;
        this.countLikers = countLikers;
        this.countMessages = countMessages;
        this.countServices = countServices;
        this.isLiked = isLiked;
        this.isServiceMade = isServiceMade;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(Integer serviceCount) {
        this.serviceCount = serviceCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCountLikers() {
        return countLikers;
    }

    public void setCountLikers(Integer countLikers) {
        this.countLikers = countLikers;
    }

    public Integer getCountMessages() {
        return countMessages;
    }

    public void setCountMessages(Integer countMessages) {
        this.countMessages = countMessages;
    }

    public Integer getCountServices() {
        return countServices;
    }

    public void setCountServices(Integer countServices) {
        this.countServices = countServices;
    }

    public Integer getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Integer isLiked) {
        this.isLiked = isLiked;
    }

    public Integer getIsServiceMade() {
        return isServiceMade;
    }

    public void setIsServiceMade(Integer isServiceMade) {
        this.isServiceMade = isServiceMade;
    }
}
