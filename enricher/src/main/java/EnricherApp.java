import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.kafka.common.protocol.types.Field;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EnricherApp {
  public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
  private static final String DOMAIN_REGISTRATION_ENDPOINT = "http://localhost:8000/search";
  // Read from kafka
  // Every hundred work as a batch
  // Enrich / Request Domain Registration

  // Write to Kafka
  // only then commit offset
  public static void main(String[] args) throws IOException {
    OkHttpClient client = new OkHttpClient.Builder()
        .build();
    String jsonStringToBePosted = """
        [
            "aexpresswaythreat.co.uk",
            "tetarhn.trade",
            "tripple-rock.com"
        ]""";
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
    ObjectMapper objectMapper = new ObjectMapper();
    if (response.body() == null) {
    return;
    }
    TypeReference<HashMap<String,DomainInfo>> typeRef = new TypeReference<HashMap<String,DomainInfo>>() {};
    Map<String, DomainInfo> domainInfos = objectMapper.readValue(response.body().bytes(), typeRef);
    response.close();
    log.info(domainInfos.toString());
  }
}
