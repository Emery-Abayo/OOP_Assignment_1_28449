package com.auca.assessment;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.time.LocalDate;

public class Assessment implements Identifiable, Registrable {
    private String assessmentId;
    private final Enrollment enrollment;
    private String assessmentType;
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment, String assessmentType,
                      double maximumScore, double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    @Override
    public String getId() {
        return assessmentId;
    }

    @Override
    public String getDisplayName() {
        String course = enrollment == null || enrollment.getCourse() == null ? "?" : enrollment.getCourse().getCourseCode();
        return assessmentType + " " + course + " (" + formatScore(obtainedScore) + "/" + formatScore(maximumScore) + ")";
    }

    @Override
    public boolean isActive() {
        return enrollment != null && enrollment.isActive();
    }

    @Override
    public void validate() throws ValidationException {
        String scoreRule = null;
        if (maximumScore > 0 && (obtainedScore < 0 || obtainedScore > maximumScore)) {
            scoreRule = "Obtained score must be between 0 and the maximum score (" + formatScore(maximumScore) + ").";
        }
        String dateOrder = null;
        if (assessmentDate != null && enrollment != null && enrollment.getEnrollmentDate() != null
                && assessmentDate.isBefore(enrollment.getEnrollmentDate())) {
            dateOrder = "Assessment date cannot be before the enrollment date ("
                    + Registrable.formatDate(enrollment.getEnrollmentDate()) + ").";
        }
        Registrable.throwIfInvalid("Assessment",
                Identifiable.checkId(assessmentId, "Assessment ID"),
                Registrable.checkRequired(enrollment, "Enrollment"),
                Registrable.checkAllowed(assessmentType, "Assessment type", Registrable.allowedValues("assessmentType")),
                maximumScore <= 0 ? "Maximum score must be greater than zero." : null,
                Registrable.checkRange(maximumScore, "Maximum score", 0.01, 1000),
                scoreRule,
                Registrable.checkNotFuture(assessmentDate, "Assessment date"),
                dateOrder);
    }

    @Override
    public void printDetails() {
        System.out.println("+------------------------------------------------------------+");
        System.out.println("  ASSESSMENT  " + Identifiable.labelOf(this));
        System.out.println("+------------------------------------------------------------+");
        System.out.printf("   %-20s: %s%n", "Assessment ID", assessmentId);
        System.out.printf("   %-20s: %s%n", "Enrollment", Identifiable.labelOf(enrollment));
        System.out.printf("   %-20s: %s%n", "Assessment type", assessmentType);
        System.out.printf("   %-20s: %s%n", "Maximum score", formatScore(maximumScore));
        System.out.printf("   %-20s: %s (%.1f%%)%n", "Obtained score", formatScore(obtainedScore), getPercentage());
        System.out.printf("   %-20s: %s%n", "Assessment date", Registrable.formatDate(assessmentDate));
    }

    @Override
    public String getRegistrationSummary() {
        return Identifiable.buildReference("ASM", assessmentId) + " | " + assessmentType + " for "
                + (enrollment == null ? "?" : enrollment.getDisplayName()) + " | "
                + formatScore(obtainedScore) + "/" + formatScore(maximumScore) + " | v" + ENTITY_VERSION;
    }

    @Override
    public String toString() {
        return "Assessment [" + assessmentId + "] " + assessmentType + " | "
                + (enrollment == null ? "?" : enrollment.getDisplayName()) + " | "
                + formatScore(obtainedScore) + "/" + formatScore(maximumScore)
                + String.format(" (%.1f%%)", getPercentage()) + " | " + Registrable.formatDate(assessmentDate);
    }

    private double getPercentage() {
        return maximumScore <= 0 ? 0 : obtainedScore * 100.0 / maximumScore;
    }

    private static String formatScore(double score) {
        return score == Math.rint(score) ? String.valueOf((long) score) : String.valueOf(score);
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public double getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(double maximumScore) {
        this.maximumScore = maximumScore;
    }

    public double getObtainedScore() {
        return obtainedScore;
    }

    public void setObtainedScore(double obtainedScore) {
        this.obtainedScore = obtainedScore;
    }

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }
}
