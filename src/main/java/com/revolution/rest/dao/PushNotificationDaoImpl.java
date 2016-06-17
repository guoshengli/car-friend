package com.revolution.rest.dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.PushNotification;

@Repository("pushNotificationDao")
@SuppressWarnings("unchecked")
public class PushNotificationDaoImpl extends BaseDaoImpl<PushNotification, Long>implements PushNotificationDao {
	public PushNotificationDaoImpl() {
		super(PushNotification.class);
	}

	public PushNotification getPushNotificationByClientid(String clientId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from PushNotification where 1=1 and clientId=?";
		Query query = session.createQuery(hql).setString(0, clientId);
		List<PushNotification> list = query.list();
		PushNotification pn = null;
		if ((list != null) && (list.size() > 0)) {
			pn = (PushNotification) list.get(0);
		}
		return pn;
	}

	public List<PushNotification> getPushNotificationByUserid(Long userId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from PushNotification where 1=1 and userId=?";
		Query query = session.createQuery(hql).setLong(0, userId.longValue());
		List<PushNotification> list = query.list();
		return list;
	}

	public void deletePushNotification(String client_id, String device_type) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from PushNotification where 1=1 and clientId=? and deviceType=?";
		Query query = session.createQuery(hql).setString(0, client_id).setString(1, device_type);
		query.executeUpdate();
	}

	public PushNotification getPushNotificationByClientidAndDevice(String clientId, String deviceType) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from PushNotification where 1=1 and clientId=? and deviceType=?";
		Query query = session.createQuery(hql).setString(0, clientId).setString(1, deviceType);
		List<PushNotification> list = query.list();
		PushNotification pn = null;
		if ((list != null) && (list.size() > 0)) {
			pn = (PushNotification) list.get(0);
		}
		return pn;
	}

	@Override
	public List<PushNotification> getPushNotification() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from PushNotification";
		Query query = session.createQuery(hql);
		List<PushNotification> list = query.list();
		return list;
	}

	@Override
	public void delPushNotificationByUserId(Long userId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from PushNotification where userId=?";
		Query query = session.createQuery(hql).setLong(0, userId);
		query.executeUpdate();
		
	}

	@Override
	public PushNotification getPushNotificationByDeviceToken(String deviceToken) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from PushNotification where 1=1 and deviceToken=?";
		Query query = session.createQuery(hql).setString(0, deviceToken);
		List<PushNotification> list = query.list();
		PushNotification pn = null;
		if(list != null && list.size() > 0){
			pn = list.get(0);
		}
		return pn;
	}
}
