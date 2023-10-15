package ir.aut.ce.cloud.serverlessauthentication.recognitionService.detection;

import feign.form.FormProperty;

public record DetectionRequest(@FormProperty("image_base64") String imageBase64,
        @FormProperty("return_face_id") int returnFaceId) {
}