package com.example.sgpgthesis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sgpgthesis.adapters.CustomizedExpandableListAdapter;
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
import java.util.HashMap;
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

    ExpandableListView expandableListViewExample;
    ExpandableListAdapter expandableListAdapter;

    List<OrderModel> expandableTitleList;
    HashMap<OrderModel, List<HashMap<String, Object>>> expandableDetailList;


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

        checkOut = root.findViewById(R.id.check_out);
        expandableListViewExample = root.findViewById(R.id.expandableListViewSample);

        expandableTitleList = new ArrayList<>();
        expandableDetailList = new HashMap<>();



        db.collection("Orders").document(auth.getCurrentUser().getUid())
                .collection("CurrentUser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                                OrderModel order = documentSnapshot.toObject(OrderModel.class);
                                String documentId = documentSnapshot.getId();
                                order.setDocumentId(documentId);

                                expandableTitleList.add(order);
                                expandableDetailList.put(order, order.getItems());
                            }
                            expandableListViewExample.invalidateViews();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

        expandableListAdapter = new CustomizedExpandableListAdapter(getActivity(), expandableTitleList, expandableDetailList);
        expandableListViewExample.setAdapter(expandableListAdapter);



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