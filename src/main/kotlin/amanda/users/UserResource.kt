package amanda.users

import io.quarkus.vertx.web.Body
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.sql.SQLIntegrityConstraintViolationException

@Path("/users")
class UserResource {
    @Inject
    lateinit var userRepository: UserRepository;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun getUsers(): List<User> {
        return userRepository.listAll();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun add(@Body user: UserDTO): Response {
        return try {
            val createdUser = userRepository.createUser(user);
            Response.ok(createdUser).build();
        } catch (exception: SQLIntegrityConstraintViolationException) {
            Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
