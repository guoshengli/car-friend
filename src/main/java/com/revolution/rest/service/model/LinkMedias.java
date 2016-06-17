package com.revolution.rest.service.model;

import java.io.Serializable;


public class LinkMedias implements Serializable {

	private static final long serialVersionUID = 7985773715620757475L;

	private String url;
	
	private String name;
	
	private String description;
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
