package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.Republish;

public interface RepublishDao extends BaseDao<Republish, Long>
{
  public List<Republish> getRepublishesByUserId(Long paramLong);

  public int count(Long paramLong);

  public int userRepostCount(Long paramLong);

  public void deleteRepublish(Long paramLong1, Long paramLong2);
  
  public List<Republish> getRepublishesByUserId(Long userId, int count);

  public Republish getRepostByUserIdAndStoryId(Long paramLong1, Long paramLong2);

  public List<Republish> getRepublishesPageByUserId(Long paramLong, int paramInt);

  public List<Republish> getRepublishesPageByUserId(Long paramLong1, int paramInt1, Long paramLong2, int paramInt2);
}

