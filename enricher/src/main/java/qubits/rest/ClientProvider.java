package qubits.rest;

import lombok.experimental.UtilityClass;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import qubits.rest.interceptors.CacheInterceptor;
import qubits.rest.interceptors.JsonInterceptor;

import java.io.File;

@UtilityClass
public class ClientProvider {
  public OkHttpClient getClient() {
    long maxSize = 10 * 1024 * 1024; // 10 Mb
    Cache clientCache = new Cache(new File("./tmp"), maxSize);
    return new OkHttpClient.Builder()
        .cache(clientCache)
        .addInterceptor(new CacheInterceptor())
        .addInterceptor(new JsonInterceptor())
        .addInterceptor(
            new HttpLoggingInterceptor(
                HttpLoggingInterceptor.Logger.DEFAULT
            ).setLevel(HttpLoggingInterceptor.Level.BASIC)
        )
        .retryOnConnectionFailure(true)
        .build();
  }
}
