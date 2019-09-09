package com.pikbusiness.model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("business")
public class Business extends ParseObject {

    @SerializedName("isCurrentUser")
    @Expose
    private boolean isCurrentUser;
    @SerializedName("estimatedData")
    @Expose
    private BusinessEstimatedData mBusinessEstimatedData;

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        isCurrentUser = currentUser;
    }


    public Business withCurrentUser(boolean currentUser) {
        isCurrentUser = currentUser;
        return this;
    }


    public BusinessEstimatedData getBusinessEstimatedData() {
        return mBusinessEstimatedData;
    }

    public void setBusinessEstimatedData(BusinessEstimatedData businessEstimatedData) {
        mBusinessEstimatedData = businessEstimatedData;
    }


    public Business withBusinessEstimatedData(BusinessEstimatedData businessEstimatedData) {
        mBusinessEstimatedData = businessEstimatedData;
        return this;
    }

}
