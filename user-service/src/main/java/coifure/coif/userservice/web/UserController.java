package coifure.coif.userservice.web;

import coifure.coif.userservice.service.UserProfileService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserProfileService userProfileService;

    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody CreateUserRequest request) {
        userProfileService.create(request);
    }

    @GetMapping("/{userId}")
    public UserProfile getById(@PathVariable("userId") String userId) {
        return userProfileService.getById(userId);
    }

    @GetMapping
    public List<UserProfile> list(@RequestParam(name = "role", required = false) String role,
                                  @RequestParam(name = "status", required = false) String status) {
        return userProfileService.list(role, status);
    }

    @PatchMapping("/{userId}/status")
    public UserProfile changeStatus(@PathVariable("userId") String userId, @RequestBody StatusRequest request) {
        return userProfileService.changeStatus(userId, request);
    }

    @PatchMapping("/{userId}/profile")
    public UserProfile updateProfile(@PathVariable("userId") String userId, @RequestBody UpdateProfileRequest request) {
        return userProfileService.updateProfile(userId, request);
    }

    @PutMapping("/{userId}/availabilities")
    public UserProfile setAvailabilities(@PathVariable("userId") String userId, @RequestBody List<AvailabilitySlot> slots) {
        return userProfileService.setAvailabilities(userId, slots);
    }

    @GetMapping("/hairdressers/{hairdresserId}/agenda")
    public Map<String, Object> agenda(@PathVariable("hairdresserId") String hairdresserId) {
        UserProfile hairdresser = userProfileService.getById(hairdresserId);
        return Map.of(
                "hairdresserId", hairdresserId,
                "availabilities", hairdresser.availabilities()
        );
    }
}



