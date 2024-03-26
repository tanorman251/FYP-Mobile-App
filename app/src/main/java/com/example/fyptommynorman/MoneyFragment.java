package com.example.fyptommynorman;

import android.os.Bundle;

import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private ListView expenseLv;

    private TextView userIncomeTv;
    private TextView userOwedTv;
    private List<eItem> expenseList;
    private ArrayAdapter<eItem> expenseAA;

    private DatabaseReference moneyDb;
    
    private double totalEx = 0.0;
    private double toPay = 0;
    private double amountOwed = 0;

    private String usersGpin;






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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);

            userRef.child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    //usersGpin = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }
            });
        }
        // Inflate the layout for this fragment

        //refreshUi();
        nameEt = view.findViewById(R.id.costNameEt);
        costEt = view.findViewById(R.id.costAmountEt);
        descEt = view.findViewById(R.id.costDescEt);
        addExpenseBtn = view.findViewById(R.id.addExpenseBtn);
        expenseLv = view.findViewById(R.id.expensesLv);
        userIncomeTv = view.findViewById(R.id.usersIncomeTv);
        userOwedTv = view.findViewById(R.id.needToPayTv);
                
        expenseList = new ArrayList<>();
        moneyDb = FirebaseDatabase.getInstance().getReference().child("Expenses");
        
    expenseAA = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, expenseList);
    expenseLv.setAdapter(expenseAA);
    getGroupPin();
    addExpenseBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addExpense();
        }
    });
        return view;
    }

    private void getGroupPin() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
            userRef.child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    usersGpin = dataSnapshot.getValue(String.class);
                    refreshUi();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void refreshUi() {
        amountOwed = calulateAmountUsersOwed();

        userOwedTv.setText("You Are Owed: £" + String.format("%.2f", amountOwed));

        toPay = totalEx / expenseList.size();

        userIncomeTv.setText("You need to pay: £" + String.format("%.2f", toPay));

        //only show same group

        List<eItem> filteredByGroup = new ArrayList<>();
        for (eItem item : expenseList){
            if (item.getGroupPin() != null && item.getGroupPin().equals(usersGpin)){
                filteredByGroup.add(item);
            }
        }
        expenseAA.clear();
        expenseAA.addAll(filteredByGroup);
        expenseAA.notifyDataSetChanged();;
    }

    private double calulateAmountUsersOwed() {
        double amountPaid = 0.0;
        for (eItem item : expenseList){
                amountPaid += item.getAmount();
        }
        return amountPaid - toPay;
    }

    private void addExpense() {

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        String itemName = nameEt.getText().toString();
        double amount = Double.parseDouble(String.valueOf(Double.parseDouble((costEt.getText().toString()))));
        String desc = descEt.getText().toString();

        eItem expenseItem = new eItem(itemName, amount, desc, userId, usersGpin);

        expenseList.add(expenseItem);
        expenseAA.notifyDataSetChanged();


        totalEx += amount;
        refreshUi();

        DatabaseReference newExpenseRef = moneyDb.push();
        newExpenseRef.setValue(expenseItem);

    }
}