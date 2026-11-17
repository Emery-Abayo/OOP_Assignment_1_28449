package com.auca.interfaces;

public interface Identifiable {
    // interface attributes are constants (public static final)
    public static final String ID_PREFIX = "AUCA";
    public static final int MIN_ID_LENGTH = 4;
    public static final int MAX_ID_LENGTH = 30;
    public static final String ID_SEPARATOR = "-";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final int ENTITY_VERSION = 1;

    // abstract methods
    String getId();

    String getDisplayName();

    boolean isActive();

    static String checkId(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            return field + " is required and cannot be blank.";
        }
        String id = value.trim();
        if (id.length() < MIN_ID_LENGTH || id.length() > MAX_ID_LENGTH) {
            return field + " must be " + MIN_ID_LENGTH + "-" + MAX_ID_LENGTH
                    + " characters long (you entered " + id.length() + ").";
        }
        if (!id.matches("[A-Za-z0-9_-]+")) {
            return field + " may contain only letters, digits, '-' and '_' (no spaces).";
        }
        return null;
    }

    // example: AUCA-STU-PER0001
    static String buildReference(String typeCode, String id) {
        return ID_PREFIX + ID_SEPARATOR + typeCode + ID_SEPARATOR + id;
    }

    // gives "[ID] name", used to show related records
    static String labelOf(Identifiable record) {
        if (record == null) {
            return "(none)";
        }
        return "[" + record.getId() + "] " + record.getDisplayName();
    }
}
