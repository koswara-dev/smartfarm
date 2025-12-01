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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private Subscription subscription;
    private SubscriptionRequestDTO subscriptionRequestDTO;
    private SubscriptionPlan subscriptionPlan;
    private Tenant tenant;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("Test Tenant");

        subscriptionPlan = new SubscriptionPlan();
        subscriptionPlan.setId(1L);
        subscriptionPlan.setName("Basic Plan");
        subscriptionPlan.setPriceMonthly(BigDecimal.valueOf(10.0));
        subscriptionPlan.setPriceYearly(BigDecimal.valueOf(100.0));

        subscription = new Subscription();
        subscription.setId(1L);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setIsActive(true);
        subscription.setBillingCycle("MONTHLY");
        subscription.setTenant(tenant);
        subscription.setSubscriptionPlan(subscriptionPlan);
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setUpdatedAt(LocalDateTime.now());

        subscriptionRequestDTO = new SubscriptionRequestDTO();
        subscriptionRequestDTO.setStartDate(LocalDate.now());
        subscriptionRequestDTO.setEndDate(LocalDate.now().plusMonths(1));
        subscriptionRequestDTO.setIsActive(true);
        subscriptionRequestDTO.setBillingCycle("MONTHLY");
        subscriptionRequestDTO.setTenantId(1L);
        subscriptionRequestDTO.setSubscriptionPlanId(1L);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testGetAllSubscriptions_noFilters() {
        Page<Subscription> subscriptionPage = new PageImpl<>(Collections.singletonList(subscription));
        when(subscriptionRepository.findAll(pageable)).thenReturn(subscriptionPage);

        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(subscription.getId(), result.getContent().get(0).getId());
        verify(subscriptionRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllSubscriptions_byStatus() {
        Page<Subscription> subscriptionPage = new PageImpl<>(Collections.singletonList(subscription));
        when(subscriptionRepository.findByIsActive(true, pageable)).thenReturn(subscriptionPage);

        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(true, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(subscriptionRepository, times(1)).findByIsActive(true, pageable);
    }

    @Test
    void testGetAllSubscriptions_byBillingCycle() {
        Page<Subscription> subscriptionPage = new PageImpl<>(Collections.singletonList(subscription));
        when(subscriptionRepository.findByBillingCycle("MONTHLY", pageable)).thenReturn(subscriptionPage);

        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(null, "MONTHLY", null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(subscriptionRepository, times(1)).findByBillingCycle("MONTHLY", pageable);
    }

    @Test
    void testGetAllSubscriptions_bySubscriptionPlanId() {
        Page<Subscription> subscriptionPage = new PageImpl<>(Collections.singletonList(subscription));
        when(subscriptionRepository.findBySubscriptionPlanId(1L, pageable)).thenReturn(subscriptionPage);

        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(null, null, 1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(subscriptionRepository, times(1)).findBySubscriptionPlanId(1L, pageable);
    }

    @Test
    void testGetAllSubscriptions_byStatusAndBillingCycle() {
        Page<Subscription> subscriptionPage = new PageImpl<>(Collections.singletonList(subscription));
        when(subscriptionRepository.findByIsActiveAndBillingCycle(true, "MONTHLY", pageable)).thenReturn(subscriptionPage);

        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(true, "MONTHLY", null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(subscriptionRepository, times(1)).findByIsActiveAndBillingCycle(true, "MONTHLY", pageable);
    }

    @Test
    void testGetAllSubscriptions_byStatusAndSubscriptionPlanId() {
        Page<Subscription> subscriptionPage = new PageImpl<>(Collections.singletonList(subscription));
        when(subscriptionRepository.findByIsActiveAndSubscriptionPlanId(true, 1L, pageable)).thenReturn(subscriptionPage);

        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(true, null, 1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(subscriptionRepository, times(1)).findByIsActiveAndSubscriptionPlanId(true, 1L, pageable);
    }

    @Test
    void testGetAllSubscriptions_byBillingCycleAndSubscriptionPlanId() {
        Page<Subscription> subscriptionPage = new PageImpl<>(Collections.singletonList(subscription));
        when(subscriptionRepository.findByBillingCycleAndSubscriptionPlanId("MONTHLY", 1L, pageable)).thenReturn(subscriptionPage);

        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(null, "MONTHLY", 1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(subscriptionRepository, times(1)).findByBillingCycleAndSubscriptionPlanId("MONTHLY", 1L, pageable);
    }

    @Test
    void testGetAllSubscriptions_allFilters() {
        Page<Subscription> subscriptionPage = new PageImpl<>(Collections.singletonList(subscription));
        when(subscriptionRepository.findByIsActiveAndBillingCycleAndSubscriptionPlanId(true, "MONTHLY", 1L, pageable)).thenReturn(subscriptionPage);

        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(true, "MONTHLY", 1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(subscriptionRepository, times(1)).findByIsActiveAndBillingCycleAndSubscriptionPlanId(true, "MONTHLY", 1L, pageable);
    }

    @Test
    void testGetSubscriptionById_success() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        SubscriptionResponseDTO result = subscriptionService.getSubscriptionById(1L);

        assertNotNull(result);
        assertEquals(subscription.getId(), result.getId());
        verify(subscriptionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSubscriptionById_notFound() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.getSubscriptionById(1L));
        verify(subscriptionRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateSubscription_success() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(subscriptionPlan));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        SubscriptionResponseDTO result = subscriptionService.createSubscription(subscriptionRequestDTO);

        assertNotNull(result);
        assertEquals(subscription.getId(), result.getId());
        assertEquals(subscription.getTenant().getId(), result.getTenantId());
        assertEquals(subscription.getSubscriptionPlan().getId(), result.getSubscriptionPlanId());
        verify(tenantRepository, times(1)).findById(1L);
        verify(subscriptionPlanRepository, times(1)).findById(1L);
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    void testCreateSubscription_tenantNotFound() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.createSubscription(subscriptionRequestDTO));
        verify(tenantRepository, times(1)).findById(1L);
        verify(subscriptionPlanRepository, never()).findById(anyLong());
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void testCreateSubscription_subscriptionPlanNotFound() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.createSubscription(subscriptionRequestDTO));
        verify(tenantRepository, times(1)).findById(1L);
        verify(subscriptionPlanRepository, times(1)).findById(1L);
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void testUpdateSubscription_success() {
        Subscription updatedSubscription = new Subscription();
        updatedSubscription.setId(1L);
        updatedSubscription.setStartDate(LocalDate.now().plusDays(1));
        updatedSubscription.setEndDate(LocalDate.now().plusMonths(2));
        updatedSubscription.setIsActive(false);
        updatedSubscription.setBillingCycle("YEARLY");
        updatedSubscription.setTenant(tenant);
        updatedSubscription.setSubscriptionPlan(subscriptionPlan);

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(subscriptionPlan));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(updatedSubscription);

        SubscriptionRequestDTO updateRequest = new SubscriptionRequestDTO();
        updateRequest.setStartDate(LocalDate.now().plusDays(1));
        updateRequest.setEndDate(LocalDate.now().plusMonths(2));
        updateRequest.setIsActive(false);
        updateRequest.setBillingCycle("YEARLY");
        updateRequest.setTenantId(1L);
        updateRequest.setSubscriptionPlanId(1L);

        SubscriptionResponseDTO result = subscriptionService.updateSubscription(1L, updateRequest);

        assertNotNull(result);
        assertEquals(updatedSubscription.getStartDate(), result.getStartDate());
        assertEquals(updatedSubscription.getBillingCycle(), result.getBillingCycle());
        verify(subscriptionRepository, times(1)).findById(1L);
        verify(tenantRepository, times(1)).findById(1L);
        verify(subscriptionPlanRepository, times(1)).findById(1L);
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    void testUpdateSubscription_subscriptionNotFound() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.updateSubscription(1L, subscriptionRequestDTO));
        verify(subscriptionRepository, times(1)).findById(1L);
        verify(tenantRepository, never()).findById(anyLong());
        verify(subscriptionPlanRepository, never()).findById(anyLong());
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void testUpdateSubscription_tenantNotFound() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.updateSubscription(1L, subscriptionRequestDTO));
        verify(subscriptionRepository, times(1)).findById(1L);
        verify(tenantRepository, times(1)).findById(1L);
        verify(subscriptionPlanRepository, never()).findById(anyLong());
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void testUpdateSubscription_subscriptionPlanNotFound() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.updateSubscription(1L, subscriptionRequestDTO));
        verify(subscriptionRepository, times(1)).findById(1L);
        verify(tenantRepository, times(1)).findById(1L);
        verify(subscriptionPlanRepository, times(1)).findById(1L);
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void testDeleteSubscription_success() {
        when(subscriptionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(subscriptionRepository).deleteById(1L);

        assertDoesNotThrow(() -> subscriptionService.deleteSubscription(1L));
        verify(subscriptionRepository, times(1)).existsById(1L);
        verify(subscriptionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteSubscription_notFound() {
        when(subscriptionRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.deleteSubscription(1L));
        verify(subscriptionRepository, times(1)).existsById(1L);
        verify(subscriptionRepository, never()).deleteById(anyLong());
    }
}
