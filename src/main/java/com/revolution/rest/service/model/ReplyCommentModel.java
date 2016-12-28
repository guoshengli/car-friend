 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class ReplyCommentModel
   implements Serializable
 {
   private static final long serialVersionUID = 3546216363832625689L;
   private Long id;
   private String content;
   private Long created_time;
   private Long story_id;
   private Long target_comment_id;
   private JSONObject author;
   private JSONObject target_user;
   private String comment_user_type;
   private String comment_image;
 
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
 
   public JSONObject getTarget_user() {
     return this.target_user;
   }
 
   public void setTarget_user(JSONObject target_user) {
     this.target_user = target_user;
   }
 
   public Long getTarget_comment_id() {
     return this.target_comment_id;
   }
 
   public void setTarget_comment_id(Long target_comment_id) {
     this.target_comment_id = target_comment_id;
   }

public String getComment_user_type() {
	return comment_user_type;
}

public void setComment_user_type(String comment_user_type) {
	this.comment_user_type = comment_user_type;
}

public String getComment_image() {
	return comment_image;
}

public void setComment_image(String comment_image) {
	this.comment_image = comment_image;
}
   
   
   
 }

