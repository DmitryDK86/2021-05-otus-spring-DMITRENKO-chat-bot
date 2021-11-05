package ru.ddk.googlechatbot.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DownstreamServiceHealthIndicator implements ReactiveHealthIndicator {

    @Override
    public Mono<Health> getHealth(boolean includeDetails) {
        return health().onErrorResume(
                ex -> Mono.just(new Health.Builder().down(ex).build())
        );
    }

    @Override
    public Mono<Health> health() {
        return Mono.just(new Health.Builder().up().build());
    }
}
