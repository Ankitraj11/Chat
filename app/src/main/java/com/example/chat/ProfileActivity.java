package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private CircleImageView profileImage;
    private TextView profileName;
    private String receiverid;
    private String current_state;
    private TextView profilestatus;
    private FirebaseAuth auth;
    private FirebaseUser sender;
    private String senderid;
    private Button sendrequestBtn;
    private Button declineRequestBtn;

    @Override
    protected void onPause() {
        super.onPause();


        if(auth.getCurrentUser().getUid()!=null)
        {

            saveUserStatus("offline");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(auth.getCurrentUser().getUid()!=null)
        {

            saveUserStatus("online");

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        declineRequestBtn=findViewById(R.id.decline_rquest_btn);
        auth = FirebaseAuth.getInstance();
        sender = auth.getCurrentUser();
        sendrequestBtn = findViewById(R.id.send_request_btn);
        current_state = "new";
        sendrequestBtn.setText("Send Friend Request");
        receiverid = getIntent().getExtras().get("userid").toString();

        declineRequestBtn.setVisibility(View.INVISIBLE);
        declineRequestBtn.setEnabled(false);
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        profilestatus = findViewById(R.id.profile_status);
        ref.child("Users").child(receiverid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if(dataSnapshot.hasChild("image")) {
                      String image = dataSnapshot.child("image").getValue().toString();
                      Picasso.with(ProfileActivity.this).load(image).into(profileImage);

                  }

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();


                profileName.setText(name);
                profilestatus.setText(status);


                manageRequest();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void manageRequest() {

          ref.child("ChatRequest").child(sender.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiverid)) {
                    String requestType = dataSnapshot.child(receiverid)
                            .child("request type").getValue().toString();
                    if(requestType.equals("sent"))
                    {
                        current_state="request sent";
                        sendrequestBtn.setText("Cancel the request");
                        declineRequestBtn.setVisibility(View.INVISIBLE);
                        declineRequestBtn.setEnabled(false);
                    }
                  else  if(requestType.equals("received")) {
                        current_state = "request received";
                        sendrequestBtn.setText("Accept friend request");
                        declineRequestBtn.setVisibility(View.VISIBLE);
                        declineRequestBtn.setEnabled(true);
                        declineRequestBtn.setText("Cancel request");
                        declineRequestBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelChatRequest();
                            }
                        });
                    }

                    }
                    else {
                    ref.child("Contacts").child(sender.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(receiverid)) {
                                        current_state = "friends";
                                        sendrequestBtn.setText("Remove this contact");
                                        declineRequestBtn.setVisibility(View.INVISIBLE);
                                        declineRequestBtn.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }}

         @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });












         if(!sender.getUid().equals(receiverid))
         {

               sendrequestBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       sendrequestBtn.setEnabled(false);
                       if(current_state.equals("new"))
                       {
                           declineRequestBtn.setVisibility(View.INVISIBLE);
                           declineRequestBtn.setEnabled(false);
                                sendFriendRequest();
                           
                       }
                      if(current_state.equals("request sent"))
                      {

                          cancelChatRequest();
                      }
                      if(current_state.equals("request received"))
                      {
                          AcceptChatRequest();
                      }
                      if(current_state.equals("friends"))
                      {
                             removeContact();
                      }

                   }


                      });
                }
         else{


                        declineRequestBtn.setVisibility(View.INVISIBLE);
                      sendrequestBtn.setVisibility(View.INVISIBLE);
         }






    }

    private void removeContact() {
       ref.child("Contacts").child(sender.getUid())
               .child(receiverid).removeValue()
               .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful())
                       {

                           ref.child("Contacts").child(receiverid).child(sender.getUid())
                                   .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful())
                                           {
                                               sendrequestBtn.setEnabled(true);
                                               sendrequestBtn.setVisibility(View.VISIBLE);
                                               sendrequestBtn.setText("Send friend Request");
                                               current_state="new";
                                               declineRequestBtn.setVisibility(View.INVISIBLE);
                                               declineRequestBtn.setEnabled(false);



                                           }

                               }
                           });
                       }
                   }
               });





    }

    private void AcceptChatRequest()
    {
         ref.child("Contacts").child(sender.getUid()).child(receiverid)
                 .child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                   ref.child("Contacts").child(receiverid)
                           .child(sender.getUid()).child("Contacts").setValue("Saved")
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful())
                                       {
                                           ref.child("ChatRequest").child(sender.getUid())
                                                   .child(receiverid).removeValue()
                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           if(task.isSuccessful())
                                                           {
                                                               ref.child("ChatRequest").child(receiverid)
                                                                       .child(sender.getUid())
                                                                       .removeValue()
                                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {

                                                                               sendrequestBtn.setEnabled(true);
                                                                               current_state="friends";
                                                                               sendrequestBtn.setText("Remove this contact");
                                                                               declineRequestBtn.setVisibility(View.INVISIBLE);
                                                                                 declineRequestBtn.setEnabled(false);
                                                                           }
                                                                       });
                                                           }

                                                       }
                                                   });
                                       }

                               }
                           });


                }


             }
         });






    }

    private void cancelChatRequest() {

         ref.child("ChatRequest").child(sender.getUid()).child(receiverid)
                 .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {

                 if(task.isSuccessful())
                 {
                     ref.child("ChatRequest").child(receiverid).child(sender.getUid())
                             .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful())
                                   {
                                       sendrequestBtn.setEnabled(true);
                                       current_state="new";
                                       sendrequestBtn.setText("send Friend request");
                                       declineRequestBtn.setVisibility(View.INVISIBLE);
                                       declineRequestBtn.setEnabled(false);
                                   }
                         }
                     });
                 }


             }
         });





    }

    private void sendFriendRequest() {

                   ref.child("ChatRequest").child(sender.getUid()).child(receiverid)
                           .child("request type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {

                           if(task.isSuccessful())
                           {

                               ref.child("ChatRequest").child(receiverid).child(sender.getUid())
                                       .child("request type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {

                                       sendrequestBtn.setEnabled(true);
                                       current_state="request sent";
                                       sendrequestBtn.setText("Cancel the request");

                                   }
                               });


                           }


                       }
                   });




    }


    private void saveUserStatus(String state) {

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
        ref.child("Users").child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("state").setValue(map)
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
