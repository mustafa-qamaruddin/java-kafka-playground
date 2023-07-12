package enrichers;

import lombok.experimental.UtilityClass;

import java.net.URI;
import java.net.URISyntaxException;

@UtilityClass
public class DomainExtractor {
  public static String extract(String url) {
    if (url == null) {
      return null;
    }
    URI uri;
    try {
      uri = new URI(url);
    } catch (URISyntaxException e) {
      return null;
    }
    String domain = uri.getHost();
    if (domain != null && domain.startsWith("www.")) {
      domain = domain.substring(4);
    }
    return domain;
  }
}
