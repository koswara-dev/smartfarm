package id.petani.smartfarm.controller;

import id.petani.smartfarm.dto.ApiResponse;
import id.petani.smartfarm.dto.SubscriptionRequestDTO;
import id.petani.smartfarm.dto.SubscriptionResponseDTO;
import id.petani.smartfarm.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SubscriptionResponseDTO>>> getAllSubscriptions(
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) String billingCycle,
            @RequestParam(required = false) Long subscriptionPlanId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<SubscriptionResponseDTO> subscriptions = subscriptionService.getAllSubscriptions(status, billingCycle, subscriptionPlanId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Subscriptions retrieved successfully", subscriptions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionResponseDTO>> getSubscriptionById(@PathVariable Long id) {
        SubscriptionResponseDTO subscription = subscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Subscription retrieved successfully", subscription));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponseDTO>> createSubscription(@Valid @RequestBody SubscriptionRequestDTO subscriptionRequestDTO) {
        SubscriptionResponseDTO createdSubscription = subscriptionService.createSubscription(subscriptionRequestDTO);
        return new ResponseEntity<>(new ApiResponse<>(true, "Subscription created successfully", createdSubscription), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionResponseDTO>> updateSubscription(@PathVariable Long id, @Valid @RequestBody SubscriptionRequestDTO subscriptionRequestDTO) {
        SubscriptionResponseDTO updatedSubscription = subscriptionService.updateSubscription(id, subscriptionRequestDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Subscription updated successfully", updatedSubscription));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Subscription deleted successfully", null));
    }
}
