package com.pikbusiness.model.Response;

public class OrderItem {

    private String extraItem;
    private String categoryName;
    private String menuName;
    private String itemPrice;
    private double totalPrice;

    public String getExtraItem() {
        return extraItem;
    }

    public void setExtraItem(String extraItem) {
        this.extraItem = extraItem;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
