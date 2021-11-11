package ru.ddk.googlechatbot.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public interface ResponseService {
    void postResponse(JsonNode eventJson, ObjectNode responseNode) throws IOException;
    void process(JsonNode eventJson) throws Exception;
}
