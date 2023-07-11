package domains;

import java.util.LinkedHashMap;
import java.util.Map;

public class DomainsRepository {
  private final Map<String, DomainInfo> domainData = new LinkedHashMap<>();

  public DomainsRepository() {
    domainData.put(
        "aexpresswaythreat.co.uk",
        new DomainInfo(
            "2018-06-01T00:00:00Z",
            "2019-06-01T00:00:00Z",
            "2018-06-01T00:00:00Z"
        )
    );
    domainData.put(
        "tetarhn.trade",
        new DomainInfo(
            "2018-06-07T17:10:55Z",
            "2019-06-07T17:10:55Z",
            "2018-06-07T17:11:01Z"
        )
    );
  }

  public DomainInfo findByDomain(String domain) {
    return domainData.get(domain);
  }
}
