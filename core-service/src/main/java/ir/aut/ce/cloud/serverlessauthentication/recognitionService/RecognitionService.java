package ir.aut.ce.cloud.serverlessauthentication.recognitionService;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import ir.aut.ce.cloud.serverlessauthentication.recognitionService.detection.DetectionRequest;
import ir.aut.ce.cloud.serverlessauthentication.recognitionService.detection.DetectionResponse;
import ir.aut.ce.cloud.serverlessauthentication.recognitionService.similarity.SimilarityRequest;
import ir.aut.ce.cloud.serverlessauthentication.recognitionService.similarity.SimilarityResponse;
import jakarta.annotation.PostConstruct;

@Headers({ "Authorization: Basic {authHeader}" })
interface ImaggaClient {

    @RequestLine("POST /faces/detections")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    DetectionResponse detectFaces(@Param("image_base64") String base64Image,
            @Param("return_face_id") int returnFaceId, @Param("authHeader") String authHeader);

    @RequestLine("GET /faces/similarity?face_id={faceId}&second_face_id={secondFaceId}")
    SimilarityResponse compareFaces(@Param("faceId") String faceId,
            @Param("secondFaceId") String secondFaceId,
            @Param("authHeader") String authHeader);
}

@Component
public class RecognitionService {
    private static final String BASE_URL = "https://api.imagga.com/v2";

    @Value("${recognition.image.imagga.api-key}")
    private String API_KEY;
    @Value("${recognition.image.imagga.api-secret}")
    private String API_SECRET;

    private ImaggaClient client;
    private String authHeader;

    @PostConstruct
    public void init() {
        this.client = Feign.builder()
                .encoder(new FormEncoder())
                .decoder(new JacksonDecoder())
                .target(ImaggaClient.class, BASE_URL);
        this.authHeader = Base64.getEncoder().encodeToString((API_KEY + ":" + API_SECRET).getBytes());
    }

    public DetectionResponse detectFace(DetectionRequest request) {
        return client.detectFaces(request.imageBase64(), request.returnFaceId(), authHeader);
    }

    public SimilarityResponse compareFaces(SimilarityRequest request) {
        return client.compareFaces(request.faceId(), request.secondFaceId(), authHeader);
    }

}
