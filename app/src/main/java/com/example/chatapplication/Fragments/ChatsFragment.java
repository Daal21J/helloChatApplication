package com.example.chatapplication.Fragments;

import android.app.Activity;
import android.graphics.Path;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.chatapplication.Message;
import com.example.chatapplication.R;
import com.example.chatapplication.UserAdapter;
import com.example.chatapplication.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView chat_rv;
    private UserAdapter ua;
    private List<UserDetails> users;


    FirebaseUser fu;
    DatabaseReference dbR;
    private List<String> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chats, container, false);
        chat_rv=view.findViewById(R.id.chat_rv);
        chat_rv.setHasFixedSize(true);
        chat_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        fu= FirebaseAuth.getInstance().getCurrentUser();
        usersList=new ArrayList<>();
        dbR= FirebaseDatabase.getInstance().getReference("Messages");
        dbR.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /**usersList is String list taking IDs dial Messages Ref having a discussion with current User**/
                usersList.clear();
                for(DataSnapshot ss: snapshot.getChildren()){
                    Message msg=ss.getValue(Message.class);
                    /**if my list doesnt yet have the Id of the receiver**/
                    if(!usersList.contains(msg.getReceiver())){
                        /**with sender being my current user**/
                         if(msg.getSender().equals(fu.getUid())){
                             /**then add it to my list**/
                             usersList.add(msg.getReceiver());
                         }
                    }
                    /**or the sender**/
                    if(!usersList.contains(msg.getSender())){
                        /**with receiver being my current user**/
                        if(msg.getReceiver().equals(fu.getUid())){
                            /**then add it to my list**/
                            usersList.add(msg.getSender());
                        }
                    }


                }
                /**now I have a list of STRINGS- IDs of users that I discussed with**/
                /**but the adapter takes  UsersDetails object so I need to compare my list of ids**/
                /** with list of elements of type UserDetails, its the job of readMsgs**/
                readMsgs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
    private void readMsgs(){
        users=new ArrayList<>(); // this is a list of type UserDetails(having id,img,username)
        dbR=FirebaseDatabase.getInstance().getReference("Users");
        dbR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot ss: snapshot.getChildren()){
                    UserDetails u=ss.getValue(UserDetails.class);
                    //ss is looping through Users elements in RTDB
                    for(String id: usersList){ //id is looping through ids in my usersList
                        if(u.getId().equals(id)){ //if id-userList == id in Users in RTDB
                            if(users.size()!=0){ //if users is not empty
                                for(UserDetails ud : users){ //then we loop through its elements
                                    if(!u.getId().equals(ud.getId())){ //to avoid repetition
                                        users.add(u); // we add the element if it doesn't exist already
                                        break;
                                    }
                                }

                            }else{
                                 users.add(u); //if list is empty we dont have to loop, we just add the element
                                 break;
                            }
                        }
                    }
                }


                /*****************************************/
                /** now we have our list users of type userDetails, we will pass it as parameter to our UserAdapter*/
                ua=new UserAdapter(getContext(),users,true);
                chat_rv.setAdapter(ua);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}