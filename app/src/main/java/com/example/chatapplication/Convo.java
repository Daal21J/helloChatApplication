package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Convo extends AppCompatActivity {
    CircleImageView profile_image;
    TextView theUsern;
    FirebaseUser fbU;
    DatabaseReference dbR;
    Intent inInt;
    private DatabaseReference dbRef;
    ImageButton send_button;
    EditText text_send;
    RecyclerView recycler_view;
    MessagesAdapter msgAdapter;
    List<Message> myMsgs;

    ValueEventListener seenListener;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.convo);
       //needed to import some libraries for it to work
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


       /**Displaying msgs**/
       recycler_view=findViewById(R.id.recycler_view);
       recycler_view.setHasFixedSize(true);
       LinearLayoutManager llm=new LinearLayoutManager(getApplicationContext());
       llm.setStackFromEnd(true);
       recycler_view.setLayoutManager(llm);
      /**********************************/
       profile_image=findViewById(R.id.profile_image);
       text_send=findViewById(R.id.text_send);

       send_button=findViewById(R.id.send_button);
       theUsern=findViewById(R.id.theUsern);

       inInt=getIntent();
       String userId=inInt.getStringExtra("userid");

       fbU=FirebaseAuth.getInstance().getCurrentUser();
       dbR=FirebaseDatabase.getInstance().getReference("Users").child(userId);

       /** sending msgs when clicking send**/

          send_button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  String msg=text_send.getText().toString();
                  String nmsg=msg.replace("\n", "");
                  if(!msg.equals("")){
                      sendMsg(fbU.getUid(),userId,nmsg);
                  }
                  text_send.setText("");
              }
          });

          /**********/
          profile_image.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent out=new Intent(Convo.this,OthersProfile.class);
                  out.putExtra("userid",userId);
                  startActivity(out);
              }
          });

       dbR.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               UserDetails u=snapshot.getValue(UserDetails.class);
               theUsern.setText(u.getUsername());
               if(u.getImgURL().equals("default")){
                   profile_image.setImageResource(R.drawable.blank);
               }else{
                  Glide.with(getApplicationContext()).load(u.getImgURL()).into(profile_image);
               }
               readMsgs(fbU.getUid(),userId);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

       seenMsg(userId);


       recycler_view.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
               inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
               return false;
           }
       });
   }

   /***** Seen *****/

   private void seenMsg(final String userid){
       dbRef=FirebaseDatabase.getInstance().getReference("Messages");

       seenListener=dbRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot ss: snapshot.getChildren()){
                   Message m=ss.getValue(Message.class);
                   if(m.getReceiver().equals(fbU.getUid()) && m.getSender().equals(userid)){

                          HashMap<String, Object> hashMap = new HashMap<>();
                          hashMap.put("isseen", 1);
                          ss.getRef().updateChildren(hashMap);

                   }
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

   }



   /*****************/

   private void sendMsg(String sender, String receiver, String msg){
       Timestamp ts = new Timestamp(System.currentTimeMillis());
       DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
       HashMap<String,Object>hashMap=new HashMap<>();
       hashMap.put("sender",sender);
       hashMap.put("receiver",receiver);
       hashMap.put("msg",msg);

       //SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
       //String timestamp = s.format(new Date());
       //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
       hashMap.put("timestamp", ServerValue.TIMESTAMP);
       ref.child("Messages").push().setValue(hashMap);


   }


   private void readMsgs(final String myId, final String hisId){
       myMsgs=new ArrayList<>();
       DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Messages");
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
              myMsgs.clear();
              for(DataSnapshot ss:snapshot.getChildren()){
                  Message m=ss.getValue(Message.class);
                  if(m.getReceiver().equals(hisId) && m.getSender().equals(myId) ||  m.getReceiver().equals(myId) && m.getSender().equals(hisId)){
                      myMsgs.add(m);
                  }
                  msgAdapter=new MessagesAdapter(Convo.this, (ArrayList<Message>) myMsgs);
                  recycler_view.setAdapter(msgAdapter);
              }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

   }
    public void status(String status){
        dbR=FirebaseDatabase.getInstance().getReference("Users").child(fbU.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        dbR.updateChildren(hashMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dbRef!=null){
            dbRef.removeEventListener(seenListener);
            seenListener=null;
            dbRef=null;
        }
        status("offline");
    }


}