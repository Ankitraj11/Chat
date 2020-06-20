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
    private FirebaseUser user;


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
        ref = db.getReference();
        auth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.User_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FindFriendsActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        friendsModelList = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(friendsModelList);
        recyclerView.setAdapter(friendsAdapter);
        user = auth.getCurrentUser();


        ref.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists() && dataSnapshot.hasChild("name")
                && dataSnapshot.hasChild("image") && dataSnapshot.hasChild("status")) {
                //   FriendsModel friendsModel=dataSnapshot.getValue(FriendsModel.class);
                    String name=dataSnapshot.child("name").getValue().toString();
                    String image=dataSnapshot.child("image").getValue().toString();
                    String userid=dataSnapshot.child("userId").getValue().toString();
                    String status=dataSnapshot.child("status").getValue().toString();
                    friendsModel = new FriendsModel(name, image, status, userid);
                    friendsModelList.add(friendsModel);
                }
                else if (dataSnapshot.exists() && dataSnapshot.hasChild("name")
                        && dataSnapshot.hasChild("status"))
                {
                    String name=dataSnapshot.child("name").getValue().toString();
                    String userid=dataSnapshot.child("userId").getValue().toString();
                    String status=dataSnapshot.child("status").getValue().toString();
                    friendsModel=new FriendsModel(name,status,userid);
                    friendsModelList.add(friendsModel);
                }
                else {

                }
                friendsAdapter.notifyDataSetChanged();
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    }