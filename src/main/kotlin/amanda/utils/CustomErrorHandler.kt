package amanda.utils

import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class CustomErrorHandler: ExceptionMapper<CustomErrorException> {
    override fun toResponse(e: CustomErrorException?): Response {
        return if (e != null) {
            Response.status(Response.Status.BAD_REQUEST).entity(ErrorMessage(e.message)).build();
        } else {
            Response.status(Response.Status.BAD_REQUEST).entity(ErrorMessage("Something went wrong!")).build();
        }
    }
}