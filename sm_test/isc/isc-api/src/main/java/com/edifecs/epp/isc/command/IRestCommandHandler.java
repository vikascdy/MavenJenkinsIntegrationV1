package com.edifecs.epp.isc.command;

import java.io.Serializable;
import java.util.Collection;

import com.edifecs.epp.isc.json.JsonArg;
import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.annotations.Rest;
import com.edifecs.epp.isc.annotations.SyncCommand;
import com.edifecs.epp.isc.core.command.Pagination;

public interface IRestCommandHandler<T extends Serializable> {

  @Rest(enabled=true)
  @SyncCommand(root=true)
  Serializable restCommand(
    @Arg(name = "-x-rest-method", required = true)   String method,
    @Arg(name = "-x-url-suffix", required = false)   String urlSuffix,
    @Arg(name = "-x-request-body", required = false) JsonArg body,
    @Arg(name = "page", required = false)            Long page,
    @Arg(name = "start", required = false)           Long start,
    @Arg(name = "limit", required = false)           Long limit,
    @Arg(name = "query", required = false)           String query,
    @Arg(name = "sort", required = false)            String sortersJson,
    @Arg(name = "filter", required = false)          String filtersJson
  ) throws Exception;

  // --------------------------------------------------------------------------
  // The below methods are marked as REST-inaccessible because they are not
  // meant to be called directly, except through generated proxy classes.
  //
  // REST requests should use the root command (restCommand, above), which will
  // redirect the request to a below method depending upon the HTTP verb used.
  // --------------------------------------------------------------------------

  @Rest(enabled=false)
  @SyncCommand(name="rest-get")
  T get(
    @Arg(name="path", required=true) String url
  ) throws Exception;

  @Rest(enabled=false)
  @SyncCommand(name="rest-list")
  Collection<T> list(
    @Arg(name="pagination", required=false) Pagination pg
  ) throws Exception;

  @Rest(enabled=false)
  @SyncCommand(name="rest-post")
  T post(
    @Arg(name="item", required=true) T newItem
  ) throws Exception;

  @Rest(enabled=false)
  @SyncCommand(name="rest-put")
  T put(
    @Arg(name="path", required=true) String url,
    @Arg(name="item", required=true) T item
  ) throws Exception;

  @Rest(enabled=false)
  @SyncCommand(name="rest-delete")
  void delete(
    @Arg(name="path", required=true) String url
  ) throws Exception;
}

