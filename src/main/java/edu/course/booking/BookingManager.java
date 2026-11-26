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
       // (M1-US2) Ahmad Emads if statement
        if (request.getCustomerId() == null || request.getCustomerId().isBlank() ||
            request.getEventId() == null || request.getEventId().isBlank() ||
            request.getTicketQuantity() < 1 || request.getTicketQuantity() > 5 ||
            request.getPricePerTicketInCents() <= 0) {
            throw new IllegalArgumentException("Invalid booking request");
        }



        eventRepository.isSoldOut(request.getEventId());

        int totalAmount = request.getTicketQuantity() * request.getPricePerTicketInCents();
        String transactionId = paymentGateway.processPayment(totalAmount);

        eventRepository.saveBooking(request);
        notificationService.sendConfirmation(request);

        return new BookingResult(true, transactionId);
    }
}