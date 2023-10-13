package ir.aut.ce.cloud.serverlessauthentication.messageBroker;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ir.aut.ce.cloud.serverlessauthentication.message.RegistrsationMessage;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RabbitMQRegistrationProcessService {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @RabbitListener(queues = "${queues.registration-request.name}")
    public void notifyRequest(RegistrsationMessage message) {
        applicationEventPublisher.publishEvent(message);
    }

}
