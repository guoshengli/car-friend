package com.revolution.rest.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Collection;
import com.revolution.rest.model.Story;

@Repository("collectionDao")
@SuppressWarnings("unchecked")
public class CollectionDaoImpl extends BaseDaoImpl<Collection, Long>implements CollectionDao {
	public CollectionDaoImpl() {
		super(Collection.class);
	}

	public void disableCollection(Long id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "update Collection set status='disabled' where id=?";
		Query query = session.createQuery(hql).setLong(0,id);
		query.executeUpdate();
	}

	public List<Story> getStoriesByCollection(Long id) {
		List<Story> result = new ArrayList<Story>((get(id)).getStories());
		return result;
	}

	public List<Collection> getCollections() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where status=? order by number";
		List<Collection> collectionList = session.createQuery(hql).setString(0, "enabled").list();
		
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
		query.setLong(0, id.longValue()).setString(1, "enabled");
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
				.setString(0, "enabled").list();
		
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
	public List<Collection> getCollectionBytype(String type, int count, Long collection_id, int identify) {
		Collection collection = get(collection_id);
		List<Collection> cList = new ArrayList<Collection>();
		if(collection != null){
			Session session = getSessionFactory().getCurrentSession();
			Long num = collection.getNumber();
		      String hql = "";
		      if (identify == 1) {
		    	  hql = "from Collection where status=? and id != ? and number <= ? order by number";
		    	  cList = session.createQuery(hql)
		  				.setString(0, "enabled").setLong(1,collection_id).setLong(2,num).setMaxResults(count).list();
		      }else if(identify == 2){
		    	  hql = "from Collection where status=? and id != ? and number >= ? order by number";
		    	  cList = session.createQuery(hql)
		  				.setString(0, "enabled").setLong(1,collection_id).setLong(2,num).setMaxResults(count).list();
		      }
		}
		return cList;
	}

	@Override
	public List<Collection> getCollectionBytype(String type) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Collection where status=? order by number";
		List<Collection> collectionList = session.createQuery(hql)
				.setString(0, "enabled").list();
		
		return collectionList;
	}
	
	

	
}
