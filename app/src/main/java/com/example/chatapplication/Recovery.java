package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class Recovery extends AppCompatActivity {
    TextInputLayout email;
    TextInputEditText mail;
    Button b_recover;
    FirebaseAuth fbA;
    TextView login_i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        email = (TextInputLayout) findViewById(R.id.email);
        mail=(TextInputEditText)findViewById(R.id.mail);
        b_recover = (Button) findViewById(R.id.b_recover);
        login_i=(TextView)findViewById(R.id.login_i);
        fbA=FirebaseAuth.getInstance();

        b_recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             String em=mail.getText().toString();
             if(TextUtils.isEmpty(em)){
                 Toast.makeText(Recovery.this,"Email can't be empty!",Toast.LENGTH_SHORT).show();

             }else{
                 fbA.sendPasswordResetEmail(em).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             Toast.makeText(Recovery.this,"Please check your email",Toast.LENGTH_SHORT).show();
                             startActivity(new Intent(Recovery.this,Login.class));
                         }else{
                             String err=task.getException().getMessage();
                             Toast.makeText(Recovery.this,err,Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
             }
            }
        });

        login_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recovery.this,Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}