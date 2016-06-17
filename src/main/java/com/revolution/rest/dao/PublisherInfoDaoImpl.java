package com.revolution.rest.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.PublisherInfo;

@Repository("publisherInfoDao")
public class PublisherInfoDaoImpl extends BaseDaoImpl<PublisherInfo, Long>implements PublisherInfoDao {
	public PublisherInfoDaoImpl() {
		super(PublisherInfo.class);
	}

	public void deletePublisherInfo(Long user_id, String type) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from PublisherInfo where 1=1 and user.id=? and type=?";
		Query query = session.createQuery(hql).setLong(0, user_id.longValue()).setString(1, type);
		query.executeUpdate();
	}
}
