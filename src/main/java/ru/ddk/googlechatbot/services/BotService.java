package ru.ddk.googlechatbot.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BotService implements MessageReceiver {
    private static final Logger logger = LoggerFactory.getLogger(BotService.class.getName());

    private final ResponseService responseService;

    public BotService(ResponseService responseService) {
        this.responseService = responseService;
    }

    @Override
    public void receiveMessage(PubsubMessage pubsubMessage, AckReplyConsumer consumer) {
        logger.info("Id : " + pubsubMessage.getMessageId());
        Mono.fromRunnable(() -> processMessage(pubsubMessage, consumer)).log().subscribe();
    }

    private void processMessage(PubsubMessage pubsubMessage, AckReplyConsumer consumer) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataJson = mapper.readTree(pubsubMessage.getData().toStringUtf8());
            logger.info("Data : " + dataJson.toString());
            responseService.process(dataJson);
        } catch (Exception e) {
            logger.error(String.valueOf(e));
        }
        consumer.ack();
    }
}
