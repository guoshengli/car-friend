package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Follow;

public interface FollowDao extends BaseDao<Follow, Long> {
	public int count(Long paramLong);

	public void deleteFollow(Long paramLong1, Long paramLong2);

	public int userFollowCount(Long paramLong);

	public int userFollowedCount(Long paramLong);

	public Follow getFollow(Long paramLong1, Long paramLong2);

	public List<Follow> getFollowingsPageByUserId(Long paramLong, int paramInt);

	public List<Follow> getFollowingsPageByUserId(Long paramLong1, int paramInt1, Long paramLong2, int paramInt2);

	public List<Follow> getFollowersPageByUserId(Long paramLong, int paramInt);

	public List<Follow> getFollowersPageByUserId(Long paramLong1, int paramInt1, Long paramLong2, int paramInt2);

	public List<Follow> getFollowingsByUserId(Long paramLong);

	public List<Follow> getFollowersByUserId(Long paramLong);

	public void disableUser(Long paramLong);
}
