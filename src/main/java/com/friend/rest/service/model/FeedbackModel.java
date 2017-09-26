package com.friend.rest.service.model;

import java.io.Serializable;

public class FeedbackModel implements Serializable {

	private static final long serialVersionUID = -1737727116157224544L;
	
	private Long id;
	
	private Long user_id;
	
	private String user_name;
	
	private String info;
	
	private Long create_time;
	
	private ImageMedia cover_image;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public ImageMedia getCover_image() {
		return cover_image;
	}

	public void setCover_image(ImageMedia cover_image) {
		this.cover_image = cover_image;
	}

	
	

}
