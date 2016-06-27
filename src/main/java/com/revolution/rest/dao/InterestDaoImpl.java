package com.revolution.rest.dao;


import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Interest;
@Repository("interestDao")
@SuppressWarnings("unchecked")
public class InterestDaoImpl extends BaseDaoImpl<Interest, Long>implements InterestDao {

	public InterestDaoImpl() {
		super(Interest.class);
	}

	@Override
	public List<Interest> getInterestListBySequence() {
		String hql = "from Interest order by sequence desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		List<Interest> list = query.list();
		return list;
	}

	
	
	
}
