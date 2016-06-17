 package com.revolution.rest.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.User;


 

 
 @Repository("userDao")
 @SuppressWarnings("unchecked")
 public class UserDaoImpl extends BaseDaoImpl<User, Long>
   implements UserDao
 {
   public UserDaoImpl()
   {
     super(User.class);
   }
 
   public User loginUser(String email, String password)
   {
     String hql = "from User where email = ? and password = ? and status='enabled'";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setString(0, email);
     query.setString(1, password);
     List<User> list = query.list();
     User user = null;
     if ((list != null) && (list.size() > 0)) {
       user = list.get(0);
     }
     return user;
   }
 
   public User loginByPhone(String zone, String phone, String password)
   {
     String hql = "from User where 1=1 and zone = ? and phone=? and password = ? and status='enabled'";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setString(0, zone);
     query.setString(1, phone);
     query.setString(2, password);
     List<User> list = query.list();
     User user = null;
     if ((list != null) && (list.size() > 0)) {
       user = (User)list.get(0);
     }
     return user;
   }
 
   public void disableUser(Long key)
   {
     String hql = "update User set status=? where id=?";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setString(0, "disabled");
     query.setLong(1, key.longValue());
     query.executeUpdate();
   }
 
   public User getUserByUsernameAndEmail(String username, String email)
   {
     String hql = "from User where username=? and email=?";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setString(0, username);
     query.setString(1, email);
     List<User> list = query.list();
     User user = null;
     if ((list != null) && (list.size() > 0)) {
       user = (User)list.get(0);
     }
     return user;
   }
 
   public User getUserByZoneAndPhone(String zone, String phone)
   {
     String hql = "from User where zone=? and phone=?";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setString(0, zone);
     query.setString(1, phone);
     List<User> list = query.list();
     User user = null;
     if ((list != null) && (list.size() > 0)) {
       user = (User)list.get(0);
     }
     return user;
   }
 
   public User getUserByEamil(String email)
   {
     String hql = "from User where email=?";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setString(0, email);
     List<User> list = query.list();
     User user = null;
     if ((list != null) && (list.size() > 0)) {
       user = (User)list.get(0);
     }
     return user;
   }
 
   public User getUserByUserName(String userName)
   {
     String hql = "from User where username=?";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setString(0, userName);
     User user = null;
     List<User> list = query.list();
     if ((list != null) && (list.size() > 0)) {
       user = (User)list.get(0);
     }
     return user;
   }
 
   public List<User> getRandomUser()
   {
     Session session = getSessionFactory().getCurrentSession();
     String hql = "from User ";
     Random r = new Random();
     Query query = session.createQuery(hql);
     int size = query.list().size();
     query.setMaxResults(10);
     query.setFirstResult(r.nextInt(Math.abs(size - 10)) + 1);
     List<User> userList = query.list();
     return userList;
   }
 
   public List<User> getUsersByStoryIdAndNull(Long storyId, int count)
   {
     String hql = "select u from User u,Likes l where l.userId=u.id and l.storyId=? order by l.createTime desc";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setLong(0, storyId.longValue());
     query.setMaxResults(count);
     List<User> list = query.list();
     return list;
   }
 
   public List<User> getUsersByStoryIdAndUserId(Long storyId, Long likesId, String createTime, int count, int identify)
   {
     String hql = "";
     List<User> userList = new ArrayList<User>();
 
     Session session = getSessionFactory().getCurrentSession();
     if (identify == 1) {
       hql = "select u from User u,Likes l where l.userId=u.id and l.storyId=? and l.createTime >= ? and l.id !=? order by l.createTime";
       Query query = session.createQuery(hql);
       query.setLong(0, storyId.longValue());
       query.setString(1, createTime);
       query.setLong(2, likesId.longValue());
       query.setMaxResults(count);
       userList = query.list();
       Collections.reverse(userList);
     } else if (identify == 2) {
       hql = "select u from User u,Likes l where l.userId=u.id and l.storyId=? and l.createTime <= ? and l.id !=? order by l.createTime desc";
       Query query = session.createQuery(hql);
       query.setLong(0, storyId.longValue());
       query.setString(1, createTime);
       query.setLong(2, likesId.longValue());
       query.setMaxResults(count);
       userList = query.list();
     }
 
     return userList;
   }
 
   public List<User> getRepostUsersByStoryId(Long storyId, int count)
   {
     String hql = "select u from User u,Republish r where r.userId=u.id and r.storyId=? order by r.createTime desc";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setLong(0, storyId.longValue());
     query.setMaxResults(count);
     List<User> list = query.list();
     return list;
   }
 
   public List<User> getRepostUsersByStoryIdAndRepost(Long storyId, Long repostId, String createTime, int count, int identify)
   {
     String hql = "";
     List<User> userList = new ArrayList<User>();
 
     Session session = getSessionFactory().getCurrentSession();
     if (identify == 1) {
       hql = "select u from User u,Republish r where r.userId=u.id and r.storyId=? and l.createTime >= ? and r.id != ? order by r.createTime";
       Query query = session.createQuery(hql);
       query.setLong(0, storyId.longValue());
       query.setString(1, createTime);
       query.setLong(2, repostId.longValue());
       query.setMaxResults(count);
       userList = query.list();
       Collections.reverse(userList);
     } else if (identify == 2) {
       hql = "select u from User u,Republish r where r.userId=u.id and r.storyId=? and l.createTime <= ? and r.id != ? order by r.createTime desc";
       Query query = session.createQuery(hql);
       query.setLong(0, storyId.longValue());
       query.setString(1, createTime);
       query.setLong(2, repostId.longValue());
       query.setMaxResults(count);
       userList = query.list();
     }
 
     return userList;
   }
 
   public void updateUserByUserType(Long userId, String user_type)
   {
     Session session = getSessionFactory().getCurrentSession();
     String hql = "update User set user_type=? where id=?";
     Query query = session.createQuery(hql);
     query.setString(0, user_type).setLong(1, userId.longValue());
     query.executeUpdate();
   }
 
   public List<User> getUserByName(String username)
   {
     String hql = "from User where username like :username";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setString("username", "%" + username + "%");
     List<User> list = query.list();
     return list;
   }
 
   public User getUserByPhoneAndZone(String zone, String phone)
   {
     String hql = "from User where zone = ? and phone = ? and status='enabled'";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setString(0, zone);
     query.setString(1, phone);
     List<User> list = query.list();
     User user = null;
     if ((list != null) && (list.size() > 0)) {
       user = (User)list.get(0);
     }
     return user;
   }
 
   public List<User> getUserByUserType()
   {
     String hql = "from User where 1=1 and user_type='super_admin' or user_type='admin' and status='enabled'";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     List<User> list = query.list();
     return list;
   }

@Override
public List<User> getUserByUserType(String user_type) {
	 String hql = "from User where 1=1 and user_type=? and status='enabled'";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql).setString(0,user_type);
     List<User> list = query.list();
     return list;
}

@Override
public List<User> getUserByPhone(String phone) {
    String hql = "from User where 1=1 and phone = ? and status='enabled'";
    Session session = getSessionFactory().getCurrentSession();
    Query query = session.createQuery(hql);
    query.setString(0, phone);
    List<User> list = query.list();
    return list;
  
}

@Override
public List<User> getUserRandom() {
	
	String hql = "from User where 1=1 and status='enabled' ORDER BY RAND()";
	Session session = getSessionFactory().getCurrentSession();
    Query query = session.createQuery(hql).setMaxResults(5);
    List<User> list = query.list();
    return list;
}
 }

