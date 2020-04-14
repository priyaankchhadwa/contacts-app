package com.example.inclass12;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_fname;
    private EditText et_lname;
    private EditText et_email;
    private EditText et_pass;
    private EditText et_cpass;

    private Button btn_register;
    private Button btn_cancel;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Sign up");

        mAuth = FirebaseAuth.getInstance();

        et_fname = findViewById(R.id.et_fname);
        et_lname = findViewById(R.id.et_lname);
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        et_cpass = findViewById(R.id.et_cpass);

        btn_register = findViewById(R.id.btn_register);
        btn_cancel = findViewById(R.id.btn_cancel);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_register.setClickable(false);
                btn_register.setAlpha(0.5f);

                final String fname = et_fname.getText().toString();
                final String lname = et_lname.getText().toString();
                String email = et_email.getText().toString();
                String password = et_pass.getText().toString();
                String c_pass = et_cpass.getText().toString();

                Boolean b_email = false;
                Boolean b_pass = false;
                Boolean b_pass_len = false;

                if (!Pattern.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", email)) {
                    et_email.setError("Invalid email");
                    b_email = false;
                } else {
                    et_email.setError(null);
                    b_email = true;
                }

                if (password.length() < 6) {
                    et_pass.setError("Passwords has to be greater than 6 characters");
                    b_pass_len = false;
                } else {
                    et_pass.setError(null);
                    b_pass_len = true;
                }

                if (!password.equals(c_pass)) {
                    et_cpass.setError("Passwords do not match!");
                    b_pass = false;
                } else {
                    et_cpass.setError(null);
                    b_pass = true;
                }

                if (b_email && b_pass && b_pass_len) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("demo", "createUserWithEmail: success, " + mAuth.getCurrentUser().getEmail());
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(fname + " " + lname)
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("demo", "User profile updated. " + mAuth.getCurrentUser().getUid());
                                                        }
                                                    }
                                                });

                                        Toast.makeText(getApplicationContext(), "User created successfully!", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(RegisterActivity.this, ContactsActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Log.w("demo", "createUserWithEmail: failure", task.getException());
                                        Toast.makeText(getApplicationContext(), "Error occurred, try again later!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
