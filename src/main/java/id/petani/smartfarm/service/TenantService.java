package id.petani.smartfarm.service;

import id.petani.smartfarm.model.Tenant;
import id.petani.smartfarm.repository.TenantRepository;
import id.petani.smartfarm.dto.TenantRequestDTO;
import id.petani.smartfarm.dto.TenantResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.petani.smartfarm.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    public List<TenantResponseDTO> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TenantResponseDTO getTenantById(Long id) {
        return tenantRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id " + id));
    }

    public TenantResponseDTO createTenant(TenantRequestDTO tenantRequestDTO) {
        Tenant tenant = new Tenant();
        tenant.setName(tenantRequestDTO.getName());
        tenant.setEmail(tenantRequestDTO.getEmail());
        tenant.setDomain(tenantRequestDTO.getDomain());
        tenant.setSubdomain(tenantRequestDTO.getSubdomain());
        tenant.setPhoneNumber(tenantRequestDTO.getPhoneNumber());
        tenant.setActive(tenantRequestDTO.isActive());
        return convertToDto(tenantRepository.save(tenant));
    }

    public TenantResponseDTO updateTenant(Long id, TenantRequestDTO tenantRequestDTO) {
        return tenantRepository.findById(id).map(tenant -> {
            tenant.setName(tenantRequestDTO.getName());
            tenant.setEmail(tenantRequestDTO.getEmail());
            tenant.setDomain(tenantRequestDTO.getDomain());
            tenant.setSubdomain(tenantRequestDTO.getSubdomain());
            tenant.setPhoneNumber(tenantRequestDTO.getPhoneNumber());
            tenant.setActive(tenantRequestDTO.isActive());
            return convertToDto(tenantRepository.save(tenant));
        }).orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id " + id));
    }

    public void deleteTenant(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant not found with id " + id);
        }
        tenantRepository.deleteById(id);
    }

    private TenantResponseDTO convertToDto(Tenant tenant) {
        return new TenantResponseDTO(tenant.getId(), tenant.getName(), tenant.getEmail(),
                tenant.getDomain(), tenant.getSubdomain(), tenant.getPhoneNumber(),
                tenant.isActive(), tenant.getCreatedAt(), tenant.getUpdatedAt());
    }
}
