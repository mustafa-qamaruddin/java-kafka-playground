package rest;

import dev.failsafe.CircuitBreaker;
import dev.failsafe.RetryPolicy;
import dev.failsafe.okhttp.FailsafeCall;
import lombok.experimental.UtilityClass;
import okhttp3.Call;
import okhttp3.Response;

@UtilityClass
public class FailSafeDecorator {
  public FailsafeCall decorateRequest(Call call) {
    RetryPolicy<Response> retryPolicy = RetryPolicy.ofDefaults();
    CircuitBreaker<Response> circuitBreaker = CircuitBreaker.ofDefaults();
    return FailsafeCall
        .with(retryPolicy)
        .compose(circuitBreaker)
        .compose(call);
  }
}
