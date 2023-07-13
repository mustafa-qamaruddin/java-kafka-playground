package domains;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class DomainsAdapter {
  private final ObjectMapper objectMapper;
  public DomainsAdapter() {
    objectMapper = new ObjectMapper();
  }

  public String toJson(List<String> domains) throws JsonProcessingException {
    return objectMapper.writeValueAsString(domains);
  }

  public HashMap<String, DomainInfo> fromJson(String jsonBody) throws JsonProcessingException {
    TypeReference<HashMap<String, DomainInfo>> typeRef = new TypeReference<>() {};
    return objectMapper.readValue(jsonBody, typeRef);
  }
}
