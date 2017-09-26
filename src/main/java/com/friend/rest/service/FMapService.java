package com.friend.rest.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import net.sf.json.JSONObject;

@Path("/fmap")
@Produces({"application/json"})
public interface FMapService {
	
	@Path("/add_wiki")
	@POST
	@Consumes({"application/json"})
	public Response saveFmap(JSONObject param);
	
	@Path("/{type}/{ssm_id}/timeline")
	@GET
	public Response timeline(@PathParam("type")String type,@PathParam("ssm_id")int ssm_id,@Context HttpServletRequest request);
	
	@Path("/wiki_summary")
	@GET
	public Response wiki_summary(@Context HttpServletRequest request);
	
}
