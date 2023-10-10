package ir.aut.ce.cloud.serverlessauthentication.message;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrsationMessage implements Serializable {
    String registrationKey;
}
