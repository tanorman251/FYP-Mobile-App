package com.example.fyptommynorman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.fyptommynorman.adapter.adapterView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class NavActivity extends AppCompatActivity {
    ViewPager2 pager;
    BottomNavigationView bottomNav;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        pager = findViewById(R.id.pager);

        bottomNav = findViewById(R.id.bottomNavBar);
//this makes it so the user can slide between the fragments and select which fragment they want on the bottom navigation barcv



        fragmentArrayList.add(new MessageFragment());

        fragmentArrayList.add(new MoneyFragment());

        fragmentArrayList.add(new HomeFragment());

        fragmentArrayList.add(new AdviceFragment());

        fragmentArrayList.add(new CalanderFragment());




        adapterView adapterView = new adapterView(this, fragmentArrayList);

        pager.setAdapter(adapterView);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                switch (position){
                    case 0:
                        bottomNav.setSelectedItemId(R.id.messageIc);
                        break;
                    case 1:
                        bottomNav.setSelectedItemId(R.id.moneyIc);
                        break;
                    case 2:
                        bottomNav.setSelectedItemId(R.id.homeIc);
                        break;
                    case 3:
                        bottomNav.setSelectedItemId(R.id.adviceIc);
                        break;
                    case 4:
                        bottomNav.setSelectedItemId(R.id.calenderIc);
                        break;
                }


                super.onPageSelected(position);
                pager.setCurrentItem(2);
            }
        });
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.messageIc:
                        pager.setCurrentItem(0);
                        break;
                    case R.id.moneyIc:
                        pager.setCurrentItem(1);
                        break;
                    case R.id.homeIc:
                        pager.setCurrentItem(2);
                        break;
                    case R.id.adviceIc:
                        pager.setCurrentItem(3);
                        break;
                    case R.id.calenderIc:
                        pager.setCurrentItem(4);
                        break;
                }
                return true;
            }
        });
    }
}