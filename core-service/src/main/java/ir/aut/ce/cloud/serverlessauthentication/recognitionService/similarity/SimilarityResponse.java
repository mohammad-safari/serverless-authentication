package ir.aut.ce.cloud.serverlessauthentication.recognitionService.similarity;

import com.fasterxml.jackson.annotation.JsonProperty;

import ir.aut.ce.cloud.serverlessauthentication.recognitionService.ServiceStatus;

public record SimilarityResponse(@JsonProperty("result") SimilarityResult result,
        @JsonProperty("status") ServiceStatus status) {
}
