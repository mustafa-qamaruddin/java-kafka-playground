package qubits.rest;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import qubits.dataModels.domainRegistrations.DomainInfo;

public class CachingManager {
  private final Cache<String, DomainInfo> cache;

  public CachingManager() {
    cache = Caffeine.newBuilder().build();
  }

  public DomainInfo get(String key) {
    return cache.getIfPresent(key);
  }

  public void set(String key, DomainInfo value) {
    cache.put(key, value);
  }
}
