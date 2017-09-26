package com.friend.rest.service;

import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.friend.rest.common.PushNotificationUtil;
import com.friend.rest.model.PushNotification;

@Service
public class JobService {
	@Async
	public void run(String appId,String appKey,String masterSecret,List<PushNotification> pnList,String content,String str){
		try {
			PushNotificationUtil.pushInfoAllCopy(appId, appKey, masterSecret, pnList, content,str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
