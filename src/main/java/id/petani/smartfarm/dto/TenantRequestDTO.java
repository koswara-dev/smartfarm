package id.petani.smartfarm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequestDTO {
    @NotBlank(message = "Name cannot be empty")
    @Pattern(
        regexp = "^[a-zA-Z0-9 .-_]{3,100}$",
        message = "Invalid characters in name"
    )
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Domain cannot be empty")
    private String domain;

    private String subdomain;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    private MultipartFile logo;

    @NotNull(message = "isActive cannot be null")
    private boolean isActive;
}
