package com.auca.teacher;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Teacher extends AcademicMember {
    private String employeeNumber;
    private String specialization;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String officeNumber;
    private String employmentType;

    public Teacher(String personId, String firstName, String lastName, String email,
                   String phoneNumber, LocalDate dateOfBirth,
                   String memberNumber, Department department, LocalDate joinDate,
                   String academicStatus, String institutionalEmail, String advisorName,
                   String employeeNumber, String specialization, LocalDate hireDate,
                   BigDecimal salary, String officeNumber, String employmentType) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth,
                memberNumber, department, joinDate, academicStatus, institutionalEmail, advisorName);
        this.employeeNumber = employeeNumber;
        this.specialization = specialization;
        this.hireDate = hireDate;
        this.salary = salary;
        this.officeNumber = officeNumber;
        this.employmentType = employmentType;
    }

    @Override
    public String getRoleDescription() {
        String dept = getDepartment() == null ? "no department" : getDepartment().getDepartmentName();
        return "Teacher of " + specialization + " in " + dept + " (" + employmentType + ")";
    }

    @Override
    public String getAcademicCategory() {
        return "Academic staff - " + employmentType;
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        String hireOrder = null;
        if (hireDate != null && getDateOfBirth() != null && !hireDate.isAfter(getDateOfBirth())) {
            hireOrder = "Hire date must be after the date of birth.";
        }
        Registrable.throwIfInvalid("Teacher",
                Identifiable.checkId(employeeNumber, "Employee number"),
                Registrable.checkText(specialization, "Specialization", MIN_NAME_LENGTH, MAX_NAME_LENGTH),
                Registrable.checkNotFuture(hireDate, "Hire date"),
                hireOrder,
                Registrable.checkPositiveAmount(salary, "Salary"),
                Registrable.checkText(officeNumber, "Office number", 1, 20),
                Registrable.checkAllowed(employmentType, "Employment type", Registrable.allowedValues("employmentType")));
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("  TEACHER");
        System.out.printf("   %-20s: %s%n", "Employee number", employeeNumber);
        System.out.printf("   %-20s: %s%n", "Specialization", specialization);
        System.out.printf("   %-20s: %s%n", "Hire date", Registrable.formatDate(hireDate));
        System.out.printf("   %-20s: %s%n", "Salary", Registrable.formatMoney(salary));
        System.out.printf("   %-20s: %s%n", "Office number", officeNumber);
        System.out.printf("   %-20s: %s%n", "Employment type", employmentType);
    }

    @Override
    public String toString() {
        return "Teacher " + super.toString() + " | " + employeeNumber + " | " + specialization + " | " + employmentType;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }
}
