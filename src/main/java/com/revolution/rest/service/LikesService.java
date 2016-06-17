package com.revolution.rest.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.revolution.rest.model.Likes;

@Path("/likes")
@Produces({"application/json"})
public abstract interface LikesService
{
  @POST
  @Consumes({"application/json"})
  public abstract Response createLike(Likes paramLikes);

  @Path("/{id}")
  @DELETE
  public abstract Response deleteLike(@PathParam("id") Long paramLong);
}

/* Location:           E:\project\tella-webservice\WEB-INF\classes\
 * Qualified Name:     com.tella.rest.service.LikesService
 * JD-Core Version:    0.6.2
 */