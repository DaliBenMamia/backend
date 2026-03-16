package coifure.coif.authservice.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(max = 120)
        String nom,

        @NotBlank
        @Size(max = 120)
        String prenom,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(regexp = "^[+0-9]{8,20}$")
        String telephone,

        @NotBlank
        @Size(min = 8, max = 128)
        String password,

        @NotBlank
        @Pattern(regexp = "CLIENT|HAIRDRESSER|ADMIN")
        String role
) {
}
