package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Likes;

public interface LikesDao extends BaseDao<Likes, Long> {
	public void delete(Likes paramLikes);

	public List<Likes> getLikesByUserId(Long paramLong);

	public void deleteLike(Long paramLong1, Long paramLong2);

	public int count(Long paramLong1, Long paramLong2);

	public int userLikesCount(Long paramLong);

	public int likeStoryCount(Long paramLong);

	public Likes getLikeByUserIdAndStoryId(Long paramLong1, Long paramLong2);

	public void disableUser(Long paramLong);

	public List<Likes> getLikesPageByUserId(Long paramLong, int paramInt);

	public List<Likes> getLikesPageByUserId(Long paramLong1, int paramInt1, Long paramLong2, int paramInt2);
}
