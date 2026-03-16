package coifure.coif.salonservice.web;

import coifure.coif.salonservice.domain.PromotionEntity;
import coifure.coif.salonservice.domain.ReviewEntity;
import coifure.coif.salonservice.domain.SalonEntity;
import coifure.coif.salonservice.domain.SalonRepository;
import coifure.coif.salonservice.domain.SalonServiceEntity;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/salons")
@Transactional
public class SalonController {

    private final SalonRepository salonRepository;

    public SalonController(SalonRepository salonRepository) {
        this.salonRepository = salonRepository;
    }

    @PostConstruct
    void seedDefaultSalon() {
        if (salonRepository.count() > 0) {
            return;
        }
        String salonId = UUID.randomUUID().toString();
        SalonEntity salon = new SalonEntity();
        salon.setSalonId(salonId);
        salon.setName("Coif Premium");
        salon.setLocation("Centre Ville");
        salon.setRating(4.6);
        salon.setBasePrice(20.0);
        salon.setActive(true);
        salon.addService(service(UUID.randomUUID().toString(), "Coupe Homme", 30, 20.0));
        salon.addService(service(UUID.randomUUID().toString(), "Brushing", 45, 30.0));
        salon.addPromotion(promotion(UUID.randomUUID().toString(), "10% sur brushing", 10.0, true));
        salon.addReview(review(UUID.randomUUID().toString(), "client-1", 5, "Excellent service"));
        salonRepository.save(salon);
    }

