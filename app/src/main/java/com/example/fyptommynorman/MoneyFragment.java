package com.example.fyptommynorman;

import android.os.Bundle;

import android.util.Log;
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
    private ListView toPayLv, owedLv;

    private TextView userIncomeTv;
    private TextView userOwedTv;
    private List<eItem> expenseList;
    private ArrayAdapter<eItem> toPayAdapter, amountOwedAdapter;

    private DatabaseReference moneyDb;
    
    private double totalEx = 0.0;
    private double toPay = 0;
    private double amountOwed = 0;

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
        userIncomeTv = view.findViewById(R.id.usersIncomeTv);
        userOwedTv = view.findViewById(R.id.needToPayTv);


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
            Toast.makeText(getContext(), "PLease dont leave blank", Toast.LENGTH_LONG).show();
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
                updateOwedAmount();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });
        return 1;
        //TODO add logic to divide group
    }

    private void loadExpenses() {

        DatabaseReference expenseRef = databaseReference.child("Expenses");
        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                toPayAdapter.clear();
                amountOwedAdapter.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    eItem expenseItem = snapshot.getValue(eItem.class);
                    if (expenseItem != null){
                        if(expenseItem.getUserId().equals(currentUser.getUid())){
                            amountOwedAdapter.add(expenseItem);
                            updateOwedAmount();
                        } else {
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
    }

    private void updateOwedAmount() {
        double amountPaid = totalEx - (totalEx / countGroupMembers(usersGpin));
        userOwedTv.setText(String.format("you need to pay: £%.2f", amountPaid));
    }
}






















//        //refreshUi();
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null){
//            String userId = currentUser.getUid();
//            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);
//            //getGroupPin(currentUser);
//        getGroupPin();
//            userRef.child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
//
//                    if (dataSnapshot.exists()){
//                        usersGpin = dataSnapshot.child("pin").getValue(String.class);
//                        refreshUi();
//                    }
//                  //  usersGpin = dataSnapshot.getValue(String.class);
//                    //if(currentUser != null && currentUser.getGroupPin() != null && currentUser){
//                        refreshUi();
//               //     }
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//        // Inflate the layout for this fragment
//
//
//        nameEt = view.findViewById(R.id.costNameEt);
//        costEt = view.findViewById(R.id.costAmountEt);
//        descEt = view.findViewById(R.id.costDescEt);
//        addExpenseBtn = view.findViewById(R.id.addExpenseBtn);
//        expenseLv = view.findViewById(R.id.expensesLv);
//        userIncomeTv = view.findViewById(R.id.usersIncomeTv);
//        userOwedTv = view.findViewById(R.id.needToPayTv);
//
//       // refreshUi();
//
//        expenseList = new ArrayList<>();
//        moneyDb = FirebaseDatabase.getInstance().getReference().child("Expenses");
//
//        expenseAA = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, expenseList);
//        expenseLv.setAdapter(expenseAA);
//        refreshUi();
//
//    moneyDb.addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
//            expenseAA.clear();
//            refreshUi();
//            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                eItem expense = snapshot.getValue(eItem.class);
//                if(expense != null && usersGpin!= null && expense.getGroupPin() != null && expense.getGroupPin().equals(usersGpin)){
//                    expenseAA.add(expense);
//                }
//            }
//            expenseAA.notifyDataSetChanged();
//            refreshUi();
//        }
//
//        @Override
//        public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
//
//        }
//    });
//   // getGroupPin();
//   // refreshUi();
//    addExpenseBtn.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            addExpense();
//        }
//    });
//        return view;
//    }
//
//    private void getGroupPin() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null){
//            String uid = currentUser.getUid();
//            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
//            userRef.child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
//                    usersGpin = dataSnapshot.getValue(String.class);
//                    refreshUi();
//                }
//
//                @Override
//                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }
//
//    private void refreshUi() {
//        if (expenseList == null){
//            expenseList = new ArrayList<>();
//        }
//
//        amountOwed = calculateAmountUsersOwed();
//
//        toPay = calculateAmountToPay(expenseList);
//
//        userOwedTv.setText("You Are Owed: £" + String.format("%.2f", amountOwed));
//
//        //toPay = totalEx / expenseList.size();
//
//        userIncomeTv.setText("You need to pay: £" + String.format("%.2f", toPay));
//
//        //only show same group
//
//        List<eItem> filteredByGroup = new ArrayList<>();
//        for (eItem item : expenseList){
//            if (item.getGroupPin() != null && usersGpin != null && item.getGroupPin().equals(usersGpin)){
//                Log.d("TAG", "refreshUi: " + item.getGroupPin() );
//                Log.d("TAG", "refreshUi: " + usersGpin );
//
//                filteredByGroup.add(item);
//            }
//        }
//        expenseAA.clear();
//        expenseAA.addAll(filteredByGroup);
//        expenseAA.notifyDataSetChanged();;
//    }
//
//    private double calculateAmountToPay(List<eItem> expenseList) {
//        double totalToPay = 0.0;
//        int groupMembers = 0;
//        for (eItem expense : expenseList){
//            if(expense != null && expense.getGroupPin() != null && expense.getGroupPin().equals(usersGpin)&& usersGpin != null){
//                totalToPay += expense.getAmount();
//                groupMembers = 1;
//
//            }
//        }
//        if(groupMembers > 0){
//            return  totalToPay / groupMembers;
//        } else {
//            return 0.0;
//        }
//    }
//
//    private double calculateAmountUsersOwed() {
//        double amountPaid = 0.0;
//        if(expenseList != null){
//        for (eItem item : expenseList){
//            if(item.getGroupPin() != null && item.getGroupPin().equals(usersGpin)) {
//
//                amountPaid += item.getAmount();
//            }
//        }}
//        return amountPaid - toPay;
//        }
//
//
//    private void addExpense() {
//
//        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//
//        String itemName = nameEt.getText().toString();
//        double amount = Double.parseDouble(String.valueOf(Double.parseDouble((costEt.getText().toString()))));
//        String desc = descEt.getText().toString();
//
//        eItem expenseItem = new eItem(itemName, amount, desc, userId, usersGpin);
//
//        if (usersGpin != null && usersGpin.equals(expenseItem.getGroupPin())) {
//
//
//            expenseList.add(expenseItem);
//            expenseAA.notifyDataSetChanged();
//
//        }
//        totalEx += amount;
//        refreshUi();
//
//        DatabaseReference newExpenseRef = moneyDb.push();
//        newExpenseRef.setValue(expenseItem);
//
//    }
//}