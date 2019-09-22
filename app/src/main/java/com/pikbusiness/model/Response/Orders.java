package com.pikbusiness.model.Response;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Orders implements Parcelable {

    @SerializedName("estimatedData")
    @Expose
    private EstimatedData estimatedData;
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
    @SerializedName("state")
    @Expose
    private State state;

    public EstimatedData getEstimatedData() {
        return estimatedData;
    }

    public void setEstimatedData(EstimatedData estimatedData) {
        this.estimatedData = estimatedData;
    }

    public Orders withEstimatedData(EstimatedData estimatedData) {
        this.estimatedData = estimatedData;
        return this;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Orders withIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        return this;
    }

    public Boolean getIsDeleting() {
        return isDeleting;
    }

    public void setIsDeleting(Boolean isDeleting) {
        this.isDeleting = isDeleting;
    }

    public Orders withIsDeleting(Boolean isDeleting) {
        this.isDeleting = isDeleting;
        return this;
    }

    public Integer getIsDeletingEventually() {
        return isDeletingEventually;
    }

    public void setIsDeletingEventually(Integer isDeletingEventually) {
        this.isDeletingEventually = isDeletingEventually;
    }

    public Orders withIsDeletingEventually(Integer isDeletingEventually) {
        this.isDeletingEventually = isDeletingEventually;
        return this;
    }

    public Boolean getLdsEnabledWhenParceling() {
        return ldsEnabledWhenParceling;
    }

    public void setLdsEnabledWhenParceling(Boolean ldsEnabledWhenParceling) {
        this.ldsEnabledWhenParceling = ldsEnabledWhenParceling;
    }

    public Orders withLdsEnabledWhenParceling(Boolean ldsEnabledWhenParceling) {
        this.ldsEnabledWhenParceling = ldsEnabledWhenParceling;
        return this;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Orders withSate(State state) {
        this.state = state;
        return this;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}