package amanda.authentication

import amanda.utils.ErrorMessage
import io.quarkus.vertx.web.Body
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.Claims
import org.eclipse.microprofile.jwt.JsonWebToken

@Path("/authentication")
class AuthenticationResource {
    @Inject
    lateinit var jwt: JsonWebToken;

    @Inject
    lateinit var authenticationService: AuthenticationService;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    fun login(@Valid loginData: LoginDTO): Response {
        return try {
            val token = authenticationService.login(loginData);

            Response.ok(AuthenticationResponse(token)).build();
        } catch (e: BadRequestException) {
            Response.status(Response.Status.BAD_REQUEST).entity(ErrorMessage("Invalid credentials")).build();
        }
    }

    @GET
    @Path("/test")
    @RolesAllowed( "USER", "ADMIN" )
    @Produces(MediaType.TEXT_PLAIN)
    fun test(@Context ctx: SecurityContext): String {
        println("Claim:")
        println(jwt.getClaim(Claims.email.name) as String)
        println(ctx.userPrincipal)
        return "Hello User!"
    }

    @POST
    @Path("/forgot-password")
    @Consumes(MediaType.APPLICATION_JSON)
    fun forgotPassword(@Body data: RecoverPasswordRequestDTO): Response {
        authenticationService.forgotPassword(data)
        return Response.ok().build();
    }

    @POST
    @Path("/reset-password")
    @Consumes(MediaType.APPLICATION_JSON)
    fun resetPassword(@Body resetPasswordData: ResetPasswordRequestDTO): Response {
        authenticationService.resetPassword(resetPasswordData);
        return Response.ok().build();
    }
}
