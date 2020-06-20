package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UpdateProfile extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;
    private ImageView image;
    private EditText userName;
    private EditText userStatus;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    Uri profileImage;
    private FirebaseStorage storage;
    private StorageReference sref;
    private FirebaseUser currentUser;
    String ftechprofileImage;
    private Button updateProfile;
    private Button completeprofile;
    String fetchFromGalleryImage;
    private Button addProfileImageBtn;
    private String currentUserId;
    private Button gotoHOmebtn;
    String profileImageurl;

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
        completeprofile = findViewById(R.id.complete_profile);
        updateProfile=findViewById(R.id.update_profile);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

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
                   final String name = userName.getText().toString();
                   final String status = userStatus.getText().toString();
               updateprofile(name,status);


               }
           });

        completeprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = userName.getText().toString();
                final String status = userStatus.getText().toString();

                if (name.isEmpty() || status.isEmpty() ||TextUtils.isEmpty(fetchFromGalleryImage)) {
                    if (name.isEmpty()) {
                        userName.requestFocus();

                    }
                    if (status.isEmpty()) {
                        userStatus.requestFocus();
                    }
                    if(TextUtils.isEmpty(fetchFromGalleryImage))
                    {
                        Toast.makeText(UpdateProfile.this,"You must choose an image",Toast.LENGTH_SHORT).show();

                    }

                } else {

                    sref.child(currentUser.getUid() + ".jpg").putFile(profileImage)
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
                                                     profileImageurl = uri.toString();



                                                    Userdata userdata = new Userdata(name, status, currentUser.getUid(), profileImageurl);
                                                    ref.child("Users").child(currentUser.getUid()).setValue(userdata)
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
                                            });

                                        }
                                    }
                                }
                            });


                }
            }
        });
    }

       private void updateprofile(final String name,final String status ) {
          if(!TextUtils.isEmpty(fetchFromGalleryImage)) {
              sref.child(currentUser.getUid() + ".jpg").putFile(profileImage)
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
                                              profileImageurl = uri.toString();

                                              HashMap<String, Object> map = new HashMap<>();
                                              map.put("image", profileImageurl);
                                              map.put("name", name);
                                              map.put("status", status);

                                              // Userdata userdata = new Userdata(name, status, currentUser.getUid(), profileImageurl);
                                              ref.child("Users").child(currentUser.getUid()).updateChildren(map)
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
                                      });

                                  }
                              }
                          }
                      });
          }
          else {
               HashMap<String, Object> map=new HashMap<String, Object>();
              map.put("name", name);
              map.put("status", status);

              // Userdata userdata = new Userdata(name, status, currentUser.getUid(), profileImageurl);
              ref.child("Users").child(currentUser.getUid()).updateChildren(map)
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

       private void LoadProfileinfo() {

        ref.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))
                        && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("status"))) {

                        completeprofile.setVisibility(View.INVISIBLE);
                        completeprofile.setEnabled(false);
                        Userdata userdata=dataSnapshot.getValue(Userdata.class);
                        String name =userdata.getName();
                        String status = userdata.getStatus();
                        ftechprofileImage = userdata.getImage();

                    userName.setText(name);
                    userStatus.setText(status);
                    Picasso.with(UpdateProfile.this).load(ftechprofileImage).into(image);

                } else if (dataSnapshot.exists() &&dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("status"))) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    userName.setText(name);
                    userStatus.setText(status);
                    Picasso.with(UpdateProfile.this).load(R.drawable.ic_launcher_background).into(image);
                }
                     else{

                         updateProfile.setEnabled(false);
                         updateProfile.setVisibility(View.INVISIBLE);

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

             profileImage = data.getData();
             fetchFromGalleryImage=profileImage.toString();
            Picasso.with(UpdateProfile.this).load(profileImage).into(image);

        }

                                }


    private void updateuserStatus(String state) {

        String currentTime,currentDate;

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM dd, yyyy");
        currentDate=simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat simpleTimeformat=new SimpleDateFormat("hh:mm a");
        currentTime=simpleTimeformat.format(calendar.getTime());


        HashMap<String, Object> map=new HashMap<>();
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
