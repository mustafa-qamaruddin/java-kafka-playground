package domains;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DomainAdapter {
  private final ObjectMapper objectMapper;

  public DomainAdapter() {
    objectMapper = new ObjectMapper();
  }

  public String toJson(Map<String, DomainInfo> outputDomainsInfo) throws JsonProcessingException {
    return objectMapper.writeValueAsString(outputDomainsInfo);
  }

  public List<String> fromJson(byte[] bytes) throws IOException {
    return List.of(
        objectMapper.readValue(bytes, String[].class)
    );
  }
}
