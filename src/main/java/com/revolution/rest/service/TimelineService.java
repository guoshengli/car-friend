package com.revolution.rest.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.revolution.rest.model.Timeline;

@Path("/timelines")
@Produces({"application/json", "application/x-www-form-urlencoded"})
public abstract interface TimelineService
{
  @POST
  @Consumes({"application/json"})
  public abstract Response create(Timeline paramTimeline);

  @Path("/{timelineId}")
  @GET
  public abstract Timeline get(@PathParam("timelineId") Long paramLong);
}

/* Location:           E:\project\tella-webservice\WEB-INF\classes\
 * Qualified Name:     com.tella.rest.service.TimelineService
 * JD-Core Version:    0.6.2
 */