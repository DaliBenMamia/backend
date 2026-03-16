package coifure.coif.aiservice.application;

import coifure.coif.aiservice.domain.UserInsightSeedEntity;
import coifure.coif.aiservice.domain.UserInsightSeedRepository;
import coifure.coif.aiservice.web.dto.PlanningOptimizationResponse;
import coifure.coif.aiservice.web.dto.PreferencesResponse;
import coifure.coif.aiservice.web.dto.RecommendationResponse;
import coifure.coif.aiservice.web.dto.TrendsResponse;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AiInsightService {
    private static final Map<String, UserInsightSeed> DEFAULT_USER_SEEDS = Map.of(
            "guest", new UserInsightSeed(
                    List.of("Coupe + barbe", "Brushing express"),
                    List.of("Fin de matinee", "Apres-midi"),
                    List.of("Coif Premium Tunis", "Urban Curl Studio"),
                    List.of("Prix abordables", "Reservation mobile", "Coiffeurs bien notes")
            ),
            "user-1", new UserInsightSeed(
                    List.of("Soin cheveux boucles", "Brushing protecteur"),
                    List.of("Samedi 10:00", "Vendredi 17:00"),
                    List.of("Curl House Tunis", "Beauty Corner Lac 2"),
                    List.of("Cheveux boucles", "Ambiance calme", "Paiement en ligne")
            ),
            "user-2", new UserInsightSeed(
                    List.of("Coupe homme premium", "Entretien barbe"),
                    List.of("Mardi 18:00", "Jeudi 19:00"),
                    List.of("Barber Elite Centre Ville", "Gentleman Cut Ariana"),
                    List.of("Rapidite", "Parking facile", "Historique reservable")
            )
    );

    private final UserInsightSeedRepository userInsightSeedRepository;

    public AiInsightService(UserInsightSeedRepository userInsightSeedRepository) {
        this.userInsightSeedRepository = userInsightSeedRepository;
    }

    @PostConstruct
    void initializeSeeds() {
        if (userInsightSeedRepository.count() > 0) {
            return;
        }
        DEFAULT_USER_SEEDS.forEach((userId, seed) -> {
            UserInsightSeedEntity entity = new UserInsightSeedEntity();
            entity.setUserId(userId);
            entity.setFavoriteServices(seed.favoriteServices());
            entity.setUsualTimeSlots(seed.usualTimeSlots());
            entity.setFavoriteSalons(seed.favoriteSalons());
            entity.setRecurringPreferences(seed.recurringPreferences());
            userInsightSeedRepository.save(entity);
        });
    }

    public RecommendationResponse buildRecommendations(String userId) {
        UserInsightSeed seed = seedFor(userId);
        return new RecommendationResponse(
                userId,
                List.of(
                        recommendation("service-101", seed.favoriteServices().getFirst(), "Fortement reserve par des clients au profil similaire."),
                        recommendation("service-102", "Coloration soin profond", "Bon complement a vos habitudes de visite.")
                ),
                List.of(
                        recommendation("salon-201", seed.favoriteSalons().getFirst(), "Bien note et adapte a vos preferences de prix et de localisation."),
                        recommendation("salon-202", "Studio Capillaire Menzah", "Disponibilites regulieres en fin de journee.")
                ),
                List.of(
                        recommendation("hairdresser-301", "Sami Ben Youssef", "Apprecie pour les soins personnalises."),
                        recommendation("hairdresser-302", "Ines Trabelsi", "Bonne regularite sur les rendez-vous du week-end.")
                )
        );
    }

    public PreferencesResponse buildPreferences(String userId) {
        UserInsightSeed seed = seedFor(userId);
        return new PreferencesResponse(
                userId,
                seed.favoriteServices(),
                seed.usualTimeSlots(),
                seed.favoriteSalons(),
                seed.recurringPreferences()
        );
    }

    public TrendsResponse buildTrends() {
        return new TrendsResponse(
                List.of("Brushing express", "Soin cheveux boucles", "Coupe homme degrade"),
                List.of("Coif Premium Tunis", "Beauty Corner Lac 2", "Barber Elite Centre Ville"),
                List.of("12:00-14:00", "17:00-19:00", "Samedi 09:00-12:00"),
                List.of(
                        "Hausse des reservations rapides sur mobile.",
                        "Le paiement en ligne progresse sur les rendez-vous premium.",
                        "Les soins personnalises pour cheveux boucles gagnent en popularite."
                )
        );
    }

    public PlanningOptimizationResponse buildPlanningOptimization() {
        return new PlanningOptimizationResponse(
                List.of("Vendredi 17:00-19:00", "Samedi 10:00-13:00"),
                List.of("Lundi 09:00-11:00", "Mercredi 14:00-16:00"),
                List.of(
                        "Ouvrir davantage de creneaux courts sur la pause de midi.",
                        "Proposer des promotions sur les plages sous-utilisees en debut de semaine.",
                        "Affecter les coiffeurs specialises cheveux boucles aux pics du week-end."
                )
        );
    }

    private RecommendationResponse.RecommendationItem recommendation(String id, String name, String reason) {
        return new RecommendationResponse.RecommendationItem(id, name, reason);
    }

    private UserInsightSeed seedFor(String userId) {
        if (userId == null || userId.isBlank()) {
            return DEFAULT_USER_SEEDS.get("guest");
        }
        return userInsightSeedRepository.findById(userId.toLowerCase(Locale.ROOT))
                .map(entity -> new UserInsightSeed(
                        entity.getFavoriteServices(),
                        entity.getUsualTimeSlots(),
                        entity.getFavoriteSalons(),
                        entity.getRecurringPreferences()
                ))
                .orElse(DEFAULT_USER_SEEDS.get("guest"));
    }

    private record UserInsightSeed(
            List<String> favoriteServices,
            List<String> usualTimeSlots,
            List<String> favoriteSalons,
            List<String> recurringPreferences
    ) {
    }
}
