package com.darkcode.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private EditText otpNum1,otpNum2,otpNum3,otpNum4,otpNum5,otpNum6;
    private Button submit;
    private FirebaseAuth mAuth;
    String otp_string,received_otp;
    private ProgressBar progressBar2;
    private TextView timer;
    private ImageView logo;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);


        mAuth = FirebaseAuth.getInstance();

        received_otp = getIntent().getStringExtra("backendotp");
        progressBar2 = findViewById(R.id.progress_bar2);
        timer = findViewById(R.id.timer);
        logo = findViewById(R.id.logo);



        otpNum1 = findViewById(R.id.input_otp_1);
        otpNum2 = findViewById(R.id.input_otp_2);
        otpNum3 = findViewById(R.id.input_otp_3);
        otpNum4 = findViewById(R.id.input_otp_4);
        otpNum5 = findViewById(R.id.input_otp_5);
        otpNum6 = findViewById(R.id.input_otp_6);

        submit = findViewById(R.id.submit);


        new CountDownTimer(60000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText("Enter the OTP within "+ millisUntilFinished/1000 + " sec");
            }

            @Override
            public void onFinish() {
                timer.setText("Resend OTP");
                timer.setTextColor(Color.BLACK);
                timer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OTPActivity.this,LoginActivity.class);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(OTPActivity.this,
                                Pair.<View, String>create(logo,"logo_trans"));
                        startActivity(intent,options.toBundle());
                    }
                });
            }
        }.start();



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar2.setVisibility(View.VISIBLE);
                submit.setVisibility(View.INVISIBLE);

                if(!otpNum1.getText().toString().trim().isEmpty() &&
                   !otpNum2.getText().toString().trim().isEmpty() &&
                   !otpNum3.getText().toString().trim().isEmpty() &&
                   !otpNum4.getText().toString().trim().isEmpty() &&
                   !otpNum5.getText().toString().trim().isEmpty() &&
                   !otpNum6.getText().toString().trim().isEmpty()){

                     otp_string = otpNum1.getText().toString()+
                                        otpNum2.getText().toString()+
                                        otpNum3.getText().toString()+
                                        otpNum4.getText().toString()+
                                        otpNum5.getText().toString()+
                                        otpNum6.getText().toString();

                    Toast.makeText(OTPActivity.this, "Verifying OTP..", Toast.LENGTH_SHORT).show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(received_otp, otp_string);
                    signInWithPhoneAuthCredential(credential);

                }
                else {

                    progressBar2.setVisibility(View.GONE);
                    submit.setVisibility(View.VISIBLE);
                    Toast.makeText(OTPActivity.this, "Please Enter the OTP..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        movenumbers();

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            progressBar2.setVisibility(View.GONE);
                            submit.setVisibility(View.VISIBLE);
                            Toast.makeText(OTPActivity.this, "Verifieed Successfully..", Toast.LENGTH_SHORT).show();

                            FirebaseDatabase.getInstance("https://sosapp-aa44d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users")
                                    .child(mAuth.getCurrentUser().getUid()).child("phoneNumber").setValue(getIntent().getStringExtra("phoneNumber")).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Intent intent = new Intent(OTPActivity.this,ProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });



                        } else
                        {
                            progressBar2.setVisibility(View.GONE);
                            submit.setVisibility(View.VISIBLE);
                            Toast.makeText(OTPActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void movenumbers() {
        otpNum1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                      if(!s.toString().trim().isEmpty()){
                          otpNum2.requestFocus();
                      }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpNum2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    otpNum3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpNum3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    otpNum4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpNum4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    otpNum5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpNum5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    otpNum6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}