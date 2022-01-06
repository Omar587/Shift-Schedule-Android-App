package com.example.f21torvals.activity.employee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.example.f21torvals.DrawerBaseActivity;
import com.example.f21torvals.databinding.ActivityAddingemployBinding;

/* Allows the UI to be used to enter in the information for the employees
  @author Omar Mohamed, Meaghan Neill, Axel Nguyen, Daine Garon, Rudra Patel
 */
import com.example.f21torvals.DatabaseHelper;
import com.example.f21torvals.Employee;
import com.example.f21torvals.R;

public class AddingEmployeeActivity extends DrawerBaseActivity {
    Button saveButton;
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText phone;
    EditText hiredDate;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Employee employee;
    Bundle extras;
    DatabaseHelper databaseHelper;
    ActivityAddingemployBinding activityAddingemployBinding;

    /**
     * Actually gets the employee information from the UI activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddingemployBinding = ActivityAddingemployBinding.inflate(getLayoutInflater());
        setContentView(activityAddingemployBinding.getRoot());
        allocateActivityTilte("Add Employee");
        //Get size of list of employees so that can create ID
        extras = getIntent().getExtras();
        databaseHelper = new DatabaseHelper(AddingEmployeeActivity.this);
        getFields();
        addButtonListener();
        if (extras.getBoolean("isEdit")) {
            setFields();
        }


    }

    public void getFields() {
        saveButton = findViewById(R.id.save_button);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        hiredDate = findViewById(R.id.hired_date);
        radioGroup = findViewById(R.id.trained_as);
        radioGroup.check(R.id.none);
    }

    public void setFields() {
        int employeeId = extras.getInt("employeeId");

        Employee employee = databaseHelper.getEmployeeByID(employeeId);

        firstName.setText(employee.getFirstName());
        lastName.setText(employee.getLastName());
        email.setText(employee.getEmail());
        phone.setText(employee.getPhoneNumber());
        hiredDate.setText(employee.getHireDate());
        if (employee.getCloser() && employee.getOpener()) {
            radioGroup.check(R.id.both);
        }

        if (employee.getOpener()) {
            radioGroup.check(R.id.opener);
            return;
        }

        if (employee.getCloser()) {
            radioGroup.check(R.id.closer);
            return;
        }

        radioGroup.check(R.id.none);


    }

    /**
     * The ability for the buttons to work for the Opener/Closer Role
     */
    public void addButtonListener() {
        saveButton.setOnClickListener(view -> {

            if (firstName.getText().toString().equals("") || lastName.getText().toString().equals("") || email.getText().toString().equals("") || phone.getText().toString().equals("") || radioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(AddingEmployeeActivity.this, "Required fields: First Name, Last Name, Email, Phone, and Roles", Toast.LENGTH_LONG).show();
                return;
            }
            //Get Radio Button value
            int radioSelectedId = radioGroup.getCheckedRadioButtonId();
            radioButton = findViewById(radioSelectedId);
            //Get id
            int employeeId;
            if (!extras.getBoolean("isEdit")) {
                employeeId = extras.getInt("lastEmployeeId") + 1;
            } else {
                employeeId = extras.getInt("employeeId");
            }


            try {
                //Get role
                String role = radioButton.getText().toString();
                Boolean opener = role.equals("Opener") || role.equals("Both");
                Boolean closer = role.equals("Closer") || role.equals("Both");

                employee = new Employee(employeeId, firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), phone.getText().toString(), hiredDate.getText().toString(), opener, closer);
            } catch (Exception e) {
                Toast.makeText(AddingEmployeeActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

            long index;
            boolean isEdit = extras.getBoolean("isEdit");
            if (!isEdit) {
                index = databaseHelper.addEmployee(employee);
            } else {
               index = databaseHelper.editEmployee(employee);
            }


            Toast.makeText(AddingEmployeeActivity.this, index > 0 ? "Done" : "Error", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddingEmployeeActivity.this, isEdit ? ViewEmployeeActivity.class : AvailabilityActivity.class);
            intent.putExtra("employeeId",(int) index);
            startActivity(intent);
        });
    }

}
