package stroom.security.server;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import stroom.security.SecurityContext;

import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(
        value = "authorisation - /v1",
        description = "Stroom Authorisation API")
@Path("/authorisation/v1")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class AuthorisationResource {
    private final SecurityContext securityContext;

    @Inject
    public AuthorisationResource(final SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    /**
     * Authenticates using JWT
     */
    @POST
    @Path("isAuthorised")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "Submit a request to verify if the user has the requested permission on a 'document'",
        response = Response.class)
    public Response isAuthorised(@ApiParam("AuthorisationRequest") final AuthorisationRequest authorisationRequest) {

        boolean result = securityContext.hasDocumentPermission(
            authorisationRequest.getDocRef().getType(),
            authorisationRequest.getDocRef().getUuid(),
            authorisationRequest.getPermission());

        return result
            ? Response
            .ok()
            .build()
            : Response
            .status(Response.Status.UNAUTHORIZED)
            .build();
    }

    @POST
    @Path("canManageUsers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response canManageUsers(UserPermissionRequest userPermissionRequest) {
        // TODO what happens if the permission is bad? What's the result of this method call and how should we handle it?
        boolean result = securityContext.hasAppPermission(userPermissionRequest.getPermission());
        // The user here will be the one logged in by the JWT.
        return result ? Response.ok().build() : Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
