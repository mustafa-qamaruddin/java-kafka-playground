package qubits.dataModels.classificationDecisions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ClassificationDeserializer implements Deserializer<ClassificationDecision> {
  private final ObjectMapper objectMapper;

  public ClassificationDeserializer() {
    objectMapper = new ObjectMapper();
  }

  public String toJson(ClassificationDecision classificationDecision) throws JsonProcessingException {
    return objectMapper.writeValueAsString(classificationDecision);
  }

  public ClassificationDecision fromJson(String jsonMessage) throws JsonProcessingException {
    return objectMapper.readValue(jsonMessage, ClassificationDecision.class);
  }

  @Override
  public ClassificationDecision deserialize(String topic, byte[] data) {
    try {
      if (data == null) {
        log.error("Null received at deserializing");
        return null;
      }
      return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), ClassificationDecision.class);
    } catch (Exception e) {
      throw new SerializationException("Error when deserializing byte[] to ClassificationDecision");
    }
  }
}
