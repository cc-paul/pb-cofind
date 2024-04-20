package com.pegp.eservicio.Service;

public class serviceData {
    Integer id;
    String serviceName;
    Boolean isSelected;

    public serviceData(Integer id, String serviceName, Boolean isSelected) {
        this.id = id;
        this.serviceName = serviceName;
        this.isSelected = isSelected;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
