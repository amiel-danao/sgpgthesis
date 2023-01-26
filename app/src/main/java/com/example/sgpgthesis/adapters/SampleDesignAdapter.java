package com.example.sgpgthesis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.models.SampleDesignModel;

import java.util.List;

public class SampleDesignAdapter extends RecyclerView.Adapter<SampleDesignAdapter.ViewHolder> {

    private Context context;
    private List<SampleDesignModel> sampleDesignModelList;

    public SampleDesignAdapter(Context context, List<SampleDesignModel> sampleDesignModelList) {
        this.context = context;
        this.sampleDesignModelList = sampleDesignModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_design,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(sampleDesignModelList.get(position).getImg_url()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return sampleDesignModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.sample_img);
        }
    }
}
