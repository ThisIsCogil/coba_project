package com.example.coba_aplikasi;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.coba_aplikasi.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        masukFragment(new HomeFragment());

        FrameLayout frameLayout =findViewById(R.id.framelayout);
        BottomNavigationView navbar = findViewById(R.id.btnnavbar);

        navbar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.cart) {
                masukFragment(new CartFragment());
            } else if (id == R.id.home) {
                masukFragment(new HomeFragment());
            } else if (id == R.id.menu1) {
                masukFragment(new MenuFragment());
            } else if (id == R.id.setting) {
                masukFragment(new SettingFragment());
            } else {
                return false;
            }
            return true;
        });


    }

    private void masukFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,fragment);
        fragmentTransaction.commit();
    }
}

