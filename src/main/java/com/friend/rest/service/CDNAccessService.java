package com.friend.rest.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import net.sf.json.JSONObject;

@Path("/cdnaccesstokens")
@Produces({"application/json"})
public abstract interface CDNAccessService
{
  @GET
  public abstract JSONObject getQiNiuToken();
}

