import classification.ClassificationAdapter;
import classification.ClassificationDecision;
import classification.ClassificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import messaging.ProduceService;

@Slf4j
public class ProducerApp {
  public static void main(String[] args) {
    ClassificationRepository repository = new ClassificationRepository();
    ClassificationAdapter adapter = new ClassificationAdapter();
    ProduceService produceService = new ProduceService();
    while (true) {
      ClassificationDecision classificationDecision = repository.findNext();
      String message;
      try {
        message = adapter.toJson(classificationDecision);
      } catch (JsonProcessingException e) {
        log.error("Error serializing JSON message: {}", e.getMessage());
        continue;
      }
      produceService.sendJsonMessage(message);
    }
  }
}
