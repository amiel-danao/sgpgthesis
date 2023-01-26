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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.model.Model;
import com.example.sgpgthesis.CartFragment;
import com.example.sgpgthesis.OrderFragment;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.models.CartModel;
import com.example.sgpgthesis.models.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

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
        holder.order_id.setText(list.get(position).getDocumentId());
        if (list.get(position).getOrder_date() != null) {
            holder.date.setText(list.get(position).getOrder_date().toString());
        }
        holder.status.setText(list.get(position).getStatus());
        holder.totalPrice.setText(String.valueOf(list.get(position).getTotalPrice()));


    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView order_id, date, status, totalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            order_id = itemView.findViewById(R.id.order_id);
            date = itemView.findViewById(R.id.order_date);
            status = itemView.findViewById(R.id.status);
            totalPrice = itemView.findViewById(R.id.total_price);
        }
    }
}
