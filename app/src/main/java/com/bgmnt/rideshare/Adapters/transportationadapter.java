package com.bgmnt.rideshare.Adapters;


import android.app.AlertDialog;
import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bgmnt.rideshare.R;


public class transportationadapter extends RecyclerView.Adapter<transportationadapter.ViewHolder> {
    Context context;
    int[] logo;
    String[] name;
    AlertDialog.Builder alertDialogBuilder;
    public void dialPhoneNumber(String str) {
        Intent intent = new Intent("android.intent.action.DIAL");
        intent.setData(Uri.parse("tel:" + str));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
           context.startActivity(intent);
        }
    }

    public void redirect_to_playstore(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(str));
        intent.setPackage("com.android.vending");
        this.context.startActivity(intent);
    }

    public interface OnCustomEventListener{
        void onEvent(boolean z);
    }

    public void alert(final  OnCustomEventListener On){
        alertDialogBuilder   = new AlertDialog.Builder(context);
     alertDialogBuilder.setTitle("Info").setMessage("Press yes if you want to redirect to the Google app store").setCancelable(true).setNegativeButton("No", (dialogInterface, i) -> {

                 On.onEvent(false);
                dialogInterface.cancel();
     }).setPositiveButton("Yes", (dialogInterface, i) -> On.onEvent(true)).show();


    }

    @NonNull
    @Override

    public ViewHolder  onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transportation_item, viewGroup, false));
    }

    public transportationadapter(Context context, int[] iArr, String[] strArr) {
        this.context = context;
        this.logo = iArr;
        this.name = strArr;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.trname.setText(this.name[i]);
        viewHolder.transport_logo.setImageResource(this.logo[i]);
    }

    @Override
    public int getItemCount() {
        return this.name.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView contact;
        ImageView playstore;
        ImageView transport_logo;
        TextView trname;

        public ViewHolder(View view) {
            super(view);
            this.trname = view.findViewById(R.id.trname);
            this.transport_logo = view.findViewById(R.id.trlogo);
            this.contact = view.findViewById(R.id.contact);
            ImageView imageView = view.findViewById(R.id.playstore);
            this.playstore = imageView;

            imageView.setOnClickListener(view2 -> ViewHolder.this.lambda$new$0$transportationadapter$ViewHolder());
            this.contact.setOnClickListener(view2 -> ViewHolder.this.lambda$new$1$transportationadapter$ViewHolder());
        }

        public  void lambda$new$0$transportationadapter$ViewHolder() {
            switch (getAdapterPosition()) {
                case 0:
                    alert(z -> {
                        if(z) {
                            transportationadapter.this.redirect_to_playstore("https://play.google.com/store/apps/details?id=com.zayride.passenger&hl=en&gl=US");
                        }

                    });
                        return;
                case 1:
                    alert(z -> {
                        if(z) {
                            transportationadapter.this.redirect_to_playstore("https://play.google.com/store/apps/details?id=com.feres.user&hl=en&gl=US");
                        }

                    });
                    transportationadapter.this.redirect_to_playstore("https://play.google.com/store/apps/details?id=com.feres.user&hl=en&gl=US");
                    return;
                case 2:
                    alert(z -> {
                        if(z) {
                            transportationadapter.this.redirect_to_playstore("https://play.google.com/store/apps/details?id=com.multibrains.taxi.passenger.ridepassengeret&hl=en&gl=US");
                        }

                    });

                    return;
                case 3:
                    alert(z -> {
                        if(z) {
                            transportationadapter.this.redirect_to_playstore("https://play.google.com/store/apps/details?id=com.taxiye");
                        }

                    });


                    return;
                case 4:
                    alert(z -> {
                        if(z) {
                            transportationadapter.this.redirect_to_playstore("https://play.google.com/store/apps/details?id=com.yta.passenger.seregela");
                        }

                    });


                    return;
                case 5:
                    alert(z -> {
                        if(z) {
                            transportationadapter.this.redirect_to_playstore("https://play.google.com/store/apps/details?id=et.com.lole.ethio.user&hl=en_US&gl=US");
                        }

                    });

                    return;
                case 6:
                    alert(z -> {
                        if(z) {
                            transportationadapter.this.redirect_to_playstore("https://play.google.com/store/apps/details?id=com.shuufare.passenger&hl=en&gl=US");
                        }

                    });


                    return;
                case 7:

                    alert(z -> {
                        if(z) {
                            transportationadapter.this.redirect_to_playstore("https://play.google.com/store/apps/details?id=com.marvel.computing.michuride&hl=en_US&gl=US");
                        }

                    });
                case 8:
                    alert(z -> {
                        if(z) {
                            transportationadapter.this.redirect_to_playstore("https://play.google.com/store/apps/details?id=com.ilift.passenger");
                        }

                    });

                    return;
                default:
            }
        }

        public  void lambda$new$1$transportationadapter$ViewHolder() {
            switch (getAdapterPosition()) {
                case 0:
                    dialPhoneNumber("6303");
                    return;
                case 1:
                    dialPhoneNumber("6090");
                    return;
                case 2:
                    dialPhoneNumber("8294");
                    return;
                case 3:
                   dialPhoneNumber("6055");
                    return;
                case 4:
                    dialPhoneNumber("7878");
                    return;
                case 5:
                    dialPhoneNumber("6161");
                    return;
                case 6:
                 dialPhoneNumber("8610");
                    return;
                case 7:
                    dialPhoneNumber("9510");
                    return;
                case 8:
                    dialPhoneNumber("6212");
                    return;
                default:
            }
        }
    }
}