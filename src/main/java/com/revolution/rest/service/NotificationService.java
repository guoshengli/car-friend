package com.revolution.rest.service;

import com.revolution.rest.model.Notification;
import com.revolution.rest.service.model.NotificationModel;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import net.sf.json.JSONObject;

@Path("/notifications")
@Produces({"application/json"})
public abstract interface NotificationService
{
  @Path("/{notificationId}")
  @GET
  public abstract Notification getNotification(@PathParam("notificationId") Long paramLong);

  @Path("/users/{userId}")
  @GET
  public abstract List<NotificationModel> getNotifications(@PathParam("userId") Long paramLong,@Context HttpServletRequest request);
  
  @Path("/collections/{userId}")
  @GET
  public abstract List<NotificationModel> getNotificationsByCollection(@PathParam("userId") Long paramLong,@Context HttpServletRequest request);

  @Path("/notifications_info")
  @POST
  public abstract Response notificationInfo(@HeaderParam("X-Tella-Request-Userid") Long paramLong, JSONObject paramJSONObject);

  @Path("/notifications_info/del")
  @DELETE
  public abstract Response deleteNotificationInfo(@HeaderParam("X-Tella-Request-Userid") Long paramLong, JSONObject paramJSONObject);
  
  @Path("/friend_dynamics")
  @GET
  public abstract List<NotificationModel> friend_dynamics(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request);
  
  @Path("/friend_dynamics/last_id")
  @GET
  public abstract Response friend_dynamics_lastid(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request);
  
  @Path("/collection_dynamics/last_id")
  @GET
  public abstract Response collection_dynamics_lastid(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request);
  
}

