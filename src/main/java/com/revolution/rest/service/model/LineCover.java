package com.revolution.rest.service.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LineCover extends CoverMedia implements Serializable {
	private static final long serialVersionUID = -7569069869072413226L;
	private String type;
	private LineMedia media;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LineMedia getMedia() {
		return media;
	}

	public void setMedia(LineMedia media) {
		this.media = media;
	}

}
