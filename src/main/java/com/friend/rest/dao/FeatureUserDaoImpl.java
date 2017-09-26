package com.friend.rest.dao;

import java.util.List;
import java.util.Random;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.FeatureUser;

@Repository("featureUserDao")
@SuppressWarnings("unchecked")
public class FeatureUserDaoImpl extends BaseDaoImpl<FeatureUser, Long>implements FeatureUserDao {
	public FeatureUserDaoImpl() {
		super(FeatureUser.class);
	}

	public FeatureUser getFeatureUserByUserid(Long userId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from FeatureUser where userId=?";
		Query query = session.createQuery(hql).setLong(0, userId.longValue());
		List<FeatureUser> list = query.list();
		FeatureUser fu = null;
		if ((list != null) && (list.size() > 0)) {
			fu = (FeatureUser) list.get(0);
		}
		return fu;
	}

	public void deleteFeatureUser(Long userId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from FeatureUser where 1=1 and userId=?";
		Query query = session.createQuery(hql).setLong(0, userId.longValue());
		query.executeUpdate();
	}

	public List<FeatureUser> getRandomFeatureUser() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from FeatureUser fu where (select count(*) from Story s where s.user.id=fu.userId) >=3 ";
		Random r = new Random();
		Query query = session.createQuery(hql);
		int size = query.list().size();
		query.setMaxResults(10);
		if (size > 10) {
			query.setFirstResult(r.nextInt(Math.abs(size - 10)) + 1);
		}

		List<FeatureUser> list = query.list();
		return list;
	}

	@Override
	public List<FeatureUser> getRandomFeatureUser(int count) {
		String hql = "from FeatureUser ORDER BY RAND()";
		Session session = getSessionFactory().getCurrentSession();
	    Query query = session.createQuery(hql).setMaxResults(count);
	    List<FeatureUser> list = query.list();
		return list;
	}
}
