package com.pikbusiness.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("estimatedData")
public class EstimatedData extends ParseObject {

    @SerializedName("shopStatus")
    @Expose
    private Integer shopStatus;
    @SerializedName("businessObjectId")
    @Expose
    private String businessObjectId;
    @SerializedName("locationName")
    @Expose
    private String locationName;
    @SerializedName("pin")
    @Expose
    private Integer pin;
    @SerializedName("phoneNo")
    @Expose
    private Integer phoneNo;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("tax")
    @Expose
    private Integer tax;
    @SerializedName("business")
    @Expose
    private Business business;
    @SerializedName("approve")
    @Expose
    private String approve;


    public Integer getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(Integer shopStatus) {
        this.shopStatus = shopStatus;
    }

    public EstimatedData withShopStatus(Integer shopStatus) {
        this.shopStatus = shopStatus;
        return this;
    }

    public String getBusinessObjectId() {
        return businessObjectId;
    }

    public void setBusinessObjectId(String businessObjectId) {
        this.businessObjectId = businessObjectId;
    }

    public EstimatedData withBusinessObjectId(String businessObjectId) {
        this.businessObjectId = businessObjectId;
        return this;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public EstimatedData withLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public EstimatedData withPin(Integer pin) {
        this.pin = pin;
        return this;
    }

    public Integer getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(Integer phoneNo) {
        this.phoneNo = phoneNo;
    }

    public EstimatedData withPhoneNo(Integer phoneNo) {
        this.phoneNo = phoneNo;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public EstimatedData withLocation(Location location) {
        this.location = location;
        return this;
    }


    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public EstimatedData withTax(Integer tax) {
        this.tax = tax;
        return this;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public EstimatedData withBusiness(Business business) {
        this.business = business;
        return this;
    }

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }

    public EstimatedData withApprove(String approve) {
        this.approve = approve;
        return this;
    }

}