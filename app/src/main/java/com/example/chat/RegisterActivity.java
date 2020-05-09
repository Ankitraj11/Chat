package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
        private FirebaseAuth auth;
          private FirebaseDatabase database;
          private DatabaseReference databaseref;
    private Button registerBtn;
    private TextView registerEmail;
    private TextView registerPass;
    private Button haveAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
      auth=FirebaseAuth.getInstance();
      database=FirebaseDatabase.getInstance();
      databaseref=database.getReference();
      haveAccount=findViewById(R.id.have_account);
    registerEmail=findViewById(R.id.register_email);
    registerPass=findViewById(R.id.register_pass);
    registerBtn=findViewById(R.id.register_btn);
    haveAccount.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
            finish();
            startActivity(intent);
        }
    });

    registerBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            String email=registerEmail.getText().toString();
            String pass=registerPass.getText().toString();
            auth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                   String deviceToken= FirebaseInstanceId.getInstance().getId();
                   String currentUserId=auth.getCurrentUser().getUid();
                   databaseref.child("Users").child(currentUserId).setValue("");
                   databaseref.child("Users").child(deviceToken).setValue(deviceToken)
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {


                               }
                           }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                       }
                   });
                    final AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                   builder.setMessage("Registration Successful");
                    builder.setCancelable(false);
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    final AlertDialog.Builder builder1=new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage(e.getMessage());
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder1.show();
                }
            });

        }
    });

    }
}
