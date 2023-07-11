import com.sun.net.httpserver.HttpServer;
import domains.DomainRegistrationHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class AppServer {
  public static void main(String[] args) {
    HttpServer server = null;
    try {
      server = HttpServer.create(new InetSocketAddress(8000), 0);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    server.createContext("/search", new DomainRegistrationHandler());
    server.setExecutor(null);
    server.start();
  }
}
