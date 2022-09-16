package com.posada.santiago.alphapostsandcomments.application.adapters.bus;

import co.com.sofka.domain.generic.DomainEvent;
import com.google.gson.Gson;
import com.posada.santiago.alphapostsandcomments.application.adapters.repository.MongoEventStoreRepository;
import com.posada.santiago.alphapostsandcomments.application.config.RabbitConfig;
import com.posada.santiago.alphapostsandcomments.business.gateways.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqEventBus implements EventBus {

    private final static Logger logger= LoggerFactory.getLogger(RabbitMqEventBus.class);
    private final RabbitTemplate rabbitTemplate;
    private final Gson gson = new Gson();

    public RabbitMqEventBus(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(DomainEvent event) {
        var notification = new Notification(
                event.getClass().getTypeName(),
                gson.toJson(event)
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE, RabbitConfig.GENERAL_ROUTING_KEY, notification.serialize().getBytes()
        );
        logger.info("Message sent successfully from alpha");
    }

    @Override
    public void publishError(Throwable errorEvent) {
        var event = new ErrorEvent(errorEvent.getClass().getTypeName(), errorEvent.getMessage());
        var notification = new Notification(
                event.getClass().getTypeName(),
                gson.toJson(event)
        );
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.GENERAL_ROUTING_KEY, notification.serialize().getBytes());
        logger.error("The message couldnt be sent");
    }
}
