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
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    private EditText et_email;
    private EditText et_password;

    private Button btn_login;
    private Button btn_register;

    private FirebaseAuth mAuth;
//    FirebaseStorage storage;
//    StorageReference storageRef;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d("demo", "Current User: " + currentUser.getEmail());
            Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
            startActivity(intent);
            finish();

        } else {
            Log.d("demo", "No user found, login!!");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");

        mAuth = FirebaseAuth.getInstance();
//        storage = FirebaseStorage.getInstance();
//        storageRef = storage.getReference();

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
//                startActivity(intent);
//                finish();

                btn_login.setClickable(false);
                btn_login.setAlpha(0.5f);

                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                if (!Pattern.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", email)) {
                    et_email.setError("Invalid email");
                } else if (password.equals("")) {
                    et_password.setError("Enter a password");
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("demo", "signInWithEmail: success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Log.d("demo", "user: " + user.getEmail());

                                        Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.d("demo", "signInWithEmail:failure" + task.getException());
                                        Toast.makeText(MainActivity.this, "Incorrect username and/or password.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }


            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
