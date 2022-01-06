package com.example.f21torvals.activity.schedule.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f21torvals.DatabaseHelper;
import com.example.f21torvals.Employee;
import com.example.f21torvals.EmployeeClickListener;
import com.example.f21torvals.R;
import com.example.f21torvals.activity.CustomAdapter;
import com.example.f21torvals.activity.schedule.AvailableEmployee;

import java.util.List;

public class AfternoonFragment extends Fragment implements EmployeeClickListener {

    RecyclerView rv_employees;
    List<Employee> availableEmployeeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_afternoon, container, false);
        String dayOfWeek = null;
        String date = null;

        if (this.getArguments() != null) {
            dayOfWeek = this.getArguments().getString("dayOfWeek");
            date = this.getArguments().getString("date");
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(view.getContext());
        // Get available employees for this shift
        availableEmployeeList = databaseHelper.getAvailableEmployee(dayOfWeek, date, "afternoon");
        availableEmployeeList.addAll(databaseHelper.getAvailableEmployee(dayOfWeek, date, "all day"));
        CustomAdapter customAdapter = new CustomAdapter(availableEmployeeList, AfternoonFragment.this);

        //Show to Recycle View
        rv_employees = view.findViewById(R.id.afternoon_shifts_employee_list);
        rv_employees.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv_employees.setAdapter(customAdapter);

        return view;
    }

    @Override
    public void onEmployeeClick(Employee employee) {
        AvailableEmployee.toggleEmployeeInList(employee);
    }
}