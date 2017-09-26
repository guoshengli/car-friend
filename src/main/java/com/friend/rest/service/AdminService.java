package com.friend.rest.service;


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


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/admin")
@Produces({"application/json"})
public abstract interface AdminService
{
//  @Path("/collection")
//  @POST
//  @Consumes({"application/json"})
//  public abstract Response createCollection(@HeaderParam("X-Tella-Request-Userid") Long paramLong, JSONObject paramJSONObject);
//
//  @Path("/collection/{collection_id}")
//  @PUT
//  public abstract Response updateCollection(@PathParam("collection_id") Long collection_id);
//
//  
//  @Path("/collection/{collection_id}")
//  @DELETE
//  public abstract Response deleteCollection(@PathParam("collection_id") Long paramLong);
//
//  @Path("/collection/stories/{storyId}")
//  @POST
//  public abstract Response createCollectionStory(@PathParam("storyId") Long paramLong2,JSONObject collectionIds,@HeaderParam("X-Tella-Request-Userid") Long loginUserid);
//
//  @Path("/collection/{collectionId}/stories/{storyId}")
//  @DELETE
//  public abstract Response deleteCollectionStory(@PathParam("collectionId") Long paramLong1, @PathParam("storyId") Long paramLong2);
//
//  @Path("/collection/{storyId}")
//  @GET
//  public abstract List<CollectionModel> getCollections(@PathParam("storyId") Long paramLong);
//
//  @Path("/collection")
//  @GET
//  public abstract List<CollectionIntro> getCollections();
//
//  @Path("/stories")
//  @GET
//  public abstract List<StoryModel> getStoryByTime(@HeaderParam("X-Tella-Request-Userid") Long paramLong, @Context HttpServletRequest paramHttpServletRequest);
//
//  @Path("/slide")
//  @POST
//  @Consumes({"application/json"})
//  public abstract Response createSlide(@HeaderParam("X-Tella-Request-Userid") Long paramLong, JSONObject paramJSONObject)throws Exception;
//
//  @Path("/stories/{storyId}/hide")
//  @POST
//  public abstract Response reportStory(@PathParam("storyId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);
//
//  @Path("/comments/{commentId}/hide")
//  @POST
//  public abstract Response reportComment(@PathParam("commentId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);
//
//  @Path("/reports")
//  @GET
//  public abstract List<ReportModel> getReports(@Context HttpServletRequest paramHttpServletRequest);
//
//  @Path("/reports/{reportId}")
//  @POST
//  public abstract Response confirmReport(@PathParam("reportId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);
//
//  @Path("/reports/{reportId}")
//  @DELETE
//  public abstract Response ignoreReport(@PathParam("reportId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);
//
//  @Path("/reports/{reportId}")
//  @PUT
//  public abstract Response revokeReport(@PathParam("reportId") Long paramLong1, @HeaderParam("X-Tella-Request-Userid") Long paramLong2);
//
//  @Path("/featured/user/{userId}")
//  @POST
//  public abstract Response addFeaturedUser(@PathParam("userId") Long paramLong);
//
//  @Path("/featured/user/{userId}")
//  @DELETE
//  public abstract Response removeFeaturedUser(@PathParam("userId") Long paramLong);
//
//  @Path("/featured/collection/{collectionId}")
//  @POST
//  public abstract Response addFeaturedCollection(@PathParam("collectionId") Long collectionId);
//
//  @Path("/featured/collection/{collectionId}")
//  @DELETE
//  public abstract Response removeFeaturedCollection(@PathParam("collectionId") Long collectionId);
//
//  
//  @Path("/stories/{story_id}/recommend")
//  @PUT
//  public abstract Response recommendStory(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@PathParam("story_id") Long paramLong, @Context HttpServletRequest paramHttpServletRequest);
//
//  @Path("/stories/{story_id}/cancelrecommend")
//  @DELETE
//  public abstract Response unrecommendStory(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@PathParam("story_id") Long paramLong, @Context HttpServletRequest paramHttpServletRequest);
//
//  
//  @Path("/user/{user_id}")
//  @PUT
//  public abstract Response updateUserType(@PathParam("user_id") Long paramLong, @Context HttpServletRequest paramHttpServletRequest);
//
//  @Path("/user/{user_id}/publisher_info")
//  @POST
//  public abstract Response createPublisherInfo(@PathParam("user_id") Long paramLong, JSONObject paramJSONObject);
//
//  @Path("/publisher_info/{publisher_info_id}")
//  @DELETE
//  public abstract Response removePublisher(@PathParam("publisher_info_id") Long paramLong);
//  
//  @Path("/userfeedback")
//  @GET
//  public abstract List<JSONObject> getFeedback(@Context HttpServletRequest request);
//  
//  @Path("/allusernotification")
//  @POST
//  public abstract Response allusernotification(JSONObject json);
//  
//  @Path("/slide/{id}")
//  @DELETE
//  public abstract Response delSlide(@PathParam("id")Long id);
//  @Path("/exception")
//  @GET
//  public void createException();
//  
//  @Path("/interests")
//  @GET
//  public List<JSONObject> getAllInterest();
//  
//  @Path("/interest")
//  @POST
//  @Consumes({"application/json"})
//  public abstract Response createInterest(JSONObject json);
//  
//  @Path("/interest/{interest_id}")
//  @PUT
//  public abstract Response updateInterest(@PathParam("interest_id")Long interest_id,JSONObject interest);
//  
//  @Path("/columns")
//  @POST
//  @Consumes({"application/json"})
//  public abstract Response createColumns(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,JSONObject json);
//  
//  @Path("/columns/{columns_id}")
//  @PUT
//  public abstract Response updateColumns(@PathParam("columns_id")Long columns_id,JSONObject columns);
//  
//  @Path("/columns/{columnsId}/story/{storyId}")
//  @POST
//  public abstract Response createColumnsStory(@PathParam("columnsId")Long columnsId,@PathParam("storyId")Long storyId,@HeaderParam("X-Tella-Request-Userid") Long loginUserid);
//  
//  @Path("/columns/{columnsId}/story/{storyId}")
//  @DELETE
//  public abstract Response deleteColumnsStory(@PathParam("columnsId")Long columnsId,@PathParam("storyId")Long storyId);
//  
//  @Path("/columns/{columnsId}/story/{storyId}")
//  @PUT
//  public abstract Response updateColumnsStory(@PathParam("columnsId")Long columnsId,@PathParam("storyId")Long storyId,@HeaderParam("X-Tella-Request-Userid") Long loginUserid);
//  
//  @Path("/users/privatechat")
//  @GET
//  public abstract Response getConversion(@Context HttpServletRequest request);
//  
//  @Path("/{storyId}/lottery/{count}")
//  @GET
//  public abstract Response getLottery(@PathParam("storyId")Long storyId,@PathParam("count")int count);
//  
  //---------------------------------car-friend ------------------
  @Path("/login")
  @POST
  public abstract Response adminLogin(JSONObject adminJson,@Context HttpServletRequest request);
  
