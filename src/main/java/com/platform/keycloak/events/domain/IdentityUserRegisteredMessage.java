package com.platform.keycloak.events.domain;

import java.time.Instant;
import java.util.UUID;

public final class IdentityUserRegisteredMessage {
    private final UUID messageId;
    private final UUID userId;
    private final String userName;
    private final String email;
    private final Instant occurredAt;

    public IdentityUserRegisteredMessage(UUID messageId, UUID userId, String userName, String email, Instant occurredAt) {
        this.messageId = messageId;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
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

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
