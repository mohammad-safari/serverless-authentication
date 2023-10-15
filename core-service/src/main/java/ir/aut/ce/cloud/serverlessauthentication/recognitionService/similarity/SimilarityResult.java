package ir.aut.ce.cloud.serverlessauthentication.recognitionService.similarity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SimilarityResult(@JsonProperty("score") String score) {
}