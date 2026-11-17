package com.auca.classroom;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class Classroom implements Identifiable, Registrable {
    private String classroomId;
    private String buildingName;
    private String roomNumber;
    private int capacity;
    private int floorNumber;
    private boolean available;

    public Classroom(String classroomId, String buildingName, String roomNumber,
                     int capacity, int floorNumber, boolean available) {
        this.classroomId = classroomId;
        this.buildingName = buildingName;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.floorNumber = floorNumber;
        this.available = available;
    }

    @Override
    public String getId() {
        return classroomId;
    }

    @Override
    public String getDisplayName() {
        return buildingName + " room " + roomNumber;
    }

    @Override
    public boolean isActive() {
        return available;
    }

    @Override
    public void validate() throws ValidationException {
        // capacity at least 1, floor can be 0 (ground floor)
        Registrable.throwIfInvalid("Classroom",
                Identifiable.checkId(classroomId, "Classroom ID"),
                Registrable.checkText(buildingName, "Building name", MIN_NAME_LENGTH, MAX_NAME_LENGTH),
                Registrable.checkCode(roomNumber, "Room number", 1, 20),
                Registrable.checkRange(capacity, "Capacity", 1, 1000),
                Registrable.checkRange(floorNumber, "Floor number", 0, 100));
    }

    @Override
    public void printDetails() {
        System.out.println("+------------------------------------------------------------+");
        System.out.println("  CLASSROOM  " + Identifiable.labelOf(this));
        System.out.println("+------------------------------------------------------------+");
        System.out.printf("   %-20s: %s%n", "Classroom ID", classroomId);
        System.out.printf("   %-20s: %s%n", "Building name", buildingName);
        System.out.printf("   %-20s: %s%n", "Room number", roomNumber);
        System.out.printf("   %-20s: %d%n", "Capacity", capacity);
        System.out.printf("   %-20s: %d%n", "Floor number", floorNumber);
        System.out.printf("   %-20s: %s%n", "Available", available ? "YES" : "NO");
    }

    @Override
    public String getRegistrationSummary() {
        return Identifiable.buildReference("CLS", classroomId) + " | Classroom " + getDisplayName()
                + ", floor " + floorNumber + ", " + capacity + " seats | "
                + (available ? "AVAILABLE" : "NOT AVAILABLE") + " | v" + ENTITY_VERSION;
    }

    @Override
    public String toString() {
        return "Classroom [" + classroomId + "] " + buildingName + " " + roomNumber + " | floor " + floorNumber
                + " | " + capacity + " seats | " + (available ? "available" : "not available");
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
