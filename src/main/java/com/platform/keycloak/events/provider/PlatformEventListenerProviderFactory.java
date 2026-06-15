package com.platform.keycloak.events.provider;

import com.platform.keycloak.events.application.RegistrationEventProcessor;
import com.platform.keycloak.events.configuration.KafkaPublisherSettings;
import com.platform.keycloak.events.configuration.KafkaPublisherSettingsLoader;
import com.platform.keycloak.events.infrastructure.KafkaIdentityEventPublisher;
import com.platform.keycloak.events.ports.IdentityEventPublisher;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public final class PlatformEventListenerProviderFactory implements EventListenerProviderFactory {
    private static final String LISTENER_ID = "platform-event-listener";

    private IdentityEventPublisher publisher;
    private RegistrationEventProcessor processor;

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new PlatformEventListenerProvider(session, processor);
    }

    @Override
    public void init(Config.Scope config) {
        KafkaPublisherSettings settings = KafkaPublisherSettingsLoader.loadFromEnvironment();
        publisher = KafkaIdentityEventPublisher.create(settings);
        processor = new RegistrationEventProcessor(publisher);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
        if (publisher != null) {
            publisher.close();
        }
    }

    @Override
    public String getId() {
        return LISTENER_ID;
    }
}
