package com.pikbusiness.model.Response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Shop {

//    @SerializedName("estimatedData")
//    @Expose
//    private EstimatedData__ estimatedData;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("isDeleting")
    @Expose
    private Boolean isDeleting;
    @SerializedName("isDeletingEventually")
    @Expose
    private Integer isDeletingEventually;
    @SerializedName("ldsEnabledWhenParceling")
    @Expose
    private Boolean ldsEnabledWhenParceling;
//    @SerializedName("mutex")
//    @Expose
//    private Mutex_ mutex;
//    @SerializedName("operationSetQueue")
//    @Expose
//    private List<OperationSetQueue_> operationSetQueue = null;
//    @SerializedName("saveEvent")
//    @Expose
//    private SaveEvent_ saveEvent;
//    @SerializedName("state")
//    @Expose
//    private State_ state;
//    @SerializedName("taskQueue")
//    @Expose
//    private TaskQueue_ taskQueue;

//    public EstimatedData__ getEstimatedData() {
//        return estimatedData;
//    }
//
//    public void setEstimatedData(EstimatedData__ estimatedData) {
//        this.estimatedData = estimatedData;
//    }
//
//    public Shop withEstimatedData(EstimatedData__ estimatedData) {
//        this.estimatedData = estimatedData;
//        return this;
//    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Shop withIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        return this;
    }

    public Boolean getIsDeleting() {
        return isDeleting;
    }

    public void setIsDeleting(Boolean isDeleting) {
        this.isDeleting = isDeleting;
    }

    public Shop withIsDeleting(Boolean isDeleting) {
        this.isDeleting = isDeleting;
        return this;
    }

    public Integer getIsDeletingEventually() {
        return isDeletingEventually;
    }

    public void setIsDeletingEventually(Integer isDeletingEventually) {
        this.isDeletingEventually = isDeletingEventually;
    }

    public Shop withIsDeletingEventually(Integer isDeletingEventually) {
        this.isDeletingEventually = isDeletingEventually;
        return this;
    }

    public Boolean getLdsEnabledWhenParceling() {
        return ldsEnabledWhenParceling;
    }

    public void setLdsEnabledWhenParceling(Boolean ldsEnabledWhenParceling) {
        this.ldsEnabledWhenParceling = ldsEnabledWhenParceling;
    }

    public Shop withLdsEnabledWhenParceling(Boolean ldsEnabledWhenParceling) {
        this.ldsEnabledWhenParceling = ldsEnabledWhenParceling;
        return this;
    }

//    public Mutex_ getMutex() {
//        return mutex;
//    }
//
//    public void setMutex(Mutex_ mutex) {
//        this.mutex = mutex;
//    }
//
//    public Shop withMutex(Mutex_ mutex) {
//        this.mutex = mutex;
//        return this;
//    }
//
//    public List<OperationSetQueue_> getOperationSetQueue() {
//        return operationSetQueue;
//    }
//
//    public void setOperationSetQueue(List<OperationSetQueue_> operationSetQueue) {
//        this.operationSetQueue = operationSetQueue;
//    }
//
//    public Shop withOperationSetQueue(List<OperationSetQueue_> operationSetQueue) {
//        this.operationSetQueue = operationSetQueue;
//        return this;
//    }
//
//    public SaveEvent_ getSaveEvent() {
//        return saveEvent;
//    }
//
//    public void setSaveEvent(SaveEvent_ saveEvent) {
//        this.saveEvent = saveEvent;
//    }
//
//    public Shop withSaveEvent(SaveEvent_ saveEvent) {
//        this.saveEvent = saveEvent;
//        return this;
//    }
//
//    public State_ getState() {
//        return state;
//    }
//
//    public void setState(State_ state) {
//        this.state = state;
//    }
//
//    public Shop withState(State_ state) {
//        this.state = state;
//        return this;
//    }
//
//    public TaskQueue_ getTaskQueue() {
//        return taskQueue;
//    }
//
//    public void setTaskQueue(TaskQueue_ taskQueue) {
//        this.taskQueue = taskQueue;
//    }
//
//    public Shop withTaskQueue(TaskQueue_ taskQueue) {
//        this.taskQueue = taskQueue;
//        return this;
//    }

}