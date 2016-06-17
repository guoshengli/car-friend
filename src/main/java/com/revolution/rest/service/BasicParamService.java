package com.revolution.rest.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path("/basic_params")
@Produces({"application/json"})
public interface BasicParamService {
	 @GET
	  public abstract Response getBasicParams();
}
