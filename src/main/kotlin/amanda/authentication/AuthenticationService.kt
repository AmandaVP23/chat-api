package amanda.authentication

import amanda.users.UserRepository
import amanda.utils.CustomErrorException
import io.quarkus.elytron.security.common.BcryptUtil
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.BadRequestException
import java.time.LocalDateTime
import java.util.*

@ApplicationScoped
class AuthenticationService {
    @Inject
    lateinit var userRepository: UserRepository;

    fun login(data: LoginDTO): String {
        val user = userRepository.find("email", data.email).firstResult();

        if (user == null || !BcryptUtil.matches(data.password, user.password) ) {
            throw BadRequestException("Invalid Credentials");
        }

        val token = Token().generateToken(user.name, user.email);
        return token;
    }

    @Transactional
    fun forgotPassword(userData: RecoverPasswordRequestDTO) {
        val user = userRepository.find("email", userData.email).firstResult();

        println(user.toString())

        if (user == null) {
            return;
        }

        user.requestResetPassword();

        userRepository.persist(user);
    }

    @Transactional
    fun resetPassword(resetPasswordData: ResetPasswordRequestDTO): String?  {
        val user = userRepository.find("emailToken", resetPasswordData.token).firstResult();

        if (user == null) {
            throw BadRequestException();
        }

        if (LocalDateTime.now().isAfter(user.emailTokenExpireDate)) {
            throw CustomErrorException("Token invalid");
        }

        user.password = userRepository.encryptPassword(resetPasswordData.newPassword);
        user.emailToken = null;
        user.emailTokenExpireDate = null;
        userRepository.persist(user);

        return Token().generateToken(user.name, user.email);
    }
}