package ir.aut.ce.cloud.serverlessauthentication.endpoints;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.PutObjectResult;
import java.util.Base64;

import ir.aut.ce.cloud.serverlessauthentication.RequestContext;
import ir.aut.ce.cloud.serverlessauthentication.dtos.UserRegistrationRequest;
import ir.aut.ce.cloud.serverlessauthentication.dtos.UserRegistrationResponse;
import ir.aut.ce.cloud.serverlessauthentication.model.User;
import ir.aut.ce.cloud.serverlessauthentication.objectStorage.S3ObjectStorageService;
import ir.aut.ce.cloud.serverlessauthentication.repository.UserRepository;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.var;

@Component
@Configuration
@Path("/registration")
@RequiredArgsConstructor
public class RegisterEndpoint {

    private final S3ObjectStorageService objectStorage;
    private final UserRepository userRepository;
    private final RequestContext requestContext;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        var userImageKey = request.nationalId();
        var putResultList = putAllImages(request, userImageKey);
        storeUserRegistrationData(request, userImageKey);
        return processRegistrationResponse();
    }

    private UserRegistrationResponse processRegistrationResponse() {
        return new UserRegistrationResponse();
    }

    private void storeUserRegistrationData(UserRegistrationRequest request, String userImageKey) {
        userRepository.save(
                User.builder().email(request.email())
                        .lastname(request.lastname())
                        .nationalId(request.nationalId())
                        .state("INFORMATION_REGISTERED")
                        .imageKey(userImageKey)
                        .ip(requestContext.getRequestIp()).build());
    }

    private List<PutObjectResult> putAllImages(UserRegistrationRequest request, String userImageKey) {
        var decoder = Base64.getDecoder();
        var imageCounter = new AtomicInteger(0);
        return List.of(request.imageBase64String()).stream()
                .map(decoder::decode).map(ByteArrayInputStream::new)
                .map(stream -> objectStorage.putFileIntoS3Bucket(
                        processFullImageName(userImageKey, imageCounter.getAndIncrement()), stream))
                .collect(Collectors.toList());
    }

    private String processFullImageName(String userImageKey, int no) {
        return userImageKey + String.format("/image%d", no);
    }
}
