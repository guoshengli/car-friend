 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class UserFeatur
   implements Serializable
 {
   private static final long serialVersionUID = 612556230701489425L;
   private Long id;
   private String introduction;
   private String username;
   private JSONObject avatar_image;
   private JSONObject cover_image;
   
   private boolean followed_by_current_user;
 
   public Long getId() {
     return this.id;
   }
 
   public void setId(Long id) {
     this.id = id;
   }
 
   public String getIntroduction() {
     return this.introduction;
   }
 
   public void setIntroduction(String introduction) {
     this.introduction = introduction;
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

public JSONObject getCover_image() {
	return cover_image;
}

public void setCover_image(JSONObject cover_image) {
	this.cover_image = cover_image;
}

public boolean isFollowed_by_current_user() {
	return followed_by_current_user;
}

public void setFollowed_by_current_user(boolean followed_by_current_user) {
	this.followed_by_current_user = followed_by_current_user;
}


 
  
 }

