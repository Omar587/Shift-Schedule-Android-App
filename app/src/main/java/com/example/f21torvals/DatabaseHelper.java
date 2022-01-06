package com.example.f21torvals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is the list of all the variables we have for our databases
 * Status: Not finished - employees() needs to be updated, getAllAvailability() needs to be updated
 *
 * @author Omar Mohomed, Meaghan Neill, Axel Nguyen, Daine Garon, Rudra Patel
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Variables for employee database
    public static final String DATABASE_NAME = "EmployeeDatabase.db";
    public static final String EMPLOYEE_TABLE = "EMPLOYEE_TABLE";
    public static final String COLUMN_ID = "EMPLOYEE_ID"; // Primary key
    public static final String COLUMN_FIRSTNAME = "EMPLOYEE_FIRST_NAME";
    public static final String COLUMN_LASTNAME = "EMPLOYEE_LAST_NAME";
    public static final String COLUMN_EMAIL = "EMPLOYEE_EMAIL";
    public static final String COLUMN_PHONE_NUM = "EMPLOYEE_PHONE_NUM";
    public static final String COLUMN_HIRED_DATE = "EMPLOYEE_HIRED_DATE";
    public static final String COLUMN_OPENER = "EMPLOYEE_OPENER";
    public static final String COLUMN_CLOSER = "EMPLOYEE_CLOSER";
    // Variables for availability database
    public static final String AVAILABILITY_TABLE = "AVAILABILITY_TABLE";
    public static final String COLUMN_SUNDAY = "SUNDAY_AVAILABILITY";
    public static final String COLUMN_MONDAY = "MONDAY_AVAILABILITY";
    public static final String COLUMN_TUESDAY = "TUESDAY_AVAILABILITY";
    public static final String COLUMN_WEDNESDAY = "WEDNESDAY_AVAILABILITY";
    public static final String COLUMN_THURSDAY = "THURSDAY_AVAILABILITY";
    public static final String COLUMN_FRIDAY = "FRIDAY_AVAILABILITY";
    public static final String COLUMN_SATURDAY = "SATURDAY_AVAILABILITY";

    ///Variables for schedule database
    public static final String SCHEDULED_TABLE = "SCHEDULED";
    public static final String SCHEDULED_DATE = "DATE";
    public static final String SHIFT_TYPE = "SHIFT_TYPE";
    public static final String BUSY_DAY = "BUSY_DAY";


    //Variable for shift_fact_table db.
    public static final String SHIFT_FACT_TABLE = "SHIFT_FACT_TABLE";


    // Database creation for all databases
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the SQLite database for employee
        String employeeQuery = " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
                COLUMN_FIRSTNAME + " TEXT NOT NULL, " +
                COLUMN_LASTNAME + " TEXT NOT NULL, " +
                COLUMN_EMAIL + " TEXT NOT NULL, " +
                COLUMN_PHONE_NUM + " TEXT NOT NULL, " +
                COLUMN_HIRED_DATE + " TEXT, " +
                COLUMN_OPENER + " INTEGER, " +
                COLUMN_CLOSER + " INTEGER )";

        // Create the SQLite database for availability
        String availabilityQuery = " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
                COLUMN_SUNDAY + " TEXT NOT NULL, " +
                COLUMN_MONDAY + " TEXT NOT NULL, " +
                COLUMN_TUESDAY + " TEXT NOT NULL, " +
                COLUMN_WEDNESDAY + " TEXT NOT NULL, " +
                COLUMN_THURSDAY + " TEXT NOT NULL, " +
                COLUMN_FRIDAY + " TEXT NOT NULL, " +
                COLUMN_SATURDAY + " TEXT NOT NULL )";
        // Create the SQLite database for the schedule
        String scheduledQuery = " (" + SCHEDULED_DATE + " NUMERIC NOT NULL," +
                BUSY_DAY + " INTEGER NOT NULL, " +
                SHIFT_TYPE + " TEXT NOT NULL, " +
                "PRIMARY KEY(" + SCHEDULED_DATE + "," + SHIFT_TYPE + "))";

        //Create the SQLite database for the shift fact table.
        String factTableQuery = " (" + COLUMN_ID + " TEXT NOT NULL, " +
                SCHEDULED_DATE + " NUMERIC NOT NULL, " +
                SHIFT_TYPE + " TEXT NOT NULL, " +
                "PRIMARY KEY(" + COLUMN_ID + "," + SHIFT_TYPE + "," + SCHEDULED_DATE + "))";


        // Create databases
        sqLiteDatabase.execSQL("CREATE TABLE " + EMPLOYEE_TABLE + employeeQuery);
        sqLiteDatabase.execSQL("CREATE TABLE " + AVAILABILITY_TABLE + availabilityQuery);
        sqLiteDatabase.execSQL("CREATE TABLE " + SCHEDULED_TABLE + scheduledQuery);
        sqLiteDatabase.execSQL("CREATE TABLE " + SHIFT_FACT_TABLE + factTableQuery);
    }

    // Update table if it already is made
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EMPLOYEE_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AVAILABILITY_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SCHEDULED_TABLE);
        onCreate(sqLiteDatabase);
    }

    //======================================================================================================================
    //EMPLOYEES
    private List<Employee> getListEmployeeFromQuery(String queryString) {
        List<Employee> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                int employeeId = cursor.getInt(0);
                String firstName = cursor.getString(1);
                String lastName = cursor.getString(2);
                String email = cursor.getString(3);
                String phoneNum = cursor.getString(4);
                String hiredDate = cursor.getString(5);
                boolean opener = cursor.getInt(6) > 0;
                boolean closer = cursor.getInt(7) > 0;

                returnList.add(new Employee(employeeId, firstName, lastName, email, phoneNum, hiredDate, opener, closer));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return returnList;
    }

    // Get all employees
    // Status: Not finished
    public List<Employee> getAllEmployees() {
        //get data from the db:

        String queryString = "SELECT * FROM " + EMPLOYEE_TABLE;

        return getListEmployeeFromQuery(queryString);
    }


    public List<Employee> getAvailableEmployee(String dateOfWeek, String date, String timeOfDay) {
        String queryDateOfWeek = dateOfWeek.toUpperCase(Locale.ROOT) + "_AVAILABILITY";
        timeOfDay = timeOfDay.toUpperCase(Locale.ROOT);

        /*SELECT * FROM EMPLOYEE_TABLE AS E1
         *WHERE E1.EMPLOYEE_ID IN (SELECT AVAILABILITY_TABLE.EMPLOYEE_ID FROM AVAILABILITY_TABLE WHERE AVAILABILITY_TABLE.THURSDAY == 'Morning')
         *AND NOT EXIST (SELECT * FROM SHIFT_FACT_TABLE AS SF WHERE SF.EMPLOYEE_ID == E1.EMPLOYEE_ID AND SCHEDULED_DATE == '30/10/2021' AND SHIFT_TYPE == 'Morning')
         */
        String queryString =
                "SELECT * FROM " + EMPLOYEE_TABLE + " AS E1 WHERE E1." + COLUMN_ID +
                        " IN (SELECT " + AVAILABILITY_TABLE + "." + COLUMN_ID + " FROM " + AVAILABILITY_TABLE +
                        " WHERE " + AVAILABILITY_TABLE + "." + queryDateOfWeek + " == '" + timeOfDay + "')" +
                        " AND NOT EXISTS" +
                        " (SELECT * FROM " + SHIFT_FACT_TABLE + " AS SF WHERE SF." + COLUMN_ID + " == E1." + COLUMN_ID + " AND " + SCHEDULED_DATE + " == '" + date + "')";
        return getListEmployeeFromQuery(queryString);
    }

    public Employee getEmployeeByID(int employeeId) {
        String queryString = "SELECT * FROM " + EMPLOYEE_TABLE + " WHERE " + COLUMN_ID + "==" + employeeId;
        return getListEmployeeFromQuery(queryString).get(0);
    }

    // Create the employee on the UI screen
    public long addEmployee(Employee employee) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_FIRSTNAME, employee.getFirstName());
        cv.put(COLUMN_LASTNAME, employee.getLastName());
        cv.put(COLUMN_EMAIL, employee.getEmail());
        cv.put(COLUMN_PHONE_NUM, employee.getPhoneNumber());
        cv.put(COLUMN_HIRED_DATE, employee.getHireDate());
        cv.put(COLUMN_OPENER, employee.getOpener());
        cv.put(COLUMN_CLOSER, employee.getCloser());

        return db.insert(EMPLOYEE_TABLE, null, cv);
    }

    public long editEmployee(Employee employee) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_FIRSTNAME, employee.getFirstName());
        cv.put(COLUMN_LASTNAME, employee.getLastName());
        cv.put(COLUMN_EMAIL, employee.getEmail());
        cv.put(COLUMN_PHONE_NUM, employee.getPhoneNumber());
        cv.put(COLUMN_HIRED_DATE, employee.getHireDate());
        cv.put(COLUMN_CLOSER, employee.getCloser());
        cv.put(COLUMN_OPENER, employee.getOpener());


        return db.update(EMPLOYEE_TABLE, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(employee.getId())});
    }

    public List<Employee> getEmployeeForAScheduledShift(String date, String shiftType) {
        shiftType = shiftType.toUpperCase(Locale.ROOT);
        String queryString = "SELECT * FROM " + EMPLOYEE_TABLE + " WHERE " + COLUMN_ID + " IN (SELECT " + COLUMN_ID + " FROM " + SHIFT_FACT_TABLE + " AS SF WHERE SF." + SCHEDULED_DATE + " == '" + date + "' AND SF." + SHIFT_TYPE + " == '" + shiftType + "')";
        return getListEmployeeFromQuery(queryString);
    }


    //======================================================================================================================
    //Availability


    // Create the availability for the employee on the UI screen
    public boolean addAvailability(Availability availability, int employeeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, employeeId);
        cv.put(COLUMN_SUNDAY, availability.getSunday());
        cv.put(COLUMN_MONDAY, availability.getMonday());
        cv.put(COLUMN_TUESDAY, availability.getTuesday());
        cv.put(COLUMN_WEDNESDAY, availability.getWednesday());
        cv.put(COLUMN_THURSDAY, availability.getThursday());
        cv.put(COLUMN_FRIDAY, availability.getFriday());
        cv.put(COLUMN_SATURDAY, availability.getSaturday());

        long insert = db.insert(AVAILABILITY_TABLE, null, cv);

        return insert > 0;
    }

    // Look at the employee availability
    public Availability searchEmployeeAvailability(int employeeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Availability availability = new Availability(employeeId, "", "", "", "", "", "", "");
        String searchQuery = "SELECT * FROM " + AVAILABILITY_TABLE + " WHERE " + COLUMN_ID + " == " + employeeId;

        Cursor cursor = db.rawQuery(searchQuery, null);

        if (cursor.moveToFirst()) {
            availability.setSunday(cursor.getString(1));
            availability.setMonday(cursor.getString(2));
            availability.setTuesday(cursor.getString(3));
            availability.setWednesday(cursor.getString(4));
            availability.setThursday(cursor.getString(5));
            availability.setFriday(cursor.getString(6));
            availability.setSaturday(cursor.getString(7));
        }

        cursor.close();
        db.close();
        return availability;
    }

    public String searchEmployeeAvailabilityByDayOfWeek(int employeeId, String dayOfWeek) {
        SQLiteDatabase db = this.getReadableDatabase();
        String dayOfWeekQuery = AVAILABILITY_TABLE + "." + dayOfWeek.toUpperCase(Locale.ROOT) + "_AVAILABILITY";
        String result = null;
        String searchQuery = "SELECT " + dayOfWeekQuery + " FROM " + AVAILABILITY_TABLE + " WHERE " + COLUMN_ID + " == " + employeeId;

        Cursor cursor = db.rawQuery(searchQuery, null);

        if (cursor.moveToFirst()) {
            result = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return result;
    }


    //======================================================================================================================
    //Scheduled
    // Create the schedule on the UI screen
    public boolean addSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(BUSY_DAY, schedule.getBusyDay());
        cv.put(SHIFT_TYPE, schedule.getShiftType());
        cv.put(SCHEDULED_DATE, schedule.getDate());

        long insert = db.insertWithOnConflict(SCHEDULED_TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);

        return insert > 0;

    }

    /*After every schedule is made make sure to call this method. */
    public boolean addShiftFactTable(String employeeID, String date, String shiftType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(SCHEDULED_DATE, date);
        cv.put(COLUMN_ID, employeeID);
        cv.put(SHIFT_TYPE, shiftType);

        long insert = db.insertWithOnConflict(SHIFT_FACT_TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);

        return insert > 0;
    }

    public int countEmployeePerShift(String date, String shiftType) {
        int employeesCount = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT COUNT(*) FROM " + SHIFT_FACT_TABLE + " WHERE " + SHIFT_TYPE + " == '" + shiftType + "' AND " + SCHEDULED_DATE + " == '" + date + "' GROUP BY " + SCHEDULED_DATE + ", " + SHIFT_TYPE;

        Cursor cursor = db.rawQuery(searchQuery, null);

        if (cursor.moveToFirst()) {
            employeesCount = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return employeesCount;
    }


    /**
     * @param firstOfTheMonthDate expects a string in the format YYYY-MM-DD
     * @return the contents of the query as a list of strings
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<String> scheduledForMonth(String firstOfTheMonthDate) {

        LocalDate lastOfTheMonthDate = LocalDate.parse(firstOfTheMonthDate, DateTimeFormatter.ofPattern("yyyy-MM-d"))
                .with(TemporalAdjusters.lastDayOfMonth());


        String query = "select " + SHIFT_FACT_TABLE + "." + SCHEDULED_DATE + "," + SHIFT_FACT_TABLE + "." + COLUMN_ID + "," +
                EMPLOYEE_TABLE + "." + COLUMN_FIRSTNAME + "," + SHIFT_FACT_TABLE + "." + SHIFT_TYPE +

                " FROM " + SHIFT_FACT_TABLE + "," + EMPLOYEE_TABLE +
                " where (" + "DATE BETWEEN " + "'" + firstOfTheMonthDate + "'" + " AND " + "'" + lastOfTheMonthDate + "'" +
                " AND " + SHIFT_FACT_TABLE + "." + COLUMN_ID + "= " + EMPLOYEE_TABLE + "." + COLUMN_ID + ") ORDER BY DATE";


        List<String> retData = new ArrayList<>();


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                retData.add(cursor.getString(0));
                retData.add(cursor.getString(1));
                retData.add(cursor.getString(2));
                retData.add(cursor.getString(3));


            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return retData;
    }


    /**
     * @param startDate Start of the week
     *                  <p>
     *                  returns a list of employee information that do not have a shift within the
     *                  specified week range.
     *                  <p>
     *                  <p>
     *                  If an empty list is returned then that means every employee was scheduled
     *                  for that week.
     * @param endDate   end of the week
     * @return a list of employee information
     */
    public List<String> isScheduledForWeek(String startDate, String endDate) {


        String query = " Select "
                + EMPLOYEE_TABLE + "." + COLUMN_ID + ","
                + EMPLOYEE_TABLE + "." + COLUMN_FIRSTNAME + ","
                + EMPLOYEE_TABLE + "." + COLUMN_LASTNAME +
                " FROM " + EMPLOYEE_TABLE + " " +
                " WHERE NOT EXISTS ( SELECT " + EMPLOYEE_TABLE + "." + COLUMN_ID +
                " FROM " + SHIFT_FACT_TABLE +

                " WHERE " +
                SHIFT_FACT_TABLE + "." + COLUMN_ID + "=" + EMPLOYEE_TABLE + "." + COLUMN_ID +
                " AND  DATE BETWEEN " + "'" + startDate + "' AND '" + endDate + "'" + ")";


        List<String> retData = new ArrayList<>();


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                retData.add(cursor.getString(0));
                retData.add(cursor.getString(1));
                retData.add(cursor.getString(2));


            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return retData;

    }


    public boolean editAvailability(Availability availability, int employeeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SUNDAY, availability.getSunday());
        cv.put(COLUMN_MONDAY, availability.getMonday());
        cv.put(COLUMN_TUESDAY, availability.getTuesday());
        cv.put(COLUMN_WEDNESDAY, availability.getWednesday());
        cv.put(COLUMN_THURSDAY, availability.getThursday());
        cv.put(COLUMN_FRIDAY, availability.getFriday());
        cv.put(COLUMN_SATURDAY, availability.getSaturday());

        long update = db.update(AVAILABILITY_TABLE, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(employeeId)});

        return update > 0;
    }

    // Look at the employee availability
    public boolean getIsBusyDay(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        int isBusyDay = 0;
        String searchQuery = "SELECT * FROM " + SCHEDULED_TABLE + " WHERE " + SCHEDULED_DATE + " = '" + date + "'";

        Cursor cursor = db.rawQuery(searchQuery, null);

        if (cursor.moveToFirst()) {
            isBusyDay = cursor.getInt(1);
        }

        cursor.close();
        db.close();
        return isBusyDay > 0;
    }


    /**
     * This will be a helper method for deleting the employee in the scheduled table
     * Gets the shift type.
     *
     * @param empId employee id
     */
    private String getShiftType(String empId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String shiftType = "";
        String query = "Select " + SHIFT_TYPE + " FROM " + SHIFT_FACT_TABLE + " WHERE " +
                COLUMN_ID + "= '" + empId + "' AND " + SCHEDULED_DATE + "= '" + date + "'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            shiftType = cursor.getString(0);
        }

        cursor.close();

        return shiftType;

    }

    /**
     * @param empId Employee id to be deleted
     *              Deletes employee from SHIFT_FACT_TABLE
     * @param date  the date employee is scheduled
     */
    public boolean deleteEmployeeFromShiftFactTableByDate(String empId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        long result = db.delete(SHIFT_FACT_TABLE, COLUMN_ID + "=? AND " + SCHEDULED_DATE + "=?", new String[]{empId, date});
        return result > 0;
    }


    /**
     * The employee who was scheduled for the date and with the shift type will be deleted.
     *
     * @param empId Employee id to be deleted
     * @param date  the date employee is scheduled
     */

    public boolean deleteEmployeeFromScheduledByDate(String empId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        long result = db.delete(SCHEDULED_TABLE, SHIFT_TYPE + "=? AND " + SCHEDULED_DATE + "=?", new String[]{getShiftType(empId, date), date});
        return result > 0;
    }

    public boolean deleteEmployeeFromShiftFactTable(String empId) {
        SQLiteDatabase db = this.getReadableDatabase();
        long result = db.delete(SHIFT_FACT_TABLE, COLUMN_ID + "=?", new String[]{empId});
        return result > 0;
    }

    public boolean deleteEmployeeFromEmployee(String empId) {
        SQLiteDatabase db = this.getReadableDatabase();
        long result = db.delete(EMPLOYEE_TABLE, COLUMN_ID + "=?", new String[]{empId});
        return result > 0;

    }

    public boolean deleteEmployeeFromAvailability(String empId) {
        SQLiteDatabase db = this.getReadableDatabase();
        long result = db.delete(AVAILABILITY_TABLE, COLUMN_ID + "=?", new String[]{empId});
        return result > 0;
    }

//
//    public Employee searchEmployeeHelper(String firstname, String lastname)
//    {
//
//    }
//
//    public boolean deleteEmployeeHelper(String firstname, String lastname)
//    {
//
//    }
//
//    public boolean updateEmployeeHelper(String firstname, String lastname)
//    {
//
//    }
}