 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import net.sf.json.JSONObject;
 
 public class UserFeatured
   implements Serializable
 {
   private static final long serialVersionUID = 612556230701489425L;
   private Long id;
   private String introduction;
   private String username;
   private JSONObject avatar_image;
   private List<StoryIntro> stories = new ArrayList<StoryIntro>();
 
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
 
   public List<StoryIntro> getStories() {
     return this.stories;
   }
 
   public void setStories(List<StoryIntro> stories) {
     this.stories = stories;
   }
 }

