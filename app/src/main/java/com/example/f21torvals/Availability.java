package com.example.f21torvals;

import androidx.annotation.NonNull;

import java.util.Locale;

/** This creates the availability database for each employee
 * Options for Mon-Fri are Open, Close, Either, or None
 * Options for Sat/Sun are Full or None
 * @author Omar Mohomed, Meaghan Neill, Axel Nguyen, Daine Garon, Rudra Patel
 */
public class Availability {

    // Availability Information
    private int id;
    private String Sunday; //Full or none
    private String Saturday; //Full or none
    private String Monday;  //Open, Close, Either or None
    private String Tuesday;  //Open, Close, Either or None
    private String Wednesday;  //Open, Close, Either or None
    private String Thursday;  //Open, Close, Either or None
    private String Friday;  //Open, Close, Either or None

    public Availability() {
    }
    // Availability database
    public Availability(int id, String Sunday, String Monday, String Tuesday,
                        String Wednesday, String Thursday, String Friday,
                        String Saturday) {
        this.id = id;
        this.Sunday = Sunday.toUpperCase(Locale.ROOT);
        this.Monday = Monday.toUpperCase(Locale.ROOT);
        this.Tuesday = Tuesday.toUpperCase(Locale.ROOT);
        this.Wednesday = Wednesday.toUpperCase(Locale.ROOT);
        this.Thursday = Thursday.toUpperCase(Locale.ROOT);
        this.Friday = Friday.toUpperCase(Locale.ROOT);
        this.Saturday = Saturday.toUpperCase(Locale.ROOT);
    }

    @NonNull
    @Override
    public String toString() {
        return "Availability{" +
                "id='" + id + '\'' +
                ", Sunday='" + Sunday + '\'' +
                ", Monday='" + Monday + '\'' +
                ", Tuesday='" + Tuesday + '\'' +
                ", Wednesday='" + Wednesday + '\'' +
                ", Thursday='" + Thursday + '\'' +
                ", Friday='" + Friday + '\'' +
                ", Saturday='" + Saturday + '\'' +
                '}';
    }

    // Setters and getters
    public void setId (int employeeId) {
        this.id = employeeId;
    }

    public void setSunday(String sunday) {
        this.Sunday = sunday;
    }

    public void setMonday(String monday) {
        this.Monday = monday;
    }

    public void setTuesday(String tuesday) {
        this.Tuesday = tuesday;
    }

    public void setWednesday(String wednesday) {
        this.Wednesday = wednesday;
    }

    public void setThursday(String thursday) {
        this.Thursday = thursday;
    }

    public void setFriday(String friday) {
        this.Friday = friday;
    }

    public void setSaturday(String saturday) {
        this.Saturday = saturday;
    }


    // Get variables
    public int getId() {
        return this.id;
    }

    public String getSunday() {
        return this.Sunday;
    }

    public String getMonday() {
        return this.Monday;
    }

    public String getTuesday() {
        return this.Tuesday;
    }

    public String getWednesday() {
        return this.Wednesday;
    }

    public String getThursday() {
        return this.Thursday;
    }

    public String getFriday() {
        return this.Friday;
    }

    public String getSaturday() {
        return this.Saturday;
    }
}

