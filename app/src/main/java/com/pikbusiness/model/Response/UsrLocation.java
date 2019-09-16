package com.pikbusiness.model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class UsrLocation {

    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public UsrLocation withLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public UsrLocation withLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

}