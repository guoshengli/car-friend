package com.friend.rest.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.Republish;

@Repository("republishDao")
@SuppressWarnings("unchecked")
public class RepublishDaoImpl extends BaseDaoImpl<Republish, Long>implements RepublishDao {
	public RepublishDaoImpl() {
		super(Republish.class);
	}

	public void saveOrUpdate(Republish republish) {
		Session session = getSessionFactory().getCurrentSession();
		session.save(republish);
	}

	public List<Republish> getRepublishesByUserId(Long userId) {
		String hql = "from Republish where userId=" + userId;
		Session session = getSessionFactory().getCurrentSession();
		List<Republish> republishList = session.createQuery(hql).list();
		return republishList;
	}

	public int count(Long storyId) {
		String hql = "select count(*) from Republish where repost_story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		List<Long> list = session.createQuery(hql).setLong(0, storyId.longValue()).list();
		int count = 0;
		if ((list != null) && (list.size() > 0)) {
			Long c = (Long) list.get(0);
			count = c.intValue();
		}
		return count;
	}

	public void deleteRepublish(Long userId, Long storyId) {
		String hql = "delete from Republish where repost_users.id =? and repost_story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		session.createQuery(hql).setLong(0, userId.longValue()).setLong(1, storyId.longValue()).executeUpdate();
	}

	public int userRepostCount(Long userId) {
		String hql = "select count(id) from Republish where repost_users.id = ?";
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

	public Republish getRepostByUserIdAndStoryId(Long userId, Long storyId) {
		String hql = "from Republish where repost_users.id=? and repost_story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		List<Republish> list = session.createQuery(hql).setLong(0, userId.longValue()).setLong(1, storyId.longValue())
				.list();
		Republish republish = null;
		if ((list != null) && (list.size() > 0)) {
			republish = (Republish) list.get(0);
		}
		return republish;
	}

	public List<Republish> getRepublishesByUserId(Long userId, int count) {
		String hql = "from Republish where userId=? order by createTime desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, userId.longValue());
		query.setMaxResults(count);
		List<Republish> list = query.list();
		return list;
	}

	public List<Republish> getRepublishesPageByUserId(Long userId, int count) {
		String hql = "from Republish where userId=? order by createTime desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, userId.longValue());
		query.setMaxResults(count);
		List<Republish> list = query.list();
		return list;
	}

	public List<Republish> getRepublishesPageByUserId(Long userId, int count, Long storyId, int identifier) {
		String hql = "";
		Republish r = getRepostByUserIdAndStoryId(userId, storyId);
		List<Republish> republishList = new ArrayList<Republish>();
		if (r != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date(r.getCreateTime().longValue() * 1000L));
			Session session = getSessionFactory().getCurrentSession();

			if (identifier == 1) {
				hql = "from Republish where userId=? and createTime >= ? and id != ? order by createTime";
				Query query = session.createQuery(hql);
				query.setLong(0, userId.longValue());
				query.setString(1, createTime);
				query.setLong(2, ((Long) r.getId()).longValue());
				query.setMaxResults(count);
				republishList = query.list();
				Collections.reverse(republishList);
			} else if (identifier == 2) {
				hql = "from Republish where userId=? and createTime <= ? and id != ? order by createTime desc";
				Query query = session.createQuery(hql);
				query.setLong(0, userId.longValue());
				query.setString(1, createTime);
				query.setLong(2, ((Long) r.getId()).longValue());
				query.setMaxResults(count);
				republishList = query.list();
			}
		}

		return republishList;
	}
}
