 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class StoryHomeCopy
   implements Serializable
 {
   private static final long serialVersionUID = -5048828880584684658L;
   private Long id;
   private String title;
   private Long created_time;
   private JSONObject cover_media;
   private JSONObject author;
   private int image_count;
   private String summary;
   private String recommend_date;
   private String url;
   //09-25 ÐÂ¼Ó
   private int comment_count;
   private int repost_count;
   private boolean repost_by_current_user;
   private int like_count;
   
   private JSONObject columns;
 
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
 
   public JSONObject getCover_media() {
     return this.cover_media;
   }
 
   public void setCover_media(JSONObject cover_media) {
     this.cover_media = cover_media;
   }
 
   public JSONObject getAuthor() {
     return this.author;
   }
 
   public void setAuthor(JSONObject author) {
     this.author = author;
   }
 
   public Long getCreated_time() {
     return this.created_time;
   }
 
   public void setCreated_time(Long created_time) {
     this.created_time = created_time;
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
 
 
   public String getRecommend_date() {
     return this.recommend_date;
   }
 
   public void setRecommend_date(String recommend_date) {
     this.recommend_date = recommend_date;
   }

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

/*	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}*/

	public int getRepost_count() {
		return repost_count;
	}

	public void setRepost_count(int repost_count) {
		this.repost_count = repost_count;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public boolean isRepost_by_current_user() {
		return repost_by_current_user;
	}

	public void setRepost_by_current_user(boolean repost_by_current_user) {
		this.repost_by_current_user = repost_by_current_user;
	}

	public int getLike_count() {
		return like_count;
	}

	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}

	public JSONObject getColumns() {
		return columns;
	}

	public void setColumns(JSONObject columns) {
		this.columns = columns;
	}
   
	
	
   
 }
