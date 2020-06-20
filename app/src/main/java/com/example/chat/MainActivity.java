package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AddGroupDialog.DialogInterface {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseUser curentUser;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private TabLayout tabLayout;
    private Fragment fragment;
    private FrameLayout frameLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
   TextView imageuploadinfo;
    TextView nameuploadinfo;
    TextView statuseuploadinfo;


    @Override
    protected void onStart() {
        super.onStart();
        user=auth.getCurrentUser();
    if(user!=null)
        {

            updateUserStatus("online");

            String currentUserId=auth.getCurrentUser().getUid();
            ref.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")
                    && dataSnapshot.hasChild("image") && dataSnapshot.hasChild("status"))) {


                           nameuploadinfo.setBackgroundColor(Color.BLUE);
                        imageuploadinfo.setBackgroundColor(Color.BLUE);
                        statuseuploadinfo.setBackgroundColor(Color.BLUE);
                        nameuploadinfo.setText("name  is successfully uploded");
                        imageuploadinfo.setText("image is successfully uploaded");
                        statuseuploadinfo.setText("status is successfully uploaded");

                    }
                    else if(dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image"))
                    {

                        nameuploadinfo.setBackgroundColor(Color.BLUE);
                        imageuploadinfo.setBackgroundColor(Color.BLUE);
                        nameuploadinfo.setText("name  is successfully uploded");
                        imageuploadinfo.setText("image is successfully uploaded");


                    }
                    else if(dataSnapshot.exists() && dataSnapshot.hasChild("name"))
                    {
                        nameuploadinfo.setBackgroundColor(Color.BLUE);
                        nameuploadinfo.setText("name  is successfully uploded");


                    }

                    else {

                        nameuploadinfo.setText("Please upload your name  before sending and receiving friend request");
                        imageuploadinfo.setText("Please upload your image  before sending and receiving friend request");
                        statuseuploadinfo.setText("Please upload your status  before sending and receiving friend request");

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else{
            if(user==null) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        }



    }






    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(auth.getCurrentUser()!=null)
        {

            updateUserStatus("offline");





        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tablayout);
        curentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
         user=auth.getCurrentUser();
        TabLayout.Tab first = tabLayout.newTab();
        first.setText("Chat");
        tabLayout.addTab(first);
        imageuploadinfo=findViewById(R.id.image_upload_information);
         nameuploadinfo=findViewById(R.id.name_upload_information);
         statuseuploadinfo=findViewById(R.id.status_upload_information);
        TabLayout.Tab secondTab = tabLayout.newTab();
        secondTab.setText("Group Chat");
        tabLayout.addTab(secondTab);

        TabLayout.Tab thirdTab = tabLayout.newTab();
        thirdTab.setText("Contact");
        tabLayout.addTab(thirdTab);


        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu, menu);
           return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.log_out) {
             auth.signOut();
             updateUserStatus("offline");
            finish();
            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginActivity);
        }
        if (item.getItemId() == R.id.add_group) {
            addGroup();
        }
        if (item.getItemId() == R.id.update_profile) {

            Intent updateintent = new Intent(MainActivity.this, UpdateProfile.class);
            startActivity(updateintent);

        }
        if (item.getItemId() == R.id.find_friends) {
            Intent findfriendintnet = new Intent(MainActivity.this, FindFriendsActivity.class);
            startActivity(findfriendintnet);
        }
        if(item.getItemId()==R.id.my_account)
        {
            Intent intent=new Intent(MainActivity.this,MyAccountActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.request_user) {
            Intent requestuserintent = new Intent(MainActivity.this, RequestUserActivity.class);
            startActivity(requestuserintent);

        }
        return true;    //  switch (item.getItemId())
    }   //  {
        //    case R.id.log_out:
        //         auth.signOut();
        //      finish();
        //      Intent loginActivity=new Intent(MainActivity.this,LoginActivity.class);
        //       startActivity(loginActivity);

        //      case R.id.add_group:
        //       addGroup();

//            case R.id.update_profile:
        //             finish();
        //           Intent updateintent=new Intent(MainActivity.this,UpdateProfile.class);
        //            startActivity(updateintent);

        //    case R.id.find_friends:

        //      Intent findfriendintnet=new Intent(MainActivity.this,FindFriendsActivity.class);
        //     startActivity(findfriendintnet);



       // return  true;
    //

    private void addGroup() {

             AddGroupDialog addGroupDialog=new AddGroupDialog();
             addGroupDialog.show(getSupportFragmentManager(),"add group dialog");

    }

    @Override
    public void apply(String name) {

        ref.child("Group Name").child(name).setValue("")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(MainActivity.this,"Group Added",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

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





}

