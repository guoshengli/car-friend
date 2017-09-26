package com.friend.rest.service.model;

import java.io.Serializable;

public class IframeCover extends CoverMedia implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String type;
	private IframeMedia media;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public IframeMedia getMedia() {
		return media;
	}
	public void setMedia(IframeMedia media) {
		this.media = media;
	}
	
	

}
