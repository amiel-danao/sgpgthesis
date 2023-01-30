package com.example.sgpgthesis.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.example.sgpgthesis.databinding.ActivityRegisterBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    ActivityRegisterBinding binding;
    private Uri uri;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);

        binding= DataBindingUtil.setContentView(this,R.layout.activity_register);
        context = getApplicationContext();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.progressBar.setVisibility(View.GONE);

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFormValid()) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    createUser();
                }
            }
        });

        binding.passwordToggle.setOnCheckedChangeListener(showPasswordListener);
        binding.confirmPasswordToggle.setOnCheckedChangeListener(showPasswordListener);

        binding.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
//                startActivityForResult(intent, 100);
            }
        });

    }

    CompoundButton.OnCheckedChangeListener showPasswordListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // Show password
                if (buttonView.equals(binding.confirmPasswordToggle)){
                    binding.confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.confirmPassword.setSelection(binding.confirmPassword.getText().length());
                }
                else{
                    binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.password.setSelection(binding.password.getText().length());
                }
            } else {
                if (buttonView.equals(binding.confirmPasswordToggle)) {
                    binding.confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.confirmPassword.setSelection(binding.confirmPassword.getText().length());
                }
                else{
                    binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.password.setSelection(binding.password.getText().length());
                }
            }
        }
    };

    private boolean isFormValid(){
        if (TextUtils.isEmpty(binding.name.getText().toString())) {
            binding.name.setError("Invalid name!");
            return false;
        }

        if (TextUtils.isEmpty(binding.email.getText().toString())) {
            binding.email.setError("Email is Empty!");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches()){
            binding.email.setError("Invalid Email!");
            return false;
        }

        if (TextUtils.isEmpty(binding.password.getText().toString())) {
            binding.password.setError("Password is Empty!");
            return false;
        }

        if (!binding.password.getText().toString().equals(binding.confirmPassword.getText().toString())) {
            binding.password.setError("Password doesn't match!");
            binding.confirmPassword.setError("Password doesn't match!");
            return false;
        }

        if (binding.password.getText().toString().length() < 6) {
            binding.password.setError("Password must be more than 6 letters");
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    private void createUser() {


        auth.createUserWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString())
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        UserModel userModel = new UserModel(binding.name.getText().toString(),
                                                    binding.phoneNumber.getText().toString(),
                                                    binding.address.getText().toString(),
                                                    binding.gender.getSelectedItem().toString(),
                                            "");

                        String id = task.getResult().getUser().getUid();
                        database.getReference().child("Users").child(id).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    if (uri != null) {
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
                                                                registrationComplete(auth.getCurrentUser());
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else{
                                        registrationComplete(auth.getCurrentUser());
                                    }
                                }
                                else{
                                    registrationFailed(task.getException());
                                }
                            }
                        });
                    } else {
                        registrationFailed(task.getException());
                    }
                }
            });
    }

    private void registrationComplete(FirebaseUser user){
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Registered Successfully\n Please Check your email to verify your account.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
                else{
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registrationFailed(Exception exception){
        binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(RegisterActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Glide.with(getApplicationContext()).load(profileUri).into(binding.profilePicture);
                        } else {
                            Toast.makeText(context, "Error selecting an image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
}