package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Columns;

public interface ColumnsDao extends BaseDao<Columns, Long> {
	public List<Columns> getAllColumns();
}
