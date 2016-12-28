package com.revolution.rest.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Notification;

@Repository("notificationDao")
@SuppressWarnings("unchecked")
public class NotificationDaoImpl extends BaseDaoImpl<Notification, Long>implements NotificationDao {
	public NotificationDaoImpl() {
		super(Notification.class);
	}

	public void saveNotifications(List<Notification> notificationList) {
		Session session = getSessionFactory().getCurrentSession();
		if ((notificationList != null) && (notificationList.size() > 0))
			for (int i = 0; i < notificationList.size(); i++) {
				session.save(notificationList.get(i));
				if (i % 50 == 0) {
					session.flush();
					session.clear();
				}
			}
	}

	public List<Notification> getNotifications(Long userId,int count) {
		String hql = "from Notification where recipientId=? and notificationType in (1,2,3,5,6,7,8,15,16) and status='enabled' order by create_at desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql).setLong(0, userId.longValue());
		query.setMaxResults(count);
		List<Notification> nList = query.list();
		return nList;
	}
	
	@Override
	public List<Notification> getNotificationsByPage(Long paramLong, int count, Long id, int flag) {
		Notification notification = get(id);
		Session session = getSessionFactory().getCurrentSession();
		List<Notification> list = new ArrayList<Notification>();
		if(flag == 1){
			Long createTime = Long.valueOf(notification.getCreate_at().longValue() * 1000L);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(new Date(createTime.longValue()));
			String hql = "from Notification where recipientId=? and notificationType in (1,2,3,5,6,7,8,15,16) and create_at <= ? and id != ? and status='enabled' order by create_at desc";
			Query query = session.createQuery(hql).setLong(0,paramLong).setString(1,create_time).setLong(2, notification.getId());
			query.setMaxResults(count);
			list = query.list();
			
		}else if(flag == 2){
			Long createTime = Long.valueOf(notification.getCreate_at().longValue() * 1000L);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(new Date(createTime.longValue()));
			String hql = "from Notification where recipientId=? and notificationType in (1,2,3,5,6,7,8,15,16) and create_at >= ? and id != ? and status='enabled' order by create_at";
			Query query = session.createQuery(hql).setLong(0,paramLong).setString(1,create_time).setLong(2, notification.getId());
			query.setMaxResults(count);
			list = query.list();
			Collections.reverse(list);
		}
		return list;
	}
	
	public List<Notification> getNotificationsCollection(Long userId,int count) {
		String hql = "from Notification where recipientId=? and notificationType in (9,10,11,12,13) and status='enabled' order by create_at desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql).setLong(0, userId.longValue());
		query.setMaxResults(count);
		List<Notification> nList = query.list();
		return nList;
	}
	
	@Override
	public Notification getNotificationsCollectionLastId(Long userId,int count) {
		String hql = "from Notification where recipientId=? and notificationType in (9,10,11,12,13) and status='enabled' order by id desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql).setLong(0, userId.longValue());
		query.setMaxResults(1);
		List<Notification> nList = query.list();
		Notification n = null;
		if(nList != null && nList.size() > 0){
			n = nList.get(0);
		}
		return n;
	}
	
	@Override
	public List<Notification> getNotificationsByPageCollection(Long paramLong, int count, Long id, int flag) {
		Notification notification = get(id);
		Session session = getSessionFactory().getCurrentSession();
		List<Notification> list = new ArrayList<Notification>();
		if(flag == 1){
			Long createTime = Long.valueOf(notification.getCreate_at().longValue() * 1000L);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(new Date(createTime.longValue()));
			String hql = "from Notification where recipientId=? and notificationType in (9,10,11,12,13) and create_at <= ? and id != ? and status='enabled' order by create_at desc";
			Query query = session.createQuery(hql).setLong(0,paramLong).setString(1,create_time).setLong(2, notification.getId());
			query.setMaxResults(count);
			list = query.list();
			
		}else if(flag == 2){
			Long createTime = Long.valueOf(notification.getCreate_at().longValue() * 1000L);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(new Date(createTime.longValue()));
			String hql = "from Notification where recipientId=? and notificationType in (9,10,11,12,13) and create_at >= ? and id != ? and status='enabled' order by create_at";
			Query query = session.createQuery(hql).setLong(0,paramLong).setString(1,create_time).setLong(2, notification.getId());
			query.setMaxResults(count);
			list = query.list();
			Collections.reverse(list);
		}
		return list;
	}

