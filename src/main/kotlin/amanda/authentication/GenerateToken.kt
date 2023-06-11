package amanda.authentication

import amanda.users.UserRole
import io.smallrye.jwt.build.Jwt
import org.eclipse.microprofile.jwt.Claims

class Token {
    fun generateToken(name: String, email: String): String {
        val rolesSet = arrayOf(UserRole.USER.name, UserRole.ADMIN.name).toHashSet()
        val token: String = Jwt.issuer("https://example.com/issuer")
            .upn(email)
            .groups(rolesSet)
            .expiresAt(currentTimeInSecs() + (10 * 60))
            .claim(Claims.email.name, email)
            .sign();

        println(Claims.email.name)

        return token;
    }

    fun currentTimeInSecs(): Long {
        val currentTimeMS = System.currentTimeMillis();
        return currentTimeMS / 1000;
    }
}
