package com.friend.rest.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
@Entity
@Table(name="navigation")
public class Navigation extends BaseEntity<Long> implements Serializable {
	private static final long serialVersionUID = 2029628865891515863L;
	
	@Column(name="collection_id")
	private int collection_id;
	
	@Column(name="sequence")
	private int sequence;
	
	public int getCollection_id() {
		return collection_id;
	}
	public void setCollection_id(int collection_id) {
		this.collection_id = collection_id;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	
	
}
