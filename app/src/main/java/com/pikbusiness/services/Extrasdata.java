package com.pikbusiness.services;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Extrasdata {

    @SerializedName("SingleExtras")
    @Expose
    private List<SingleExtra> singleExtras = null;
    @SerializedName("Extras")
    @Expose
    private List<Object> extras = null;

    public List<SingleExtra> getSingleExtras() {
        return singleExtras;
    }

    public void setSingleExtras(List<SingleExtra> singleExtras) {
        this.singleExtras = singleExtras;
    }

    public List<Object> getExtras() {
        return extras;
    }

    public void setExtras(List<Object> extras) {
        this.extras = extras;
    }


   public class SingleExtra {

        @SerializedName("Id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("isEnabled")
        @Expose
        private String isEnabled;
        @SerializedName("values")
        @Expose
        private List<Value> values = null;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(String isEnabled) {
            this.isEnabled = isEnabled;
        }

        public List<Value> getValues() {
            return values;
        }

        public void setValues(List<Value> values) {
            this.values = values;
        }

    }

   public class Value {

        @SerializedName("extraId")
        @Expose
        private Integer extraId;
        @SerializedName("objId")
        @Expose
        private String objId;
        @SerializedName("extraName")
        @Expose
        private String extraName;
        @SerializedName("extraPrice")
        @Expose
        private String extraPrice;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("isEnabled")
        @Expose
        private String isEnabled;

        public Integer getExtraId() {
            return extraId;
        }

        public void setExtraId(Integer extraId) {
            this.extraId = extraId;
        }

        public String getObjId() {
            return objId;
        }

        public void setObjId(String objId) {
            this.objId = objId;
        }

        public String getExtraName() {
            return extraName;
        }

        public void setExtraName(String extraName) {
            this.extraName = extraName;
        }

        public String getExtraPrice() {
            return extraPrice;
        }

        public void setExtraPrice(String extraPrice) {
            this.extraPrice = extraPrice;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(String isEnabled) {
            this.isEnabled = isEnabled;
        }

    }
}