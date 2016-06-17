 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class CommentSummaryModel
   implements Serializable
 {
   private static final long serialVersionUID = 4274952966889735629L;
   private Long id;
   private String content;
   private Long created_time;
   private Long story_id;
   private JSONObject author;
   
   private JSONObject comment_image;
 
   public Long getId()
   {
     return this.id;
   }
 
   public void setId(Long id) {
     this.id = id;
   }
 
   public String getContent() {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 
   public Long getCreated_time() {
     return this.created_time;
   }
 
   public void setCreated_time(Long created_time) {
     this.created_time = created_time;
   }
 
   public Long getStory_id() {
     return this.story_id;
   }
 
   public void setStory_id(Long story_id) {
     this.story_id = story_id;
   }
 
   public JSONObject getAuthor() {
     return this.author;
   }
 
   public void setAuthor(JSONObject author) {
     this.author = author;
   }

public JSONObject getComment_image() {
	return comment_image;
}

public void setComment_image(JSONObject comment_image) {
	this.comment_image = comment_image;
}
   
   
   
 }

