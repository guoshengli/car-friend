package com.friend.rest.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.FeatureCollection;
@Repository(value="featureCollectionDao")
@SuppressWarnings("unchecked")
public class FeatureCollectionDaoImpl extends BaseDaoImpl<FeatureCollection, Long> 
implements FeatureCollectionDao {

	public FeatureCollectionDaoImpl() {
		super(FeatureCollection.class);
	}

	
	@Override
	public FeatureCollection getFeatureCollectionByCollectionId(Long collectionId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from FeatureCollection where 1=1 and collectionId=?";
		Query query = session.createQuery(hql).setLong(0,collectionId);
		List<FeatureCollection> list = query.list();
		FeatureCollection fc = null;
		if(list != null && list.size() > 0){
			fc = list.get(0);
		}
		return fc;
	}


	@Override
	public void delFeatureCollection(Long collectionId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from FeatureCollection where 1=1 and collectionId=?";
		Query query = session.createQuery(hql).setLong(0,collectionId);
		query.setLong(0,collectionId);
		query.executeUpdate();
	}


	@Override
	public List<FeatureCollection> getFeatureCollectionByThree() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from FeatureCollection";
		Query query = session.createQuery(hql).setMaxResults(3);
		List<FeatureCollection> list = query.list();
		return list;
	}


	@Override
	public List<FeatureCollection> getFeatureCollection(int count) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from FeatureCollection order by id desc";
		Query query = session.createQuery(hql).setMaxResults(count);
		List<FeatureCollection> list = query.list();
		return list;
	}


	@Override
	public List<FeatureCollection> getFeatureCollectionByPage(Long id, int count, int identify) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "";
		List<FeatureCollection> fcList = new ArrayList<FeatureCollection>();
		if (identify == 1) {
			
			hql = "from FeatureCollection where id < ? order by id desc";

		} else if (identify == 2) {
			hql = "from FeatureCollection where id < ? order by id desc";
			Query query = session.createQuery(hql);
			query.setLong(0, id);
			query.setMaxResults(20);
			fcList = query.list();
		}
		return fcList;
	}


	@Override
	public FeatureCollection getFeatureCollectionByOne() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from FeatureCollection";
		Query query = session.createQuery(hql);
		int size = query.list().size();
		Random r = new Random();
		query.setMaxResults(1);
		if (size == 1)
			query.setFirstResult(0);
		else {
			query.setFirstResult(r.nextInt(Math.abs(size - 1)) + 1);
		}
		List<FeatureCollection> list = query.list();
		FeatureCollection fc = null;
		if(list != null && list.size() > 0){
			fc = list.get(0);
		}
		return fc;
	}
}
