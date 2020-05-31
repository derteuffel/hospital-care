package com.hospital.helpers;


import lombok.Data;

@Data
public class IncubatorHelper {


    private Integer quantity;

    private String incubatorNo;

    private String dateObtained;

    private Boolean status;

    public String getIncubatorNo() {
        return incubatorNo;
    }

    public void setIncubatorNo(String incubatorNo) {
        this.incubatorNo = incubatorNo;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDateObtained() {
        return dateObtained;
    }

    public void setDateObtained(String dateObtained) {
        this.dateObtained = dateObtained;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
