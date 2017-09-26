package com.friend.rest.service.model;

import java.io.Serializable;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class StoryPagesModel implements Serializable {

	private static final long serialVersionUID = 4406854001533740964L;
	
	 private Long id;
	   private String title;
	   private Object subtitle;
	   private JSONObject cover_media;
	   private JSONArray elements;
	   private Long update_time;
	   private Long created_time;
	   private int comment_count;
	   private boolean commnents_enables;
	   private int like_count;
	   private boolean liked_by_current_user;
	   private int repost_count;
	   private boolean repost_by_current_user;
	   private int view_count;
	   private Object url;
	   private JSONObject author;
	   private List<CommentSummaryModel> comments;
	   private List<StoryShare> recommendation;
	   private CollectionIntro collection;
	   private int image_count;
	   private String summary;
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
	public Object getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(Object subtitle) {
		this.subtitle = subtitle;
	}
	public JSONObject getCover_media() {
		return cover_media;
	}
	public void setCover_media(JSONObject cover_media) {
		this.cover_media = cover_media;
	}
	public JSONArray getElements() {
		return elements;
	}
	public void setElements(JSONArray elements) {
		this.elements = elements;
	}
	public Long getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Long update_time) {
		this.update_time = update_time;
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
	public boolean isCommnents_enables() {
		return commnents_enables;
	}
	public void setCommnents_enables(boolean commnents_enables) {
		this.commnents_enables = commnents_enables;
	}
	public int getLike_count() {
		return like_count;
	}
	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}
	public boolean isLiked_by_current_user() {
		return liked_by_current_user;
	}
	public void setLiked_by_current_user(boolean liked_by_current_user) {
		this.liked_by_current_user = liked_by_current_user;
	}
	public int getRepost_count() {
		return repost_count;
	}
	public void setRepost_count(int repost_count) {
		this.repost_count = repost_count;
	}
	public boolean isRepost_by_current_user() {
		return repost_by_current_user;
	}
	public void setRepost_by_current_user(boolean repost_by_current_user) {
		this.repost_by_current_user = repost_by_current_user;
	}
	public int getView_count() {
		return view_count;
	}
	public void setView_count(int view_count) {
		this.view_count = view_count;
	}
	public Object getUrl() {
		return url;
	}
	public void setUrl(Object url) {
		this.url = url;
	}
	public JSONObject getAuthor() {
		return author;
	}
	public void setAuthor(JSONObject author) {
		this.author = author;
	}
	public List<CommentSummaryModel> getComments() {
		return comments;
	}
	public void setComments(List<CommentSummaryModel> comments) {
		this.comments = comments;
	}
	public List<StoryShare> getRecommendation() {
		return recommendation;
	}
	public void setRecommendation(List<StoryShare> recommendation) {
		this.recommendation = recommendation;
	}
	public CollectionIntro getCollection() {
		return collection;
	}
	public void setCollection(CollectionIntro collection) {
		this.collection = collection;
	}
	public int getImage_count() {
		return image_count;
	}
	public void setImage_count(int image_count) {
		this.image_count = image_count;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	   
	   

}
