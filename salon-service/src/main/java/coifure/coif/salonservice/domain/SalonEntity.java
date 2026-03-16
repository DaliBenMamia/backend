package coifure.coif.salonservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salons")
public class SalonEntity {

    @Id
    @Column(name = "salon_id", nullable = false, length = 64)
    private String salonId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "rating", nullable = false)
    private double rating;

    @Column(name = "base_price", nullable = false)
    private double basePrice;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<SalonServiceEntity> services = new ArrayList<>();

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<PromotionEntity> promotions = new ArrayList<>();

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<ReviewEntity> reviews = new ArrayList<>();

    public String getSalonId() {
        return salonId;
    }

    public void setSalonId(String salonId) {
        this.salonId = salonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<SalonServiceEntity> getServices() {
        return services;
    }

    public void addService(SalonServiceEntity service) {
        service.setSalon(this);
        services.add(service);
    }

    public List<PromotionEntity> getPromotions() {
        return promotions;
    }

    public void addPromotion(PromotionEntity promotion) {
        promotion.setSalon(this);
        promotions.add(promotion);
    }

    public List<ReviewEntity> getReviews() {
        return reviews;
    }

    public void addReview(ReviewEntity review) {
        review.setSalon(this);
        reviews.add(review);
    }
}
