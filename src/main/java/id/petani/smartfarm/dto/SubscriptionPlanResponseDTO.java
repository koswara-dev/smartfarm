package id.petani.smartfarm.dto;

import lombok.Data;
import id.petani.smartfarm.model.SubscriptionPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SubscriptionPlanResponseDTO {
    private Long id;
    private String name;
    private BigDecimal priceMonthly;
    private BigDecimal priceYearly;
    private Integer maxUsers;
    private Integer maxDevices;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SubscriptionPlanResponseDTO fromEntity(SubscriptionPlan subscriptionPlan) {
        SubscriptionPlanResponseDTO dto = new SubscriptionPlanResponseDTO();
        dto.setId(subscriptionPlan.getId());
        dto.setName(subscriptionPlan.getName());
        dto.setPriceMonthly(subscriptionPlan.getPriceMonthly());
        dto.setPriceYearly(subscriptionPlan.getPriceYearly());
        dto.setMaxUsers(subscriptionPlan.getMaxUsers());
        dto.setMaxDevices(subscriptionPlan.getMaxDevices());
        dto.setDescription(subscriptionPlan.getDescription());
        dto.setCreatedAt(subscriptionPlan.getCreatedAt());
        dto.setUpdatedAt(subscriptionPlan.getUpdatedAt());
        return dto;
    }
}
