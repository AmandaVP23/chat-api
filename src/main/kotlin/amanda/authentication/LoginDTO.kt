package amanda.authentication

import jakarta.validation.constraints.NotBlank

//data class LoginDTO (
//    val email: String,
//    val password: String
//)

class LoginDTO {
    @NotBlank
    lateinit var email: String;

    @NotBlank
    lateinit var password: String;
}
