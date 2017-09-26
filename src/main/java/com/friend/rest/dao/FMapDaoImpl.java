package com.friend.rest.dao;


import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.FMap;
@Repository("fmapDao")
public class FMapDaoImpl extends BaseDaoImpl<FMap, Long> implements FMapDao {

	public FMapDaoImpl() {
		super(FMap.class);
	}

	@Override
	public List<FMap> getFMapList(int count,String type,int ssm_id) {
		String hql = "from FMap where 1=1 and type=? and ssm_id=? order by id desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setString(0,type).setInteger(1,ssm_id);
		query.setMaxResults(count);
		List<FMap> list = query.list();
		return list;
	}

	@Override
	public List<FMap> getFMapList(int count, Long max_id,String type,int ssm_id) {
		String hql = "from FMap where id<? and type=? and ssm_id=? order by id desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0,max_id).setString(1,type).setInteger(2,ssm_id);
		query.setMaxResults(count);
		List<FMap> list = query.list();
		return list;
	}

	@Override
	public List<FMap> getFMapList(Long story_id) {
		String hql = "from FMap where wiki_id = ?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0,story_id);
		List<FMap> list = query.list();
		return list;
	}
	
	
	
}
