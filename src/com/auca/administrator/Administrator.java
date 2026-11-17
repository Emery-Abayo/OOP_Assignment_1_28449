package com.auca.administrator;

import com.auca.abstracts.Person;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.time.LocalDate;

public class Administrator extends Person {
    private String staffNumber;
    private String jobTitle;
    private String assignedOffice;
    private LocalDate employmentDate;
    private int accessLevel;
    private boolean active;

    public Administrator(String personId, String firstName, String lastName, String email,
                         String phoneNumber, LocalDate dateOfBirth,
                         String staffNumber, String jobTitle, String assignedOffice,
                         LocalDate employmentDate, int accessLevel, boolean active) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.staffNumber = staffNumber;
        this.jobTitle = jobTitle;
        this.assignedOffice = assignedOffice;
        this.employmentDate = employmentDate;
        this.accessLevel = accessLevel;
        this.active = active;
    }

    @Override
    public String getRoleDescription() {
        return "Administrator - " + jobTitle + " at " + assignedOffice + " (access level " + accessLevel + ")";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        String employmentOrder = null;
        if (employmentDate != null && getDateOfBirth() != null && !employmentDate.isAfter(getDateOfBirth())) {
            employmentOrder = "Employment date must be after the date of birth.";
        }
        Registrable.throwIfInvalid("Administrator",
                Identifiable.checkId(staffNumber, "Staff number"),
                Registrable.checkText(jobTitle, "Job title", MIN_NAME_LENGTH, MAX_NAME_LENGTH),
                Registrable.checkText(assignedOffice, "Assigned office", MIN_NAME_LENGTH, MAX_NAME_LENGTH),
                Registrable.checkNotFuture(employmentDate, "Employment date"),
                employmentOrder,
                Registrable.checkRange(accessLevel, "Access level", 1, 5));
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("  ADMINISTRATOR");
        System.out.printf("   %-20s: %s%n", "Staff number", staffNumber);
        System.out.printf("   %-20s: %s%n", "Job title", jobTitle);
        System.out.printf("   %-20s: %s%n", "Assigned office", assignedOffice);
        System.out.printf("   %-20s: %s%n", "Employment date", Registrable.formatDate(employmentDate));
        System.out.printf("   %-20s: %d%n", "Access level", accessLevel);
        System.out.printf("   %-20s: %s%n", "Active", active ? "YES" : "NO");
    }

    @Override
    public String toString() {
        return "Administrator " + super.toString() + " | " + staffNumber + " | " + jobTitle
                + " | " + (active ? "ACTIVE" : "INACTIVE");
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getAssignedOffice() {
        return assignedOffice;
    }

    public void setAssignedOffice(String assignedOffice) {
        this.assignedOffice = assignedOffice;
    }

    public LocalDate getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(LocalDate employmentDate) {
        this.employmentDate = employmentDate;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    // the getter for active is isActive() above

    public void setActive(boolean active) {
        this.active = active;
    }
}
