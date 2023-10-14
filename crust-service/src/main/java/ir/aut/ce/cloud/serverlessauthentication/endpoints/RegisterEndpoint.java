package ir.aut.ce.cloud.serverlessauthentication.endpoints;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.PutObjectResult;

import ir.aut.ce.cloud.serverlessauthentication.RequestContext;
import ir.aut.ce.cloud.serverlessauthentication.common.Encryption;
import ir.aut.ce.cloud.serverlessauthentication.dtos.UserRegistrationRequest;
import ir.aut.ce.cloud.serverlessauthentication.dtos.UserRegistrationResponse;
import ir.aut.ce.cloud.serverlessauthentication.message.RegistrsationMessage;
import ir.aut.ce.cloud.serverlessauthentication.messageBroker.RabbitMQRegistrationRequestService;
import ir.aut.ce.cloud.serverlessauthentication.model.User;
import ir.aut.ce.cloud.serverlessauthentication.objectStorage.S3ObjectStorageService;
import ir.aut.ce.cloud.serverlessauthentication.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@Path("/registration")
@RequiredArgsConstructor
public class RegisterEndpoint {

    private static final String ADJECTIVE_KEY = "ADJECTIVES";
    private static final String NOUN_KEY = "NOUNS";
    private static final String ADJECTIVES_FILENAME = "adjective.txt";
    private static final String NOUNS_FILENAME = "noun.txt";
    private final RabbitMQRegistrationRequestService registrationRequestServiceMQ;
    private final S3ObjectStorageService objectStorage;
    private final UserRepository userRepository;
    private final RequestContext requestContext;
    private final Encryption encryption;

    private Map<String, List<String>> dictionary; // ENUM MAP

    @SneakyThrows
    @PostConstruct
    public void buildDictionary() {
        dictionary = new HashMap<>();
        try (var reader = new BufferedReader(
                new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(NOUNS_FILENAME)))) {
            dictionary.put(NOUN_KEY, reader.lines().collect(Collectors.toList()));
        }
        try (var reader = new BufferedReader(
                new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(ADJECTIVES_FILENAME)))) {
            dictionary.put(ADJECTIVE_KEY, reader.lines().collect(Collectors.toList()));
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        var generatedUsername = generateUsernameByAdjectiveNoun(dictionary.get(NOUN_KEY),
                dictionary.get(ADJECTIVE_KEY));
        var putResultList = putAllImages(request, generatedUsername);
        storeUserRegistrationData(request, generatedUsername);
        postRequestMessage(generatedUsername);
        return processRegistrationResponse();
    }

    private UserRegistrationResponse processRegistrationResponse() {
        return new UserRegistrationResponse();
    }

    private void postRequestMessage(String username) {
        registrationRequestServiceMQ.postRegitrsationRequestMessage(
                RegistrsationMessage.builder().registrationKey(username).build());
    }

    private void storeUserRegistrationData(UserRegistrationRequest request, String username) {
        userRepository.save(
                User.builder().username(username)
                        .email(request.email())
                        .lastname(request.lastname())
                        .nationalId(encryption.encrypt(request.nationalId()))
                        .state("INFORMATION_REGISTERED")
                        .imageKey(username)
                        .ip(requestContext.getRequestIp()).build());
    }

    private List<PutObjectResult> putAllImages(UserRegistrationRequest request, String registrationMessageKey) {
        var decoder = Base64.getDecoder();
        var imageCounter = new AtomicInteger(0);
        return List.of(request.imageBase64String()).stream()
                .map(decoder::decode).map(ByteArrayInputStream::new)
                .map(stream -> objectStorage.putFileIntoS3Bucket(
                        processFullImageName(registrationMessageKey, imageCounter.getAndIncrement()), stream))
                .collect(Collectors.toList());
    }

    private String processFullImageName(String registrationKey, int no) {
        return registrationKey + String.format("/image%d", no);
    }

    private String generateUsernameByAdjectiveNoun(List<String> nouns, List<String> adjectives) {
        var rand = new Random();
        var nounsCopy = new ArrayList<>(nouns);
        var adjectivesCopy = new ArrayList<>(adjectives);
        Collections.shuffle(nounsCopy);
        Collections.shuffle(adjectivesCopy);
        return new StringBuilder().append(adjectivesCopy.get(rand.nextInt(adjectivesCopy.size())))
                .append("-").append(nounsCopy.get(rand.nextInt(nounsCopy.size())))
                .append("-").append(rand.nextInt()).toString();
    }
}
