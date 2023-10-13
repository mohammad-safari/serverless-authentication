package ir.aut.ce.cloud.serverlessauthentication.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrsationMessage {
    String registrationKey;
}
