package com.darkcode.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {

    private EditText name,email,parentNumber,parentEmail,currentAdddress,permanentAddress;
    private TextView mobileNumber;
    private Button save;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    private CircleImageView profileImage;
    private StorageReference profileImageRef;
    private ProgressDialog pd;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        rootRef = FirebaseDatabase.getInstance("https://sosapp-aa44d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users");
        profileImageRef = FirebaseStorage.getInstance("gs://sosapp-aa44d.appspot.com").getReference().child("ProfileImages");

        pd = new ProgressDialog(ProfileActivity.this);


        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        parentEmail = findViewById(R.id.parent_email_address);
        parentNumber = findViewById(R.id.parent_mobile_number);
        currentAdddress = findViewById(R.id.current_address);
        permanentAddress = findViewById(R.id.permanent_address);
        profileImage = findViewById(R.id.profile_image);

        mobileNumber = findViewById(R.id.mobile_number);


        getMobileNumber();

        getInfo();

        userId = mAuth.getCurrentUser().getUid();

        save = findViewById(R.id.save);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(ProfileActivity.this);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.setTitle("Please Wait..");
                pd.setMessage("Updating..");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                if(!name.getText().toString().isEmpty() &&
                        !email.getText().toString().isEmpty() &&
                        !parentEmail.getText().toString().isEmpty() &&
                        !parentNumber.getText().toString().isEmpty() &&
                        !currentAdddress.getText().toString().isEmpty() &&
                        !permanentAddress.getText().toString().isEmpty()){



                    Map<String,Object> map = new HashMap();
                    map.put("name",name.getText().toString());
                    map.put("email",email.getText().toString());
                    map.put("parentEmail",parentEmail.getText().toString());
                    map.put("parentNumber",parentNumber.getText().toString());
                    map.put("currentAddress",currentAdddress.getText().toString());
                    map.put("permanentAddress",permanentAddress.getText().toString());
                    map.put("userId",userId);


                   rootRef.child(userId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {

                           if (task.isSuccessful()){
                               pd.dismiss();

                               Toast.makeText(ProfileActivity.this, "Profile Updated Successfully..", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(ProfileActivity.this,SosActivity.class);
                               startActivity(intent);
                               overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                           }
                           else {

                               pd.dismiss();
                               Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       }
                   });

                }
                else {

                    pd.dismiss();
                    Toast.makeText(ProfileActivity.this, "All fields are mandatory..", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void getInfo() {
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

                     if (snapshot.child("parentEmail").exists()) {
                         parentEmail.setText(snapshot.child("parentEmail").getValue().toString());
                     }
                     if (snapshot.child("parentNumber").exists()) {
                         parentNumber.setText(snapshot.child("parentNumber").getValue().toString());
                     }

                     if (snapshot.child("currentAddress").exists()) {
                         currentAdddress.setText(snapshot.child("currentAddress").getValue().toString());
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

    private void getMobileNumber() {

        FirebaseDatabase.getInstance("https://sosapp-aa44d-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 mobileNumber.setText(snapshot.child("phoneNumber").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Unable to load phone Number "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            pd.setTitle("Updating Image");
            pd.setMessage("Please Wait..");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

                final StorageReference filepath = profileImageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

                Uri resultUri = result.getUri();

                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                pd.dismiss();

                                final String downloadUrl = uri.toString();

                                rootRef.child(mAuth.getCurrentUser().getUid()).child("profileImage").setValue(downloadUrl);
                                Toast.makeText(ProfileActivity.this, "Profile Image Updated Successfully..", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                pd.dismiss();
                                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            }







}