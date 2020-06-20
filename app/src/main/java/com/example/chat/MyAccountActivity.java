package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountActivity extends AppCompatActivity {
private CircleImageView circleImageView;
private TextView name;
private  TextView status;
private DatabaseReference ref;
private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

       circleImageView=findViewById(R.id.user_profile_image);
       name=findViewById(R.id.user_name);
       status=findViewById(R.id.user_status);
       ref= FirebaseDatabase.getInstance().getReference();
       user= FirebaseAuth.getInstance().getCurrentUser();

       ref.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if (dataSnapshot.exists()) {

                   if (dataSnapshot.hasChild("name") || dataSnapshot.hasChild("image")
                           || dataSnapshot.hasChild("status")) {
                       Userdata userdata = dataSnapshot.getValue(Userdata.class);
                       String names = userdata.getName();
                       String image = userdata.getImage();
                       String stautss = userdata.getStatus();
                       Picasso.with(MyAccountActivity.this).load(image).into(circleImageView);
                       name.setText(names);
                       status.setText(stautss);
                   } else if((dataSnapshot.hasChild("name") || dataSnapshot.hasChild("status"))) {

                   }
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }
}
