package com.friend.rest.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.friend.rest.model.Notification;

import net.sf.json.JSONObject;

@Path("/notifications")
@Produces({"application/json"})
public abstract interface NotificationService
{
//  @Path("/{notificationId}")
//  @GET
//  public abstract Notification getNotification(@PathParam("notificationId") Long paramLong);
//
//  @Path("/users/{userId}")
//  @GET
//  public abstract List<NotificationModel> getNotifications(@PathParam("userId") Long paramLong,@Context HttpServletRequest request);
//  
//  @Path("/collections/{userId}")
//  @GET
//  public abstract List<NotificationModel> getNotificationsByCollection(@PathParam("userId") Long paramLong,@Context HttpServletRequest request);
//
//  @Path("/notifications_info")
//  @POST
//  public abstract Response notificationInfo(@HeaderParam("X-Tella-Request-Userid") Long paramLong, JSONObject paramJSONObject);
//
//  @Path("/notifications_info/del")
//  @DELETE
//  public abstract Response deleteNotificationInfo(@HeaderParam("X-Tella-Request-Userid") Long paramLong, JSONObject paramJSONObject);
//  
//  @Path("/friend_dynamics")
//  @GET
//  public abstract List<NotificationModel> friend_dynamics(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request);
//  
//  @Path("/friend_dynamics/last_id")
//  @GET
//  public abstract Response friend_dynamics_lastid(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request);
//  
//  @Path("/collection_dynamics/last_id")
//  @GET
//  public abstract Response collection_dynamics_lastid(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request);
	
  @Path("/list")
  @GET
  public abstract JSONObject getNotifications(@Context HttpServletRequest request);
	/**
	 * @api {POST} /notifications/device 添加设备信息
	 * @apiGroup Notifications
	 * @apiVersion 0.0.1
	 * @apiDescription 添加设备信息
	 * @apiParam {String} client_id 个推返回的client_id
	 * @apiParam {String} device_type 设备类型（ios android）
	 * @apiParam {String} device_token 设备token
	 * @apiParamExample {json} 范例 { "client_id":"client_id",
	 *                  "device_type":"device_type",
	 *                  "device_token":"device_token" }
	 * @apiSuccessExample {json} 返回数据: 
	 * { "status":"success" }
	 */
	@Path("/device")
	@POST
	public abstract Response notificationInfo(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,
			JSONObject device,@Context HttpServletRequest request);
	
	/**
	 * @api {POST} /notifications/device 添加设备信息
	 * @apiGroup Notifications
	 * @apiVersion 0.0.1
	 * @apiDescription 添加设备信息
	 * @apiParam {String} client_id 个推返回的client_id
	 * @apiParam {String} device_type 设备类型（ios android）
	 * @apiParam {String} device_token 设备token
	 * @apiParamExample {json} 范例 { "client_id":"client_id",
	 *                  "device_type":"device_type",
	 *                  "device_token":"device_token" }
	 * @apiSuccessExample {json} 返回数据: 
	 * { "status":"success" }
	 */
	@Path("/device/clear")
	@POST
	public abstract Response clearNotification(JSONObject device);
}

