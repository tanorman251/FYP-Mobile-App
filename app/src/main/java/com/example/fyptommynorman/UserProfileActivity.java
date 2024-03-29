package com.example.fyptommynorman;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

public class UserProfileActivity extends AppCompatActivity {

    private TextView nametv, pintv, emailtv;
    private ImageView profilePic;

    private DatabaseReference databaseReference;

    private FirebaseUser currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Button homeBtn = findViewById(R.id.backBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);


        emailtv = findViewById(R.id.emailTv);
        nametv = findViewById(R.id.nameTv);
        pintv = findViewById(R.id.groupTv);
        profilePic = findViewById(R.id.profileImage);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();


        if (currentUser != null){
            String userEmail = currentUser.getEmail();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(currentUser.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("name").getValue().toString();

                            String group = dataSnapshot.child("pin").getValue().toString();
                            nametv.setText(name);
                            emailtv.setText(userEmail);
                            pintv.setText("Your group pin is: "+ group);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }
            });
        }



        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, NavActivity.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}