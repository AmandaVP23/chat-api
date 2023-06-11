package amanda.users

import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.security.SecureRandom

@ApplicationScoped
class UserRepository: PanacheRepository<User> {
    fun encryptPassword(password: String): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)

        return BcryptUtil.bcryptHash(password, 5, salt);
    }

    @Transactional
    fun createUser(user: UserDTO): User {
        val newUser = User();
        newUser.email = user.email;
        newUser.name = user.name;
        newUser.password = this.encryptPassword(user.password);

        persist(newUser);
        return newUser;
    }
}