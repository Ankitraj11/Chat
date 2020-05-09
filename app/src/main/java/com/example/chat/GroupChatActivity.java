package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String groupname;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    String currentUserId;
    String name;
    private GroupMessageAdapter groupMessageAdapter;
    private List<GroupMesgModel> groupMesgModelList;
    private RecyclerView recyclerView;
    private EditText groupMes;
    private Button sendMsgBtn;


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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        toolbar = findViewById(R.id.toolbar);
        db = FirebaseDatabase.getInstance();
        recyclerView=findViewById(R.id.group_chat_recyclerview);
        ref = db.getReference();


        groupMes = findViewById(R.id.group_msg);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        sendMsgBtn = findViewById(R.id.send_message_btn);
        currentUserId = user.getUid();
        groupname = getIntent().getExtras().get("groupname").toString();
        toolbar.setTitle(groupname);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitleMarginStart(400);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(GroupChatActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        groupMesgModelList=new ArrayList<>();
        groupMessageAdapter=new GroupMessageAdapter(groupMesgModelList);
           recyclerView.setAdapter(groupMessageAdapter);


        ref.child("Users").child(currentUserId).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        name = dataSnapshot.child("name").getValue().toString();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String groupmsg = groupMes.getText().toString();
                groupMes.setText("");
//                recyclerView.smoothScrollToPosition(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount()-1);

                GroupMesgModel groupMesgModel = new GroupMesgModel(name, groupmsg);
                String msgKey = ref.child("Group Name").child(groupname).push().getKey();

                ref.child("Group Name").child(groupname)
                                      .child(msgKey)
                        .setValue(groupMesgModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {



                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {

                                  }
                              });

            }

        });

        ref.child("Group Name").child(groupname)
             .addChildEventListener(new ChildEventListener() {
                  @Override
                  public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                      Iterator iterator = dataSnapshot.getChildren().iterator();
                      while (iterator.hasNext()) {

                          String name = ((DataSnapshot) iterator.next()).getValue().toString();
                          String message = ((DataSnapshot) iterator.next()).getValue().toString();
                          GroupMesgModel groupMesgModel = new GroupMesgModel(name, message);
                          groupMesgModelList.add(groupMesgModel);
                      }
                      //  for(DataSnapshot data:dataSnapshot.getChildren())
                      //              {
                      //        String senderName=data.child("name").getValue(String.class);
                      //         String sendermsg=data.child("messgae").getValue(String.class);
                      //         GroupMesgModel groupMesgModel=new GroupMesgModel(senderName,sendermsg);
                      //          groupMesgModelList.add(groupMesgModel);
                      // GroupMesgModel groupMesgModel=data.getValue(GroupMesgModel.class);
                      //  groupMesgModelList.add(groupMesgModel);
                      //         }
                                 groupMessageAdapter.notifyDataSetChanged();
                               }
                          @Override
                          public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                      //       for (DataSnapshot data : dataSnapshot.getChildren()) {
                      //            String name = data.child("name").getValue(String.class);
                      //         String message = data.child("messgae").getValue(String.class);
                      //           GroupMesgModel groupMesgModel = new GroupMesgModel(name, message);

                      //         groupMesgModelList.add(groupMesgModel);

                      //   GroupMesgModel groupMesgModel=data.getValue(GroupMesgModel.class);
                      //  groupMesgModelList.add(groupMesgModel);

                //      ;
                      //groupMessageAdapter.notifyDataSetChanged();

                        Iterator iterator = dataSnapshot.getChildren().iterator();
                       while (iterator.hasNext()) {

                         String name=((DataSnapshot)iterator.next()).getValue().toString();
                         String message=((DataSnapshot)iterator.next()).getValue().toString();
                         GroupMesgModel groupMesgModel=new GroupMesgModel(name,message);
                         groupMesgModelList.add(groupMesgModel);
                       }
                                 groupMessageAdapter.notifyDataSetChanged();

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



   private void  updateUserStatus(String state)
   {

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
}
