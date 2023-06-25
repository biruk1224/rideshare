package com.bgmnt.rideshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bgmnt.rideshare.Network.Connection_listener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText Confirmpassword;
    TextInputLayout Confirmpasswordt;
    TextInputEditText Email;
    TextInputLayout Emailt;
    TextInputEditText Fullname;
    TextInputLayout Fullnamet;
    TextInputEditText Password;
    TextInputLayout Passwordt;
    TextInputEditText Phone_no;
    TextInputLayout Phone_not;
    TextInputEditText Username;
    TextInputLayout Usernamet;
    private String confirmedpassword;
    Connection_listener connection_listener = new Connection_listener();
    private String email;
    private String fullname;
    TextView login;
    private FirebaseAuth mAuth;
    private String password;
    private String phone_no;
    private ProgressBar progressBar;
    DatabaseReference reference;
    private Button register;
    private String sd;
    private String username;
    public interface UserExistsCallback {
        void onCallback(boolean z);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.Fullname = findViewById(R.id.Fullname);
        this.Username = findViewById(R.id.Username);
        this.Phone_no = findViewById(R.id.phone_no);
        this.Email = findViewById(R.id.email);
        this.Password = findViewById(R.id.password);
        this.Confirmpassword = findViewById(R.id.confirmpassword);
        this.Fullnamet = findViewById(R.id.Fullnamet);
        this.Usernamet = findViewById(R.id.Usernamet);
        this.Phone_not = findViewById(R.id.phone_not);
        this.Emailt = findViewById(R.id.emailt);
        this.Passwordt = findViewById(R.id.passwordt);
        this.Confirmpasswordt = findViewById(R.id.confirmpasswordt);
        this.progressBar = findViewById(R.id.progressBar);
        this.login = findViewById(R.id.already);
        this.mAuth = FirebaseAuth.getInstance();
        this.register = findViewById(R.id.register);
        this.reference = FirebaseDatabase.getInstance().getReference().child("Users");
        init();
        this.Username.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Usernamet.setErrorEnabled(false);
            }
        });
        this.Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                sd = charSequence.toString();
                if (sd.length() > 8) {
                    Passwordt.setHelperText(" ");
                }
            }
        });
        this.Confirmpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String charSequence2 = charSequence.toString();
                if (sd != null) {
                    if (charSequence2.matches(sd)) {
                        Confirmpasswordt.setHelperText("matched");
                        Confirmpasswordt.setHelperTextColor(ColorStateList.valueOf(Color.GREEN));
                        return;
                    }
                    Confirmpasswordt.setHelperText("not matched");
                    Confirmpasswordt.setHelperTextColor(ColorStateList.valueOf(Color.RED));
                }
            }
        });

        this.register.setOnClickListener(this::lambda$onCreate$0$register);

        this.login.setOnClickListener(this::lambda$onCreate$1$register);
    }

    public void lambda$onCreate$0$register(View view) {
        onStart();
        this.progressBar.setVisibility(View.VISIBLE);
        this.register.setVisibility(View.INVISIBLE);
        register_();
    }

    public void lambda$onCreate$1$register(View view) {
        login();
    }

    private void login() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        registerReceiver(this.connection_listener, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        FirebaseUser currentUser = this.mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
            this.mAuth.getCurrentUser().reload();
            startActivity(new Intent(getApplicationContext(), Fi4st.class));
            finish();
        }
    }

    private void register_() {

        isValidUsername(z -> lambda$register_$2$register(z));

    }

    public  void lambda$register_$2$register(boolean z) {
        if (validate().booleanValue()) {
            this.progressBar.setVisibility(View.INVISIBLE);
            this.register.setVisibility(View.VISIBLE);
        } else if (!z) {
            this.progressBar.setVisibility(View.INVISIBLE);
            this.register.setVisibility(View.VISIBLE);
        } else {
            signup();
            this.progressBar.setVisibility(View.INVISIBLE);
            this.register.setVisibility(View.VISIBLE);
        }
    }

    private Boolean validate() {
        if ((!validate_fullname().booleanValue()) | (!validate_username().booleanValue()) | (!validate_phone_no().booleanValue()) | (!validate_email().booleanValue()) | (!validate_password().booleanValue())) {
            Toast.makeText(this, "Registration has been interrupted", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void init() {
        Editable text = this.Fullname.getText();
        Objects.requireNonNull(text);
        this.fullname = text.toString().trim();
        Editable text2 = this.Username.getText();
        Objects.requireNonNull(text2);
        this.username = text2.toString().toLowerCase().trim();
        Editable text3 = this.Email.getText();
        Objects.requireNonNull(text3);
        this.email = text3.toString().trim();
        Editable text4 = this.Phone_no.getText();
        Objects.requireNonNull(text4);
        this.phone_no = text4.toString().trim();
        Editable text5 = this.Password.getText();
        Objects.requireNonNull(text5);
        this.password = text5.toString();
        Editable text6 = this.Confirmpassword.getText();
        Objects.requireNonNull(text6);
        this.confirmedpassword = text6.toString();
    }

    private void signup() {
        Intent intent = new Intent(getApplicationContext(), VerificationActivity.class);
        intent.putExtra("fullname", this.fullname);
        intent.putExtra("username", this.username);
        intent.putExtra("email", this.email);
        intent.putExtra("password", this.password);
        intent.putExtra("phoneno", this.phone_no);
        startActivity(intent);
        this.progressBar.setVisibility(View.INVISIBLE);
    }

    private Boolean validate_fullname() {
        if (TextUtils.isEmpty(this.fullname)) {
            this.Fullnamet.setError("Fullname required");
            return false;
        } else if (this.fullname.length() > 40) {
            this.Fullnamet.setError("Fullname length is too long");
            return false;
        } else {
            this.Fullnamet.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validate_username() {
        if (TextUtils.isEmpty(this.username)) {
            this.Usernamet.setError("Username required");
            return false;
        } else if (this.username.length() > 10) {
            this.Usernamet.setError("Username length is too long");
            return false;
        } else if (this.username.contains(" ")) {
            this.Usernamet.setError("Space not allowed");
            return false;
        } else {
            this.Usernamet.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validate_phone_no() {
        if (TextUtils.isEmpty(this.phone_no)) {
            this.Phone_not.setError("Phone number required");
            return false;
        } else if (this.phone_no.length() > 40) {
            this.Phone_not.setError("Phone number length is too long");
            return false;
        } else if (!Patterns.PHONE.matcher(this.phone_no).matches()) {
            this.Phone_not.setError("Phone number is not valid");
            return false;
        } else {
            this.Phone_not.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validate_email() {
        if (TextUtils.isEmpty(this.email)) {
            this.Emailt.setError("Email required");
            return false;
        } else if (this.email.length() > 40) {
            this.Emailt.setError("Email length is too long");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(this.email).matches()) {
            this.Emailt.setError("Invalid email");
            return false;
        } else {
            this.Emailt.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validate_password() {
        if (TextUtils.isEmpty(this.password)) {
            this.Passwordt.setError("Password required");
            return false;
        } else if (this.password.length() < 8) {
            this.Passwordt.setError("Password must be atleast length of 8 ");
            return false;
        } else if (this.password.length() > 80) {
            this.Passwordt.setError("Password length is too long");
            return false;
        } else if (!this.confirmedpassword.equals(this.password)) {
            this.Confirmpasswordt.setError("password not match");
            return false;
        } else {
            this.Passwordt.setErrorEnabled(false);
            return true;
        }
    }

    public void isValidUsername(final UserExistsCallback userExistsCallback) {
        FirebaseDatabase.getInstance().getReference().child("DATA").child("Usernames").orderByChild("username").equalTo(this.username).addValueEventListener(new ValueEventListener() { // from class: com.test.divvyup.register.4
            @Override // com.google.firebase.database.ValueEventListener
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userExistsCallback.onCallback(false);
                    Usernamet.setError("This username already exists");
                    return;
                }
                userExistsCallback.onCallback(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegisterActivity.this, "Hello  "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onStop() {
        unregisterReceiver(this.connection_listener);
        super.onStop();
    }




    }
