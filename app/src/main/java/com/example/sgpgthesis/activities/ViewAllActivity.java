package com.example.sgpgthesis.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sgpgthesis.R;
import com.example.sgpgthesis.adapters.ViewAllAdapter;
import com.example.sgpgthesis.models.ViewAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;

    RecyclerView recyclerView;
    ViewAllAdapter viewAllAdapter;
    List<ViewAllModel> viewAllModelList;

    Toolbar toolbar;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        firebaseFirestore = FirebaseFirestore.getInstance();

        String type = getIntent().getStringExtra("type");
        recyclerView = findViewById(R.id.view_all_rec);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewAllModelList = new ArrayList<>();
        viewAllAdapter = new ViewAllAdapter(this, viewAllModelList);
        recyclerView.setAdapter(viewAllAdapter);

        //getting drinkware
        if (type != null && type.equalsIgnoreCase("drinkware")){
            firebaseFirestore.collection("AllProducts")
                    .whereEqualTo("active", true)
                    .whereEqualTo("type","drinkware").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                                ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                                viewAllModel.setId(documentSnapshot.getId());
                                if (!viewAllModel.getName().isEmpty()) {
                                    viewAllModelList.add(viewAllModel);
                                    viewAllAdapter.notifyDataSetChanged();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    });
        }

        //getting others
        if (type != null && type.equalsIgnoreCase("others")){
            firebaseFirestore.collection("AllProducts")
                    .whereEqualTo("type","others")
                    .whereEqualTo("active", true)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                                ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                                viewAllModel.setId(documentSnapshot.getId());
                                if (!viewAllModel.getName().isEmpty()) {
                                    viewAllModelList.add(viewAllModel);
                                    viewAllAdapter.notifyDataSetChanged();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }
}