package com.friend.rest.service.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class LocationModel extends CoverMedia implements Serializable {

	private static final long serialVersionUID = -6723942255459201668L;
	
	private String type;
	
	private LocationMedia media;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocationMedia getMedia() {
		return media;
	}

	public void setMedia(LocationMedia media) {
		this.media = media;
	}


	
	

}
