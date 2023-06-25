
package com.bgmnt.rideshare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bgmnt.rideshare.GroupchatActivity;
import com.bgmnt.rideshare.Model.Message;
import com.bgmnt.rideshare.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder> {

    FirebaseAuth auth;
    Context context;
    FirebaseUser firebaseUser;
    GroupchatActivity groupchat;
    DatabaseReference messageDb;
    List<Message> messages;
    private final onmessageClick monmessageClick;
    String senderid;


    public interface onmessageClick {
        boolean messageclick(int i);
    }

    public MessageAdapter(Context context, List<Message> list, DatabaseReference databaseReference, onmessageClick onmessageclick, String str, GroupchatActivity groupchat) {
        this.context = context;
        this.messageDb = databaseReference;
        this.messages = list;
        this.monmessageClick = onmessageclick;
        this.senderid = str;
        this.groupchat = groupchat;
    }

    @Override
    public MessageAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate;
        if (i == 1) {
            inflate = LayoutInflater.from(this.context).inflate(R.layout.sender, viewGroup, false);
        } else {
            inflate = LayoutInflater.from(this.context).inflate(R.layout.receiver, viewGroup, false);
        }
        return new MessageAdapterViewHolder(inflate, this.monmessageClick, this.groupchat);
    }

    @Override
    public void onBindViewHolder(MessageAdapterViewHolder messageAdapterViewHolder, int i) {
        Message message = this.messages.get(i);
        if (getItemViewType(i) == 1) {
            if (!groupchat.contextual_Action_mode_enable) {
                messageAdapterViewHolder.checkBoxright.setVisibility(View.GONE);
            } else {
                messageAdapterViewHolder.checkBoxright.setVisibility(View.VISIBLE);
                messageAdapterViewHolder.checkBoxright.setChecked(false);
            }
            messageAdapterViewHolder.message.setText(message.getMessage());
            messageAdapterViewHolder.time.setText(message.getMessageTime());
            return;
        }
        if (!groupchat.contextual_Action_mode_enable) {
            messageAdapterViewHolder.checkBoxleft.setVisibility(View.GONE);
        } else {
            messageAdapterViewHolder.checkBoxleft.setVisibility(View.VISIBLE);
            messageAdapterViewHolder.checkBoxleft.setChecked(false);
        }
        messageAdapterViewHolder.receiver.setText(message.getName());
        messageAdapterViewHolder.receivermessage.setText(message.getMessage());
        messageAdapterViewHolder.time.setText(message.getMessageTime());
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    @Override
    public int getItemViewType(int i) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        this.auth = firebaseAuth;
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        this.firebaseUser = currentUser;
        return currentUser.getDisplayName().equals(this.messageDb.child(this.messages.get(i).getName()).getKey()) ? 1 : 2;
    }


    public class MessageAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        CheckBox checkBoxleft;
        CheckBox checkBoxright;
        TextView message;
        onmessageClick onmessageClick;
        TextView receiver;
        TextView receivermessage;
        TextView time;

        public MessageAdapterViewHolder(View view, onmessageClick onmessageclick, GroupchatActivity groupchat) {
            super(view);
            this.checkBoxright = view.findViewById(R.id.checkBox2);
            this.checkBoxleft = view.findViewById(R.id.checkBox);
            this.message =  view.findViewById(R.id.message);
            this.receivermessage =  view.findViewById(R.id.receviermessage);
            this.receiver =  view.findViewById(R.id.recevier);
            this.time =  view.findViewById(R.id.time);
            CheckBox checkBox = this.checkBoxright;
            if (checkBox != null) {
                checkBox.setOnClickListener(this);
            }
            CheckBox checkBox2 = this.checkBoxleft;
            if (checkBox2 != null) {
                checkBox2.setOnClickListener(this);
            }
            this.onmessageClick = onmessageclick;
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            this.onmessageClick.messageclick(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            MessageAdapter.this.groupchat.Make_selection(view, getAdapterPosition());
        }
    }

    public void Remove(ArrayList<Message> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (Objects.equals(this.messageDb.getKey(), this.firebaseUser.getUid())) {
                this.messages.remove(arrayList.get(i));
                this.messageDb.child(arrayList.get(i).getKey()).setValue(null);
                notifyDataSetChanged();
            } else {
                Toast.makeText(this.context, "Only the creator of the group can delete the message", Toast.LENGTH_LONG).show();
            }
        }
    }
}