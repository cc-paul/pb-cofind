package com.pegp.eservicio.Others;

public class Months {
    int monthID;
    String monthName;

    public int getMonthID() {
        return monthID;
    }

    public void setMonthID(int monthID) {
        this.monthID = monthID;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public Months(int monthID, String monthName) {
        this.monthID = monthID;
        this.monthName = monthName;
    }
}
