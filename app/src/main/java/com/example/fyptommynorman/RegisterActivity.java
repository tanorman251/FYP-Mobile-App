package com.example.fyptommynorman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private EditText etname, etpword, etemail;
    private RadioGroup rggroup;
    private RadioButton rbJoin, rbCreate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etname = findViewById(R.id.fullnameET);
        etpword = findViewById(R.id.pwordET);
        etemail = findViewById(R.id.emailET);

        rggroup = findViewById(R.id.jorc);

        rbJoin =  findViewById(R.id.joinrbtn);
        rbCreate = findViewById(R.id.createrbtn);

        Button regBtn = findViewById(R.id.registerBtn);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedGroupChoice = rggroup.getCheckedRadioButtonId();
                rggroup = findViewById(R.id.jorc);

                //get data from the users input
                String fullName = etname.getText().toString();
                String userEmail = etemail.getText().toString();
                String userPword = etpword.getText().toString();
                final String[] groupPin = new String[1];

                if (TextUtils.isEmpty(fullName)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Name", Toast.LENGTH_SHORT).show();
                    etname.setError("Your name is required to register an account");
                    etname.requestFocus();
                } else if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    etemail.setError("Your email is required to register an account");
                    etemail.requestFocus();
                } else if (TextUtils.isEmpty(userPword)) {
                    Toast.makeText(RegisterActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    etpword.setError("A Password is required to register an account");
                    etpword.requestFocus();

                    //TODO add more error handeling it got deleted

                }
                else if (etpword.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Please enter a password Longer than 6 characters", Toast.LENGTH_SHORT).show();
                    etpword.setError("The password must include 6 characters");
                    etpword.requestFocus();

                } else if (rggroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(RegisterActivity.this, "Please choose and option", Toast.LENGTH_SHORT).show();
                rggroup.requestFocus();
                }  else if (selectedGroupChoice == R.id.joinrbtn) {
                    Toast.makeText(RegisterActivity.this, "test", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Enter Group Pin");
                    builder.setCancelable(true);
                    final EditText userInput = new EditText(RegisterActivity.this);
                    builder.setView(userInput);

                    builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            groupPin[0] = userInput.getText().toString();
                            Toast.makeText(RegisterActivity.this, groupPin[0], Toast.LENGTH_SHORT).show();
                            registerUser(fullName, userEmail, userPword, groupPin[0]);

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else if (selectedGroupChoice == R.id.createrbtn) {
                    Random random = new Random();
                    int pin = 100000 + random.nextInt(900000);
                    String randomPin = String.valueOf(pin);
                    Toast.makeText(RegisterActivity.this, "Your new Pin for your group is" + randomPin + " remeber to share it with your group :)", Toast.LENGTH_LONG).show();
                    groupPin[0] = randomPin;
                    registerUser(fullName, userEmail, userPword, groupPin[0]);
                    //generates random group pin and stores it in the firebase realtime database

                }
            }
        });
    }

    private void registerUser(String fullName, String userEmail, String userPword, String groupPin) {

        //add to firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(userEmail, userPword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User Successfully Registered", Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseuser = auth.getCurrentUser();
                    //add info to database about user
                    readWriteData writeUserDetails = new readWriteData(fullName, groupPin);
                    DatabaseReference userProfile = FirebaseDatabase.getInstance().getReference("User");
                    userProfile.child(firebaseuser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isCanceled()) {
                                Toast.makeText(RegisterActivity.this, "Error adding details", Toast.LENGTH_LONG).show();
                                FirebaseUser firebaseuser = auth.getCurrentUser();

                            } else if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }}