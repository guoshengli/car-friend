package com.revolution.rest.service.model;

import java.io.Serializable;

public class IframeMedia implements Serializable {

	private static final long serialVersionUID = 1L;
	private String url;
	private String iframe_code;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIframe_code() {
		return iframe_code;
	}
	public void setIframe_code(String iframe_code) {
		this.iframe_code = iframe_code;
	}
	
	
	
	

}
