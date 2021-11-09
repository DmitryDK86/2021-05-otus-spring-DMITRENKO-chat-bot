package ru.ddk.googlechatbot.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.http.*;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ddk.googlechatbot.config.BotConfig;
import ru.ddk.googlechatbot.domain.Message;
import ru.ddk.googlechatbot.enumerable.StatusValue;
import ru.ddk.googlechatbot.services.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResponseServiceImpl implements ResponseService {

    private static final Logger logger = LoggerFactory.getLogger(ResponseServiceImpl.class.getName());
    private final BotConfig botConfig;
    private final HttpRequestFactory requestFactory;
    private final ParserMessageService parserMessageService;
    private final CommandService commandService;

    public ResponseServiceImpl(BotConfig botConfig, HttpRequestFactory requestFactory, ParserMessageService parserMessageService, CommandService commandService) {
        this.botConfig = botConfig;
        this.requestFactory = requestFactory;
        this.parserMessageService = parserMessageService;
        this.commandService = commandService;
    }

    @Override
    public void postResponse(JsonNode eventJson, ObjectNode responseNode) throws IOException {
        logger.info("postResponse");
        String URI =
                botConfig.getResponseUrlTemplate().replaceFirst(
                        "__SPACE_ID__", eventJson.get("space").get("name").asText());
        GenericUrl url = new GenericUrl(URI);

        HttpContent content =
                new ByteArrayContent("application/json", responseNode.toString().getBytes("UTF-8"));
        HttpRequest request = requestFactory.buildPostRequest(url, content);
        com.google.api.client.http.HttpResponse response = request.execute();
        logger.info(String.format("%s", response.getStatusCode()));
    }

    @Override
    public void process(JsonNode eventJson) throws Exception {
        logger.info("process");
        JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
        ObjectNode responseNode = jsonNodeFactory.objectNode();
        // Construct the response depending on the event received.
        String eventType = eventJson.get("type").asText();
        switch (eventType) {
            case "ADDED_TO_SPACE":
                responseNode.put("text", botConfig.getResponseAdded());
                if (!eventJson.has("message")) {
                    break;
                }
            case "MESSAGE":
                // In case of message, post the response in the same thread.
                ObjectNode threadNode = jsonNodeFactory.objectNode();
                threadNode.put("name", eventJson.get("message").get("thread").get("name").asText());
                responseNode.set("thread", threadNode);
                break;
            case "REMOVED_FROM_SPACE":
                responseNode.put("text", "Good luck!");
                if (!eventJson.has("message")) {
                    break;
                }
            default:
                // Do nothing
                return;
        }

        try {
            Pair<String, HashMap<String, String>> parseMessage = parserMessageService.parseMessage(eventJson.get("message").get("text").asText());
            responseNode.put("text", parseMessage.getValue1().get("value"));
            postResponse(eventJson, responseNode);

            if (parseMessage.getValue0().contains("copy") || parseMessage.getValue0().contains("file_info")) {
                List<Message> messageList = parseMessage.getValue1().entrySet()
                        .stream().filter(m -> !m.getKey().contains("value"))
                        .map(pathMap -> new Message(new HashMap<>() {{put("metadata", eventJson);}}, pathMap.getValue(), "W"))
                        .collect(Collectors.toList());

                if(parseMessage.getValue0().contains("copy")) {
                    commandService.copyFile(messageList)
                            .subscribe(r ->
                                    {
                                        responseNode.put("text", formatAnswer(r.getInputValue(), r.getStatus()));
                                        try {
                                            postResponse(eventJson, responseNode);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                            );
                }

                if(parseMessage.getValue0().contains("file_info")) {
                    commandService.getInfoFile(messageList)
                            .subscribe(r ->
                                    {
                                        responseNode.put("text", formatAnswer(
                                                String.format("%s %s",
                                                r.keySet().stream().findFirst().orElse("error_key"),
                                                r.values().stream().findFirst().orElse("error_value")) , "C"));
                                        try {
                                            postResponse(eventJson, responseNode);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                            );
                }

                // clear db
                //messageService.deleteAllByStatusNot("W").log().subscribe();
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            responseNode.put("text", "произошла ошибка - " + e);
            postResponse(eventJson, responseNode);
        }
    }

    private String formatAnswer(String message, String status)
    {
        switch (status){
            case "C":
            case "B":
            case "W":
                message = String.format("```\n %s \n %s \n```", StatusValue.valueOf(status).getText(), message.replace(",", " -> "));
                break;
            case "F":
                message = String.format("``` %s \n %s ```", StatusValue.valueOf(status).getText(), message);
                break;
            default:
                message = String.format("``` %s ```", message);
                break;
        }

        return message;
    }

}
