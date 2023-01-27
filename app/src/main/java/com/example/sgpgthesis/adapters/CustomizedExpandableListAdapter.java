package com.example.sgpgthesis.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.activities.DrinkwareDetailsActivity;
import com.example.sgpgthesis.models.OrderModel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class CustomizedExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<OrderModel> expandableTitleList;
    private HashMap<OrderModel, List<HashMap<String, Object>>> expandableDetailList;

    // constructor
    public CustomizedExpandableListAdapter(Context context, List<OrderModel> expandableListTitle,
                                           HashMap<OrderModel, List<HashMap<String, Object>>> expandableListDetail) {
        this.context = context;
        this.expandableTitleList = expandableListTitle;
        this.expandableDetailList = expandableListDetail;
    }

    @Override
    // Gets the data associated with the given child within the given group.
    public Object getChild(int lstPosn, int expanded_ListPosition) {
        return this.expandableDetailList.get(this.expandableTitleList.get(lstPosn)).get(expanded_ListPosition);
    }

    @Override
    // Gets the ID for the given child within the given group.
    // This ID must be unique across all children within the group. Hence we can pick the child uniquely
    public long getChildId(int listPosition, int expanded_ListPosition) {
        return expanded_ListPosition;
    }

    @Override
    // Gets a View that displays the data for the given child within the given group.
    public View getChildView(int lstPosn, final int expanded_ListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final HashMap<String, Object> cartItem = (HashMap<String, Object>) getChild(lstPosn, expanded_ListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.cart_item, null);
        }

        convertView.setPadding(10, 10, 10, 10);
        convertView.findViewById(R.id.delete).setVisibility(View.GONE);
        ImageView imageDesignPreviewButton = convertView.findViewById(R.id.design_preview_button);

        if (cartItem.containsKey("image")) {
            imageDesignPreviewButton.setVisibility(View.VISIBLE);
            imageDesignPreviewButton.setTag(cartItem);
            imageDesignPreviewButton.setOnClickListener(designPreviewClickListener);
        }
        TextView product_name = convertView.findViewById(R.id.product_name);
        TextView product_price = convertView.findViewById(R.id.product_price);
        TextView product_date = convertView.findViewById(R.id.product_date);
        TextView product_time = convertView.findViewById(R.id.product_time);
        TextView product_quantity = convertView.findViewById(R.id.total_quantity);
        TextView total_price = convertView.findViewById(R.id.total_price);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            product_name.setText(cartItem.getOrDefault("productName", "").toString());
            product_price.setText(cartItem.getOrDefault("productPrice", "").toString());
            product_date.setText(cartItem.getOrDefault("currentDate", "").toString());
            product_time.setText(cartItem.getOrDefault("currentTime", "").toString());
            product_quantity.setText(cartItem.getOrDefault("totalQuantity", "").toString());
            total_price.setText(cartItem.getOrDefault("totalPrice", "").toString());
        }
        else{
            if (cartItem.containsKey("productName")){
                product_name.setText(cartItem.get("productName").toString());
                product_price.setText(cartItem.get("productPrice").toString());
                product_date.setText(cartItem.get("currentDate").toString());
                product_time.setText(cartItem.get("currentTime").toString());
                product_quantity.setText(cartItem.get("totalQuantity").toString());
                total_price.setText(cartItem.get("totalPrice").toString());
            }
        }
        return convertView;
    }

    @Override
    // Gets the number of children in a specified group.
    public int getChildrenCount(int listPosition) {
        return this.expandableDetailList.get(this.expandableTitleList.get(listPosition)).size();
    }

    @Override
    // Gets the data associated with the given group.
    public Object getGroup(int listPosition) {
        return this.expandableTitleList.get(listPosition);
    }

    @Override
    // Gets the number of groups.
    public int getGroupCount() {
        return this.expandableTitleList.size();
    }

    @Override
    // Gets the ID for the group at the given position. This group ID must be unique across groups.
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    // Gets a View that displays the given group.
    // This View is only for the group--the Views for the group's children
    // will be fetched using getChildView()
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        OrderModel order = (OrderModel)getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.order_item, null);
        }
        TextView order_id = (TextView) convertView.findViewById(R.id.order_id);
        order_id.setTypeface(null, Typeface.BOLD);
        order_id.setText(order.getDocumentId());

        TextView order_date = (TextView) convertView.findViewById(R.id.order_date);
        if (order.getOrder_date() != null) {
            SimpleDateFormat currentDate = new SimpleDateFormat("EEE, d MMM yyyy");
            String currentDateString = currentDate.format(order.getOrder_date().getTime());
            order_date.setText(currentDateString);
        }

        TextView status = (TextView) convertView.findViewById(R.id.status);
        status.setText(order.getStatus());

        TextView total = convertView.findViewById(R.id.total_price);
        total.setText(String.valueOf(order.getTotalPrice()));

        return convertView;
    }

    private View.OnClickListener designPreviewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            HashMap<String, Object> cartItem = (HashMap<String, Object>) view.getTag();

            if (cartItem.containsKey("image")) {
                showProductDesign(cartItem.get("image").toString());
            }
            else{
                Toast.makeText(context, "This item has no design", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void showProductDesign(String photoUri){
        AlertDialog.Builder popupDialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = (LayoutInflater) this.context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.alert_design_preview, null);


        ImageView popupImv = dialogView.findViewById(R.id.design_preview_pop_up);
        Glide.with(context).load(photoUri)
                .into(popupImv);
        popupDialogBuilder.setView(popupImv);
        AlertDialog alertDialog = popupDialogBuilder.create();

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    @Override
    // Indicates whether the child and group IDs are stable across changes to the underlying data.
    public boolean hasStableIds() {
        return false;
    }

    @Override
    // Whether the child at the specified position is selectable.
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
