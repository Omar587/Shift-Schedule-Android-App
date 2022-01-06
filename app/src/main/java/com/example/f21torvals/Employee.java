package com.example.f21torvals;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Create the employee database and person construct for each employees
 *
 * @author Omar Mohomed, Meaghan Neill, Axel Nguyen, Daine Garon, Rudra Patel
 */
public class Employee implements Serializable {
    // Employee Information
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String hireDate;
    private boolean isOpener;
    private boolean isCloser;
    private boolean isSelected;

    // Employee constructors
    public Employee() {
    }

    public Employee(int id, String firstName, String lastName,
                    String email, String phoneNumber, String hireDate,
                    Boolean isOpener, Boolean isCloser) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.hireDate = hireDate;
        this.isOpener = isOpener;
        this.isCloser = isCloser;
    }

    @NonNull
    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", hireDate='" + hireDate + '\'' +
                ", trainedOpener='" + isOpener + '\'' +
                ", trainedCloser='" + isCloser + '\'' +
                '}';
    }

    // Setters and getters
    public void setId(int employeeId) {
        this.id = employeeId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
    }

    public void setHireDate(String hired) {
        this.hireDate = hired;
    }

    public void setOpener(Boolean opener) {
        this.isOpener = opener;
    }

    public void setCloser(Boolean closer) {
        this.isCloser = closer;
    }

    public void setSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    // Get variables
    public int getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getHireDate() {
        return this.hireDate;
    }

    public boolean getOpener() {
        return this.isOpener;
    }

    public boolean getCloser() {
        return this.isCloser;
    }

    public boolean getSelected() {
        return this.isSelected;
    }
}