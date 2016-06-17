/*    */ package com.revolution.rest.service.mock;
import com.revolution.rest.dao.TimelineDao;
import com.revolution.rest.model.Timeline;
import com.revolution.rest.service.TimelineService;

/*    */ import javax.ws.rs.core.Response;
/*    */ import javax.ws.rs.core.Response.ResponseBuilder;
/*    */ import javax.ws.rs.core.Response.Status;
/*    */ import org.springframework.beans.factory.annotation.Autowired;
/*    */ 
/*    */ public class TimelineServiceMock
/*    */   implements TimelineService
/*    */ {
/*    */ 
/*    */   @Autowired
/*    */   private TimelineDao timelineDao;
/*    */ 
/*    */   public Response create(Timeline timeline)
/*    */   {
/*    */     try
/*    */     {
/* 18 */       this.timelineDao.saveOrUpdate(timeline);
/*    */     } catch (Exception e) {
/* 20 */       return null;
/*    */     }
/*    */ 
/* 23 */     return Response.status(Response.Status.CREATED)
/* 24 */       .entity("Timeline Created")
/* 25 */       .build();
/*    */   }
/*    */ 
/*    */   public Timeline get(Long timelineId)
/*    */   {
/* 30 */     Timeline timeline = (Timeline)this.timelineDao.get(timelineId);
/* 31 */     return timeline;
/*    */   }
/*    */ }

/* Location:           E:\project\tella-webservice\WEB-INF\classes\
 * Qualified Name:     com.tella.rest.service.mock.TimelineServiceMock
 * JD-Core Version:    0.6.2
 */