package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Objects;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
    private FriendsAdapter friendsAdapter;
    List<FriendsModel> friendsModelList;
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private DatabaseReference ref;
    private RecyclerView recyclerView;
    private FriendsModel friendsModel;


    @Override
    protected void onResume() {
        super.onResume();



        if(auth.getCurrentUser().getUid()!=null)
        {
            updateUserStatus("online");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


        if(auth.getCurrentUser().getUid()!=null)
        {
            updateUserStatus("offline");
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);


        db = FirebaseDatabase.getInstance();
       ref=db.getReference();
             auth=FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.User_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FindFriendsActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        friendsModelList = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(friendsModelList);
        recyclerView.setAdapter(friendsAdapter);




        ref.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               // Iterator iterator=dataSnapshot.getChildren().iterator();
              //  while (iterator.hasNext())
            //    {
              //      String image=(String)((DataSnapshot)iterator.next()).getValue();
                //    String name=(String)((DataSnapshot)iterator.next()).getValue();
                  //  String status=(String)((DataSnapshot)iterator.next()).getValue();
      //              String userid=(String)((DataSnapshot)iterator.next()).getValue();
        //            FriendsModel friendsModel=new FriendsModel(name,image,status,userid);
          //          friendsModelList.add(friendsModel);
            //    }
        //        friendsAdapter.notifyDataSetChanged();
                    friendsModelList.clear();
                          for(DataSnapshot data:dataSnapshot.getChildren()) {
                              if (dataSnapshot.exists() && dataSnapshot.hasChild("image")) {
                                  String name = data.child("name").getValue(String.class);
                                  String image = data.child("image").getValue(String.class);
                                  String status = data.child("status").getValue(String.class);
                                  String userid = data.child("userId").getValue(String.class);
                                   friendsModel = new FriendsModel(name, image, status, userid);

                              } else if (dataSnapshot.exists()) {

                                  String name = data.child("name").getValue(String.class);
                                  String status = data.child("status").getValue(String.class);
                                  String userid = data.child("userId").getValue(String.class);
                                  friendsModel=new FriendsModel(name,status,userid);
                              } else { }

                              friendsModelList.add(friendsModel);
                          }
                          friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}

