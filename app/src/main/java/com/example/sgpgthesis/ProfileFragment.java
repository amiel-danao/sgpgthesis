package com.example.sgpgthesis;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sgpgthesis.databinding.FragmentProfileBinding;
import com.example.sgpgthesis.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private UserModel userModel;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase db;
    private Uri uri;
    private FragmentProfileBinding binding;
    private View root;
    private FirebaseDatabase database;

    public ProfileFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            userModel = bundle.getParcelable("user");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false);
        root=binding.getRoot();
//        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        database = FirebaseDatabase.getInstance();

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        binding.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    saveUserProfile();
                }
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateUIProfile(userModel);
    }

    private boolean isFormValid(){
        if (TextUtils.isEmpty(binding.profileName.getText().toString())) {
            binding.profileName.setError("Invalid name!");
            return false;
        }

        if (TextUtils.isEmpty(binding.profileEmail.getText().toString())) {
            binding.profileEmail.setError("Email is Empty!");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.profileEmail.getText().toString()).matches()){
            binding.profileEmail.setError("Invalid Email!");
            return false;
        }


        return true;
    }

    private void saveUserProfile(){
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userModel.setName(binding.profileName.getText().toString());
        userModel.setAddress(binding.profileAdd.getText().toString());
        userModel.setPhoneNumber(binding.profileNumber.getText().toString());

        database.getReference().child("Users").child(id).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    saveUserEmail();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void saveUserEmail() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (!user.getEmail().equals(binding.profileEmail.getText().toString())) {

            user.updateEmail(binding.profileEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getActivity().getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                updateProfilePicture();
                            }
                            else{
                                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                database.getReference().child("Users").child(id).child("email").setValue(binding.profileEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        updateProfilePicture();
                                    }
                                });
                            }

                        }
                    });
        }
        else{
            updateProfilePicture();
        }
    }

    private void updateProfilePicture(){
        if (uri == null){
            updateProfileComplete();
            return;
        }

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("profile_picture/" + id + ".jpg");
        UploadTask uploadTask = riversRef.putFile(uri);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                String profilePicturePath = String.format("%s/%s.jpg", "profile_picture", id);
                FirebaseStorage.getInstance().getReference().child(profilePicturePath).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String profilePicUrl = task.getResult().toString();

                        database.getReference().child("Users").child(id).child("profileImg").setValue(profilePicUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updateProfileComplete();
                            }
                        });
                    }
                });
            }
        });
    }

    private void updateProfileComplete(){
        binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity().getApplicationContext(), "Profile was updated successfully.", Toast.LENGTH_LONG).show();
    }

    private void updateUIProfile(UserModel userModel) {
        if (userModel == null){
            return;
        }
        binding.profileName.setText(userModel.getName());
        binding.profileEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        binding.profileNumber.setText(userModel.getPhoneNumber());
        binding.profileAdd.setText(userModel.getAddress());
        Glide.with(getContext()).load(userModel.getProfileImg()).into(binding.profileImg);
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
                        Glide.with(getActivity().getApplicationContext()).load(profileUri).into(binding.profileImg);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Error selecting an image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

}