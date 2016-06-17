package com.revolution.rest.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.revolution.rest.model.CollectionStory;

@Path("/collectionstory")
@Produces({"application/json"})
public abstract interface CollectionStoryService
{
  @POST
  @Consumes({"application/json"})
  public abstract Response create(CollectionStory paramCollectionStory);

  @Path("/delete/{collectionId}/{storyId}")
  @DELETE
  public abstract Response delete(@PathParam("collectionId") Long paramLong1, @PathParam("storyId") Long paramLong2);
}

/* Location:           E:\project\tella-webservice\WEB-INF\classes\
 * Qualified Name:     com.tella.rest.service.CollectionStoryService
 * JD-Core Version:    0.6.2
 */