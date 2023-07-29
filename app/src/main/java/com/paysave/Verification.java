package com.paysave;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Verification extends AppCompatActivity {

    private EditText verificationCodeEditText;
    private Button verifyButton;
    private FirebaseAuth mAuth;
    private String verificationCode;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();

        verificationCodeEditText = findViewById(R.id.verify_code);
        verifyButton = findViewById(R.id.btnVerifyEmail);

        // Retrieve the verification code from the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            verificationCode = extras.getString("verificationCode");
        }

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredVerificationCode = verificationCodeEditText.getText().toString().trim();
                verifyEmail(enteredVerificationCode);
            }
        });
    }

    private void verifyEmail(String enteredVerificationCode) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (user.isEmailVerified()) {
                            // Email is already verified
                            Toast.makeText(Verification.this, "Email already verified", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Verification.this, TransferFragment.class));
                            finish();
                        } else {
                            // Check if verification code matches
                            if (enteredVerificationCode.equals(verificationCode)) {
                                // Verification code matches, mark email as verified
                                user.updateEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Verification.this, "Email verified successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Verification.this, TransferFragment.class));
                                            finish();
                                        } else {
                                            Toast.makeText(Verification.this, "Failed to verify email", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // Verification code does not match
                                Toast.makeText(Verification.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(Verification.this, "Failed to reload user", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(Verification.this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }
}