package com.github.demming.experiments.quarkus;


import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletionStage;

@Slf4j
public
class WebsiteSanitizerService {

  /** Compound OWASP HTML sanitization policy */
  private static final PolicyFactory policy =
    Sanitizers.FORMATTING
      .and(Sanitizers.LINKS)
      .and(Sanitizers.TABLES)
      .and(Sanitizers.BLOCKS)
      .and(Sanitizers.IMAGES)
      .and(Sanitizers.STYLES);

  /** Constructed OkHttp client instance. */
  private final OkHttpClient okHttpClient = new OkHttpClient();

  /** Constructed MicroProfile client builder instance */
  private final RestClientBuilder clientBuilder = RestClientBuilder.newBuilder();


  /** Blocking upsteam GET */
  public
  String get (URL url) {
    var result = clientBuilder.baseUrl(url)
                              .build(RestClientService.class);

    return result.get();
  }

  /** Blocking upstream GET with OkHttp */
  @Deprecated
  public
  String getWithOkHttp (URL url) throws IOException {
    var request = new Request.Builder()
      .url(url)
      .build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      if (response.body() != null) {
        return response.body().string();
      } else {
        throw new IllegalStateException("Response body is null");
      }
    }
  }

  /** Async upstream GET */
  public
  CompletionStage <String> getAsync (URL url) {
    var result = clientBuilder.baseUrl(url)
                              .build(RestClientService.class);

    return result.getAsync();
  }

  /** Uni async upstream GET */
  public
  Uni <String> getUni (URL url) {
    var result = clientBuilder.baseUrl(url)
                              .build(RestClientService.class);

    return result.getUni();
  }


  // Global counters, singleton scope, enable the calculation of the number of method invocations.
  private static Integer _uniCounter            = 0;
  private static Integer _forNonblockingCounter = 0;
  private static Integer _asyncCounter          = 0;
  private static Integer _blockingCounter       = 0;


  /**
   * Sanitize the HTML obtained from the URL argument.
   *
   * @return An evaluated String witness.
   */
  public
  String sanitizeUrl (final URL url) {
    _blockingCounter += 1;
    log.info(">> Service.sanitizeUrlBlocking #{}", _blockingCounter);

    return policy.sanitize(get(url));
  }


  /**
   * Same as blocking, but used separately for the endpoint decorated with @NonBlocking
   *
   * @return An evaluated String witness.
   */
  public
  String sanitizeUrlForNonBlocking (final URL url) {
    _forNonblockingCounter += 1;
    log.info(">> Service.sanitizeUrlForNonBlocking #{}", _forNonblockingCounter);

    return policy.sanitize(get(url));
  }


  /**
   * @return A CompletionStage action.
   */
  public
  CompletionStage <String> sanitizeUrlAsync (final URL url) {
    _asyncCounter += 1;
    log.info(">> Service.sanitizeUrlAsync #{}", _asyncCounter);

    return getAsync(url).thenApply(html -> policy.sanitize(html));
  }


  /**
   * @return A Uni action.
   */
  public
  Uni <String> sanitizeUrlUni (final URL url) {
    _uniCounter += 1;
    log.info(">> Service.sanitizeUrlUni #{}", _uniCounter);

    return getUni(url).map(html -> policy.sanitize(html));
  }


}
