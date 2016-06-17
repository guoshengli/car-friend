package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Notification;

public interface NotificationDao extends BaseDao<Notification, Long> {
	public void saveNotifications(List<Notification> paramList);

	public List<Notification> getNotifications(Long paramLong,int count);
	
	public List<Notification> getNotificationsByPage(Long paramLong,int count,Long id,int flag);

	public List<Notification> getNotificationsCollection(Long paramLong,int count);
	
	public List<Notification> getNotificationsByPageCollection(Long paramLong,int count,Long id,int flag);
	
	public int deleteNotificationByAction(Long paramLong1, Long paramLong2, int paramInt1, int paramInt2);

	public Notification getNotificationByAction(Long paramLong1, Long paramLong2, int paramInt1, int paramInt2);

	public int getNotificationByRecipientId(Long paramLong);

	public void updateNotificationByLoginUserid(Long paramLong);
	
	public void disableNotification(int object_type,Long object_id);
	
	public void enableNotification(int object_type,Long object_id);
	
	public List<Notification> getNotificationsFollowers(String userids,int count);
	
	public List<Notification> getNotificationsFollowers(String userids,int count,Long notification_id,int identify);
	
	public Notification getNotificationsFollowers(String userids);
	
	public Notification getNotificationsCollectionLastId(Long userId,int count);
}
