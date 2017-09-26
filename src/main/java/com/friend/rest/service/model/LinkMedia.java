package com.friend.rest.service.model;

import java.io.Serializable;


public class LinkMedia implements Serializable {

	
	private static final long serialVersionUID = -3902521914719884523L;
	
	private String url;
	
	private String name;
	
	private String description;
	
	private ImageModel image;

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

	public ImageModel getImage() {
		return image;
	}

	public void setImage(ImageModel image) {
		this.image = image;
	}

	
	
	
}
