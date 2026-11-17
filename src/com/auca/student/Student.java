package com.auca.student;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.time.LocalDate;

public class Student extends AcademicMember {
    private String studentNumber;
    private String programName;
    private LocalDate admissionDate;
    private int studyYear;
    private String guardianPhone;
    private boolean scholarshipStatus;

    public Student(String personId, String firstName, String lastName, String email,
                   String phoneNumber, LocalDate dateOfBirth,
                   String memberNumber, Department department, LocalDate joinDate,
                   String academicStatus, String institutionalEmail, String advisorName,
                   String studentNumber, String programName, LocalDate admissionDate,
                   int studyYear, String guardianPhone, boolean scholarshipStatus) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth,
                memberNumber, department, joinDate, academicStatus, institutionalEmail, advisorName);
        this.studentNumber = studentNumber;
        this.programName = programName;
        this.admissionDate = admissionDate;
        this.studyYear = studyYear;
        this.guardianPhone = guardianPhone;
        this.scholarshipStatus = scholarshipStatus;
    }

    @Override
    public String getRoleDescription() {
        String dept = getDepartment() == null ? "no department" : getDepartment().getDepartmentName();
        return "Student of " + programName + " (year " + studyYear + ") in " + dept;
    }

    @Override
    public String getAcademicCategory() {
        return "Undergraduate student - year " + studyYear + (scholarshipStatus ? " (scholarship)" : " (self-sponsored)");
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        String admissionOrder = null;
        if (admissionDate != null && getDateOfBirth() != null && !admissionDate.isAfter(getDateOfBirth())) {
            admissionOrder = "Admission date must be after the date of birth.";
        }
        // study year must be 1-6
        Registrable.throwIfInvalid("Student",
                Identifiable.checkId(studentNumber, "Student number"),
                Registrable.checkText(programName, "Program name", 2, 120),
                Registrable.checkNotFuture(admissionDate, "Admission date"),
                admissionOrder,
                Registrable.checkRange(studyYear, "Study year", 1, 6),
                Registrable.checkPhone(guardianPhone, "Guardian phone"));
    }

    @Override
    public void printDetails() {
        super.printDetails(); // person and academic part first
        System.out.println("  STUDENT");
        System.out.printf("   %-20s: %s%n", "Student number", studentNumber);
        System.out.printf("   %-20s: %s%n", "Program name", programName);
        System.out.printf("   %-20s: %s%n", "Admission date", Registrable.formatDate(admissionDate));
        System.out.printf("   %-20s: %d%n", "Study year", studyYear);
        System.out.printf("   %-20s: %s%n", "Guardian phone", guardianPhone);
        System.out.printf("   %-20s: %s%n", "Scholarship", scholarshipStatus ? "YES" : "NO");
    }

    @Override
    public String toString() {
        return "Student " + super.toString() + " | " + studentNumber + " | " + programName + ", year " + studyYear;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public int getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(int studyYear) {
        this.studyYear = studyYear;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    public boolean isScholarshipStatus() {
        return scholarshipStatus;
    }

    public void setScholarshipStatus(boolean scholarshipStatus) {
        this.scholarshipStatus = scholarshipStatus;
    }
}
