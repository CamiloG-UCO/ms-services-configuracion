package co.edu.hotel.configuracionservice.services.user;

import co.edu.hotel.configuracionservice.domain.user.dto.AuthResponse;
import co.edu.hotel.configuracionservice.domain.user.dto.LoginRequest;
import co.edu.hotel.configuracionservice.domain.user.dto.RegisterRequest;
import co.edu.hotel.configuracionservice.domain.user.dto.UserResponse;

import java.util.UUID;


public interface IUserService {


    AuthResponse login(LoginRequest loginRequest);

    UserResponse register(RegisterRequest registerRequest);

    UserResponse getUserById(UUID userId);

    UserResponse getUserByEmail(String email);

    UserResponse updateUserStatus(UUID userId, Boolean isActive);
}