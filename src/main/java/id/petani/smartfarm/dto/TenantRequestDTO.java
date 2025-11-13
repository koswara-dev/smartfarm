package id.petani.smartfarm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequestDTO {
    private String name;
    private String email;
    private String domain;
    private String subdomain;
    private String phoneNumber;
    private boolean isActive;
}
