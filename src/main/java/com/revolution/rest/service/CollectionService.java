package com.revolution.rest.service;
import net.sf.json.JSONObject;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.revolution.rest.service.model.CollectionIntro;
import com.revolution.rest.service.model.UserIntro;

@Path("/collections")
@Produces({"application/json"})
public abstract interface CollectionService
{
  @Path("/")
  @POST
  public abstract Response create(@HeaderParam("X-Tella-Request-Userid") Long paramLong, JSONObject paramJSONObject);
  
  @Path("/")
  @GET
  public abstract Response getCollections(@HeaderParam("X-Tella-Request-Userid") Long paramLong);
  
  @Path("/all")
  @GET
  public abstract Response getCollectionsAll(@HeaderParam("X-Tella-Request-Userid") Long paramLong);

  @Path("/{collectionId}")
  @PUT
  @Consumes({"application/json"})
  public abstract Response updateCollection(@PathParam("collectionId") Long paramLong, JSONObject collection);

  @Path("/{collectionId}/timeline")
  @GET
  public abstract List<JSONObject> getTimelinesByCollecionId(@PathParam("collectionId") Long paramLong1, @Context HttpServletRequest paramHttpServletRequest, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/{collectionId}/featured")
  @GET
  public abstract JSONObject getFeaturedStoriesByCollection_id(@PathParam("collectionId") Long paramLong1, @Context HttpServletRequest paramHttpServletRequest, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);
  
  @Path("/{collectionId}/hot")
  @GET
  public abstract List<JSONObject> getHotStoriesByCollection_id(@PathParam("collectionId") Long paramLong1, @Context HttpServletRequest paramHttpServletRequest, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);
  
  @Path("/{collectionId}/featured_is_following")
  @GET
  public JSONObject getFeaturedStoriesByFollowing(@PathParam("collectionId") Long collectionId, @Context HttpServletRequest request, @HeaderParam("X-Tella-Request-Userid") Long loginUserid);
  
  @Path("/featured")
  @GET
  public abstract List<JSONObject> getFeaturedStoriesByCollections(@Context HttpServletRequest paramHttpServletRequest, @HeaderParam("X-Tella-Request-Userid") Long loginUserid);
  
  @Path("/follow")
  @POST
  public Response followCollection(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,JSONObject collectionIds);
  
  @Path("/follow/{collection_id}")
  @DELETE
  public Response unfollowCollection(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@PathParam("collection_id")Long collection_id);
  
  @Path("/order")
  @PUT
  @Consumes({"application/json"})
  public abstract Response updateUserCollection(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,JSONObject user_collection);
  
  @Path("/{collectionId}/stories/{storyId}")
  @DELETE
  public abstract Response deleteCollectionStory(@PathParam("collectionId") Long paramLong1, @PathParam("storyId") Long paramLong2)throws Exception;

  @Path("/{collectionId}/stories/{storyId}")
  @PUT
  @Consumes({"application/json"})
  public Response updateCollectionStory(@PathParam("collectionId") Long paramLong1, @PathParam("storyId") Long paramLong2,JSONObject status)throws Exception;
  
  @Path("/{collection_id}/following")
  @GET
  public abstract List<UserIntro> getFollowingByCollectionId(@PathParam("collection_id") Long collection_id, @Context HttpServletRequest request, @HeaderParam("X-Tella-Request-Userid") Long loginUserid);
  
  @Path("/{user_id}/follow")
  @GET
  public abstract List<CollectionIntro> getCollectionByFollow(@PathParam("user_id") Long user_id, @Context HttpServletRequest request);
}

