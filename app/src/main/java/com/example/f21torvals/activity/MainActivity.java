package com.example.f21torvals.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

/* Main screen
  @author Omar Mohamed, Meaghan Neill, Axel Nguyen, Daine Garon, Rudra Patel
 */

import com.example.f21torvals.DrawerBaseActivity;
import com.example.f21torvals.R;
import com.example.f21torvals.activity.employee.ViewEmployeeActivity;
import com.example.f21torvals.activity.schedule.Calendar;
import com.example.f21torvals.databinding.ActivityMainBinding;

public class MainActivity extends DrawerBaseActivity {
    Button viewButton;
    Button calendarButton;
    Button viewScheduleButton;

    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        viewButton = findViewById(R.id.view_button);
        viewButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ViewEmployeeActivity.class)));

        viewScheduleButton = findViewById(R.id.view_schedule_button);
        viewScheduleButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Calendar.class)));


    }
}