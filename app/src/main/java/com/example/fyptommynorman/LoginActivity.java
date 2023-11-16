package com.example.fyptommynorman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText etEmail, etPword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.emailET);
        etPword = findViewById(R.id.pwordET);
        firebaseAuth = FirebaseAuth.getInstance();

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pword = etPword.getText().toString();
                String email = etEmail.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Eamail is required", Toast.LENGTH_SHORT).show();
                    etEmail.setError("Please enter an email");
                    etEmail.requestFocus();
                } else{
                    loginUser(email, pword);

                    }
                }
            });
        }
  //TODO add more error handeling such as see if the email fits the tight pattern i.e @ and check if everything is empty
    private void loginUser(String email, String pword) {
        firebaseAuth.signInWithEmailAndPassword(email, pword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "sucsess", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, NavActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    }
