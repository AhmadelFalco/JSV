package edu.course.booking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingManagerTest {
//maq M1-US1 test (MajdAldeen)
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
        assertTrue(result.isSuccess());
        assertEquals("TXN-12345", result.getTransactionId());
    }




// belflaco test M1-US2 (Ahmad Emad)
    @ParameterizedTest
        @CsvSource(value = {
        "'', 'E1', 2, 2500",      // customerId empty
        "'   ', 'E1', 2, 2500",   // customerId white spaces
        "'C1', '', 2, 2500",      // eventId empty
        "'C1', '   ', 2, 2500",   // eventId white spaces
        "'C1', 'E1', 0, 2500",    // tickets less than 1
        "'C1', 'E1', 6, 2500",    // tickets more than 5
        "'C1', 'E1', 2, 0",       // the price zero
        "'C1', 'E1', 2, -100"     // the price is negative
    })
    void shouldRejectInvalidBookingRequest(String customerId, String eventId, int quantity, int price) {
        IPaymentGateway paymentGateway = mock(IPaymentGateway.class);
        INotificationService notificationService = mock(INotificationService.class);
        IEventRepository eventRepository = mock(IEventRepository.class);

        BookingRequest request = new BookingRequest(customerId, eventId, quantity, price);
        BookingManager manager = new BookingManager(paymentGateway, notificationService, eventRepository);

        assertThrows(IllegalArgumentException.class, () -> manager.processBooking(request));  // we used lamda expersion so that the function wrapped by and object and the framwork call the function when ever it wants (inside its try-catch logic) 

        verifyNoInteractions(eventRepository, paymentGateway, notificationService); // WCK THAT WE DIDNT CALL ANY service
    }
    @Test
void shouldRejectWhenCustomerIdOrEventIdIsNull() {
    IPaymentGateway paymentGateway = mock(IPaymentGateway.class);
    INotificationService notificationService = mock(INotificationService.class);
    IEventRepository eventRepository = mock(IEventRepository.class);
    BookingManager manager = new BookingManager(paymentGateway, notificationService, eventRepository);

    BookingRequest nullCustomer = new BookingRequest(null, "E1", 2, 2500);
    assertThrows(IllegalArgumentException.class, () -> manager.processBooking(nullCustomer));

    BookingRequest nullEvent = new BookingRequest("C1", null, 2, 2500);
    assertThrows(IllegalArgumentException.class, () -> manager.processBooking(nullEvent));

    verifyNoInteractions(eventRepository, paymentGateway, notificationService);
}



@Test
void shouldRejectBookingWhenEventIsSoldOut() {

    IPaymentGateway paymentGateway = mock(IPaymentGateway.class);
    INotificationService notificationService = mock(INotificationService.class);
    IEventRepository eventRepository = mock(IEventRepository.class);

    BookingRequest request = new BookingRequest("C1", "E1", 2, 2500);

    when(eventRepository.isSoldOut("E1")).thenReturn(true);

    BookingManager manager = new BookingManager(
            paymentGateway, notificationService, eventRepository);

    assertThrows(IllegalStateException.class,
            () -> manager.processBooking(request));

    verify(eventRepository, times(1)).isSoldOut("E1");

    verify(paymentGateway, never()).processPayment(anyInt());
    verify(eventRepository, never()).saveBooking(any());
    verify(notificationService, never()).sendConfirmation(any());
}


}