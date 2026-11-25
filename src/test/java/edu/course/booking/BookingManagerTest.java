package edu.course.booking;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class BookingManagerTest {

    @Test
    void shouldProcessSuccessfulBooking() {
        IPaymentGateway paymentGateway = mock(IPaymentGateway.class);
        INotificationService notificationService = mock(INotificationService.class);
        IEventRepository eventRepository = mock(IEventRepository.class);

        BookingRequest request = new BookingRequest("C1", "E1", 2, 2500);

        when(eventRepository.isSoldOut("E1")).thenReturn(false);
        when(paymentGateway.processPayment(5000)).thenReturn("TXN-12345");

        BookingManager manager = new BookingManager(paymentGateway, notificationService, eventRepository);
        BookingResult result = manager.processBooking(request);

        verify(eventRepository, times(1)).isSoldOut("E1");
        verify(paymentGateway, times(1)).processPayment(5000);
        verify(eventRepository, times(1)).saveBooking(any());
        verify(notificationService, times(1)).sendConfirmation(any());
    }
}