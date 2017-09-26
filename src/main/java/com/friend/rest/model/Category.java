package com.friend.rest.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;
@Entity
@Table(name="category")
public class Category extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = 3459499322528289283L;
	
	@Column(name="name")
	private String name;
	
	@Column(name="sequence")
	private int sequence;
	
	@Column(name="status")
	private String status;
	
	@Column(name="is_online",columnDefinition="BIT")
	private boolean is_online;
	
	@XmlTransient
	@ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
	private Set<Collection> collections;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isIs_online() {
		return is_online;
	}

	public void setIs_online(boolean is_online) {
		this.is_online = is_online;
	}

	public Set<Collection> getCollections() {
		return collections;
	}

	public void setCollections(Set<Collection> collections) {
		this.collections = collections;
	}
	
	

}
