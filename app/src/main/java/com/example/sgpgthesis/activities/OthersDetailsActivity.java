package com.example.sgpgthesis.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.adapters.DiscountAdapter;
import com.example.sgpgthesis.adapters.NavOthersAdapter;
import com.example.sgpgthesis.models.Discount;
import com.example.sgpgthesis.models.NavDrinkwareModel;
import com.example.sgpgthesis.models.NavOthersModel;
import com.google.android.gms.cast.framework.media.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;



public class OthersDetailsActivity extends AppCompatActivity {

    ImageView detailedImg;
    TextView price,rating,description,quantity;
    Button addToCart,removeDesignButton;
    ImageView addItem,removeItem,image;

    private String id,title;
    private Uri uri;

    int totalQuantity = 1;
    float totalPrice = 0;
    float origPrice = 0;

    Toolbar toolbar;

    FirebaseFirestore db;
    FirebaseAuth auth;

    NavOthersModel navOthersModel = null;
    Context context;
    View loadingIndicator;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private DiscountAdapter mAdapter;
    protected ArrayList<Discount> discounts;
    private TextView discountedPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_details);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = getApplicationContext();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final Object object = getIntent().getSerializableExtra("detail");
        if(object instanceof NavOthersModel){
            navOthersModel = (NavOthersModel) object;
        }

        quantity = findViewById(R.id.quantity);

        image = findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
//                startActivityForResult(intent, 100);
            }
        });

        removeDesignButton = findViewById(R.id.removeDesignButton);
        removeDesignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uri = null;
                image.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_upload));
            }
        });

        detailedImg = findViewById(R.id.detailed_img);
        addItem = findViewById(R.id.add_item);
        removeItem = findViewById(R.id.remove_item);

        price = findViewById(R.id.detailed_price);
        rating = findViewById(R.id.detailed_rating);
        description = findViewById(R.id.detailed_desc);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        discountedPrice = findViewById(R.id.discounted_price);

        if (navOthersModel != null){
            Glide.with(getApplicationContext()).load(navOthersModel.getImg_url()).into(detailedImg);
            rating.setText(navOthersModel.getRating());
            description.setText(navOthersModel.getDescription());
            price.setText("Price: ₱" + navOthersModel.getPrice());

            totalPrice = navOthersModel.getPrice() * totalQuantity;
        }

        addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addedToCart();
            }
        });
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalQuantity = Math.min(10, totalQuantity+1);
                totalPrice = navOthersModel.getPrice() * totalQuantity;
                quantity.setText(String.valueOf(totalQuantity));
                updateActualPriceWithDiscount();
            }
        });

        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalQuantity = Math.max(1, totalQuantity-1);
                totalPrice = navOthersModel.getPrice() * totalQuantity;
                quantity.setText(String.valueOf(totalQuantity));
                updateActualPriceWithDiscount();
            }
        });

        discounts = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.discountRecyclerView);
        db.collection("Discounts")
                .whereEqualTo("id", navOthersModel.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                            Discount discount = documentSnapshot.toObject(Discount.class);
                            discounts.add(discount);
                        }
                        if (!discounts.isEmpty()) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mAdapter = new DiscountAdapter(discounts);
                            // Set CustomAdapter as the adapter for RecyclerView.
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }
                });
    }

    void updateActualPriceWithDiscount(){
        discountedPrice.setVisibility(View.GONE);
        price.setPaintFlags(price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        price.setTextColor(Color.BLACK);
        int qty = Integer.parseInt(quantity.getText().toString());
        totalPrice = navOthersModel.getPrice() * qty;
        origPrice = totalPrice;
        price.setText("Price: ₱" + origPrice);
        if (discounts.isEmpty()){
            return;
        }

        float overallDiscountPercent = 0;

        for (Discount discount : discounts) {
            if(discount.getMin_quantity() <= qty && discount.getMax_quantity() >= qty){
                overallDiscountPercent += discount.getPercent();
            }
        }
        if (overallDiscountPercent <= 0){
            return;
        }

        totalPrice = Math.max(0, origPrice - (origPrice * (overallDiscountPercent/100)));
        discountedPrice.setText("Price: ₱" + totalPrice);
        discountedPrice.setVisibility(View.VISIBLE);
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        price.setTextColor(Color.RED);
    }

    ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();

                        if (data.getData() != null){
                            Uri profileUri = data.getData();
                            uri = profileUri;
                            Glide.with(getApplicationContext()).load(profileUri).into(image);
                        } else {
                            Toast.makeText(context, "Error selecting an image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private void uploadImage(String cartItemId) {
        String path = String.format("%s/%s/%s.png", "product_designs", auth.getUid(), cartItemId);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);

        UploadTask uploadTask = storageReference.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("image", downloadUri.toString());
                    map.put("productImage", navOthersModel.getImg_url());
                    FirebaseFirestore.getInstance()
                            .collection("AddToCart")
                            .document(auth.getUid())
                            .collection("CurrentUser")
                            .document(cartItemId)
                            .set(map, SetOptions.merge());

                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();

                    DoneAddToCart();
                } else {
                    // Handle failures
                    // ...
                    Toast.makeText(context, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addedToCart() {
        loadingIndicator.setVisibility(View.VISIBLE);

        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("EEE, d MMM yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("productName", navOthersModel.getName());
        cartMap.put("productPrice", price.getText().toString());
        cartMap.put("productDescription", navOthersModel.getDescription());
        cartMap.put("currentDate", saveCurrentDate);
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("totalQuantity", String.valueOf(totalQuantity));
        cartMap.put("totalPrice", totalPrice);
        cartMap.put("origPrice", origPrice);
        cartMap.put("productImage", navOthersModel.getImg_url());

        db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("CurrentUser").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (uri == null){
                            DoneAddToCart();
                        }
                        else{
                            uploadImage(task.getResult().getId());
                        }

                    }
                });
    }

    private void DoneAddToCart(){
        loadingIndicator.setVisibility(View.GONE);
        Toast.makeText(context, "Added To A Cart", Toast.LENGTH_SHORT).show();
        finish();
    }
}