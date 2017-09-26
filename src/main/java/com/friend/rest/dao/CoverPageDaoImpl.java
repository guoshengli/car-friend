package com.friend.rest.dao;

import org.springframework.stereotype.Repository;

import com.friend.rest.model.Cover_page;

@Repository("coverPageDao")
public class CoverPageDaoImpl extends BaseDaoImpl<Cover_page, Long>implements CoverPageDao {
	public CoverPageDaoImpl() {
		super(Cover_page.class);
	}
}
