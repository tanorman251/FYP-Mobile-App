package com.example.fyptommynorman;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.widget.*;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdviceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView adviceLv;
    private Spinner filterSpinner;

    private Button chatbotBtn;

    private List<filterdAdvice> allAdvice;

    public AdviceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdviceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdviceFragment newInstance(String param1, String param2) {
        AdviceFragment fragment = new AdviceFragment();
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

    public class FilterdAdviceAdapter extends ArrayAdapter<filterdAdvice>{
        private Context fContext;
        private List<filterdAdvice> fAdviceList;

        public FilterdAdviceAdapter (Context context, List<filterdAdvice> adviceList){
            super(context, 0, adviceList);
            fContext = context;
            fAdviceList = adviceList;
        }



    public View getView(int position, View convertView, ViewGroup parent){
            Log.d("AdviceFragment", "getView function called??");
            View listItem = convertView;
        if (listItem != null) {
        } else {
            Log.d("AdviceFragment", "inflating new list item view");

            listItem = LayoutInflater.from(fContext).inflate(R.layout.list_item_advice, parent, false);

        }
        TextView adviceTxt = listItem.findViewById(R.id.itemAdviceTxt);
            filterdAdvice currentAdvice = fAdviceList.get(position);
        Log.d("AdviceFragment", "current advice:" + currentAdvice.toString());

        adviceTxt.setText(currentAdvice.getText());
            return listItem;
    }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advice, container, false);
        // Inflate the layout for this fragment

        adviceLv = view.findViewById(R.id.adviceList);
        filterSpinner = view.findViewById(R.id.filterAdvice);
        chatbotBtn = view.findViewById(R.id.chatbotBtn);

        this.allAdvice = new ArrayList<filterdAdvice>();
        allAdvice.add(new filterdAdvice("test Budgeting", "Budgeting"));
        allAdvice.add(new filterdAdvice("test budegting ", "Budgeting"));
        allAdvice.add(new filterdAdvice("test mental", "Mental Well-being"));
        allAdvice.add(new filterdAdvice("test money tips", "Money Saving Tips"));
        Log.d("AdviceFragment", "All Advice: " + allAdvice.toString());


        FilterdAdviceAdapter adviceAdapter = new FilterdAdviceAdapter(requireContext(),allAdvice);
        adviceLv.setAdapter(adviceAdapter);

        //filter Advice
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.filterOptions, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);


        chatbotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO ADD CODE TO OPEN API
                Toast.makeText(requireContext(), "Chatbot coming soon...", Toast.LENGTH_LONG).show();
            }
        });


        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedFilter = parent.getItemAtPosition(position).toString();

                if (selectedFilter.equals("All Advice")) {
                    adviceLv.setAdapter(adviceAdapter);
                } else {


                    List<filterdAdvice> filterdList = new ArrayList<>();
                    for (filterdAdvice advice : allAdvice) {
                        if (advice.getFilter().equals(selectedFilter)) {
                            filterdList.add(advice);
                        }

                    }
                    Log.d("testing: %d", filterdList.toString());

                    FilterdAdviceAdapter filterdAdapter = new FilterdAdviceAdapter(requireContext(), filterdList);
                    adviceLv.setAdapter(filterdAdapter);

//                adviceAdapter.clear();
//
//                adviceAdapter.notifyDataSetChanged();
//
//                adviceAdapter.addAll(filterdList);
//
//                adviceAdapter.notifyDataSetChanged();

                    //             adviceLv.setAdapter(adviceAdapter);

                    //changed below
//                FilterdAdviceAdapter adviceAdapter2 = null;
//                adviceAdapter2.addAll(filterdList);
//                adviceAdapter2.notifyDataSetChanged();
//                adviceLv.setAdapter(adviceAdapter2);


                    //adviceLv.setAdapter(adviceAdapter);
                    // adviceAdapter.notifyDataSetChanged();

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            //adviceLv.setAdapter((ListAdapter) allAdvice);
            }
        });

        return view;
    }

}

