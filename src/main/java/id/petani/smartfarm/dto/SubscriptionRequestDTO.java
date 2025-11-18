package id.petani.smartfarm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SubscriptionRequestDTO {

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Is active status cannot be null")
    private Boolean isActive;

    @NotNull(message = "Billing cycle cannot be null")
    private String billingCycle;

    @NotNull(message = "Tenant ID cannot be null")
    private Long tenantId;

    @NotNull(message = "Subscription Plan ID cannot be null")
    private Long subscriptionPlanId;
}
