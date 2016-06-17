 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 
 public class ReplyComment
   implements Serializable
 {
   private static final long serialVersionUID = 7325136963855746123L;
   private String content;
   private Long story_id;
   private Long target_user_id;
 
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
 
   public Long getTarget_user_id() {
     return this.target_user_id;
   }
 
   public void setTarget_user_id(Long target_user_id) {
     this.target_user_id = target_user_id;
   }
 }

