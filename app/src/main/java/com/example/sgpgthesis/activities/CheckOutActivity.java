package com.example.sgpgthesis.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sgpgthesis.MainActivity;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.models.CartModel;
import com.example.sgpgthesis.models.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckOutActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;
    Context context;

    List<CartModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        context = getApplicationContext();

        list = (ArrayList<CartModel>) getIntent().getSerializableExtra("itemList");

        // Get a new write batch
        WriteBatch batch = db.batch();

        OrderModel newOrder = new OrderModel();

        if(list != null & list.size() > 0) {
            List<HashMap<String, Object>> items = new ArrayList<>();
            double totalPrice = 0;
            for(CartModel model : list) {
                final HashMap<String,Object> cartMap = new HashMap<>();

                cartMap.put("productName",model.getProductName());
                cartMap.put("productImage",model.getProductImage());
                cartMap.put("productDescription",model.getProductDescription());
                cartMap.put("productPrice",model.getProductPrice());
                cartMap.put("currentDate",model.getCurrentDate());
                cartMap.put("currentTime",model.getCurrentTime());
                cartMap.put("totalQuantity",model.getTotalQuantity());
                cartMap.put("totalPrice",model.getTotalPrice());
                cartMap.put("image",model.getImage());

                items.add(cartMap);
                totalPrice += model.getTotalPrice();


                DocumentReference cartRef = db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("CurrentUser").document(model.getDocumentId());
                batch.delete(cartRef);
            }

            newOrder.setItems(items);
            newOrder.setStatus("Pending");
            newOrder.setTotalPrice(totalPrice);
            DocumentReference newOrderRef = db.collection("Orders").document(auth.getCurrentUser().getUid())
                    .collection("CurrentUser").document();
            batch.set(newOrderRef, newOrder, SetOptions.merge());
            // Commit the batch
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // ...
                    if (task.isSuccessful()){
                        Toast.makeText(context, "Your Order Has Been Placed", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
//        super.onBackPressed();
    }
}