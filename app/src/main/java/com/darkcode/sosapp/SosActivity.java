package com.darkcode.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SosActivity extends AppCompatActivity {

    Button contactUs;
    CircleImageView profileimage;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    String userId;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        name = findViewById(R.id.name);
        contactUs = findViewById(R.id.contact_us);
        profileimage = findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();

        rootRef = FirebaseDatabase.getInstance("https://sosapp-aa44d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users");

        getInfo();

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SosActivity.this,ContactUsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


     profileimage.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             Intent intent = new Intent(SosActivity.this,ProfileViewActivity.class);
             startActivity(intent);

         }
     });

    }

    private void getInfo() {
       rootRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("name").exists()) {
                    name.setText(snapshot.child("name").getValue().toString());
                }

                if (snapshot.child("profileImage").exists()) {
                    Picasso.get().load(snapshot.child("profileImage").getValue().toString()).placeholder(R.drawable.splash2_image).into(profileimage);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}