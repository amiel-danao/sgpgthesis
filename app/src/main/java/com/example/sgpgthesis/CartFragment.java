package com.example.sgpgthesis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sgpgthesis.activities.CheckOutActivity;
import com.example.sgpgthesis.adapters.CartAdapter;
import com.example.sgpgthesis.models.CartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;

    TextView overTotalAmount;
    int totalBill;
    Button checkOut;

    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    List<CartModel> list;

    ProgressBar progressBar;


    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

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
        cartAdapter = new CartAdapter(getActivity(), this, list);
        recyclerView.setAdapter(cartAdapter);

        db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("CurrentUser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                                String documentId = documentSnapshot.getId();

                                CartModel cartModel = documentSnapshot.toObject(CartModel.class);
                                cartModel.setDocumentId(documentId);

                                list.add(cartModel);
                            }

                            cartAdapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                            calculateTotalAmount(list);
                        }
                    }
                });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.isEmpty()){
                    Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), CheckOutActivity.class);
                intent.putExtra("itemList", (Serializable) list);
                startActivity(intent);

            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cartAdapter != null){
            cartAdapter.notifyDataSetChanged();
        }
    }

    public void calculateTotalAmount(List<CartModel> list) {
        double totalAmount = 0.0;
        for (CartModel cartModel : list){
            totalAmount += cartModel.getTotalPrice();
        }

        overTotalAmount.setText("Total Amount: ₱" + totalAmount);
    }
}