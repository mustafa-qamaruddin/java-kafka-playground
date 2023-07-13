package messaging.consumers;

import classifications.ClassificationDeserializer;
import classifications.ClassificationDecision;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

@UtilityClass
class ConsumerFactory {
  private final String BOOTSTRAP_SERVERS = "localhost:29092";
  private final String CONSUMER_GROUP_ID = "cg1";

  public Consumer<String, ClassificationDecision> createConsumer() {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ClassificationDeserializer.class.getName());
    // Batch Configuration
    properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");

    return new KafkaConsumer<>(properties);
  }
}
