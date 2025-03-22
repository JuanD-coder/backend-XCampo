package com.rojas.dev.XCampo.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // Constructor vacío necesario para Jackson
@AllArgsConstructor // Constructor con todos los argumentos (opcional)
public class RegisterRequest {
    String name;
    String department;
    String city;
    Long cell;
    String email;
    String password;
}
