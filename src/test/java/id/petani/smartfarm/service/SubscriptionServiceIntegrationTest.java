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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SubscriptionServiceIntegrationTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    private Tenant tenant1;
    private SubscriptionPlan plan1;
    private Subscription subscription1;
    private Subscription subscription2;

    @BeforeEach
    void setUp() {
        // Clear all repositories before each test
        subscriptionRepository.deleteAll();
        tenantRepository.deleteAll();
        subscriptionPlanRepository.deleteAll();

        // Setup common test data
        tenant1 = new Tenant();
        tenant1.setName("Integration Test Tenant 1");
        tenant1.setEmail("tenant1@example.com");
        tenant1.setCreatedAt(LocalDateTime.now());
        tenant1.setUpdatedAt(LocalDateTime.now());
        tenantRepository.save(tenant1);

        Tenant tenant2 = new Tenant();
        tenant2.setName("Integration Test Tenant 2");
        tenant2.setEmail("tenant2@example.com");
        tenant2.setCreatedAt(LocalDateTime.now());
        tenant2.setUpdatedAt(LocalDateTime.now());
        tenantRepository.save(tenant2);

        plan1 = new SubscriptionPlan();
        plan1.setName("Basic Plan IT");
        plan1.setDescription("Basic plan for integration test");
        plan1.setPriceMonthly(BigDecimal.valueOf(50.0));
        plan1.setPriceYearly(BigDecimal.valueOf(500.0));
        plan1.setMaxDevices(10);
        plan1.setMaxUsers(5);
        plan1.setCreatedAt(LocalDateTime.now());
        plan1.setUpdatedAt(LocalDateTime.now());
        subscriptionPlanRepository.save(plan1);

        SubscriptionPlan plan2 = new SubscriptionPlan();
        plan2.setName("Premium Plan IT");
        plan2.setDescription("Premium plan for integration test");
        plan2.setPriceMonthly(BigDecimal.valueOf(100.0));
        plan2.setPriceYearly(BigDecimal.valueOf(1000.0));
        plan2.setMaxDevices(20);
        plan2.setMaxUsers(10);
        plan2.setCreatedAt(LocalDateTime.now());
        plan2.setUpdatedAt(LocalDateTime.now());
        subscriptionPlanRepository.save(plan2);

        subscription1 = new Subscription();
        subscription1.setStartDate(LocalDate.now());
        subscription1.setEndDate(LocalDate.now().plusMonths(1));
        subscription1.setIsActive(true);
        subscription1.setBillingCycle("MONTHLY");
        subscription1.setTenant(tenant1);
        subscription1.setSubscriptionPlan(plan1);
        subscription1.setCreatedAt(LocalDateTime.now());
        subscription1.setUpdatedAt(LocalDateTime.now());
        subscriptionRepository.save(subscription1);

        subscription2 = new Subscription();
        subscription2.setStartDate(LocalDate.now().minusMonths(2));
        subscription2.setEndDate(LocalDate.now().minusMonths(1));
        subscription2.setIsActive(false);
        subscription2.setBillingCycle("ANNUALLY");
        subscription2.setTenant(tenant2);
        subscription2.setSubscriptionPlan(plan2);
        subscription2.setCreatedAt(LocalDateTime.now());
        subscription2.setUpdatedAt(LocalDateTime.now());
        subscriptionRepository.save(subscription2);
    }

    @AfterEach
    void tearDown() {
        subscriptionRepository.deleteAll();
        tenantRepository.deleteAll();
        subscriptionPlanRepository.deleteAll();
    }

    @Test
    void testGetAllSubscriptions_noFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(null, null, null, pageable);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testGetAllSubscriptions_byStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(true, null, null, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(subscription1.getId(), result.getContent().get(0).getId());
    }

    @Test
    void testGetAllSubscriptions_byBillingCycle() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(null, "MONTHLY", null, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(subscription1.getId(), result.getContent().get(0).getId());
    }

    @Test
    void testGetAllSubscriptions_bySubscriptionPlanId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(null, null, plan1.getId(), pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(subscription1.getId(), result.getContent().get(0).getId());
    }

    @Test
    void testGetAllSubscriptions_byStatusAndBillingCycle() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SubscriptionResponseDTO> result = subscriptionService.getAllSubscriptions(true, "MONTHLY", null, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(subscription1.getId(), result.getContent().get(0).getId());
    }

    @Test
    void testGetSubscriptionById_success() {
        SubscriptionResponseDTO result = subscriptionService.getSubscriptionById(subscription1.getId());
        assertNotNull(result);
        assertEquals(subscription1.getId(), result.getId());
        assertEquals(tenant1.getId(), result.getTenantId());
        assertEquals(plan1.getId(), result.getSubscriptionPlanId());
    }

    @Test
    void testGetSubscriptionById_notFound() {
        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.getSubscriptionById(99L));
    }

    @Test
    void testCreateSubscription_success() {
        SubscriptionRequestDTO requestDTO = new SubscriptionRequestDTO();
        requestDTO.setStartDate(LocalDate.now());
        requestDTO.setEndDate(LocalDate.now().plusMonths(3));
        requestDTO.setIsActive(true);
        requestDTO.setBillingCycle("QUARTERLY");
        requestDTO.setTenantId(tenant1.getId());
        requestDTO.setSubscriptionPlanId(plan1.getId());

        SubscriptionResponseDTO result = subscriptionService.createSubscription(requestDTO);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("QUARTERLY", result.getBillingCycle());
        assertEquals(3, subscriptionRepository.count());
    }

    @Test
    void testCreateSubscription_tenantNotFound() {
        SubscriptionRequestDTO requestDTO = new SubscriptionRequestDTO();
        requestDTO.setStartDate(LocalDate.now());
        requestDTO.setEndDate(LocalDate.now().plusMonths(3));
        requestDTO.setIsActive(true);
        requestDTO.setBillingCycle("QUARTERLY");
        requestDTO.setTenantId(99L); // Non-existent tenant
        requestDTO.setSubscriptionPlanId(plan1.getId());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.createSubscription(requestDTO));
    }

    @Test
    void testCreateSubscription_subscriptionPlanNotFound() {
        SubscriptionRequestDTO requestDTO = new SubscriptionRequestDTO();
        requestDTO.setStartDate(LocalDate.now());
        requestDTO.setEndDate(LocalDate.now().plusMonths(3));
        requestDTO.setIsActive(true);
        requestDTO.setBillingCycle("QUARTERLY");
        requestDTO.setTenantId(tenant1.getId());
        requestDTO.setSubscriptionPlanId(99L); // Non-existent plan

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.createSubscription(requestDTO));
    }

    @Test
    void testUpdateSubscription_success() {
        SubscriptionRequestDTO updateRequestDTO = new SubscriptionRequestDTO();
        updateRequestDTO.setStartDate(LocalDate.now().plusDays(5));
        updateRequestDTO.setEndDate(LocalDate.now().plusMonths(6));
        updateRequestDTO.setIsActive(false);
        updateRequestDTO.setBillingCycle("YEARLY");
        updateRequestDTO.setTenantId(tenant1.getId());
        updateRequestDTO.setSubscriptionPlanId(plan1.getId());

        SubscriptionResponseDTO result = subscriptionService.updateSubscription(subscription1.getId(), updateRequestDTO);

        assertNotNull(result);
        assertEquals(subscription1.getId(), result.getId());
        assertEquals("YEARLY", result.getBillingCycle());
        assertEquals(LocalDate.now().plusMonths(6), result.getEndDate());
        assertFalse(result.getIsActive());
    }

    @Test
    void testUpdateSubscription_subscriptionNotFound() {
        SubscriptionRequestDTO updateRequestDTO = new SubscriptionRequestDTO();
        updateRequestDTO.setStartDate(LocalDate.now());
        updateRequestDTO.setEndDate(LocalDate.now().plusMonths(3));
        updateRequestDTO.setIsActive(true);
        updateRequestDTO.setBillingCycle("QUARTERLY");
        updateRequestDTO.setTenantId(tenant1.getId());
        updateRequestDTO.setSubscriptionPlanId(plan1.getId());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.updateSubscription(99L, updateRequestDTO));
    }

    @Test
    void testUpdateSubscription_tenantNotFound() {
        SubscriptionRequestDTO updateRequestDTO = new SubscriptionRequestDTO();
        updateRequestDTO.setStartDate(LocalDate.now());
        updateRequestDTO.setEndDate(LocalDate.now().plusMonths(3));
        updateRequestDTO.setIsActive(true);
        updateRequestDTO.setBillingCycle("QUARTERLY");
        updateRequestDTO.setTenantId(99L); // Non-existent tenant
        updateRequestDTO.setSubscriptionPlanId(plan1.getId());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.updateSubscription(subscription1.getId(), updateRequestDTO));
    }

    @Test
    void testUpdateSubscription_subscriptionPlanNotFound() {
        SubscriptionRequestDTO updateRequestDTO = new SubscriptionRequestDTO();
        updateRequestDTO.setStartDate(LocalDate.now());
        updateRequestDTO.setEndDate(LocalDate.now().plusMonths(3));
        updateRequestDTO.setIsActive(true);
        updateRequestDTO.setBillingCycle("QUARTERLY");
        updateRequestDTO.setTenantId(tenant1.getId());
        updateRequestDTO.setSubscriptionPlanId(99L); // Non-existent plan

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.updateSubscription(subscription1.getId(), updateRequestDTO));
    }

    @Test
    void testDeleteSubscription_success() {
        subscriptionService.deleteSubscription(subscription1.getId());
        assertEquals(1, subscriptionRepository.count());
        assertFalse(subscriptionRepository.findById(subscription1.getId()).isPresent());
    }

    @Test
    void testDeleteSubscription_notFound() {
        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.deleteSubscription(99L));
    }
}
