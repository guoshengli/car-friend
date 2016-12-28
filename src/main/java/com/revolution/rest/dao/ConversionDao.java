package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Conversion;

public interface ConversionDao extends BaseDao<Conversion, Long> {
	public List getConversion(int count);
	
	public List getConversionById(int count,Long id);
}
