package com.pikbusiness.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("")
public class BusinessEstimatedData extends ParseObject {

    @SerializedName("Business_name")
    @Expose
    private String businessName;
    @SerializedName("firstTimeLogin")
    @Expose
    private boolean firstTimeLogin;


    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public BusinessEstimatedData withBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }


    public boolean isFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }

    public BusinessEstimatedData withFirstTimeLogin(boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
        return this;
    }

}
