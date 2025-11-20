package id.petani.smartfarm.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private Boolean isActive;
    private Long tenantId;
    private String tenantName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
