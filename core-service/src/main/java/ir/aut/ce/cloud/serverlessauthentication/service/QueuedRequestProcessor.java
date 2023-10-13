package ir.aut.ce.cloud.serverlessauthentication.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ir.aut.ce.cloud.serverlessauthentication.message.RegistrsationMessage;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QueuedRequestProcessor {
    @EventListener
    public void process(RegistrsationMessage message) {
        log.info("recieved id " + message.getRegistrationKey());
    }

}
