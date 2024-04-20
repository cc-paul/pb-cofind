package com.pegp.eservicio.ValidID;

public class validIDData {
    String id,imageLink;

    public validIDData(String id, String imageLink) {
        this.id = id;
        this.imageLink = imageLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
