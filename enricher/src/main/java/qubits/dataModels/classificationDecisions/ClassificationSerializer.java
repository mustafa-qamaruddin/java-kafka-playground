package qubits.dataModels.classificationDecisions;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class ClassificationSerializer implements Serializer<ClassificationDecision> {
  private final ObjectMapper objectMapper;

  public ClassificationSerializer() {
    objectMapper = new ObjectMapper();
  }

  @Override
  public byte[] serialize(String topic, ClassificationDecision data) {
    try {
      if (data == null) {
        log.error("Null received at serializing");
        return null;
      }
      return objectMapper.writeValueAsBytes(data);
    } catch (Exception e) {
      throw new SerializationException("Error when serializing ClassificationDecision to byte[]");
    }
  }
}
