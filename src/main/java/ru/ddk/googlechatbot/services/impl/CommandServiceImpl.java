package ru.ddk.googlechatbot.services.impl;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ddk.googlechatbot.domain.Message;
import ru.ddk.googlechatbot.services.CommandService;
import ru.ddk.googlechatbot.services.FileService;
import ru.ddk.googlechatbot.services.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandServiceImpl implements CommandService {
    private final FileService fileService;
    private final MessageService messageService;

    public CommandServiceImpl(FileService fileService, MessageService messageService) {
        this.fileService = fileService;
        this.messageService = messageService;
    }

    @Override
    public Flux<Message> copyFile(List<Message> messageList) {
        // save new message and start copy
        return messageService
                .saveNewMessages(messageList)
                .flatMap(message -> {
                    if (message.getStatus().equals("B")) {
                        return Mono.just(message);
                    }

                    try {
                        if (fileService.copyFile(message.getInputValue().split(",")[0], message.getInputValue().split(",")[1])) {
                            message.setStatus("C");
                        } else {
                            message.setStatus("F");
                        }
                    } catch (Exception e) {
                        message.setStatus("F");
                        message.setInputValue(e.getMessage());
                    }
                    return messageService.save(message);
                })
                .log();
    }

    @Override
    public Flux<Map<String, String>> getInfoFile(List<Message> messageList) {
        return Flux.fromIterable(messageList)
                .flatMap(msg ->
                        {
                         Map <String, String> rz =
                                 new HashMap<>() {{
                                     put(msg.getInputValue(), fileService.getFileInfo(msg.getInputValue()));
                                 }};
                         return Mono.just(rz);
                       }
                )
                .log();
    }
}
