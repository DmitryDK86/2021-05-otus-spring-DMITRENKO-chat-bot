package ru.ddk.googlechatbot.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Objects;

@Configuration
@ConfigurationProperties(prefix = "bot")
@Data
public class BotConfig {
    private String credentialsPathEnvProperty;

    // Google Cloud Project ID
    private String projectId;

    // Cloud Pub/Sub Subscription ID
    private String subscriptionId;

    // Developer code for Google Chat api scope.
    private String googleChatApiScope;

    // Response URL Template with placeholders for space id.
    private String responseUrlTemplate;

    private String responseAdded;

    // Response echo message template.
    private String responseTemplate;

    @Bean
    public GoogleCredential credential() throws IOException {
        return GoogleCredential.fromStream(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(getCredentialsPathEnvProperty())))
                .createScoped(Collections.singleton(getGoogleChatApiScope()));
    }

    @Bean
    public HttpRequestFactory requestFactory() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return httpTransport.createRequestFactory(credential());
    }
}
