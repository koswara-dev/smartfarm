package id.petani.smartfarm.service;

import id.petani.smartfarm.dto.UserRequestDTO;
import id.petani.smartfarm.dto.UserResponseDTO;
import id.petani.smartfarm.exception.ResourceNotFoundException;
import id.petani.smartfarm.model.Tenant;
import id.petani.smartfarm.model.User;
import id.petani.smartfarm.repository.TenantRepository;
import id.petani.smartfarm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, TenantRepository tenantRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserResponseDTO> getAllUsers(Boolean isActive, Long tenantId, String email, Pageable pageable) {
        Page<User> users;
        if (isActive != null && tenantId != null && email != null) {
            users = userRepository.findByIsActiveAndTenantIdAndEmailContainingIgnoreCase(isActive, tenantId, email, pageable);
        } else if (isActive != null && tenantId != null) {
            users = userRepository.findByIsActiveAndTenantId(isActive, tenantId, pageable);
        } else if (isActive != null && email != null) {
            users = userRepository.findByIsActiveAndEmailContainingIgnoreCase(isActive, email, pageable);
        } else if (tenantId != null && email != null) {
            users = userRepository.findByTenantIdAndEmailContainingIgnoreCase(tenantId, email, pageable);
        } else if (isActive != null) {
            users = userRepository.findByIsActive(isActive, pageable);
        } else if (tenantId != null) {
            users = userRepository.findByTenantId(tenantId, pageable);
        } else if (email != null) {
            users = userRepository.findByEmailContainingIgnoreCase(email, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(this::convertToDto);
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return convertToDto(user);
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        Tenant tenant = tenantRepository.findById(userRequestDTO.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id " + userRequestDTO.getTenantId()));

        User user = new User();
        user.setFullName(userRequestDTO.getFullName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setIsActive(userRequestDTO.getIsActive());
        user.setTenant(tenant);

        user.setRoles("ROLE_USER"); // Default role for new users
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        Tenant tenant = tenantRepository.findById(userRequestDTO.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id " + userRequestDTO.getTenantId()));

        existingUser.setFullName(userRequestDTO.getFullName());
        existingUser.setEmail(userRequestDTO.getEmail());
        // Only update password if a new one is provided
        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }
        existingUser.setIsActive(userRequestDTO.getIsActive());
        existingUser.setTenant(tenant);

        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO convertToDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        dto.setTenantId(user.getTenant().getId());
        dto.setTenantName(user.getTenant().getName());

        return dto;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
