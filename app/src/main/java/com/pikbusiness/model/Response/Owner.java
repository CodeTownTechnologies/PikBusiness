package com.pikbusiness.model.Response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Owner {

    @SerializedName("isCurrentUser")
    @Expose
    private Boolean isCurrentUser;
//    @SerializedName("estimatedData")
//    @Expose
//    private EstimatedData_ estimatedData;
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
//    private Mutex mutex;
//    @SerializedName("operationSetQueue")
//    @Expose
//    private List<OperationSetQueue> operationSetQueue = null;
//    @SerializedName("saveEvent")
//    @Expose
//    private SaveEvent saveEvent;
//    @SerializedName("state")
//    @Expose
//    private State state;
//    @SerializedName("taskQueue")
//    @Expose
//    private TaskQueue taskQueue;

    public Boolean getIsCurrentUser() {
        return isCurrentUser;
    }

    public void setIsCurrentUser(Boolean isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
    }

    public Owner withIsCurrentUser(Boolean isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
        return this;
    }

 /*   public EstimatedData_ getEstimatedData() {
        return estimatedData;
    }

    public void setEstimatedData(EstimatedData_ estimatedData) {
        this.estimatedData = estimatedData;
    }

    public Owner withEstimatedData(EstimatedData_ estimatedData) {
        this.estimatedData = estimatedData;
        return this;
    }*/

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Owner withIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        return this;
    }

    public Boolean getIsDeleting() {
        return isDeleting;
    }

    public void setIsDeleting(Boolean isDeleting) {
        this.isDeleting = isDeleting;
    }

    public Owner withIsDeleting(Boolean isDeleting) {
        this.isDeleting = isDeleting;
        return this;
    }

    public Integer getIsDeletingEventually() {
        return isDeletingEventually;
    }

    public void setIsDeletingEventually(Integer isDeletingEventually) {
        this.isDeletingEventually = isDeletingEventually;
    }

    public Owner withIsDeletingEventually(Integer isDeletingEventually) {
        this.isDeletingEventually = isDeletingEventually;
        return this;
    }

    public Boolean getLdsEnabledWhenParceling() {
        return ldsEnabledWhenParceling;
    }

    public void setLdsEnabledWhenParceling(Boolean ldsEnabledWhenParceling) {
        this.ldsEnabledWhenParceling = ldsEnabledWhenParceling;
    }

    public Owner withLdsEnabledWhenParceling(Boolean ldsEnabledWhenParceling) {
        this.ldsEnabledWhenParceling = ldsEnabledWhenParceling;
        return this;
    }

   /* public Mutex getMutex() {
        return mutex;
    }

    public void setMutex(Mutex mutex) {
        this.mutex = mutex;
    }

    public Owner withMutex(Mutex mutex) {
        this.mutex = mutex;
        return this;
    }

    public List<OperationSetQueue> getOperationSetQueue() {
        return operationSetQueue;
    }

    public void setOperationSetQueue(List<OperationSetQueue> operationSetQueue) {
        this.operationSetQueue = operationSetQueue;
    }

    public Owner withOperationSetQueue(List<OperationSetQueue> operationSetQueue) {
        this.operationSetQueue = operationSetQueue;
        return this;
    }

    public SaveEvent getSaveEvent() {
        return saveEvent;
    }

    public void setSaveEvent(SaveEvent saveEvent) {
        this.saveEvent = saveEvent;
    }

    public Owner withSaveEvent(SaveEvent saveEvent) {
        this.saveEvent = saveEvent;
        return this;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Owner withState(State state) {
        this.state = state;
        return this;
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public Owner withTaskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
        return this;
    }*/

}
