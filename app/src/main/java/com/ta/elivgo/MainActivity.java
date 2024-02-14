package com.ta.elivgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigation;
    private Fragment fragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        //Untuk inisialisasi fragment pertama kali
        fragmentManager.beginTransaction().replace(R.id.main_container, new FragmentHome()).commit();

        //Memberikan listener saat menu item di bottom navigation diklik
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.iv_menu_home:
                        fragment = new FragmentHome();
                        break;
                    case R.id.iv_menu_reservation:
                        fragment = new FragmentReservation();
                        break;
                    case R.id.iv_menu_activity:
                        fragment = new FragmentHistory();
                        break;
                    case R.id.iv_menu_account:
                        fragment = new FragmentAccount();
                        break;
                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });
    }
}