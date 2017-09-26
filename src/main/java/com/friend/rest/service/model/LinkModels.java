package com.friend.rest.service.model;

import java.io.Serializable;

public class LinkModels extends CoverMedia implements Serializable {

	private static final long serialVersionUID = -8817146681160442840L;
	
	private String type;
	
	private LinkMedias media;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LinkMedias getMedia() {
		return media;
	}

	public void setMedia(LinkMedias media) {
		this.media = media;
	}
	
	

}
