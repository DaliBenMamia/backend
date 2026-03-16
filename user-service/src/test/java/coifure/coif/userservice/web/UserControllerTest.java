package coifure.coif.userservice.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createUserAcceptsExpectedPayload() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "user-123",
                                  "email": "user@example.com",
                                  "role": "CLIENT"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/users/user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("CLIENT"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createUserAcceptsProfilePayloadWithoutUserId() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Hamadi Test",
                                  "email": "hamadi@test.com",
                                  "role": "CLIENT",
                                  "speciality": "coupe",
                                  "phone": "12345578"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/users")
                        .param("role", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.email=='hamadi@test.com')].fullName").value("Hamadi Test"))
                .andExpect(jsonPath("$[?(@.email=='hamadi@test.com')].speciality").value("coupe"))
                .andExpect(jsonPath("$[?(@.email=='hamadi@test.com')].phone").value("12345578"));
    }
}
