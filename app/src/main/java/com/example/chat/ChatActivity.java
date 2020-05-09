package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
String image;
String name;
String receiveruserid;
CircleImageView toolbarImage;
TextView toolbarName;
List<PrivateMessage> privateMessageList;
private DatabaseReference ref,Rootref;
private FirebaseAuth auth;
private FirebaseUser user;
private Toolbar toolbar;
private Button sendMsgbtn;

private RecyclerView recyclerView;
 String    messageReceiverRef;
 private EditText editmsg;
    String messageSenderRef;
    private PrivateMessageAdapter adapter;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
       protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView=findViewById(R.id.chatting_recyclerview);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ChatActivity.this) {


            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                super.smoothScrollToPosition(recyclerView, state, position);

                LinearSmoothScroller smoothScroller=new LinearSmoothScroller(ChatActivity.this){

                    private static final float SPEED=50f;

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED/displayMetrics.densityDpi;

                    }
                };
                   smoothScroller.setTargetPosition(position);
                   startSmoothScroll(smoothScroller);
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        privateMessageList=new ArrayList<>();
        adapter=new PrivateMessageAdapter(privateMessageList);
        recyclerView.setAdapter(adapter);
        editmsg=findViewById(R.id.edit_msg);
        receiveruserid=(String)getIntent().getExtras().get("receiverid");
       name=(String)getIntent().getExtras().get("username");
       image=(String)getIntent().getExtras().get("userImage");
       toolbar=findViewById(R.id.toolbar);
       toolbar.setTitle(name);
       toolbar.setTitleMarginStart(550);
       toolbar.setTitleTextColor(Color.WHITE);
       Rootref=FirebaseDatabase.getInstance().getReference();
       sendMsgbtn=findViewById(R.id.send_user_msg_btn);
       toolbarImage=findViewById(R.id.chat_user_image);
       ref= FirebaseDatabase.getInstance().getReference();
        Picasso.with(ChatActivity.this).load(image).into(toolbarImage);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

         messageSenderRef="Messages/" +user.getUid()+ "/" +receiveruserid;
        messageReceiverRef="Messages/" +receiveruserid +"/" + user.getUid();


            sendMsgbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessageBtn();
                }
            });


    }

    @Override
    protected void onStart() {
        super.onStart();

          ref.child("Messages").child(user.getUid())
                  .child(receiveruserid).addChildEventListener(new ChildEventListener() {
              @Override
              public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                  PrivateMessage privateMessage= dataSnapshot.getValue(PrivateMessage.class);
                  privateMessageList.add(privateMessage);
                  adapter.notifyDataSetChanged();
                  recyclerView.smoothScrollToPosition(adapter.getItemCount());


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

    private void sendMessageBtn() {


       String keyid=  ref.child("Messages").child(user.getUid()).child(receiveruserid)
                .push().getKey();

          String message=editmsg.getText().toString();
          editmsg.setText("");
          Map messageInfo=new HashMap();
          messageInfo.put("message",message);
          messageInfo.put("from",user.getUid());
          messageInfo.put("type","text");
          messageInfo.put("to",receiveruserid);

          Map messageBody =new HashMap();
          messageBody.put( messageSenderRef +"/" +keyid, messageInfo);
          messageBody.put( messageReceiverRef+ "/" +keyid, messageInfo);

        Rootref.updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
           if(task.isSuccessful())
           {


           }


            }
        });




    }
}
