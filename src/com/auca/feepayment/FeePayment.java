package com.auca.feepayment;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeePayment implements Identifiable, Registrable {
    private String paymentId;
    private final Student student;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String receiptNumber;

    public FeePayment(String paymentId, Student student, BigDecimal amount, LocalDate paymentDate,
                      String paymentMethod, String receiptNumber) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }

    @Override
    public String getId() {
        return paymentId;
    }

    @Override
    public String getDisplayName() {
        return "Receipt " + receiptNumber + " - " + Registrable.formatMoney(amount);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void validate() throws ValidationException {
        // receipt number uses the same rule as the ids
        Registrable.throwIfInvalid("Fee payment",
                Identifiable.checkId(paymentId, "Payment ID"),
                Registrable.checkRequired(student, "Student"),
                Registrable.checkPositiveAmount(amount, "Amount"),
                Registrable.checkNotFuture(paymentDate, "Payment date"),
                Registrable.checkAllowed(paymentMethod, "Payment method", Registrable.allowedValues("paymentMethod")),
                Identifiable.checkId(receiptNumber, "Receipt number"));
    }

    @Override
    public void printDetails() {
        System.out.println("+------------------------------------------------------------+");
        System.out.println("  FEE PAYMENT  " + Identifiable.labelOf(this));
        System.out.println("+------------------------------------------------------------+");
        System.out.printf("   %-20s: %s%n", "Payment ID", paymentId);
        System.out.printf("   %-20s: %s%n", "Student", Identifiable.labelOf(student));
        System.out.printf("   %-20s: %s%n", "Amount", Registrable.formatMoney(amount));
        System.out.printf("   %-20s: %s%n", "Payment date", Registrable.formatDate(paymentDate));
        System.out.printf("   %-20s: %s%n", "Payment method", paymentMethod);
        System.out.printf("   %-20s: %s%n", "Receipt number", receiptNumber);
    }

    @Override
    public String getRegistrationSummary() {
        return Identifiable.buildReference("PAY", paymentId) + " | " + Registrable.formatMoney(amount) + " paid by "
                + (student == null ? "?" : student.getDisplayName()) + " via " + paymentMethod
                + " | receipt " + receiptNumber + " | v" + ENTITY_VERSION;
    }

    @Override
    public String toString() {
        return "FeePayment [" + paymentId + "] " + (student == null ? "?" : student.getDisplayName()) + " | "
                + Registrable.formatMoney(amount) + " | " + Registrable.formatDate(paymentDate) + " | "
                + paymentMethod + " | receipt " + receiptNumber;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Student getStudent() {
        return student;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
}
