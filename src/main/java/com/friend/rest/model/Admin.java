package com.friend.rest.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="admin")
public class Admin extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = 4034225309612575708L;
	
	@Column(name="fb_id")
	private int fb_id;
	
	@Column(name="username")
	private String username;
	
	@Column(name="type")
	private String type;
	
	@Column(name="status")
	private String status;
	
	public int getFb_id() {
		return fb_id;
	}
	public void setFb_id(int fb_id) {
		this.fb_id = fb_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	

}
