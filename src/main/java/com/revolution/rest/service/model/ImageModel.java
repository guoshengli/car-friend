package com.revolution.rest.service.model;

import java.io.Serializable;

public class ImageModel implements Serializable {
	
	private static final long serialVersionUID = 8677822494332406332L;
	
	private String zoom;
	
	private String original_size;
	
	private String focalpoint;
	
	private String name;
	
	private String comment;

	public String getZoom() {
		return zoom;
	}

	public void setZoom(String zoom) {
		this.zoom = zoom;
	}

	public String getOriginal_size() {
		return original_size;
	}

	public void setOriginal_size(String original_size) {
		this.original_size = original_size;
	}

	public String getFocalpoint() {
		return focalpoint;
	}

	public void setFocalpoint(String focalpoint) {
		this.focalpoint = focalpoint;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	

}
