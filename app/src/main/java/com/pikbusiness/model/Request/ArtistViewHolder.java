package com.pikbusiness.model.Request;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pikbusiness.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class ArtistViewHolder extends ChildViewHolder {

  TextView tvCustomerName, tvDistance, tvDistanceTime, tvTimer, tvNote,
          tvSubTotal, tvVatText, tvVatPrice, tvTotal, tvCancelOrder, tvTagLine, btnCall;
  LinearLayout headBackgroundLayout;
  Button btnChangeStatus;

  public ArtistViewHolder(View itemView) {
    super(itemView);
    tvCustomerName = (TextView) itemView.findViewById(R.id.customer_name);
    tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
    tvDistanceTime = (TextView) itemView.findViewById(R.id.tv_distance_time);
    headBackgroundLayout = (LinearLayout) itemView.findViewById(R.id.head_background_color);
    tvTimer = (TextView) itemView.findViewById(R.id.timer);
//    itemRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_recycler_view);
//    orderItemAdapter = new OrderItemListAdapter(mContext, orderItemList);
//    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
//    itemRecyclerView.setLayoutManager(mLayoutManager);
//    itemRecyclerView.setItemAnimator(new DefaultItemAnimator());
//    itemRecyclerView.setAdapter(orderItemAdapter);


    tvNote = (TextView) itemView.findViewById(R.id.tv_note);
    tvSubTotal = (TextView) itemView.findViewById(R.id.tv_subtotal);
    tvVatText = (TextView) itemView.findViewById(R.id.tv_vat_txt);
    tvVatPrice = (TextView) itemView.findViewById(R.id.vatprice);
    tvTotal = (TextView) itemView.findViewById(R.id.tv_total);
    btnChangeStatus = (Button) itemView.findViewById(R.id.btn_change_status);
    tvCancelOrder = (TextView) itemView.findViewById(R.id.tv_cancel_order);
    tvTagLine = (TextView) itemView.findViewById(R.id.txt_tag_line);
    btnCall = (Button) itemView.findViewById(R.id.call);

  }

  public void setCustomerName(String name) {
    tvCustomerName.setText(name);
  }
}
