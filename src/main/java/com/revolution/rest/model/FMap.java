package com.revolution.rest.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
@Entity
@Table(name="wiki")
public class FMap extends BaseEntity<Long> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="type")
	private String type;
	
	@Column(name="ssm_id")
	private int ssm_id;
	
	@Column(name="story_id")
	private Long wiki_id;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getSsm_id() {
		return ssm_id;
	}

	public void setSsm_id(int ssm_id) {
		this.ssm_id = ssm_id;
	}

	public Long getWiki_id() {
		return wiki_id;
	}

	public void setWiki_id(Long wiki_id) {
		this.wiki_id = wiki_id;
	}
	
	
}
