package id.petani.smartfarm.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SubscriptionResponseDTO {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String billingCycle;
    private Long tenantId;
    private String tenantName;
    private Long subscriptionPlanId;
    private String subscriptionPlanName;
    private Double priceMonthly;
    private Double priceYearly;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
