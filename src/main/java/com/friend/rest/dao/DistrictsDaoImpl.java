package com.friend.rest.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.Districts;
@Repository("districtsDao")
@SuppressWarnings("unchecked")
public class DistrictsDaoImpl extends BaseDaoImpl<Districts, Long> implements DistrictsDao {

	public DistrictsDaoImpl() {
		super(Districts.class);
	}

	
	@Override
	public List<Districts> getDistrictsByParentId(Long parent_id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Districts where 1=1 and parent_id=?";
		Query query = session.createQuery(hql).setLong(0, parent_id);
		List<Districts> list = query.list();
		
		return list;
	}


	@Override
	public List<Object[]> getDistrictsById(Long id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select * from Districts d1 left join Districts d2 on d1.parent_id=d2.id "
				+ "left join Districts d3 on d2.parent_id=d3.id"
				+ "where 1=1 and d1.id=?";
		Query query = session.createQuery(hql).setLong(0, id);
		List<Object[]> list = query.list();
		return list;
	}


	@Override
	public List<Districts> getDistrictsByHot(int hot) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Districts where 1=1 and hot=?";
		Query query = session.createQuery(hql).setInteger(0, hot);
		List<Districts> list = query.list();
		return list;
	}

	

}
