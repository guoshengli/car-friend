 package com.revolution.rest.service.model;
 
 import java.io.Serializable;

import net.sf.json.JSONObject;
 public class CollectionNotification
   implements Serializable
 {
   private static final long serialVersionUID = -8605259752032107633L;
   private Long id;
   private String collection_name;
   private JSONObject cover_image;
   private String info;
   private int story_count;
   
   private JSONObject author;
   
   private String collection_type;
   
   
   private boolean is_followed_by_current_user;

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getCollection_name() {
	return collection_name;
}

public void setCollection_name(String collection_name) {
	this.collection_name = collection_name;
}

public JSONObject getCover_image() {
	return cover_image;
}

public void setCover_image(JSONObject cover_image) {
	this.cover_image = cover_image;
}

public String getInfo() {
	return info;
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


public boolean isIs_followed_by_current_user() {
	return is_followed_by_current_user;
}

public void setIs_followed_by_current_user(boolean is_followed_by_current_user) {
	this.is_followed_by_current_user = is_followed_by_current_user;
}
   
   

 
  
   
   
 }

