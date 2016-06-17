package com.revolution.rest.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.revolution.rest.service.model.UserFeatured;

import net.sf.json.JSONObject;

@Path("/discover")
@Produces({"application/json"})
public interface DiscoverService
{
  @Path("/")
  @GET
  public JSONObject getDiscover(@HeaderParam("X-Tella-Request-Userid") Long paramLong,@Context HttpServletRequest request,@HeaderParam("X-Tella-Request-AppVersion") String version);

  @Path("/new")
  @GET
  public JSONObject getNewDiscover(@HeaderParam("X-Tella-Request-Userid") Long paramLong,@Context HttpServletRequest request,@HeaderParam("X-Tella-Request-AppVersion") String version);

  
  @Path("/users")
  @GET
  public List<UserFeatured> getUserFeature();
  
  @Path("/shuffle")
  @GET
  public List<JSONObject> getRandomFive(@HeaderParam("X-Tella-Request-Userid") Long loginUserid);

  @Path("/recommend")
  @GET
  public List<JSONObject> getCollectionByRecommand(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request);
  
  @Path("/hot")
  @GET
  public List<JSONObject> getCollectionByHot(@HeaderParam("X-Tella-Request-Userid") Long loginUserid,@Context HttpServletRequest request);
}

