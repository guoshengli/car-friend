package com.revolution.rest.service.model;

import java.io.Serializable;


public class LinkAccountWebModel implements Serializable {

	private static final long serialVersionUID = -4092206212898238023L;

	private String auth_token;
	 
	   private String description;
	 
	   private String refreshed_at;
	 
	   private String service;
	 
	   private String uuid;
	 
	   private Long user_id;
	 
	   private String avatar_url;
	   
	   private String rsa;
	 
	   public String getRsa() {
		return rsa;
	}

	public void setRsa(String rsa) {
		this.rsa = rsa;
	}

	public String getAuth_token()
	   {
	     return this.auth_token;
	   }
	 
	   public void setAuth_token(String auth_token) {
	     this.auth_token = auth_token;
	   }
	 
	   public String getDescription() {
	     return this.description;
	   }
	 
	   public void setDescription(String description) {
	     this.description = description;
	   }
	 
	   public String getRefreshed_at() {
	     return this.refreshed_at;
	   }
	 
	   public void setRefreshed_at(String refreshed_at) {
	     this.refreshed_at = refreshed_at;
	   }
	 
	   public String getService() {
	     return this.service;
	   }
	 
	   public void setService(String service) {
	     this.service = service;
	   }
	 
	   public String getUuid() {
	     return this.uuid;
	   }
	 
	   public void setUuid(String uuid) {
	     this.uuid = uuid;
	   }
	 
	   public Long getUser_id() {
	     return this.user_id;
	   }
	 
	   public void setUser_id(Long user_id) {
	     this.user_id = user_id;
	   }
	 
	   public String getAvatar_url() {
	     return this.avatar_url;
	   }
	 
	   public void setAvatar_url(String avatar_url) {
	     this.avatar_url = avatar_url;
	   }
	 
}
