package com.bp.app.event.consumer.testcontainers;

import java.time.Duration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainerFactory {

    private static final String CONFLUENT_KAFKA_IMAGE_NAME = "confluentinc/cp-kafka";
    private static final String CONFLUENT_SCHEMA_REGISTRY_CONTAINER_IMAGE_NAME = "confluentinc/cp-schema-registry";
    private static final String CONFLUENT_PLATFORM_VERSION = "7.4.0";
    private static final String MYSQL_VERSION = "9.1";

    public static MySQLContainer<?> createMySQLContainer() {
        return new MySQLContainer<>(
            DockerImageName.parse("mysql:" + MYSQL_VERSION))
            .withDatabaseName("beautify-project-test")
            .withUsername("root")
            .withPassword("root");
    }

    public static ConfluentKafkaContainer createKafkaContainer(Network network) {
        return new ConfluentKafkaContainer(
            DockerImageName.parse(CONFLUENT_KAFKA_IMAGE_NAME)
                .withTag(CONFLUENT_PLATFORM_VERSION))
            .withListener("tc-kafka:29093")
            .withNetwork(network)
            .withNetworkAliases("tc-kafka")
            .withReuse(true)
            .waitingFor(Wait.forListeningPort());
    }

    public static GenericContainer<?> createSchemaRegistryContainer(Network network, ConfluentKafkaContainer kafkaContainer) {
        return new GenericContainer<>(
            DockerImageName.parse(CONFLUENT_SCHEMA_REGISTRY_CONTAINER_IMAGE_NAME)
                .withTag(CONFLUENT_PLATFORM_VERSION))
            .withExposedPorts(29095)
            .withNetworkAliases("schemaregistry")
            .withNetwork(network)
            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS",
                "PLAINTEXT://tc-kafka:29093")
            .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:29095")
            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schemaregistry")
            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_SECURITY_PROTOCOL", "PLAINTEXT")
            .waitingFor(Wait.forHttp("/subjects").forStatusCode(200))
            .withStartupTimeout(Duration.ofSeconds(60))
            .dependsOn(kafkaContainer);
    }

    public static void overrideDatasourceProps(DynamicPropertyRegistry dynamicPropertyRegistry, MySQLContainer<?> mySQLContainer) {
        final String datasourceUrlFromContainer = mySQLContainer.getJdbcUrl();
        final String datasourceParams = "?serverTimezone=Asia/Seoul&useUniCode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true";
        dynamicPropertyRegistry.add("spring.datasource.url", () -> datasourceUrlFromContainer + datasourceParams);
        dynamicPropertyRegistry.add("spring.datasource.driver-class-name",() -> "com.mysql.cj.jdbc.Driver");
        dynamicPropertyRegistry.add("spring.datasource.username", () -> "root");
        dynamicPropertyRegistry.add("spring.datasource.password", () -> "root");
    }

    public static void overrideKafkaProps(DynamicPropertyRegistry dynamicPropertyRegistry, ConfluentKafkaContainer kafkaContainer, GenericContainer<?> schemaRegistryContainer) {
        final String kafkaBrokerUrl = "http://" + kafkaContainer.getBootstrapServers();
        final String schemaRegistryUrl = "http://localhost:" + schemaRegistryContainer.getMappedPort(29095);
        dynamicPropertyRegistry.add("kafka.broker-url", () -> kafkaBrokerUrl);
        dynamicPropertyRegistry.add("kafka.schema-registry-url", () -> schemaRegistryUrl);
    }

    public static void overrideMailProps(final DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.mail.host", () -> "smtp.gmail.com");
        dynamicPropertyRegistry.add("spring.mail.port", () -> 587);
        dynamicPropertyRegistry.add("spring.mail.username", () -> "dev.mail.dp@gmail.com");
        dynamicPropertyRegistry.add("spring.mail.password", () -> "");
    }
}
