package coifure.coif.promotionsservice.web;

import coifure.coif.promotionsservice.service.PromotionService;
import coifure.coif.promotionsservice.web.dto.CreatePromotionRequest;
import coifure.coif.promotionsservice.web.dto.PromotionResponse;
import coifure.coif.promotionsservice.web.dto.UpdatePromotionStatusRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping
    public PromotionResponse create(@Valid @RequestBody CreatePromotionRequest request) {
        return PromotionResponse.from(promotionService.create(request));
    }

    @GetMapping
    public List<PromotionResponse> list(@RequestParam(required = false) String salonId) {
        return promotionService.findAll(salonId).stream()
                .map(PromotionResponse::from)
                .toList();
    }

    @GetMapping("/{promotionId}")
    public PromotionResponse getById(@PathVariable String promotionId) {
        return PromotionResponse.from(promotionService.findById(promotionId));
    }

    @PatchMapping("/{promotionId}/status")
    public PromotionResponse updateStatus(@PathVariable String promotionId,
                                          @Valid @RequestBody UpdatePromotionStatusRequest request) {
        return PromotionResponse.from(promotionService.updateStatus(promotionId, request.active()));
    }
}
