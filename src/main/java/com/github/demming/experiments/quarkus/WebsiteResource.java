package com.github.demming.experiments.quarkus;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletionStage;


@Path("/website")
public
class WebsiteResource {

  // FIXME: Cache stampede if non-blocking!
  //  lockTimeout only works when String is returned and no @NonBlocking is activated.


  @GET
  @Path("/blocking")
  @Produces(MediaType.TEXT_HTML)
  // lockTimeout even at 1s managed to admit cache stampede.
  @CacheResult(cacheName = "get-sanitized-html-blocking", lockTimeout = 0L) //, lockTimeout = 500L)
  public
  String getSanitizedHtmlBlocking (@QueryParam("address") @CacheKey final String address)
  throws MalformedURLException {

    // Having this service static leads to issues with GraalVM introspection at compile-time.
    // But we're testing response caching here, so this extra allocation is irrelevant here.
    return new WebsiteSanitizerService().sanitizeUrl(new URL(address));
  }


  @NonBlocking
  @GET
  @Path("/non-blocking")
  @Produces(MediaType.TEXT_HTML)
  @CacheResult(cacheName = "get-sanitized-html-fornonblocking", lockTimeout = 0L) //, lockTimeout = 500L)
  public
  String getSanitizedHtmlNonBlocking (@QueryParam("address") @CacheKey final String address)
  throws MalformedURLException {

    return new WebsiteSanitizerService().sanitizeUrlForNonBlocking(new URL(address));
  }


  @GET
  @Path("/async")
  @Produces(MediaType.TEXT_HTML)
  @CacheResult(cacheName = "get-sanitized-html-async", lockTimeout = 0L) //, lockTimeout = 500L)
  public
  CompletionStage <String> getSanitizedHtmlAsync (@QueryParam("address") @CacheKey final String address)
  throws MalformedURLException {

    return new WebsiteSanitizerService().sanitizeUrlAsync(new URL(address));
  }


  @GET
  @Path("/uni")
  @Produces(MediaType.TEXT_HTML)
  @CacheResult(cacheName = "get-sanitized-html-uni", lockTimeout = 0L) //, lockTimeout = 500L)
  public
  Uni <String> getSanitizedHtmlUni (@QueryParam("address") @CacheKey final String address)
  throws MalformedURLException {

    return new WebsiteSanitizerService().sanitizeUrlUni(new URL(address));
  }

}
