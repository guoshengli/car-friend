package com.revolution.rest.dao;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Conversion;
@Repository("conversionDao")
@SuppressWarnings("unchecked")
public class ConversionDaoImpl extends BaseDaoImpl<Conversion, Long> implements ConversionDao {

	public ConversionDaoImpl() {
		super(Conversion.class);
	}

	
	@Override
	public List getConversion(int count) {
		String hql = "select c.id,c.target_user_id,a.content,a.create_time from conversion c left join (select content,create_time,conversion_id  from (select content,create_time,conversion_id from chat ORDER BY create_time desc) t group by t.conversion_id) a on c.id=a.conversion_id order by c.id desc limit "+count+";";
		Session session = getSessionFactory().getCurrentSession();
		List list = session.createSQLQuery(hql).list();
		return list;
	}


	@Override
	public List getConversionById(int count, Long id) {
		String hql = "select c.id,c.target_user_id,a.content,a.create_time from conversion c left join (select content,create_time,conversion_id  from (select content,create_time,conversion_id from chat ORDER BY create_time desc) t group by t.conversion_id) a on c.id=a.conversion_id where c.id < "+id+" order by c.id desc limit "+count+";";
		Session session = getSessionFactory().getCurrentSession();
		List list = session.createSQLQuery(hql).list();
		return list;
	}
	
}
