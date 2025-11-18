package id.petani.smartfarm.repository;

import id.petani.smartfarm.model.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Page<Subscription> findByIsActive(Boolean isActive, Pageable pageable);
    Page<Subscription> findByBillingCycle(String billingCycle, Pageable pageable);
    Page<Subscription> findByIsActiveAndBillingCycle(Boolean isActive, String billingCycle, Pageable pageable);
    Page<Subscription> findBySubscriptionPlanId(Long subscriptionPlanId, Pageable pageable);
    Page<Subscription> findByIsActiveAndSubscriptionPlanId(Boolean isActive, Long subscriptionPlanId, Pageable pageable);
    Page<Subscription> findByBillingCycleAndSubscriptionPlanId(String billingCycle, Long subscriptionPlanId, Pageable pageable);
    Page<Subscription> findByIsActiveAndBillingCycleAndSubscriptionPlanId(Boolean isActive, String billingCycle, Long subscriptionPlanId, Pageable pageable);
}
