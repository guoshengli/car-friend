package com.friend.rest.dao;

import java.util.List;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.Slide;

@Repository("slideDao")
@SuppressWarnings("unchecked")
public class SlideDaoImpl extends BaseDaoImpl<Slide, Long>implements SlideDao {
	public SlideDaoImpl() {
		super(Slide.class);
	}

	public List<Slide> getSlideList() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Slide where group='homepage' and status = 'enabled' order by create_time desc";
		List<Slide> list = session.createQuery(hql).setMaxResults(5).list();
		return list;
	}

	@Override
	public List<Slide> getSlideLists() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Slide where group='time_square' and status = 'enabled' order by sequence desc";
		List<Slide> list = session.createQuery(hql).setMaxResults(5).list();
		return list;
	}

	@Override
	public List<Slide> getSlideByGroups(String groups) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Slide where group=? and status = 'enabled' order by sequence desc";
		List<Slide> list = session.createQuery(hql).setString(0,groups)
				.setMaxResults(5).list();
		return list;
	}
}
