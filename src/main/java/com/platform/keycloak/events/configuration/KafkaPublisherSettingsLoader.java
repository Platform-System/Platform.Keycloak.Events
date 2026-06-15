package com.platform.keycloak.events.configuration;

public final class KafkaPublisherSettingsLoader {
    private KafkaPublisherSettingsLoader() {
    }

    public static KafkaPublisherSettings loadFromEnvironment() {
        return new KafkaPublisherSettings(
            requireEnv("PLATFORM_KEYCLOAK_EVENTS_KAFKA_BOOTSTRAP_SERVERS"),
            requireEnv("PLATFORM_KEYCLOAK_EVENTS_KAFKA_TOPIC"),
            readEnv("PLATFORM_KEYCLOAK_EVENTS_KAFKA_CLIENT_ID", "platform-keycloak-events"),
            normalizeKafkaEnum(readEnv("PLATFORM_KEYCLOAK_EVENTS_KAFKA_SECURITY_PROTOCOL", "")),
            normalizeKafkaEnum(readEnv("PLATFORM_KEYCLOAK_EVENTS_KAFKA_SASL_MECHANISM", "")),
            readEnv("PLATFORM_KEYCLOAK_EVENTS_KAFKA_SASL_USERNAME", ""),
            readEnv("PLATFORM_KEYCLOAK_EVENTS_KAFKA_SASL_PASSWORD", ""));
    }

    private static String requireEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }

        return value;
    }

    private static String readEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null ? defaultValue : value;
    }

    private static String normalizeKafkaEnum(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.trim().replace('-', '_').toUpperCase();
    }
}
