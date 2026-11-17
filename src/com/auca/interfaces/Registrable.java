package com.auca.interfaces;

import com.auca.exceptions.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public interface Registrable {
    // interface attributes are constants (public static final)
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 60;
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final int MIN_PHONE_LENGTH = 10;
    public static final int MAX_PHONE_LENGTH = 15;
    public static final String DATE_PATTERN = "uuuu-MM-dd";

    // abstract methods
    void validate() throws ValidationException;

    void printDetails();

    String getRegistrationSummary();

    // validation rules: they return null when the value is ok, otherwise the error message

    static String checkRequired(Object value, String field) {
        return value == null ? field + " is required." : null;
    }

    static String checkText(String value, String field, int min, int max) {
        if (value == null || value.trim().isEmpty()) {
            return field + " is required and cannot be blank.";
        }
        int length = value.trim().length();
        if (length < min || length > max) {
            return field + " must be " + min + "-" + max + " characters long (you entered " + length + ").";
        }
        return null;
    }

    static String checkName(String value, String field) {
        String error = checkText(value, field, MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        if (error != null) {
            return error;
        }
        if (!value.trim().matches("[\\p{L} .'-]+")) {
            return field + " may contain only letters, spaces, apostrophes, dots and hyphens.";
        }
        return null;
    }

    static String checkCode(String value, String field, int min, int max) {
        String error = checkText(value, field, min, max);
        if (error != null) {
            return error;
        }
        if (!value.trim().matches("[A-Za-z0-9 -]+")) {
            return field + " may contain only letters, digits, spaces and hyphens.";
        }
        return null;
    }

    static String checkEmail(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            return field + " is required and cannot be blank.";
        }
        String email = value.trim();
        if (email.length() > MAX_EMAIL_LENGTH) {
            return field + " must be at most " + MAX_EMAIL_LENGTH + " characters long.";
        }
        if (!email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}")
                || email.contains("..")) {
            return field + " is not a valid e-mail address (example: name@example.com).";
        }
        return null;
    }

    static String checkInstitutionalEmail(String value, String field) {
        String error = checkEmail(value, field);
        if (error != null) {
            return error;
        }
        if (!value.trim().toLowerCase(Locale.ROOT).endsWith("@auca.ac.rw")) {
            return field + " must be an AUCA address ending with @auca.ac.rw (example: alice@auca.ac.rw).";
        }
        return null;
    }

    static String checkPhone(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            return field + " is required and cannot be blank.";
        }
        String phone = value.trim();
        String digits = phone.startsWith("+") ? phone.substring(1) : phone;
        if (!digits.matches("\\d+")) {
            return field + " must contain digits only, with an optional leading + (example: 0781234567).";
        }
        if (digits.length() < MIN_PHONE_LENGTH || digits.length() > MAX_PHONE_LENGTH) {
            return field + " must contain " + MIN_PHONE_LENGTH + "-" + MAX_PHONE_LENGTH
                    + " digits (you entered " + digits.length() + ").";
        }
        return null;
    }

    static String checkIsbn(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            return field + " is required and cannot be blank.";
        }
        String compact = value.trim().replace("-", "");
        if (!(compact.matches("\\d{13}") || compact.matches("\\d{9}[\\dXx]"))) {
            return field + " must have 10 or 13 digits; hyphens are allowed (example: 978-0134685991).";
        }
        return null;
    }

    static String checkPastDate(LocalDate date, String field) {
        if (date == null) {
            return field + " is required.";
        }
        if (!date.isBefore(LocalDate.now())) {
            return field + " must be in the past (before " + formatDate(LocalDate.now()) + ").";
        }
        if (date.isBefore(LocalDate.of(1900, 1, 1))) {
            return field + " cannot be before 1900-01-01.";
        }
        return null;
    }

    static String checkNotFuture(LocalDate date, String field) {
        if (date == null) {
            return field + " is required.";
        }
        if (date.isAfter(LocalDate.now())) {
            return field + " cannot be in the future (today is " + formatDate(LocalDate.now()) + ").";
        }
        if (date.isBefore(LocalDate.of(1900, 1, 1))) {
            return field + " cannot be before 1900-01-01.";
        }
        return null;
    }

    static String checkRange(int value, String field, int min, int max) {
        if (value < min || value > max) {
            return field + " must be between " + min + " and " + max + " (you entered " + value + ").";
        }
        return null;
    }

    static String checkRange(double value, String field, double min, double max) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < min || value > max) {
            return field + " must be between " + min + " and " + max + ".";
        }
        return null;
    }

    static String checkPositiveAmount(BigDecimal amount, String field) {
        if (amount == null) {
            return field + " is required.";
        }
        if (amount.signum() <= 0) {
            return field + " must be strictly greater than zero.";
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            return field + " can have at most 2 decimal places.";
        }
        if (amount.compareTo(new BigDecimal("1000000000")) > 0) {
            return field + " cannot exceed 1,000,000,000.";
        }
        return null;
    }

    static String checkAllowed(String value, String field, String[] allowed) {
        if (value == null || value.trim().isEmpty()) {
            return field + " is required.";
        }
        for (String option : allowed) {
            if (option.equalsIgnoreCase(value.trim())) {
                return null;
            }
        }
        return field + " must be one of " + String.join(", ", allowed) + ".";
    }

    // allowed values for the choice fields
    static String[] allowedValues(String attribute) {
        switch (attribute) {
            case "academicStatus":   return new String[] {"ACTIVE", "INACTIVE"};
            case "employmentType":   return new String[] {"FULL_TIME", "PART_TIME", "CONTRACT"};
            case "semester":         return new String[] {"SEMESTER_1", "SEMESTER_2", "SUMMER"};
            case "enrollmentStatus": return new String[] {"ACTIVE", "COMPLETED", "DROPPED"};
            case "assessmentType":   return new String[] {"QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL_EXAM", "PROJECT"};
            case "paymentMethod":    return new String[] {"CASH", "BANK_TRANSFER", "MOBILE_MONEY", "CARD"};
            default:                 return new String[0];
        }
    }

    // helpers for dates and money

    // uuuu (not yyyy) because STRICT needs it, then dates like 2025-02-30 are rejected
    static LocalDate parseDate(String text) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN).withResolverStyle(ResolverStyle.STRICT);
        return LocalDate.parse(text.trim(), formatter);
    }

    static String formatDate(LocalDate date) {
        return date == null ? "(none)" : date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    static String formatMoney(BigDecimal amount) {
        return amount == null ? "(none)" : String.format(Locale.US, "%,.2f RWF", amount);
    }

    // collects the error messages and throws one ValidationException
    static void throwIfInvalid(String recordType, String... errors) throws ValidationException {
        List<String> problems = new ArrayList<>();
        for (String error : errors) {
            if (error != null) {
                problems.add(error);
            }
        }
        if (!problems.isEmpty()) {
            throw new ValidationException(recordType + " is invalid: " + String.join(" ", problems));
        }
    }
}
