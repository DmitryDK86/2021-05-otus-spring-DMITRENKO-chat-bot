package ru.ddk.googlechatbot.services;

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.ddk.googlechatbot.config.BotConfig;

@Component
class CommandLineRunnerImpl implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CommandLineRunnerImpl.class.getName());

    private final BotConfig botConfig;
    private final BotService botService;

    public CommandLineRunnerImpl(BotConfig botConfig, BotService botService) {
        this.botConfig = botConfig;
        this.botService = botService;
    }

    @Override
    public void run(String... args) throws Exception {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(botConfig.getProjectId(), botConfig.getSubscriptionId());

        // Create a subscriber for "my-subscription-id" bound to the message receiver
        final Subscriber subscriber =
                Subscriber.newBuilder(subscriptionName, botService).build();
        logger.info("Starting subscriber...");
        subscriber.startAsync();

        // Wait for termination
        subscriber.awaitTerminated();
    }
}