package qubits.rest.interceptors;

import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JsonInterceptor implements Interceptor {
  @NotNull
  @Override
  public Response intercept(@NotNull Chain chain) throws IOException {
    return chain.proceed(
        chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .build()
    );
  }
}