  @Path("/add_user")
  @POST
  public abstract Response add_user(JSONObject adminJson,@Context HttpServletRequest request);
  
  @Path("/{admin_id}")
  @DELETE
  public abstract Response delete_user(@PathParam("admin_id") Long admin_id);
  
  @Path("/{admin_id}")
  @PUT
  public abstract Response update_user(@PathParam("admin_id") Long admin_id,JSONObject user);
  
  @Path("/all")
  @GET
  public abstract Response get_all_user();
  
  @Path("/collection/search")
  @GET
  public abstract Response search(@Context HttpServletRequest request);
  
  @Path("/nav/add")
  @POST
  public abstract Response add_collection_nav(JSONObject json);
  
  @Path("/nav/{nav_id}")
  @DELETE
  public abstract Response del_collection_nav(@PathParam("nav_id") Long nav_id);
  
  @Path("/nav")
  @GET
  public abstract Response nav_list(@Context HttpServletRequest request);
  
  @Path("/nav/collections")
  @GET
  public abstract Response nav_collection_list();
  
  @Path("/nav_sequence")
  @PUT
  public abstract Response nav_sequence(JSONArray arr);
  
  @Path("/collection/list")
  @GET
  public abstract Response get_collection_all();
 
  @Path("/collection/{category_id}")
  @GET
  public abstract Response get_collection_category(@PathParam("category_id")Long category_id);
  
  @Path("/collection/add_car")
  @POST
  public abstract Response add_car(JSONObject json,@Context HttpServletRequest request)throws Exception;
  
  @Path("/collection/update_car")
  @PUT
  public abstract Response update_car(JSONObject json,@Context HttpServletRequest request)throws Exception;
  
  @Path("/collection/add_club")
  @POST
  public abstract Response add_club(JSONObject json,@Context HttpServletRequest request)throws Exception;
  
  @Path("/collection/club/{car_id}")
  @GET
  public abstract Response get_club(@PathParam("car_id")int car_id)throws Exception;
  
  @Path("/collection/all/{type}")
  @GET
  public abstract Response get_all_collection(@PathParam("type") int type)throws Exception;
  
  @Path("/collection/car_club")
  @POST
  public abstract Response car_club(JSONObject car_club,@Context HttpServletRequest request)throws Exception;
  
  @Path("/collection/update_club")
  @PUT
  public abstract Response update_club(JSONObject json,@Context HttpServletRequest request)throws Exception;
  
