package qubits.messaging.admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class AdminServiceTest {
  @Container
  private static final KafkaContainer kafkaContainer = new KafkaContainer();

  private AdminService adminService;

  @BeforeEach
  void setUp() {
    String bootstrapServers = kafkaContainer.getBootstrapServers();
    adminService = new AdminService(bootstrapServers);
  }

  @AfterEach
  void tearDown() {
    kafkaContainer.stop();
  }

  @Test
  void testIsBrokerUp() {
    // Assert that the broker is initially up
    Assertions.assertTrue(adminService.isBrokerUp());

    // Stop the Kafka container to simulate an unreachable broker
    kafkaContainer.stop();

    // Assert that the broker is no longer up
    Assertions.assertFalse(adminService.isBrokerUp());
  }
}
