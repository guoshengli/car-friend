package com.revolution.rest.dao;

import com.revolution.rest.model.Configuration;

public interface ConfigurationDao extends BaseDao<Configuration, Long> {
	public void update(Configuration paramConfiguration);

	public Configuration getConfByUserId(Long paramLong);
}
