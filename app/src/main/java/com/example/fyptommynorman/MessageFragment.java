package com.example.fyptommynorman;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import android.R.layout.*;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ImageButton sendMsg;
    private TextView groupName;
    private ListView displayMsg;
    private EditText typeMsg;
    private DatabaseReference databaseReference, databaseReference2;
    private FirebaseAuth authUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase db;
    private FirebaseUser currentUser;
    private List<String> messageList;
    private ArrayAdapter<String> messageAdapter;

    private String groupPin;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
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
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        groupName = view.findViewById(R.id.groupPin);
        displayMsg = view.findViewById(R.id.messagesLv);
        typeMsg = view.findViewById(R.id.messageET);



        authUser = FirebaseAuth.getInstance();
        currentUser = authUser.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        db = FirebaseDatabase.getInstance();
        if (currentUser != null){

            String currentUserId = currentUser.getUid();
            DatabaseReference gpinRef = db.getReference("User").child(currentUserId).child("pin");
            gpinRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    groupPin = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }
            });
            DatabaseReference pin = db.getReference("User").child(currentUserId).child("pin");
            databaseReference = pin;
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String currentUserPin =snapshot.getValue(String.class);
                    groupName.setText("Group Pin: "+ currentUserPin);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                // TODO ADD ERROR HANDELING
                }
            });

            databaseReference2 = FirebaseDatabase.getInstance().getReference("Messages");
            messageList = new ArrayList<>();
            messageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1 ,messageList);
            messageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            displayMsg.setAdapter(messageAdapter);

            loadMsg();

            databaseReference2.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    //if (snapshot.exists()){
                        //HashMap<String, Object> dataM = (HashMap<String, Object>) snapshot.getValue();

                        //if(dataM != null && dataM.containsKey("Messages")) {
                        //String message = (String) dataM.get("Messages");
                       // messageList.add(message);
                        //messageAdapter.notifyDataSetChanged();
                    //}}
                    //HashMap<String, Object> dataM = (HashMap<String, Object>) snapshot.getValue();
                    //String message = (String) dataM.get("Messages");
                    //String message = snapshot.getValue(String.class);
                    try {
                        String message = snapshot.getValue(String.class);
                        messageList.add(message);
                        messageAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //TODO hanel errors aand other event listeners
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            sendMsg = view.findViewById(R.id.sendBtn);
            sendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage();
                }
            });

            //TODO COMPLETE THIS FUNCTION
        }
        // Inflate the layout for this fragment
        return view;
    }

    private void sendMessage() {

        String text = typeMsg.getText().toString().trim();
        if (!text.isEmpty()){
            if (currentUser != null && groupPin != null){
                String message = groupPin + ":" + text;
                databaseReference2.push().setValue(message);

            }
        } else {
            Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();

            /*if (currentUser != null){
                String currentUserId = currentUser.getUid();
                DatabaseReference userRef = db.getReference("User").child(currentUserId);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                        String userPin = dataSnapshot.child("pin").getValue(String.class);
                        databaseReference2.child(userPin).push().setValue(text);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                    //handel error TODO handel errors
                    }
                });
            }*/

            //databaseReference2.push().setValue(text);
        }
    }

    private void loadMsg() {
        if(groupPin != null) {
            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    messageList.clear();
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()){
                        String message = messageSnapshot.getValue(String.class);
                        if (message != null){
                            String[] delimeter = message.split(":");
                            if (delimeter.length == 2 && delimeter[0].equals(groupPin)){
                                Toast.makeText(getContext(), delimeter[1], Toast.LENGTH_LONG).show();

                                messageList.add(delimeter[1]);

                            }
                        }
                    }
                    messageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }
            });


        }}};

       /* databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot msgSnapshot : snapshot.getChildren()){
                    String message = msgSnapshot.getValue(String.class);
                    messageList.add(message);

                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
        // TODO handel errors here
            }
        });*/



//TODO delete message after sending, add pin to each message, add name of user and time maybe??