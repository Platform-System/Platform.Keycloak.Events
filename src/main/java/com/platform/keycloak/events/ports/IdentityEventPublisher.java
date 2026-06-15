package com.platform.keycloak.events.ports;

import com.platform.keycloak.events.domain.IdentityUserRegisteredMessage;

public interface IdentityEventPublisher extends AutoCloseable {
    void publish(IdentityUserRegisteredMessage message) throws Exception;

    @Override
    void close();
}
