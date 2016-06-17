package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.FeatureUser;

public interface FeatureUserDao extends BaseDao<FeatureUser, Long>
{
  public FeatureUser getFeatureUserByUserid(Long paramLong);

  public void deleteFeatureUser(Long paramLong);

  public List<FeatureUser> getRandomFeatureUser();
  
  public List<FeatureUser> getRandomFeatureUser(int count);
}

