package id.petani.smartfarm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String domain;
    private String subdomain;
    private String phoneNumber;
    private boolean isActive;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
}
