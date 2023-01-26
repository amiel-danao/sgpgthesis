package com.example.sgpgthesis.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sgpgthesis.R;
import com.example.sgpgthesis.activities.HomeActivity;
import com.example.sgpgthesis.activities.LoginActivity;
import com.example.sgpgthesis.activities.RegisterActivity;
import com.example.sgpgthesis.activities.ViewAllActivity;
import com.example.sgpgthesis.adapters.DrinkWareAdapter;
import com.example.sgpgthesis.adapters.OthersAdapter;
import com.example.sgpgthesis.adapters.SampleDesignAdapter;
import com.example.sgpgthesis.adapters.ViewAllAdapter;
import com.example.sgpgthesis.models.DrinkWareModel;
import com.example.sgpgthesis.models.OthersModel;
import com.example.sgpgthesis.models.SampleDesignModel;
import com.example.sgpgthesis.models.ViewAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    ScrollView scrollView;
    ProgressBar progressBar;
    TextView viewDrinkWare,viewOthers;

    RecyclerView sampleDesignRec, drinkWareRec, othersRec;
    FirebaseFirestore db;

    //sample design
    List<SampleDesignModel> sampleDesignModelList;
    SampleDesignAdapter sampleDesignAdapter;

    //search
    EditText search_box;
    private List<ViewAllModel> viewAllModelList;
    private RecyclerView recyclerViewSearch;
    private ViewAllAdapter viewAllAdapter;

    //drink ware product
    List<DrinkWareModel> drinkWareList;
    DrinkWareAdapter drinkWareAdapter;

    //other products
    List<OthersModel> othersModelList;
    OthersAdapter othersAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home,container,false);

        db = FirebaseFirestore.getInstance();

        viewDrinkWare = root.findViewById(R.id.view_all_drinkware);

        sampleDesignRec = root.findViewById(R.id.design_recycler);
        drinkWareRec = root.findViewById(R.id.drinkware_recycler);
        othersRec = root.findViewById(R.id.other_recycler);
        scrollView = root.findViewById(R.id.scrollView);
        progressBar = root.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        //sample designs
        sampleDesignRec.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        sampleDesignModelList = new ArrayList<>();
        sampleDesignAdapter = new SampleDesignAdapter(getActivity(),sampleDesignModelList);
        sampleDesignRec.setAdapter(sampleDesignAdapter);

        db.collection("SampleDesign")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SampleDesignModel sampleDesignModel = document.toObject(SampleDesignModel.class);
                                sampleDesignModelList.add(sampleDesignModel);
                                sampleDesignAdapter.notifyDataSetChanged();

                                progressBar.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //drink ware
        drinkWareRec.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        drinkWareList = new ArrayList<>();
        drinkWareAdapter = new DrinkWareAdapter(getActivity(),drinkWareList);
        drinkWareRec.setAdapter(drinkWareAdapter);

        db.collection("Drinkware")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DrinkWareModel drinkWareModel = document.toObject(DrinkWareModel.class);
                                drinkWareList.add(drinkWareModel);
                                drinkWareAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //others
        othersRec.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        othersModelList = new ArrayList<>();
        othersAdapter = new OthersAdapter(getActivity(),othersModelList);
        othersRec.setAdapter(othersAdapter);

        db.collection("Others")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                OthersModel othersModel = document.toObject(OthersModel.class);
                                othersModelList.add(othersModel);
                                othersAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //search
        recyclerViewSearch = root.findViewById(R.id.search_rec);
        search_box = root.findViewById(R.id.search_box);
        viewAllModelList = new ArrayList<>();
        viewAllAdapter = new ViewAllAdapter(getContext(),viewAllModelList);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearch.setAdapter(viewAllAdapter);
        recyclerViewSearch.setHasFixedSize(true);
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().isEmpty()){
                    viewAllModelList.clear();
                    viewAllAdapter.notifyDataSetChanged();
                } else {
                    searchProduct(editable.toString());
                }
            }
        });
        return root;
    }

    private void searchProduct(String name) {
        if(!name.isEmpty()){
            db.collection("AllProducts").whereEqualTo("name",name).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                viewAllModelList.clear();
                                viewAllAdapter.notifyDataSetChanged();
                                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                    ViewAllModel viewAllModel = doc.toObject(ViewAllModel.class);
                                    viewAllModelList.add(viewAllModel);
                                    viewAllAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }
    }
}