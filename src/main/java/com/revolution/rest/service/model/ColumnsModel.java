package com.revolution.rest.service.model;

import java.io.Serializable;

import net.sf.json.JSONObject;

public class ColumnsModel implements Serializable {

	private static final long serialVersionUID = 3746851102992912699L;
	
	private Long id;
	
	private String column_name;
	
	private String description;
	
	private JSONObject cover_media;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JSONObject getCover_media() {
		return cover_media;
	}

	public void setCover_media(JSONObject cover_media) {
		this.cover_media = cover_media;
	}
	
	

}
