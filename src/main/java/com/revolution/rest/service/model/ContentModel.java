 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class ContentModel
   implements Serializable
 {
   private static final long serialVersionUID = -2455456363855725689L;
   private JSONObject sender;
   private JSONObject comment;
   private JSONObject story;
   private JSONObject collection;
 
  
 
   public JSONObject getSender() {
	return sender;
}

public void setSender(JSONObject sender) {
	this.sender = sender;
}

public JSONObject getComment() {
     return this.comment;
   }
 
   public void setComment(JSONObject comment) {
     this.comment = comment;
   }
 
   public JSONObject getStory() {
     return this.story;
   }
 
   public void setStory(JSONObject story) {
     this.story = story;
   }

public JSONObject getCollection() {
	return collection;
}

public void setCollection(JSONObject collection) {
	this.collection = collection;
}
   
   
 }

