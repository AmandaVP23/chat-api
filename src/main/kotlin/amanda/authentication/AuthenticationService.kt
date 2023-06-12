package amanda.authentication

import amanda.users.UserRepository
import amanda.utils.CustomErrorException
import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.mailer.Mail
import io.quarkus.mailer.MailTemplate
import io.quarkus.mailer.Mailer
import io.quarkus.qute.Location
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.BadRequestException
import java.io.File
import java.time.LocalDateTime
import java.util.*


@ApplicationScoped
class AuthenticationService {
    @Inject
    lateinit var userRepository: UserRepository;

    @Inject
    lateinit var mailer: Mailer;

    @Inject
    @Location("forgotPassword")
    lateinit var forgotPasswordTemplate: MailTemplate;

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

        if (user == null) {
            return;
        }

        user.requestResetPassword();
        //mailer.send(Mail.withText(user.email, "Reset password", "This is my body."));

        forgotPasswordTemplate.to(user.email)
            .subject("Reset password")
            .send()
            .subscribeAsCompletionStage();

        /*
            return hello.to("to@acme.org")
           .subject("Hello from Qute template")
           .data("name", "John")
           .send()
         */

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