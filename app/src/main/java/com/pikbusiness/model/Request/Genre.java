package com.pikbusiness.model.Request;

import com.pikbusiness.model.Response.Orders;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Genre extends ExpandableGroup<Orders> {

  private int iconResId;

  public Genre(String title, List<Orders> items, int iconResId) {
    super(title, items);
    this.iconResId = iconResId;
  }



  public int getIconResId() {
    return iconResId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Genre)) return false;

    Genre genre = (Genre) o;

    return getIconResId() == genre.getIconResId();

  }

  @Override
  public int hashCode() {
    return getIconResId();
  }
}

