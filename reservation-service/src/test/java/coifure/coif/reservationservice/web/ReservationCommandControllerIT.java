package coifure.coif.reservationservice.web;

import coifure.coif.reservationservice.persistence.ReservationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationCommandControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
    }

    @Test
    void shouldRejectInvalidStatusTransitionFromCompletedToAccepted() throws Exception {
        String reservationId = createReservation("u-1", "h-1", "Cut", LocalDateTime.now().plusHours(3));
        mockMvc.perform(patch("/api/reservations/{reservationId}/accept", reservationId))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/api/reservations/{reservationId}/complete", reservationId))
                .andExpect(status().isOk());

        MvcResult invalidTransition = mockMvc.perform(patch("/api/reservations/{reservationId}/accept", reservationId))
                .andExpect(status().isConflict())
                .andReturn();

        String body = invalidTransition.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(body).contains("Invalid status transition");
    }

    @Test
    void shouldRejectPastDateWhenCreatingReservation() throws Exception {
        String payload = """
                {
                  "userId": "u-1",
                  "salonId": "s-1",
                  "hairdresserId": "h-1",
                  "serviceName": "Cut",
                  "dateTime": "%s"
                }
                """.formatted(LocalDateTime.now().minusMinutes(5));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
                        .contains("dateTime must be in the future"));
    }

    @Test
    void shouldFilterAndSortReservations() throws Exception {
        createReservation("u-1", "h-1", "Style", LocalDateTime.now().plusHours(6));
        createReservation("u-1", "h-1", "Cut", LocalDateTime.now().plusHours(2));
        createReservation("u-2", "h-2", "Color", LocalDateTime.now().plusHours(4));

        MvcResult result = mockMvc.perform(get("/api/reservations")
                        .queryParam("userId", "u-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertThat(root).hasSize(2);
        assertThat(root.get(0).get("serviceName").asText()).isEqualTo("Cut");
        assertThat(root.get(1).get("serviceName").asText()).isEqualTo("Style");
    }

    @Test
    void shouldValidateReasonLengthOnCancel() throws Exception {
        String reservationId = createReservation("u-1", "h-1", "Cut", LocalDateTime.now().plusHours(1));
        String payload = """
                {
                  "reason": "%s"
                }
                """.formatted("x".repeat(256));

        mockMvc.perform(patch("/api/reservations/{reservationId}/cancel", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldExplainMalformedDateFormat() throws Exception {
        String payload = """
                {
                  "userId": "u-1",
                  "salonId": "s-1",
                  "hairdresserId": "h-1",
                  "serviceName": "Cut",
                  "dateTime": "15/03/2026 14:30"
                }
                """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
                        .contains("Malformed JSON request"));
    }

    private String createReservation(String userId, String hairdresserId, String serviceName, LocalDateTime dateTime) throws Exception {
        String payload = """
                {
                  "userId": "%s",
                  "salonId": "s-1",
                  "hairdresserId": "%s",
                  "serviceName": "%s",
                  "dateTime": "%s"
                }
                """.formatted(userId, hairdresserId, serviceName, dateTime);

        MvcResult result = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        return jsonNode.get("reservationId").asText();
    }
}
