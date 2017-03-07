package com.revolution.rest.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="user_centre")
public class UserCentre extends BaseEntity<Long> implements Serializable {
	private static final long serialVersionUID = 7540247066436544983L;

	private Long user_id;
	
	private int centre_id;

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public int getCentre_id() {
		return centre_id;
	}

	public void setCentre_id(int centre_id) {
		this.centre_id = centre_id;
	}
	
	
}
