package messaging.producers;

import enrichedclassifications.EnrichedClassification;
import enrichedclassifications.EnrichedClassificationSerializer;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

@UtilityClass
class ProducerFactory {
  private static final String BOOTSTRAP_SERVERS = "localhost:29092";

  public KafkaProducer<String, EnrichedClassification> createProducer() {
    // Configure the Kafka producer
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EnrichedClassificationSerializer.class.getName());
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "false");

    // Create the Kafka producer
    return new KafkaProducer<>(props);
  }
}
