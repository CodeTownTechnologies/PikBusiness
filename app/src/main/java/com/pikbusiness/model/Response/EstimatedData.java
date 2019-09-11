package com.pikbusiness.model.Response;

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
    private int pin;
    @SerializedName("phoneNo")
    @Expose
    private int phoneNo;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("tax")
    @Expose
    private int tax;
    @SerializedName("business")
    @Expose
    private Business business;
    @SerializedName("approveStatus")
    @Expose
    private String approveStatus;


    public int getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(int shopStatus) {
        this.shopStatus = shopStatus;
    }

    public EstimatedData withShopStatus(int shopStatus) {
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

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public EstimatedData withPin(int pin) {
        this.pin = pin;
        return this;
    }

    public int getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(int phoneNo) {
        this.phoneNo = phoneNo;
    }

    public EstimatedData withPhoneNo(int phoneNo) {
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


    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public EstimatedData withTax(int tax) {
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

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public EstimatedData withApprove(String approve) {
        this.approveStatus = approve;
        return this;
    }

}