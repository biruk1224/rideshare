package com.bgmnt.rideshare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetActivity extends AppCompatActivity {
    private TextInputLayout reset_email;
    private TextInputEditText email_to_reset;
    TextView Forgetpassword;
    private Button reset;
    private String Email_to_reset;
    private FirebaseAuth Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        email_to_reset = findViewById(R.id.reemail);
        reset = findViewById(R.id.reset);
        reset_email = findViewById(R.id.resett);
        Forgetpassword =  findViewById(R.id.forget_passwordl);

        Auth = FirebaseAuth.getInstance();
        if (getIntent().getBooleanExtra("Change_Password", false)) {
            Forgetpassword.setText("Change Password");
        }
        reset.setOnClickListener(v -> {
            Email_to_reset = email_to_reset.getText().toString().trim();
            if(validate_email()){
                sendemailtoreset(Email_to_reset);
            }
            else{
                return;
            }
        });

    }





    private Boolean validate_email(){




        if(TextUtils.isEmpty(Email_to_reset)) {
            reset_email.setError("Email required");
            return false;

        }
        else if(Email_to_reset.length()>40){
            reset_email.setError("Email length is too long");
            return false;

        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(Email_to_reset).matches()){
            reset_email.setError("Invalid email");
            return false;
        }
        else{
            reset_email.setErrorEnabled(false);
            return true;
        }


    }
    private void sendemailtoreset(String email){

        Auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> send(task));


    }
    private void send(Task task) {
        if (task.isSuccessful()) {
            Toast.makeText(this, "Check your email to reset your password", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}