package com.example.sgpgthesis.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.sgpgthesis.MainActivity;
import com.example.sgpgthesis.OrderDetailFragment;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.activities.DrinkwareDetailsActivity;
import com.example.sgpgthesis.models.OrderModel;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CustomizedExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<OrderModel> expandableTitleList;
    private HashMap<OrderModel, List<HashMap<String, Object>>> expandableDetailList;
    private Fragment parent;
    // constructor
    public CustomizedExpandableListAdapter(Context context, Fragment parent, List<OrderModel> expandableListTitle,
                                           HashMap<OrderModel, List<HashMap<String, Object>>> expandableListDetail) {
        this.context = context;
        this.parent = parent;
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
        ImageView imageDesignPreviewButton = convertView.findViewById(R.id.imageDesign);

        if (cartItem.containsKey("image")) {
            imageDesignPreviewButton.setTag(cartItem);
            imageDesignPreviewButton.setOnClickListener(designPreviewClickListener);
            Glide.with(context).load(cartItem.get("image")).into(imageDesignPreviewButton);
        }

        ImageView imageProductPreviewButton = convertView.findViewById(R.id.imageProduct);

        if (cartItem.containsKey("productImage")) {
            imageProductPreviewButton.setTag(cartItem);
            imageProductPreviewButton.setOnClickListener(productPreviewClickListener);
            Glide.with(context).load(cartItem.get("productImage")).into(imageProductPreviewButton);
        }

        TextView product_name = convertView.findViewById(R.id.product_name);
        TextView product_price = convertView.findViewById(R.id.product_price);
//        TextView product_date = convertView.findViewById(R.id.product_date);
//        TextView product_time = convertView.findViewById(R.id.product_time);
        TextView product_quantity = convertView.findViewById(R.id.total_quantity);
        TextView total_price = convertView.findViewById(R.id.total_price);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            product_name.setText(cartItem.getOrDefault("productName", "").toString());
            product_price.setText(cartItem.getOrDefault("productPrice", "").toString());
//            product_date.setText(cartItem.getOrDefault("currentDate", "").toString());
//            product_time.setText(cartItem.getOrDefault("currentTime", "").toString());
            product_quantity.setText(cartItem.getOrDefault("totalQuantity", "").toString());
            total_price.setText(cartItem.getOrDefault("totalPrice", "").toString());
        }
        else{
            if (cartItem.containsKey("productName")){
                product_name.setText(cartItem.get("productName").toString());
                product_price.setText(cartItem.get("productPrice").toString());
//                product_date.setText(cartItem.get("currentDate").toString());
//                product_time.setText(cartItem.get("currentTime").toString());
                product_quantity.setText(cartItem.get("totalQuantity").toString());
                total_price.setText(cartItem.get("totalPrice").toString());
            }
        }
        double totalPrice = ((Long) cartItem.get("totalPrice")).doubleValue();
        total_price.setText(context.getResources().getString(R.string.peso_sign, String.format(Locale.ENGLISH, "%.2f", totalPrice)));

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


        TextView order_id = (TextView) convertView.findViewById(R.id.product_name);
        order_id.setTypeface(null, Typeface.BOLD);
        order_id.setText(order.getDocumentId());
//
        TextView order_date = (TextView) convertView.findViewById(R.id.order_date);
        if (order.getOrder_date() != null) {
            SimpleDateFormat currentDate = new SimpleDateFormat("EEE, d MMM yyyy");
            String currentDateString = currentDate.format(order.getOrder_date().getTime());
            order_date.setText(context.getResources().getString(R.string.order_date, currentDateString));
        }
//
        TextView status = (TextView) convertView.findViewById(R.id.order_status);
        status.setText(order.getStatus());

        String plural = "";
        if (order.getItems().size() > 1){
            plural = "s";
        }
        TextView item_count = convertView.findViewById(R.id.item_count);
        item_count.setText(context.getResources().getString(R.string.item_label, order.getItems().size(), plural));

        TextView total = convertView.findViewById(R.id.total_price);
        double totalPrice = order.getTotalPrice();
        total.setText(context.getResources().getString(R.string.peso_sign, String.format(Locale.ENGLISH, "%.2f", totalPrice)));
//        total.setText(String.valueOf(order.getTotalPrice()));

//        convertView.setTag(order);
//        convertView.setOnClickListener(order_view_click);

        return convertView;
    }

    private View.OnClickListener order_view_click  = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            OrderModel order = (OrderModel)view.getTag();
//            parent.getActivity().getIntent().putExtra("order", (Serializable)order);
            Fragment fragment = null;
            try {
                fragment = (Fragment) OrderDetailFragment.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            fragment.setObjectFunction(order);
            Bundle bundle = new Bundle();
            bundle.putParcelable("order", order);
            fragment.setArguments(bundle);
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = parent.getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, fragment)
                    .commit();
        }
    };


    private View.OnClickListener productPreviewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            HashMap<String, Object> cartItem = (HashMap<String, Object>) view.getTag();

            if (cartItem.containsKey("productImage")) {
                showProductDesign(cartItem.get("productImage").toString());
            }
            else{
                Toast.makeText(context, "This item has no product image", Toast.LENGTH_SHORT).show();
            }
        }
    };

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
