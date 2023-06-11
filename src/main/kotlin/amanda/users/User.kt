package amanda.users

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_seq", allocationSize = 1)
    var id: Long? = null;

    lateinit var name: String;

    @Column(unique = true)
    lateinit var email: String;

    lateinit var password: String;

    @JsonIgnore
    var emailToken: String? = null;

    @JsonIgnore
    var emailTokenExpireDate: LocalDateTime? = null;

    fun requestResetPassword() {
        val token = UUID.randomUUID().toString().replace("-", "");
        this.emailToken = token;
        this.emailTokenExpireDate = LocalDateTime.now().plusMinutes(20);
    }
}