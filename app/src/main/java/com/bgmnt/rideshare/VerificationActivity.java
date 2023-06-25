package com.bgmnt.rideshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bgmnt.rideshare.Model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    String Verfication_Nobysystem;
    EditText Verifcation_no;
    private Button button;
    private TextView counter;
    private String email;
    private FirebaseUser firebaseUser;
    private PhoneAuthProvider.ForceResendingToken foToken;
    private FirebaseAuth mAuth;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String str, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(str, forceResendingToken);
            VerificationActivity.this.foToken = forceResendingToken;
            VerificationActivity.this.Verfication_Nobysystem = str;
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String str) {
            super.onCodeAutoRetrievalTimeOut(str);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String smsCode = phoneAuthCredential.getSmsCode();
            if (smsCode != null) {
                progressBar.setVisibility(View.VISIBLE);
                button.setVisibility(View.INVISIBLE);
                Verifcation_no.setText(smsCode);
                verifycode(smsCode);
            }
        }



        @Override
        public void onVerificationFailed(FirebaseException firebaseException) {
            Context applicationContext = getApplicationContext();
            Toast.makeText(applicationContext, "Verification no " + firebaseException.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            button.setVisibility(View.VISIBLE);
        }
    };
    private String name;
    TextView number;
    private String password;
    private String phoneno;
    private ProgressBar progressBar;
    DatabaseReference reference;
    private Button resend;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        button = findViewById(R.id.verifying);
        resend =  findViewById(R.id.resend);
        Verifcation_no = findViewById(R.id.verifno);
        progressBar = findViewById(R.id.progressBar4);
        mAuth = FirebaseAuth.getInstance();
        number = findViewById(R.id.phone_number);
        counter = findViewById(R.id.counter);
        phoneno = getIntent().getStringExtra("phoneno");
        name = getIntent().getStringExtra("fullname");
        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");


        if(phoneno.startsWith("09")){
            phoneno = phoneno.replaceFirst("0","+251");
        }
        sendVerficationCodetoUser(phoneno);
        this.reference = FirebaseDatabase.getInstance().getReference().child("Users");
        timerforotp();
        this.number.setText(phoneno);
        this.progressBar.setVisibility(View.GONE);
        this.resend.setOnClickListener(VerificationActivity.this::changePhonenoprefix);
        this.button.setOnClickListener(VerificationActivity.this::checkOTP);
    }









    public void changePhonenoprefix(View view) {

        if(phoneno.startsWith("09"))
            phoneno = phoneno.replaceFirst("0","+251");
        resend(phoneno);
        timerforotp();
    }

    public void checkOTP(View view) {
        String obj = this.Verifcation_no.getText().toString();
        if (obj.isEmpty() || obj.length() < 6) {
            this.Verifcation_no.setError("Wrong Code");
            this.Verifcation_no.requestFocus();
            return;
        }
        this.progressBar.setVisibility(View.VISIBLE);
        this.button.setVisibility(View.INVISIBLE);
        verifycode(obj);
    }

    private void timerforotp() {
        new CountDownTimer(60000L, 1000L) {
            @Override
            public void onTick(long j) {
                TextView textView = VerificationActivity.this.counter;
                textView.setText("You will get an OTP by SMS in " + ((j / 1000) % 60) + " secs");
            }

            @Override
            public void onFinish() {
                VerificationActivity.this.resend.setVisibility(View.VISIBLE);
                VerificationActivity.this.counter.setText("");
            }
        }.start();
    }

    private void sendVerficationCodetoUser(String str) {

        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(this.mAuth).setPhoneNumber(str).setTimeout(30L, TimeUnit.SECONDS).setActivity(this).setCallbacks(this.mCallbacks).build());
    }

    private void resend(String str) {
        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(this.mAuth).
                setPhoneNumber(str)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this).setCallbacks(this.mCallbacks)
                .setForceResendingToken(this.foToken).build());
        this.resend.setVisibility(View.GONE);
    }


    public void verifycode(String str) {
        try {
            signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(this.Verfication_Nobysystem, str));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            this.progressBar.setVisibility(View.INVISIBLE);
            this.button.setVisibility(View.VISIBLE);
        }
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential phoneAuthCredential) {

        this.mAuth.createUserWithEmailAndPassword(this.email, this.password).addOnCompleteListener(this, task -> VerificationActivity.this.linkCredential(phoneAuthCredential, task));
    }

    public  void linkCredential(PhoneAuthCredential phoneAuthCredential, Task<com.google.firebase.auth.AuthResult> task) {
        if (task.isSuccessful()) {
            this.firebaseUser = this.mAuth.getCurrentUser();
            FirebaseUser currentUser = this.mAuth.getCurrentUser();
            Objects.requireNonNull(currentUser);

            currentUser.linkWithCredential(phoneAuthCredential).addOnCompleteListener(VerificationActivity.this::checkCredential);

            return;
        }
        this.button.setVisibility(View.VISIBLE);
        this.progressBar.setVisibility(View.GONE);
        Exception exception = task.getException();
        Objects.requireNonNull(exception);

        try {
            throw task.getException();
        } catch (FirebaseAuthInvalidCredentialsException ignored) {

        } catch (FirebaseAuthInvalidUserException ignored) {

        } catch (FirebaseAuthUserCollisionException unused3) {
            this.firebaseUser = this.mAuth.getCurrentUser();

            new AlertDialog.Builder(this).setTitle("Account Already Exists").setMessage("The email that you have entered in the registration form is already in use please try to sign in").setPositiveButton("OK", this::dismiss).create().show();
        } catch (Exception e) {


            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void checkCredential(Task task) {
        if (task.isSuccessful()) {

            this.firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(this.username).build()).addOnCompleteListener(VerificationActivity.this::saveandupdate);
            return;
        }
        this.progressBar.setVisibility(View.GONE);
        this.button.setVisibility(View.VISIBLE);
        Exception exception = task.getException();
        Objects.requireNonNull(exception);

        try {
            throw task.getException();
        } catch (FirebaseAuthInvalidCredentialsException unused) {
            FirebaseUser currentUser = this.mAuth.getCurrentUser();
            this.firebaseUser = currentUser;
            assert currentUser != null;

            currentUser.delete().addOnCompleteListener(task2 -> VerificationActivity.this.setError());
        } catch (FirebaseAuthUserCollisionException unused2) {

            FirebaseUser currentUser2 = this.mAuth.getCurrentUser();
            this.firebaseUser = currentUser2;
            if (currentUser2 == null) {
                return;
            }

            currentUser2.delete().addOnCompleteListener(VerificationActivity.this::checkAccount);
        } catch (Exception e) {
            FirebaseUser currentUser3 = this.mAuth.getCurrentUser();
            this.firebaseUser = currentUser3;
            if (currentUser3 != null) {
                currentUser3.delete().addOnCompleteListener(task1 -> {
                    // report
                });
            }
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveandupdate(Task task) {
        if (task.isSuccessful()) {

            User user = new User();
            user.setEmail(this.email);
            user.setPhoneno(this.phoneno);
            user.setUsername(this.username);
            user.setFullname(this.name);
            FirebaseDatabase.getInstance().getReference().child("DATA").child("Usernames").child(this.firebaseUser.getUid()).child("username").setValue(this.username);
            Preference_Manager preference_manager = new Preference_Manager(getApplicationContext());
            preference_manager.putString("Fullname", this.name);
            preference_manager.putString("username", this.username);
            preference_manager.putString("email", this.email);
            preference_manager.putString("phone_no", this.phoneno);
            this.reference.child(this.firebaseUser.getUid()).setValue(user);
            Intent intent = new Intent(getApplicationContext(), Fi4st.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void setError() {

        this.Verifcation_no.setError("Wrong Code");
        this.Verifcation_no.requestFocus();
        this.progressBar.setVisibility(View.INVISIBLE);
        this.button.setVisibility(View.VISIBLE);
    }

    public void checkAccount(Task task) {
        if (task.isSuccessful()) {

            new AlertDialog.Builder(this).setTitle("Account Already Exists").setCancelable(false).setMessage("The phone number that you have entered in the registration form is already in use please try to sign in").setPositiveButton("OK", this::dismiss).create().show();
        }
    }

    public void dismiss(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        dialogInterface.dismiss();
    }





}