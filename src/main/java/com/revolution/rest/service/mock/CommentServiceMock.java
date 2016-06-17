/*    */ package com.revolution.rest.service.mock;
import com.revolution.rest.model.Comment;
import com.revolution.rest.service.CommentService;

/*    */ import java.util.List;
/*    */ import javax.ws.rs.core.Response;
/*    */ 
/*    */ public class CommentServiceMock
/*    */   implements CommentService
/*    */ {
/*    */   public Response createComment(Comment comment)
/*    */   {
/* 15 */     return null;
/*    */   }
/*    */ 
/*    */   public List<Comment> getAllComment(Long storyId)
/*    */   {
/* 20 */     return null;
/*    */   }
/*    */ 
/*    */   public Comment getComment(Long commentId)
/*    */   {
/* 26 */     return null;
/*    */   }
/*    */ 
/*    */   public Response updateComment(Long commentId, Comment comment)
/*    */   {
/* 32 */     return null;
/*    */   }
/*    */ }

/* Location:           E:\project\tella-webservice\WEB-INF\classes\
 * Qualified Name:     com.tella.rest.service.mock.CommentServiceMock
 * JD-Core Version:    0.6.2
 */