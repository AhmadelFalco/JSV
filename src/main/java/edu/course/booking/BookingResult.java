package edu.course.booking;

public class BookingResult {
    private boolean success;
    private String transactionId;

    public BookingResult(boolean success, String transactionId) {
        this.success = success;
        this.transactionId = transactionId;
    }

    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
}