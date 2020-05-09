package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;
    private ImageView image;
    private EditText userName;
    private EditText userStatus;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference sref;
    private FirebaseUser currentUser;
    String ftechprofileImage;
    private Button updateProfile;
    private Button addProfileImageBtn;
    private String currentUserId;
    private Button gotoHOmebtn;


    @Override
    protected void onPause() {
        super.onPause();

        if(auth.getCurrentUser().getUid()!=null)
        {

            updateuserStatus("offline");

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(auth.getCurrentUser().getUid()!=null)
        {

            updateuserStatus("online");

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        storage = FirebaseStorage.getInstance();
        sref = storage.getReference();

        addProfileImageBtn = findViewById(R.id.add_image_btn);
        userName = findViewById(R.id.user_name);
        userStatus = findViewById(R.id.user_status);
        image = findViewById(R.id.image);
        updateProfile = findViewById(R.id.update_profile);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        currentUserId = currentUser.getUid();
        userStatus = findViewById(R.id.user_status);

        LoadProfileinfo();



        addProfileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_PICK);

            }
        });




        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = userName.getText().toString();
                String status = userStatus.getText().toString();

                if(name.isEmpty()|| status.isEmpty())
                {
                    if(name.isEmpty())
                    {
                        userName.requestFocus();

                    }
                    if( status.isEmpty())
                    {
                        userStatus.requestFocus();
                    }
                }
                else {
                    Userdata userdata = new Userdata(name, status, auth.getCurrentUser().getUid());
                    ref.child("Users").child(auth.getCurrentUser().getUid()).setValue(userdata)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfile.this);
                                    builder.setMessage("Profile updated");
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(UpdateProfile.this, MainActivity.class);
                                            finish();
                                            startActivity(intent);
                                        }
                                    });
                                    builder.show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfile.this);
                            builder.setMessage(e.getMessage());
                            builder.setCancelable(false);
                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            builder.show();
                        }
                    });
                }
            }
        });

    }

    private void LoadProfileinfo() {

        ref.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))
                        && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("status"))) {


                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    ftechprofileImage = dataSnapshot.child("image").getValue().toString();

                    userName.setText(name);
                    userStatus.setText(status);
                    Picasso.with(UpdateProfile.this).load(ftechprofileImage).into(image);

                } else if ((dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("status"))) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    userName.setText(name);
                    userStatus.setText(status);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == GALLERY_PICK) && (resultCode == RESULT_OK) && data != null && data.getData() != null) {

            Uri profileImage = data.getData();
            //    CropImage.activity()
            //          .setGuidelines(CropImageView.Guidelines.ON)
            //         .setAspectRatio(1, 1)
            //         .start(this);

            //  if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            //              CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
            //              if (resultCode == RESULT_OK) {
            //              Uri resultUri = result.getUri();
            //                  profileImage=resultUri;


            sref.child(auth.getCurrentUser().getUid() + ".jpg").putFile(profileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getMetadata()
                                            .getReference().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String profileImageurl = uri.toString();


                                            ref.child("Users").child(currentUserId).child("image").setValue(profileImageurl)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(UpdateProfile.this, "image saved to real time", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }


                                    });
                                }
                            }
                        }
                    });
        }}


    private void updateuserStatus(String state) {

        String currentTime,currentDate;

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM dd, yyyy");
        currentDate=simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat simpleTimeformat=new SimpleDateFormat("hh:mm a");
        currentTime=simpleTimeformat.format(calendar.getTime());


        HashMap<String, Object> map=new HashMap();
        map.put("time",currentTime);
        map.put("date",currentDate);
        map.put("state",state);
        ref.child("Users").child(auth.getCurrentUser().getUid()).child("state").setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {



                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });




    }



}
