package com.friend.rest.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.Collection;
import com.friend.rest.model.Story;
import com.google.common.base.Strings;

@Repository("collectionDao")
@SuppressWarnings("unchecked")
public class CollectionDaoImpl extends BaseDaoImpl<Collection, Integer>implements CollectionDao {
	public CollectionDaoImpl() {
		super(Collection.class);
	}

	public void disableCollection(Long id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "update Collection set status='disabled' where id=?";
		Query query = session.createQuery(hql).setLong(0,id);
		query.executeUpdate();
	}


	public List<Collection> getCollections() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where status='enable' order by number";
		List<Collection> collectionList = session.createQuery(hql).list();
		
		return collectionList;
	}

	public int getCollectionCountByName(String collectionName) {
		String hql = "from Collection where collectionName=? and status=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setString(0, collectionName).setString(1, "enabled");
		int size = query.list().size();
		return size;
	}

	
	public Collection getCollectionById(Long id) {
		String hql = "from Collection where id=? and status=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, id.longValue()).setString(1, "enable");
		List<Collection> list = query.list();
		Collection c = null;
		if ((list != null) && (list.size() > 0)) {
			c = (Collection) list.get(0);
		}
		return c;
	}

	@Override
	public List<Collection> getCollectionBynormal() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where status=? order by number desc";
		List<Collection> collectionList = session.createQuery(hql)
				.setString(0, "enable").list();
		
		return collectionList;
	}

	@Override
	public Collection getCollectionByCollectionName(String collectionname) {
		String hql = "from Collection where collectionName=? and status=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setString(0, collectionname).setString(1, "enabled");
		List<Collection> cList = query.list();
		Collection c = null;
		if(cList != null && cList.size() > 0){
			c = cList.get(0);
		}
		return c;
	}

	@Override
	public List<Collection> getCollectionByRanTen() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where status='enabled'";
		Query query = session.createQuery(hql);
		int size = query.list().size();
		Random r = new Random();
		query.setMaxResults(5);
		if (size == 5)
			query.setFirstResult(0);
		else {
			query.setFirstResult(r.nextInt(Math.abs(size - 5)) + 1);
		}
		List<Collection> list = query.list();
		return list;
	}

	@Override
	public List<Collection> getCollectionBytype(String type, int count) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where status=? order by number";
		List<Collection> collectionList = session.createQuery(hql)
				.setString(0, "enabled").setMaxResults(count).list();
		
		return collectionList;
	}


	@Override
	public List<Collection> getCollectionBytype(int type) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where status=? and type=? order by number";
		List<Collection> collectionList = session.createQuery(hql)
				.setString(0, "enable").setInteger(1, type).list();
		
		return collectionList;
	}

	@Override
	public List<Collection> getCollectionListByCollectionName(String collection_name) {
		
		Session session = getSessionFactory().getCurrentSession();
		List<Collection> collectionList = null;
		if(!Strings.isNullOrEmpty(collection_name)){
			String hql = "from Collection where status='enable' and collection_name like :collection_name";
			Query query = session.createQuery(hql).setString("collection_name", "%"+collection_name+"%");
			collectionList = query.list();
		}else{
			String hql = "from Collection where status='enable'";
			Query query = session.createQuery(hql);
			collectionList = query.list();
		}
		
		return collectionList;
	}

	@Override
	public List<Collection> getCollectionByCarType(String type) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection order by number";
		List<Collection> collectionList = session.createQuery(hql).list();
		return collectionList;
	}

	@Override
	public Collection getCollectionById(int id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where id=?";
		List<Collection> collectionList = session.createQuery(hql).setInteger(0, id).list();
		Collection c = null;
		if(collectionList != null && collectionList.size() > 0){
			c = collectionList.get(0);
		}
		return c;
	}

	@Override
	public List<Collection> getCollectionListByParams(int count, Long districts_id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where 1=1 and districts.parent_id=? and status='enable' and type=60 order by id desc";
		Query query = session.createQuery(hql).setLong(0, districts_id).setMaxResults(count);
		List<Collection> collectionList = query.list();
		return collectionList;
	}

	@Override
	public List<Collection> getCollectionListByParams(int count, Long districts_id, int collection_id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where 1=1 and districts.parent_id=? and id<? and status='enable' and type=60 order by id desc";
		Query query = session.createQuery(hql).setLong(0, districts_id).setInteger(1, collection_id).setMaxResults(count);
		List<Collection> collectionList = query.list();
		return collectionList;
	}

	@Override
	public List<Collection> getCollectionListByParams(int count, Long districts_id, int collection_id,
			String collection_name) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where 1=1 and districts.parent_id=? and id<? and collection_name like :collection_name and status='enable' and type=60 order by id desc";
		Query query = session.createQuery(hql).setLong(0, districts_id).setInteger(1, collection_id).setString("collection_name", "%"+collection_name+"%").setMaxResults(count);
		List<Collection> collectionList = query.list();
		return collectionList;
	}

	@Override
	public List<Collection> getCollectionListByParams(int count, Long districts_id, String collection_name) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where 1=1 and districts.parent_id=? and collection_name like :collection_name and status='enable' and type=60 order by id desc";
		Query query = session.createQuery(hql).setLong(0, districts_id).setString("collection_name", "%"+collection_name+"%").setMaxResults(count);
		List<Collection> collectionList = query.list();
		return collectionList;
	}
	
	

	
}
