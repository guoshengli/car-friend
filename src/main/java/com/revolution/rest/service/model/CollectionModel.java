 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class CollectionModel
   implements Serializable
 {
   private static final long serialVersionUID = -4389192341257415723L;
   private Long id;
   private String collection_name;
   private JSONObject cover_image;
   private String info;
   private boolean is_story_in_collection;
 
   public Long getId()
   {
     return this.id;
   }
 
   public void setId(Long id) {
     this.id = id;
   }
 
   public String getCollection_name() {
     return this.collection_name;
   }
 
   public void setCollection_name(String collection_name) {
     this.collection_name = collection_name;
   }
 
   public JSONObject getCover_image() {
     return this.cover_image;
   }
 
   public void setCover_image(JSONObject cover_image) {
     this.cover_image = cover_image;
   }
 
   public boolean isIs_story_in_collection() {
     return this.is_story_in_collection;
   }
 
   public void setIs_story_in_collection(boolean is_story_in_collection) {
     this.is_story_in_collection = is_story_in_collection;
   }
 
   public String getInfo() {
     return this.info;
   }
 
   public void setInfo(String info) {
     this.info = info;
   }

 
 }

