package domains;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DomainsAdapter {
  private final ObjectMapper objectMapper;
  public DomainsAdapter() {
    objectMapper = new ObjectMapper();
  }

  public String toJson(String[] domains) throws JsonProcessingException {
    return objectMapper.writeValueAsString(domains);
  }
}
