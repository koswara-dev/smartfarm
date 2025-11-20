package id.petani.smartfarm.controller;

import id.petani.smartfarm.dto.AuthRequest;
import id.petani.smartfarm.dto.ApiResponse;
import id.petani.smartfarm.dto.AuthResponse;
import id.petani.smartfarm.dto.UserRequestDTO;
import id.petani.smartfarm.dto.UserResponseDTO;
import id.petani.smartfarm.security.JwtUtil;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                User userDetails = (User) authentication.getPrincipal();
                String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getFullName(), userDetails.getRoles());
                AuthResponse authResponse = new AuthResponse(token);
                return new ResponseEntity<>(ApiResponse.success("Login successful", authResponse), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ApiResponse.error("Invalid user request!", null), HttpStatus.UNAUTHORIZED);
            }
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage(), null), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("An unexpected error occurred during login.", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO registeredUser = userService.createUser(userRequestDTO);
        return new ResponseEntity<>(ApiResponse.success("User registered successfully", registeredUser), HttpStatus.CREATED);
    }
}
