package com.example.sgpgthesis.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.sgpgthesis.MainActivity;
import com.example.sgpgthesis.R;
import com.example.sgpgthesis.databinding.ActivityLoginBinding;
import com.example.sgpgthesis.models.UserModel;
import com.example.sgpgthesis.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidForm()) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    loginUser();
                }
                else {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });

        binding.passwordToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                        binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                        binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                binding.password.setSelection(binding.password.getText().length());
            }
        });

        binding.resendEmailLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth.getCurrentUser() != null){
                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "An email verification was sent.\nPlease check your email.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            auth.signOut();
                            binding.resendEmailLink.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPasswordResetEmail();
            }
        });
    }

    private void sendPasswordResetEmail(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage("Enter your email");

        final EditText email = new EditText(this);
        email.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email.setHint("Email...");

        alert.setView(email);

        alert.setPositiveButton("Ok", (dialogInterface, i) -> {
            String emailAddress = email.getText().toString();

            if (TextUtils.isEmpty(emailAddress)) {
                email.setError("Email is Empty!");
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches()){
                email.setError("Invalid Email!");
            }

            auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    dialogInterface.dismiss();
                    Toast.makeText(LoginActivity.this, String.format("An email was sent to %s", emailAddress), Toast.LENGTH_SHORT).show();
                }
            });
        });

        alert.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        alert.show();
    }


    private boolean isValidForm(){
        String userEmail = binding.email.getText().toString();
        String userPassword = binding.password.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            binding.email.setError("Email is Empty!");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches()){
            binding.email.setError("Invalid Email!");
            return false;
        }

        if (TextUtils.isEmpty(userPassword)) {
            binding.password.setError("Password is Empty!");
            return false;
        }

        if (userPassword.length() < 6) {
            binding.password.setError("Password must be more than 6 letters");
            return false;
        }

        return true;
    }

    private void loginUser() {

        auth.signInWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        binding.progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            if (auth.getCurrentUser().isEmailVerified()){
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                Toast.makeText(LoginActivity.this, "Redirecting to the application", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                binding.resendEmailLink.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Please verify your account first!\n check your email", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
