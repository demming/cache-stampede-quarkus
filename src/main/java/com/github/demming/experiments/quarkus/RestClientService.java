package com.github.demming.experiments.quarkus;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import javax.ws.rs.GET;
import java.util.concurrent.CompletionStage;
import io.smallrye.mutiny.Uni;

@RegisterRestClient(configKey = "rest-client-service")
public interface RestClientService {

  @GET
  String get();

  @GET
  CompletionStage <String> getAsync();

  @GET
  Uni <String> getUni ();
}
