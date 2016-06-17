 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class StoryIntro
   implements Serializable
 {
   private static final long serialVersionUID = 7824726363855725357L;
   private Long id;
   private String title;
   private String subtitle;
   private Long collectionId;
   private JSONObject cover_media;
   private String summary;
   private int image_count;
 
   public Long getId()
   {
     return this.id;
   }
 
   public void setId(Long id) {
     this.id = id;
   }
 
   public String getTitle() {
     return this.title;
   }
 
   public void setTitle(String title) {
     this.title = title;
   }
 
   public String getSubtitle() {
     return this.subtitle;
   }
 
   public void setSubtitle(String subtitle) {
     this.subtitle = subtitle;
   }
 
   public Long getCollectionId() {
     return this.collectionId;
   }
 
   public void setCollectionId(Long collectionId) {
     this.collectionId = collectionId;
   }
 
   public JSONObject getCover_media() {
     return this.cover_media;
   }
 
   public void setCover_media(JSONObject cover_media) {
     this.cover_media = cover_media;
   }
 
   public int getImage_count() {
     return this.image_count;
   }
 
   public void setImage_count(int image_count) {
     this.image_count = image_count;
   }
 
   public String getSummary() {
     return this.summary;
   }
 
   public void setSummary(String summary) {
     this.summary = summary;
   }
 }

