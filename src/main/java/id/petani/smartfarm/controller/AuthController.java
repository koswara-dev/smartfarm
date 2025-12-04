package id.petani.smartfarm.controller;

import id.petani.smartfarm.dto.AuthRequest;
import id.petani.smartfarm.dto.ApiResponse;
import id.petani.smartfarm.dto.AuthResponse;
import id.petani.smartfarm.dto.UserRequestDTO;
import id.petani.smartfarm.dto.UserResponseDTO;
import id.petani.smartfarm.security.JwtUtil;
import id.petani.smartfarm.service.RateLimitingService; // Import RateLimitingService
import id.petani.smartfarm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import id.petani.smartfarm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest; // Import HttpServletRequest

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private RateLimitingService rateLimitingService; // Inject RateLimitingService

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateAndGetToken(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        String rateLimitKey = "login_" + clientIp; // Rate limit by IP address

        if (rateLimitingService.isRateLimited(rateLimitKey)) {
            logger.warn("Rate limit exceeded for IP: {}", clientIp);
            return new ResponseEntity<>(ApiResponse.error("Too many login attempts. Please try again later.", null), HttpStatus.TOO_MANY_REQUESTS);
        }

        logger.info("Attempting to authenticate user: {}", authRequest.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                rateLimitingService.recordSuccessfulAttempt(rateLimitKey); // Clear attempts on success
                User userDetails = (User) authentication.getPrincipal();
                String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getFullName(), userDetails.getRoles());
                AuthResponse authResponse = new AuthResponse(token);
                logger.info("User {} authenticated successfully.", authRequest.getEmail());
                return new ResponseEntity<>(ApiResponse.success("Login successful", authResponse), HttpStatus.OK);
            } else {
                logger.warn("Authentication failed for user: {}. Invalid credentials.", authRequest.getEmail());
                // No need to explicitly call recordFailedAttempt here, as isRateLimited already increments on failed attempts within the window.
                return new ResponseEntity<>(ApiResponse.error("Invalid user request!", null), HttpStatus.UNAUTHORIZED);
            }
        } catch (UsernameNotFoundException e) {
            logger.warn("User not found: {}", authRequest.getEmail());
            // No need to explicitly call recordFailedAttempt here, as isRateLimited already increments on failed attempts within the window.
            return new ResponseEntity<>(ApiResponse.error(e.getMessage(), null), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during login for user {}: {}", authRequest.getEmail(), e.getMessage(), e);
            // No need to explicitly call recordFailedAttempt here, as isRateLimited already increments on failed attempts within the window.
            return new ResponseEntity<>(ApiResponse.error("An unexpected error occurred during login.", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to extract client IP address
    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(".")) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        logger.info("Attempting to register new user: {}", userRequestDTO.getEmail());
        UserResponseDTO registeredUser = userService.createUser(userRequestDTO);
        logger.info("User {} registered successfully.", registeredUser.getEmail());
        return new ResponseEntity<>(ApiResponse.success("User registered successfully", registeredUser), HttpStatus.CREATED);
    }
}
