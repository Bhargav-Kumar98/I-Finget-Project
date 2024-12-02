/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private TextView redirect_to_login;
    private EditText signup_email, signup_password, signup_confirm_password;
    private Button signup_button;
    private FirebaseAuth fb_auth;

    private ProgressBar loadingProgressBar;
    private FrameLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fb_auth = FirebaseAuth.getInstance();
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);
        signup_confirm_password = findViewById(R.id.signup_confirm_password);
        signup_button = findViewById(R.id.signup_button);
        redirect_to_login = findViewById(R.id.login_redirect);

        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailId = signup_email.getText().toString().trim();
                String passwd = signup_password.getText().toString().trim();
                String confpasswd = signup_confirm_password.getText().toString().trim();
                if (emailId.isEmpty()){
                    signup_email.setError("Email cannot be empty");
                }
                else if (passwd.isEmpty()){
                    signup_password.setError("Password cannot be empty");
                }
                else if (confpasswd.isEmpty()){
                    signup_confirm_password.setError("Confirm your Password!");
                }
                else if (!passwd.equals(confpasswd)){
                    signup_confirm_password.setError("Passwords do not match!");
                }
                else{
                    showLoading(true);
                    fb_auth.createUserWithEmailAndPassword(emailId, passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLoading(false);
                                        Toast.makeText(SignupActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                }, 1000);

                            } else {
                                showLoading(false);
                                Toast.makeText(SignupActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        redirect_to_login.setOnClickListener(view -> startActivity(new Intent(SignupActivity.this, LoginActivity.class)));

    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            loadingOverlay.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.VISIBLE);
            signup_button.setEnabled(false);
        } else {
            loadingOverlay.setVisibility(View.GONE);
            loadingProgressBar.setVisibility(View.GONE);
            signup_button.setEnabled(true);
        }
    }
}