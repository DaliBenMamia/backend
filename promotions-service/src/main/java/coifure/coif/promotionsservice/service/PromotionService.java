package coifure.coif.promotionsservice.service;

import coifure.coif.promotionsservice.domain.Promotion;
import coifure.coif.promotionsservice.integration.events.PromotionEventPayload;
import coifure.coif.promotionsservice.integration.events.PromotionEventsPublisher;
import coifure.coif.promotionsservice.repository.PromotionRepository;
import coifure.coif.promotionsservice.web.dto.CreatePromotionRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionEventsPublisher eventsPublisher;

    public PromotionService(PromotionRepository promotionRepository,
                            PromotionEventsPublisher eventsPublisher) {
        this.promotionRepository = promotionRepository;
        this.eventsPublisher = eventsPublisher;
    }

    @Transactional
    public Promotion create(CreatePromotionRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate must be after or equal to startDate");
        }
        Promotion promotion = new Promotion();
        promotion.setSalonId(request.salonId().trim());
        promotion.setTitle(request.title().trim());
        promotion.setDescription(request.description());
        promotion.setDiscountPercent(request.discountPercent());
        promotion.setStartDate(request.startDate());
        promotion.setEndDate(request.endDate());
        promotion.setActive(true);
        Promotion saved = promotionRepository.save(promotion);
        eventsPublisher.promotionCreated(toEvent(saved));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Promotion> findAll(String salonId) {
        if (salonId == null || salonId.isBlank()) {
            return promotionRepository.findAll(Sort.by(Sort.Direction.ASC, "startDate"));
        }
        return promotionRepository.findBySalonIdOrderByStartDateAsc(salonId.trim());
    }

    @Transactional(readOnly = true)
    public Promotion findById(String promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion not found"));
    }

    @Transactional
    public Promotion updateStatus(String promotionId, boolean active) {
        Promotion promotion = findById(promotionId);
        promotion.setActive(active);
        Promotion saved = promotionRepository.save(promotion);
        eventsPublisher.promotionStatusUpdated(toEvent(saved));
        return saved;
    }

    private PromotionEventPayload toEvent(Promotion promotion) {
        return new PromotionEventPayload(
                promotion.getId(),
                promotion.getSalonId(),
                promotion.getTitle(),
                promotion.getDiscountPercent(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.isActive()
        );
    }
}
