package com.example.myapplication.userActs;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Cafe;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUp extends AppCompatActivity {
    EditText email,password,confirmPassword;
    Button signup;
    FirebaseAuth auth;
    ProgressBar progressBar;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        confirmPassword = findViewById(R.id.passConf);
        signup = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.exit);
        signup.setOnClickListener(v -> {
            progressBar.setVisibility(v.VISIBLE);
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();

            if (emailText.isEmpty()) {
                Toast.makeText(SignUp.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(SignUp.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (passwordText.isEmpty()) {
                Toast.makeText(SignUp.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (passwordText.length() < 6) {
                Toast.makeText(SignUp.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!passwordText.equals(confirmPassword.getText().toString())) {
                Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                Intent intent = new Intent(SignUp.this, Cafe.class);
                                startActivity(intent);

                                FirebaseUser user = auth.getCurrentUser();

                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUp.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT);

                            }
                        }
                    });


        });
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);
        });

    }




}

