package ir.aut.ce.cloud.serverlessauthentication.endpoints;

import org.springframework.stereotype.Component;

import ir.aut.ce.cloud.serverlessauthentication.RequestContext;
import ir.aut.ce.cloud.serverlessauthentication.dtos.UserInquiryRequest;
import ir.aut.ce.cloud.serverlessauthentication.dtos.UserInquiryResponse;
import ir.aut.ce.cloud.serverlessauthentication.objectStorage.S3ObjectStorageService;
import ir.aut.ce.cloud.serverlessauthentication.repository.UserRepository;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.RequiredArgsConstructor;

@Component
@Path("/inquiry")
@RequiredArgsConstructor
public class InquiryEndpoint {
    private final UserRepository userRepository;
    private final RequestContext requestContext;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserInquiryResponse inquiryUserStatus(UserInquiryRequest request) {
        var target = userRepository.getReferenceById(request.nationalId());
        if (target == null) {
            var reponse = new UserInquiryResponse("Registration Request Not Found", null);
            throw new WebApplicationException(
                    Response.status(Status.NOT_FOUND).entity(reponse).build());
        }
        if (requestContext.getRequestIp() != target.getIp()) {
            var reponse = new UserInquiryResponse("Unauthorized Access", null);
            throw new WebApplicationException(
                    Response.status(Status.FORBIDDEN).entity(reponse).build());
        }

        if (target.getState() == "INFORMATION_REGISTERED") {
            return new UserInquiryResponse("Registration Request Is Still Pending", null);
        }
        if (target.getState() == "INFORMATION_REJECTED") {
            return new UserInquiryResponse("Registration Request Has Been Rejected", null);
        }
        if (target.getState() == "REGISTRATION_COMPLETED") {
            return new UserInquiryResponse("Registration Request Is Still Pending", target.getLastname());
        }

        throw new WebApplicationException(Status.NOT_IMPLEMENTED);

    }

}
