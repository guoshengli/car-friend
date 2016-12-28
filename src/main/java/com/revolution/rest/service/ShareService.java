package com.revolution.rest.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.revolution.rest.service.model.LinkAccountWebModel;

import net.sf.json.JSONObject;

@Path("/web")
@Produces({"application/json"})
public interface ShareService {
	@Path("/stories/{story_code}")
	@GET
	public Response getStory(@PathParam("story_code") String story_code, @HeaderParam("X-Tella-Request-Userid") Long loginUserid);
	
	@Path("/collections/{collection_id}")
	@GET
	public Response getCollectionStory(@PathParam("collection_id") Long collection_id,@Context HttpServletRequest request);
	
	@Path("/homepage")
	@GET
	public abstract JSONObject getTimelinesBySlidesColumns(@HeaderParam("X-Tella-Request-Userid") Long paramLong, @Context HttpServletRequest paramHttpServletRequest, @Context HttpServletResponse response,@HeaderParam("X-Tella-Request-AppVersion") String appVersion);
	 
	@Path("/columns/{columns_id}")
	@GET
	public abstract JSONObject getStoryColumnsByColumnsId(@PathParam("columns_id")Long columns_id,@HeaderParam("X-Tella-Request-Userid")Long loginUserid,@Context HttpServletRequest request);
	
	@Path("/appsignup")
	  @POST
	  @Consumes({"application/json"})
	  public abstract Response create(JSONObject paramJSONObject)throws Exception;

	 @Path("/login/linked_account")
	  @POST
	  public abstract Response loginLinkAccounts(LinkAccountWebModel paramLinkAccounts)throws Exception;
}
