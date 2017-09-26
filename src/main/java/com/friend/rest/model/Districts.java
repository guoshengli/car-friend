package com.friend.rest.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
@Entity
@Table(name="districts")
public class Districts extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = 8576118500335978305L;

	@Column(name="parent_id")
	private Long parent_id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="hot")
	private int hot;
	
	@OneToMany(mappedBy="districts", cascade=CascadeType.ALL)
	private Set<Collection> collections;

	public Long getParent_id() {
		return parent_id;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHot() {
		return hot;
	}

	public void setHot(int hot) {
		this.hot = hot;
	}

	public Set<Collection> getCollections() {
		return collections;
	}

	public void setCollections(Set<Collection> collections) {
		this.collections = collections;
	}
	
	
	
}
