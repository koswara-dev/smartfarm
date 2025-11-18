package id.petani.smartfarm.controller;

import id.petani.smartfarm.dto.ApiResponse;
import id.petani.smartfarm.dto.SubscriptionPlanRequestDTO;
import id.petani.smartfarm.dto.SubscriptionPlanResponseDTO;
import id.petani.smartfarm.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscription-plans")
public class SubscriptionPlanController {

    @Autowired
    private SubscriptionPlanService subscriptionPlanService;

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionPlanResponseDTO>> createSubscriptionPlan(@Valid @RequestBody SubscriptionPlanRequestDTO requestDTO) {
        SubscriptionPlanResponseDTO createdPlan = subscriptionPlanService.createSubscriptionPlan(requestDTO);
        return new ResponseEntity<>(new ApiResponse<>(true, "Subscription plan created successfully", createdPlan), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponseDTO>>> getAllSubscriptionPlans() {
        List<SubscriptionPlanResponseDTO> plans = subscriptionPlanService.getAllSubscriptionPlans();
        return ResponseEntity.ok(new ApiResponse<>(true, "Subscription plans retrieved successfully", plans));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponseDTO>> getSubscriptionPlanById(@PathVariable Long id) {
        SubscriptionPlanResponseDTO plan = subscriptionPlanService.getSubscriptionPlanById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Subscription plan retrieved successfully", plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponseDTO>> updateSubscriptionPlan(@PathVariable Long id, @Valid @RequestBody SubscriptionPlanRequestDTO requestDTO) {
        SubscriptionPlanResponseDTO updatedPlan = subscriptionPlanService.updateSubscriptionPlan(id, requestDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Subscription plan updated successfully", updatedPlan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubscriptionPlan(@PathVariable Long id) {
        subscriptionPlanService.deleteSubscriptionPlan(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Subscription plan deleted successfully", null));
    }
}
