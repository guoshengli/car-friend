package com.revolution.rest.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Follow;

@Repository("followDao")
@SuppressWarnings("unchecked")
public class FollowDaoImpl extends BaseDaoImpl<Follow, Long>implements FollowDao {
	public FollowDaoImpl() {
		super(Follow.class);
	}

	public int count(Long userId) {
		String hql = "select count(*) from Follow f where f.pk.user.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, userId.longValue());

		List<Long> list = query.list();
		int count = 0;
		if ((list != null) && (list.size() > 0)) {
			Long c = (Long) list.get(0);
			count = c.intValue();
		}
		return count;
	}

	public void deleteFollow(Long userId, Long followerId) {
		String hql = "delete from Follow f where f.pk.user.id = ? and f.pk.follower.id = ?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, userId.longValue());
		query.setLong(1, followerId.longValue());
		query.executeUpdate();
	}

	public int userFollowCount(Long userId) {
		String hql = "select count(*) from Follow f where f.pk.user.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, userId.longValue());
		List<Long> list = query.list();
		int count = 0;
		if ((list != null) && (list.size() > 0)) {
			Long c = (Long) list.get(0);
			count = c.intValue();
		}

		return count;
	}

	public int userFollowedCount(Long followerId) {
		String hql = "select count(*) from Follow f where f.pk.follower.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, followerId.longValue());
		List<Long> list = query.list();
		int count = 0;
		if ((list != null) && (list.size() > 0)) {
			Long c = (Long) list.get(0);
			count = c.intValue();
		}

		return count;
	}

	public Follow getFollow(Long userId, Long followerId) {
		String hql = "from Follow f where f.pk.user.id=? and f.pk.follower.id=?";
		Session session = getSessionFactory().getCurrentSession();
		List<Follow> followList = session.createQuery(hql).setLong(0, userId.longValue())
				.setLong(1, followerId.longValue()).list();
		Follow follow = null;
		if ((followList != null) && (followList.size() > 0)) {
			follow = (Follow) followList.get(0);
		}
		return follow;
	}

	public List<Follow> getFollowingsByUserId(Long userId) {
		String hql = "from Follow f where f.pk.user.id = ?";
		Session session = getSessionFactory().getCurrentSession();
		List<Follow> followList = session.createQuery(hql).setLong(0, userId.longValue()).list();
		return followList;
	}

	public List<Follow> getFollowersByUserId(Long userId) {
		String hql = "from Follow f where f.pk.follower.id = ?";
		Session session = getSessionFactory().getCurrentSession();
		List<Follow> followList = session.createQuery(hql).setLong(0, userId.longValue()).list();
		return followList;
	}

	public void disableUser(Long id) {
	}

	public List<Follow> getFollowingsPageByUserId(Long userId, int count) {
		String hql = "from Follow f where f.pk.user.id = ? order by f.createTime desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, userId.longValue());
		query.setMaxResults(count);
		List<Follow> list = query.list();
		return list;
	}

	public List<Follow> getFollowingsPageByUserId(Long userId, int count, Long since_userid, int identifier) {
		String hql = "";
		Follow follow = getFollow(userId, since_userid);
		List<Follow> followList = new ArrayList<Follow>();
		if (follow != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date(follow.getCreateTime().longValue() * 1000l));

			Session session = getSessionFactory().getCurrentSession();
			if (identifier == 1) {
				hql = "from Follow f where f.pk.user.id=? and f.pk.follower.id != ?  and createTime >= ? order by createTime";
				Query query = session.createQuery(hql);
				query.setLong(0, userId.longValue());
				query.setLong(1, since_userid);
				query.setString(2, createTime);
				query.setMaxResults(count);
				followList = query.list();
				Collections.reverse(followList);
			} else if (identifier == 2) {
				hql = "from Follow f where f.pk.user.id=?  and f.pk.follower.id != ?  and createTime <= ? order by createTime desc";
				Query query = session.createQuery(hql);
				query.setLong(0, userId.longValue());
				query.setLong(1, since_userid);
				query.setString(2, createTime);
				query.setMaxResults(count);
				followList = query.list();
			}
		}

		return followList;
	}

	public List<Follow> getFollowersPageByUserId(Long userId, int count) {
		String hql = "from Follow f where f.pk.follower.id = ? order by f.createTime desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, userId.longValue());
		query.setMaxResults(count);
		List<Follow> list = query.list();
		return list;
	}

	public List<Follow> getFollowersPageByUserId(Long userId, int count, Long since_userid, int identifier) {
		String hql = "";
		Follow follow = getFollow(since_userid, userId);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createTime = sdf.format(new Date(follow.getCreateTime().longValue() * 1000L));
		List<Follow> followList = new ArrayList<Follow>();
		Session session = getSessionFactory().getCurrentSession();
		if (identifier == 1) {
			hql = "from Follow f where f.pk.follower.id = ? and createTime >= ? and f.pk.user.id != ? order by createTime";
			Query query = session.createQuery(hql);
			query.setLong(0, userId.longValue());
			query.setString(1, createTime);
			query.setLong(2, ((Long) follow.getPk().getUser().getId()).longValue());
			query.setMaxResults(count);
			List<Follow> list = query.list();
			if ((list != null) && (list.size() > 0)) {
				for (int i = list.size() - 1; i >= 0; i--)
					followList.add(list.get(i));
			}
		} else if (identifier == 2) {
			hql = "from Follow f where f.pk.follower.id = ? and createTime <= ? and f.pk.user.id != ? order by createTime desc";
			Query query = session.createQuery(hql);
			query.setLong(0, userId.longValue());
			query.setString(1, createTime);
			query.setLong(2, ((Long) follow.getPk().getUser().getId()).longValue());
			query.setMaxResults(count);
			followList = query.list();
		}
		return followList;
	}
}
