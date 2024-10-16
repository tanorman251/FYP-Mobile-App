package com.example.fyptommynorman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalanderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalanderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private CalendarView calanderView;
    private EditText billEditText;

    private String dateSelector;

    private TextView eventText;

    private Spinner eventSpinner;
    private DatabaseReference databaseReference;

   // private List<String> eventList;

    private ListView eventLv;

    private FirebaseAuth authUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase db;
    private FirebaseUser currentUser;

    private String groupPin;

    private ArrayAdapter<String> billsAdapter;
    private ArrayList<String> billsList;



    public CalanderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalanderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalanderFragment newInstance(String param1, String param2) {
        CalanderFragment fragment = new CalanderFragment();
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
        View view = inflater.inflate(R.layout.fragment_calander, container, false);

        calanderView = view.findViewById(R.id.calendarView);
        billEditText = view.findViewById(R.id.etEvent);
        eventLv = view.findViewById(R.id.eventListView);
        Button button = view.findViewById(R.id.addButton);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if(currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = databaseReference.child("User").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        groupPin = dataSnapshot.child("pin").getValue(String.class);
                        loadBills();
                    }
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }
            });
        }
        billsList = new ArrayList<>();
        billsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, billsList);
        eventLv.setAdapter(billsAdapter);
        calanderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dateSelector = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            }
        });

        eventLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedBill = (String) parent.getItemAtPosition(position)  ;
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirm?").setMessage("Are you sure you want to delete this expense for everyone in your group?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract date and text to remove from db
                        String[] split = selectedBill.split("   -   ");
                        String text = split[0];
                        String date = split[1];
                        //String[] pin = selectedBill.split("   -   ");
                       // String pin = pin;
                        DatabaseReference billsRef = databaseReference.child("bills");
                        billsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren() ){
                                    String textDb = dataSnapshot1.child("text").getValue(String.class);
                                    String dateDb = dataSnapshot1.child("date").getValue(String.class);
                                    String pinDb = dataSnapshot1.child("pin").getValue(String.class);

                                    if (textDb != null && dateDb!= null && pinDb!= null && textDb.equals(text) && dateDb.equals(date) && groupPin.equals(pinDb)){
                                        dataSnapshot1.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), "Item successfully deleted", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                Toast.makeText(getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                            }
                        });
                    }
                })
                        .setNegativeButton("No", null)
                        .show();


                return true;
            }
        });

    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addBill();
        }
    });
        return view;
    }

    private void addBill() {
        String billName = billEditText.getText().toString().trim();
        if (!billName.isEmpty()){
            //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//          String date = dateFormat.format(new Date());
            String date = dateSelector;

            String key = databaseReference.child("bills").push().getKey();
            if (key != null){
                Map<String, Object> billsMap = new HashMap<>();
                billsMap.put("text", billName);
                billsMap.put("date", date);
                billsMap.put("pin", groupPin);
                databaseReference.child("bills").child(key).setValue(billsMap).addOnSuccessListener(aVoid -> {
                    billEditText.setText("");
                }).addOnFailureListener(e -> {
                });
            }
        } else {

        }
    }

    private void loadBills() {
        DatabaseReference billsRef = databaseReference.child("bills");
        billsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                billsList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String text = snapshot.child("text").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String pin = snapshot.child("pin").getValue(String.class);

                    if(groupPin != null && groupPin.equals(pin)){
                        billsList.add(text + "   -   " + date);
                    }
                }
                billsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });
    }
}



