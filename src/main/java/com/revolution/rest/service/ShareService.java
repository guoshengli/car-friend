package com.revolution.rest.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/web")
@Produces({"application/json"})
public interface ShareService {
	@Path("/stories/{story_code}")
	@GET
	public Response getStory(@PathParam("story_code") String story_code);
	
	@Path("/collections/{collection_id}")
	@GET
	public Response getCollectionStory(@PathParam("collection_id") Long collection_id,@Context HttpServletRequest request);
}
