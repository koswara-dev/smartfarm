package id.petani.smartfarm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionPlanRequestDTO {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Price monthly cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price monthly must be greater than 0")
    private BigDecimal priceMonthly;

    @NotNull(message = "Price yearly cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price yearly must be greater than 0")
    private BigDecimal priceYearly;

    @NotNull(message = "Max users cannot be null")
    @Min(value = 1, message = "Max users must be at least 1")
    private Integer maxUsers;

    @NotNull(message = "Max devices cannot be null")
    @Min(value = 1, message = "Max devices must be at least 1")
    private Integer maxDevices;

    private String description;
}
