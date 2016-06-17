package com.revolution.rest.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
@Entity
@Table(name="columns")
public class Columns extends BaseEntity<Long>implements Serializable {

	private static final long serialVersionUID = 3171210697245419841L;
	
	@Column(name="column_name")
	private String column_name;

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}
	
	

}
