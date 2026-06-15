package com.platform.keycloak.events.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.platform.keycloak.events.configuration.KafkaPublisherSettings;
import com.platform.keycloak.events.domain.IdentityUserRegisteredMessage;
import com.platform.keycloak.events.ports.IdentityEventPublisher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public final class KafkaIdentityEventPublisher implements IdentityEventPublisher {
    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final ObjectMapper objectMapper;

    public KafkaIdentityEventPublisher(Properties properties, String topic) {
        this.producer = new KafkaProducer<>(properties);
        this.topic = topic;
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static KafkaIdentityEventPublisher create(KafkaPublisherSettings settings) {
        return new KafkaIdentityEventPublisher(
            createProperties(settings),
            settings.topic());
    }

    public static Properties createProperties(KafkaPublisherSettings settings) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, settings.bootstrapServers());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, settings.clientId());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        properties.put(ProducerConfig.RETRIES_CONFIG, "5");

        if (!settings.securityProtocol().isBlank()) {
            properties.put("security.protocol", settings.securityProtocol());
        }

        if (!settings.saslMechanism().isBlank()) {
            properties.put("sasl.mechanism", settings.saslMechanism());
        }

        if (!settings.saslUsername().isBlank() && !settings.saslPassword().isBlank() && !settings.saslMechanism().isBlank()) {
            properties.put(
                "sasl.jaas.config",
                String.format(
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                    settings.saslUsername(),
                    settings.saslPassword()));
        }

        return properties;
    }

    @Override
    public void publish(IdentityUserRegisteredMessage message) throws Exception {
        String payload = objectMapper.writeValueAsString(message);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message.getUserId().toString(), payload);
        producer.send(record).get();
    }

    @Override
    public void close() {
        producer.flush();
        producer.close();
    }
}
