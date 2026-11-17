package com.auca.course;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Course implements Identifiable, Registrable {
    private String courseId;
    private String courseCode;
    private String courseTitle;
    private int creditHours;
    private Department department;
    private Teacher assignedTeacher;

    public Course(String courseId, String courseCode, String courseTitle, int creditHours,
                  Department department, Teacher assignedTeacher) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.department = department;
        this.assignedTeacher = assignedTeacher;
    }

    @Override
    public String getId() {
        return courseId;
    }

    @Override
    public String getDisplayName() {
        return courseCode + " " + courseTitle;
    }

    @Override
    public boolean isActive() {
        return assignedTeacher != null && assignedTeacher.isActive();
    }

    @Override
    public void validate() throws ValidationException {
        Registrable.throwIfInvalid("Course",
                Identifiable.checkId(courseId, "Course ID"),
                Registrable.checkCode(courseCode, "Course code", 3, 15),
                Registrable.checkText(courseTitle, "Course title", 2, 120),
                Registrable.checkRange(creditHours, "Credit hours", 1, 6),
                Registrable.checkRequired(department, "Department"),
                Registrable.checkRequired(assignedTeacher, "Assigned teacher"));
    }

    @Override
    public void printDetails() {
        System.out.println("+------------------------------------------------------------+");
        System.out.println("  COURSE  " + Identifiable.labelOf(this));
        System.out.println("+------------------------------------------------------------+");
        System.out.printf("   %-20s: %s%n", "Course ID", courseId);
        System.out.printf("   %-20s: %s%n", "Course code", courseCode);
        System.out.printf("   %-20s: %s%n", "Course title", courseTitle);
        System.out.printf("   %-20s: %d%n", "Credit hours", creditHours);
        System.out.printf("   %-20s: %s%n", "Department", Identifiable.labelOf(department));
        System.out.printf("   %-20s: %s%n", "Assigned teacher", Identifiable.labelOf(assignedTeacher));
    }

    @Override
    public String getRegistrationSummary() {
        return Identifiable.buildReference("CRS", courseId) + " | Course " + courseCode + " " + courseTitle
                + " (" + creditHours + " credits) | " + (department == null ? "-" : department.getDepartmentName())
                + " | taught by " + (assignedTeacher == null ? "-" : assignedTeacher.getDisplayName())
                + " | v" + ENTITY_VERSION;
    }

    @Override
    public String toString() {
        return "Course [" + courseId + "] " + courseCode + " - " + courseTitle + " | " + creditHours + " credits | "
                + (department == null ? "-" : department.getDepartmentCode()) + " | "
                + (assignedTeacher == null ? "-" : assignedTeacher.getDisplayName());
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Teacher getAssignedTeacher() {
        return assignedTeacher;
    }

    public void setAssignedTeacher(Teacher assignedTeacher) {
        this.assignedTeacher = assignedTeacher;
    }
}
