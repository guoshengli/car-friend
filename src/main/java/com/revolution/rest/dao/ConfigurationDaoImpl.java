package com.revolution.rest.dao;

import java.util.List;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Configuration;

@Repository("configurationDao")
@SuppressWarnings("unchecked")
public class ConfigurationDaoImpl extends BaseDaoImpl<Configuration, Long>implements ConfigurationDao {
	public ConfigurationDaoImpl() {
		super(Configuration.class);
	}

	public Configuration getConfByUserId(Long userId) {
		String hql = "from Configuration where userId = ?";
		Session session = getSessionFactory().getCurrentSession();
		List<Configuration> confList = session.createQuery(hql).setLong(0, userId).list();
		Configuration conf = null;
		if ((confList != null) && (confList.size() > 0)) {
			conf = (Configuration) confList.get(0);
		}
		return conf;
	}

	public void update(Configuration conf) {
		Session session = getSessionFactory().getCurrentSession();
		session.update(conf);
	}
}
