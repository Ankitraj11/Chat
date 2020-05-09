package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
   private EditText loginEmail;
   private EditText loginPass;
   private Button donNOtHaveAccount;
   private Button loginBtn;
   private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_login);
       loginBtn=findViewById(R.id.login_btn);
       auth=FirebaseAuth.getInstance();
       loginEmail=findViewById(R.id.login_email);
       loginPass=findViewById(R.id.login_pass);
       donNOtHaveAccount=findViewById(R.id.do_not_accont);
       donNOtHaveAccount.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
               finish();
               startActivity(intent);

           }
       });


       loginBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String loginemails=loginEmail.getText().toString();
               String loginPasss=loginPass.getText().toString();
               auth.signInWithEmailAndPassword(loginemails,loginPasss).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                   @Override
                   public void onSuccess(AuthResult authResult) {

                       Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                       finish();
                       startActivity(intent);

                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                       builder.setMessage(e.getMessage());
                       builder.setCancelable(false);
                       builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {


                           }
                       });
                       builder.show();

                   }
               });

           }
       });




    }
}
