package amanda.authentication

import jakarta.validation.constraints.NotBlank

class ResetPasswordRequestDTO {
    @NotBlank
    lateinit var token: String;

    @NotBlank
    lateinit var newPassword: String;
}
