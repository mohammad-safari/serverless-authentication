package ir.aut.ce.cloud.serverlessauthentication;

import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ir.aut.ce.cloud.serverlessauthentication.mailService.MailService;
import ir.aut.ce.cloud.serverlessauthentication.mailService.MessageTemplate;
import ir.aut.ce.cloud.serverlessauthentication.message.RegistrsationMessage;
import ir.aut.ce.cloud.serverlessauthentication.model.User;
import ir.aut.ce.cloud.serverlessauthentication.objectStorage.S3ObjectStorageService;
import ir.aut.ce.cloud.serverlessauthentication.recognitionService.RecognitionService;
import ir.aut.ce.cloud.serverlessauthentication.recognitionService.detection.DetectionRequest;
import ir.aut.ce.cloud.serverlessauthentication.recognitionService.detection.DetectionResponse;
import ir.aut.ce.cloud.serverlessauthentication.recognitionService.similarity.SimilarityRequest;
import ir.aut.ce.cloud.serverlessauthentication.recognitionService.similarity.SimilarityResponse;
import ir.aut.ce.cloud.serverlessauthentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueuedRequestProcessor {
    private static final int SIMILARITY_THRESHOLD = 80;
    private static final String MAIL_FROM = "postmasters@ce.aut.ac.ir";
    private static final String CONFIRM_MAIL_SUBJECT = "Your registration has been confirmed";
    private static final String FAILURE_MAIL_SUBJECT = "Your registration information has been rejected!";
    // public final MailService mailGunApiService;
    public final MailService springSmtpService;
    public final UserRepository userRepository;
    public final RecognitionService recognitionService;
    public final S3ObjectStorageService storageService;

    @EventListener
    public void process(RegistrsationMessage message) {
        log.info("recieved request " + message.getRegistrationKey());
        var target = userRepository.getReferenceById(message.getRegistrationKey());
        if (target == null) {
            log.warn("user " + message.getRegistrationKey() + " was not found!");
            return;
        }
        Function<InputStream, byte[]> streamToByte = a -> {
            try {
                return a.readAllBytes();
            } catch (Exception e) {
                return new byte[0];
            }
        };
        var encoder = Base64.getEncoder();
        var imageBase64 = List.of(storageService.getFileFromS3Bucket(message.getRegistrationKey() + "/image0"),
                storageService.getFileFromS3Bucket(message.getRegistrationKey() + "/image1")).stream()
                .map(streamToByte)
                .map(encoder::encodeToString)
                .collect(Collectors.toList());
        // detection
        var responses = imageBase64.stream()
                .map(bi -> recognitionService.detectFace(new DetectionRequest(bi, 1)))
                .toList();
        Predicate<DetectionResponse> detectionPredicate = r -> r.result().faces().size() > 0;
        var faceExist = responses.stream().allMatch(detectionPredicate);
        if (!faceExist) {
            onNoFaceFound(target);
            return;
        }
        // similarity
        Predicate<SimilarityResponse> comparisonPredicate = r -> Double
                .parseDouble(r.result().score()) >= SIMILARITY_THRESHOLD;
        var similarityResponse = recognitionService.compareFaces(new SimilarityRequest(
                responses.getFirst().result().faces().getFirst().faceId(),
                responses.getLast().result().faces().getFirst().faceId()));
        var isSimilar = comparisonPredicate.test(similarityResponse);
        if (!isSimilar) {
            onNoSimilarity(target);
            return;
        }
        onConfirm(target);
    }

    private void onConfirm(User target) {
        target.setState("REGISTRATION_COMPLETED");
        userRepository.save(target);
        springSmtpService.sendRegistrationConfirmMail(
                new MessageTemplate(MAIL_FROM,
                        List.of(target.getEmail()),
                        CONFIRM_MAIL_SUBJECT,
                        String.format("Welcome %s, your username is %s",
                                target.getLastname(), target.getUsername())));
    }

    private void onNoSimilarity(User target) {
        target.setState("INFORMATION_REJECTED");
        userRepository.save(target);
        springSmtpService.sendRegistrationConfirmMail(
                new MessageTemplate(MAIL_FROM,
                        List.of(target.getEmail()),
                        FAILURE_MAIL_SUBJECT,
                        String.format(
                                "Sorry %s, the sent images were not similar, you can try again any time",
                                target.getLastname())));
    }

    private void onNoFaceFound(User target) {
        target.setState("INFORMATION_REJECTED");
        userRepository.save(target);
        springSmtpService.sendRegistrationConfirmMail(
                new MessageTemplate(MAIL_FROM,
                        List.of(target.getEmail()),
                        FAILURE_MAIL_SUBJECT,
                        String.format(
                                "Sorry %s, one of the images didnt contain a face, you can try again any time",
                                target.getLastname())));
    }

}
