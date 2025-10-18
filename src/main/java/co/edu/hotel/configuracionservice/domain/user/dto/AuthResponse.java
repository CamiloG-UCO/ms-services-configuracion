package co.edu.hotel.configuracionservice.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {


    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private UserResponse user;

    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.tokenType = "Bearer";
        this.user = user;
    }
}