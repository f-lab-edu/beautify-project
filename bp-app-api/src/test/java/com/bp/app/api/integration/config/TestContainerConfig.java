package com.bp.app.api.integration.config;

import java.time.Duration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainerConfig {

    private static final String CONFLUENT_KAFKA_IMAGE_NAME = "confluentinc/cp-kafka";
    private static final String CONFLUENT_SCHEMA_REGISTRY_CONTAINER_IMAGE_NAME = "confluentinc/cp-schema-registry";

    private static final String CONFLUENT_PLATFORM_VERSION = "7.4.0";
    private static final String MYSQL_VERSION = "9.1";

    private static final Network NETWORK;

    public static final MySQLContainer<?> MYSQL_CONTAINER;
    public static final ConfluentKafkaContainer KAFKA_CONTAINER;
    public static final GenericContainer<?> SCHEMA_REGISTRY_CONTAINER;

    static {
        NETWORK = Network.newNetwork();

        MYSQL_CONTAINER = new MySQLContainer<>(
            DockerImageName.parse("mysql:" + MYSQL_VERSION))
            .withDatabaseName("beautify-project-test")
            .withUsername("root")
            .withPassword("root");
        MYSQL_CONTAINER.start();


        KAFKA_CONTAINER = new ConfluentKafkaContainer(
            DockerImageName.parse(CONFLUENT_KAFKA_IMAGE_NAME)
                .withTag(CONFLUENT_PLATFORM_VERSION))
            .withListener("tc-kafka:29093")
            .withNetwork(NETWORK)
            .withNetworkAliases("tc-kafka")
            .withReuse(true);
        KAFKA_CONTAINER.start();

        SCHEMA_REGISTRY_CONTAINER = new GenericContainer<>(
            DockerImageName.parse(CONFLUENT_SCHEMA_REGISTRY_CONTAINER_IMAGE_NAME)
                .withTag(CONFLUENT_PLATFORM_VERSION))
            .withExposedPorts(29095)
            .withNetworkAliases("schemaregistry")
            .withNetwork(NETWORK)
            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS",
                "PLAINTEXT://tc-kafka:29093")
            .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:29095")
            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schemaregistry")
            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_SECURITY_PROTOCOL", "PLAINTEXT")
            .waitingFor(Wait.forHttp("/subjects").forStatusCode(200))
            .withStartupTimeout(Duration.ofSeconds(60));
        SCHEMA_REGISTRY_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry dynamicPropertyRegistry) {
        final String datasourceUrlFromContainer = MYSQL_CONTAINER.getJdbcUrl();
        final String datasourceParams = "?serverTimezone=Asia/Seoul&useUniCode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true";
        dynamicPropertyRegistry.add("spring.datasource.url", () -> datasourceUrlFromContainer + datasourceParams);
        dynamicPropertyRegistry.add("spring.datasource.driver-class-name",() -> "com.mysql.cj.jdbc.Driver");
        dynamicPropertyRegistry.add("spring.datasource.username", () -> "root");
        dynamicPropertyRegistry.add("spring.datasource.password", () -> "root");

        final String kafkaBrokerUrl = "http://" + KAFKA_CONTAINER.getBootstrapServers();
        final String schemaRegistryUrl = "http://localhost:" + SCHEMA_REGISTRY_CONTAINER.getMappedPort(29095);
        dynamicPropertyRegistry.add("kafka.broker-url", () -> kafkaBrokerUrl);
        dynamicPropertyRegistry.add("kafka.schema-registry-url", () -> schemaRegistryUrl);

        dynamicPropertyRegistry.add("spring.mail.host", () -> "smtp.gmail.com");
        dynamicPropertyRegistry.add("spring.mail.port", () -> 587);
        dynamicPropertyRegistry.add("spring.mail.username", () -> "dev.mail.dp@gmail.com");
        dynamicPropertyRegistry.add("spring.mail.password", () -> "");
    }
}
