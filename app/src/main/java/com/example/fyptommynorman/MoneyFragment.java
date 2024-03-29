package com.example.fyptommynorman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoneyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoneyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText costEt, nameEt, descEt;
    private Button addExpenseBtn;
    private ListView toPayLv, owedLv;

    private TextView userIncomeTv;
    private TextView userOwedTv;
    private List<eItem> expenseList;
    private ArrayAdapter<eItem> toPayAdapter, amountOwedAdapter;

    private DatabaseReference moneyDb;

    private TextView amountOwedTv, toPayTv;
    
    private double totalEx = 0.0;
    private double toPay = 0;
    private double amountOwed = 0;

    private int groupMembersCount;

    private String usersGpin;

    private FirebaseUser currentUser;

    private DatabaseReference databaseReference;





    public MoneyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoneyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoneyFragment newInstance(String param1, String param2) {
        MoneyFragment fragment = new MoneyFragment();
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
        View view = inflater.inflate(R.layout.fragment_money, container, false);
        nameEt = view.findViewById(R.id.costNameEt);
        costEt = view.findViewById(R.id.costAmountEt);

        addExpenseBtn = view.findViewById(R.id.addExpenseBtn);
        toPayLv = view.findViewById(R.id.needToPayLv);
        owedLv = view.findViewById(R.id.moneyOwedLv);

        amountOwedTv = view.findViewById(R.id.usersIncomeTv);
        toPayTv = view.findViewById(R.id.needToPayTv);


        //expenseList = new ArrayList<>();
        toPayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        toPayLv.setAdapter(toPayAdapter);
        
        amountOwedAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        owedLv.setAdapter(amountOwedAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();




        if (currentUser != null){
            String uid = currentUser.getUid();
            DatabaseReference userRef = databaseReference.child("User").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        usersGpin = dataSnapshot.child("pin").getValue(String.class);
                        loadExpenses();
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }
            });
        }
//        groupMembersCount = countGroupMembers(usersGpin);
        owedLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                eItem selectedExpense = (eItem) parent.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirm?").setMessage("Have you been paid for this and want to delete this item?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DatabaseReference billsRef = databaseReference.child("Expenses");
                                billsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren() ){
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

        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense();
            }
        });



        return view;
    }

    private void addExpense() {
        String item =  nameEt.getText().toString().trim();
        String amount = costEt.getText().toString().trim();

        if (item.isEmpty() || amount.isEmpty()){
            Toast.makeText(getContext(), "PLease don't leave this blank", Toast.LENGTH_LONG).show();
            return;
        }
        double cost = Double.parseDouble(amount);

        //calculate amount per person

        double amountPerPerson = cost / countGroupMembers(usersGpin);
        eItem expenseItem = new eItem(item, cost, amountPerPerson, currentUser.getUid(), usersGpin);

        DatabaseReference expenseRef = databaseReference.child("Expenses").push();
        expenseRef.setValue(expenseItem);
//        expenseRef.child("item").setValue(item);
//        expenseRef.child("total amount").setValue(cost);
//        expenseRef.child("amount per person").setValue(amountPerPerson);
//        expenseRef.child("pin").setValue(usersGpin);
//        expenseRef.child("uid").setValue(currentUser.getUid());
        Toast.makeText(getContext(), "Added Succsessfully", Toast.LENGTH_LONG).show();

        nameEt.setText("");
        costEt.setText("");


    }

    private int countGroupMembers(String usersGpin) {
        DatabaseReference usersRef = databaseReference.child("User");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    String pin = userSnapshot.child("pin").getValue(String.class);
                    if (pin != null && pin.equals(usersGpin)){
                        count++;

                    }
                }
                groupMembersCount = count;
                Toast.makeText(getContext(), String.valueOf(count), Toast.LENGTH_LONG).show();


                //updateOwedAmount();
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });
        return 1;
        //TODO add logic to divide group
    }

    interface CountListener{
        void onCountReceived(int count);
    }

    private void loadExpenses() {

        DatabaseReference expenseRef = databaseReference.child("Expenses");
        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                toPayAdapter.clear();
                amountOwedAdapter.clear();

                //countGroupMembers(usersGpin);
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    eItem expenseItem = snapshot.getValue(eItem.class);
                    if (expenseItem != null){
                        if(expenseItem.getUserId().equals(currentUser.getUid())){
                            amountOwedAdapter.add(expenseItem);
                            updateOwedAmount();
                        } else {
                            //TODO change 4 to change depending on the amount of users in a group
                            double amountPerPerson = expenseItem.getAmount() / groupMembersCount;
                            expenseItem.setAmount(amountPerPerson);
                            toPayAdapter.add(expenseItem);
                            updateToPay();
                        }
                        
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });


    }

    private void updateToPay() {
        toPayTv.setText("You need to Pay:");
    }

    private void updateOwedAmount() {
//        double amountPaid = totalEx - (totalEx / countGroupMembers(usersGpin));
        amountOwedTv.setText(String.format("You are Owed:"));
    }
}
