package id.petani.smartfarm.service;

import id.petani.smartfarm.model.Tenant;
import id.petani.smartfarm.repository.TenantRepository;
import id.petani.smartfarm.service.storage.StorageService;
import id.petani.smartfarm.dto.TenantRequestDTO;
import id.petani.smartfarm.dto.TenantResponseDTO;
import id.petani.smartfarm.util.ImageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import id.petani.smartfarm.exception.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // Import Value

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TenantService {

    private static final Logger logger = LoggerFactory.getLogger(TenantService.class);

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private StorageService storageService;

    @Value("${base.url}") // Inject base.url from application.properties
    private String baseUrl;

    public List<TenantResponseDTO> getAllTenants(Boolean isActive) {
        logger.info("Fetching all tenants with isActive: {}", isActive);
        List<Tenant> tenants;
        if (isActive != null) {
            tenants = tenantRepository.findByIsActiveOrderByIdAsc(isActive);
        } else {
            tenants = tenantRepository.findAllByOrderByIdAsc();
        }
        logger.debug("Found {} tenants", tenants.size());
        return tenants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TenantResponseDTO getTenantById(Long id) {
        logger.info("Fetching tenant with id: {}", id);
        return tenantRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> {
                    logger.warn("Tenant not found with id: {}", id);
                    return new ResourceNotFoundException("Tenant not found with id " + id);
                });
    }

    public TenantResponseDTO createTenant(TenantRequestDTO tenantRequestDTO) {
        logger.info("Creating new tenant: {}", tenantRequestDTO.getName());
        Tenant tenant = new Tenant();
        tenant.setName(tenantRequestDTO.getName());
        tenant.setEmail(tenantRequestDTO.getEmail());
        tenant.setDomain(tenantRequestDTO.getDomain());
        tenant.setSubdomain(tenantRequestDTO.getSubdomain());
        tenant.setPhoneNumber(tenantRequestDTO.getPhoneNumber());
        tenant.setActive(tenantRequestDTO.isActive());

        MultipartFile logoFile = tenantRequestDTO.getLogo();
        if (logoFile != null && !logoFile.isEmpty()) {
            logger.info("Attempting to store logo for tenant: {}", tenantRequestDTO.getName());
            ImageValidator.validateImage(logoFile);
            String logoUrl = storageService.store(logoFile);
            tenant.setLogoUrl(logoUrl);
            logger.debug("Logo stored at: {}", logoUrl);
        }

        Tenant savedTenant = tenantRepository.save(tenant);
        logger.info("Tenant created successfully with id: {}", savedTenant.getId());
        return convertToDto(savedTenant);
    }

    public TenantResponseDTO updateTenant(Long id, TenantRequestDTO tenantRequestDTO) {
        logger.info("Updating tenant with id: {}", id);
        return tenantRepository.findById(id).map(tenant -> {
            tenant.setName(tenantRequestDTO.getName());
            tenant.setEmail(tenantRequestDTO.getEmail());
            tenant.setDomain(tenantRequestDTO.getDomain());
            tenant.setSubdomain(tenantRequestDTO.getSubdomain());
            tenant.setPhoneNumber(tenantRequestDTO.getPhoneNumber());
            tenant.setActive(tenantRequestDTO.isActive());

            MultipartFile logoFile = tenantRequestDTO.getLogo();
            if (logoFile != null && !logoFile.isEmpty()) {
                logger.info("Attempting to update logo for tenant with id: {}", id);
                ImageValidator.validateImage(logoFile);
                String oldLogoUrl = tenant.getLogoUrl();
                if (oldLogoUrl != null && !oldLogoUrl.isEmpty()) {
                    storageService.delete(oldLogoUrl); // Delete old logo
                    logger.debug("Old logo {} deleted for tenant id: {}", oldLogoUrl, id);
                }
                String logoUrl = storageService.store(logoFile);
                tenant.setLogoUrl(logoUrl);
                logger.debug("New logo stored at: {}", logoUrl);
            } else if (tenantRequestDTO.getLogo() != null && tenantRequestDTO.getLogo().isEmpty() && tenant.getLogoUrl() != null) {
                // If logo is intentionally cleared by providing an empty file
                storageService.delete(tenant.getLogoUrl());
                tenant.setLogoUrl(null);
                logger.info("Logo cleared for tenant id: {}", id);
            }

            Tenant updatedTenant = tenantRepository.save(tenant);
            logger.info("Tenant with id: {} updated successfully.", updatedTenant.getId());
            return convertToDto(updatedTenant);
        }).orElseThrow(() -> {
            logger.warn("Tenant not found with id: {} for update.", id);
            return new ResourceNotFoundException("Tenant not found with id " + id);
        });
    }

    public void deleteTenant(Long id) {
        logger.info("Deleting tenant with id: {}", id);
        Optional<Tenant> tenantOptional = tenantRepository.findById(id);
        if (!tenantOptional.isPresent()) {
            logger.warn("Tenant not found with id: {} for deletion.", id);
            throw new ResourceNotFoundException("Tenant not found with id " + id);
        }
        Tenant tenant = tenantOptional.get();
        if (tenant.getLogoUrl() != null && !tenant.getLogoUrl().isEmpty()) {
            storageService.delete(tenant.getLogoUrl());
            logger.debug("Logo {} deleted for tenant id: {}", tenant.getLogoUrl(), id);
        }
        tenantRepository.deleteById(id);
        logger.info("Tenant with id: {} deleted successfully.", id);
    }

    private TenantResponseDTO convertToDto(Tenant tenant) {
        String logoUrl = tenant.getLogoUrl();
        if (logoUrl != null && !logoUrl.isEmpty()) {
            logoUrl = baseUrl + "/uploads/" + logoUrl; // Prepend base.url and /uploads/ for public access
        }
        return new TenantResponseDTO(tenant.getId(), tenant.getName(), tenant.getEmail(),
                tenant.getDomain(), tenant.getSubdomain(), tenant.getPhoneNumber(),
                logoUrl, // Include logoUrl in DTO conversion
                tenant.isActive(), tenant.getCreatedAt(), tenant.getUpdatedAt());
    }
}
