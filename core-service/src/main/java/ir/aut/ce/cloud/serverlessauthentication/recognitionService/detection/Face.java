package ir.aut.ce.cloud.serverlessauthentication.recognitionService.detection;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Face(@JsonProperty("confidence") Double confidence,
        @JsonProperty("coordinates") Map<String, Integer> coordinates, @JsonProperty("face_id") String faceId) {
}