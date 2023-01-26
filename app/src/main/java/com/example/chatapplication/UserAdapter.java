package com.example.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;
/**displays all users in a page: chats fragment**/
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private Context context;
    private List<UserDetails> users;
    private boolean isChat;
    String theLastMessage;
    public UserAdapter(Context context,List<UserDetails>users,boolean isChat){
        this.context=context;
        this.users=users;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.profile_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDetails u=users.get(position);
        holder.theusername.setText(u.getUsername());
        if(u.getImgURL().equals("default")){
            holder.profile_i.setImageResource(R.drawable.blank);
        }else{
            Glide.with(context).load(u.getImgURL()).into(holder.profile_i);
        }
        if(isChat){
            lastMessage(u.getId(),holder.last_msg);
            isNotif(u.getId(),holder.notif);
        }else{
            holder.last_msg.setVisibility(View.GONE);
            holder.theusername.setGravity(Gravity.CENTER_VERTICAL);
            holder.notif.setVisibility(View.GONE);
        }

        if(isChat){
            if(u.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);

            }
        }else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
            holder.theusername.setGravity(Gravity.CENTER_VERTICAL);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(context,Convo.class);
                in.putExtra("userid",u.getId());
                context.startActivity(in);
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView theusername;
        public ImageView profile_i;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;
        private TextView notif;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            theusername=itemView.findViewById(R.id.theusername);
            profile_i=itemView.findViewById(R.id.profile_i);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
            last_msg=itemView.findViewById(R.id.last_msg);
            notif=itemView.findViewById(R.id.notif);

        }
    }

    private void lastMessage(String userid,TextView last_msg) {
        theLastMessage = "default";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseUser fu = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Messages");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot sp : snapshot.getChildren()) {
                        Message m = sp.getValue(Message.class);
                        if (m.getReceiver().equals(fu.getUid()) && m.getSender().equals(userid)) {
                            theLastMessage = m.getMsg();
                        }
                        if (m.getReceiver().equals(userid) && m.getSender().equals(fu.getUid())) {
                            theLastMessage = "You: " + m.getMsg();
                        }
                    }
                    switch (theLastMessage) {
                        case "default":
                            last_msg.setText("");
                            break;
                        default:
                            last_msg.setText(theLastMessage);
                            break;
                    }
                    theLastMessage = "default";
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void isNotif(String userid,TextView theNotif) {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseUser fu = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Messages");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot sp : snapshot.getChildren()) {
                        Message m = sp.getValue(Message.class);
                        if (m.getReceiver().equals(fu.getUid()) && m.getSender().equals(userid) && m.getIsseen() == 0) {
                             theNotif.setVisibility(View.VISIBLE);

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }
}
