package com.revolution.rest.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.revolution.rest.model.Follow;

@Path("/follow")
@Produces({"application/json"})
public abstract interface FollowService
{
  @POST
  @Consumes({"application/json"})
  public abstract Response createFollow(Follow paramFollow);

  @Path("/{id}")
  @DELETE
  public abstract Response deleteFollow(@PathParam("id") Long paramLong);
}

/* Location:           E:\project\tella-webservice\WEB-INF\classes\
 * Qualified Name:     com.tella.rest.service.FollowService
 * JD-Core Version:    0.6.2
 */