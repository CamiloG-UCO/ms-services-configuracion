package co.edu.hotel.configuracionservice.controller;

import co.edu.hotel.configuracionservice.domain.user.dto.AuthResponse;
import co.edu.hotel.configuracionservice.domain.user.dto.LoginRequest;
import co.edu.hotel.configuracionservice.domain.user.dto.RegisterRequest;
import co.edu.hotel.configuracionservice.domain.user.dto.UserResponse;
import co.edu.hotel.configuracionservice.services.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final IUserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Solicitud de login para usuario: {}", loginRequest.getEmail());
        
        try {
            AuthResponse response = userService.login(loginRequest);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Login fallido para {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Solicitud de registro para usuario: {}", registerRequest.getEmail());
        
        try {
            UserResponse response = userService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Registro fallido para {}: {}", registerRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        log.info("Solicitud de información para usuario ID: {}", userId);
        
        try {
            UserResponse response = userService.getUserById(userId);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Usuario no encontrado ID {}: {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/by-email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("Solicitud de información para usuario email: {}", email);
        
        try {
            UserResponse response = userService.getUserByEmail(email);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Usuario no encontrado email {}: {}", email, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/user/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable UUID userId,
            @RequestParam Boolean isActive) {
        log.info("Solicitud de cambio de estado para usuario ID {}: {}", userId, isActive);
        
        try {
            UserResponse response = userService.updateUserStatus(userId, isActive);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error actualizando estado usuario ID {}: {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}