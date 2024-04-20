package com.pegp.eservicio.Others;

public class Days {
    Integer days;
    Boolean isCurrentMonth;

    public Days(Integer days, Boolean isCurrentMonth) {
        this.days = days;
        this.isCurrentMonth = isCurrentMonth;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Boolean getCurrentMonth() {
        return isCurrentMonth;
    }

    public void setCurrentMonth(Boolean currentMonth) {
        isCurrentMonth = currentMonth;
    }
}
