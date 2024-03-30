package com.example.fyptommynorman;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton profileBtn, settingsBtn;

    private Button feedbackBtn, copyPinBtn;
    private RatingBar ratingBar;

    private EditText feedbackEt;

    private TextView groupPinTv;

    private String groupPin;

    private FirebaseAuth authUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase db;

    private FirebaseUser currentUser;
    private DatabaseReference databaseReference, pinRef;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        copyPinBtn = view.findViewById(R.id.copyPinBtn);

        settingsBtn = view.findViewById(R.id.settingsBtn);

        profileBtn   = view.findViewById(R.id.profileBtn);

        ratingBar = view.findViewById(R.id.userRating);

        feedbackBtn = view.findViewById(R.id.feedbackBtn);

        feedbackEt = view.findViewById(R.id.feedbackEt);

        groupPinTv = view.findViewById(R.id.textViewGroupPin);

        authUser = FirebaseAuth.getInstance();
        currentUser = authUser.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        db = FirebaseDatabase.getInstance();

        feedbackBtn.setOnClickListener(v -> submitFeedback());
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                startActivity(intent);
            }
        });

        if (currentUser != null){
            String currentUid = currentUser.getUid();
            DatabaseReference pinRef = db.getReference("User").child(currentUid).child("pin");
            pinRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    groupPin = dataSnapshot.getValue(String.class);
                    groupPinTv.setText("Your group pin is " + groupPin + " share this with your housemates when they sign up");
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }
            });
            copyPinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyGroupPin(groupPin);
                }
            });

        }
        return view;
    }

    private void copyGroupPin(String groupPin) {

        ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Pin:", groupPin);
        if(clipboardManager != null ){
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(requireContext(), "Pin copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitFeedback() {

        //TODO IF I CANT WORK OUT EMAIL, JUST SEND TO Firebase which ive done
        //EAJBXM4VYNLVFLYC4SZJM5D3 twillo recovery code
        float starRating = ratingBar.getRating();
        String feedback = feedbackEt.getText().toString().trim();
        if (feedback.isEmpty()){
            Toast.makeText(getContext(), "Please enter your feedback before submitting...", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User-Feedback");
        databaseReference.push().setValue("Star Rating: " + starRating + "\n\nUser Feedback: " + feedback);
        feedbackEt.getText().clear();

    }
}