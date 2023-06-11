package amanda.authentication

data class ResetPasswordRequestDTO(val token: String, val newPassword: String)
