package com.darkcode.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private Button getOTP;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mAuth = FirebaseAuth.getInstance();

        phoneNumber = findViewById(R.id.phone_number);
        getOTP = findViewById(R.id.get_otp);
        progressBar = findViewById(R.id.progress_bar);
        logo = findViewById(R.id.logo);

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getOTP.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                if (!phoneNumber.getText().toString().trim().isEmpty()){
                    if ((phoneNumber.getText().toString().trim()).length() == 10){

                        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

                        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {

                                progressBar.setVisibility(View.GONE);
                                getOTP.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(LoginActivity.this,ProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                getOTP.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String backendCode,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                progressBar.setVisibility(View.GONE);
                                getOTP.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(LoginActivity.this,OTPActivity.class);
                                intent.putExtra("backendotp",backendCode);
                                intent.putExtra("phoneNumber",phoneNumber.getText().toString());
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,
                                        Pair.<View, String>create(logo,"logo_trans"));
                                startActivity(intent,options.toBundle());
                            }
                        };

                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(mAuth)
                                        .setPhoneNumber("+91"+phoneNumber.getText().toString())       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);

                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        getOTP.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Please Enter the correct No..", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    getOTP.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Please Enter the number..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}