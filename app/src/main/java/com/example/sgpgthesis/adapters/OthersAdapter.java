package com.example.sgpgthesis.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.activities.ViewAllActivity;
import com.example.sgpgthesis.models.OthersModel;

import java.util.List;

public class OthersAdapter extends RecyclerView.Adapter<OthersAdapter.ViewHolder> {

    Context context;
    List<OthersModel> othersModelList;

    public OthersAdapter(Context context, List<OthersModel> othersModelList) {
        this.context = context;
        this.othersModelList = othersModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(othersModelList.get(position).getImg_url()).into(holder.imageView);
        holder.name.setText(othersModelList.get(position).getName());
        holder.description.setText(othersModelList.get(position).getDescription());
        holder.rating.setText(othersModelList.get(position).getRating());
        holder.price.setText("₱"+othersModelList.get(position).getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewAllActivity.class);
                intent.putExtra("type", othersModelList.get(holder.getAdapterPosition()).getType());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return othersModelList.size();
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
