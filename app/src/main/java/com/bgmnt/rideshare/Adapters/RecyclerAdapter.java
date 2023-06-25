package com.bgmnt.rideshare.Adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bgmnt.rideshare.GroupchatActivity;
import com.bgmnt.rideshare.Model.User;
import com.bgmnt.rideshare.Notification.FCM;
import com.bgmnt.rideshare.Preference_Manager;
import com.bgmnt.rideshare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.ArrayList;



public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final Context context;
    private FirebaseUser fa;
    private final ArrayList<User> joinerArrayList;
    private FirebaseAuth mAuth;
    private Preference_Manager preference_manager;
    private String username;

    public RecyclerAdapter(Context context, ArrayList<User> arrayList) {
        this.context = context;
        this.joinerArrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.createrlist_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.Name.setText(this.joinerArrayList.get(i).getName());
        viewHolder.destination.setText(this.joinerArrayList.get(i).getDestination());
        viewHolder.distance.setText(this.joinerArrayList.get(i).getDistance());
        viewHolder.people.setText(this.joinerArrayList.get(i).getNoofpeople());
        final String uId = this.joinerArrayList.get(i).getUId();

        viewHolder.cardView.setOnClickListener(view -> RecyclerAdapter.this.lambda$onBindViewHolder$0$RecyclerAdapter(uId, view));
    }

    public  void lambda$onBindViewHolder$0$RecyclerAdapter(String str, View view) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        this.mAuth = firebaseAuth;
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        this.fa = currentUser;
        this.username = currentUser.getDisplayName();
        Intent intent = new Intent(this.context, GroupchatActivity.class);
        Preference_Manager preference_manager = new Preference_Manager(this.context.getApplicationContext());
        this.preference_manager = preference_manager;
        preference_manager.putBoolean("joiner", true);
        this.preference_manager.putString("uid", str);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(intent);
        final DatabaseReference child = FirebaseDatabase.getInstance().getReference("MESSAGES_DATABASE").child("Group Tokens").child(str);

        final String[] token = new String[1];
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    private static final String TAG = "d";

                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        token[0] = task.getResult();
                        child.child(username).setValue(token[0]);
                    }
                });





        DatabaseReference tokenref = FirebaseDatabase.getInstance().getReference("MESSAGES_DATABASE").child("Group Tokens").child(str);
        tokenref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot s:snapshot.getChildren()){
                        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
                        Log.d("Token",s.getValue().toString());

                        FCM pushnotification  =  new FCM(
                                s.getValue().toString(),
                                "RideShare",
                                username +" joined the chat room",
                                context);
                        pushnotification.SendNotifications();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    public int getItemCount() {
        return this.joinerArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        CardView cardView;
        TextView destination;
        TextView distance;
        ImageView imageView;
        TextView people;

        public ViewHolder(View view) {
            super(view);
            this.Name = view.findViewById(R.id.namer);
            this.distance = view.findViewById(R.id.distance);
            this.destination = view.findViewById(R.id.destinationr);
            this.imageView = view.findViewById(R.id.imageView);
            this.people = view.findViewById(R.id.people);
            this.cardView = view.findViewById(R.id.join);
        }
    }
}