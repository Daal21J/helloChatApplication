package com.example.chatapplication.Fragments;



import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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

public class UsersFragment extends Fragment {
    private RecyclerView rv;
    private UserAdapter ua;
    private List<UserDetails> users;
    FirebaseUser fbU;
    DatabaseReference dbR;
    EditText search_users;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);
        rv=view.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        search_users=view.findViewById(R.id.search_users);

        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                     searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        users=new ArrayList<>();
        readUsers();
        return view;
    }


    private void searchUsers(String s){
        final FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s).endAt(s+"\uf8ff"); //escape character of java
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot ss: snapshot.getChildren()){
                    UserDetails u=ss.getValue(UserDetails.class);

                    assert u!=null;
                    assert fuser!=null;

                    if(!u.getId().equals(fuser.getUid())){
                        users.add(u);
                    }

                }
                ua=new UserAdapter(getContext(),users,false);
                rv.setAdapter(ua);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    private void readUsers(){
        fbU= FirebaseAuth.getInstance().getCurrentUser();
        dbR= FirebaseDatabase.getInstance().getReference("Users");
        dbR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(search_users.getText().toString().equals("")){
                users.clear();
                for(DataSnapshot ss: snapshot.getChildren()){
                    UserDetails us=ss.getValue(UserDetails.class);

                    if(!us.getId().equals(fbU.getUid())){
                        users.add(us);
                    }
                    ua=new UserAdapter(getContext(),users,false);
                    rv.setAdapter(ua);
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


}