	public int deleteNotificationByAction(Long storyId, Long loginUserid, int objectType, int notificationType) {
		String hql = "delete from Notification where objectId=? and senderId=? and objectType=? and notificationType=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, storyId.longValue()).setLong(1, loginUserid.longValue()).setInteger(2, objectType)
				.setInteger(3, notificationType);
		int bol = query.executeUpdate();
		return bol;
	}

	public Notification getNotificationByAction(Long storyId, Long loginUserid, int objectType, int notificationType) {
		String hql = "from Notification where objectId=? and senderId=? and objectType=? and notificationType=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, storyId.longValue()).setLong(1, loginUserid.longValue()).setInteger(2, objectType)
				.setInteger(3, notificationType);
		List<Notification> list = query.list();
		Notification notification = null;
		if ((list != null) && (list.size() > 0)) {
			notification = (Notification) list.get(0);
		}
		return notification;
	}

	public int getNotificationByRecipientId(Long reccipientId) {
		String hql = "from Notification where 1=1 and recipientId=? and read_already=0";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql).setLong(0, reccipientId);
		List<Notification> list = query.list();
		return list.size();
	}

	public void updateNotificationByLoginUserid(Long userId) {
		String hql = "update Notification set read_already=1 where recipientId=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql).setLong(0, userId.longValue());
		query.executeUpdate();
	}

	@Override
	public void disableNotification(int object_type, Long object_id) {
		String hql = "update Notification set status='disabled' where objectType=? and objectId=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql).setInteger(0,object_type).setLong(1, object_id);
		query.executeUpdate();
	}

	@Override
	public void enableNotification(int object_type, Long object_id) {
		String hql = "update Notification set status='enabled' where objectType=? and objectId=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql).setInteger(0,object_type).setLong(1, object_id);
		query.executeUpdate();
	}

	@Override
	public List<Notification> getNotificationsFollowers(String userids,int count) {
		String type = "3,4,14";
		String hql = "from Notification where 1=1 and status='enabled' and senderId in ("+userids+") and notificationType in ("+type+") group by notificationType,objectType,objectId order by create_time desc";
		Session session = getSessionFactory().getCurrentSession();
		List<Notification> list = session.createQuery(hql).setMaxResults(count).list();
		return list;
	}

	@Override
	public List<Notification> getNotificationsFollowers(String userids, int count, Long notification_id, int identify) {
		String type = "3,4,14";
		Notification notification = get(notification_id);
		Session session = getSessionFactory().getCurrentSession();
		List<Notification> list = new ArrayList<Notification>();
		if(identify == 1){
			Long createTime = Long.valueOf(notification.getCreate_at().longValue() * 1000L);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(new Date(createTime.longValue()));
			String hql = "from Notification where 1=1 and status='enabled' and senderId in ("+userids+") and notificationType in ("+type+") and create_at <= ? and id != ? group by notificationType,objectType,objectId order by create_time desc";
			Query query = session.createQuery(hql).setString(0,create_time).setLong(1,notification_id);
			query.setMaxResults(count);
			list = query.list();
			
		}else if(identify == 2){
			Long createTime = Long.valueOf(notification.getCreate_at().longValue() * 1000L);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(new Date(createTime.longValue()));
			String hql = "from Notification where senderId in ("+userids+")  and notificationType in ("+type+") and create_at >= ? and id != ? and status='enabled' group by notificationType,objectType,objectId order by create_at";
			Query query = session.createQuery(hql).setString(0,create_time).setLong(1,notification_id);
			query.setMaxResults(count);
			list = query.list();
			Collections.reverse(list);
		}
		return list;
	
	}

	@Override
	public Notification getNotificationsFollowers(String userids) {
		String type = "3,4,14";
		String hql = "from Notification where 1=1 and status='enabled' and senderId in ("+userids+") and notificationType in ("+type+") group by notificationType,objectType,objectId order by id desc";
		Session session = getSessionFactory().getCurrentSession();
		List<Notification> list = session.createQuery(hql).setMaxResults(1).list();
		Notification n = null;
		if(list != null && list.size() > 0){
			n = list.get(0);
		}
		return n;
	}

	
}
