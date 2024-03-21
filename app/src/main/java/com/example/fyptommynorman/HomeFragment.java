package com.example.fyptommynorman;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.widget.*;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private Button feedbackBtn;
    private RatingBar ratingBar;

    private EditText feedbackEt;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment

        settingsBtn = view.findViewById(R.id.settingsBtn);

        profileBtn   = view.findViewById(R.id.profileBtn);


        ratingBar = view.findViewById(R.id.userRating);

        feedbackBtn = view.findViewById(R.id.feedbackBtn);

        feedbackEt = view.findViewById(R.id.feedbackEt);

        feedbackBtn.setOnClickListener(v -> submitFeedback());





        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open profile
            }
        });

        return view;
    }

    private void submitFeedback() {

        //TODO IF I CANT WORK OUT EMAIL, JUST SENd |TO Firebase
        //EAJBXM4VYNLVFLYC4SZJM5D3 twillo recovery code
        float starRating = ratingBar.getRating();
        String feedback = feedbackEt.getText().toString().trim();
        if (feedback.isEmpty()){
            Toast.makeText(getContext(), "Please enter your feedback", Toast.LENGTH_LONG).show();
            return;
        }
        //EMAIL PASSWORD = SwF12345!  EMAIL = walletShareFeedback@outlook.com
        String emailSubject = "User Feedback";
        String emailReview = "Star Rating: " + starRating + " User Feedback: " + feedback;
        String email = "walletShareFeedback@outlook.com";

        Intent sendEmail = new Intent(Intent.ACTION_SENDTO);
        sendEmail.setData(Uri.parse("mailto:" + email));

        sendEmail.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        sendEmail.putExtra(Intent.EXTRA_TEXT, emailReview);

        PackageManager packageManager = requireContext().getPackageManager();

        if(sendEmail.resolveActivity(packageManager) != null){
            startActivity(sendEmail);

            Toast.makeText(getContext(), "Feedback Successfully Retrieved", Toast.LENGTH_LONG).show();
            feedbackEt.getText().clear();
        } else {
            Toast.makeText(getContext(), "Server is currently down please try again later", Toast.LENGTH_LONG).show();
            feedbackEt.getText().clear();
        }





    }
}