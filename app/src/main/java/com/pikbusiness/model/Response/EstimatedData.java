package com.pikbusiness.model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.security.acl.Owner;

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
    @SerializedName("owner")
    @Expose
    private Owner owner;
    @SerializedName("shop")
    @Expose
    private Shop shop;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("orderStatus")
    @Expose
    private Integer orderStatus;
    @SerializedName("subTotal")
    @Expose
    private int subTotal;
    @SerializedName("tranRef")
    @Expose
    private String tranRef;
    @SerializedName("usrLocation")
    @Expose
    private UsrLocation usrLocation;
    @SerializedName("offerEnanbled")
    @Expose
    private Boolean offerEnanbled;
    @SerializedName("isPaid")
    @Expose
    private Boolean isPaid;
    @SerializedName("taxId")
    @Expose
    private String taxId;
    @SerializedName("userString")
    @Expose
    private String userString;
    @SerializedName("currency")
    @Expose
    private String currency;
    //    @SerializedName("user")
//    @Expose
//    private User user;
    @SerializedName("totalCost")
    @Expose
    private int totalCost;
    @SerializedName("cardLast4")
    @Expose
    private String cardLast4;
    @SerializedName("order")
    @Expose
    private String order;
    @SerializedName("discountAmount")
    @Expose
    private String discountAmount;
    @SerializedName("name")
    @Expose
    private String customerName;
    @SerializedName("carDetails")
    @Expose
    private String carDetails;
    @SerializedName("phoneNumber")
    @Expose
    private int customerPhoneNumber;

    private String offerObjectId;

    private String shopLocationName;

    private int shopPhoneNo;

    private int RefundCost;

    private String cancelledBy;

    private String refund;

    private String cancelNote;

    private int totalTime;

    private String time;

    private Location userLocation;

    private String createdDateAt;

    private String buttonStatus;


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


    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public EstimatedData withUserLocation(Location userLocation) {
        this.userLocation = userLocation;
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

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public EstimatedData withOwner(Owner owner) {
        this.owner = owner;
        return this;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public EstimatedData withShop(Shop shop) {
        this.shop = shop;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public EstimatedData withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public EstimatedData withOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public EstimatedData withSubTotal(int subTotal) {
        this.subTotal = subTotal;
        return this;
    }

    public String getTranRef() {
        return tranRef;
    }

    public void setTranRef(String tranRef) {
        this.tranRef = tranRef;
    }

    public EstimatedData withTranRef(String tranRef) {
        this.tranRef = tranRef;
        return this;
    }

    public UsrLocation getUsrLocation() {
        return usrLocation;
    }

    public void setUsrLocation(UsrLocation usrLocation) {
        this.usrLocation = usrLocation;
    }

    public EstimatedData withUsrLocation(UsrLocation usrLocation) {
        this.usrLocation = usrLocation;
        return this;
    }

    public Boolean getOfferEnanbled() {
        return offerEnanbled;
    }

    public void setOfferEnanbled(Boolean offerEnanbled) {
        this.offerEnanbled = offerEnanbled;
    }

    public EstimatedData withOfferEnanbled(Boolean offerEnanbled) {
        this.offerEnanbled = offerEnanbled;
        return this;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public EstimatedData withIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
        return this;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public EstimatedData withTaxId(String taxId) {
        this.taxId = taxId;
        return this;
    }

    public String getUserString() {
        return userString;
    }

    public void setUserString(String userString) {
        this.userString = userString;
    }

    public EstimatedData withUserString(String userString) {
        this.userString = userString;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public EstimatedData withCurrency(String currency) {
        this.currency = currency;
        return this;
    }
/*

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EstimatedData withUser(User user) {
        this.user = user;
        return this;
    }
*/

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public EstimatedData withTotalCost(int totalCost) {
        this.totalCost = totalCost;
        return this;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public EstimatedData withCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
        return this;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public EstimatedData withOrder(String order) {
        this.order = order;
        return this;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public EstimatedData withDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    public int getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(int customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public EstimatedData withcustomerPhoneNumber(int customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public EstimatedData withCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }


    public String getCarDetails() {
        return carDetails;
    }

    public void setCarDetails(String carDetails) {
        this.carDetails = carDetails;
    }

    public EstimatedData withCarDetails(String CarDetails) {
        this.carDetails = CarDetails;
        return this;
    }

    public String setOfferObjectId() {
        return offerObjectId;
    }

    public void setOfferObjectId(String offerObjectId) {
        this.offerObjectId = offerObjectId;
    }

    public EstimatedData withOfferObjectId(String offerObjectId) {
        this.offerObjectId = offerObjectId;
        return this;
    }

    public String getShopLocationName() {
        return shopLocationName;
    }

    public void setShopLocationName(String shopLocationName) {
        this.shopLocationName = shopLocationName;
    }

    public EstimatedData withShopLocationName(String shopLocationName) {
        this.shopLocationName = shopLocationName;
        return this;
    }

    public int getShopPhoneNo() {
        return shopPhoneNo;
    }

    public void setShopPhoneNo(int shopPhoneNo) {
        this.shopPhoneNo = shopPhoneNo;
    }

    public EstimatedData withShopPhoneNo(int shopPhoneNo) {
        this.shopPhoneNo = shopPhoneNo;
        return this;
    }

    public String getUserId() {
        return userString;
    }

    public void setUserId(String userString) {
        this.userString = userString;
    }

    public int getRefundCost() {
        return RefundCost;
    }

    public void setRefundCost(int RefundCost) {
        this.RefundCost = RefundCost;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }


    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getRefund() {
        return refund;
    }


    public void setRefund(String refund) {
        this.refund = refund;
    }


    public String getCancelNote() {
        return cancelNote;
    }


    public void setCancelNote(String cancelNote) {
        this.cancelNote = cancelNote;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCreatedDateAt(String createdDateAt) {
        this.createdDateAt = createdDateAt;
    }

    public String getCreatedDateAt() {
        return createdDateAt;
    }

    public void setButtonStatus(String buttonStatus) {
        this.buttonStatus = buttonStatus;
    }

    public String getButtonStatus() {
        return buttonStatus;
    }
}