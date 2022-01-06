package com.example.f21torvals.activity.employee;

import android.content.Intent;
import android.os.Bundle;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f21torvals.DatabaseHelper;
import com.example.f21torvals.DrawerBaseActivity;
import com.example.f21torvals.Employee;
import com.example.f21torvals.R;
import com.example.f21torvals.activity.CustomAdapter;
import com.example.f21torvals.databinding.ActivityViewEmployeeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * See the employee's saved "person" information and the availability information
 *
 * @author Omar Mohomed, Meaghan Neill, Axel Nguyen, Daine Garon, Rudra Patel
 */
public class ViewEmployeeActivity extends DrawerBaseActivity {
    List<Employee> employeeList;
    RecyclerView rv_employees;
    FloatingActionButton addEmployeeButton;

    ActivityViewEmployeeBinding activityViewEmployeeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityViewEmployeeBinding = ActivityViewEmployeeBinding.inflate(getLayoutInflater());
        setContentView(activityViewEmployeeBinding.getRoot());
        allocateActivityTilte("View Employee");

        //Add employee button listener
        addEmployeeButton = findViewById(R.id.add_employee_button);
        addEmployeeButton.setOnClickListener(view -> {


            Intent intent = new Intent(ViewEmployeeActivity.this, AddingEmployeeActivity.class);
            intent.putExtra("lastEmployeeId", employeeList.isEmpty() ? 0 : employeeList.get(employeeList.size() - 1).getId());
            intent.putExtra("isEdit", false);
            startActivity(intent);
        });

        //Show everyone
        DatabaseHelper databaseHelper = new DatabaseHelper(ViewEmployeeActivity.this);
        employeeList = databaseHelper.getAllEmployees();
        CustomAdapter employeeAdapter = new CustomAdapter(employeeList);

        //Show to Recycle View
        rv_employees = findViewById(R.id.employee_list);
        rv_employees.setLayoutManager(new LinearLayoutManager(this));
        rv_employees.setAdapter(employeeAdapter);
    }
}