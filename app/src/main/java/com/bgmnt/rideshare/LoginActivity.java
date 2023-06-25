package com.bgmnt.rideshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText Email;
    TextInputLayout Emailt;
    TextInputEditText Password;
    TextInputLayout Passwordt;
    AlertDialog.Builder builder;
    TextView create;
    TextView forgetpassword;
    Button login;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(getApplicationContext());




        this.create = findViewById(R.id.create_user);
        this.login = findViewById(R.id.login_button);
        this.Email = findViewById(R.id.email);
        this.Password = findViewById(R.id.passwordl);
        this.Emailt = findViewById(R.id.emailt);
        this.Passwordt = findViewById(R.id.passwordt);
        this.forgetpassword = findViewById(R.id.forgetpass);
        this.progressBar = findViewById(R.id.progressBar2);
        this.builder = new AlertDialog.Builder(this);
        onStart();
        this.Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });

        this.create.setOnClickListener(LoginActivity.this::register);

        this.login.setOnClickListener(LoginActivity.this::Login);

        this.forgetpassword.setOnClickListener(LoginActivity.this::Forgetpassword);
    }

    public void register(View view) {
        register();
    }

    public  void Login(View view) {
        logininto();
    }

    public void Forgetpassword(View view) {
        startActivity(new Intent(getApplicationContext(), ForgetActivity.class));
    }

    private void register() {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    private void logininto() {

        String email;
        String password = Objects.requireNonNull(this.Password.getText()).toString();
        String trim = Objects.requireNonNull(this.Email.getText()).toString().trim();
        email = trim;
        if (TextUtils.isEmpty(trim)) {
            this.Emailt.setError("Email required");
        } else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            this.Passwordt.setError("Password required");
        } else {
            this.progressBar.setVisibility(View.VISIBLE);
            this.login.setVisibility(View.INVISIBLE);

            this.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("", "signInWithEmail:success");
                    FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(LoginActivity.this.mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Preference_Manager Preference_Manager = new Preference_Manager(LoginActivity.this.getApplicationContext());
                                Preference_Manager.putString("Fullname", Objects.requireNonNull(dataSnapshot.child("fullname").getValue()).toString());
                                Preference_Manager.putString("username", Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString());
                                Preference_Manager.putString("email", Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
                                Preference_Manager.putString("phone_no", Objects.requireNonNull(dataSnapshot.child("phoneno").getValue()).toString());
                                LoginActivity.this.progressBar.setVisibility(View.INVISIBLE);
                                LoginActivity.this.startActivity(new Intent(LoginActivity.this.getApplicationContext(), Fi4st.class));
                                LoginActivity.this.finish();
                                Toast.makeText(LoginActivity.this, "Login successfull!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Log.d("message", "class");
                        }
                    });
                    return;
                }
                Log.w("", "signInWithEmail:failure", task.getException());
                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                LoginActivity.this.progressBar.setVisibility(View.INVISIBLE);
                LoginActivity.this.login.setVisibility(View.VISIBLE);
            });
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        FirebaseApp.initializeApp(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if (this.mAuth.getCurrentUser() != null) {
            this.mAuth.getCurrentUser().reload();
            startActivity(new Intent(getApplicationContext(), Fi4st.class));
            finish();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
    }
    }
