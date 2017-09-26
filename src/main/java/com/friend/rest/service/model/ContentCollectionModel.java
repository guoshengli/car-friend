 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class ContentCollectionModel
   implements Serializable
 {
   private static final long serialVersionUID = -2455456363855725689L;
   private JSONObject sender;
   private JSONObject collection;
   private JSONObject story;
 
  
 
   public JSONObject getSender() {
	return sender;
}

public void setSender(JSONObject sender) {
	this.sender = sender;
}

   public JSONObject getCollection() {
	return collection;
}

public void setCollection(JSONObject collection) {
	this.collection = collection;
}

public JSONObject getStory() {
     return this.story;
   }
 
   public void setStory(JSONObject story) {
     this.story = story;
   }
 }

