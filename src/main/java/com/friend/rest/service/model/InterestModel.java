package com.friend.rest.service.model;

import java.io.Serializable;

import net.sf.json.JSONObject;

public class InterestModel implements Serializable {

	private static final long serialVersionUID = 8276783941063067345L;
	
	private Long id;
	
	private String interest_name;
	
	private String description;
	
	private JSONObject cover_image;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInterest_name() {
		return interest_name;
	}

	public void setInterest_name(String interest_name) {
		this.interest_name = interest_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JSONObject getCover_image() {
		return cover_image;
	}

	public void setCover_image(JSONObject cover_image) {
		this.cover_image = cover_image;
	}
	
	

}
