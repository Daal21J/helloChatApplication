package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    CircleImageView my_profile_picture;
    TextView username,bio;
    DatabaseReference ref;
    FirebaseUser fu;
    Button save_username;
    Button save_bio;
    EditText n_username;
    EditText n_bio;
    Button signout;

    /** profile pic changing code part 1**/
    StorageReference storageReference;
    private static final int IMG_REQ=1;
    private Uri imgUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    /********************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        my_profile_picture=findViewById(R.id.my_profile_picture);
        username=findViewById(R.id.username);
        bio=findViewById(R.id.bio);

        save_bio=findViewById(R.id.save_bio);
        save_username=findViewById(R.id.save_username);
        signout=findViewById(R.id.signout);

        n_username=findViewById(R.id.n_username);
        n_bio=findViewById(R.id.n_bio);

        /** profile pic changing code part 2**/
        storageReference= FirebaseStorage.getInstance().getReference("Uploads");

        /********************/

        fu= FirebaseAuth.getInstance().getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference("Users").child(fu.getUid());
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
                bio.setText(u.getBio());
                if(u.getImgURL().equals("default")){
                    my_profile_picture.setImageResource(R.drawable.blank);
                }else{
                    Glide.with(getApplicationContext()).load(u.getImgURL()).into(my_profile_picture);
                }
                if(TextUtils.isEmpty(u.getBio())){
                    bio.setText("You don't have a bio");
                }else{
                    bio.setText(u.getBio());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /** profile pic changing code part 3**/
        my_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImg();
            }
        });
        /**************/

        /** Buttons handling **/
        save_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_u=n_username.getText().toString();
                if(!TextUtils.isEmpty(new_u)){
                    //updating the username
                    ref=FirebaseDatabase.getInstance().getReference("Users").child(fu.getUid());
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("username",new_u);
                    ref.updateChildren(map);
                    n_username.setText("");
                    Toast.makeText(UserProfile.this, "username updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        save_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_b=n_bio.getText().toString();
                if(!TextUtils.isEmpty(new_b)){
                    //updating the username
                    ref=FirebaseDatabase.getInstance().getReference("Users").child(fu.getUid());
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("bio",new_b);
                    ref.updateChildren(map);
                    n_bio.setText("");
                    Toast.makeText(UserProfile.this, "bio updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i=new Intent(UserProfile.this, Login.class);
                startActivity(i);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
            }
        });
    }

/** profile pic changing code part 4**/

  private void openImg(){
      Intent in=new Intent();
      in.setType("image/*");
      in.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(in,IMG_REQ);
  }

  private String getFileExtenson(Uri uri){
      ContentResolver cr=getApplicationContext().getContentResolver();
      MimeTypeMap mtm=MimeTypeMap.getSingleton();
      return mtm.getExtensionFromMimeType(cr.getType(uri));
  }
  private void uploadImg(){
      final ProgressDialog pd=new ProgressDialog(UserProfile.this);
      pd.setMessage("Uploading...");
      pd.show();
      if(imgUri != null){
          final StorageReference fileRef=storageReference.child(System.currentTimeMillis()+"."+getFileExtenson(imgUri));
          uploadTask=fileRef.putFile(imgUri);

          uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
              @Override
              public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                  if(!task.isSuccessful()){
                      throw task.getException();
                  }
                  return fileRef.getDownloadUrl();
              }
          }).addOnCompleteListener(new OnCompleteListener<Uri>() {
              @Override
              public void onComplete(@NonNull Task<Uri> task) {
                  if(task.isSuccessful()){
                      Uri downloadUri=task.getResult();
                      String mUri=downloadUri.toString();
                      ref=FirebaseDatabase.getInstance().getReference("Users").child(fu.getUid());
                      HashMap<String,Object> map=new HashMap<>();
                      map.put("imgURL",mUri);
                      ref.updateChildren(map);
                      pd.dismiss();

                  }else{
                      Toast.makeText(getApplicationContext(),"Failed..",Toast.LENGTH_SHORT).show();
                      pd.dismiss();
                  }

              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                  pd.dismiss();
              }
          });
      }else{
          Toast.makeText(getApplicationContext(),"No image selected..",Toast.LENGTH_SHORT).show();
      }
  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMG_REQ && resultCode == RESULT_OK && data.getData() != null){
            imgUri=data.getData();
            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(),"Upload in progress..",Toast.LENGTH_SHORT).show();
            }else{
                uploadImg();
            }
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}