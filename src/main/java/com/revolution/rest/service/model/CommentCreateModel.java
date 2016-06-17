 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 
 public class CommentCreateModel
   implements Serializable
 {
   private static final long serialVersionUID = 7352436963855725689L;
   private String content;
   private Long story_id;
   private String comment_image;
 
   public String getContent()
   {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 
   public Long getStory_id() {
     return this.story_id;
   }
 
   public void setStory_id(Long story_id) {
     this.story_id = story_id;
   }

public String getComment_image() {
	return comment_image;
}

public void setComment_image(String comment_image) {
	this.comment_image = comment_image;
}
   
   
   
 }

