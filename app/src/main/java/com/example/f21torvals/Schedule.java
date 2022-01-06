package com.example.f21torvals;

import androidx.annotation.NonNull;

import java.util.Objects;

/** Display the schedule of the employee
 * Status: Not sure if this is complete?
 * @author Omar Mohomed, Meaghan Neill, Axel Nguyen, Daine Garon, Rudra Patel
 */
public class Schedule {


    private String date; //dd/mm/yyyy
    private String shiftType;
    private int busyDay; //0 for false, 1 for true

    public Schedule() {
        this("", "", 0);
    }


    public Schedule(String date, String shiftType, int busyDay) {
        this.date = date;
        this.shiftType = shiftType;
        this.busyDay = busyDay;
    }

    @NonNull
    @Override
    public String toString() {
        return "Schedule{" +
                "date='" + date + '\'' +
                ", shiftType='" + shiftType + '\'' +
                ", busyDay='" + busyDay + '\'' +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public int getBusyDay() {
        return busyDay;
    }

    public void setBusyDay(int   busyDay) {
        this.busyDay = busyDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return date.equals(schedule.date) && shiftType.equals(schedule.shiftType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, shiftType);
    }
}
