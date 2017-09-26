package com.friend.rest.service.model;

import java.io.Serializable;

public class LinkModel extends CoverMedia implements Serializable {

	
	private static final long serialVersionUID = -4126491388336945143L;
	
	private String type;
	
	private LinkMedia media;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LinkMedia getMedia() {
		return media;
	}

	public void setMedia(LinkMedia media) {
		this.media = media;
	}
	
	
}
