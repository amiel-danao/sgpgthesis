package com.example.sgpgthesis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.model.Model;
import com.example.sgpgthesis.CartFragment;
import com.example.sgpgthesis.OrderDetailFragment;
import com.example.sgpgthesis.OrderFragment;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.models.CartModel;
import com.example.sgpgthesis.models.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    Context context;
    List<OrderModel> list;

    int totalPrice = 0;

    FirebaseFirestore db;
    FirebaseAuth auth;
    OrderFragment orderFragment;

    public OrderAdapter(Context context, OrderFragment orderFragment, List<OrderModel> list) {
        this.context = context;
        this.list = list;
        this.orderFragment = orderFragment;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = list.get(position);
//        holder.order_id.setText(order.getDocumentId());
//        if (list.get(position).getOrder_date() != null) {
//            holder.date.setText(order.getOrder_date().toString());
//        }
        Map<String, Object> first_item = order.getItems().get(0);

        if (first_item != null) {
            if (first_item.containsKey("productName")) {
                holder.first_product_name.setText(first_item.get("productName").toString());
            }
            else{
                holder.first_product_name.setText(order.getDocumentId());
            }
        }
        else{
            holder.first_product_name.setText(order.getDocumentId());
        }
        holder.order_status.setText(order.getStatus());
        double total_price = order.getTotalPrice();
        holder.totalPrice.setText(context.getResources().getString(R.string.peso_sign, String.format(Locale.ENGLISH, "%.2f", total_price)));

        String plural = "";
        if (list.size() > 1){
            plural = "s";
        }
        holder.item_count.setText(context.getResources().getString(R.string.item_label,  list.size(), plural));


    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView order_id, item_count, first_product_name, order_status, totalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            first_product_name = itemView.findViewById(R.id.product_name);
            item_count = itemView.findViewById(R.id.item_count);
//            date = itemView.findViewById(R.id.order_date);
            order_status = itemView.findViewById(R.id.order_status);
            totalPrice = itemView.findViewById(R.id.total_price);
        }
    }
}
