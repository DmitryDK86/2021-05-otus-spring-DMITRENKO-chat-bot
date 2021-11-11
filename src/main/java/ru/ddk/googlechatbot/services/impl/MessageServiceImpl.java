package ru.ddk.googlechatbot.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ddk.googlechatbot.domain.Message;
import ru.ddk.googlechatbot.repository.MessageRepository;
import ru.ddk.googlechatbot.services.MessageService;

import java.util.HashMap;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class.getName());

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Message> findAllByStatus(String status) {
        return messageRepository.findAllByStatus(status);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<Message> save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<Void> deleteAllByStatusNot(String status) {
        return messageRepository.deleteAllByStatusNot(status);
    }

    @Override
    public Flux<Message> saveAll(List<Message> messageList) {
        return messageRepository.saveAll(messageList);
    }

    @Override
    public Mono<Boolean> existsByInputValueAndStatus(String inputValue, String status) {
        return messageRepository.existsByInputValueAndStatus(inputValue, status);
    }

    @Override
    public Flux<Message> saveNewMessages(List<Message> messageList) {
        return Flux.fromIterable(messageList)
                .flatMap(msg -> existsByInputValueAndStatus(msg.getInputValue(), "W")
                        .flatMap(isProgress -> {
                            if (isProgress) {
                                msg.setStatus("B");
                                return Mono.just(msg);
                            } else
                                return save(msg);
                        })
                )
                .log();
    }
}
