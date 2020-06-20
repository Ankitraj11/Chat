package com.example.chat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

   List<FriendsModel> friendsModelList;

    public FriendsAdapter(List<FriendsModel> friendsModelList) {
        this.friendsModelList = friendsModelList;
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user,null);
              return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
                  FriendsModel friendsModel=friendsModelList.get(position);
                  String names=friendsModel.getFriendname();
                  String images=friendsModel.getFriendimage();
                  holder.setData(names,images);

    }

    @Override
    public int getItemCount() {
        return friendsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private CircleImageView image;

        public ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   String userid=friendsModelList.get(getAdapterPosition()).getUserid();
                    Intent intent=new Intent(view.getContext(),ProfileActivity.class);
                    intent.putExtra("userid",userid);
                    view.getContext().startActivity(intent);







                }
            });


            name=itemView.findViewById(R.id.single_user_name);
            image=itemView.findViewById(R.id.single_user_image);
        }
        private void setData(String names,String images)
        {
           if(images!=null)
           {name.setText(names);
            Picasso.with(itemView.getContext()).load(images).into(image);
        }
           else {
               Picasso.with(itemView.getContext()).load(R.drawable.ic_launcher_background).into(image);
               name.setText(names);
           }
    }
}}
