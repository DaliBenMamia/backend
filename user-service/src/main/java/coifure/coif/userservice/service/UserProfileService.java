package coifure.coif.userservice.service;

import coifure.coif.userservice.domain.AvailabilitySlotEntity;
import coifure.coif.userservice.domain.UserProfileEntity;
import coifure.coif.userservice.domain.UserProfileRepository;
import coifure.coif.userservice.web.AvailabilitySlot;
import coifure.coif.userservice.web.CreateUserRequest;
import coifure.coif.userservice.web.StatusRequest;
import coifure.coif.userservice.web.UpdateProfileRequest;
import coifure.coif.userservice.web.UserProfile;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public void create(CreateUserRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body is required");
        }
        if (request.email() == null || request.email().isBlank() || !request.email().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "valid email is required");
        }
        String userId = request.userId() == null || request.userId().isBlank()
                ? UUID.randomUUID().toString()
                : request.userId().trim();
        if (userProfileRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        if (userProfileRepository.existsByEmailIgnoreCase(request.email().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        UserProfileEntity profile = new UserProfileEntity();
        profile.setUserId(userId);
        profile.setFullName(normalizedText(request.fullName()));
        profile.setEmail(request.email().trim());
        profile.setRole(normalizedRole(request.role()));
        profile.setStatus("ACTIVE");
        profile.setSpeciality(normalizedText(request.speciality()));
        profile.setPhone(normalizedText(request.phone()));
        userProfileRepository.save(profile);
    }

    public UserProfile getById(String userId) {
        return userProfileRepository.findById(userId)
                .map(this::toUserProfile)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<UserProfile> list(String role, String status) {
        if (role != null && status != null) {
            return userProfileRepository.findAllByRoleIgnoreCaseAndStatusIgnoreCase(role, status).stream()
                    .map(this::toUserProfile)
                    .toList();
        }
        if (role != null) {
            return userProfileRepository.findAllByRoleIgnoreCase(role).stream()
                    .map(this::toUserProfile)
                    .toList();
        }
        if (status != null) {
            return userProfileRepository.findAllByStatusIgnoreCase(status).stream()
                    .map(this::toUserProfile)
                    .toList();
        }
        return userProfileRepository.findAll().stream()
                .map(this::toUserProfile)
                .toList();
    }

    public UserProfile changeStatus(String userId, StatusRequest request) {
        UserProfileEntity current = findUser(userId);
        if (request == null || request.status() == null || request.status().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }
        String nextStatus = request.status().toUpperCase(Locale.ROOT);
        if (!List.of("ACTIVE", "SUSPENDED").contains(nextStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be ACTIVE or SUSPENDED");
        }
        current.setStatus(nextStatus);
        return toUserProfile(userProfileRepository.save(current));
    }

    public UserProfile updateProfile(String userId, UpdateProfileRequest request) {
        UserProfileEntity current = findUser(userId);
        if (request != null && request.email() != null && !request.email().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email must be valid");
        }
        if (request != null) {
            if (request.email() != null
                    && !request.email().equalsIgnoreCase(current.getEmail())
                    && userProfileRepository.existsByEmailIgnoreCase(request.email().trim())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
            }
            current.setFullName(request.fullName() == null ? current.getFullName() : normalizedText(request.fullName()));
            current.setEmail(request.email() == null ? current.getEmail() : request.email().trim());
            current.setSpeciality(request.speciality() == null ? current.getSpeciality() : normalizedText(request.speciality()));
            current.setPhone(request.phone() == null ? current.getPhone() : normalizedText(request.phone()));
        }
        return toUserProfile(userProfileRepository.save(current));
    }

    public UserProfile setAvailabilities(String userId, List<AvailabilitySlot> slots) {
        UserProfileEntity current = findUser(userId);
        current.setAvailabilities((slots == null ? List.<AvailabilitySlot>of() : slots).stream()
                .map(this::toAvailabilityEntity)
                .toList());
        return toUserProfile(userProfileRepository.save(current));
    }

    private UserProfileEntity findUser(String userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private String normalizedRole(String role) {
        if (role == null || role.isBlank()) {
            return "CLIENT";
        }
        return role.toUpperCase(Locale.ROOT);
    }

    private String normalizedText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private AvailabilitySlotEntity toAvailabilityEntity(AvailabilitySlot slot) {
        AvailabilitySlotEntity entity = new AvailabilitySlotEntity();
        entity.setDay(normalizedText(slot.day()));
        entity.setStartHour(normalizedText(slot.startHour()));
        entity.setEndHour(normalizedText(slot.endHour()));
        return entity;
    }

    private UserProfile toUserProfile(UserProfileEntity entity) {
        return new UserProfile(
                entity.getUserId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getRole(),
                entity.getStatus(),
                entity.getSpeciality(),
                entity.getPhone(),
                entity.getAvailabilities().stream()
                        .map(slot -> new AvailabilitySlot(slot.getDay(), slot.getStartHour(), slot.getEndHour()))
                        .toList()
        );
    }
}
