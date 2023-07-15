package qubits.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.failsafe.okhttp.FailsafeCall;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import qubits.dataModels.domainRegistrations.DomainInfo;
import qubits.dataModels.domainRegistrations.DomainsAdapter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DomainRegistrationClient {
  public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
  private static final String DOMAIN_REGISTRATION_ENDPOINT = "http://localhost:8000/search";
  private final OkHttpClient client;
  private final DomainsAdapter adapter;
  private final CachingManager cache;

  public DomainRegistrationClient() {
    client = ClientProvider.getClient();
    adapter = new DomainsAdapter();
    cache = new CachingManager();
  }

  public Map<String, DomainInfo> queryDomainInfos(List<String> domains) {
    // Does exist in cache?
    Map<String, DomainInfo> cachedResults = queryCache(domains);
    // Set Difference
    HashSet<String> allDomains = new HashSet<>(domains);
    allDomains.removeAll(cachedResults.keySet());
    List<String> remainingDomains = allDomains.stream().toList();
    // Query microservice for uncached
    Map<String, DomainInfo> freshResults = queryService(remainingDomains);
    // Cache results
    freshResults.forEach(cache::set);
    // merge and return
    cachedResults.putAll(freshResults);
    return cachedResults;
  }

  private Map<String, DomainInfo> queryCache(List<String> domains) {
    return domains
        .stream()
        .parallel()
        .filter(domain -> cache.get(domain) != null)
        .distinct()
        .collect(Collectors.toMap(domain -> domain, cache::get));
  }

  private Map<String, DomainInfo> queryService(List<String> domains) {
    // parse parameters
    String jsonStringToBePosted = null;
    try {
      jsonStringToBePosted = adapter.toJson(domains);
    } catch (JsonProcessingException e) {
      log.error("domain parsing exception: {}", e.getMessage());
      return Collections.emptyMap();
    }
    // build request
    RequestBody body = RequestBody.create(jsonStringToBePosted, MEDIA_TYPE_JSON);
    Request request = new Request.Builder()
        .url(DOMAIN_REGISTRATION_ENDPOINT)
        .post(body)
        .build();
    // Add circuit breaker
    FailsafeCall call = FailSafeDecorator.decorateRequest(
        client.newCall(request)
    );
    // send request
    Response response = null;
    try {
      response = call.execute();
    } catch (IOException e) {
      log.error("domain registration api call exception: {}", e.getMessage());
      return Collections.emptyMap();
    }
    // check response
    if (response.code() != 200) {
      log.error("domain registration api call failed: {}, {}", response.code(), response.body().toString());
      return Collections.emptyMap();
    }
    // handle empty response
    if (response.body() == null) {
      return Collections.emptyMap();
    }
    // parse results and return
    Map<String, DomainInfo> domainInfos = null;
    try {
      domainInfos = adapter.fromJson(response.body().string());
    } catch (IOException e) {
      log.error("failed to parse json response to Domains: {}", e.getMessage());
      return Collections.emptyMap();
    }
    response.close();
    return domainInfos;
  }
}
