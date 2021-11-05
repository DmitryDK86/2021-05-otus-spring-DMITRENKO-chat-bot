package ru.ddk.googlechatbot.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ddk.googlechatbot.domain.Message;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
    Flux<Message> findAllByStatus(String status);
    Mono<Void> deleteAllByStatusNot(String status);
    Mono<Boolean> existsByInputValueAndStatus(String inputValue, String status);
}
