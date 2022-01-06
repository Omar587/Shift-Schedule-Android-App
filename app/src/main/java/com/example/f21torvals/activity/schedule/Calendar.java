package com.example.f21torvals.activity.schedule;


import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.example.f21torvals.BuildConfig;
import com.example.f21torvals.DatabaseHelper;
import com.example.f21torvals.DrawerBaseActivity;
import com.example.f21torvals.R;
import com.example.f21torvals.databinding.ActivityCalendarBinding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class Calendar extends DrawerBaseActivity implements AdapterView.OnItemSelectedListener {

    private AlertDialog dialog;

    FrameLayout popup;
    String Popmessage;
    TextView PopupText;
    CalendarView calendar;
    Bundle extras;
    ActivityCalendarBinding activityCalendarBinding;
    Button button;
    Button verifyShifts;
    Button popupClose;
    Spinner months;
    Spinner years;
    String selectedDate;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCalendarBinding = ActivityCalendarBinding.inflate(getLayoutInflater());
        allocateActivityTilte("Customers");
        setContentView(activityCalendarBinding.getRoot());
        this.setTitle("Calendar");
        extras = getIntent().getExtras();


        //get selected calendar date
        calendar = findViewById(R.id.calendar1);
        calendar.setOnDateChangeListener((view, year, month, day) -> {
            Intent intent = new Intent(view.getContext(), AvailableEmployee.class);
            try {
                intent.putExtra("dayOfWeek", findDayOfWeek(day, month + 1, year));
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(year + "-" + (month + 1) + "-" + day);
                intent.putExtra("date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            startActivity(intent);
        });

        monthSpinner();
        yearSpinner();
        button = findViewById(R.id.button);
        verifyShifts = findViewById(R.id.verify);
        verifyShifts.setOnClickListener(view -> createNewContactDialog());

        button.setOnClickListener(view -> scheduleTextFile(selectedDate));


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNewContactDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopup = View.inflate(calendar.getContext(), R.layout.popup_text, popup);
        PopupText = contactPopup.findViewById(R.id.popupText);
        popupClose = contactPopup.findViewById(R.id.popupClose);
        Popmessage = verifyShiftPopupMessage();
        //copy retured string to textfield
        PopupText.setText(Popmessage);
        dialogBuilder.setView(contactPopup);
        dialog = dialogBuilder.create();
        dialog.show();

        popupClose.setOnClickListener(view -> dialog.dismiss());
    }


    /*Will return the start of the current months date in the format YYYY-MM-DD. */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String firstOfMonthDate() {
        return LocalDate.parse(selectedDate).with(TemporalAdjusters.firstDayOfMonth()).toString();
    }


    /*Requires the month format YYYY-MM-DD outputs schedule as a text file*/
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void scheduleTextFile(String firstOfMonthDate) {
        if (Environment.isExternalStorageManager()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + firstOfMonthDate + "-Schedule.txt"))) {

                DatabaseHelper databaseHelper = new DatabaseHelper(Calendar.this);
                List<String> scheduleInfo = databaseHelper.scheduledForMonth(firstOfMonthDate());

                writer.write("DATE            ID     NAME       SHIFT   \n");
                int count = 1;

                for (String info : scheduleInfo) {
                    writer.write(info + "      ");

                    if (count % 4 == 0) {
                        writer.write("\n");
                    }
                    count++;
                }

                openFile();
            } catch (IOException ex) {
                Log.i("error", ex.getLocalizedMessage());
            }
        } else {
            //request for the permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void openFile() {
        if (Environment.isExternalStorageManager()) {
            File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + selectedDate + "-Schedule.txt");
            Uri textUri = FileProvider.getUriForFile(Calendar.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(textUri, "text/plain");
            startActivity(intent);
        } else {
            //request for the permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }

    }


    /**
     * @param startDate week start
     * @param endDate   week end
     * @return returns employees without shifts for a given week.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String verifyShifts(String startDate, String endDate) {
        DatabaseHelper databaseHelper = new DatabaseHelper(Calendar.this);
        List<String> scheduleInfo = databaseHelper.isScheduledForWeek(startDate, endDate);

        if (scheduleInfo.size() == 0) {
            return "";
        }

        StringBuilder noShifts = new StringBuilder("People without shifts from " + startDate + " to " + endDate + " \n");

        int count = 1;
        for (String info : scheduleInfo) {
            noShifts.append(" ").append(info).append(" ");
            if (count % 3 == 0) {
                noShifts.append("\n");
            }
            count++;
        }


        return noShifts.toString();
    }


    /**
     * @return message will be displayed on popscreen to show what employees dont have shifts for
     * the given weeks. Or it will display a message saying "All employee's were scheduled correctly!"
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String verifyShiftPopupMessage() {

        //parse the string to the YYYY-MM format which will later be used to append days to the end
        String yearAndMonth = selectedDate.substring(0, 7);
        String message;

        String weekOne = verifyShifts(selectedDate, yearAndMonth + "-07");
        String weekTwo = verifyShifts(yearAndMonth + "-07", yearAndMonth + "-14");
        String weekThree = verifyShifts(yearAndMonth + "-14", yearAndMonth + "-21");
        String weekFour = verifyShifts(yearAndMonth + "-21", yearAndMonth + "-28");


        if (weekOne.equals("") && weekTwo.equals("") && weekThree.equals("") && weekFour.equals("")) {
            return "All employee's were scheduled correctly!";
        }

        message = weekOne + "\n" + weekTwo + "\n" + weekThree + "\n" + weekFour;


        return message;
    }


    private String findDayOfWeek(int day, int month, int year) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(year + "-" + month + "-" + day);

        return new SimpleDateFormat("EEEE", Locale.US).format(Objects.requireNonNull(date));
    }


    /**
     * month spinner on Ui
     */
    public void monthSpinner() {

        /*Month Spinner */
        months = findViewById(R.id.MONTH);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        months.setAdapter(adapter);
        months.setOnItemSelectedListener(this);


    }

    /**
     * year spinner on Ui
     */
    public void yearSpinner() {
        /*Month Spinner */
        years = findViewById(R.id.YEAR);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.year_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        years.setAdapter(adapter);
        years.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        HashMap<String, String> monthMap = new HashMap<>();
        monthMap.put("January", "01");
        monthMap.put("February", "02");
        monthMap.put("March", "03");
        monthMap.put("April", "04");
        monthMap.put("May", "05");
        monthMap.put("June", "06");
        monthMap.put("July", "07");
        monthMap.put("August", "08");
        monthMap.put("September", "09");
        monthMap.put("October", "10");
        monthMap.put("November", "11");
        monthMap.put("December", "12");


        // turns string into YYYY-MM-01 where YYYY and MM are the values selected in the combo box
        selectedDate = years.getSelectedItem() + "-" + monthMap.get(months.getSelectedItem()) + "-01";

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}