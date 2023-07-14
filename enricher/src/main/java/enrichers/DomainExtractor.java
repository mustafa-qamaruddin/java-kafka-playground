package enrichers;

import com.google.common.net.InternetDomainName;
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
    String host = uri.getHost();
    InternetDomainName internetDomainName = InternetDomainName.from(host).topPrivateDomain();
    return internetDomainName.toString();
  }
}
