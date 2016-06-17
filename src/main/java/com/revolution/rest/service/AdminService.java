package com.revolution.rest.service;

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
import com.revolution.rest.service.model.CollectionModel;
import com.revolution.rest.service.model.ReportModel;
import com.revolution.rest.service.model.StoryModel;

import net.sf.json.JSONObject;

@Path("/admin")
@Produces({"application/json"})
public abstract interface AdminService
{
  @Path("/collection")
  @POST
  @Consumes({"application/json"})
  public abstract Response createCollection(@HeaderParam("X-Tella-Request-Userid") Long paramLong, JSONObject paramJSONObject);

  @Path("/collection/{collection_id}")
  @PUT
  public abstract Response updateCollection(@PathParam("collection_id") Long collection_id);

  
  @Path("/collection/{collection_id}")
  @DELETE
  public abstract Response deleteCollection(@PathParam("collection_id") Long paramLong);

  @Path("/collection/{collectionId}/stories/{storyId}")
  @POST
  public abstract Response createCollectionStory(@PathParam("collectionId") Long paramLong1, @PathParam("storyId") Long paramLong2,@HeaderParam("X-Tella-Request-Userid") Long loginUserid);

  @Path("/collection/{collectionId}/stories/{storyId}")
  @DELETE
  public abstract Response deleteCollectionStory(@PathParam("collectionId") Long paramLong1, @PathParam("storyId") Long paramLong2);

  @Path("/collection/{storyId}")
  @GET
  public abstract List<CollectionModel> getCollections(@PathParam("storyId") Long paramLong);

  @Path("/collection")
  @GET
  public abstract List<CollectionIntro> getCollections();

  @Path("/stories")
  @GET
  public abstract List<StoryModel> getStoryByTime(@HeaderParam("X-Tella-Request-Userid") Long paramLong, @Context HttpServletRequest paramHttpServletRequest);

  @Path("/slide")
  @POST
  @Consumes({"application/json"})
  public abstract Response createSlide(@HeaderParam("X-Tella-Request-Userid") Long paramLong, JSONObject paramJSONObject)throws Exception;

  @Path("/stories/{storyId}/hide")
  @POST
  public abstract Response reportStory(@PathParam("storyId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/comments/{commentId}/hide")
  @POST
  public abstract Response reportComment(@PathParam("commentId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/reports")
  @GET
  public abstract List<ReportModel> getReports(@Context HttpServletRequest paramHttpServletRequest);

  @Path("/reports/{reportId}")
  @POST
  public abstract Response confirmReport(@PathParam("reportId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/reports/{reportId}")
  @DELETE
  public abstract Response ignoreReport(@PathParam("reportId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/reports/{reportId}")
  @PUT
  public abstract Response revokeReport(@PathParam("reportId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/featured/user/{userId}")
  @POST
  public abstract Response addFeaturedUser(@PathParam("userId") Long paramLong);

  @Path("/featured/user/{userId}")
  @DELETE
  public abstract Response removeFeaturedUser(@PathParam("userId") Long paramLong);

  @Path("/featured/collection/{collectionId}")
  @POST
  public abstract Response addFeaturedCollection(@PathParam("collectionId") Long collectionId);

  @Path("/featured/collection/{collectionId}")
  @DELETE
  public abstract Response removeFeaturedCollection(@PathParam("collectionId") Long collectionId);

  
  @Path("/stories/{story_id}/recommend")
  @PUT
  public abstract Response recommendStory(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@PathParam("story_id") Long paramLong, @Context HttpServletRequest paramHttpServletRequest);

  @Path("/user/{user_id}")
  @PUT
  public abstract Response updateUserType(@PathParam("user_id") Long paramLong, @Context HttpServletRequest paramHttpServletRequest);

  @Path("/user/{user_id}/publisher_info")
  @POST
  public abstract Response createPublisherInfo(@PathParam("user_id") Long paramLong, JSONObject paramJSONObject);

  @Path("/publisher_info/{publisher_info_id}")
  @DELETE
  public abstract Response removePublisher(@PathParam("publisher_info_id") Long paramLong);
  
  @Path("/userfeedback")
  @GET
  public abstract List<JSONObject> getFeedback(@Context HttpServletRequest request);
  
  @Path("/allusernotification")
  @POST
  public abstract Response allusernotification(JSONObject json);
  
  @Path("/slide/{id}")
  @DELETE
  public abstract Response delSlide(@PathParam("id")Long id);
  @Path("/exception")
  @GET
  public void createException();
}

