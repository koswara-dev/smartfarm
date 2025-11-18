package id.petani.smartfarm.service;

import id.petani.smartfarm.dto.SubscriptionPlanRequestDTO;
import id.petani.smartfarm.dto.SubscriptionPlanResponseDTO;
import id.petani.smartfarm.exception.ResourceNotFoundException;
import id.petani.smartfarm.model.SubscriptionPlan;
import id.petani.smartfarm.repository.SubscriptionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SubscriptionPlanService {

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Transactional
    public SubscriptionPlanResponseDTO createSubscriptionPlan(SubscriptionPlanRequestDTO requestDTO) {
        if (subscriptionPlanRepository.findByName(requestDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Subscription plan with name " + requestDTO.getName() + " already exists.");
        }

        SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
        subscriptionPlan.setName(requestDTO.getName());
        subscriptionPlan.setPriceMonthly(requestDTO.getPriceMonthly());
        subscriptionPlan.setPriceYearly(requestDTO.getPriceYearly());
        subscriptionPlan.setMaxUsers(requestDTO.getMaxUsers());
        subscriptionPlan.setMaxDevices(requestDTO.getMaxDevices());
        subscriptionPlan.setDescription(requestDTO.getDescription());

        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(subscriptionPlan);
        return SubscriptionPlanResponseDTO.fromEntity(savedPlan);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponseDTO> getAllSubscriptionPlans() {
        return subscriptionPlanRepository.findAllByOrderByIdAsc()
                .stream()
                .map(SubscriptionPlanResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubscriptionPlanResponseDTO getSubscriptionPlanById(Long id) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubscriptionPlan not found with id " + id));
        return SubscriptionPlanResponseDTO.fromEntity(subscriptionPlan);
    }

    @Transactional
    public SubscriptionPlanResponseDTO updateSubscriptionPlan(Long id, SubscriptionPlanRequestDTO requestDTO) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubscriptionPlan not found with id " + id));

        Optional<SubscriptionPlan> existingPlanWithName = subscriptionPlanRepository.findByName(requestDTO.getName());
        if (existingPlanWithName.isPresent() && !existingPlanWithName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Subscription plan with name " + requestDTO.getName() + " already exists.");
        }

        subscriptionPlan.setName(requestDTO.getName());
        subscriptionPlan.setPriceMonthly(requestDTO.getPriceMonthly());
        subscriptionPlan.setPriceYearly(requestDTO.getPriceYearly());
        subscriptionPlan.setMaxUsers(requestDTO.getMaxUsers());
        subscriptionPlan.setMaxDevices(requestDTO.getMaxDevices());
        subscriptionPlan.setDescription(requestDTO.getDescription());

        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(subscriptionPlan);
        return SubscriptionPlanResponseDTO.fromEntity(updatedPlan);
    }

    @Transactional
    public void deleteSubscriptionPlan(Long id) {
        if (!subscriptionPlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("SubscriptionPlan not found with id " + id);
        }
        subscriptionPlanRepository.deleteById(id);
    }
}
