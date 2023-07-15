package qubits.enrichers;

import com.google.common.net.InternetDomainName;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

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
    String host = uri.getHost();
    if (Objects.isNull(host)) {
      return null;
    }
    InternetDomainName internetDomainName = InternetDomainName.from(host).topPrivateDomain();
    return internetDomainName.toString();
  }
}
