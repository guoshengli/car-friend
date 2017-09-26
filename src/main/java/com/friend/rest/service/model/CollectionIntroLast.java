 package com.friend.rest.service.model;
 
 import java.io.Serializable;

import net.sf.json.JSONObject;
 
 public class CollectionIntroLast
   implements Serializable
 {

	   private static final long serialVersionUID = -8605259752032107633L;
	   private int id;
	   private String collection_name;
	   private JSONObject cover_image;
	   private boolean is_followed_by_current_user;
	   
	   private int story_count;
	   
	   private int followers_count;
	   
	   
	   private String collection_type;
	   
	   
	   
	   
	   public int getId() {
		return id;
	}
	public void setId(int id) {
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
		public boolean isIs_followed_by_current_user() {
			return is_followed_by_current_user;
		}
		public void setIs_followed_by_current_user(boolean is_followed_by_current_user) {
			this.is_followed_by_current_user = is_followed_by_current_user;
		}
		public int getStory_count() {
			return story_count;
		}
		public void setStory_count(int story_count) {
			this.story_count = story_count;
		}
		public int getFollowers_count() {
			return followers_count;
		}
		public void setFollowers_count(int followers_count) {
			this.followers_count = followers_count;
		}
		public String getCollection_type() {
			return collection_type;
		}
		public void setCollection_type(String collection_type) {
			this.collection_type = collection_type;
		}
	 
 }

