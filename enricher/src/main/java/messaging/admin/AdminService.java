package messaging.admin;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.Node;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class AdminService {
  private static final String BOOTSTRAP_SERVERS = "localhost:29092";

  private final AdminClient client;

  public AdminService() {
    Properties props = new Properties();
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 1500);
    props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 1000);
    props.put(AdminClientConfig.RETRIES_CONFIG, 100);

    this.client = AdminClient.create(props);
  }

  public boolean verifyConnection() throws ExecutionException, InterruptedException {
    Collection<Node> nodes = this.client.describeCluster()
        .nodes()
        .get();
    return nodes != null && !nodes.isEmpty();
  }
}
