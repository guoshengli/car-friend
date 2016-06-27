package com.revolution.rest.dao;



import java.util.List;

import com.revolution.rest.model.Interest;
public interface InterestDao extends BaseDao<Interest, Long> {
	
	public List<Interest> getInterestListBySequence();
}
