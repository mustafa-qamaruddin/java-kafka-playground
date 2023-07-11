package domains;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DomainRegistrationHandler implements HttpHandler {
  private final DomainsRepository repository;
  private final DomainAdapter adapter;

  public DomainRegistrationHandler() {
    this.repository = new DomainsRepository();
    this.adapter = new DomainAdapter();
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equals("POST")) {
      // Method Not Allowed
      exchange.sendResponseHeaders(405, -1);
      return;
    }

    // Read input string
    byte[] inputBytes;
    try {
      inputBytes = exchange.getRequestBody().readAllBytes();
    } catch (IOException e) {
      // Bad Request
      exchange.sendResponseHeaders(400, -1);
      return;
    }

    List<String> inputDomains;
    try {
      inputDomains = adapter.fromJson(inputBytes);
    } catch (IOException e) {
      // Bad Request
      exchange.sendResponseHeaders(400, -1);
      return;
    }

    // Find the info
    Map<String, DomainInfo> outputDomainsInfo = new LinkedHashMap<>();
    for (String inputDomain : inputDomains) {
      DomainInfo domainInfo = repository.findByDomain(inputDomain);
      if (Objects.isNull(domainInfo)) {
        continue;
      }
      outputDomainsInfo.put(inputDomain, domainInfo);
    }

    // Return the response
    String response;
    try {
      response = adapter.toJson(outputDomainsInfo);
    } catch (JsonProcessingException e) {
      // Internal Server Error
      exchange.sendResponseHeaders(500, -1);
      return;
    }

    exchange.getResponseHeaders().set("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, response.length());

    OutputStream responseBody = exchange.getResponseBody();
    responseBody.write(response.getBytes());
    responseBody.flush();
    responseBody.close();
  }
}
