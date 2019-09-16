package com.pikbusiness.model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("location")
public class Location extends ParseObject {

    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;

    private Double userlatitude;

    private Double userlongitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Location withLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Location withLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public Double getUserlatitude() {
        return userlatitude;
    }

    public void setUserlatitude(Double userlatitude) {
        this.userlatitude = userlatitude;
    }

    public Location withUserlatitude(Double userlatitude) {
        this.userlatitude = userlatitude;
        return this;
    }


    public Double getUserlongitude() {
        return userlongitude;
    }

    public void setUserlongitude(Double userlongitude) {
        this.userlongitude = userlongitude;
    }


    public Location withUserlongitude(Double userlongitude) {
        this.userlongitude = userlongitude;
        return this;
    }
}