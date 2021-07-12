package com.darkcode.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileViewActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView name,phoneNumber,parentNumber,email,currentAddress,permanentAddress;
    FirebaseAuth mAuth;
    private Button edit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        parentNumber = findViewById(R.id.parent_mobile_number);
        currentAddress = findViewById(R.id.current_address);
        permanentAddress = findViewById(R.id.permanent_address);
        profileImage = findViewById(R.id.profile_image);
        edit = findViewById(R.id.edit);

        geInfo();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileViewActivity.this,ProfileActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileViewActivity.this,
                        Pair.create(profileImage,"profileTrans")
                        );
                startActivity(intent,options.toBundle());
            }
        });

    }

    private void geInfo() {

        FirebaseDatabase.getInstance("https://sosapp-aa44d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("name").exists()) {
                    name.setText(snapshot.child("name").getValue().toString());
                }

                if (snapshot.child("email").exists()) {
                    email.setText(snapshot.child("email").getValue().toString());
                }

                if (snapshot.child("parentNumber").exists()) {
                    parentNumber.setText(snapshot.child("parentNumber").getValue().toString());
                }

                if (snapshot.child("currentAddress").exists()) {
                    currentAddress.setText(snapshot.child("currentAddress").getValue().toString());
                }

                if (snapshot.child("permanentAddress").exists()) {
                    permanentAddress.setText(snapshot.child("permanentAddress").getValue().toString());
                }

                if (snapshot.child("profileImage").exists()) {
                    Picasso.get().load(snapshot.child("profileImage").getValue().toString()).placeholder(R.drawable.splash2_image).into(profileImage);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}