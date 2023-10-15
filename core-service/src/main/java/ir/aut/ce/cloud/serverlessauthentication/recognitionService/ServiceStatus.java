package ir.aut.ce.cloud.serverlessauthentication.recognitionService;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ServiceStatus(@JsonProperty("text") String text, @JsonProperty("type") String type) {
}