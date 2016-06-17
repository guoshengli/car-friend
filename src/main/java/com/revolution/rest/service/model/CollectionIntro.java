 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class CollectionIntro
   implements Serializable
 {
   private static final long serialVersionUID = -8605259752032107633L;
   private Long id;
   private String collection_name;
   private JSONObject cover_image;
   private String info;
   private JSONObject avatar_image;
   private int story_count;
   
   private JSONObject author;
   
   private String collection_type;
   
   private String activity_description;
   
   private int followers_count;
 
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
   public String getInfo() {
     return this.info;
   }
   public void setInfo(String info) {
     this.info = info;
   }
public int getStory_count() {
	return story_count;
}
public void setStory_count(int story_count) {
	this.story_count = story_count;
}
public JSONObject getAvatar_image() {
	return avatar_image;
}
public void setAvatar_image(JSONObject avatar_image) {
	this.avatar_image = avatar_image;
}
public JSONObject getAuthor() {
	return author;
}
public void setAuthor(JSONObject author) {
	this.author = author;
}
public String getCollection_type() {
	return collection_type;
}
public void setCollection_type(String collection_type) {
	this.collection_type = collection_type;
}
public String getActivity_description() {
	return activity_description;
}
public void setActivity_description(String activity_description) {
	this.activity_description = activity_description;
}
public int getFollowers_count() {
	return followers_count;
}
public void setFollowers_count(int followers_count) {
	this.followers_count = followers_count;
}
   
	
   
   
 }

