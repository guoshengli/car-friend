package com.revolution.rest.dao;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Collection;
import com.revolution.rest.model.User;
import com.revolution.rest.model.UserCollection;
@Repository(value="userCollectionDao")
@SuppressWarnings({"unchecked","rawtypes"})
public class UserCollectionDaoImpl 
	extends BaseDaoImpl<UserCollection, Long> 
		implements UserCollectionDao {

	public UserCollectionDaoImpl() {
		super(UserCollection.class);
	}

	
	@Override
	public int getCollectionCountByUserid(Long userid) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select count(id) from UserCollection where users.id=?";
		List<Long> list = session.createQuery(hql).setLong(0, userid).list();
		int count = 0;
		if ((list != null) && (list.size() > 0)) {
			Long l = list.get(0);
			count = l.intValue();
		}
		return count;
	}


	@Override
	public List<Collection> getCollectionByUserid(Long userid) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select uc.collections from UserCollection uc where 1=1 and uc.users.id=? and uc.collections.collection_type != 'activity'";
		List<Collection> list = session.createQuery(hql).setLong(0, userid).list();
		return list;
	}
	
	@Override
	public List<Collection> getCollectionByUserid(Long userid,String type) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select uc.collections from UserCollection uc where 1=1 and uc.users.id=? and uc.collections.collection_type != ?";
		List<Collection> list = session.createQuery(hql).setLong(0, userid).setString(1,type).list();
		return list;
	}
	
	@Override
	public List<Collection> getCollectionByUserid(Long userid, int count) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select uc.collections from UserCollection uc where 1=1 and uc.users.id=? and uc.collections.collection_type='activity' order by uc.create_time desc";
		Query query = session.createQuery(hql).setLong(0,userid);
		query.setMaxResults(count);
		List<Collection> list = query.list();
		return list;
	}

	@Override
	public List<Collection> getCollectionByUserid(Long userid, int count, Long collection_id, int identify) {

	     UserCollection uc = getUserCollectionByCollectionId(collection_id, userid);
	     List<Collection> cList = new ArrayList<Collection>();
	     if (uc != null) {
	       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	       long timestamp = uc.getCreate_time().longValue() * 1000L;
	       String create_time = sdf.format(new Date(timestamp));
	 
	       Session session = getSessionFactory().getCurrentSession();
	       String hql = "";
	       if (identify == 1) {
	         hql = "select uc.collections from UserCollection uc where 1=1 and uc.users.id=? and uc.collections.collection_type='activity' and uc.create_time <= ? and uc.id != ? order by uc.create_time";
	         Query query = session.createQuery(hql);
	         query.setLong(0, userid);
	         query.setString(1, create_time);
	         query.setLong(2, uc.getId());
	         query.setMaxResults(count);
	         cList = query.list();
	         Collections.reverse(cList);
	       }
	       else if (identify == 2) {
	         hql = "select uc.collections from UserCollection uc where 1=1 and uc.users.id=? and uc.collections.collection_type='activity' and uc.create_time <= ? and uc.id != ? order by create_time desc";
	         Query query = session.createQuery(hql);
	         query.setLong(0, userid);
	         query.setString(1, create_time);
	         query.setLong(2, uc.getId());
	         query.setMaxResults(count);
	         cList = query.list();
	       }
	 
	     }
	 
	   
		return cList;
	}


	@Override
	public void deleteUserCollection(Long loginUserid, Long collection_id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from UserCollection uc where 1=1 and uc.users.id=? and uc.collections.id = ?";
		Query query = session.createQuery(hql).setLong(0,loginUserid).setLong(1,collection_id);
		query.executeUpdate();
	}


	@Override
	public UserCollection getUserCollectionByCollectionId(Long collectionId,Long userid) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from UserCollection uc where 1=1 and uc.collections.id=? and uc.users.id=?";
		List<UserCollection> list = session.createQuery(hql).setLong(0, collectionId).setLong(1, userid).list();
		UserCollection uc = null;
		if(list != null && list.size() > 0){
			uc = list.get(0);
		}
		return uc;
	}


	@Override
	public void updateUserCollectionSequenceByUserIdAndCollectionId(Long userId, Long collectionId,int sequence) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "update UserCollection set sequence=? where 1=1 and users.id=? and collections.id=?";
		Query query = session.createQuery(hql).setLong(0,sequence).setLong(1,userId).setLong(2,collectionId);
		query.executeUpdate();
		
	}


	@Override
	public void delUserCollectionByCollectionId(Long collectionId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from UserCollection uc where 1=1 and uc.collections.id = ?";
		Query query = session.createQuery(hql).setLong(0,collectionId);
		query.executeUpdate();
	}

	@Override
	public List<Object[]> getCollectionByHot() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select count(*),uc.collections from UserCollection uc group by uc.collections.id order by count(*) desc";
		Query query = session.createQuery(hql).setMaxResults(6);
		List list = query.list();
		return list;
	}


	@Override
	public List<Object[]> getCollectionByHot(int count) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select count(*),uc.collections from UserCollection uc where uc.collections.status='enabled' group by uc.collections.id order by count(*) desc";
		Query query = session.createQuery(hql).setMaxResults(count);
		List list = query.list();
		return list;
	}


	@Override
	public List<Object[]> getCollectionByHot(int num,Long collection_id, int count, int identify) {

		Session session = getSessionFactory().getCurrentSession();
		String hql = "";
		List<Object[]> ucList = new ArrayList<Object[]>();
		if (identify == 1) {
			
			hql = "from FeatureCollection where id < ? order by id desc";

		} else if (identify == 2) {
			hql = "select count(*) count,uc.collections c from UserCollection uc where 1=1 and count <= ? and c.id != ? and uc.collections.status='enabled' group by uc.collections.id order by count(*) desc";
			Query query = session.createQuery(hql);
			query.setInteger(0,num).setLong(1,collection_id);
			query.setMaxResults(count);
			ucList = query.list();
		}
		return ucList;
	
	}


	@Override
	public int getCollectionByCount(Long collection_id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select count(uc.id) from UserCollection uc where uc.collections.id = ? group by uc.collections.id";
		Query query = session.createQuery(hql).setLong(0, collection_id);
		List<Object> list = query.list();
		int count = 0;
		if(list != null && list.size() > 0){
			Object o = list.get(0);
			count = Integer.parseInt(o.toString());
		}
		return count;
	}


	@Override
	public List<User> getUserByCollectionId(Long collectionId, int count) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select uc.users from UserCollection uc where 1=1 and uc.collections.id=? order by uc.create_time desc";
		Query query = session.createQuery(hql).setLong(0,collectionId).setMaxResults(count);
		List<User> list = query.list();
		return list;
	}


	@Override
	public List<User> getUserByCollectionIdPage(Long collectionId, int count, Long userId, int identify) {

	     UserCollection uc = getUserCollectionByCollectionId(collectionId, userId);
	     List<User> uList = new ArrayList<User>();
	     if (uc != null) {
	       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	       long timestamp = uc.getCreate_time().longValue() * 1000L;
	       String create_time = sdf.format(new Date(timestamp));
	 
	       Session session = getSessionFactory().getCurrentSession();
	       String hql = "";
	       if (identify == 1) {
	         hql = "select uc.users from UserCollection uc where 1=1 and uc.collections.id=? and uc.create_time <= ? and uc.id != ? order by uc.create_time";
	         Query query = session.createQuery(hql);
	         query.setLong(0, collectionId);
	         query.setString(1, create_time);
	         query.setLong(2, uc.getId());
	         query.setMaxResults(count);
	         uList = query.list();
	         Collections.reverse(uList);
	       }
	       else if (identify == 2) {
	         hql = "select uc.users from UserCollection uc where 1=1 and uc.collections.id=? and uc.create_time <= ? and uc.id != ? order by uc.create_time desc";
	         Query query = session.createQuery(hql);
	         query.setLong(0, collectionId);
	         query.setString(1, create_time);
	         query.setLong(2, uc.getId());
	         query.setMaxResults(count);
	         uList = query.list();
	       }
	 
	     }
	 
	   
		return uList;
	}
	
}
