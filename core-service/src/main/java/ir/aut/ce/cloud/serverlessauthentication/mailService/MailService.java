package ir.aut.ce.cloud.serverlessauthentication.mailService;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

public interface MailService {
    public CompletableFuture<?> sendRegistrationConfirmMail(MessageTemplate message);

}

@Configuration
class MailGunApiService implements MailService {
    @Value("${rest.mail.mailgun.api-key}")
    private String PRIVATE_API_KEY;
    @Value("${rest.mail.mailgun.domain}")
    private String DOMAIN_NAME;
    private MailgunMessagesApi messagesApi;

    @PostConstruct
    public void init() {
        this.messagesApi = MailgunClient.config(PRIVATE_API_KEY)
                .createApi(MailgunMessagesApi.class);
    }

    @Override
    public CompletableFuture<MessageResponse> sendRegistrationConfirmMail(MessageTemplate message) {
        return messagesApi.sendMessageAsync(DOMAIN_NAME, Message.builder()
                .from(message.from())
                .to(message.to())
                .subject(message.subject())
                .text(message.text())
                .build());
    }
}
@Component
@RequiredArgsConstructor
class SpringSmtpService implements MailService {
    private final JavaMailSender emailSender;

    @Override
    public CompletableFuture<?> sendRegistrationConfirmMail(MessageTemplate message) {
        var simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom(message.from());
        simpleMessage.setTo(message.to().toArray(new String[message.to().size()]));
        simpleMessage.setSubject(message.subject());
        simpleMessage.setText(message.text());
        emailSender.send(simpleMessage);
        return CompletableFuture.completedFuture(null);

    }
}
