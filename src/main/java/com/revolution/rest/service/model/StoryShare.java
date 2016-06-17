package com.revolution.rest.service.model;

import java.io.Serializable;

import net.sf.json.JSONObject;

public class StoryShare implements Serializable {
	private static final long serialVersionUID = -309006025587972687L;
	private Long id;
	   private String title;
	   private String subtitle;
	   private Long collectionId;
	   private JSONObject cover_media;
	   private String summary;
	   private int image_count;
	   private String url;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public Long getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(Long collectionId) {
		this.collectionId = collectionId;
	}
	public JSONObject getCover_media() {
		return cover_media;
	}
	public void setCover_media(JSONObject cover_media) {
		this.cover_media = cover_media;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public int getImage_count() {
		return image_count;
	}
	public void setImage_count(int image_count) {
		this.image_count = image_count;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	   
	   
}