    @GetMapping
    public List<SalonSummary> search(@RequestParam(name = "name", required = false) String name,
                                     @RequestParam(name = "location", required = false) String location,
                                     @RequestParam(name = "minRating", required = false) Double minRating,
                                     @RequestParam(name = "maxPrice", required = false) Double maxPrice) {
        if (minRating != null && (minRating < 0 || minRating > 5)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minRating must be between 0 and 5");
        }
        if (maxPrice != null && maxPrice < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxPrice must be >= 0");
        }
        return salonRepository.findAll().stream()
                .filter(SalonEntity::isActive)
                .filter(s -> name == null || s.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(s -> location == null || s.getLocation().toLowerCase().contains(location.toLowerCase()))
                .filter(s -> minRating == null || s.getRating() >= minRating)
                .filter(s -> maxPrice == null || s.getBasePrice() <= maxPrice)
                .sorted(Comparator.comparing(SalonEntity::getRating).reversed())
                .map(this::toSummary)
                .toList();
    }

    @GetMapping("/{salonId}")
    public SalonDetails getDetails(@PathVariable("salonId") String salonId) {
        return toDetails(findSalon(salonId));
    }

    @PostMapping
    public SalonDetails createSalon(@RequestBody CreateSalonRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (request.location() == null || request.location().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "location is required");
        }
        if (request.basePrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "basePrice must be >= 0");
        }
        SalonEntity salon = new SalonEntity();
        salon.setSalonId(UUID.randomUUID().toString());
        salon.setName(request.name().trim());
        salon.setLocation(request.location().trim());
        salon.setRating(0.0);
        salon.setBasePrice(request.basePrice());
        salon.setActive(true);
        return toDetails(salonRepository.save(salon));
    }

    @PatchMapping("/{salonId}")
    public SalonDetails updateSalon(@PathVariable("salonId") String salonId, @RequestBody UpdateSalonRequest request) {
        SalonEntity current = findSalon(salonId);
        if (request != null && request.basePrice() != null && request.basePrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "basePrice must be >= 0");
        }
        if (request != null) {
            current.setName(request.name() == null ? current.getName() : request.name().trim());
            current.setLocation(request.location() == null ? current.getLocation() : request.location().trim());
            current.setBasePrice(request.basePrice() == null ? current.getBasePrice() : request.basePrice());
            current.setActive(request.active() == null ? current.isActive() : request.active());
        }
        return toDetails(salonRepository.save(current));
    }

    @DeleteMapping("/{salonId}")
    public Map<String, String> deleteSalon(@PathVariable("salonId") String salonId) {
        SalonEntity salon = findSalon(salonId);
        salonRepository.delete(salon);
        return Map.of("status", "DELETED", "salonId", salonId);
    }

    @PostMapping("/{salonId}/services")
    public SalonService addService(@PathVariable("salonId") String salonId, @RequestBody CreateServiceRequest request) {
        SalonEntity salon = findSalon(salonId);
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "service name is required");
        }
        if (request.durationMinutes() <= 0 || request.price() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "durationMinutes must be > 0 and price >= 0");
        }
        SalonServiceEntity service = service(UUID.randomUUID().toString(), request.name().trim(), request.durationMinutes(), request.price());
        salon.addService(service);
        salonRepository.save(salon);
        return new SalonService(service.getServiceId(), service.getName(), service.getDurationMinutes(), service.getPrice());
    }

    @PostMapping("/{salonId}/promotions")
    public Promotion addPromotion(@PathVariable("salonId") String salonId, @RequestBody CreatePromotionRequest request) {
        SalonEntity salon = findSalon(salonId);
        if (request == null || request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
        }
        if (request.discountPercent() <= 0 || request.discountPercent() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "discountPercent must be between 1 and 100");
        }
        PromotionEntity promotion = promotion(UUID.randomUUID().toString(), request.title().trim(), request.discountPercent(), true);
        salon.addPromotion(promotion);
        salonRepository.save(salon);
        return new Promotion(promotion.getPromotionId(), promotion.getTitle(), promotion.getDiscountPercent(), promotion.isActive());
    }

    @PostMapping("/{salonId}/reviews")
    public Review addReview(@PathVariable("salonId") String salonId, @RequestBody CreateReviewRequest request) {
        SalonEntity salon = findSalon(salonId);
        if (request == null || request.clientId() == null || request.clientId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clientId is required");
        }
        if (request.rating() < 1 || request.rating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rating must be between 1 and 5");
        }
        ReviewEntity review = review(UUID.randomUUID().toString(), request.clientId().trim(), request.rating(), request.comment());
        salon.addReview(review);
        double average = salon.getReviews().stream().mapToInt(ReviewEntity::getRating).average().orElse(0.0);
        salon.setRating(Math.round(average * 10.0) / 10.0);
        salonRepository.save(salon);
        return new Review(review.getReviewId(), review.getClientId(), review.getRating(), review.getComment());
    }

    @GetMapping("/admin/stats")
    public Map<String, Object> stats() {
        List<SalonEntity> salons = salonRepository.findAll();
        long activeSalons = salons.stream().filter(SalonEntity::isActive).count();
        long serviceCount = salons.stream().mapToLong(s -> s.getServices().size()).sum();
        long promotionCount = salons.stream().mapToLong(s -> s.getPromotions().size()).sum();

        return Map.of(
                "salonsTotal", salons.size(),
                "activeSalons", activeSalons,
                "servicesTotal", serviceCount,
                "promotionsTotal", promotionCount
        );
    }

    private SalonEntity findSalon(String salonId) {
        return salonRepository.findById(salonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salon not found"));
    }

    private SalonServiceEntity service(String serviceId, String name, int durationMinutes, double price) {
        SalonServiceEntity entity = new SalonServiceEntity();
        entity.setServiceId(serviceId);
        entity.setName(name);
        entity.setDurationMinutes(durationMinutes);
        entity.setPrice(price);
        return entity;
    }

    private PromotionEntity promotion(String promotionId, String title, double discountPercent, boolean active) {
        PromotionEntity entity = new PromotionEntity();
        entity.setPromotionId(promotionId);
        entity.setTitle(title);
        entity.setDiscountPercent(discountPercent);
        entity.setActive(active);
        return entity;
    }

    private ReviewEntity review(String reviewId, String clientId, int rating, String comment) {
        ReviewEntity entity = new ReviewEntity();
        entity.setReviewId(reviewId);
        entity.setClientId(clientId);
        entity.setRating(rating);
        entity.setComment(comment);
        return entity;
    }

    private SalonSummary toSummary(SalonEntity salon) {
        return new SalonSummary(salon.getSalonId(), salon.getName(), salon.getLocation(), salon.getRating(), salon.getBasePrice());
    }

    private SalonDetails toDetails(SalonEntity salon) {
        return new SalonDetails(
                salon.getSalonId(),
                salon.getName(),
                salon.getLocation(),
                salon.getRating(),
                salon.getBasePrice(),
                salon.isActive(),
                salon.getServices().stream()
                        .map(service -> new SalonService(service.getServiceId(), service.getName(), service.getDurationMinutes(), service.getPrice()))
                        .toList(),
                salon.getPromotions().stream()
                        .map(promotion -> new Promotion(promotion.getPromotionId(), promotion.getTitle(), promotion.getDiscountPercent(), promotion.isActive()))
                        .toList(),
                salon.getReviews().stream()
                        .map(review -> new Review(review.getReviewId(), review.getClientId(), review.getRating(), review.getComment()))
                        .toList()
        );
    }

    public record CreateSalonRequest(String name, String location, double basePrice) {}

    public record UpdateSalonRequest(String name, String location, Double basePrice, Boolean active) {}

    public record CreateServiceRequest(String name, int durationMinutes, double price) {}

    public record CreatePromotionRequest(String title, double discountPercent) {}

    public record CreateReviewRequest(String clientId, int rating, String comment) {}

    public record SalonSummary(String salonId, String name, String location, double rating, double basePrice) {}

    public record SalonDetails(
            String salonId,
            String name,
            String location,
            double rating,
            double basePrice,
            boolean active,
            List<SalonService> services,
            List<Promotion> promotions,
            List<Review> reviews
    ) {}

    public record SalonService(String serviceId, String name, int durationMinutes, double price) {}

    public record Promotion(String promotionId, String title, double discountPercent, boolean active) {}

    public record Review(String reviewId, String clientId, int rating, String comment) {}
}
