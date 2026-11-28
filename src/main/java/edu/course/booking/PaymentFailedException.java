package edu.course.booking;
// maq (M1-US4)
public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }
}