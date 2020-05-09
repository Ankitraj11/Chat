package com.example.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupMessageAdapter  extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder> {
    List<GroupMesgModel> groupMesgModelList;

    public GroupMessageAdapter(List<GroupMesgModel> groupMesgModelList) {
        this.groupMesgModelList = groupMesgModelList;
    }

    @NonNull
    @Override
    public GroupMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_groupmessage,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMessageAdapter.ViewHolder holder, int position) {
           GroupMesgModel groupMesgModel=groupMesgModelList.get(position);
           String sender=groupMesgModel.getName();
           String message=groupMesgModel.getMessgae();
           holder.setData(sender,message);

    }

    @Override
    public int getItemCount() {
        return groupMesgModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        name=itemView.findViewById(R.id.sender_name);
        msg=itemView.findViewById(R.id.sender_message);
        }

        private void setData(String names,String messages)
        {
            name.setText(names);
            msg.setText(messages);


        }
    }
}
