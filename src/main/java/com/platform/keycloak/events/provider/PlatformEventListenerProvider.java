package com.platform.keycloak.events.provider;

import com.platform.keycloak.events.application.RegistrationEventProcessor;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.time.Instant;

public final class PlatformEventListenerProvider implements EventListenerProvider {
    private static final Logger LOGGER = Logger.getLogger(PlatformEventListenerProvider.class);

    private final KeycloakSession session;
    private final RegistrationEventProcessor processor;

    public PlatformEventListenerProvider(KeycloakSession session, RegistrationEventProcessor processor) {
        this.session = session;
        this.processor = processor;
    }

    @Override
    public void onEvent(Event event) {
        if (event == null) {
            return;
        }

        if (event.getType() != EventType.REGISTER && event.getType() != EventType.LOGIN) {
            return;
        }

        RealmModel realm = session.getContext().getRealm();
        if (realm == null) {
            LOGGER.warnf("Skipping %s event because realm context is missing.", event.getType());
            return;
        }

        UserModel user = session.users().getUserById(realm, event.getUserId());
        if (user == null) {
            LOGGER.warnf("Skipping %s event because user %s could not be loaded from Keycloak.", event.getType(), event.getUserId());
            return;
        }

        processor.process(
            event.getType(),
            event.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            Instant.ofEpochMilli(event.getTime()));
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
    }

    @Override
    public void close() {
    }
}
