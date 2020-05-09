package com.example.chat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupChatListAdapter extends RecyclerView.Adapter<GroupChatListAdapter.ViewHolder> {

    List<String> groupNameModelList;
    String name;
    public GroupChatListAdapter(List<String> groupNameModelList) {
        this.groupNameModelList = groupNameModelList;
    }

    @NonNull
    @Override
    public GroupChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

     View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.single_group_name,null);
     return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatListAdapter.ViewHolder holder, int position) {

         name=groupNameModelList.get(position);

        holder.setData(name);



    }

    @Override
    public int getItemCount() {
        return groupNameModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);





        textView=itemView.findViewById(R.id.single_group_name);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String groupName=groupNameModelList.get(getAdapterPosition());
                Intent intent=new Intent(view.getContext(),GroupChatActivity.class);
                intent.putExtra("groupname",groupName);
                 view.getContext().startActivity(intent);



            }
        });




        }




        private void setData(String name)
        {
            textView.setText(name);
        }
    }
}
