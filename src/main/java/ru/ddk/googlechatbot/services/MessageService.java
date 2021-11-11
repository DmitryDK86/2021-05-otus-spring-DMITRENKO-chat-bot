package ru.ddk.googlechatbot.services;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ddk.googlechatbot.domain.Message;

import java.util.List;

public interface MessageService {
    Flux<Message> findAll();

    Flux<Message> findAllByStatus(String status);

    Mono<Message> save(Message message);

    Mono<Void> deleteAllByStatusNot(String status);

    Flux<Message> saveAll(List<Message> messageList);

    Mono<Boolean> existsByInputValueAndStatus(String inputValue, String status);

    Flux<Message> saveNewMessages(List<Message> messageList);
}
