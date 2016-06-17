/*    */ package com.revolution.rest.service.mock;
import com.revolution.rest.model.Configuration;
import com.revolution.rest.service.ConfigurationService;

/*    */ import javax.ws.rs.PathParam;
/*    */ import javax.ws.rs.core.Response;
/*    */ 
/*    */ public class ConfigurationServiceMock
/*    */   implements ConfigurationService
/*    */ {
/*    */   public Response create(Configuration c)
/*    */   {
/* 14 */     return null;
/*    */   }
/*    */ 
/*    */   public Response update(@PathParam("configurationId") Long configurationId, Configuration c)
/*    */   {
/* 21 */     return null;
/*    */   }
/*    */ }

/* Location:           E:\project\tella-webservice\WEB-INF\classes\
 * Qualified Name:     com.tella.rest.service.mock.ConfigurationServiceMock
 * JD-Core Version:    0.6.2
 */