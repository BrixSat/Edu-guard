package com.example.eduguard.ui.mentor;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.eduguard.R;
import com.example.eduguard.ui.admin.AdminMainActivity;
import com.example.eduguard.ui.common.SettingsActivity;
import com.example.eduguard.ui.mentor.fragments.MentorDashboardFragment;
import com.example.eduguard.ui.mentor.fragments.MentorRequestsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MentorMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_main);

        BottomNavigationView nav = findViewById(R.id.mentor_bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            Fragment f = null;
            if (item.getItemId() == R.id.nav_mentor_dashboard) {
                f = new MentorDashboardFragment();
            } else if (item.getItemId() == R.id.nav_mentor_requests) {
                f = new MentorRequestsFragment();
            }else if(item.getItemId() == R.id.nav_settings){
                startActivity(new Intent(MentorMainActivity.this, SettingsActivity.class));
                return true;
            }

            if (f != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mentor_fragment_container, f)
                        .commit();
            }
            return true;
        });

        nav.setSelectedItemId(R.id.nav_mentor_dashboard);
    }
}
