package com.example.chat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.ViewHolder> {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference ref;
    List<PrivateMessage> privateMessageList;
      private LinearLayout senderLayout;
      private LinearLayout receiverLayout;

    public PrivateMessageAdapter(List<PrivateMessage> privateMessageList) {
        this.privateMessageList = privateMessageList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText;
        public TextView senderName;
        public TextView receiverName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             senderLayout=itemView.findViewById(R.id.sender_layout);
             receiverLayout =itemView.findViewById(R.id.receiver_layout);
            senderMessageText =  itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverName =  itemView.findViewById(R.id.receiver_name);
       //     messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image);
            senderName = itemView.findViewById(R.id.sender_name);




        }
    }


    @NonNull
    @Override
    public PrivateMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_private_message,null);
               auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String currentuserid=user.getUid();

        PrivateMessage privateMessage=privateMessageList.get(position);
             String fromuserid=privateMessage.getFrom();
               String messageTpe=privateMessage.getType();

           ref= FirebaseDatabase.getInstance().getReference().child("Users").child(fromuserid);
          ref.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                  {

                      String senderName = (String) dataSnapshot.child("name").getValue();
                      String receiverName = (String) dataSnapshot.child("name").getValue();
                              holder.senderName.setText(senderName);
                              holder.receiverName.setText(receiverName);
                  }

              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });



        holder.senderMessageText.setVisibility(View.GONE);
         holder.receiverName.setVisibility(View.GONE);
        holder.receiverMessageText.setVisibility(View.GONE);
        holder.senderName.setVisibility(View.GONE);

//        holder.messageReceiverPicture.setVisibility(View.GONE);
        if ( messageTpe.equals("text"))
        {
            if (fromuserid.equals(currentuserid))
            {
                holder.senderName.setVisibility(View.VISIBLE);


                holder.senderMessageText.setVisibility(View.VISIBLE);
             LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                     ViewGroup.LayoutParams.WRAP_CONTENT);
                      params.gravity=Gravity.START;

          //   holder.messageSenderPicture.setLayoutParams(params);
            //   holder.senderMessageText.setLayoutParams(params);

                holder.senderMessageText.setText(privateMessage.getMessage() );}
            else
            {
                holder.receiverName.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.gravity=Gravity.END;

              //  holder.receiverMessageText.setLayoutParams(params1);
               // holder.receiverProfileImage.setLayoutParams(params1);

                holder.receiverMessageText.setText(privateMessage.getMessage());
            }
        }
    }








    @Override
    public int getItemCount() {
        return privateMessageList.size();
    }






        }


