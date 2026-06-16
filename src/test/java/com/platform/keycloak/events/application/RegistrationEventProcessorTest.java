package com.platform.keycloak.events.application;

import com.platform.keycloak.events.domain.IdentityUserRegisteredMessage;
import com.platform.keycloak.events.ports.IdentityEventPublisher;
import org.junit.jupiter.api.Test;
import org.keycloak.events.EventType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistrationEventProcessorTest {
    @Test
    void process_whenRegisterEventIsValid_publishesKafkaMessage() {
        RecordingPublisher publisher = new RecordingPublisher();
        RegistrationEventProcessor processor = new RegistrationEventProcessor(publisher);

        processor.process(
            EventType.REGISTER,
            "92f5a1dd-7e59-4ad8-bcc1-b8ceab6eff61",
            "hung",
            "hung@example.com",
            Instant.parse("2026-06-12T10:00:00Z"));

        assertEquals(1, publisher.messages.size());
        assertEquals("hung@example.com", publisher.messages.get(0).getEmail());
    }

    @Test
    void process_whenLoginEventIsValid_publishesKafkaMessage() {
        RecordingPublisher publisher = new RecordingPublisher();
        RegistrationEventProcessor processor = new RegistrationEventProcessor(publisher);

        processor.process(
            EventType.LOGIN,
            "92f5a1dd-7e59-4ad8-bcc1-b8ceab6eff61",
            "hung",
            "hung@example.com",
            Instant.parse("2026-06-12T10:00:00Z"));

        assertEquals(1, publisher.messages.size());
        assertEquals("hung@example.com", publisher.messages.get(0).getEmail());
    }

    @Test
    void process_whenEventIsUnsupported_skipsPublishing() {
        RecordingPublisher publisher = new RecordingPublisher();
        RegistrationEventProcessor processor = new RegistrationEventProcessor(publisher);

        processor.process(
            EventType.LOGOUT,
            "92f5a1dd-7e59-4ad8-bcc1-b8ceab6eff61",
            "hung",
            "hung@example.com",
            Instant.parse("2026-06-12T10:00:00Z"));

        assertEquals(0, publisher.messages.size());
    }

    @Test
    void process_whenEmailIsMissing_skipsPublishing() {
        RecordingPublisher publisher = new RecordingPublisher();
        RegistrationEventProcessor processor = new RegistrationEventProcessor(publisher);

        processor.process(
            EventType.REGISTER,
            "92f5a1dd-7e59-4ad8-bcc1-b8ceab6eff61",
            "hung",
            "",
            Instant.parse("2026-06-12T10:00:00Z"));

        assertEquals(0, publisher.messages.size());
    }

    private static final class RecordingPublisher implements IdentityEventPublisher {
        private final List<IdentityUserRegisteredMessage> messages = new ArrayList<>();

        @Override
        public void publish(IdentityUserRegisteredMessage message) {
            messages.add(message);
        }

        @Override
        public void close() {
        }
    }
}
