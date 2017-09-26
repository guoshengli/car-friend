package com.friend.rest.service.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LineMedia implements Serializable {
	
	private static final long serialVersionUID = 2341177009717509826L;
	private String style;

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
