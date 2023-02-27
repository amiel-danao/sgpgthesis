package com.example.sgpgthesis;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sgpgthesis.models.OrderModel;

public class OrderDetailFragment extends Fragment {

    private OrderModel mViewModel;

    public static OrderDetailFragment newInstance() {
        return new OrderDetailFragment();
    }

    public void setObjectFunction(OrderModel obj){this.mViewModel = obj;};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

//        mViewModel = new ViewModelProvider(this).get(OrderModel.class);

//        Intent i = getActivity().getIntent();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mViewModel = (OrderModel) bundle.getParcelable("order");//(OrderModel)i.getSerializableExtra("order");
        }

        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }
}