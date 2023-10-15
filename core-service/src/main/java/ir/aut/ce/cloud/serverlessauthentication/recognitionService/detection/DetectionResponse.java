package ir.aut.ce.cloud.serverlessauthentication.recognitionService.detection;

import com.fasterxml.jackson.annotation.JsonProperty;

import ir.aut.ce.cloud.serverlessauthentication.recognitionService.ServiceStatus;

public record DetectionResponse(@JsonProperty("result") DetectionResult result,
        @JsonProperty("status") ServiceStatus status) {
}