package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.PushNotification;

public interface PushNotificationDao extends BaseDao<PushNotification, Long>
{
  public PushNotification getPushNotificationByClientid(String paramString);

  public PushNotification getPushNotificationByClientidAndDevice(String paramString1, String paramString2);

  public List<PushNotification> getPushNotificationByUserid(Long paramLong);

  public void deletePushNotification(String paramString1, String paramString2);
  
  public List<PushNotification> getPushNotification();
  
  public void delPushNotificationByUserId(Long userId);
  
  public PushNotification getPushNotificationByDeviceToken(String deviceToken);
}

