package com.example.chatapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Message> list;
    FirebaseUser fbU;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public MessagesAdapter(Context context, ArrayList<Message> list) {
        this.context = context;
        this.list = list;
    }



    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.me, parent, false);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.him, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        Message message = list.get(position);
        holder.the_message.setText(message.getMsg());
        if (position == list.size()-1) {
            if (message.getIsseen()==1) {
                holder.txt_seen.setText("seen");
            } else {
                holder.txt_seen.setText("sent");
            }
        }else{
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView the_message;
        TextView txt_seen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

          the_message=itemView.findViewById(R.id.the_message);
          txt_seen=itemView.findViewById(R.id.txt_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fbU= FirebaseAuth.getInstance().getCurrentUser();
        if(list.get(position).getSender().equals(fbU.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return  MSG_TYPE_LEFT;
        }

    }
}
