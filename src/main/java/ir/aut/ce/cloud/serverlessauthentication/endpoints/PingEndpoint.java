package ir.aut.ce.cloud.serverlessauthentication.endpoints;

import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;

@Component
@Path("/ping")
public class PingEndpoint {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping(Request request){
        return Response.ok().entity(Map.of("state", "available")).build();
    }
}
