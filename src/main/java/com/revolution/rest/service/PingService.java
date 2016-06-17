package com.revolution.rest.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/ping")
public abstract interface PingService
{
  @GET
  public abstract Response responseMsg();
}

/* Location:           E:\project\tella-webservice\WEB-INF\classes\
 * Qualified Name:     com.tella.rest.service.PingService
 * JD-Core Version:    0.6.2
 */