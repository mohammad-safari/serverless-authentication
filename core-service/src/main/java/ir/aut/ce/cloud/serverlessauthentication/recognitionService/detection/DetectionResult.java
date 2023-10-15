package ir.aut.ce.cloud.serverlessauthentication.recognitionService.detection;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DetectionResult(@JsonProperty("faces") List<Face> faces) {
}