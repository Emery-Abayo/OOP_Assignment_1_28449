package com.auca.department;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Department implements Identifiable, Registrable {
    private String departmentId;
    private String departmentName;
    private String departmentCode;
    private String officeLocation;
    private String contactEmail;
    private Teacher headTeacher;

    public Department(String departmentId, String departmentName, String departmentCode,
                      String officeLocation, String contactEmail, Teacher headTeacher) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.officeLocation = officeLocation;
        this.contactEmail = contactEmail;
        this.headTeacher = headTeacher;
    }

    // when there is no head teacher yet
    public Department(String departmentId, String departmentName, String departmentCode,
                      String officeLocation, String contactEmail) {
        this(departmentId, departmentName, departmentCode, officeLocation, contactEmail, null);
    }

    @Override
    public String getId() {
        return departmentId;
    }

    @Override
    public String getDisplayName() {
        return departmentName;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void validate() throws ValidationException {
        // head teacher can be empty at the start
        Registrable.throwIfInvalid("Department",
                Identifiable.checkId(departmentId, "Department ID"),
                Registrable.checkText(departmentName, "Department name", MIN_NAME_LENGTH, MAX_NAME_LENGTH),
                Registrable.checkCode(departmentCode, "Department code", 2, 10),
                Registrable.checkText(officeLocation, "Office location", MIN_NAME_LENGTH, MAX_NAME_LENGTH),
                Registrable.checkEmail(contactEmail, "Contact email"));
    }

    @Override
    public void printDetails() {
        System.out.println("+------------------------------------------------------------+");
        System.out.println("  DEPARTMENT  " + Identifiable.labelOf(this));
        System.out.println("+------------------------------------------------------------+");
        System.out.printf("   %-20s: %s%n", "Department ID", departmentId);
        System.out.printf("   %-20s: %s%n", "Department name", departmentName);
        System.out.printf("   %-20s: %s%n", "Department code", departmentCode);
        System.out.printf("   %-20s: %s%n", "Office location", officeLocation);
        System.out.printf("   %-20s: %s%n", "Contact email", contactEmail);
        System.out.printf("   %-20s: %s%n", "Head teacher",
                headTeacher == null ? "(none - assign later with Update Record)" : Identifiable.labelOf(headTeacher));
    }

    @Override
    public String getRegistrationSummary() {
        return Identifiable.buildReference("DEP", departmentId) + " | Department " + departmentName
                + " (" + departmentCode + ") | head: " + (headTeacher == null ? "not assigned" : headTeacher.getDisplayName())
                + " | v" + ENTITY_VERSION;
    }

    @Override
    public String toString() {
        return "Department [" + departmentId + "] " + departmentName + " (" + departmentCode + ") | "
                + officeLocation + " | head: " + (headTeacher == null ? "none" : headTeacher.getDisplayName());
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Teacher getHeadTeacher() {
        return headTeacher;
    }

    public void setHeadTeacher(Teacher headTeacher) {
        this.headTeacher = headTeacher;
    }
}
