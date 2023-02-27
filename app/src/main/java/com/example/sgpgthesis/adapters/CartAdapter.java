package com.example.sgpgthesis.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sgpgthesis.CartFragment;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.models.CartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<CartModel> list;

    int totalPrice = 0;

    FirebaseFirestore db;
    FirebaseAuth auth;
    CartFragment cartFragment;

    public CartAdapter(Context context, CartFragment cartFragment, List<CartModel> list) {
        this.context = context;
        this.list = list;
        this.cartFragment = cartFragment;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartModel item = list.get(position);
        holder.name.setText(item.getProductName());
        holder.description.setText(item.getProductDescription());

        if (item.getOrigPrice() != item.getTotalPrice()) {
            holder.price.setText(String.valueOf(context.getResources().getString(R.string.peso_sign, String.format(Locale.ENGLISH, "%.2f", item.getOrigPrice()))));
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.price.setVisibility(View.VISIBLE);
        }
        else{
            holder.price.setVisibility(View.GONE);
        }

        holder.quantity.setText(item.getTotalQuantity());
        double total_price = item.getTotalPrice();
        holder.totalPrice.setText(context.getResources().getString(R.string.peso_sign, String.format(Locale.ENGLISH, "%.2f", total_price)));

        Glide.with(context).load(item.getProductImage()).into(holder.imageProduct);
        Glide.with(context).load(item.getImage()).into(holder.imageDesign);


        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("CurrentUser")
                        .document(item.getDocumentId())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    list.remove(list.get(holder.getAdapterPosition()));
                                    cartFragment.calculateTotalAmount(list);
                                    notifyDataSetChanged();
                                    Toast.makeText(context,"Item Deleted", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(context,"Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, description, price, quantity, totalPrice;
        ImageView deleteItem, imageProduct, imageDesign;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            description = itemView.findViewById(R.id.product_description);
            price = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.total_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            deleteItem = itemView.findViewById(R.id.delete);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            imageDesign = itemView.findViewById(R.id.imageDesign);
        }
    }
}
