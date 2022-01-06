package com.example.f21torvals;

import org.junit.Test;

import static org.junit.Assert.*;

import android.content.Context;
import android.provider.ContactsContract;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    //Test creation of schedule object
    @Test
    public void scheduleCreation(){

    }

    /*not sure what goes into the databasehelper paramater without causing a weird error. */
    @Test
    public void scheduleTableInsertion(){

        Schedule sch = new Schedule("2021/10/29", "M", 1);
        /*DatabaseHelper databaseHelper = new DatabaseHelper("Paramater??");
        databaseHelper.addSchedule(sch); */

    }
}