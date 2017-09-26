package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.Columns;

public interface ColumnsDao extends BaseDao<Columns, Long> {
	public List<Columns> getAllColumns();
}
