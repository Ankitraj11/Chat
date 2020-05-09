package com.example.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {
 private FirebaseDatabase db;
 private DatabaseReference ref;
 private FirebaseAuth auth;
 private FirebaseUser user;
    FirebaseRecyclerAdapter<RequestedUserModel, RequestViewHolder> adapter;
    private RecyclerView recyclerView;
    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_contact, container, false);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        recyclerView=view.findViewById(R.id.friend_recyclerView);
        db=FirebaseDatabase.getInstance();
        ref=db.getReference();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);








        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<RequestedUserModel> options = new FirebaseRecyclerOptions.Builder<RequestedUserModel>()
                .setQuery(ref.child("Contacts").child(user.getUid()), RequestedUserModel.class)
                .build();



               adapter = new FirebaseRecyclerAdapter<RequestedUserModel, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull RequestedUserModel model) {

                final String list_user_id = getRef(position).getKey();
                final DatabaseReference databaseref = getRef(position).child("Contacts").getRef();

                databaseref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            String value = dataSnapshot.getValue().toString();
                            if (value.equals("Saved")) {
                                ref.child("Users").child(list_user_id)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.exists()) {
                                                    if (dataSnapshot.hasChild("image")) {
                                                        String image = dataSnapshot.child("image").getValue().toString();

                                                        Picasso.with(getActivity()).load(image).into(holder.image);

                                                    }

                                                    String name = dataSnapshot.child("name").getValue().toString();
                                                    holder.name.setText(name);

                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_friend, null);
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

    public  class RequestViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView image;
    private TextView name;

    public RequestViewHolder(@NonNull View itemView) {

        super(itemView);

         image=itemView.findViewById(R.id.friend_image);
         name=itemView.findViewById(R.id.friend_name);
    }
}



}
