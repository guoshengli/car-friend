package com.friend.rest.dao;



import java.util.List;

import com.friend.rest.model.Interest;
public interface InterestDao extends BaseDao<Interest, Long> {
	
	public List<Interest> getInterestListBySequence();
}
