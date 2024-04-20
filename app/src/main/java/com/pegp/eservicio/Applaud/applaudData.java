package com.pegp.eservicio.Applaud;

public class applaudData {
    String name,ImageLink;

    public applaudData(String name, String imageLink) {
        this.name = name;
        ImageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }
}
