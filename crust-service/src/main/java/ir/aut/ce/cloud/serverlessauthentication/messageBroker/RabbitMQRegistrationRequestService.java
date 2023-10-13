package ir.aut.ce.cloud.serverlessauthentication.messageBroker;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import ir.aut.ce.cloud.serverlessauthentication.message.RegistrsationMessage;
import lombok.NoArgsConstructor;

@Configuration
@NoArgsConstructor
public class RabbitMQRegistrationRequestService {

    @Value("${queues.registration-request.name}")
    private String registrationRequestQueueName;
    @Value("${queues.registration-request.durability}")
    private Boolean registrationRequestQueueDurabilty;
    private RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public Queue registrationRequestQueue() {
        return new Queue(registrationRequestQueueName, registrationRequestQueueDurabilty);
    }

    @Lazy
    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate; 
    }

    public void postRegitrsationRequestMessage(RegistrsationMessage message) {
        rabbitTemplate.convertAndSend(registrationRequestQueueName, message);
    }
}
