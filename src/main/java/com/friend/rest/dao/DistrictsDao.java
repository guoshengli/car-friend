package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.Districts;

public interface DistrictsDao extends BaseDao<Districts, Long> {
	
	public List<Districts> getDistrictsByParentId(Long parent_id);
	
	public List<Object[]> getDistrictsById(Long id);
	
	public List<Districts> getDistrictsByHot(int hot);
	
}
