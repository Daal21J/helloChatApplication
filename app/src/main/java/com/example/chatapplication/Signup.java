package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Signup extends AppCompatActivity {
    TextInputLayout email, username, password;
    Button b_login, b_signup;
    FirebaseAuth auth;
    TextView login_instead;
    FirebaseUser firebaseUser;
    DatabaseReference ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        email = (TextInputLayout) findViewById(R.id.email);
        password = (TextInputLayout) findViewById(R.id.password);
        username=(TextInputLayout) findViewById(R.id.username);
        b_login = (Button) findViewById(R.id.b_login);
        b_signup = (Button) findViewById(R.id.b_signup);
        login_instead=(TextView)findViewById(R.id.login_instead);

        b_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getEditText().getText().toString();
                String p = password.getEditText().getText().toString();
                String u=username.getEditText().getText().toString();

                if (TextUtils.isEmpty(e)) {
                    Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(p)) {
                    Toast.makeText(getApplicationContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(u)) {
                    Toast.makeText(getApplicationContext(), "Please enter username!!", Toast.LENGTH_LONG).show();
                    return;
                }
                auth=FirebaseAuth.getInstance();

                auth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser=auth.getCurrentUser();
                            //storing other data into firebase's Realtime DB
                            UserDetails ud=new UserDetails(firebaseUser.getUid(),u,"default","","");
                            //extracting user reference from DB for 'registered users' where we will stock data of all users
                            ref=FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseUser.getUid()).setValue(ud).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Account created successfully! please verify your email :)", Toast.LENGTH_LONG).show();
                                            firebaseUser.sendEmailVerification();
                                            auth.signOut();
                                            Intent intent= new Intent(Signup.this,Login.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Account creation failed.. please try again", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                            switch (task.getException().toString()) {
                                case "auth/email-already-in-use":
                                    Toast.makeText(getApplicationContext(), "Email already in use!", Toast.LENGTH_LONG).show();
                                case "auth/invalid-email":
                                    Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_LONG).show();
                                case "auth/weak-password":
                                    Toast.makeText(getApplicationContext(), "Password should be 8 characters or longer", Toast.LENGTH_LONG).show();
                                default:
                                    Toast.makeText(getApplicationContext(), "Something went wrong..", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });
            }
        });


        login_instead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this,Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP ));
            }
        });


    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}