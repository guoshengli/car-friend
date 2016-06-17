package com.revolution.rest.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
@Entity
@Table(name="interest")
public class Interest extends BaseEntity<Long> implements Serializable {
	private static final long serialVersionUID = -3576102842152120295L;
	@Column(name="interest_name")
	private String interest_name;

	public String getInterest_name() {
		return interest_name;
	}

	public void setInterest_name(String interest_name) {
		this.interest_name = interest_name;
	}
	
	
}
