package messaging.consumers;

import classifications.ClassificationAdapter;
import classifications.ClassificationDecision;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

@UtilityClass
public class ConsumerFactory {
  // TODO read properties from configs
  private final String BOOTSTRAP_SERVERS = "localhost:29092";
  private final String CONSUMER_GROUP_ID = "cg1";

  public Consumer<String, ClassificationDecision> createConsumer() {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // TODO ?
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ClassificationAdapter.class.getName());
//    TODO
//    properties.setProperty(
//        ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.ROUNDROBIN_ASSIGNOR_NAME
//    );
    return new KafkaConsumer<>(properties);
  }
}
