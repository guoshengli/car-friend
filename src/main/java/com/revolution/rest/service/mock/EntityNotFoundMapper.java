/*    */ package com.revolution.rest.service.mock;
/*    */ 
/*    */ import javax.persistence.EntityNotFoundException;
/*    */ import javax.ws.rs.core.Response;
/*    */ import javax.ws.rs.core.Response.ResponseBuilder;
/*    */ import javax.ws.rs.core.Response.Status;
/*    */ import javax.ws.rs.ext.ExceptionMapper;
/*    */ import javax.ws.rs.ext.Provider;
/*    */ 
/*    */ @Provider
/*    */ public class EntityNotFoundMapper
/*    */   implements ExceptionMapper<EntityNotFoundException>
/*    */ {
/*    */   public Response toResponse(EntityNotFoundException exception)
/*    */   {
/* 13 */     return Response.status(Response.Status.NOT_FOUND).entity("").build();
/*    */   }
/*    */ }

/* Location:           E:\project\tella-webservice\WEB-INF\classes\
 * Qualified Name:     com.tella.rest.service.mock.EntityNotFoundMapper
 * JD-Core Version:    0.6.2
 */