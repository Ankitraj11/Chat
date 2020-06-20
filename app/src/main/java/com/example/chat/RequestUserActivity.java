package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestUserActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private DatabaseReference chatref, ref;
     private Toolbar toolbar;
    private RecyclerView recyclerView;
    List<RequestedUserModel> requestedUserModelList;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userid;

    @Override
    protected void onPause() {
        super.onPause();

        if(auth.getCurrentUser().getUid()!=null)
        {

            updateUserStatus("offline");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(auth.getCurrentUser().getUid()!=null)
        {

            updateUserStatus("online");

        }

    }

    private void updateUserStatus(String state) {


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

        ref.child("Users").child(user.getUid()).child("state").setValue(map)
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

    String id;

    FirebaseRecyclerAdapter<RequestedUserModel, RequestsViewHolder> adapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_user);
        db = FirebaseDatabase.getInstance();
        toolbar=findViewById(R.id.friend_request_tool);
        toolbar.setTitle("Friend Request");
        toolbar.setTitleMarginStart(300);
        toolbar.setTitleTextColor(Color.WHITE);
        recyclerView = findViewById(R.id.request_recyclerview);
          chatref=db.getReference().child("ChatRequest");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        userid=user.getUid();
        ref=db.getReference();
        requestedUserModelList = new ArrayList<>();



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RequestUserActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<RequestedUserModel> options =
               new FirebaseRecyclerOptions.Builder<RequestedUserModel>()
                     .setQuery(chatref.child(user.getUid()), RequestedUserModel.class)
                   .build();


           adapter =
                 new FirebaseRecyclerAdapter<RequestedUserModel, RequestsViewHolder>(options) {
                   @NonNull
               @Override
                public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_request, null);
                  return new RequestsViewHolder(view);
              }

             @Override
              protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull RequestedUserModel model) {

            final String  list_user_id= getRef(position).getKey();

              DatabaseReference getTypeRef = getRef(position).child("request type").getRef();
              getTypeRef.addValueEventListener(new ValueEventListener() {
                @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                     if(dataSnapshot.exists())
                    {

                        String requestType=dataSnapshot.getValue().toString();
                        if(requestType.equals("received"))
                      {


                       ref.child("Users").child(list_user_id)
                               .addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if(dataSnapshot.exists())
                                           {
                                               if( dataSnapshot.hasChild("image"))
                                             {
                                                 String image=dataSnapshot.child("image").getValue().toString();
                                           Picasso.with(RequestUserActivity.this).load(image).into(holder.profileImage);

                                             }
                                               String name=dataSnapshot.child("name").getValue().toString();
                                           String status=dataSnapshot.child("status").getValue().toString();
                                           holder.userName.setText(name);
                                             Picasso.with(RequestUserActivity.this).load(R.drawable.ic_launcher_background).into(holder.profileImage);
                                           }
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                               });
                      }

              }


          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

         }
                });



            holder.CancelButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                   holder.CancelButton.setEnabled(false);
                         chatref.child(user.getUid())
                                 .child(list_user_id).removeValue()
                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if(task.isSuccessful())
                                         {
                                             ref.child("ChatRequest").child(list_user_id)
                                                     .child(user.getUid())
                                                     .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     if(task.isSuccessful())
                                                    holder.CancelButton.setEnabled(true);
                                                     Toast.makeText(RequestUserActivity.this,"Contact Removed",Toast.LENGTH_SHORT).show();

                                                 }
                                             });
                                         }
                                     }
                                 });
                     }
                 });






              holder.AcceptButton.setOnClickListener(new View.OnClickListener() {
                  @Override

                      public void onClick(View view) {
                             holder.AcceptButton.setEnabled(false);
                          ref.child("Contacts").child(user.getUid()).child(list_user_id)
                                  .child("Contacts").setValue("Saved")
                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                          if(task.isSuccessful())
                                          {
                                              ref.child("Contacts").child(list_user_id).child(user.getUid())
                                                      .child("Contacts").setValue("Saved")
                                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                          @Override
                                                          public void onComplete(@NonNull Task<Void> task) {
                                                              if(task.isSuccessful())
                                                              {
                                                                  ref.child("ChatRequest").child(user.getUid())
                                                                          .child(list_user_id).removeValue()
                                                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                              @Override
                                                                              public void onComplete(@NonNull Task<Void> task) {
                                                                                  ref.child("ChatRequest").child(list_user_id)
                                                                                          .child(user.getUid())
                                                                                          .removeValue()
                                                                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                              @Override
                                                                                              public void onComplete(@NonNull Task<Void> task) {

                                                                                                  if(task.isSuccessful())
                                                                                                  {
                                                                                                      Toast.makeText(RequestUserActivity.this,"New Contact added",Toast.LENGTH_SHORT).show();
                                                                                                      holder.AcceptButton.setEnabled(true);
                                                                                                  }
                                                                                              }
                                                                                          });
                                                                              }
                                                                          });
                                                              }


                                                          }
                                                      });

                                          }
                                      }
                                  });

                      }
                  });





          }

          };

        //  adapter.notifyDataSetChanged();
        }

          public class RequestsViewHolder extends RecyclerView.ViewHolder {
            TextView userName, userStatus;
          CircleImageView profileImage;
              Button AcceptButton, CancelButton;


            public RequestsViewHolder(@NonNull View itemView) {
              super(itemView);




            userName = itemView.findViewById(R.id.request_username);

            profileImage = itemView.findViewById(R.id.request_userimage);
            AcceptButton = itemView.findViewById(R.id.accept_request_btn);
            CancelButton = itemView.findViewById(R.id.declines_request_btn);
            }

         }

    @Override
    protected void onStart() {
        super.onStart();

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
        {
            adapter.stopListening();
        }
    }
}
