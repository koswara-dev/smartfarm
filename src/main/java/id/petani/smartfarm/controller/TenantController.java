package id.petani.smartfarm.controller;

import id.petani.smartfarm.dto.ApiResponse;
import id.petani.smartfarm.dto.TenantRequestDTO;
import id.petani.smartfarm.dto.TenantResponseDTO;
import id.petani.smartfarm.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantResponseDTO>>> getAllTenants(@RequestParam(required = false) Boolean isActive) {
        List<TenantResponseDTO> tenants = tenantService.getAllTenants(isActive);
        return ResponseEntity.ok(ApiResponse.success("Tenants retrieved successfully", tenants));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponseDTO>> getTenantById(@PathVariable Long id) {
        TenantResponseDTO tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", tenant));
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<TenantResponseDTO>> createTenant(@Valid @ModelAttribute TenantRequestDTO tenantRequestDTO) {
        TenantResponseDTO createdTenant = tenantService.createTenant(tenantRequestDTO);
        return new ResponseEntity<>(ApiResponse.success("Tenant created successfully", createdTenant), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<TenantResponseDTO>> updateTenant(@PathVariable Long id, @Valid @ModelAttribute TenantRequestDTO tenantRequestDTO) {
        TenantResponseDTO updatedTenant = tenantService.updateTenant(id, tenantRequestDTO);
        return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", updatedTenant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant deleted successfully", null));
    }
}