  @Path("/collection/add_column")
  @POST
  public abstract Response add_column(JSONObject json,@Context HttpServletRequest request)throws Exception;
  
  @Path("/collection/update_column")
  @PUT
  public abstract Response update_column(JSONObject json,@Context HttpServletRequest request)throws Exception;
  
  @Path("/collection/content")
  @GET
  public abstract Response collection_content_all(@Context HttpServletRequest request)throws Exception;
  
  
  @Path("/category/list")
  @GET
  public abstract Response category_list();
  
  @Path("/category/{category_id}/collections")
  @GET
  public abstract Response category_collections(@PathParam("category_id")Long category_id);
  
  @Path("/category/add")
  @POST
  public abstract Response add_category(JSONObject json);
  
  @Path("/category/update")
  @PUT
  public abstract Response update_category(JSONObject json);
  
  @Path("/category/sequence")
  @PUT
  public abstract Response update_sequence(JSONArray arr);
  
  @Path("/category/collection/add")
  @POST
  public abstract Response add_category_collection(JSONObject json);
  
  @Path("/category/collection/sequence")
  @PUT
  public abstract Response update_category_collection_sequence(JSONArray arr);
  
  @Path("/category/collection/delete")
  @DELETE
  public abstract Response delete_category_collection(JSONObject json);
  
  @Path("/category/collection/all")
  @DELETE
  public abstract Response get_collection_content(JSONObject json,@Context HttpServletRequest request)throws Exception;
  
  @Path("/category/collection/feature")
  @DELETE
  public abstract Response get_collection_feature_content(JSONObject json,@Context HttpServletRequest request)throws Exception;
  
  @Path("/content/add_feature/{content_id}")
  @POST
  public abstract Response add_feature(@PathParam("content_id") int content_id,@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request)throws Exception;
  
  @Path("/content/status")
  @PUT
  public abstract Response content_status(JSONObject json,@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request)throws Exception;
  
  @Path("/content/all")
  @GET
  public abstract Response all_content(@Context HttpServletRequest request)throws Exception;
  
  @Path("/content/feature")
  @GET
  public abstract Response feature_content(@Context HttpServletRequest request)throws Exception;
  
  @Path("/districts")
  @GET
  public abstract Response districts_list(@Context HttpServletRequest request);
  
  /**
	 * @api {POST} /admin/message/add 添加推送通知
	 * @apiGroup SendMessage
	 * @apiVersion 0.0.1
	 * @apiDescription 添加推送通知
	 * @apiParam {String} title 内容标题
	 * @apiParam {String} type 通知类型类型 poi content url
	 * @apiParam {Long} reference_id 平台内内容Id、POI id
	 * @apiParam {String} url 链接
	 * @apiParam {int} send_type 推送类型 0 即时没有send_time 1 定时传send_time
	 * @apiParam {String} send_time 推送时间
	 * @apiParamExample {json} 范例
	 *                {
	 *                	"title":"标题",
	 *                  "type":"轮播图类型",
	 *                  "reference_id":21,
	 *                  "url":"www.baidu.co",
	 *                  "send_time":"2017-12-03 12:00",
	 *                  "send_type":0
	 *                }
	 * @apiSuccess (200) {String} status success
	 * @apiSuccessExample {json} 返回范例:
	 *                {
	 *                	"status":"success",
	 *                }
	 */
	@Path("/message/add")
	@POST
	@Consumes({"application/json"})
	public Response add_message(JSONObject content,@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request)throws Exception;
	
	/**
	 * @api {GET} /admin/message/list 推送通知列表
	 * @apiGroup SendMessage
	 * @apiVersion 0.0.1
	 * @apiDescription 推送通知列表
	 * @apiParam {String} title 通知标题
	 * @apiSuccessExample {json} 返回数据:
	 * 			[
	 * 				{
	 *                	"title":"标题",
	 *                  "type":"轮播图类型",
	 *                  "reference_id":21,
	 *                  "url":"www.baidu.co",
	 *                  "send_time":"2017-02-24 12:00"
	 *                },
	 *                {
	 *                	"title":"标题",
	 *                  "type":"轮播图类型",
	 *                  "reference_id":21,
	 *                  "url":"www.baidu.co",
	 *                  "send_time":"2017-02-24 12:00"
	 *                }
	 *           ]
   *  @apiErrorExample {json} Error-Response: 
   *  HTTP/1.1 400 Not Found 
   *  { 
   *   status:'内容不存在'
   *   code:10003, 
   *   error_message:'内容不存在', 
   *   }
	 */
	@Path("/message/list")
	@GET
	public Response message_list(@Context HttpServletRequest request,@HeaderParam("X-Tella-Request-Userid") Long loginUserid);
  
}

