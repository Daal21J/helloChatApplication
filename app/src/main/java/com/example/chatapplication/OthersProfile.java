package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class OthersProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.others_profile);
        TextView bio,username;
        CircleImageView profile_picture;
        Intent in;
        DatabaseReference ref;


        bio=findViewById(R.id.bio);
        username=findViewById(R.id.username);
        profile_picture=findViewById(R.id.profile_picture);

        in=getIntent();
        String userId=in.getStringExtra("userid");


        ref= FirebaseDatabase.getInstance().getReference("Users").child(userId);
        Toolbar myToolBar=(Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails u=snapshot.getValue(UserDetails.class);
                username.setText(u.getUsername());

                if(u.getImgURL().equals("default")){
                    profile_picture.setImageResource(R.drawable.blank);
                }else{
                    Glide.with(getApplicationContext()).load(u.getImgURL()).into(profile_picture);
                }
                if(TextUtils.isEmpty(u.getBio())){
                    bio.setText("no bio");
                }else{
                    bio.setText(u.getBio());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}