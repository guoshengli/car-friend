 package com.revolution.rest.service;
 
 import com.revolution.rest.dao.ConfigurationDao;
import com.revolution.rest.model.Configuration;

import javax.ws.rs.core.Response;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.transaction.annotation.Transactional;
 
 @Transactional
 public class ConfigurationServiceImpl
   implements ConfigurationService
 {
 
   @Autowired
   private ConfigurationDao configurationDao;
 
   public Response create(Configuration c)
   {
     try
     {
       this.configurationDao.saveOrUpdate(c);
     } catch (Exception e) {
       return null;
     }
     return Response.status(Response.Status.OK)
       .entity("Configuration created").build();
   }
 
   public Response update(Long configurationId, Configuration c)
   {
     if (!((Long)c.getId()).equals(configurationId))
       throw new IllegalArgumentException();
     this.configurationDao.update(c);
     return Response.status(Response.Status.OK)
       .entity("Configuration updated").build();
   }
 }

