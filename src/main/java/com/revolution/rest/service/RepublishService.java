package com.revolution.rest.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.revolution.rest.model.Republish;

@Path("/republish")
@Produces({ "application/json" })
public abstract interface RepublishService {
	@POST
	@Consumes({ "application/json" })
	public abstract Response createRepublish(Republish paramRepublish);

	@Path("/delete/{id}")
	@DELETE
	public abstract Response deleteRepublish(@PathParam("id") Long paramLong);
}
