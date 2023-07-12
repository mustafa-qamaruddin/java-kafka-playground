import lombok.extern.slf4j.Slf4j;
import messaging.consumers.ConsumeService;

import java.io.IOException;

@Slf4j
public class EnricherApp {
  // Read from kafka
  // Every hundred work as a batch
  // Enrich / Request Domain Registration
  // Write to Kafka
  // only then commit offset
  // TODO if end of topic reached, but not 100 yet, then enrich anyway
  public static void main(String[] args) throws IOException {
    ConsumeService service = new ConsumeService();
    service.consumeTopic();
  }
}
