package id.petani.smartfarm.repository;

import id.petani.smartfarm.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    List<Tenant> findByIsActiveOrderByIdAsc(boolean isActive);
    List<Tenant> findAllByOrderByIdAsc();
}
