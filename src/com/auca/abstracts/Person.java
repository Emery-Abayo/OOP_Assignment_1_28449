package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.time.LocalDate;

// abstract class: we can not create a Person object, only Student, Teacher or Administrator
public abstract class Person implements Identifiable, Registrable {
    private String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    // protected so only the subclasses can call it with super(...)
    protected Person(String personId, String firstName, String lastName,
                     String email, String phoneNumber, LocalDate dateOfBirth) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    // every subclass gives its own version (overriding)
    public abstract String getRoleDescription();

    @Override
    public String getId() {
        return personId;
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    // isActive() is done in the subclasses

    @Override
    public void validate() throws ValidationException {
        Registrable.throwIfInvalid("Person",
                Identifiable.checkId(personId, "Person ID"),
                Registrable.checkName(firstName, "First name"),
                Registrable.checkName(lastName, "Last name"),
                Registrable.checkEmail(email, "Email"),
                Registrable.checkPhone(phoneNumber, "Phone"),
                Registrable.checkPastDate(dateOfBirth, "Date of birth"));
    }

    @Override
    public void printDetails() {
        System.out.println("+------------------------------------------------------------+");
        System.out.println("  " + getClass().getSimpleName().toUpperCase() + "  " + Identifiable.labelOf(this));
        System.out.println("  Role: " + getRoleDescription());
        System.out.println("+------------------------------------------------------------+");
        System.out.println("  PERSON");
        System.out.printf("   %-20s: %s%n", "Person ID", personId);
        System.out.printf("   %-20s: %s%n", "First name", firstName);
        System.out.printf("   %-20s: %s%n", "Last name", lastName);
        System.out.printf("   %-20s: %s%n", "Email", email);
        System.out.printf("   %-20s: %s%n", "Phone", phoneNumber);
        System.out.printf("   %-20s: %s%n", "Date of birth", Registrable.formatDate(dateOfBirth));
    }

    @Override
    public String getRegistrationSummary() {
        String typeCode = getClass().getSimpleName().substring(0, 3).toUpperCase();
        return Identifiable.buildReference(typeCode, personId) + " | " + getClass().getSimpleName()
                + " " + getDisplayName() + " | " + getRoleDescription()
                + " | " + (isActive() ? "ACTIVE" : "INACTIVE") + " | v" + ENTITY_VERSION;
    }

    @Override
    public String toString() {
        return "[" + personId + "] " + getDisplayName();
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
