package com.revolution.rest.service.model;

import java.io.Serializable;

import net.sf.json.JSONObject;

public class UserPhone implements Serializable {

	private static final long serialVersionUID = -595989681072430650L;

	private Long id;
	private String introduction;
	private String username;
	private JSONObject avatar_image;
	private boolean followed_by_current_user;
	private String phone;
	private String zone;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public JSONObject getAvatar_image() {
		return avatar_image;
	}
	public void setAvatar_image(JSONObject avatar_image) {
		this.avatar_image = avatar_image;
	}
	public boolean isFollowed_by_current_user() {
		return followed_by_current_user;
	}
	public void setFollowed_by_current_user(boolean followed_by_current_user) {
		this.followed_by_current_user = followed_by_current_user;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	
	

}
