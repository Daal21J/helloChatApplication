package com.example.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.chatapplication.Fragments.ChatsFragment;
import com.example.chatapplication.Fragments.UsersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView theUsern;
    FirebaseUser firebaseUser;
    DatabaseReference ref,reff;
    Toolbar myToolBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profile_image = findViewById(R.id.profile_image);
        theUsern = findViewById(R.id.theUsern);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        myToolBar = findViewById(R.id.myToolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("");

        final TabLayout tab_Layout=findViewById(R.id.tab_layout);
        final ViewPager view_pager=findViewById(R.id.view_pager);



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails ud = snapshot.getValue(UserDetails.class);
                theUsern.setText(ud.getUsername());
                if (ud.getImgURL().equals("default")) {
                    profile_image.setImageResource(R.drawable.blank);
                } else {
                    Glide.with(getApplicationContext()).load(ud.getImgURL()).into(profile_image);

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reff=FirebaseDatabase.getInstance().getReference("Messages");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ViewPagerAdapter vpa=new ViewPagerAdapter(getSupportFragmentManager());
                int unread=0;
                for(DataSnapshot ss:snapshot.getChildren()){
                    Message msg=ss.getValue(Message.class);
                    if(msg.getReceiver().equals(firebaseUser.getUid()) && msg.getIsseen()==0){
                        unread++;
                    }
                }
                if(unread==0){
                    vpa.addFragment(new ChatsFragment(),"Chats");
                }else{
                    vpa.addFragment(new ChatsFragment(),"("+unread+")Chats");
                }
                vpa.addFragment(new UsersFragment(),"Users");
                view_pager.setAdapter(vpa);
                tab_Layout.setupWithViewPager(view_pager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            case R.id.profile:
                startActivity(new Intent(MainActivity.this, UserProfile.class));
                return true;
        }
        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }
        //ctrl+o + look for char

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public void status(String status){
        ref=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        ref.updateChildren(hashMap);
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }


}
