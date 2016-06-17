 package com.revolution.rest.service.model;
 
 import java.io.Serializable;

import net.sf.json.JSONObject;
 
 public class CollectionIntroLast
   implements Serializable
 {

	   private static final long serialVersionUID = -8605259752032107633L;
	   private Long id;
	   private String collection_name;
	   private JSONObject cover_image;
	   private String info;
	   private JSONObject avatar_image;
	   private boolean is_followed_by_current_user;
	   
	   private int story_count;
	   
	   private int followers_count;
	   
	   private JSONObject author;
	   
	   private boolean is_review;
	   
	   private String collection_type;
	   
	   private String activity_description;
	   
	 
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
		public boolean isIs_followed_by_current_user() {
			return is_followed_by_current_user;
		}
		public void setIs_followed_by_current_user(boolean is_followed_by_current_user) {
			this.is_followed_by_current_user = is_followed_by_current_user;
		}
		public JSONObject getAvatar_image() {
			return avatar_image;
		}
		public void setAvatar_image(JSONObject avatar_image) {
			this.avatar_image = avatar_image;
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
		public JSONObject getAuthor() {
			return author;
		}
		public void setAuthor(JSONObject author) {
			this.author = author;
		}
		public boolean isIs_review() {
			return is_review;
		}
		public void setIs_review(boolean is_review) {
			this.is_review = is_review;
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
	   
	 
 }

