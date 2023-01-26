package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class Login extends AppCompatActivity {
    TextInputLayout email, password;
    Button b_login, b_signup;
    FirebaseAuth auth;
    TextView signup_instead;
    DatabaseReference dbUsersRef;
    TextInputEditText mail;
    TextInputEditText pass;
    TextView pass_r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        auth = FirebaseAuth.getInstance();

        email = (TextInputLayout) findViewById(R.id.email);
        password = (TextInputLayout) findViewById(R.id.password);
        b_login = (Button) findViewById(R.id.b_login);
        b_signup = (Button) findViewById(R.id.b_signup);
        pass_r = (TextView) findViewById(R.id.pass_r);

        signup_instead = (TextView) findViewById(R.id.signup_instead);
        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.pass);

        pass_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Recovery.class));
            }
        });


        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getEditText().getText().toString();
                String p = password.getEditText().getText().toString();

                if (TextUtils.isEmpty(e)) {
                    Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(p)) {
                    Toast.makeText(getApplicationContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
                    return;
                }


                auth.signInWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser.isEmailVerified()) {
                                Intent i = new Intent(Login.this, MainActivity.class);
                                startActivity(i);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Please verify your email first", Toast.LENGTH_LONG).show();

                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "wrong username/password", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });


        signup_instead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });




    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            Intent i = new Intent(Login.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }
}

