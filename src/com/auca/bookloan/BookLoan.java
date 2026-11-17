package com.auca.bookloan;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;

import java.time.LocalDate;

public class BookLoan implements Identifiable, Registrable {
    private String loanId;
    private final Student student;
    private final LibraryBook book;
    private final LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;

    public BookLoan(String loanId, Student student, LibraryBook book, LocalDate borrowedDate,
                    LocalDate dueDate, LocalDate returnedDate) {
        this.loanId = loanId;
        this.student = student;
        this.book = book;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
    }

    // new loan, not returned yet
    public BookLoan(String loanId, Student student, LibraryBook book, LocalDate borrowedDate, LocalDate dueDate) {
        this(loanId, student, book, borrowedDate, dueDate, null);
    }

    @Override
    public String getId() {
        return loanId;
    }

    @Override
    public String getDisplayName() {
        String who = student == null ? "?" : student.getDisplayName();
        String what = book == null ? "?" : book.getTitle();
        return what + " -> " + who;
    }

    @Override
    public boolean isActive() {
        return returnedDate == null;
    }

    @Override
    public void validate() throws ValidationException {
        String dueOrder = null;
        if (dueDate != null && borrowedDate != null && !dueDate.isAfter(borrowedDate)) {
            dueOrder = "Due date must be after the borrowed date (" + Registrable.formatDate(borrowedDate) + ").";
        }
        String returnRule = null;
        if (returnedDate != null) {
            if (borrowedDate != null && returnedDate.isBefore(borrowedDate)) {
                returnRule = "Returned date cannot be before the borrowed date (" + Registrable.formatDate(borrowedDate) + ").";
            } else {
                returnRule = Registrable.checkNotFuture(returnedDate, "Returned date");
            }
        }
        Registrable.throwIfInvalid("Book loan",
                Identifiable.checkId(loanId, "Loan ID"),
                Registrable.checkRequired(student, "Student"),
                Registrable.checkRequired(book, "Library book"),
                Registrable.checkNotFuture(borrowedDate, "Borrowed date"),
                Registrable.checkRequired(dueDate, "Due date"),
                dueOrder,
                returnRule);
    }

    @Override
    public void printDetails() {
        System.out.println("+------------------------------------------------------------+");
        System.out.println("  BOOK LOAN  " + Identifiable.labelOf(this));
        System.out.println("+------------------------------------------------------------+");
        System.out.printf("   %-20s: %s%n", "Loan ID", loanId);
        System.out.printf("   %-20s: %s%n", "Student", Identifiable.labelOf(student));
        System.out.printf("   %-20s: %s%n", "Book", Identifiable.labelOf(book));
        System.out.printf("   %-20s: %s%n", "Borrowed date", Registrable.formatDate(borrowedDate));
        System.out.printf("   %-20s: %s%n", "Due date", Registrable.formatDate(dueDate));
        System.out.printf("   %-20s: %s%n", "Returned date",
                returnedDate == null ? "(not returned yet)" : Registrable.formatDate(returnedDate));
    }

    @Override
    public String getRegistrationSummary() {
        return Identifiable.buildReference("LN", loanId) + " | \"" + (book == null ? "?" : book.getTitle())
                + "\" borrowed by " + (student == null ? "?" : student.getDisplayName())
                + " | due " + Registrable.formatDate(dueDate) + " | " + (isActive() ? "OPEN" : "RETURNED")
                + " | v" + ENTITY_VERSION;
    }

    @Override
    public String toString() {
        return "BookLoan [" + loanId + "] \"" + (book == null ? "?" : book.getTitle()) + "\" -> "
                + (student == null ? "?" : student.getDisplayName()) + " | borrowed "
                + Registrable.formatDate(borrowedDate) + " | due " + Registrable.formatDate(dueDate) + " | "
                + (returnedDate == null ? "NOT RETURNED" : "returned " + Registrable.formatDate(returnedDate));
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public Student getStudent() {
        return student;
    }

    public LibraryBook getBook() {
        return book;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }
}
