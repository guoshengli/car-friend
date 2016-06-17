package com.revolution.rest.dao;

import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Cover_page;

@Repository("coverPageDao")
public class CoverPageDaoImpl extends BaseDaoImpl<Cover_page, Long>implements CoverPageDao {
	public CoverPageDaoImpl() {
		super(Cover_page.class);
	}
}
