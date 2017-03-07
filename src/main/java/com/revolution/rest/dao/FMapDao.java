package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.FMap;

public interface FMapDao extends BaseDao<FMap, Long> {
	public List<FMap> getFMapList(int count,String type,int ssm_id);
	
	public List<FMap> getFMapList(int count,Long max_id,String type,int ssm_id);
	
	public List<FMap> getFMapList(Long story_id);
}
