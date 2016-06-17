package com.revolution.rest.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.revolution.rest.model.Configuration;

@Path("/configuration")
@Produces({"application/json"})
public abstract interface ConfigurationService
{
  @POST
  @Consumes({"application/json"})
  public abstract Response create(Configuration paramConfiguration);

  @Path("/{configurationId}")
  @PUT
  @Consumes({"application/json"})
  public abstract Response update(@PathParam("configurationId") Long paramLong, Configuration paramConfiguration);
}

/* Location:           E:\project\tella-webservice\WEB-INF\classes\
 * Qualified Name:     com.tella.rest.service.ConfigurationService
 * JD-Core Version:    0.6.2
 */