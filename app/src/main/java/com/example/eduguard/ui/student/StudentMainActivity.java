package com.example.eduguard.ui.student;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.eduguard.R;
import com.example.eduguard.ui.common.SettingsActivity;
import com.example.eduguard.ui.mentor.MentorMainActivity;
import com.example.eduguard.ui.student.fragments.EmergencyFragment;
import com.example.eduguard.ui.student.fragments.RequestTimeFragment;
import com.example.eduguard.ui.student.fragments.StudentHomeFragment;
import com.example.eduguard.ui.student.fragments.StudentRemainingTimeFragment;
import com.example.eduguard.ui.student.fragments.StudentUsageChartFragment;
import com.example.eduguard.ui.student.fragments.StudentWeeklyUsageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StudentMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        BottomNavigationView nav = findViewById(R.id.student_bottom_nav);

        nav.setOnItemSelectedListener(item -> {
            Fragment f = null;
            int id = item.getItemId();

            if (id == R.id.nav_student_home) {

                f = new StudentHomeFragment();

            } else if (id == R.id.nav_student_request_time) {

                f = new RequestTimeFragment();

            }
//            else if (id == R.id.nav_student_emergency) {
//
//                f = new EmergencyFragment();
//
//            }
            else if (id == R.id.nav_settings) {

                startActivity(new Intent(StudentMainActivity.this, SettingsActivity.class));
                return true;

            } else if (id == R.id.nav_remaining_time) {

                f = new StudentRemainingTimeFragment();

            } else if (id == R.id.nav_usage_chart) {

                f = new StudentUsageChartFragment();   // <-- Added

            }
//            else if (id == R.id.nav_weekly_summary) {
//
//                f = new StudentWeeklyUsageFragment();  // <-- Added
//            }
            if (f != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.student_fragment_container, f)
                        .commit();
            }
            return true;
        });

        nav.setSelectedItemId(R.id.nav_student_home);
    }
}
