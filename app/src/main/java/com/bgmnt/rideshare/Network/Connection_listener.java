package com.bgmnt.rideshare.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.bgmnt.rideshare.R;

public class Connection_listener extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!Connection.connected(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View inflate = LayoutInflater.from(context).inflate(R.layout.internet_check, (ViewGroup) null);
            builder.setView(inflate);
            final AlertDialog create = builder.create();
            create.setCancelable(false);
            create.getWindow().setGravity(17);
            create.show();
            inflate.findViewById(R.id.retry).setOnClickListener(view -> Connection_listener.this.lambda$onReceive$0$Connection_listener(create, context, intent, view));
        }
    }

    public  void lambda$onReceive$0$Connection_listener(AlertDialog alertDialog, Context context, Intent intent, View view) {
        alertDialog.dismiss();
        onReceive(context, intent);
    }
}