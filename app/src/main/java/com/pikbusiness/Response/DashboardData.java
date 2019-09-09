package com.pikbusiness.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.parse.ParseObject;

import java.util.List;

public class DashboardData extends ParseObject {

    @SerializedName("estimatedData")
    @Expose
    private List<EstimatedData> estimatedData;

    public List<EstimatedData> getEstimatedData() {
        return estimatedData;
    }

    public void setEstimatedData(List<EstimatedData> estimatedData) {
        this.estimatedData = estimatedData;
    }
}

