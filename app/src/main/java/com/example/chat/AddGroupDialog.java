package com.example.chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddGroupDialog extends AppCompatDialogFragment {
    private TextView groupAddMessage;
    private EditText groupName;
    private Button AddGroupBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference ref;

    private DialogInterface listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_group_dialog, null);
        groupName = view.findViewById(R.id.group_name);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Group Name");
        builder.setView(view);
        builder.setCancelable(false);
       builder.setPositiveButton("ok", new android.content.DialogInterface.OnClickListener() {
           @Override
           public void onClick(android.content.DialogInterface dialogInterface, int i) {

               String groupname = groupName.getText().toString();
               listener.apply(groupname);

           }
       });


        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try
        {
            listener=(DialogInterface)context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() +"must implement listener");
        }




    }
public  interface  DialogInterface
{
    void apply(String name);
}


}
