package com.revolution.rest.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Likes;

@Repository("likesDao")
@SuppressWarnings("unchecked")
public class LikesDaoImpl extends BaseDaoImpl<Likes, Long>implements LikesDao {
	public LikesDaoImpl() {
		super(Likes.class);
	}

	public void saveOrUpdate(Likes likes) {
		Session session = getSessionFactory().getCurrentSession();
		session.save(likes);
	}

	public void delete(Likes likes) {
		Session session = getSessionFactory().getCurrentSession();
		session.delete(likes);
	}

	public List<Likes> getLikesByUserId(Long userId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Likes where like_users.id=" + userId;
		List<Likes> likesList = session.createQuery(hql).list();
		return likesList;
	}

	public void deleteLike(Long userId, Long storyId) {
		String hql = "delete from Likes where like_users.id = ? and like_story.id = ?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, userId.longValue());
		query.setLong(1, storyId.longValue());
		query.executeUpdate();
	}

	public int count(Long userId, Long storyId) {
		String hql = "select count(*) from Likes where like_story.id=?";
		Session session = getSessionFactory().getCurrentSession();

		List<Likes> list = session.createQuery(hql).setLong(0, storyId.longValue()).list();
		Iterator<Likes> iter = list.iterator();
		int count = 0;
		if (iter.hasNext()) {
			Object o = iter.next();
			count = ((Integer) o).intValue();
		}
		return count;
	}

	public int userLikesCount(Long userId) {
		String hql = "select count(id) from Likes where like_users.id = ?";
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

	public int likeStoryCount(Long storyId) {
		String hql = "select count(*) from Likes where like_story.id = ?";
		Session session = getSessionFactory().getCurrentSession();
		List<Long> list = session.createQuery(hql).setLong(0, storyId.longValue()).list();
		int count = 0;
		if ((list != null) && (list.size() > 0)) {
			Long c = (Long) list.get(0);
			count = c.intValue();
		}

		return count;
	}

	public Likes getLikeByUserIdAndStoryId(Long userId, Long storyId) {
		String hql = "from Likes where like_users.id = ? and like_story.id = ?";
		Session session = getSessionFactory().getCurrentSession();
		List<Likes> list = session.createQuery(hql).setLong(0, userId.longValue()).setLong(1, storyId.longValue())
				.list();
		Likes likes = null;
		if ((list != null) && (list.size() > 0)) {
			likes = (Likes) list.get(0);
		}
		return likes;
	}

	public void disableUser(Long id) {
	}

	public List<Likes> getLikesPageByUserId(Long userId, int count) {
		String hql = "from Likes where like_users.id=? order by createTime desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, userId.longValue());
		query.setMaxResults(count);
		List<Likes> list = query.list();
		return list;
	}

	public List<Likes> getLikesPageByUserId(Long userId, int count, Long storyId, int identifier) {
		String hql = "";
		List<Likes> likesList = new ArrayList<Likes>();
		Likes likes = getLikeByUserIdAndStoryId(userId, storyId);
		if (likes != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date(likes.getCreateTime().longValue() * 1000L));

			Session session = getSessionFactory().getCurrentSession();
			if (identifier == 1) {
				hql = "from Likes where like_users.id=? and createTime >= ? and id != ? order by createTime";
				Query query = session.createQuery(hql);
				query.setLong(0, userId.longValue());
				query.setString(1, createTime);
				query.setLong(2, ((Long) likes.getId()).longValue());
				query.setMaxResults(count);
				likesList = query.list();
				Collections.reverse(likesList);
			} else if (identifier == 2) {
				hql = "from Likes where like_users.id=? and createTime <= ? and id != ? order by createTime desc";
				Query query = session.createQuery(hql);
				query.setLong(0, userId.longValue());
				query.setString(1, createTime);
				query.setLong(2, ((Long) likes.getId()).longValue());
				query.setMaxResults(count);
				likesList = query.list();
			}
		}

		return likesList;
	}
}
