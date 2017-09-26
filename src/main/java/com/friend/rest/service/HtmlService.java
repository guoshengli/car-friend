package com.friend.rest.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public interface HtmlService {
	@Path("index")
	@GET
	@Consumes("text/plain,text/html")
	public Response getIndex();
}
