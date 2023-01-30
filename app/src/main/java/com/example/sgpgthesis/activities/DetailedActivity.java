package com.example.sgpgthesis.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.models.ViewAllModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailedActivity extends AppCompatActivity {

    ImageView detailedImg;
    TextView price,rating,description,quantity;
    Button addToCart,uploadPic, removeDesignButton;
    ImageView addItem,removeItem,image;

    private String id,title;
    private Uri uri;

    int totalQuantity = 1;
    int totalPrice = 0;

    Toolbar toolbar;

    FirebaseFirestore db;
    FirebaseAuth auth;

    ViewAllModel viewAllModel = null;
    Context context;
    View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = getApplicationContext();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final Object object = getIntent().getSerializableExtra("detail");
        if(object instanceof ViewAllModel){
            viewAllModel = (ViewAllModel) object;
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

        uploadPic = findViewById(R.id.uploadPic);
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

        if (viewAllModel != null){
            Glide.with(getApplicationContext()).load(viewAllModel.getImg_url()).into(detailedImg);
            rating.setText(viewAllModel.getRating());
            description.setText(viewAllModel.getDescription());
            price.setText("Price: ₱" + viewAllModel.getPrice());

            totalPrice = viewAllModel.getPrice() * totalQuantity;
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
                totalPrice = viewAllModel.getPrice() * totalQuantity;
                quantity.setText(String.valueOf(totalQuantity));
            }
        });

        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalQuantity = Math.max(1, totalQuantity-1);
                totalPrice = viewAllModel.getPrice() * totalQuantity;
                quantity.setText(String.valueOf(totalQuantity));
            }
        });
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
                    map.put("productImage", viewAllModel.getImg_url());
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

        final HashMap<String,Object> cartMap = new HashMap<>();

        cartMap.put("productName",viewAllModel.getName());
        cartMap.put("productPrice",price.getText().toString());
        cartMap.put("productDescription", viewAllModel.getDescription());
        cartMap.put("currentDate",saveCurrentDate);
        cartMap.put("currentTime",saveCurrentTime);
        cartMap.put("totalQuantity", String.valueOf(totalQuantity));
        cartMap.put("totalPrice", totalPrice);

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