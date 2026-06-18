package com.platform.keycloak.events.domain;

import java.time.Instant;
import java.util.UUID;

public final class IdentityUserRegisteredMessage {
    private final UUID messageId;
    private final UUID userId;
    private final String userName;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Instant occurredAt;

    public IdentityUserRegisteredMessage(UUID messageId, UUID userId, String userName, String email, String firstName, String lastName, Instant occurredAt) {
        this.messageId = messageId;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.occurredAt = occurredAt;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
