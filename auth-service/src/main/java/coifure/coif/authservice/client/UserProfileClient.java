package coifure.coif.authservice.client;

import coifure.coif.authservice.client.dto.CreateUserProfileRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserProfileClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserProfileClient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${app.user-service-url:http://localhost:8893}") String userServiceUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.userServiceUrl = userServiceUrl;
    }

    public void createUserProfile(CreateUserProfileRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateUserProfileRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(
                userServiceUrl + "/api/users",
                entity,
                Void.class
        );
    }
}
