package com.mykidevs.notiservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mykidevs.sharedlib.events.UserCreatedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers

public class NotiServiceIntegrationTests {

    @Autowired
    KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
    private final Logger log = LoggerFactory.getLogger(NotiServiceIntegrationTests.class);

    @Container
    static GenericContainer<?> mailhog = new GenericContainer<>("mailhog/mailhog:latest")
            .withExposedPorts(1025, 8025);

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofMinutes(3));

    private UserCreatedEvent testEvent;
    @Autowired
    private ConsumerFactory<String, UserCreatedEvent> consumerFactory;
    private Consumer<String, UserCreatedEvent> testConsumer;

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka"));


    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry reg) {
        reg.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        reg.add("spring.mail.host", mailhog::getHost);
        reg.add("spring.mail.port", () -> mailhog.getMappedPort(1025));
    }
    @BeforeEach
    void setUp() {
        testEvent = new UserCreatedEvent(UUID.randomUUID(), "sda@adads.com", Instant.now(), UUID.randomUUID().toString().replace("-", ""));
        testConsumer = consumerFactory.createConsumer("test-group-" + UUID.randomUUID().toString(), null);
        testConsumer.subscribe(Collections.singleton("test-topic"));
    }
    @Test
    void sendAndConsumeEvent_shouldBeSuccessful() throws ExecutionException, InterruptedException {
        kafkaTemplate.send("test-topic", testEvent.userEmail(), testEvent).get();
        var record = KafkaTestUtils.getSingleRecord(testConsumer, "test-topic", Duration.ofSeconds(10));
        log.info("Kafka record: {}", record);
        UserCreatedEvent event = record.value();
        assertThat(event).isNotNull();
        assertThat(event.userEmail()).isEqualTo(testEvent.userEmail());
    }





}
