package com.revolution.rest.dao;

import java.io.Serializable;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BaseDaoImpl<Entity, PrimaryKey extends Serializable> extends HibernateDaoSupport
		implements BaseDao<Entity, PrimaryKey> {
	private static final Log LOG = LogFactory.getLog(BaseDaoImpl.class);
	private final Class<Entity> entity;

	public BaseDaoImpl(Class<Entity> entity) {
		this.entity = entity;
	}

	@Autowired
	public void init(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	public List<Entity> getAll() {
		LOG.debug("Loading all entities: " + this.entity.getCanonicalName());
		Criteria crit = getSessionFactory().getCurrentSession().createCriteria(this.entity);
		return crit.list();
	}

	public Entity get(PrimaryKey key) {
		LOG.debug("Get entity: " + this.entity.getCanonicalName() + " by primary key: " + key);
		Session session = getSessionFactory().getCurrentSession();
		Entity entityObj = (Entity) session.get(this.entity, key);
		return entityObj;
	}

	public void save(Entity entityObj) {
		LOG.debug("Save entity: " + this.entity.getCanonicalName());
		getSessionFactory().getCurrentSession().save(entityObj);
	}

	public void saveOrUpdate(Entity entityObj) {
		LOG.debug("Save or Update entity: " + this.entity.getCanonicalName());
		System.out.println(getSessionFactory().getCurrentSession().getFlushMode().toString());
		Session session = getSessionFactory().getCurrentSession();
		session.saveOrUpdate(entityObj);
	}

	public void update(Entity entityObj) {
		LOG.debug("Update entity: " + this.entity.getCanonicalName());
		System.out.println(getSessionFactory().getCurrentSession().getFlushMode().toString());
		Session session = getSessionFactory().getCurrentSession();
		session.update(entityObj);
	}

	public void delete(PrimaryKey key) {
		LOG.debug("Delete entity: " + this.entity.getCanonicalName() + " by primary key: " + key);
		Object entityObj = getSessionFactory().getCurrentSession().get(this.entity, key);
		getSessionFactory().getCurrentSession().delete(entityObj);
	}

	public List<Entity> findByHQL(String hql, Object[] params) {
		Query query = getSessionFactory().getCurrentSession().createQuery(hql);
		for (int i = 0; (params != null) && (i < params.length); i++) {
			query.setParameter(i, params);
		}
		return query.list();
	}

	public Entity merge(Entity entityObj) {
		LOG.debug("merge entity: " + this.entity.getCanonicalName());
		return (Entity) getSessionFactory().getCurrentSession().merge(entityObj);
	}
}
