package ru.ddk.googlechatbot.services;

import reactor.core.publisher.Flux;
import ru.ddk.googlechatbot.domain.Message;

import java.util.List;
import java.util.Map;

public interface CommandService {
    Flux<Message> copyFile(List<Message> messageList);
    Flux<Map<String, String>> getInfoFile(List<Message> messageList);
}
