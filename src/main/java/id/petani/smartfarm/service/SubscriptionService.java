package id.petani.smartfarm.service;

import id.petani.smartfarm.dto.SubscriptionRequestDTO;
import id.petani.smartfarm.dto.SubscriptionResponseDTO;
import id.petani.smartfarm.exception.ResourceNotFoundException;
import id.petani.smartfarm.model.Subscription;
import id.petani.smartfarm.model.SubscriptionPlan;
import id.petani.smartfarm.model.Tenant;
import id.petani.smartfarm.repository.SubscriptionPlanRepository;
import id.petani.smartfarm.repository.SubscriptionRepository;
import id.petani.smartfarm.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    public Page<SubscriptionResponseDTO> getAllSubscriptions(Boolean status, String billingCycle, Long subscriptionPlanId, Pageable pageable) {
        Page<Subscription> subscriptions;
        if (status != null && billingCycle != null && subscriptionPlanId != null) {
            subscriptions = subscriptionRepository.findByIsActiveAndBillingCycleAndSubscriptionPlanId(status, billingCycle, subscriptionPlanId, pageable);
        } else if (status != null && billingCycle != null) {
            subscriptions = subscriptionRepository.findByIsActiveAndBillingCycle(status, billingCycle, pageable);
        } else if (status != null && subscriptionPlanId != null) {
            subscriptions = subscriptionRepository.findByIsActiveAndSubscriptionPlanId(status, subscriptionPlanId, pageable);
        } else if (billingCycle != null && subscriptionPlanId != null) {
            subscriptions = subscriptionRepository.findByBillingCycleAndSubscriptionPlanId(billingCycle, subscriptionPlanId, pageable);
        } else if (status != null) {
            subscriptions = subscriptionRepository.findByIsActive(status, pageable);
        } else if (billingCycle != null) {
            subscriptions = subscriptionRepository.findByBillingCycle(billingCycle, pageable);
        } else if (subscriptionPlanId != null) {
            subscriptions = subscriptionRepository.findBySubscriptionPlanId(subscriptionPlanId, pageable);
        } else {
            subscriptions = subscriptionRepository.findAll(pageable);
        }
        return subscriptions.map(this::convertToDto);
    }

    public SubscriptionResponseDTO getSubscriptionById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id " + id));
        return convertToDto(subscription);
    }

    public SubscriptionResponseDTO createSubscription(SubscriptionRequestDTO subscriptionRequestDTO) {
        Tenant tenant = tenantRepository.findById(subscriptionRequestDTO.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id " + subscriptionRequestDTO.getTenantId()));
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(subscriptionRequestDTO.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription Plan not found with id " + subscriptionRequestDTO.getSubscriptionPlanId()));

        Subscription subscription = new Subscription();
        subscription.setStartDate(subscriptionRequestDTO.getStartDate());
        subscription.setEndDate(subscriptionRequestDTO.getEndDate());
        subscription.setIsActive(subscriptionRequestDTO.getIsActive());
        subscription.setBillingCycle(subscriptionRequestDTO.getBillingCycle());
        subscription.setTenant(tenant);
        subscription.setSubscriptionPlan(subscriptionPlan);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return convertToDto(savedSubscription);
    }

    public SubscriptionResponseDTO updateSubscription(Long id, SubscriptionRequestDTO subscriptionRequestDTO) {
        Subscription existingSubscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id " + id));

        Tenant tenant = tenantRepository.findById(subscriptionRequestDTO.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id " + subscriptionRequestDTO.getTenantId()));
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(subscriptionRequestDTO.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription Plan not found with id " + subscriptionRequestDTO.getSubscriptionPlanId()));

        existingSubscription.setStartDate(subscriptionRequestDTO.getStartDate());
        existingSubscription.setEndDate(subscriptionRequestDTO.getEndDate());
        existingSubscription.setIsActive(subscriptionRequestDTO.getIsActive());
        existingSubscription.setBillingCycle(subscriptionRequestDTO.getBillingCycle());
        existingSubscription.setTenant(tenant);
        existingSubscription.setSubscriptionPlan(subscriptionPlan);

        Subscription updatedSubscription = subscriptionRepository.save(existingSubscription);
        return convertToDto(updatedSubscription);
    }

    public void deleteSubscription(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscription not found with id " + id);
        }
        subscriptionRepository.deleteById(id);
    }

    private SubscriptionResponseDTO convertToDto(Subscription subscription) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setId(subscription.getId());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setIsActive(subscription.getIsActive());
        dto.setBillingCycle(subscription.getBillingCycle());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());

        dto.setTenantId(subscription.getTenant().getId());
        dto.setTenantName(subscription.getTenant().getName());

        dto.setSubscriptionPlanId(subscription.getSubscriptionPlan().getId());
        dto.setSubscriptionPlanName(subscription.getSubscriptionPlan().getName());
        dto.setPriceMonthly(subscription.getSubscriptionPlan().getPriceMonthly().doubleValue());
        dto.setPriceYearly(subscription.getSubscriptionPlan().getPriceYearly().doubleValue());

        return dto;
    }
}
