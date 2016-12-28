package com.revolution.rest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
@Entity
@Table(name="interest")
public class Interest extends BaseEntity<Long> implements Serializable {
	private static final long serialVersionUID = -3576102842152120295L;
	@Column(name="interest_name")
	private String interest_name;
	
	@Column(name="sequence")
	private int sequence;
	
	@Column(name = "cover_image", columnDefinition = "TEXT")
	private String cover_image;

	@Column(name = "description")
	private String description;
	
	@OneToMany(mappedBy="interest", cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
	@OrderBy("number") 
	private List<Collection> collections = new ArrayList<Collection>();

	public String getInterest_name() {
		return interest_name;
	}

	public void setInterest_name(String interest_name) {
		this.interest_name = interest_name;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public List<Collection> getCollections() {
		return collections;
	}

	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}

	public String getCover_image() {
		return cover_image;
	}

	public void setCover_image(String cover_image) {
		this.cover_image = cover_image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
