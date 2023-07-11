import classification.ClassificationDecision;
import classification.ClassificationRepository;
import classification.ProducerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;

@Slf4j
public class ProducerApp {
  // https://www.linkedin.com/pulse/kafka-transactions-part-1-exactly-once-messaging-rob-golder/
  private static final String TOPIC_NAME = "classification_decisions";

  public static void main(String[] args) {
    ClassificationRepository repository = new ClassificationRepository();
    try (KafkaProducer<String, String> producer = new ProducerFactory().createProducer()) {
      ClassificationDecision classificationDecision = repository.findNext();
      sendJsonMessage(producer, classificationDecision);
    }
  }

  private static void sendJsonMessage(
      KafkaProducer<String, String> producer, ClassificationDecision message
  ) {
    String jsonMessage;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      jsonMessage = objectMapper.writeValueAsString(message);
    } catch (JsonProcessingException e) {
      log.error("Error serializing JSON message: " + e.getMessage());
      return;
    }

    try {
      ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, jsonMessage);
      producer.send(record, (metadata, exception) -> {
        if (exception != null) {
          log.error("Error sending message: " + exception.getMessage());
        } else {
          log.error("Message sent successfully to topic " + metadata.topic());
        }
      });
    } catch (KafkaException e) {
      log.error("Error sending JSON message: " + e.getMessage());
    }
  }
}
