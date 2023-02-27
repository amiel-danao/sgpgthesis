package com.example.sgpgthesis.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.activities.ViewAllActivity;
import com.example.sgpgthesis.models.DrinkWareModel;

import org.checkerframework.checker.signature.qual.ClassGetSimpleName;

import java.util.List;

public class DrinkWareAdapter extends RecyclerView.Adapter<DrinkWareAdapter.ViewHolder> {

    Context context;
    List<DrinkWareModel> drinkWareList;

    public DrinkWareAdapter(Context context, List<DrinkWareModel> drinkWareList) {
        this.context = context;
        this.drinkWareList = drinkWareList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(drinkWareList.get(position).getImg_url()).into(holder.imageView);
        holder.name.setText(drinkWareList.get(position).getName());
        holder.description.setText(drinkWareList.get(position).getDescription());
        holder.rating.setText(drinkWareList.get(position).getRating());
        holder.price.setText("₱"+drinkWareList.get(position).getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ViewAllActivity.class);
                intent.putExtra("type", drinkWareList.get(holder.getAdapterPosition()).getType());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return drinkWareList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name,description,rating,price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.cat_img);
            name = itemView.findViewById(R.id.cat_name);
            description = itemView.findViewById(R.id.cat_desc);
            price = itemView.findViewById(R.id.cat_price);
            rating = itemView.findViewById(R.id.cat_rating);
        }
    }
}
