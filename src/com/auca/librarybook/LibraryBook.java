package com.auca.librarybook;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class LibraryBook implements Identifiable, Registrable {
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public LibraryBook(String bookId, String isbn, String title, String author,
                       int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    @Override
    public String getId() {
        return bookId;
    }

    @Override
    public String getDisplayName() {
        return title + " by " + author;
    }

    @Override
    public boolean isActive() {
        return availableCopies > 0;
    }

    @Override
    public void validate() throws ValidationException {
        String stockRule = null;
        if (availableCopies > totalCopies) {
            stockRule = "Available copies (" + availableCopies + ") cannot exceed total copies (" + totalCopies + ").";
        }
        Registrable.throwIfInvalid("Library book",
                Identifiable.checkId(bookId, "Book ID"),
                Registrable.checkIsbn(isbn, "ISBN"),
                Registrable.checkText(title, "Title", 2, 120),
                Registrable.checkName(author, "Author"),
                Registrable.checkRange(totalCopies, "Total copies", 0, 1000),
                Registrable.checkRange(availableCopies, "Available copies", 0, 1000),
                stockRule);
    }

    @Override
    public void printDetails() {
        System.out.println("+------------------------------------------------------------+");
        System.out.println("  LIBRARY BOOK  " + Identifiable.labelOf(this));
        System.out.println("+------------------------------------------------------------+");
        System.out.printf("   %-20s: %s%n", "Book ID", bookId);
        System.out.printf("   %-20s: %s%n", "ISBN", isbn);
        System.out.printf("   %-20s: %s%n", "Title", title);
        System.out.printf("   %-20s: %s%n", "Author", author);
        System.out.printf("   %-20s: %d%n", "Total copies", totalCopies);
        System.out.printf("   %-20s: %d%n", "Available copies", availableCopies);
    }

    @Override
    public String getRegistrationSummary() {
        return Identifiable.buildReference("BK", bookId) + " | \"" + title + "\" by " + author + " | ISBN " + isbn
                + " | " + availableCopies + "/" + totalCopies + " available | v" + ENTITY_VERSION;
    }

    @Override
    public String toString() {
        return "LibraryBook [" + bookId + "] \"" + title + "\" by " + author + " | ISBN " + isbn + " | "
                + availableCopies + "/" + totalCopies + " available";
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
}
