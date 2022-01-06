package com.example.f21torvals.activity.schedule;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.f21torvals.DatabaseHelper;
import com.example.f21torvals.DrawerBaseActivity;
import com.example.f21torvals.Employee;
import com.example.f21torvals.R;
import com.example.f21torvals.Schedule;
import com.example.f21torvals.activity.schedule.fragment.AfternoonFragment;
import com.example.f21torvals.activity.schedule.fragment.AllFragment;
import com.example.f21torvals.activity.schedule.fragment.MorningFragment;
import com.example.f21torvals.databinding.ActivityAvailableEmployeeBinding;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Allows for you to see the list of all the available employees for a certain day
 * ***** Status: Currently not finished and needs fixing
 *
 * @author Omar Mohomed, Meaghan Neill, Axel Nguyen, Daine Garon, Rudra Patel
 */
public class AvailableEmployee extends DrawerBaseActivity {
    private static final List<Employee> selectedEmployees = new ArrayList<>();
    ActivityAvailableEmployeeBinding activityAvailableEmployeeBinding;
    //Scheduled employees data
    List<Employee> morningScheduledEmployee = new ArrayList<>();
    List<Employee> afternoonScheduledEmployee = new ArrayList<>();
    List<Employee> allDayScheduledEmployees = new ArrayList<>();
    ListView lv_left;
    ListView lv_right;
    //Shift button
    Button assignShiftBtn;
    Button aboveButton;
    Button belowButton;
    ToggleButton isBusyDayButton;
    Switch switchRepeatAllMonth;
    //Schedule data
    private String date;
    private String dayOfWeek;
    private String month;
    String[] splitString;
    private static boolean isBusyDay;
    private boolean isRepeatAllMonth;
    private TabLayout tabLayout;
    private PopupWindow popupWindow;
    private RelativeLayout relativeLayout;
    DatabaseHelper databaseHelper;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAvailableEmployeeBinding = ActivityAvailableEmployeeBinding.inflate(getLayoutInflater());
        allocateActivityTilte("Employee");
        setContentView(activityAvailableEmployeeBinding.getRoot());

        date = getIntent().getExtras().getString("date");
        dayOfWeek = getIntent().getExtras().getString("dayOfWeek");
        splitString = date.split("-");
        switch(splitString[1])
        {
            case "01":
                month = "January ";
                break;
            case "02":
                month = "February ";
                break;
            case "03":
                month = "March ";
                break;
            case "04":
                month = "April ";
                break;
            case "05":
                month = "May ";
                break;
            case "06":
                month = "June ";
                break;
            case "07":
                month = "July ";
                break;
            case "08":
                month = "August ";
                break;
            case "09":
                month = "September ";
                break;
            case "10":
                month = "October ";
                break;
            case "11":
                month = "November ";
                break;
            case "12":
                month = "December ";
                break;

        }
        this.setTitle(dayOfWeek +", "+ month +splitString[2] + ", " + splitString[0]);
        databaseHelper = new DatabaseHelper(this);


        //List view for scheduled employees
        setUpListView();
        //TabLayout:
        setUpTabLayout();

