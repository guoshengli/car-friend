 package com.revolution.rest.service;
 
 import com.revolution.rest.dao.CollectionStoryDao;
import com.revolution.rest.model.CollectionStory;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

 
 @Transactional
 public class CollectionStoryServiceImpl
   implements CollectionStoryService
 {
 
   @Autowired
   private CollectionStoryDao collectionStoryDao;
 
   public Response create(CollectionStory cs)
   {
     try
     {
       this.collectionStoryDao.saveOrUpdate(cs);
     } catch (Exception e) {
       return null;
     }
     return Response.status(Response.Status.CREATED)
       .entity("CollectionStory created").build();
   }
 
   public Response delete(Long collectionId, Long storyId)
   {
     this.collectionStoryDao.delete(collectionId, storyId);
 
     return Response.status(Response.Status.OK).entity("CollectionStory deleted").build();
   }
 }

