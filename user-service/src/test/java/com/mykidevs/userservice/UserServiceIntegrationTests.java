package com.mykidevs.userservice;


import com.mykidevs.sharedlib.events.UserCreatedEvent;
import com.mykidevs.userservice.dto.requests.UserCreateRequest;
import com.mykidevs.userservice.dto.responses.UserResponse;
import com.mykidevs.userservice.models.User;
import com.mykidevs.userservice.repositories.UserRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@Testcontainers
public class UserServiceIntegrationTests {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    UserRepository userRepository;
    RestTestClient client;
    private final Logger log = LoggerFactory.getLogger(UserServiceIntegrationTests.class);
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private ConsumerFactory<String, UserCreatedEvent> consumerFactory;
    private Consumer<String, UserCreatedEvent> testConsumer;
    private final UserCreateRequest goodUser = new UserCreateRequest("sasda@fassd.ccc", "nyanpasu123", "dddescriptionn");
    private final UserCreateRequest badUser = new UserCreateRequest("sasdfassd.ccc", "asda", "daksda");
    @Autowired
    WebApplicationContext context;
    @ServiceConnection
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofMinutes(3));
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);;

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka"));
    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry reg) {
        reg.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        reg.add("spring.data.redis.host", redis::getHost);
        reg.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
    }

    @BeforeAll
    static void setUpAll() {

    }

    @BeforeEach
    void setUp() {
        this.client = RestTestClient.bindToApplicationContext(context).build();
        testConsumer = consumerFactory.createConsumer("test-group-" + UUID.randomUUID(), null);
        testConsumer.subscribe(Collections.singleton("user-creation-topic"));
        KafkaTestUtils.getRecords(testConsumer, Duration.ofMillis(1000));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
    @Test
    void createUser_shouldReturn200AndSendToKafka() {
        client.post().uri("/api/v1/users/new")
                .body(goodUser)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class)
                .consumeWith(System.out::println);
        ConsumerRecord<String, UserCreatedEvent> record = KafkaTestUtils.getSingleRecord(testConsumer, "user-creation-topic");
        log.info("Kafka record: {}", record.value());
        UserCreatedEvent event = record.value();
        assertThat(event)
                .returns(goodUser.email(), UserCreatedEvent::userEmail)
                .satisfies(e -> {
                    assertThat(e.uuid()).isInstanceOf(UUID.class);
                    assertThat(e.createdAt()).isNotNull();
                    assertThat(e.token()).isInstanceOf(String.class).hasSize(32);
                });
    }

    @Test
    void createUser_shouldStoreTokenInRedis() {
        UserResponse createdUser = client.post()
                .uri("/api/v1/users/new")
                .body(goodUser)
                .exchange()
                .expectBody(UserResponse.class)
                .returnResult()
                .getResponseBody();

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    Cache cache = cacheManager.getCache("users");
                    assertThat(cache).isNotNull();

                    User cached = cache.get(createdUser.id(), User.class);
                    assertThat(cached).isNotNull();
                    assertThat(cached.getEmail()).isEqualTo(goodUser.email());
                });


    }




    @Test
    void createUser_shouldReturn400AndFieldsError() {
        client.post().uri("/api/v1/users/new")
                .body(badUser)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println);
    }







    }




