package com.pegp.eservicio.Photos;

public class GalleryData {
    Integer id;
    String imageLink;
    Boolean isDisabled;

    public GalleryData(Integer id, String imageLink, Boolean isDisabled) {
        this.id = id;
        this.imageLink = imageLink;
        this.isDisabled = isDisabled;
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

    public Boolean getDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean disabled) {
        isDisabled = disabled;
    }
}
