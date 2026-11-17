package com.auca.abstracts;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.time.LocalDate;

// second level: Person -> AcademicMember -> Student / Teacher
public abstract class AcademicMember extends Person {
    private String memberNumber;
    private Department department;
    private LocalDate joinDate;
    private String academicStatus;
    private String institutionalEmail;
    private String advisorName;

    protected AcademicMember(String personId, String firstName, String lastName, String email,
                             String phoneNumber, LocalDate dateOfBirth,
                             String memberNumber, Department department, LocalDate joinDate,
                             String academicStatus, String institutionalEmail, String advisorName) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth); // Person constructor
        this.memberNumber = memberNumber;
        this.department = department;
        this.joinDate = joinDate;
        this.academicStatus = academicStatus;
        this.institutionalEmail = institutionalEmail;
        this.advisorName = advisorName;
    }

    public abstract String getAcademicCategory();

    @Override
    public boolean isActive() {
        return DEFAULT_STATUS.equalsIgnoreCase(academicStatus);
    }

    @Override
    public void validate() throws ValidationException {
        super.validate(); // person checks first
        String joinOrder = null;
        if (joinDate != null && getDateOfBirth() != null && !joinDate.isAfter(getDateOfBirth())) {
            joinOrder = "Join date must be after the date of birth.";
        }
        String sameEmail = null;
        if (institutionalEmail != null && institutionalEmail.trim().equalsIgnoreCase(getEmail())) {
            sameEmail = "Institutional email must be different from the personal email.";
        }
        Registrable.throwIfInvalid("Academic member",
                Identifiable.checkId(memberNumber, "Member number"),
                Registrable.checkRequired(department, "Department"),
                Registrable.checkNotFuture(joinDate, "Join date"),
                joinOrder,
                Registrable.checkAllowed(academicStatus, "Academic status", Registrable.allowedValues("academicStatus")),
                Registrable.checkInstitutionalEmail(institutionalEmail, "Institutional email"),
                sameEmail,
                Registrable.checkText(advisorName, "Advisor name", 2, 120));
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("  ACADEMIC MEMBER");
        System.out.printf("   %-20s: %s%n", "Member number", memberNumber);
        System.out.printf("   %-20s: %s%n", "Department", Identifiable.labelOf(department));
        System.out.printf("   %-20s: %s%n", "Join date", Registrable.formatDate(joinDate));
        System.out.printf("   %-20s: %s%n", "Academic status", academicStatus);
        System.out.printf("   %-20s: %s%n", "Institutional email", institutionalEmail);
        System.out.printf("   %-20s: %s%n", "Advisor name", advisorName);
        System.out.printf("   %-20s: %s%n", "Academic category", getAcademicCategory());
    }

    @Override
    public String toString() {
        String dept = department == null ? "no department" : department.getDepartmentCode();
        return super.toString() + " | member " + memberNumber + " | " + dept + " | " + academicStatus;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public String getInstitutionalEmail() {
        return institutionalEmail;
    }

    public void setInstitutionalEmail(String institutionalEmail) {
        this.institutionalEmail = institutionalEmail;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }
}
