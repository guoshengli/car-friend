package com.friend.rest.service;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.friend.rest.service.model.CommentModel;
import com.friend.rest.service.model.ReplyCommentModel;
import com.friend.rest.service.model.UserIntro;

import net.sf.json.JSONObject;

@Path("/stories")
@Produces({"application/json"})
public abstract interface StoryService
{
  @POST
  @Consumes({"application/json"})
  public abstract Response createStory(JSONObject paramJSONObject, @HeaderParam("X-Tella-Request-Userid") int paramLong, @Context HttpServletRequest paramHttpServletRequest)throws Exception;


  @Path("/{storyId}/unpublish")
  @PUT
  public abstract Response unpublishStory(@PathParam("storyId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @POST
  @Path("/{storyId}/comments")
  @Consumes({"application/json"})
  public abstract Response createComment(JSONObject paramCommentCreateModel, @PathParam("storyId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2,@Context HttpServletRequest request);

  @Path("/{storyId}/comments/{commentId}")
  @POST
  @Consumes({"application/json"})
  public abstract Response createReplyComment(JSONObject paramReplyComment, @PathParam("storyId") Long paramLong1, @PathParam("commentId") Long paramLong2, @HeaderParam("X-Tella-Request-Userid") Long paramLong3,@Context HttpServletRequest request);
  
  @Path("/{storyId}/comments/{commentId}/users/{userId}")
  @POST
  @Consumes({"application/json"})
  public abstract Response createChatComment(JSONObject paramReplyComment, @PathParam("storyId") Long storyId, @PathParam("commentId") Long commentId, @PathParam("userId") Long userId, @HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request);

  @Path("/{storyId}/comments")
  @GET
  public abstract Response getComments(@PathParam("storyId") Long paramLong, @Context HttpServletRequest request);

  @Path("/{storyId}/comments/{commentId}")
  @GET
  public abstract Response getRepliesComments(@PathParam("storyId") Long paramLong1, @PathParam("commentId") Long paramLong2, @Context HttpServletRequest paramHttpServletRequest);

  @Path("/{storyId}")
  @GET
  public abstract Response getStory(@PathParam("storyId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2,@HeaderParam("X-Tella-Request-AppVersion") String version,@Context HttpServletRequest request);

//  @Path("/collection/{collectionId}")
//  @GET
//  public abstract List<Story> getAllByCollectionId(@PathParam("collectionId") Long paramLong);

  @Path("/{storyId}")
  @PUT
  @Consumes({"application/json"})
  public abstract Response updateStory(@PathParam("storyId") Long paramLong1, JSONObject paramJSONObject,
		  @HeaderParam("X-Tella-Request-Userid") Long paramLong2,@Context HttpServletRequest request);

  @Path("/{storyId}")
  @DELETE
  public abstract Response deleteStory(@PathParam("storyId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2,@Context HttpServletRequest request);

  @Path("/comments/{commentId}")
  @GET
  public abstract CommentModel getComment(@PathParam("commentId") Long paramLong);

  @Path("/{storyId}/comments/{commentId}")
  @DELETE
  public abstract Response deleteComment(@PathParam("storyId") Long paramLong1, @PathParam("commentId") Long paramLong2, @HeaderParam("X-Tella-Request-Userid") Long paramLong3,@Context HttpServletRequest request);

  @Path("/{storyId}/likes")
  @POST
  public abstract Response createLikes(@PathParam("storyId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2,@Context HttpServletRequest request);
  
//  @Path("/add_like")
//  @POST
//  public abstract Response createLike(JSONObject json,@Context HttpServletRequest request);

  @Path("/{storyId}/likes")
  @DELETE
  public abstract Response deleteLike(@PathParam("storyId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

  @Path("/{storyId}/repost")
  @POST
  public abstract Response createRepost(@PathParam("storyId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2,@Context HttpServletRequest request);

  @Path("/{storyId}/repost")
  @DELETE
  public abstract Response deleteRepost(@PathParam("storyId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2,@Context HttpServletRequest request);

//  @Path("/{storyId}/report")
//  @POST
//  public abstract Response reportStory(@PathParam("storyId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

//  @Path("/{storyId}/comments/{commentId}/report")
//  @POST
//  public abstract Response reportComment(@PathParam("commentId") Long paramLong1, @PathParam("storyId") Long paramLong2, @HeaderParam("X-Tella-Request-Userid") Long paramLong3);

  @Path("/{storyId}/likes/user")
  @GET
  public abstract List<UserIntro> getLikeStoryUserByStoryId(@PathParam("storyId") Long paramLong1, @Context HttpServletRequest paramHttpServletRequest, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);

//  @Path("/{storyId}/reposts/user")
//  @GET
//  public abstract List<UserIntro> getRepostStoryUserByStoryId(@PathParam("storyId") Long paramLong1, @Context HttpServletRequest paramHttpServletRequest, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);
//  @Path("/discover")
//  @GET
//  public JSONObject getEventModelsRand(@HeaderParam("X-Tella-Request-Userid") Long loginUserid);
//  
  @Path("/{storyId}/resource")
  @PUT
  public Response addResource(@PathParam("storyId")Long storyId,
		  @QueryParam("resource")String resource);
  
  @Path("/{storyId}/comments/count")
  @GET
  public Response commentcounts(@PathParam("storyId")Long storyId);
  
  @Path("/{storyId}/commentcounts")
  @GET
  public Response comment_counts(@PathParam("storyId")Long storyId);
  
  
  
//  @Path("/invitation")
//  @POST
//  public Response getFbInvitation(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@HeaderParam("X-Tella-Request-FbToken") String fbToken,JSONObject invitation)throws Exception;
//  
//  @Path("/synchroniseEdit")
//  @POST
//  @Consumes({"application/json"})
//  public Response synchroniseEditStory(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@HeaderParam("X-Tella-Request-FbToken") String fbToken,JSONObject invitation)throws Exception;
  
  
  
  
}

