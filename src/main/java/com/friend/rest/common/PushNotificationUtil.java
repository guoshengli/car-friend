package com.friend.rest.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.friend.rest.model.PushNotification;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

import net.sf.json.JSONObject;

public class PushNotificationUtil {
	static String url = "http://sdk.open.api.igexin.com/apiex.htm";

	public static void pushInfoAllFollow(String appId, String appKey, String masterSecret,
			List<PushNotification> pnList, Map<String, Integer> map, String content,String payload) throws Exception {
		IGtPush push = new IGtPush(url, appKey, masterSecret);
		push.connect();
		if ((pnList != null) && (pnList.size() > 0))
			for (PushNotification pn : pnList) {
				int count = ((Integer) map.get(pn.getClientId())).intValue();
				TransmissionTemplate template = setTransmissionTemplate(appId, appKey, count, content,payload);

				SingleMessage message = new SingleMessage();
				message.setOffline(true);

				message.setOfflineExpireTime(86400000L);
				message.setData(template);

				Target target = new Target();
				target.setAppId(appId);
				target.setClientId(pn.getClientId());
				IPushResult ret = push.pushMessageToSingle(message, target);
				System.out.println(ret.getResponse().toString());
			}
	}
	
	public static void pushInfoAll(String appId, String appKey, String masterSecret,
			List<PushNotification> pnList, String content,String payload) throws Exception {
		IGtPush push = new IGtPush(url, appKey, masterSecret);
		push.connect();
		if ((pnList != null) && (pnList.size() > 0))
			for (PushNotification pn : pnList) {
				int count = 0;
				TransmissionTemplate template = setTransmissionTemplate(appId, appKey, count, content,payload);

				SingleMessage message = new SingleMessage();
				message.setOffline(true);

				message.setOfflineExpireTime(86400000L);
				message.setData(template);

				Target target = new Target();
				target.setAppId(appId);
				target.setClientId(pn.getClientId());
				IPushResult ret = push.pushMessageToSingle(message, target);
				System.out.println(ret.getResponse().toString()+"--taskId-->"+ret.getTaskId());
			}
	}
	
	public static void pushInfoAllCopy(String appId, String appKey, String masterSecret,
			List<PushNotification> pnList, String content,String payload) throws Exception {
		String host = "http://sdk.open.api.igexin.com/serviceex";
		IGtPush push = new IGtPush(host, appKey, masterSecret);
		push.connect();
		TransmissionTemplate template = setTransmissionTemplate(appId, appKey, 0
				, content,payload);
		AppMessage message = new AppMessage();
		message.setData(template);
		//设置消息离线，并设置离线时间
        message.setOffline(true);
        //离线有效时间，单位为毫秒，可�?
        message.setOfflineExpireTime(24*1000*3600);
        //设置推�?�目标条件过�?
        List<String> appIdList = new ArrayList<String>();
        appIdList.add(appId);
        message.setAppIdList(appIdList);
        IPushResult ret = push.pushMessageToApp(message);
		System.out.println(ret.getResponse().toString()+"-->"+ret.getTaskId());
	}
	
	public static void pushInfoAllAdmin(String appId, String appKey, String masterSecret, List<PushNotification> pnList,
			Map<String, Integer> map, String content,String payload) throws Exception {
		IGtPush push = new IGtPush(url, appKey, masterSecret);
		push.connect();
		if ((pnList != null) && (pnList.size() > 0))
			for (PushNotification pn : pnList) {
				int count = ((Integer) map.get(pn.getClientId())).intValue();

				LinkTemplate template = linkTemplateDemo(appId, appKey, count, content);
				SingleMessage message = new SingleMessage();
				message.setOffline(true);

				message.setOfflineExpireTime(86400000L);
				message.setData(template);

				Target target = new Target();
				target.setAppId(appId);
				target.setClientId(pn.getClientId());
				IPushResult ret = push.pushMessageToSingle(message, target);
				System.out.println(ret.getResponse().toString());
			}
	}

