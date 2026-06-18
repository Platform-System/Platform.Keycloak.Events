package com.platform.keycloak.events.application;

import com.platform.keycloak.events.domain.IdentityUserRegisteredMessage;
import com.platform.keycloak.events.ports.IdentityEventPublisher;
import org.jboss.logging.Logger;
import org.keycloak.events.EventType;

import java.time.Instant;
import java.util.UUID;

public final class RegistrationEventProcessor {
    private static final Logger LOGGER = Logger.getLogger(RegistrationEventProcessor.class);

    private final IdentityEventPublisher publisher;

    public RegistrationEventProcessor(IdentityEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void process(EventType eventType, String userId, String userName, String email, String firstName, String lastName, Instant occurredAt) {
        if (eventType != EventType.REGISTER && eventType != EventType.LOGIN) {
            return;
        }

        if (userId == null || userId.isBlank()) {
            LOGGER.warnf("Skipping %s event because userId is missing.", eventType);
            return;
        }

        if (userName == null || userName.isBlank() || email == null || email.isBlank()) {
            LOGGER.warnf("Skipping %s event for user %s because username/email is missing.", eventType, userId);
            return;
        }

        UUID parsedUserId;
        try {
            parsedUserId = UUID.fromString(userId);
        } catch (IllegalArgumentException exception) {
            LOGGER.warnf(exception, "Skipping %s event because userId %s is not a valid UUID.", eventType, userId);
            return;
        }

        try {
            publisher.publish(new IdentityUserRegisteredMessage(
                UUID.randomUUID(),
                parsedUserId,
                userName,
                email,
                firstName,
                lastName,
                occurredAt));
        } catch (Exception exception) {
            throw new RuntimeException("Failed to publish identity event to Kafka.", exception);
        }
    }
}
