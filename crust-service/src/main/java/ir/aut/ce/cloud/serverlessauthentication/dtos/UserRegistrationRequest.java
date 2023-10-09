package ir.aut.ce.cloud.serverlessauthentication.dtos;

public record UserRegistrationRequest(
        String email, String lastname, String nationalId, String[] imageBase64String) {
}
