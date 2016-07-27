package com.revolution.rest.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "columns")
public class Columns extends BaseEntity<Long>implements Serializable {

	private static final long serialVersionUID = 3171210697245419841L;

	@Column(name = "column_name")
	private String column_name;

	@Column(name = "sequence")
	private int sequence;

	@Column(name = "cover_image", columnDefinition = "TEXT")
	private String cover_image;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@XmlTransient
	@ManyToMany(mappedBy = "columns", fetch = FetchType.LAZY)
	private Set<Story> stories;

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}

	public Set<Story> getStories() {
		return stories;
	}

	public void setStories(Set<Story> stories) {
		this.stories = stories;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
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
