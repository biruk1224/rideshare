package com.bgmnt.rideshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class UserprofileActivity extends AppCompatActivity {

    private Button btnChoose, btnUpload;

    private ImageView imageView;

    //Firebase

    FirebaseStorage storage;

    StorageReference storageReference;


    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    TextInputEditText fullname,email,phoneno;
    Button update;

    DatabaseReference db;

    FirebaseAuth auth;

    Preference_Manager preference_manager;
    ProgressBar progressBar;

    private String Fullname,Email,Phoneno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        fullname = findViewById(R.id.fullname);
        email =  findViewById(R.id.uemail);
        phoneno =  findViewById(R.id.uphoneno);
        update = findViewById(R.id.update);
        progressBar =  findViewById(R.id.upprogress);
        imageView = findViewById(R.id.imageView2);
        btnUpload = findViewById(R.id.button);
        preference_manager = new Preference_Manager(getApplicationContext());

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        auth = firebaseAuth;
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
        fetch_user_data();

        update.setOnClickListener(view -> UserprofileActivity.this.validate_phone(view));
        imageView.setOnClickListener(view -> chooseImage());
        btnUpload.setOnClickListener(view -> {
            uploadImage();
        });
    }

    public  void validate_phone(View view) {
        loading();
        if (!phoneno.getText().toString().equals(Phoneno)) {
            Toast.makeText(this, "phone no can't be changed", Toast.LENGTH_SHORT).show();
            phoneno.setText(Phoneno);
            stop_loading();
            return;
        }
        boolean booleanValue = change_fullname().booleanValue();
        boolean change_email = change_email();

        if (booleanValue || change_email) {
            return;
        }
        stop_loading();
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    private void fetch_user_data() {
        db.child("profilePicture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Picasso.get().load(snapshot.getValue().toString()).placeholder(R.drawable.baseline_account_circle_24).into(imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Fullname = preference_manager.getString("Fullname");
        Email = preference_manager.getString("email");
        Phoneno = preference_manager.getString("phone_no");
        fullname.setText(Fullname);
        email.setText(Email);
        phoneno.setText(Phoneno);
    }

    private Boolean validate_fullname() {
        String ufullname = fullname.getText().toString();
        if (TextUtils.isEmpty(ufullname)) {
            this.fullname.setError("Fullname required");
            return false;
        } else if (ufullname.length() > 40) {
            this.fullname.setError("Fullname length is too long");
            return false;
        } else {

            return true;
        }
    }

    private void loading() {
        progressBar.setVisibility(View.VISIBLE);
        update.setVisibility(View.INVISIBLE);
    }

    private void stop_loading() {
        progressBar.setVisibility(View.INVISIBLE);
        update.setVisibility(View.VISIBLE);
    }

    private Boolean change_fullname() {
        if(validate_fullname())

        if (!Fullname.equals(fullname.getText().toString())) {
            db.child("fullname").setValue(fullname.getText().toString()).addOnCompleteListener((OnCompleteListener) task -> UserprofileActivity.this.save_fullname(task));
            return true;
        }
        return false;
    }

    public void save_fullname(Task task) {
        if (task.isSuccessful()) {
            preference_manager.putString("Fullname",fullname.getText().toString());
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            stop_loading();
            fetch_user_data();
            return;
        }
        stop_loading();
        try {
            throw task.getException();
        } catch (FirebaseNetworkException unused) {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        } catch (Exception unused2) {
            Toast.makeText(this, "Failed to update fullname", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean validate_email() {
        String uemail = email.getText().toString();
        if (TextUtils.isEmpty(uemail)) {
            this.email.setError("Email required");
            return false;
        } else if (uemail.length() > 40) {
            this.email.setError("Email length is too long");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
            this.email.setError("Invalid email");
            return false;
        } else {

            return true;
        }
    }


    private boolean change_email() {
        if(validate_email())
        if (!Email.equals(email.getText().toString())) {
            FirebaseAuth.getInstance().getCurrentUser().updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener() {
                public void onComplete(Task task) {
                    UserprofileActivity.this.change_email(task);
                }
            });
            return true;
        }
        return false;
    }

    public   void change_email(Task task) {
        if (task.isSuccessful()) {
            db.child("email").setValue(email.getText().toString());
            preference_manager.putString("email", email.getText().toString());
            fetch_user_data();
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            stop_loading();
            return;
        }
        stop_loading();
        try {
            throw task.getException();
        } catch (FirebaseAuthInvalidCredentialsException unused) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
        } catch (FirebaseAuthRecentLoginRequiredException unused2) {
            Toast.makeText(this, "Recent login required meaning logout and login again to change the email", Toast.LENGTH_LONG).show();
        } catch (Exception unused3) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }
    private void chooseImage() {

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);






    }
    @SuppressLint("SuspiciousIndentation")
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK

                && data != null && data.getData() != null )

        btnUpload.setVisibility(View.VISIBLE);

        {

            filePath = data.getData();

            try {

                 bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);


            }

            catch (IOException e)

            {

                e.printStackTrace();

            }
            imageView.setImageBitmap(bitmap);

        }

    }


    private void uploadImage() {

        storageReference=FirebaseStorage.getInstance().getReference();

        if(filePath != null)

        {

            final ProgressDialog progressDialog = new ProgressDialog(this);

            progressDialog.setTitle("Uploading...");

            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());

            ref.putFile(filePath)

                    .addOnSuccessListener(taskSnapshot -> {

                        if(taskSnapshot.getTask().isSuccessful()){
                            taskSnapshot.getTask().getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        updateProfilePicture(task.getResult().toString());
                                        Picasso.get().load(task.getResult().toString()).placeholder(R.drawable.baseline_account_circle_24).into(imageView);
                                    }
                                }
                            });
                        }

                        progressDialog.dismiss();
                        btnUpload.setVisibility(View.INVISIBLE);
                        Toast.makeText(UserprofileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();


                    })

                    .addOnFailureListener(e -> {

                        progressDialog.dismiss();

                        Toast.makeText(UserprofileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();

                    })

                    .addOnProgressListener(taskSnapshot -> {

                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot

                                .getTotalByteCount());

                        progressDialog.setMessage("Uploaded "+(int)progress+"%");

                    });

        }

    }

        private void updateProfilePicture(Object url){
            HashMap<String, Object> usermap = new HashMap<>();
            usermap.put("profilePicture",url);
            Preference_Manager preference_manager = new Preference_Manager(getApplicationContext());
            preference_manager.putString("profilePicture", url.toString());
               db.updateChildren(usermap);

    }

    @Override
    protected void onResume() {
        fetch_user_data();
        super.onResume();
    }
}