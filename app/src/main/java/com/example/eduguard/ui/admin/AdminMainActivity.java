package com.example.eduguard.ui.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.eduguard.R;
import com.example.eduguard.ui.admin.fragments.AdminDashboardFragment;
import com.example.eduguard.ui.admin.fragments.AdminMentorsFragment;
import com.example.eduguard.ui.admin.fragments.AdminPoliciesFragment;
import com.example.eduguard.ui.admin.fragments.AdminRequestsFragment;
import com.example.eduguard.ui.admin.fragments.AdminStudentsFragment;
import com.example.eduguard.ui.common.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_nav);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment selected = null;
            int id = item.getItemId();

            if (id == R.id.nav_admin_dashboard) {
                selected = new AdminDashboardFragment();

            } else if (id == R.id.nav_admin_students) {
                selected = new AdminStudentsFragment();

            } else if (id == R.id.nav_admin_mentors) {
                selected = new AdminMentorsFragment();

            }
//            else if (id == R.id.nav_admin_requests) {
//                selected = new AdminRequestsFragment();
//
//            }
            else if (id == R.id.nav_admin_policies) {
                selected = new AdminPoliciesFragment();
            }else if(id == R.id.nav_settings){
                startActivity(new Intent(AdminMainActivity.this, SettingsActivity.class));
                return true;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.admin_fragment_container, selected)
                    .commit();

            return true;
        });


        bottomNavigationView.setSelectedItemId(R.id.nav_admin_dashboard);
    }
}
