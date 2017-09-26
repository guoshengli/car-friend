package com.friend.rest.service.model;

import java.io.Serializable;

public class GetuiModel implements Serializable {

	private static final long serialVersionUID = -6024217646415679198L;
	
	private String appId;
	
	private String appKey;
	
	private String masterSecret;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getMasterSecret() {
		return masterSecret;
	}

	public void setMasterSecret(String masterSecret) {
		this.masterSecret = masterSecret;
	}
	
	

}
