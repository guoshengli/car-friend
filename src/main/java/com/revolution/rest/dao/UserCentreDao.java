package com.revolution.rest.dao;

import com.revolution.rest.model.UserCentre;

public interface UserCentreDao extends BaseDao<UserCentre, Long> {
	public UserCentre getUserCentreByCentreId(int centre_id);
	
	public UserCentre getUserCentreByUserId(Long user_id);
}
