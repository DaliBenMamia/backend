package coifure.coif.promotionsservice.repository;

import coifure.coif.promotionsservice.domain.Promotion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PromotionRepositoryTest {

    @Autowired
    private PromotionRepository promotionRepository;

    @Test
    void saveGeneratesIdAndTimestamps() {
        Promotion promotion = new Promotion();
        promotion.setSalonId("salon-1");
        promotion.setTitle("Spring Offer");
        promotion.setDescription("15 percent off");
        promotion.setDiscountPercent(BigDecimal.valueOf(15));
        promotion.setStartDate(LocalDate.of(2026, 3, 16));
        promotion.setEndDate(LocalDate.of(2026, 3, 20));
        promotion.setActive(true);

        Promotion saved = promotionRepository.saveAndFlush(promotion);

        assertThat(saved.getId()).isNotBlank();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void findBySalonIdOrderByStartDateAscReturnsSortedPromotionsForSalon() {
        promotionRepository.save(promotion("salon-1", "Late Offer", LocalDate.of(2026, 3, 20)));
        promotionRepository.save(promotion("salon-2", "Other Salon", LocalDate.of(2026, 3, 10)));
        promotionRepository.save(promotion("salon-1", "Early Offer", LocalDate.of(2026, 3, 12)));

        List<Promotion> promotions = promotionRepository.findBySalonIdOrderByStartDateAsc("salon-1");

        assertThat(promotions).hasSize(2);
        assertThat(promotions)
                .extracting(Promotion::getTitle)
                .containsExactly("Early Offer", "Late Offer");
    }

    private Promotion promotion(String salonId, String title, LocalDate startDate) {
        Promotion promotion = new Promotion();
        promotion.setSalonId(salonId);
        promotion.setTitle(title);
        promotion.setDescription(title + " description");
        promotion.setDiscountPercent(BigDecimal.valueOf(20));
        promotion.setStartDate(startDate);
        promotion.setEndDate(startDate.plusDays(5));
        promotion.setActive(true);
        return promotion;
    }
}
