package edu.course.booking;

public class BookingManager {
    private final IPaymentGateway paymentGateway;
    private final INotificationService notificationService;
    private final IEventRepository eventRepository;

    public BookingManager(IPaymentGateway paymentGateway, INotificationService notificationService,
            IEventRepository eventRepository) {
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
        this.eventRepository = eventRepository;
    }

    public BookingResult processBooking(BookingRequest request) {
       // (M1-US2) Ahmad Emads validation
        validateRequest(request);



        if (eventRepository.isSoldOut(request.getEventId())) {
    throw new IllegalStateException("Event is sold out");
}

        int totalAmount = request.getTicketQuantity() * request.getPricePerTicketInCents();
        String transactionId;
        // maq (M1-US4) start
        try {
            transactionId = paymentGateway.processPayment(totalAmount);
        } catch (RuntimeException e) {
            throw new PaymentFailedException("Payment processing failed: " + e.getMessage());
        }
        // maq (M1-US4) end

        eventRepository.saveBooking(request);
        notificationService.sendConfirmation(request);

        return new BookingResult(true, transactionId);
    }

    private void validateRequest(BookingRequest request) {
    if (request.getCustomerId() == null || request.getCustomerId().isBlank() ||
        request.getEventId() == null || request.getEventId().isBlank() ||
        request.getTicketQuantity() < 1 || request.getTicketQuantity() > 5 ||
        request.getPricePerTicketInCents() <= 0) {
        throw new IllegalArgumentException("Invalid booking request");
    }
    }

}
