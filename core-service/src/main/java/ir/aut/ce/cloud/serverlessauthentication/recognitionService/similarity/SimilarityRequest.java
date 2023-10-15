package ir.aut.ce.cloud.serverlessauthentication.recognitionService.similarity;

import feign.form.FormProperty;

public record SimilarityRequest(@FormProperty("face_id") String faceId,
        @FormProperty("second_face_id") String secondFaceId) {
}