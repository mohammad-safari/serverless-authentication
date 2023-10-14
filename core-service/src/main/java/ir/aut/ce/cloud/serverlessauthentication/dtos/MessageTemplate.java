package ir.aut.ce.cloud.serverlessauthentication.dtos;

import java.util.List;

public record MessageTemplate(String from, List<String> to, String subject, String text) {

}
