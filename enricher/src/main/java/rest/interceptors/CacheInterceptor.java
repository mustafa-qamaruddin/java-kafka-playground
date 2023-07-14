package rest.interceptors;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

public class CacheInterceptor implements Interceptor {
  @Override
  public Response intercept(Chain chain) throws IOException {
    return chain.proceed(
        chain.request().newBuilder()
            .cacheControl(CacheControl.FORCE_CACHE)
            .build()
    );
  }
}
