package com.auca.enrollment;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;

import java.time.LocalDate;

public class Enrollment implements Identifiable, Registrable {
    private String enrollmentId;
    private final Student student;
    private final Course course;
    private LocalDate enrollmentDate;
    private String semester;
    private String enrollmentStatus;

    public Enrollment(String enrollmentId, Student student, Course course, LocalDate enrollmentDate,
                      String semester, String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }

    @Override
    public String getId() {
        return enrollmentId;
    }

    @Override
    public String getDisplayName() {
        String who = student == null ? "?" : student.getDisplayName();
        String what = course == null ? "?" : course.getCourseCode();
        return who + " - " + what + " (" + semester + ")";
    }

    @Override
    public boolean isActive() {
        return DEFAULT_STATUS.equalsIgnoreCase(enrollmentStatus);
    }

    @Override
    public void validate() throws ValidationException {
        String beforeAdmission = null;
        if (enrollmentDate != null && student != null && student.getAdmissionDate() != null
                && enrollmentDate.isBefore(student.getAdmissionDate())) {
            beforeAdmission = "Enrollment date cannot be before the student's admission date ("
                    + Registrable.formatDate(student.getAdmissionDate()) + ").";
        }
        Registrable.throwIfInvalid("Enrollment",
                Identifiable.checkId(enrollmentId, "Enrollment ID"),
                Registrable.checkRequired(student, "Student"),
                Registrable.checkRequired(course, "Course"),
                Registrable.checkNotFuture(enrollmentDate, "Enrollment date"),
                beforeAdmission,
                Registrable.checkAllowed(semester, "Semester", Registrable.allowedValues("semester")),
                Registrable.checkAllowed(enrollmentStatus, "Enrollment status", Registrable.allowedValues("enrollmentStatus")));
    }

    @Override
    public void printDetails() {
        System.out.println("+------------------------------------------------------------+");
        System.out.println("  ENROLLMENT  " + Identifiable.labelOf(this));
        System.out.println("+------------------------------------------------------------+");
        System.out.printf("   %-20s: %s%n", "Enrollment ID", enrollmentId);
        System.out.printf("   %-20s: %s%n", "Student", Identifiable.labelOf(student));
        System.out.printf("   %-20s: %s%n", "Course", Identifiable.labelOf(course));
        System.out.printf("   %-20s: %s%n", "Enrollment date", Registrable.formatDate(enrollmentDate));
        System.out.printf("   %-20s: %s%n", "Semester", semester);
        System.out.printf("   %-20s: %s%n", "Enrollment status", enrollmentStatus);
    }

    @Override
    public String getRegistrationSummary() {
        return Identifiable.buildReference("ENR", enrollmentId) + " | Enrollment of "
                + (student == null ? "?" : student.getDisplayName()) + " in "
                + (course == null ? "?" : course.getDisplayName()) + " | " + semester + " | "
                + enrollmentStatus + " | v" + ENTITY_VERSION;
    }

    @Override
    public String toString() {
        return "Enrollment [" + enrollmentId + "] " + (student == null ? "?" : student.getDisplayName()) + " -> "
                + (course == null ? "?" : course.getCourseCode()) + " | " + semester + " | " + enrollmentStatus
                + " | since " + Registrable.formatDate(enrollmentDate);
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }
}
