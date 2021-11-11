package ru.ddk.googlechatbot.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@Document("message")
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    private String id;
    private Map<String, JsonNode> metaData;
    private String inputValue;
    private String status;

    public Message(Map<String, JsonNode> metaData, String inputValue, String status) {
        this.metaData = metaData;
        this.inputValue = inputValue;
        this.status = status;
    }
}
