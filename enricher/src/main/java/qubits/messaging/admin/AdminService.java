package qubits.messaging.admin;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.Node;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class AdminService {
  private final AdminClient client;

  public AdminService(String bootstrapServers) {
    Properties props = new Properties();
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 1500);
    props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 1000);
    props.put(AdminClientConfig.RETRIES_CONFIG, 100);

    this.client = AdminClient.create(props);
  }

  private boolean verifyConnection() throws ExecutionException, InterruptedException {
    Collection<Node> nodes = this.client.describeCluster()
        .nodes()
        .get();
    return nodes != null && !nodes.isEmpty();
  }

  public boolean isBrokerUp() {
    try {
      if (!verifyConnection()) {
        throw new IllegalStateException("broker is not ready");
      }
    } catch (InterruptedException e) {
      // interrupted exceptions are meant to stop execution
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      log.error("Broker unreachable {}", e.getMessage());
      return false;
    }
    return true;
  }
}
