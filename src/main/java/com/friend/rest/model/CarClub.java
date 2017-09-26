package com.friend.rest.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
@Entity
@Table(name="car_club")
public class CarClub extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = -6515274361420270301L;
	
	@Column(name="car_id")
	private int car_id;
	
	@Column(name="club_id")
	private int club_id;

	public int getCar_id() {
		return car_id;
	}

	public void setCar_id(int car_id) {
		this.car_id = car_id;
	}

	public int getClub_id() {
		return club_id;
	}

	public void setClub_id(int club_id) {
		this.club_id = club_id;
	}
	
	
}
