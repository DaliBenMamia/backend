package coifure.coif.aiservice.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_user_insight_seeds")
public class UserInsightSeedEntity {

    @Id
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @ElementCollection
    @CollectionTable(name = "ai_seed_favorite_services", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "service_value", nullable = false, length = 255)
    private List<String> favoriteServices = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ai_seed_time_slots", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "slot_value", nullable = false, length = 255)
    private List<String> usualTimeSlots = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ai_seed_favorite_salons", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "salon_value", nullable = false, length = 255)
    private List<String> favoriteSalons = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ai_seed_recurring_preferences", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "preference_value", nullable = false, length = 255)
    private List<String> recurringPreferences = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getFavoriteServices() {
        return favoriteServices;
    }

    public void setFavoriteServices(List<String> favoriteServices) {
        this.favoriteServices = favoriteServices;
    }

    public List<String> getUsualTimeSlots() {
        return usualTimeSlots;
    }

    public void setUsualTimeSlots(List<String> usualTimeSlots) {
        this.usualTimeSlots = usualTimeSlots;
    }

    public List<String> getFavoriteSalons() {
        return favoriteSalons;
    }

    public void setFavoriteSalons(List<String> favoriteSalons) {
        this.favoriteSalons = favoriteSalons;
    }

    public List<String> getRecurringPreferences() {
        return recurringPreferences;
    }

    public void setRecurringPreferences(List<String> recurringPreferences) {
        this.recurringPreferences = recurringPreferences;
    }
}
