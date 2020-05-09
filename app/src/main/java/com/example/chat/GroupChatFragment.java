package com.example.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChatFragment extends Fragment {
 private RecyclerView recyclerView;
 private GroupChatListAdapter groupChatListAdapter;
 List<String> groupNameModelList;
 private FirebaseDatabase db;
 private DatabaseReference ref;
    public GroupChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_group_chat, container, false);
             db=FirebaseDatabase.getInstance();
             ref=db.getReference();
         recyclerView=view.findViewById(R.id.group_chat_recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
         recyclerView.setLayoutManager(linearLayoutManager);

         groupNameModelList=new ArrayList<>();
         groupChatListAdapter=new GroupChatListAdapter(groupNameModelList);
           recyclerView.setAdapter(groupChatListAdapter);
            ref.child("Group Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Set<String> set = new HashSet<>();
                    Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext())
                    {
                        set.add(((DataSnapshot)iterator.next()).getKey());
                    }

                    groupNameModelList.clear();
                    groupNameModelList.addAll(set);
                    groupChatListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });











    return view;
    }
}
