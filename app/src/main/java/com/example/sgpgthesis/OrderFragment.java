package com.example.sgpgthesis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sgpgthesis.adapters.OrderAdapter;
import com.example.sgpgthesis.models.CartModel;
import com.example.sgpgthesis.models.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;

    TextView overTotalAmount;
    int totalBill;
    Button checkOut;

    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    List<OrderModel> list;

    ProgressBar progressBar;


    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_orders, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        overTotalAmount = root.findViewById(R.id.textView7);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
        checkOut = root.findViewById(R.id.check_out);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        orderAdapter = new OrderAdapter(getActivity(), this, list);
        recyclerView.setAdapter(orderAdapter);

        db.collection("Orders").document(auth.getCurrentUser().getUid())
                .collection("CurrentUser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){



                                OrderModel order = documentSnapshot.toObject(OrderModel.class);
                                String documentId = documentSnapshot.getId();
                                order.setDocumentId(documentId);

                                list.add(order);
//                                CartModel cartModel = documentSnapshot.toObject(CartModel.class);
//                                cartModel.setDocumentId(documentId);
//
//                                list.add(cartModel);
//                                cartAdapter.notifyDataSetChanged();
//                                progressBar.setVisibility(View.GONE);
//                                recyclerView.setVisibility(View.VISIBLE);
                            }

                            orderAdapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

//                            calculateTotalAmount(list);
                        }
                    }
                });


        return root;
    }

    public void calculateTotalAmount(List<CartModel> list) {
        double totalAmount = 0.0;
        for (CartModel cartModel : list){
            totalAmount += cartModel.getTotalPrice();
        }

        overTotalAmount.setText("Total Amount: ₱" + totalAmount);
    }
}