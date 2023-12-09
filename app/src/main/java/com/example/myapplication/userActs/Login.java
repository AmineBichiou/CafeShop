package com.example.myapplication.userActs;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Cafe;
import com.example.myapplication.R;
import com.example.myapplication.Supplement;
import com.google.firebase.auth.FirebaseAuth;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {

    EditText email,password;
    Button login;

    FirebaseAuth auth;
    ProgressBar progressBar;
    ImageView imageView;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(Login.this, Cafe.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        login = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.exit);
        auth = FirebaseAuth.getInstance();
        login.setOnClickListener(
                v -> {
                    progressBar.setVisibility(v.VISIBLE);
                    String emailText = email.getText().toString();
                    String passwordText = password.getText().toString();

                    if (emailText.isEmpty()) {
                        Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (passwordText.isEmpty()) {
                        Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    auth.signInWithEmailAndPassword(emailText, passwordText)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(v.GONE);
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "signInWithEmail:success");
                                        Intent intent = new Intent(Login.this, Cafe.class);
                                        startActivity(intent);

                                        FirebaseUser user = auth.getCurrentUser();

                                    } else {
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText( Login.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                });
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });

        }




}