package com.revolution.rest.service.model;

import java.io.Serializable;

public class ColumnsModel implements Serializable {

	private static final long serialVersionUID = 3746851102992912699L;
	
	private Long id;
	
	private String column_name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}
	
	

}
