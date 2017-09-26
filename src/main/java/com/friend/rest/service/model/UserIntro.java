 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 import javax.xml.bind.annotation.XmlRootElement;
 import net.sf.json.JSONObject;
 
 @XmlRootElement
 public class UserIntro
   implements Serializable
 {
   private static final long serialVersionUID = 625368135638555363L;
   private Long id;
   private String introduction;
   private String username;
   private JSONObject avatar_image;
   private boolean followed_by_current_user;
   private boolean is_following_current_user;
   private String user_type;
 
   public Long getId()
   {
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
 
   public boolean isFollowed_by_current_user() {
     return this.followed_by_current_user;
   }
 
   public void setFollowed_by_current_user(boolean followed_by_current_user) {
     this.followed_by_current_user = followed_by_current_user;
   }
 
   public boolean isIs_following_current_user() {
     return this.is_following_current_user;
   }
 
   public void setIs_following_current_user(boolean is_following_current_user) {
     this.is_following_current_user = is_following_current_user;
   }
 
   public String getUser_type() {
     return this.user_type;
   }
 
   public void setUser_type(String user_type) {
     this.user_type = user_type;
   }
 }

