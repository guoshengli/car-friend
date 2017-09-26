package com.friend.rest.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

public class FeatureCollection extends BaseEntity<Long>implements Serializable {

	private static final long serialVersionUID = 4783658834160020548L;
	@Column(name = "collection_id")
	private Long collectionId;
	
	@Column(name = "sequence")
	private int sequnce;

	public Long getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Long collectionId) {
		this.collectionId = collectionId;
	}

	public int getSequnce() {
		return sequnce;
	}

	public void setSequnce(int sequnce) {
		this.sequnce = sequnce;
	}
	
	

}
