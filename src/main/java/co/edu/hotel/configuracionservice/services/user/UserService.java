package co.edu.hotel.configuracionservice.services.user;

import co.edu.hotel.configuracionservice.domain.user.User;
import co.edu.hotel.configuracionservice.domain.user.Role;
import co.edu.hotel.configuracionservice.domain.user.dto.AuthResponse;
import co.edu.hotel.configuracionservice.domain.user.dto.LoginRequest;
import co.edu.hotel.configuracionservice.domain.user.dto.RegisterRequest;
import co.edu.hotel.configuracionservice.domain.user.dto.UserResponse;
import co.edu.hotel.configuracionservice.repository.user.IUserRepository;
import co.edu.hotel.configuracionservice.repository.user.IRoleRepository;
import co.edu.hotel.configuracionservice.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Iniciando proceso de login para usuario: {}", loginRequest.getEmail());
        
        try {

            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );


            User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));


            if (!user.getActive()) {
                throw new IllegalArgumentException("Usuario inactivo");
            }


            String token = jwtService.generateToken(user.getEmail(), user.getRole().getName());


            UserResponse userResponse = mapToUserResponse(user);
            
            log.info("Login exitoso para usuario: {}", user.getEmail());
            return new AuthResponse(token, userResponse);
            
        } catch (Exception e) {
            log.error("Error en login para usuario {}: {}", loginRequest.getEmail(), e.getMessage());
            throw new IllegalArgumentException("Credenciales inválidas");
        }
    }


    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        log.info("Iniciando registro de usuario: {}", registerRequest.getEmail());
        

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }


        String roleName = registerRequest.getRoleName() != null 
            ? registerRequest.getRoleName() 
            : "CUSTOMER";
            
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new IllegalArgumentException("Rol no válido: " + roleName));


        User user = User.builder()
            .name(registerRequest.getName())
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .role(role)
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();


        User savedUser = userRepository.save(user);
        
        log.info("Usuario registrado exitosamente: {}", savedUser.getEmail());
        return mapToUserResponse(savedUser);
    }


    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        log.info("Consultando usuario por ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
            
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Consultando usuario por email: {}", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + email));
            
        return mapToUserResponse(user);
    }


    @Override
    @Transactional
    public UserResponse updateUserStatus(UUID userId, Boolean isActive) {
        log.info("Actualizando estado de usuario ID {}: {}", userId, isActive);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
            
        user.setActive(isActive);
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        
        log.info("Estado actualizado para usuario: {}", updatedUser.getEmail());
        return mapToUserResponse(updatedUser);
    }


    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .roleName(user.getRole().getName())
            .isActive(user.getActive())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}