package id.petani.smartfarm.repository;

import id.petani.smartfarm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    Page<User> findByTenantId(Long tenantId, Pageable pageable);
    Page<User> findByIsActiveAndTenantId(Boolean isActive, Long tenantId, Pageable pageable);
    Page<User> findByIsActiveAndTenantIdAndEmailContainingIgnoreCase(Boolean isActive, Long tenantId, String email, Pageable pageable);
    Page<User> findByIsActiveAndEmailContainingIgnoreCase(Boolean isActive, String email, Pageable pageable);
    Page<User> findByTenantIdAndEmailContainingIgnoreCase(Long tenantId, String email, Pageable pageable);
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
