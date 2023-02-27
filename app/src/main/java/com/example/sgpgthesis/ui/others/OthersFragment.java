package com.example.sgpgthesis.ui.others;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sgpgthesis.R;
import com.example.sgpgthesis.adapters.NavDrinkwareAdapter;
import com.example.sgpgthesis.adapters.NavOthersAdapter;
import com.example.sgpgthesis.models.NavDrinkwareModel;
import com.example.sgpgthesis.models.NavOthersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OthersFragment extends Fragment {

    RecyclerView recyclerView;
    List<NavOthersModel> othersModelList;
    NavOthersAdapter navOthersAdapter;
    FirebaseFirestore db;

    ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_others,container,false);

        db = FirebaseFirestore.getInstance();
        recyclerView = root.findViewById(R.id.category_rec);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        othersModelList = new ArrayList<>();
        navOthersAdapter = new NavOthersAdapter(getActivity(),othersModelList);
        recyclerView.setAdapter(navOthersAdapter);

        db.collection("NavOthers")
                .whereEqualTo("active", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NavOthersModel navOthersModel = document.toObject(NavOthersModel.class);
                                navOthersModel.setId(document.getId());
                                if (!navOthersModel.getName().isEmpty()) {
                                    othersModelList.add(navOthersModel);
                                }
                            }
                            navOthersAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
        return root;
    }
}