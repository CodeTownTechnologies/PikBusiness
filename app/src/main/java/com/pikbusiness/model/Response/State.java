package com.pikbusiness.model.Response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class State {

    @SerializedName("isNew")
    @Expose
    private Boolean isNew;
    @SerializedName("availableKeys")
    @Expose
    private List<Object> availableKeys = null;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("createdAt")
    @Expose
    private int createdAt;
    @SerializedName("isComplete")
    @Expose
    private Boolean isComplete;
    @SerializedName("objectId")
    @Expose
    private String objectId;
//    @SerializedName("serverData")
//    @Expose
//    private ServerData serverData;
    @SerializedName("updatedAt")
    @Expose
    private Integer updatedAt;

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public State withIsNew(Boolean isNew) {
        this.isNew = isNew;
        return this;
    }

    public List<Object> getAvailableKeys() {
        return availableKeys;
    }

    public void setAvailableKeys(List<Object> availableKeys) {
        this.availableKeys = availableKeys;
    }

    public State withAvailableKeys(List<Object> availableKeys) {
        this.availableKeys = availableKeys;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public State withClassName(String className) {
        this.className = className;
        return this;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public State withCreatedAt(int createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(Boolean isComplete) {
        this.isComplete = isComplete;
    }

    public State withIsComplete(Boolean isComplete) {
        this.isComplete = isComplete;
        return this;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public State withObjectId(String objectId) {
        this.objectId = objectId;
        return this;
    }

//    public ServerData getServerData() {
//        return serverData;
//    }
//
//    public void setServerData(ServerData serverData) {
//        this.serverData = serverData;
//    }
//
//    public State withServerData(ServerData serverData) {
//        this.serverData = serverData;
//        return this;
//    }

    public Integer getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Integer updatedAt) {
        this.updatedAt = updatedAt;
    }

    public State withUpdatedAt(Integer updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

}