package com.revolution.rest.service.model;

import java.io.Serializable;
import net.sf.json.JSONObject;

public class StoryIntros implements Serializable {
	private static final long serialVersionUID = 7824726363855725357L;
	private Long id;
	private String title;
	private Long collectionId;
	private JSONObject cover_media;
	private String summary;
	private int image_count;
	private JSONObject author;
	private Long created_time;
	private int comment_count;
	private int like_count;
	private String url;

	public Long getId() {
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

	public JSONObject getAuthor() {
		return author;
	}

	public void setAuthor(JSONObject author) {
		this.author = author;
	}

	public Long getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Long created_time) {
		this.created_time = created_time;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public int getLike_count() {
		return like_count;
	}

	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	

	
}
