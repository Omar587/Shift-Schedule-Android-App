package com.example.f21torvals.activity.employee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.f21torvals.Availability;
import com.example.f21torvals.DatabaseHelper;
import com.example.f21torvals.DrawerBaseActivity;
import com.example.f21torvals.R;
import com.example.f21torvals.databinding.ActivityViewAvailabilityBinding;

/** See the employees availability in the UI
 * @author Omar Mohomed, Meaghan Neill, Axel Nguyen, Daine Garon, Rudra Patel
 */
public class ViewAvailabilityActivity extends DrawerBaseActivity {
    Bundle extras;
    Availability availability;
    TextView sunAvail;
    TextView monAvail;
    TextView tuesAvail;
    TextView wedAvail;
    TextView thursAvail;
    TextView friAvail;
    TextView satAvail;
    Button editAvailable;

    ActivityViewAvailabilityBinding activityViewAvailabilityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityViewAvailabilityBinding = ActivityViewAvailabilityBinding.inflate(getLayoutInflater());
        setContentView(activityViewAvailabilityBinding.getRoot());

        //Get info from intent
        extras = getIntent().getExtras();
        int employeeId = extras.getInt("employeeId");
        String employeeFullName = extras.getString("employeeName");

        setTitle(employeeFullName + "'s Availability");

        displayAvailability(employeeId);

        editAvailable = findViewById(R.id.edit_availability);
        editAvailable.setOnClickListener(view -> {
            Intent intent = new Intent(ViewAvailabilityActivity.this, AvailabilityActivity.class);
            intent.putExtra("employeeId", employeeId);
            intent.putExtra("edit", true);
            startActivity(intent);
        });
    }
    private void displayAvailability(int employeeId) {
        //get TextView
        sunAvail = findViewById(R.id.sun_avail_view);
        monAvail = findViewById(R.id.mon_avail_view);
        tuesAvail = findViewById(R.id.tues_avail_view);
        wedAvail = findViewById(R.id.wed_avail_view);
        thursAvail = findViewById(R.id.thurs_avail_view);
        friAvail = findViewById(R.id.fri_avail_view);
        satAvail = findViewById(R.id.sat_avail_view);

        //Search
        DatabaseHelper databaseHelper = new DatabaseHelper(ViewAvailabilityActivity.this);
        availability = databaseHelper.searchEmployeeAvailability(employeeId);

        //Bind data:
        sunAvail.setText(availability.getSunday());
        monAvail.setText(availability.getMonday());
        tuesAvail.setText(availability.getTuesday());
        wedAvail.setText(availability.getWednesday());
        thursAvail.setText(availability.getThursday());
        friAvail.setText(availability.getFriday());
        satAvail.setText(availability.getSaturday());
    }
}