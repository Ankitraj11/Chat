package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {
private DatabaseReference ref;
private FirebaseAuth auth;
private FirebaseUser user;
RecyclerView recyclerView;
public  String ONLINE_STATUS="online";
public  String OFFLINE_STATUS="offline";
public  String LAST_SEEN="Last seen";
FirebaseRecyclerAdapter<RequestedUserModel, RequestViewHolder> adapter;

 public ChatFragment()
 {

 }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
   super.onCreateView(inflater, container, savedInstanceState);

   ref= FirebaseDatabase.getInstance().getReference();
   auth=FirebaseAuth.getInstance();
   user=auth.getCurrentUser();

   View view=LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_chat, null);

        recyclerView=view.findViewById(R.id.chat_recyclerView);
   LinearLayoutManager linearLayoutManager=new LinearLayoutManager(view.getContext());
   linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
   recyclerView.setLayoutManager(linearLayoutManager);
      return view;

  }

  @Override
  public void onStart() {
   super.onStart();


   FirebaseRecyclerOptions<RequestedUserModel> options=new FirebaseRecyclerOptions.Builder<RequestedUserModel>()
           .setQuery(ref.child("Contacts").child(user.getUid()),RequestedUserModel.class).build();

    adapter= new FirebaseRecyclerAdapter<RequestedUserModel, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull RequestedUserModel model) {
                   final String list_used_id=getRef(position).getKey();
                final String[] name = {"default name"};
                final String[] image = {"default image"};
                final String[] state={"default state"};
                   ref.child("Users").child(list_used_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                     if(dataSnapshot.exists())
                     {
                       if(dataSnapshot.hasChild("image")) {

                           image[0] = (String) dataSnapshot.child("image").getValue();

                          ref.child("Users").child(list_used_id).child("state").addValueEventListener(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                  if((dataSnapshot.hasChild("time")) && (dataSnapshot.hasChild("date"))
                                          && (dataSnapshot.hasChild("state")))
                                  {

                                      String time=dataSnapshot.child("time").getValue().toString();
                                      String date=dataSnapshot.child("date").getValue().toString();
                                      String state=dataSnapshot.child("state").getValue().toString();

                               if(state.equals("online"))
                               {
                                   holder.state.setText("online");
                               }
                               else if(state.equals("offline"))
                               {
                                   holder.state.setText("last seen" +date+""+time);
                               }

                                  }

                              }

                              @Override
                              public void onCancelled(@NonNull DatabaseError databaseError) {

                              }
                          });

                           Picasso.with(getActivity()).load(image[0]).into(holder.image);
                       }
                         name[0] = (String) dataSnapshot.child("name").getValue();
                         holder.name.setText(name[0]);
                     }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                   });

                   holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         Intent intent=new Intent(getActivity(),ChatActivity.class);
                         intent.putExtra("receiverid",list_used_id);
                         intent.putExtra("username", name[0]);
                         intent.putExtra("userImage", image[0]);
                        startActivity(intent);
                    }
                   });

            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
             View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.single_chat_user,null);
             return new RequestViewHolder(view);
            }
           };


       recyclerView.setAdapter(adapter);
       adapter.startListening();
       adapter.notifyDataSetChanged();
 }

 @Override
 public void onStop() {
  super.onStop();
  adapter.stopListening();
 }

 public class RequestViewHolder extends RecyclerView.ViewHolder{
           private TextView name;
           private CircleImageView image;
           private TextView state;

   public RequestViewHolder(@NonNull View itemView) {
    super(itemView);

   name=itemView.findViewById(R.id.chat_user_name);
   image=itemView.findViewById(R.id.chat_user_image);
   state=itemView.findViewById(R.id.chat_user_state);
   }
  }












}
