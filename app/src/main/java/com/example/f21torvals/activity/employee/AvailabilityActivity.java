package com.example.f21torvals.activity.employee;

import static com.example.f21torvals.R.*;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.f21torvals.Availability;
import com.example.f21torvals.DatabaseHelper;
import com.example.f21torvals.DrawerBaseActivity;
import com.example.f21torvals.R;
import com.example.f21torvals.databinding.ActivityAvailabilityBinding;

/**
 * Allows for the UI to assign the availability for the employee
 *
 * @author Omar Mohomed, Meaghan Neill, Axel Nguyen, Daine Garon, Rudra Patel
 */
public class AvailabilityActivity extends DrawerBaseActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinnerSun;
    Spinner spinnerMon;
    Spinner spinnerTues;
    Spinner spinnerWed;
    Spinner spinnerThurs;
    Spinner spinnerFri;
    Spinner spinnerSat;
    Button button;
    Bundle extras;

    String sunAvailability;
    String satAvailability;
    String monAvailability;
    String tuesAvailability;
    String wedAvailability;
    String thursAvailability;
    String friAvailability;

    ActivityAvailabilityBinding activityAvailabilityBinding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAvailabilityBinding = ActivityAvailabilityBinding.inflate(getLayoutInflater());
        setContentView(activityAvailabilityBinding.getRoot());
        allocateActivityTilte("Set Availability");

        spinnerSun = findViewById(id.spinner_sun);
        spinnerMon = findViewById(id.spinner_mon);
        spinnerTues = findViewById(id.spinner_tues);
        spinnerWed = findViewById(id.spinner_wed);
        spinnerThurs = findViewById(id.spinner_thurs);
        spinnerFri = findViewById(id.spinner_fri);
        spinnerSat = findViewById(id.spinner_sat);

        //Mon-Fri
        setSpinnerMonToFri(spinnerMon);
        setSpinnerMonToFri(spinnerTues);
        setSpinnerMonToFri(spinnerWed);
        setSpinnerMonToFri(spinnerThurs);
        setSpinnerMonToFri(spinnerFri);
        //Sat-Sun
        setSpinnerSatSun(spinnerSat);
        setSpinnerSatSun(spinnerSun);

        extras = getIntent().getExtras();
        int employeeId =extras.getInt("employeeId");
        boolean edit = extras.getBoolean("edit");

        if (edit) {
            setValueSpinner();
        }

        //Add listener to button
        button = findViewById(id.save_avalability_button);
        button.setOnClickListener(view -> {
            Availability availability = new Availability(employeeId, sunAvailability, monAvailability,
                    tuesAvailability, wedAvailability, thursAvailability, friAvailability, satAvailability);

            databaseHelper = new DatabaseHelper(AvailabilityActivity.this);
            boolean success;
            if (!edit) {
                success = databaseHelper.addAvailability(availability, employeeId);
            } else {
                success = databaseHelper.editAvailability(availability, employeeId);
            }
            Toast.makeText(AvailabilityActivity.this, success ? "Done" : "Error", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AvailabilityActivity.this, ViewEmployeeActivity.class));

        });
    }

    private void setValueSpinner() {
        databaseHelper = new DatabaseHelper(this);
        Availability availability = databaseHelper.searchEmployeeAvailability(extras.getInt("employeeId"));

        spinnerMon.setSelection(mapAvailabilityToIndex(availability.getMonday(), true));
        spinnerTues.setSelection(mapAvailabilityToIndex(availability.getTuesday(), true));
        spinnerWed.setSelection(mapAvailabilityToIndex(availability.getWednesday(), true));
        spinnerThurs.setSelection(mapAvailabilityToIndex(availability.getThursday(), true));
        spinnerFri.setSelection(mapAvailabilityToIndex(availability.getFriday(), true));
        spinnerSat.setSelection(mapAvailabilityToIndex(availability.getSaturday(), false));
        spinnerSun.setSelection(mapAvailabilityToIndex(availability.getSunday(), false));

    }

    private int mapAvailabilityToIndex(String availability, boolean isWeekDay) {
        if (availability.equalsIgnoreCase("None")) return 0;
        if (availability.equalsIgnoreCase("Morning")) return 1;
        if (availability.equalsIgnoreCase("Afternoon")) return 2;
        //All day
        return isWeekDay ? 3 : 1;
    }

    // Create the dropdowns for Mon-Fri
    public void setSpinnerMonToFri(Spinner spinner) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                array.mon_to_fri, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    // Create the dropdowns for Sat/Sun
    public void setSpinnerSatSun(Spinner spinner) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                array.sat_sun, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    // Get the selected availability for the employee
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_sun:
                sunAvailability = spinnerSun.getSelectedItem().toString();
                break;
            case R.id.spinner_mon:
                monAvailability = spinnerMon.getSelectedItem().toString();
                break;
            case R.id.spinner_tues:
                tuesAvailability = spinnerTues.getSelectedItem().toString();
                break;
            case R.id.spinner_wed:
                wedAvailability = spinnerWed.getSelectedItem().toString();
                break;
            case R.id.spinner_thurs:
                thursAvailability = spinnerThurs.getSelectedItem().toString();
                break;
            case R.id.spinner_fri:
                friAvailability = spinnerFri.getSelectedItem().toString();
                break;
            case R.id.spinner_sat:
                satAvailability = spinnerSat.getSelectedItem().toString();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

}