package ir.aut.ce.cloud.serverlessauthentication.messageBroker;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Configuration
@RequiredArgsConstructor
public class RabbitMQRegistrationProcessService {
    @Value("${queues.registration-request.name}")
    private String RegistrationRequestQueueName;
    @Value("${queues.registration-request.durability}")
    private Boolean RegistrationRequestQueueDurabilty;
    private final RabbitTemplate rabbitTemplate;

    @Bean
    public Queue registrationRequestQueue() {
        return new Queue(RegistrationRequestQueueName, RegistrationRequestQueueDurabilty);
    }

}
