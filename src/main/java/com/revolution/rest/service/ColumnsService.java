package com.revolution.rest.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.revolution.rest.service.model.ColumnsModel;

import net.sf.json.JSONObject;


@Path("/columns")
@Produces({ "application/json" })
public interface ColumnsService {
	@Path("/{columns_id}/story/{story_id}")
	@POST
	@Consumes({ "application/json" })
	public abstract Response createColumnsStory(@HeaderParam("X-Tella-Request-Userid")Long loginUserid,
			@PathParam("columns_id")Long columns_id,@PathParam("story_id") Long story_id);
	
	@Path("/{columns_id}")
	@GET
	public abstract List<JSONObject> getStoryByColumnsId(@PathParam("columns_id")Long columns_id,@HeaderParam("X-Tella-Request-Userid")Long loginUserid,@Context HttpServletRequest request);
	
	@Path("/")
	@GET
	public abstract List<ColumnsModel> getAllColumns();
}
