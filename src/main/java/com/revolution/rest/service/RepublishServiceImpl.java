 package com.revolution.rest.service;
 
 import com.revolution.rest.dao.RepublishDao;
import com.revolution.rest.model.Republish;

import javax.ws.rs.PathParam;
 import javax.ws.rs.core.Response;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.transaction.annotation.Transactional;
 
 @Transactional
 public class RepublishServiceImpl
   implements RepublishService
 {
 
   @Autowired
   private RepublishDao republishDao;
 
   public Response createRepublish(Republish republish)
   {
     try
     {
       this.republishDao.saveOrUpdate(republish);
     } catch (Exception e) {
       return null;
     }
 
     return Response.status(Response.Status.CREATED)
       .entity("Response created").build();
   }
 
   public Response deleteRepublish(@PathParam("id") Long id)
   {
     this.republishDao.delete(id);
     return Response.status(Response.Status.OK)
       .entity("Republish deleted").build();
   }
 }

