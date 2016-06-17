 package com.revolution.rest.service;
 
 import javax.ws.rs.core.Response;
 import org.springframework.transaction.annotation.Transactional;
 
 @Transactional
 public class PingServiceImpl
   implements PingService
 {
   public Response responseMsg()
   {
     return Response.status(200).entity("This server is up and running").build();
   }
 }