        if ((dayOfWeek.equals("Saturday") || dayOfWeek.equals("Sunday")) && !allDayScheduledEmployees.isEmpty() && !validateEmployees("ALL DAY")) {
            createAlert("Invalid Squad in All Day shift", "Please add an employee that can both close and open");
        } else if (!morningScheduledEmployee.isEmpty() && !validateEmployees("Morning")) {
            createAlert("Invalid Squad in Morning shift", "Please add an employee that can open");
        } else if (!afternoonScheduledEmployee.isEmpty() && !validateEmployees("Afternoon")) {
            createAlert("Invalid Squad in Afternoon shift", "Please add an employee that can close");
        }

    }

    private void setUpTabLayout() {
        //--------------------------------
        relativeLayout = findViewById(R.id.relative_layout);


        //Set up TabView
        tabLayout = findViewById(R.id.shift_tabs);
        ViewPager viewPager = findViewById(R.id.view_pager_tabs);


        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                }
        );

        VPAdapter adapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        //Set up tabs
        AllFragment allFragment = new AllFragment();
        MorningFragment morningFragment = new MorningFragment();
        AfternoonFragment afternoonFragment = new AfternoonFragment();
        Bundle bundle = new Bundle();
        bundle.putString("dayOfWeek", dayOfWeek);
        bundle.putString("date", date);
        allFragment.setArguments(bundle);
        morningFragment.setArguments(bundle);
        afternoonFragment.setArguments(bundle);

        adapter.addFragment(allFragment, "All Day");
        if (!dayOfWeek.equals("Saturday") && !dayOfWeek.equals("Sunday")) {
            adapter.addFragment(morningFragment, "Morning");
            adapter.addFragment(afternoonFragment, "Afternoon");
        }

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        selectedEmployees.clear();
        isBusyDay = false;
        super.onBackPressed();
    }

    private void setUpPopUp(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.popup_select_shift_type, null);
        popupWindow = new PopupWindow(container, 500, 500, true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.popup_rounded));
        popupWindow.setAnimationStyle(R.style.Animation);
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER_HORIZONTAL, 0, 0);

        aboveButton = popupWindow.getContentView().findViewById(R.id.aboveButton);
        aboveButton.setOnClickListener(this::shiftButtonHandler);
        belowButton = popupWindow.getContentView().findViewById(R.id.belowButton);
        belowButton.setOnClickListener(this::shiftButtonHandler);
        switchRepeatAllMonth = popupWindow.getContentView().findViewById(R.id.repeat_for_whole_month_switch);

        switchRepeatAllMonth.setOnCheckedChangeListener((buttonView, isChecked) -> isRepeatAllMonth = isChecked);

        if (dayOfWeek.equals("Saturday") || dayOfWeek.equals("Sunday")) {
            aboveButton.setText("All day");
            belowButton.setText("Unavailable");
            belowButton.setEnabled(false);
        }

        switch (tabLayout.getSelectedTabPosition()) { //Disabled buttons
            case 1:
                belowButton.setEnabled(false);
                break;
            case 2:
                aboveButton.setEnabled(false);
                break;
            default:
                break;
        }


        container.setOnTouchListener((v, motionEvent) -> {
            v.performClick();
            popupWindow.dismiss();
            return true;
        });
    }

    /**
     * Handler to buttons in the popup. Will validate first before querying the DB
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void shiftButtonHandler(View view) {
        Button btn = (Button) view;
        String shiftType = btn.getText().toString().toUpperCase(Locale.ROOT);
        databaseHelper = new DatabaseHelper(this);


        int currentShiftEmployeesCount;
        if (shiftType.equals("MORNING")) {
            currentShiftEmployeesCount = morningScheduledEmployee.size();
        } else if (shiftType.equals("AFTERNOON")) {
            currentShiftEmployeesCount = afternoonScheduledEmployee.size();
        } else {
            currentShiftEmployeesCount = allDayScheduledEmployees.size();
        }

        if (isBusyDay) {
            if (currentShiftEmployeesCount + selectedEmployees.size() > 3) {
                createAlert("Full Schedule", "Cannot insert more employees. Max is 3");
                return;
            }
        } else {
            if (currentShiftEmployeesCount + selectedEmployees.size() > 2) {
                createAlert("Full Schedule", "Cannot insert more employees. Max is 2");
                return;
            }
        }
        if (!validateEmployees(shiftType)) {
            createAlert("Invalid Squad", "Current squad does not meet specification. Continue?", shiftType);
        } else {
            assignEmployee(shiftType);
        }
    }

    private void assignEmployee(String shiftType) {
        try {
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(parsedDate);
            int initialMonth = cal.get(Calendar.MONTH);
            Schedule schedule;

            for (int i = 0; i < 5; i++) {
                String dateToBeInserted = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(parsedDate);
                schedule = new Schedule(dateToBeInserted, shiftType, isBusyDay ? 1 : 0);
                databaseHelper.addSchedule(schedule);
                for (Employee employee : selectedEmployees) {
                    databaseHelper.addShiftFactTable(String.valueOf(employee.getId()), dateToBeInserted, shiftType);
                }
                if (!isRepeatAllMonth) { //Will only do once
                    break;
                }

                //Increment date by one week
                cal.setTime(parsedDate);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                if (cal.get(Calendar.MONTH) != initialMonth) { //Check if is still in month
                    break;
                }
                parsedDate = cal.getTime();
            }
            Toast.makeText(AvailableEmployee.this, "Done", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
            selectedEmployees.clear();
            finish();
            startActivity(getIntent());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(AvailableEmployee.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAlert(String title, String message, String shiftType) {
        new AlertDialog.Builder(AvailableEmployee.this)
                .setTitle(title)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, (dialog, which) -> assignEmployee(shiftType))

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.presence_busy)
                .show();
    }

    private void createAlert(String title, String message) {
        new AlertDialog.Builder(AvailableEmployee.this)
                .setTitle(title)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_menu_info_details)
                .show();
    }

    /**
     * Check if one of current selected employees is qualified as closer or opener
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean validateEmployees(String shiftType) {
        int countBoth = 0;
        if (shiftType.equalsIgnoreCase("morning") || shiftType.equalsIgnoreCase("afternoon")) {
            int countOpener = (int) morningScheduledEmployee.stream().filter(Employee::getOpener).count();
            int countCloser = (int) afternoonScheduledEmployee.stream().filter(Employee::getCloser).count();


            for (Employee employee : selectedEmployees) {
                if (employee.getCloser() && employee.getOpener()) {
                    countBoth++;
                    continue;
                }
                if (employee.getOpener()) {
                    countOpener++;
                }
                if (employee.getCloser()) {
                    countCloser++;
                }
            }

            //Morning shift:
            if (shiftType.equalsIgnoreCase("MORNING")) {
                return countOpener > 0 || countBoth > 0;
            }

            //Afternoon shift:
            if (shiftType.equalsIgnoreCase("AFTERNOON")) {
                return countCloser > 0 || countBoth > 0;
            }
        }
        //Weekend shift:

        countBoth += (int) allDayScheduledEmployees.stream().filter(e -> e.getOpener() && e.getCloser()).count();
        countBoth += (int) selectedEmployees.stream().filter(e -> e.getOpener() && e.getCloser()).count();
        return countBoth > 0;
    }

    public static void toggleEmployeeInList(Employee employee) {
        if (selectedEmployees.contains(employee)) {
            selectedEmployees.remove(employee);
            return;
        }
        selectedEmployees.add(employee);

    }

    public static boolean hasEnoughEmployees() {
        if (isBusyDay) {
            return selectedEmployees.size() == 3;
        }
        return selectedEmployees.size() == 2;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpListView() {
        //Set up button
        assignShiftBtn = findViewById(R.id.assign_shift_button);
        assignShiftBtn.setOnClickListener(this::setUpPopUp);
        isBusyDayButton = findViewById(R.id.toggleButton);
        isBusyDayButton.setOnCheckedChangeListener((buttonView, isChecked) -> isBusyDay = isChecked);

        //----------------------------------
        //Set up scheduled employees
        databaseHelper = new DatabaseHelper(this);
        isBusyDayButton.setChecked(databaseHelper.getIsBusyDay(date));

        lv_left = findViewById(R.id.morning_scheduled_employees);
        lv_right = findViewById(R.id.afternoon_scheduled_employees);
        if (!dayOfWeek.equals("Saturday") && !dayOfWeek.equals("Sunday")) {

            //Get scheduled employees
            morningScheduledEmployee = databaseHelper.getEmployeeForAScheduledShift(date, "morning");
            afternoonScheduledEmployee = databaseHelper.getEmployeeForAScheduledShift(date, "afternoon");

            //Show to list view:
            ArrayAdapter<String> morningAdapter = new ArrayAdapter<>(AvailableEmployee.this, R.layout.activity_listview, morningScheduledEmployee.stream().map(Employee::getFullName).toArray(String[]::new));
            ArrayAdapter<String> afternoonAdapter = new ArrayAdapter<>(AvailableEmployee.this, R.layout.activity_listview, afternoonScheduledEmployee.stream().map(Employee::getFullName).toArray(String[]::new));
            lv_left.setAdapter(morningAdapter);
            lv_right.setAdapter(afternoonAdapter);

            //Click listener
            lv_left.setOnItemClickListener((adapterView, view, i, l) -> {
                Employee employee = morningScheduledEmployee.get(i);
                removeEmployeeFromShift(employee);
            });
            lv_right.setOnItemClickListener((adapterView, view, i, l) -> {
                Employee employee = afternoonScheduledEmployee.get(i);
                removeEmployeeFromShift(employee);
            });
        } else {
            lv_left.setVisibility(View.GONE);
            lv_right.setVisibility(View.GONE);

            //Set up all day view:
            TextView allDayLabel = findViewById(R.id.allDayScheduledEmployee);
            TextView morningLabel = findViewById(R.id.morningScheduledEmployee);
            morningLabel.setVisibility(View.GONE);
            TextView afternoonLabel = findViewById(R.id.afternoonScheduledEmployee);
            afternoonLabel.setVisibility(View.GONE);
            allDayLabel.setVisibility(View.VISIBLE);

            lv_left = findViewById(R.id.all_day_scheduled_employees);
            lv_left.setVisibility(View.VISIBLE);
            allDayScheduledEmployees = databaseHelper.getEmployeeForAScheduledShift(date, "all day");

            ArrayAdapter<String> allDayAdapter = new ArrayAdapter<>(AvailableEmployee.this, R.layout.activity_listview, allDayScheduledEmployees.stream().map(Employee::getFullName).toArray(String[]::new));
            lv_left.setAdapter(allDayAdapter);
            lv_left.setOnItemClickListener((adapterView, view, i, l) -> {
                Employee employee = allDayScheduledEmployees.get(i);
                removeEmployeeFromShift(employee);
            });


        }


    }

    private void removeEmployeeFromShift(Employee employee) {
        new AlertDialog.Builder(AvailableEmployee.this)
                .setTitle("Remove " + employee.getFullName() + " from this schedule?")
                .setMessage("This will remove " + employee.getFullName() + " from the schedule. You will have to assign them again.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    try {
                        databaseHelper.deleteEmployeeFromScheduledByDate(String.valueOf(employee.getId()), date);
                        databaseHelper.deleteEmployeeFromShiftFactTableByDate(String.valueOf(employee.getId()), date);
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    } catch (Exception e) {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_delete)
                .show();


    }
}