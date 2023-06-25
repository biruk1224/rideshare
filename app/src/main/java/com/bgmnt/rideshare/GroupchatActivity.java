package com.bgmnt.rideshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bgmnt.rideshare.Adapters.MessageAdapter;
import com.bgmnt.rideshare.Model.Methods;
import com.bgmnt.rideshare.Model.User;
import com.bgmnt.rideshare.Network.Connection_listener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.bgmnt.rideshare.Model.Message;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GroupchatActivity extends AppCompatActivity implements View.OnClickListener, MessageAdapter.onmessageClick {
    private static ArrayList<Message> selectionlist;
    private ActionMode actionMode;
    FirebaseAuth auth;
    private AlertDialog.Builder builder;
    FirebaseDatabase database;
    EditText etMessage;
    FirebaseUser fab;
    private FirebaseUser fb;
    ImageButton imgButton;
    RecyclerView.LayoutManager layoutManager;
    MessageAdapter messageAdapter;
    DatabaseReference messagedb;
    private List<Message> messages;
    private String name;
    private int position;
    private Preference_Manager preference_manager;
    RecyclerView rvMessage;
    private String sender_uid;

    User u;


    public boolean contextual_Action_mode_enable = false;
    Connection_listener connection_listener = new Connection_listener();
    private final ActionMode.Callback actioncallback = new AnonymousClass3();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);

        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        builder = new AlertDialog.Builder(this);
        selectionlist = new ArrayList<>();
        Preference_Manager preference_manager = new Preference_Manager(getApplicationContext());
        this.preference_manager = preference_manager;
        if (preference_manager.getBoolean("joiner")) {
            this.sender_uid = this.preference_manager.getString("uid");
        }

        init();
        check_join();


    }

    public void init() {
        this.u = new User();
        this.messages = new ArrayList<>();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        this.auth = firebaseAuth;
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        this.fab = currentUser;
        assert currentUser != null;
        this.name = currentUser.getDisplayName();
        this.database = FirebaseDatabase.getInstance();
        if (!this.preference_manager.getBoolean("joiner")) {
            this.sender_uid = this.fab.getUid();
        }
        this.layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        this.rvMessage = findViewById(R.id.rvMessage);
        this.etMessage = findViewById(R.id.etMessage);
        ImageButton imageButton = findViewById(R.id.btnSend);
        this.imgButton = imageButton;
        imageButton.setOnClickListener(this);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View view) {
        if (!TextUtils.isEmpty(this.etMessage.getText().toString())) {
            String obj = this.etMessage.getText().toString();
            Message message = new Message();
            message.setMessage(obj);
            message.setName(this.name);
            message.setMessageTime(new SimpleDateFormat("h:mm a").format(new Date()));
            this.etMessage.setText("");
            this.messagedb.push().setValue(message);
            return;
        }
        Toast.makeText(getApplicationContext(), "You cannot send blank message", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStart() {
        registerReceiver(this.connection_listener, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        super.onStart();
        FirebaseUser currentUser = this.auth.getCurrentUser();
        this.fb = currentUser;
        assert currentUser != null;
        this.u.setUid(currentUser.getUid());
        this.u.setEmail(this.fb.getEmail());
        fetchandisplay();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    public void onStop() {
        unregisterReceiver(this.connection_listener);
        super.onStop();
    }

    private void fetchandisplay() {
        this.database.getReference("Users").child(this.fb.getUid()).addListenerForSingleValueEvent(new ValueEventListener() { 

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupchatActivity.this.u =  dataSnapshot.getValue(User.class);
                assert GroupchatActivity.this.u != null;
                GroupchatActivity.this.u.setUid(GroupchatActivity.this.fb.getUid());
                Methods.name = GroupchatActivity.this.u.getName();
            }
        });
        DatabaseReference child = this.database.getReference("MESSAGES_DATABASE").child("Group Messages").child(this.sender_uid);
        this.messagedb = child;
        child.addChildEventListener(new ChildEventListener() {


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String str) {
            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String str) {
                Message message =  dataSnapshot.getValue(Message.class);
                assert message != null;
                message.setKey(dataSnapshot.getKey());
                Object value = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();

                message.setName(value.toString());
                Object value2 = dataSnapshot.child("messageTime").getValue();
                Objects.requireNonNull(value2);
                message.setMessageTime(value2.toString());
                GroupchatActivity.this.messages.add(message);
                GroupchatActivity.this.rvMessage.setItemAnimator(new DefaultItemAnimator());
                GroupchatActivity.this.layoutManager.scrollToPosition(GroupchatActivity.this.messages.size() - 1);
                GroupchatActivity groupchat = GroupchatActivity.this;
                groupchat.displayMessages(groupchat.messages);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String str) {
                Message message = dataSnapshot.getValue(Message.class);
                assert message != null;
                message.setKey(dataSnapshot.getKey());
                message.setName(GroupchatActivity.this.name);
                ArrayList arrayList = new ArrayList<>();
                for (Message message2 : GroupchatActivity.this.messages) {
                    if (message2.getKey().equals(message.getKey())) {
                        arrayList.add(message);
                    } else {
                        arrayList.add(message2);
                    }
                }
                GroupchatActivity.this.messages = arrayList;
                GroupchatActivity.this.rvMessage.setItemAnimator(new DefaultItemAnimator());
                GroupchatActivity groupchat = GroupchatActivity.this;
                groupchat.displayMessages(groupchat.messages);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                assert message != null;
                message.setKey(dataSnapshot.getKey());
                ArrayList arrayList = new ArrayList();
                for (Message message2 : GroupchatActivity.this.messages) {
                    if (!message2.getKey().equals(message.getKey())) {
                        arrayList.add(message2);
                    }
                }
                GroupchatActivity.this.messages = arrayList;
                GroupchatActivity.this.rvMessage.setItemAnimator(new DefaultItemAnimator());
                GroupchatActivity groupchat = GroupchatActivity.this;
                groupchat.displayMessages(groupchat.messages);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        this.messages = new ArrayList();
    }


    public void displayMessages(List<Message> list) {
        this.rvMessage.setLayoutManager(this.layoutManager);
        this.layoutManager.scrollToPosition(list.size() - 1);
        MessageAdapter messageAdapter = new MessageAdapter(getApplicationContext(), list, this.messagedb, this, this.name, this);
        this.messageAdapter = messageAdapter;
        this.rvMessage.setAdapter(messageAdapter);
    }

    @Override
    public boolean messageclick(int i) {
        if (this.actionMode != null) {
            return false;
        }
        this.actionMode = startSupportActionMode(this.actioncallback);
        this.contextual_Action_mode_enable = true;
        this.position = i;
        this.messageAdapter.notifyDataSetChanged();
        return true;
    }

    public class AnonymousClass3 implements ActionMode.Callback {
        @Override // androidx.appcompat.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        AnonymousClass3() {
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.options, menu);
            actionMode.setTitle("Choose the options");
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {




                if (item.getItemId() == R.id.action_delete) {


                    new androidx.appcompat.app.AlertDialog.Builder(GroupchatActivity.this).setTitle("Confirm Delete?")
                            .setMessage("Selected message will be deleted permanently")
                            .setPositiveButton("YES",
                                    (dialog, which) -> {


                                        messageAdapter.Remove(selectionlist);
                                        messageAdapter.notifyItemRemoved(position);
                                        mode.finish();
                                        // Perform Action & Dismiss dialog
                                        dialog.dismiss();

                                    })
                            .setNegativeButton("NO", (dialog, which) -> {
                                // Do nothing
                                mode.finish();
                                dialog.dismiss();

                            })
                            .create()
                            .show();


                    return true;
                }
                else if (item.getItemId()==android.R.id.home) {

                contextual_Action_mode_enable = false;
                selectionlist.clear();
                messageAdapter.notifyDataSetChanged();
                return true;
            }
                else {return false;}











        }




        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            GroupchatActivity.this.contextual_Action_mode_enable = false;
            GroupchatActivity.selectionlist.clear();
            GroupchatActivity.this.messageAdapter.notifyDataSetChanged();
            GroupchatActivity.this.actionMode = null;
        }
    }


    public void checkuser() {
        if (!isFinishing()) {
            this.builder.setTitle(R.string.you_can_t_access_the_chat_room).setMessage(R.string.first_you_have_to_create_or_join).setCancelable(false).setPositiveButton("Okay", new DialogInterface.OnClickListener() { // from class: com.test.divvyup.Groupchat$$ExternalSyntheticLambda0
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    GroupchatActivity.this.backToMain(dialogInterface, i);
                }
            }).create().show();
        }
    }

    public void backToMain(DialogInterface dialogInterface, int i) {
        startActivity(new Intent(getApplicationContext(), Fi4st.class));
        finish();
        dialogInterface.dismiss();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    public void Make_selection(View view, int i) {
        if (((CheckBox) view).isChecked()) {
            selectionlist.add(this.messages.get(i));
            return;
        }
        selectionlist.remove(this.messages.get(i));
    }

    private void check_join() {
        FirebaseDatabase.getInstance().getReference("Joiner Available");
        this.preference_manager.getString("username");
        if (this.preference_manager.getBoolean("joiner").booleanValue()) {
            Log.d("Success", "Yeah");
        } else {
            FirebaseDatabase.getInstance().getReference("Creater Available").child("Status").child(this.fab.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        GroupchatActivity.this.checkuser();
                    } else {
                        Log.d(FirebaseAnalytics.Param.SUCCESS, "2");
                    }
                }
            });
        }
    }
}