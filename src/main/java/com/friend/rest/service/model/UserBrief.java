 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class UserBrief
   implements Serializable
 {
   private static final long serialVersionUID = 28148135974257163L;
   private Long id;
   private String username;
   private JSONObject avatar_image;
   private String introduction;
 
   public Long getId()
   {
     return this.id;
   }
 
   public void setId(Long id) {
     this.id = id;
   }
 
   public String getUsername() {
     return this.username;
   }
 
   public void setUsername(String username) {
     this.username = username;
   }
 
   public JSONObject getAvatar_image() {
     return this.avatar_image;
   }
 
   public void setAvatar_image(JSONObject avatar_image) {
     this.avatar_image = avatar_image;
   }
 
   public String getIntroduction() {
     return this.introduction;
   }
 
   public void setIntroduction(String introduction) {
     this.introduction = introduction;
   }
 }

