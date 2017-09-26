package com.friend.rest.dao;

import com.friend.rest.model.Configuration;

public interface ConfigurationDao extends BaseDao<Configuration, Long> {
	public void update(Configuration paramConfiguration);

	public Configuration getConfByUserId(Long paramLong);
}
