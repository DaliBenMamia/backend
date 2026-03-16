package coifure.coif.bookingservice.web;

import coifure.coif.bookingservice.domain.Booking;
import coifure.coif.bookingservice.domain.BookingStatus;
import coifure.coif.bookingservice.service.BookingService;
import coifure.coif.bookingservice.service.BookableServiceCatalog;
import coifure.coif.bookingservice.service.ExternalServicesClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingCommandController.class)
class BookingCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private BookableServiceCatalog bookableServiceCatalog;

    @MockBean
    private ExternalServicesClient externalServicesClient;

    @Test
    void createBookingAcceptsScreenshotPayload() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 12, 14, 0);
        Booking booking = new Booking(
                "booking-1",
                "1",
                "salon-2",
                "Coupe premium",
                "hairdresser-5",
                "service-2",
                dateTime,
                new BigDecimal("45.00"),
                "ONLINE",
                BookingStatus.CREATED
                ,
                "reservation-1"
        );

        when(externalServicesClient.validateToken(""))
                .thenReturn(new ExternalServicesClient.AuthValidation(true, null, null, null));
        when(bookableServiceCatalog.resolve(
                eq("service-2"),
                eq("salon-2"),
                eq("hairdresser-5"),
                eq("Coupe premium"),
                eq(new BigDecimal("45.00")),
                eq("ONLINE")
        )).thenReturn(new BookableServiceCatalog.ResolvedBookingDetails(
                "salon-2",
                "hairdresser-5",
                "Coupe premium",
                new BigDecimal("45.00"),
                "ONLINE"
        ));
        when(bookingService.createBooking(
                eq("1"),
                eq("salon-2"),
                eq("hairdresser-5"),
                eq("service-2"),
                eq("Coupe premium"),
                eq(dateTime),
                eq(new BigDecimal("45.00")),
                eq("ONLINE")
        ))
                .thenReturn(booking);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": "1",
                                  "salonId": "salon-2",
                                  "hairdresserId": "hairdresser-5",
                                  "serviceId": "service-2",
                                  "serviceName": "Coupe premium",
                                  "amount": 45.00,
                                  "paymentMethod": "ONLINE",
                                  "bookingDate": "2026-04-12T14:00:00"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value("booking-1"))
                .andExpect(jsonPath("$.userId").value("1"))
                .andExpect(jsonPath("$.salonId").value("salon-2"))
                .andExpect(jsonPath("$.serviceName").value("Coupe premium"))
                .andExpect(jsonPath("$.dateTime").value("2026-04-12T14:00:00"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.hairdresserId").value("hairdresser-5"))
                .andExpect(jsonPath("$.serviceId").value("service-2"))
                .andExpect(jsonPath("$.amount").value(45.00))
                .andExpect(jsonPath("$.paymentMethod").value("ONLINE"))
                .andExpect(jsonPath("$.reservationId").value("reservation-1"));
    }

    @Test
    void createBookingAcceptsMinimalPayloadWhenServiceCatalogContainsService() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 12, 14, 0);
        Booking booking = new Booking(
                "booking-2",
                "6",
                "salon-2",
                "Coupe premium",
                "hairdresser-5",
                "5",
                dateTime,
                new BigDecimal("45.00"),
                "ONLINE",
                BookingStatus.CREATED,
                "reservation-2"
        );

        when(externalServicesClient.validateToken(""))
                .thenReturn(new ExternalServicesClient.AuthValidation(true, null, null, null));
        when(bookableServiceCatalog.resolve(
                eq("5"),
                eq(null),
                eq(null),
                eq("5"),
                eq(null),
                eq(null)
        )).thenReturn(new BookableServiceCatalog.ResolvedBookingDetails(
                "salon-2",
                "hairdresser-5",
                "Coupe premium",
                new BigDecimal("45.00"),
                "ONLINE"
        ));
        when(bookingService.createBooking(
                eq("6"),
                eq("salon-2"),
                eq("hairdresser-5"),
                eq("5"),
                eq("Coupe premium"),
                eq(dateTime),
                eq(new BigDecimal("45.00")),
                eq("ONLINE")
        )).thenReturn(booking);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": "6",
                                  "serviceId": "5",
                                  "bookingDate": "2026-04-12T14:00:00"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value("booking-2"))
                .andExpect(jsonPath("$.userId").value("6"))
                .andExpect(jsonPath("$.salonId").value("salon-2"))
                .andExpect(jsonPath("$.hairdresserId").value("hairdresser-5"))
                .andExpect(jsonPath("$.serviceName").value("Coupe premium"))
                .andExpect(jsonPath("$.serviceId").value("5"))
                .andExpect(jsonPath("$.amount").value(45.00))
                .andExpect(jsonPath("$.paymentMethod").value("ONLINE"))
                .andExpect(jsonPath("$.reservationId").value("reservation-2"));
    }
}
