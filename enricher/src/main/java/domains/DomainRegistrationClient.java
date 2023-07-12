package domains;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
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
    // TODO set media type headers
    client = new OkHttpClient.Builder().build();
    adapter = new DomainsAdapter();
    cache = new CachingManager();
  }

  public Map<String, DomainInfo> queryDomainInfos(List<String> domains) {
    Map<String, DomainInfo> cachedResults = queryCache(domains);
    HashSet<String> allDomains = new HashSet<>(domains);
    allDomains.removeAll(cachedResults.keySet());
    List<String> remainingDomains = allDomains.stream().toList();
    Map<String, DomainInfo> freshResults = queryService(remainingDomains);
    freshResults.forEach(cache::set);
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

    String jsonStringToBePosted = null;
    try {
      jsonStringToBePosted = adapter.toJson(domains);
    } catch (JsonProcessingException e) {
      // TODO
      throw new RuntimeException(e);
    }

    RequestBody body = RequestBody.create(jsonStringToBePosted, MEDIA_TYPE_JSON);
    Request request = new Request.Builder()
        .url(DOMAIN_REGISTRATION_ENDPOINT)
        .post(body)
        .build();
    Call call = client.newCall(request);
    Response response = null;
    try {
      response = call.execute();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    // TODO Add error handling 400, 401, 403, 500, etc
    // TODO Use async request
    // Todo Use callback
    // TODO Push failed to DLQ
    // TODO Push success to Queue and Commit Offset on Success
    // TODO add httpOk caching
    // TODO add inMemory Cache
    ObjectMapper objectMapper = new ObjectMapper();
    if (response.body() == null) {
      return Collections.emptyMap();
    }
    TypeReference<HashMap<String, DomainInfo>> typeRef = new TypeReference<HashMap<String, DomainInfo>>() {
    };
    Map<String, DomainInfo> domainInfos = null;
    try {
      domainInfos = objectMapper.readValue(response.body().bytes(), typeRef);
    } catch (IOException e) {
      // TODO
      throw new RuntimeException(e);
    }
    response.close();
    log.info(domainInfos.toString());
    return domainInfos;
  }
}
