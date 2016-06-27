package com.revolution.rest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
@Entity
@Table(name="interest")
public class Interest extends BaseEntity<Long> implements Serializable {
	private static final long serialVersionUID = -3576102842152120295L;
	@Column(name="interest_name")
	private String interest_name;
	
	@Column(name="sequence")
	private int sequence;
	
	@OneToMany(mappedBy="interest", cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
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
	
	
}
