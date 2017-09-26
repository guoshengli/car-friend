package com.friend.rest.service;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.friend.rest.model.Comment;

@Path("/comments")
@Produces({"application/json"})
public abstract interface CommentService
{
  @POST
  @Consumes({"application/json"})
  public abstract Response createComment(Comment paramComment);

  @Path("/story/{storyId}")
  @GET
  public abstract List<Comment> getAllComment(@PathParam("storyId") Long paramLong);

  @Path("/{commentId}")
  @GET
  public abstract Comment getComment(@PathParam("commentId") Long paramLong);

  @Path("/{commentId}")
  @PUT
  @Consumes({"application/json"})
  public abstract Response updateComment(@PathParam("commentId") Long paramLong, Comment paramComment);
}