	public static String pushInfo(String appId, String appKey, String masterSecret, List<PushNotification> pnList,
			int count, String content,String payload) throws Exception {
		IGtPush push = new IGtPush(url, appKey, masterSecret);
		push.connect();

		TransmissionTemplate template = setTransmissionTemplate(appId, appKey, count, content,payload);

		ListMessage message = new ListMessage();
		message.setData(template);
		message.setOffline(true);

		message.setOfflineExpireTime(86400000L);

		List<Target> targets = new ArrayList<Target>();
		Target target = null;
		if ((pnList != null) && (pnList.size() > 0)) {
			for (PushNotification pn : pnList) {
				target = new Target();
				target.setAppId(appId);
				target.setClientId(pn.getClientId());
				targets.add(target);
			}
		}

		String taskId = push.getContentId(message);

		IPushResult ret = push.pushMessageToList(taskId, targets);

		System.out.println(ret.getResponse().toString());
		
       
		return ret.getResponse().toString();
	}
	
	public static String pushSimple(String appId, String appKey, String masterSecret, PushNotification pn,
			int count, String content,String payload) throws Exception {
		IGtPush push = new IGtPush(url, appKey, masterSecret);
		push.connect();

		TransmissionTemplate template = setTransmissionTemplate(appId, appKey, count, content,payload);

		ListMessage message = new ListMessage();
		message.setData(template);
		message.setOffline(true);

		message.setOfflineExpireTime(86400000L);

		List<Target> targets = new ArrayList<Target>();
		Target target = null;
		if (pn != null) {
			target = new Target();
			target.setAppId(appId);
			target.setClientId(pn.getClientId());
			targets.add(target);
		}

		String taskId = push.getContentId(message);

		IPushResult ret = push.pushMessageToList(taskId, targets);

		System.out.println(ret.getResponse().toString());
		
       
		return ret.getResponse().toString();
	}

	public static TransmissionTemplate setTransmissionTemplate(String appId, String appKey, int count, String content,String payload)
			throws Exception {
		TransmissionTemplate template = new TransmissionTemplate();

		template.setAppId(appId);
		template.setAppkey(appKey);
		template.setTransmissionType(2);
		template.setTransmissionContent(payload);
		// 设置定时展示时间
//		template.setDuration(begin, end);
		JSONObject json = JSONObject.fromObject(payload);
		APNPayload.DictionaryAlertMsg alertMsg = getDictionaryAlertMsg(content, json.getString("description"));
		APNPayload load = new APNPayload();
		load.setSound("com.gexin.ios.silence");
	    load.setContentAvailable(1);
		load.setAlertMsg(alertMsg);
		load.addCustomMsg("payload", payload);
		template.setAPNInfo(load);
//		template.setPushInfo("", count, content, "com.gexin.ios.silence",payload, "", "", "");
		 return template;
	}
	
	private static APNPayload.DictionaryAlertMsg getDictionaryAlertMsg(String title,String payload){
	    APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
	    alertMsg.setBody(payload);
	    alertMsg.setActionLocKey("ActionLockey");
	    alertMsg.setLocKey(title);
	    alertMsg.addLocArg(payload);
//	    alertMsg.setLaunchImage("launch-image");
	    // iOS8.2以上版本支持
	    alertMsg.setTitle(title);
	    alertMsg.setTitleLocKey(title);
//	    alertMsg.addTitleLocArg("TitleLocArg");
	    return alertMsg;
	}

	@SuppressWarnings("deprecation")
	public static NotificationTemplate notificationTemplateDemo(String appId, String appKey, int count, String content)
			throws Exception {
		NotificationTemplate template = new NotificationTemplate();

		template.setAppId(appId);
		template.setAppkey(appKey);

		template.setTitle("于晨�?");
		template.setText("于晨�?");

		template.setLogo("icon.png");

		template.setLogoUrl("");

		template.setIsRing(true);
		template.setIsVibrate(true);
		template.setIsClearable(true);
		template.setPushInfo("", count, content, "default", "透传消息", "", "", "");

		template.setTransmissionType(2);
		template.setTransmissionContent("于晨�?");
		return template;
	}

	public static LinkTemplate linkTemplateDemo(String appId, String appKey, int count, String content) {
		LinkTemplate template = new LinkTemplate();

		template.setAppId(appId);
		template.setAppkey(appKey);

		template.setIsRing(true);
		template.setIsVibrate(true);
		template.setIsClearable(true);
		return template;
	}
}
