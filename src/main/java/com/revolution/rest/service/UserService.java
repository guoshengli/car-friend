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

import com.revolution.rest.model.Configuration;
import com.revolution.rest.model.LinkAccounts;
import com.revolution.rest.service.model.EventModel;
import com.revolution.rest.service.model.PasswordModel;
import com.revolution.rest.service.model.StoryEvent;
import com.revolution.rest.service.model.StoryPageModel;
import com.revolution.rest.service.model.UserIntro;
import com.revolution.rest.service.model.UserParentModel;

import net.sf.json.JSONObject;

@Path("/users")
@Produces({"application/json"})
public abstract interface UserService
{
  @Path("/appsignup")
  @POST
  @Consumes({"application/json"})
  public abstract Response create(JSONObject paramJSONObject,@HeaderParam("X-Tella-Request-AppVersion") String appVersion);

  @Path("/phone")
  @GET
  public abstract Response isExistPhone(@Context HttpServletRequest paramHttpServletRequest);

  @Path("/{userId}")
  @GET
  public abstract UserParentModel get(@PathParam("userId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2,@HeaderParam("X-Tella-Request-AppVersion") String appVersion);

  @Path("/login")
  @POST
  @Consumes({"application/json"})
  public abstract Response userLogin(JSONObject userJson);

  @Path("/{userId}")
  @PUT
  @Consumes({"application/json"})
  public abstract Response updateUser(@PathParam("userId") Long paramLong1, JSONObject paramUpdateUserModel, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/{userId}/pwd")
  @PUT
  @Consumes({"application/json"})
  public abstract Response updatePassword(@HeaderParam("X-Tella-Request-Userid") Long paramLong1, @PathParam("userId") Long paramLong2, PasswordModel paramPasswordModel);

  @Path("/{userId}")
  @DELETE
  public abstract Response deleteUser(@PathParam("userId") Long paramLong);

  @Path("/{userId}/stories")
  @GET
  public abstract List<JSONObject> getAllStories(@PathParam("userId") Long paramLong1, @Context HttpServletRequest paramHttpServletRequest, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/timeline")
  @GET
  public abstract List<EventModel> getTimelinesByUserId(@HeaderParam("X-Tella-Request-Userid") Long paramLong, @Context HttpServletRequest paramHttpServletRequest);

  @Path("/{userId}/timeline")
  @GET
  public abstract List<EventModel> getTimelinesByUser(@PathParam("userId") Long paramLong,@Context HttpServletRequest paramHttpServletRequest,@HeaderParam("X-Tella-Request-Userid") Long loginUserid);

  @Path("/{userId}/reposts")
  @GET
  public abstract List<StoryEvent> getRepostStories(@PathParam("userId") Long paramLong, @HeaderParam("X-Tella-Request-Userid") Long loginUserid, @Context HttpServletRequest paramHttpServletRequest);

  @Path("/{userId}/likes")
  @GET
  public abstract List<StoryEvent> getLikeStories(@PathParam("userId") Long paramLong, @HeaderParam("X-Tella-Request-Userid") Long loginUserid, @Context HttpServletRequest paramHttpServletRequest);

  @Path("/follow/{userId}")
  @POST
  @Consumes({"application/x-www-form-urlencoded"})
  public abstract Response createFollow(@PathParam("userId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2,@Context HttpServletRequest request);

  @Path("/follow")
  @POST
  public abstract Response createFollows(@HeaderParam("X-Tella-Request-Userid") Long paramLong, JSONObject paramJSONObject)
    throws Exception;

  @Path("/follow/{userId}")
  @DELETE
  public abstract Response deleteFollow(@PathParam("userId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/{userId}/configuration")
  @PUT
  public abstract Response updateConfiguration(@PathParam("userId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2, Configuration paramConfiguration);

  @Path("/{user_id}/following")
  @GET
  public abstract List<UserIntro> getFollowingByUserId(@PathParam("user_id") Long paramLong1, @Context HttpServletRequest paramHttpServletRequest, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/{user_id}/followers")
  @GET
  public abstract List<UserIntro> getFollowersByUserId(@PathParam("user_id") Long paramLong1, @Context HttpServletRequest paramHttpServletRequest, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/error/noData")
  @GET
  public abstract Response noData();

  @Path("/error/noUser")
  @GET
  public abstract Response noUser();

  @Path("/error/invalid_token")
  @GET
  public abstract Response invalidToken();
  
  @Path("/error/invalid_version")
  @GET
  public abstract Response invalidVersion();

  @Path("/error/noAuthority")
  @GET
  public abstract Response noAuthority();

  @Path("/drafts")
  @GET
  public abstract List<StoryPageModel> draftsStory(@HeaderParam("X-Tella-Request-Userid") Long paramLong);

  @Path("/login/linked_account")
  @POST
  public abstract Response loginLinkAccounts(LinkAccounts paramLinkAccounts);

  @Path("/link_account")
  @POST
  public abstract Response bindingUser(LinkAccounts paramLinkAccounts, @HeaderParam("X-Tella-Request-Userid") Long paramLong);

  @Path("/link_account/{link_account_id}")
  @DELETE
  public abstract Response unbindingUser(@PathParam("link_account_id") Long paramLong,@HeaderParam("X-Tella-Request-Userid") Long loginUserid);

  @Path("/homepage")
  @GET
  public abstract List<EventModel> getTimelinesByRecommand(@HeaderParam("X-Tella-Request-Userid") Long paramLong, @Context HttpServletRequest paramHttpServletRequest);

  @Path("/homepage_slides")
  @GET
  public abstract JSONObject getTimelinesBySlides(@HeaderParam("X-Tella-Request-Userid") Long paramLong, @Context HttpServletRequest paramHttpServletRequest,@HeaderParam("X-Tella-Request-AppVersion") String appVersion);

  @Path("/timesquare_slides")
  @GET
  public abstract JSONObject getTimelinesByTimesquare(@HeaderParam("X-Tella-Request-Userid") Long paramLong, @Context HttpServletRequest paramHttpServletRequest);

  @Path("/timesquare_collections")
  @GET
  public abstract JSONObject getStoryByCollection(@HeaderParam("X-Tella-Request-Userid") Long loginUserid, @Context HttpServletRequest request);

  
  @Path("/forgot/email")
  @GET
  public abstract Response forgetPassword(@Context HttpServletRequest paramHttpServletRequest)
    throws Exception;

  @Path("/forgot/phone")
  @GET
  public abstract Response forgetPhone(@Context HttpServletRequest paramHttpServletRequest,@HeaderParam("X-Tella-Request-AppVersion") String appVersion)
    throws Exception;

  @Path("/search")
  @GET
  public abstract Response getUserByUserName(@Context HttpServletRequest paramHttpServletRequest, @HeaderParam("X-Tella-Request-Userid") Long paramLong);
  
  @Path("/{userId}/profile")
  @GET
  public Response profile(@PathParam("userId")Long userId,@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@HeaderParam("X-Tella-Request-AppVersion") String appVersion);
  
  @Path("/phone")
  @POST
  public Response linkPhone(@Context HttpServletRequest request,@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@HeaderParam("X-Tella-Request-AppVersion") String appVersion);

  
  @Path("/phone")
  @DELETE
  public Response unlinkPhone(@Context HttpServletRequest request,@HeaderParam("X-Tella-Request-Userid") Long loginUserid);
  
  @Path("/contacts")
  @POST
  public Response contacts(JSONObject json,@HeaderParam("X-Tella-Request-Userid") Long loginUserid);

  @Path("/feedback")
  @POST
  public Response createFeedback(JSONObject feedback);
  
  @Path("/test")
  @GET
  public abstract JSONObject getTest(@HeaderParam("X-Tella-Request-Userid") Long paramLong, @Context HttpServletRequest paramHttpServletRequest);

  @Path("/{userId}/featured")
  @GET
  public Response featured(@PathParam("userId")Long userId,@HeaderParam("X-Tella-Request-Userid") Long loginUserid);
  
  @Path("/{userId}/profile_stories")
  @GET
  public Response profile_stories(@PathParam("userId")Long userId ,@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request,@HeaderParam("X-Tella-Request-AppVersion") String appVersion);
  
  @Path("/{userId}/profile_repost")
  @GET
  public Response profile_repost(@PathParam("userId")Long userId,@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request,@HeaderParam("X-Tella-Request-AppVersion") String appVersion);
  
  @Path("/{userId}/profile_collections")
  @GET
  public Response profile_collection(@PathParam("userId")Long userId,@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request,@HeaderParam("X-Tella-Request-AppVersion") String appVersion);
  
  
  @Path("/rong_token")
  @GET//@PathParam("userId")Long userId,
  public JSONObject rong_token(@HeaderParam("X-Tella-Request-Userid") Long loginUserid);

  
}

