package com.example.f21torvals.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f21torvals.DatabaseHelper;
import com.example.f21torvals.Employee;
import com.example.f21torvals.EmployeeClickListener;
import com.example.f21torvals.R;
import com.example.f21torvals.activity.employee.AddingEmployeeActivity;
import com.example.f21torvals.activity.employee.ViewAvailabilityActivity;
import com.example.f21torvals.activity.employee.ViewEmployeeActivity;
import com.example.f21torvals.activity.schedule.AvailableEmployee;

import java.util.List;

/**
 * Custom views for the UI
 *
 * @author Omar Mohomed, Meaghan Neill, Axel Nguyen, Daine Garon, Rudra Patel
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<Employee> localDataSet;
    private EmployeeClickListener employeeClickListener;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView nameView;
        private final TextView emailView;
        private final TextView phoneView;
        private final TextView roleView;
        private final Button viewAvailButton;
        private final ImageButton editEmployeeButton;
        private final ImageView AM_image;
        private final ImageView PM_image;
        private Employee employee;
        private final Context context;
        private EmployeeClickListener employeeClickListener;
        private ImageView deleteEmployeeButton;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener(this);
            context = view.getContext();
            nameView = view.findViewById(R.id.name_view);
            emailView = view.findViewById(R.id.email_view);
            phoneView = view.findViewById(R.id.phone_view);
            roleView = view.findViewById(R.id.role_view);
            viewAvailButton = view.findViewById(R.id.view_avail_btn);
            AM_image = view.findViewById(R.id.AM_image);
            PM_image = view.findViewById(R.id.PM_image);
            editEmployeeButton = view.findViewById(R.id.edit_employee);
            deleteEmployeeButton = view.findViewById(R.id.delete_employee);

            viewAvailButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewAvailabilityActivity.class);
                intent.putExtra("employeeId", employee.getId());
                intent.putExtra("employeeName", employee.getFullName());
                context.startActivity(intent);
            });

            editEmployeeButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, AddingEmployeeActivity.class);
                intent.putExtra("employeeId", employee.getId());
                intent.putExtra("isEdit", true);
                context.startActivity(intent);
            });


        }

        public TextView getEmailView() {
            return emailView;
        }

        public TextView getPhoneView() {
            return phoneView;
        }

        public TextView getRoleView() {
            return roleView;
        }

        public TextView getNameView() {
            return nameView;
        }

        public ImageView getAM_image() {
            return AM_image;
        }

        public ImageView getPM_image() {
            return PM_image;
        }

        public ImageView getEditButton() {
            return editEmployeeButton;
        }

        public void setEmployee(Employee employee) {
            this.employee = employee;
        }

        public void setEmployeeClickListener(EmployeeClickListener employeeClickListener) {
            this.employeeClickListener = employeeClickListener;
        }

        public void onClick(View view) {

            if (context instanceof AvailableEmployee) {

                View parent = view.getRootView();

                if (!AvailableEmployee.hasEnoughEmployees()) {
                    employee.setSelected(!employee.getSelected());
                    view.setBackgroundColor(employee.getSelected() ? context.getColor(R.color.project_blue) : Color.WHITE);

                    employeeClickListener.onEmployeeClick(employee);
                } else {
                    if (employee.getSelected()) {
                        employee.setSelected(!employee.getSelected());
                        view.setBackgroundColor(employee.getSelected() ? context.getColor(R.color.project_blue) : Color.WHITE);
                        employeeClickListener.onEmployeeClick(employee);
                    } else {
                        Toast.makeText(context, "Maximum employees reached for this shift!", Toast.LENGTH_SHORT).show();
                    }

                }
                Button assignButton = parent.findViewById(R.id.assign_shift_button);
                assignButton.setVisibility(View.VISIBLE);
            }
        }

        public ImageView getDeleteButton() {
            return this.deleteEmployeeButton;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    public CustomAdapter(List<Employee> dataSet, EmployeeClickListener employeeClickListener) {
        this.employeeClickListener = employeeClickListener;
        localDataSet = dataSet;
    }

    public CustomAdapter(List<Employee> dataSet) {
        this(dataSet, null);
    }


    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Employee employee = localDataSet.get(position);
        String role;
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getNameView().setText(employee.getFullName());
        viewHolder.getEmailView().setText(employee.getEmail());
        viewHolder.getPhoneView().setText(employee.getPhoneNumber());
        viewHolder.setEmployee(employee);
        viewHolder.setEmployeeClickListener(this.employeeClickListener);
        viewHolder.getDeleteButton().setOnClickListener(v -> new AlertDialog.Builder(viewHolder.context).setTitle("Delete " + employee.getFullName() + " from employee list?")
                .setMessage("You will not be able to undo this!")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    DatabaseHelper databaseHelper = new DatabaseHelper(viewHolder.context);
                    databaseHelper.deleteEmployeeFromShiftFactTable(String.valueOf(employee.getId()));
                    databaseHelper.deleteEmployeeFromAvailability(String.valueOf(employee.getId()));
                    databaseHelper.deleteEmployeeFromEmployee(String.valueOf(employee.getId()));
                    localDataSet.remove(viewHolder.getAdapterPosition());
                    notifyDataSetChanged();
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.presence_busy)
                .show());

        if (viewHolder.context instanceof ViewEmployeeActivity) {
            viewHolder.getPM_image().setVisibility(View.GONE);
            viewHolder.getAM_image().setVisibility(View.GONE);
        }

        if (viewHolder.context instanceof AvailableEmployee) {
            DatabaseHelper databaseHelper = new DatabaseHelper(viewHolder.context);
            String todayAvailability = databaseHelper.searchEmployeeAvailabilityByDayOfWeek(employee.getId(), ((AvailableEmployee) viewHolder.context).getIntent().getExtras().getString("dayOfWeek"));
            viewHolder.getEditButton().setVisibility(View.GONE);
            viewHolder.getDeleteButton().setVisibility(View.GONE);
            if (todayAvailability.equalsIgnoreCase("Morning")) {
                viewHolder.getPM_image().setVisibility(View.GONE);
            } else if (todayAvailability.equalsIgnoreCase("Afternoon")) {
                viewHolder.getAM_image().setVisibility(View.GONE);
            }
        }
        if (employee.getOpener() && employee.getCloser()) {
            role = "Opener, Closer";
        } else if (employee.getOpener()) {
            role = "Opener";
        } else if (employee.getCloser()) {
            role = "Closer";
        } else {
            role = "In Training";
        }
        viewHolder.getRoleView().setText(role);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
