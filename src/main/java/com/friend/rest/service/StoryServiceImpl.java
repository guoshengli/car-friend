package com.friend.rest.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.friend.rest.common.FBEncryption;
import com.friend.rest.common.HttpUtil;
import com.friend.rest.common.ParseFile;
import com.friend.rest.common.PushNotificationUtil;
import com.friend.rest.dao.CollectionDao;
import com.friend.rest.dao.CollectionStoryDao;
import com.friend.rest.dao.CommentDao;
import com.friend.rest.dao.ConfigurationDao;
import com.friend.rest.dao.FMapDao;
import com.friend.rest.dao.NotificationDao;
import com.friend.rest.dao.PushNotificationDao;
import com.friend.rest.dao.ReportDao;
import com.friend.rest.dao.RepublishDao;
import com.friend.rest.dao.StoryDao;
import com.friend.rest.dao.StoryElementDao;
import com.friend.rest.dao.TimelineDao;
import com.friend.rest.dao.UserDao;
import com.friend.rest.model.Collection;
import com.friend.rest.model.CollectionStory;
import com.friend.rest.model.Columns;
import com.friend.rest.model.Comment;
import com.friend.rest.model.Configuration;
import com.friend.rest.model.FMap;
import com.friend.rest.model.Notification;
import com.friend.rest.model.PublisherInfo;
import com.friend.rest.model.PushNotification;
import com.friend.rest.model.Report;
import com.friend.rest.model.Republish;
import com.friend.rest.model.Story;
import com.friend.rest.model.StoryElement;
import com.friend.rest.model.User;
import com.friend.rest.service.model.CollectionIntro;
import com.friend.rest.service.model.CommentModel;
import com.friend.rest.service.model.CommentStoryModel;
import com.friend.rest.service.model.CommentSummaryModel;
import com.friend.rest.service.model.CoverMedia;
import com.friend.rest.service.model.EventModel;
import com.friend.rest.service.model.IframeCover;
import com.friend.rest.service.model.ImageCover;
import com.friend.rest.service.model.LineCover;
import com.friend.rest.service.model.LinkModel;
import com.friend.rest.service.model.LinkModels;
import com.friend.rest.service.model.LocationModel;
import com.friend.rest.service.model.PublisherInfoModel;
import com.friend.rest.service.model.ReplyCommentModel;
import com.friend.rest.service.model.StoryElementModel;
import com.friend.rest.service.model.StoryEvent;
import com.friend.rest.service.model.StoryIntro;
import com.friend.rest.service.model.StoryIntros;
import com.friend.rest.service.model.StoryLastModel;
import com.friend.rest.service.model.StoryModel;
import com.friend.rest.service.model.StoryPageModel;
import com.friend.rest.service.model.TextCover;
import com.friend.rest.service.model.UserIntro;
import com.friend.rest.service.model.VideoCover;
import com.google.common.base.Strings;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@Transactional
public class StoryServiceImpl implements StoryService {
	private static final Log log = LogFactory.getLog(StoryServiceImpl.class);

	@Autowired
	private StoryDao storyDao;

	@Autowired
	private StoryElementDao storyElementDao;

	@Autowired
	private NotificationDao notificationDao;

	@Autowired
	private TimelineDao timelineDao;

	@Autowired
	private CommentDao commentDao;


	@Autowired
	private RepublishDao republishDao;

	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ReportDao reportDao;

	@Autowired
	private ConfigurationDao configurationDao;

	@Autowired
	private PushNotificationDao pushNotificationDao;

	@Autowired
	private CollectionStoryDao collectionStoryDao;

	
	@Autowired
	private FMapDao fmapDao;

	char[] table = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public String makeURL(long n) {
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < 5; i++) {
			int count = (int) (Math.random() * 16);
			sb1.append(table[count]);
		}
		StringBuilder sb2 = new StringBuilder();
		n = n + 18977216l;
		while (n > 0) {
			sb2.insert(0, table[(int) n % 16]);
			n = n / 16;
		}
		String id = sb2.toString();
		String subId = id.substring(0, 5);
		int a = Integer.parseInt(subId, 16) + Integer.parseInt(sb1.toString(), 16);
		String s = Integer.toHexString(a);
		StringBuffer sb = new StringBuffer();
		sb.append(sb1).append(s).append(id.substring(5, 7));
		return sb.toString();
	}

	public Response createStory(JSONObject storyModel, int loginUserid, HttpServletRequest request)throws Exception {
		log.debug("create story");
		JSONObject json = new JSONObject();
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("story_url");
		
		String token = request.getHeader("X-Tella-Request-Token");
		Map<String,String> storyJson = new HashMap<String, String>();
		storyJson.put("token", token);
		storyJson.put("content", storyModel.toString());
		storyJson.put("ip",ip);
		storyJson.put("device",device);
		storyJson.put("from_channel","30");
		
		String param = carPublicParam(storyJson);
		String result = HttpUtil.sendPostStr(url+"/content/save", param);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				JSONObject data = resJson.getJSONObject("data");
				return Response.status(Response.Status.CREATED).entity(data).build();
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14002){
				json.put("status", "content格式错误");
				json.put("code", 10811);
				json.put("error_message", "content格式错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14003){
				json.put("status", "内容类型错误");
				json.put("code", 10812);
				json.put("error_message", "内容类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14004){
				json.put("status", "内容元素类型错误");
				json.put("code", 10813);
				json.put("error_message", "内容元素类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14005){
				json.put("status", "关系对象类型错误");
				json.put("code", 10814);
				json.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14006){
				json.put("status", "保存内容失败");
				json.put("code", 10815);
				json.put("error_message", "保存内容失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14007){
				json.put("status", "保存内容元素失败");
				json.put("code", 10816);
				json.put("error_message", "保存内容元素失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14008){
				json.put("status", "保存内容地点关系失败");
				json.put("code", 10817);
				json.put("error_message", "保存内容地点关系失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14009){
				json.put("status", "保存内容车款关系失败");
				json.put("code", 10818);
				json.put("error_message", "保存内容车款关系失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14013){
				json.put("status", "保存数据索引失败");
				json.put("code", 10819);
				json.put("error_message", "保存数据索引失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14014){
				json.put("status", "保存数据操作日志失败");
				json.put("code", 10820);
				json.put("error_message", "保存数据操作日志失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14015){
				json.put("status", "地点不存在");
				json.put("code", 10821);
				json.put("error_message", "地点不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14016){
				json.put("status", "车款不存在");
				json.put("code", 10822);
				json.put("error_message", "车款不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 10010){
				json.put("status", "用户token失效");
				json.put("code", 10823);
				json.put("error_message", "用户token失效");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else{
				json.put("status", "帖子发布失败");
				json.put("code", 10626);
				json.put("error_message", "帖子发布失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		
		

		}else{
			JSONObject jo = new JSONObject();
			jo.put("status", "invalid_request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "Invalid request payload");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
		
		
		
	}

	public Response unpublishStory(Long storyId, Long loginUserid) {
		try {
			Story story = (Story) this.storyDao.get(storyId);
			if ((loginUserid.equals(story.getUser().getId())) && (!story.getStatus().equals("unpublish"))) {
				story.setStatus("unpublish");
				this.storyDao.update(story);
				notificationDao.disableNotification(1, storyId);
			} else {
				JSONObject jo = new JSONObject();
				jo.put("status", "no_resources");
				jo.put("code", Integer.valueOf(10012));
				jo.put("error_message", "The story does not exist");
				return Response.status(Response.Status.OK).entity(jo).build();
			}
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("status", "no_resources");
			jo.put("code", Integer.valueOf(10012));
			jo.put("error_message", "The story does not exist");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
		JSONObject jo = new JSONObject();
		jo.put("status", "success");
		return Response.status(Response.Status.OK).entity(jo).build();
	}

	public Response getStory(Long storyId, Long loginUserid, String appVersion,HttpServletRequest request) {

		log.info("***get story begin *****"+storyId);
		Object object = request.getAttribute("X-Tella-Request-Userid");
		if(object != null){
			String loginUserids = object.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		
		JSONObject json = new JSONObject();
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("car_url");
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		map.put("data_type", "40");
		map.put("device", device);
		map.put("data_id", storyId.toString());
		map.put("ip", ip);
		
		
		int count = 20;
		map.put("page_count",String.valueOf(20));
		map.put("sub_page_count",String.valueOf(2));
		String comment_params = "";
		try {
			comment_params = carPublicParam(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("*****comment_result****"+comment_params);
		String comment_result = HttpUtil.sendGetStr(url+"/comment/get-list", comment_params);
		log.info("*****comment_result****"+comment_result);
		JSONObject commentJson = null;
//		JSONArray commentArr = null;
		List<JSONObject> commentListJson = new ArrayList<JSONObject>();
		if(!Strings.isNullOrEmpty(comment_result)){
			JSONObject resJson = JSONObject.fromObject(comment_result);
			int code_res = resJson.getInt("code");
			log.info("*****comments****"+resJson);
			if(code_res == 10000){
				Object obj = resJson.get("data");
				if(!obj.toString().equals("[]")){
					commentJson = JSONObject.fromObject(obj);
					JSONArray arr = commentJson.getJSONArray("comments");
					log.info("*****comments****"+arr);
					if(arr != null && arr.size() > 0){
						for(Object c:arr){
							JSONObject commentJ = JSONObject.fromObject(c);
							Iterator<String> iter = commentJ.keys();
							JsonConfig configs = new JsonConfig();
							List<String> delArray = new ArrayList<String>();
							while(iter.hasNext()){
								String key = iter.next();
								String val = commentJ.getString(key);
								if(Strings.isNullOrEmpty(val) || val.equals("null")){
									log.info("*****key****"+key);
									if(key.equals("sub_comments")){
										delArray.add(key);
									}else{
										commentJ.put(key, " ");
									}
								}else{
									if(key.equals("comment_images")){
										boolean exist = false;
										JSONArray ja = JSONArray.fromObject(commentJ.get(key));
										if(ja != null && ja.size() > 0){
											for(Object o:ja){
												JSONObject ciJson = JSONObject.fromObject(o);
												if((ciJson.containsKey("name") && Strings.isNullOrEmpty(ciJson.getString("name")))){
													exist = true;
												}
												if(exist){
													delArray.add(key);
												}
											}
										}
										
									}else if(key.equals("sub_comments")){
										JSONArray sub_ja = commentJ.getJSONArray(key);
										List<JSONObject> jList = new ArrayList<JSONObject>();
										if(sub_ja != null && sub_ja.size() > 0){
											for(Object o:sub_ja){
												JSONObject sub_json = JSONObject.fromObject(o);
												if(sub_json.containsKey("comment_images") && 
														sub_json.getString("comment_images").equals("null")){
													sub_json.discard("comment_images");
													
												}
												jList.add(sub_json);
											}
											commentJ.put(key, jList);
										}
									}
								}
							}
							
							JSONObject cJson = null;
							if ((delArray != null) && (delArray.size() > 0)) {
								configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
								configs.setIgnoreDefaultExcludes(false);
								configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

								cJson = JSONObject.fromObject(commentJ, configs);
							} else {
								cJson = JSONObject.fromObject(commentJ);
							}
							Long created_at = Long.parseLong(cJson.getString("created_at"));
							cJson.remove("created_at");
							cJson.put("created_at", created_at);
							commentListJson.add(cJson);
						}
					}
					
				}
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
			}else if(code_res == 10012){
				json.put("status", "获取作者信息失败");
				json.put("code", 10840);
				json.put("error_message", "获取作者信息失败");
			}
		}else{
			json.put("status", "PHP Warning");
			json.put("code", 10626);
			json.put("error_message", "PHP Warning");
		}
		
		
		Map<String,String> storyJson = new HashMap<String,String>();
		storyJson.put("content_id", storyId.toString());
		if(loginUserid != null && loginUserid > 0){
			storyJson.put("user_id", loginUserid.toString());
		}
		
		storyJson.put("ip", ip);
		String params = "";
		try {
			params = carPublicParam(storyJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = HttpUtil.sendGetStr(url+"/content/get",params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				JSONObject data = resJson.getJSONObject("data");
				Iterator<String> iter = data.keys();
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				while(iter.hasNext()){
					String key = iter.next();
					String val = data.getString(key);
					if(Strings.isNullOrEmpty(val) || val.equals("null")){
						delArray.add(key);
					}
					
					
				}
				
				JSONObject cJson = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					cJson = JSONObject.fromObject(data, configs);
				} else {
					cJson = JSONObject.fromObject(data);
				}
				if(cJson.containsKey("collections")){
					JSONArray arr = cJson.getJSONArray("collections");
					List<JSONObject> collectionJsonList = new ArrayList<JSONObject>();
					JSONObject collectionJson = null;
					if(arr != null && arr.size() > 0){
						for(Object o:arr){
							collectionJson = JSONObject.fromObject(o);
							int id = collectionJson.getInt("id");
							Collection c = collectionDao.get(id);
							if(c != null){
								collectionJson.put("collection_name", c.getCollection_name());
								collectionJsonList.add(collectionJson);
							}
							
						}
					}
					cJson.remove("collections");
					cJson.put("collections", collectionJsonList);
				}
				if(commentListJson != null && commentListJson.size() > 0){
					cJson.put("comments", commentListJson);
				}
				
				return Response.status(Response.Status.CREATED).entity(cJson).build();
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14002){
				json.put("status", "content格式错误");
				json.put("code", 10811);
				json.put("error_message", "content格式错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14003){
				json.put("status", "内容类型错误");
				json.put("code", 10812);
				json.put("error_message", "内容类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14004){
				json.put("status", "内容元素类型错误");
				json.put("code", 10813);
				json.put("error_message", "内容元素类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14005){
				json.put("status", "关系对象类型错误");
				json.put("code", 10814);
				json.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14006){
				json.put("status", "保存内容失败");
				json.put("code", 10815);
				json.put("error_message", "保存内容失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14007){
				json.put("status", "保存内容元素失败");
				json.put("code", 10816);
				json.put("error_message", "保存内容元素失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14008){
				json.put("status", "保存内容地点关系失败");
				json.put("code", 10817);
				json.put("error_message", "保存内容地点关系失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14009){
				json.put("status", "保存内容车款关系失败");
				json.put("code", 10818);
				json.put("error_message", "保存内容车款关系失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14013){
				json.put("status", "保存数据索引失败");
				json.put("code", 10819);
				json.put("error_message", "保存数据索引失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14014){
				json.put("status", "保存数据操作日志失败");
				json.put("code", 10820);
				json.put("error_message", "保存数据操作日志失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14015){
				json.put("status", "地点不存在");
				json.put("code", 10821);
				json.put("error_message", "地点不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14016){
				json.put("status", "车款不存在");
				json.put("code", 10822);
				json.put("error_message", "车款不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else{
				json.put("status", "校验码不正确");
				json.put("code", 10626);
				json.put("error_message", "校验码不正确");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		
		

		}else{
			JSONObject jo = new JSONObject();
			jo.put("status", "invalid_request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "Invalid request payload");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
	}

	public List<Story> getAllByCollectionId(Long collectionId) {
		return null;
	}

	public Response updateStory(Long storyId, JSONObject storyModel, Long loginUserid,HttpServletRequest request) {
		log.debug("*** start update story ***");
		JSONObject json = new JSONObject();
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("car_url");
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> storyJson = new HashMap<String,String>();
		storyJson.put("from_channel", "30");
		storyJson.put("user_id", loginUserid.toString());
		storyJson.put("content", storyModel.toString());
		storyJson.put("ip", ip);
		storyJson.put("device", device);
		String params = "";
		try {
			params = carPublicParam(storyJson);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		String result = HttpUtil.sendPostStr(url+"/content/update", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				Map<String,String> map = new HashMap<String, String>();
				map.put("data_type", "40");
				map.put("device", device);
				map.put("data_id", storyId.toString());
				map.put("ip", ip);
				
				
				int count = 20;
				map.put("page_count",String.valueOf(20));
				String comment_params = "";
				try {
					comment_params = carPublicParam(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String comment_result = HttpUtil.sendGetStr(url+"/comment/get-list", comment_params);
				JSONObject commentJson = null;
//				JSONArray commentArr = null;
				List<JSONObject> commentListJson = new ArrayList<JSONObject>();
				if(!Strings.isNullOrEmpty(comment_result)){
					JSONObject resuJson = JSONObject.fromObject(comment_result);
					int code_resu = resJson.getInt("code");
					if(code_resu == 10000){
						Object data = resuJson.get("data");
						if(!data.toString().equals("[]")){
							commentJson = JSONObject.fromObject(data);
							JSONArray arr = commentJson.getJSONArray("comments");
							
							if(arr != null && arr.size() > 0){
								for(Object c:arr){
									JSONObject commentJ = JSONObject.fromObject(c);
									Iterator<String> iter = commentJ.keys();
									JsonConfig configs = new JsonConfig();
									List<String> delArray = new ArrayList<String>();
									while(iter.hasNext()){
										String key = iter.next();
										String val = commentJ.getString(key);
										if(Strings.isNullOrEmpty(val) || val.equals("null")){
											delArray.add(key);
										}
									}
									
									JSONObject cJson = null;
									if ((delArray != null) && (delArray.size() > 0)) {
										configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
										configs.setIgnoreDefaultExcludes(false);
										configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

										cJson = JSONObject.fromObject(commentJ, configs);
									} else {
										cJson = JSONObject.fromObject(commentJ);
									}
									commentListJson.add(cJson);
								}
							}
							
						}
					}else if(code_res == 10001){
						json.put("status", "缺少参数");
						json.put("code", 10810);
						json.put("error_message", "缺少参数");
					}else if(code_res == 10012){
						json.put("status", "获取作者信息失败");
						json.put("code", 10840);
						json.put("error_message", "获取作者信息失败");
					}
				}else{
					json.put("status", "PHP Warning");
					json.put("code", 10626);
					json.put("error_message", "PHP Warning");
				}
				
				Map<String,String> storyMap = new HashMap<String,String>();
				storyMap.put("from_channel", "30");
				storyMap.put("user_id", loginUserid.toString());
				storyMap.put("content_id", storyId.toString());
				storyMap.put("ip", ip);
				storyMap.put("device", device);
				String storyParams = "";
				try {
					storyParams = carPublicParam(storyMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String resultStory = HttpUtil.sendGetStr(url+"/content/get", storyParams);
				if(!Strings.isNullOrEmpty(resultStory)){
					JSONObject res_json = JSONObject.fromObject(resultStory);
					int code_resu = res_json.getInt("code");
					if(code_resu == 10000){
						JSONObject data = res_json.getJSONObject("data");
						Iterator<String> iter = data.keys();
						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						while(iter.hasNext()){
							String key = iter.next();
							String val = data.getString(key);
							if(Strings.isNullOrEmpty(val) || val.equals("null")){
								delArray.add(key);
							}
						}
						
						JSONObject cJson = null;
						if ((delArray != null) && (delArray.size() > 0)) {
							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
							configs.setIgnoreDefaultExcludes(false);
							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							cJson = JSONObject.fromObject(data, configs);
						} else {
							cJson = JSONObject.fromObject(data);
						}
						if(cJson.containsKey("collections")){
							JSONArray arr = cJson.getJSONArray("collections");
							List<JSONObject> collectionJsonList = new ArrayList<JSONObject>();
							JSONObject collectionJson = null;
							if(arr != null && arr.size() > 0){
								for(Object o:arr){
									collectionJson = JSONObject.fromObject(o);
									int id = collectionJson.getInt("id");
									Collection c = collectionDao.get(id);
									collectionJson.put("collection_name", c.getCollection_name());
									collectionJsonList.add(collectionJson);
								}
							}
							cJson.remove("collections");
							cJson.put("collections", collectionJsonList);
						}
						if(commentListJson != null && commentListJson.size() > 0){
							cJson.put("comments", commentListJson);
						}
						
						return Response.status(Response.Status.OK).entity(cJson).build();
					}else if(code_res == 10001){
						json.put("status", "缺少参数");
						json.put("code", 10810);
						json.put("error_message", "缺少参数");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14002){
						json.put("status", "content格式错误");
						json.put("code", 10811);
						json.put("error_message", "content格式错误");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14003){
						json.put("status", "内容类型错误");
						json.put("code", 10812);
						json.put("error_message", "内容类型错误");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14004){
						json.put("status", "内容元素类型错误");
						json.put("code", 10813);
						json.put("error_message", "内容元素类型错误");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14005){
						json.put("status", "关系对象类型错误");
						json.put("code", 10814);
						json.put("error_message", "关系对象类型错误");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14006){
						json.put("status", "保存内容失败");
						json.put("code", 10815);
						json.put("error_message", "保存内容失败");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14007){
						json.put("status", "保存内容元素失败");
						json.put("code", 10816);
						json.put("error_message", "保存内容元素失败");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14008){
						json.put("status", "保存内容地点关系失败");
						json.put("code", 10817);
						json.put("error_message", "保存内容地点关系失败");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14009){
						json.put("status", "保存内容车款关系失败");
						json.put("code", 10818);
						json.put("error_message", "保存内容车款关系失败");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14013){
						json.put("status", "保存数据索引失败");
						json.put("code", 10819);
						json.put("error_message", "保存数据索引失败");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14014){
						json.put("status", "保存数据操作日志失败");
						json.put("code", 10820);
						json.put("error_message", "保存数据操作日志失败");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14015){
						json.put("status", "地点不存在");
						json.put("code", 10821);
						json.put("error_message", "地点不存在");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else if(code_res == 14016){
						json.put("status", "车款不存在");
						json.put("code", 10822);
						json.put("error_message", "车款不存在");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}else{
						json.put("status", "校验码不正确");
						json.put("code", 10626);
						json.put("error_message", "校验码不正确");
						return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
					}
				
				

				}else{
					JSONObject jo = new JSONObject();
					jo.put("status", "invalid_request");
					jo.put("code", Integer.valueOf(10010));
					jo.put("error_message", "Invalid request payload");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14002){
				json.put("status", "content格式错误");
				json.put("code", 10811);
				json.put("error_message", "content格式错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14003){
				json.put("status", "内容类型错误");
				json.put("code", 10812);
				json.put("error_message", "内容类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14004){
				json.put("status", "内容元素类型错误");
				json.put("code", 10813);
				json.put("error_message", "内容元素类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14005){
				json.put("status", "关系对象类型错误");
				json.put("code", 10814);
				json.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14006){
				json.put("status", "保存内容失败");
				json.put("code", 10815);
				json.put("error_message", "保存内容失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14007){
				json.put("status", "保存内容元素失败");
				json.put("code", 10816);
				json.put("error_message", "保存内容元素失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14008){
				json.put("status", "保存内容地点关系失败");
				json.put("code", 10817);
				json.put("error_message", "保存内容地点关系失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14009){
				json.put("status", "保存内容车款关系失败");
				json.put("code", 10818);
				json.put("error_message", "保存内容车款关系失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14013){
				json.put("status", "保存数据索引失败");
				json.put("code", 10819);
				json.put("error_message", "保存数据索引失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14014){
				json.put("status", "保存数据操作日志失败");
				json.put("code", 10820);
				json.put("error_message", "保存数据操作日志失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14015){
				json.put("status", "地点不存在");
				json.put("code", 10821);
				json.put("error_message", "地点不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14016){
				json.put("status", "车款不存在");
				json.put("code", 10822);
				json.put("error_message", "车款不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else{
				json.put("status", "校验码不正确");
				json.put("code", 10626);
				json.put("error_message", "校验码不正确");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		
		

		}else{
			JSONObject jo = new JSONObject();
			jo.put("status", "invalid_request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "Invalid request payload");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
	}

	public Response deleteStory(Long storyId, Long loginUserid,HttpServletRequest request) {
		JSONObject jo = new JSONObject();
		JSONObject json = new JSONObject();
		String ip = request.getHeader("X-Real-IP");
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("car_url");
		Map<String,String> storyJson = new HashMap<String,String>();
		storyJson.put("from_channel", "40");
		storyJson.put("user_id", loginUserid.toString());
		storyJson.put("content_id", storyId.toString());
		storyJson.put("ip", ip);
		storyJson.put("device", device);
		String params = "";
		try {
			params = carPublicParam(storyJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = HttpUtil.sendPostStr(url+"/content/delete", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				json.put("status", "success");
				return Response.status(Response.Status.OK).entity(json).build();
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14020){
				json.put("status", "删除内容失败");
				json.put("code", 10860);
				json.put("error_message", "删除内容失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		}else{
			json.put("status", "invalid_request");
			json.put("code", Integer.valueOf(10010));
			json.put("error_message", "Invalid request payload");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
		
		
		
		return Response.status(Response.Status.OK).entity(jo).build();
	}

	public Response createComment(JSONObject commentModel, Long storyId, Long loginUserid,HttpServletRequest request) {
		log.debug("*** create comment start ***" + JSONObject.fromObject(commentModel));
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		JSONObject json = new JSONObject();
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		map.put("from_channel", "30");
		map.put("user_id", loginUserid.toString());
		map.put("data_type", "40");
		map.put("device", device);
		map.put("data_id", storyId.toString());
		map.put("ip", ip);
		map.put("comment_content", commentModel.getString("content"));
		if(commentModel.containsKey("comment_image")){
			map.put("comment_images", commentModel.getString("comment_image"));
		}
		
		String params = "";
		try {
			params = carPublicParam(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = HttpUtil.sendPostStr(car_url+"/comment/create", params);
		
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				JSONObject data = resJson.getJSONObject("data");
				Iterator<String> iter = data.keys();
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				while(iter.hasNext()){
					String key = iter.next();
					String val = data.getString(key);
					if(Strings.isNullOrEmpty(val) || val.equals("null")){
						delArray.add(key);
					}
				}
				
				JSONObject cJson = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					cJson = JSONObject.fromObject(data, configs);
				} else {
					cJson = JSONObject.fromObject(data);
				}
				json.put("status", "success");
				json.put("comment", cJson);
				return Response.status(Response.Status.CREATED).entity(json).build();
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14022){
				json.put("status", "对象类型错误");
				json.put("code", 10821);
				json.put("error_message", "对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 140023){
				json.put("status", "对象不存在");
				json.put("code", 10822);
				json.put("error_message", "对象不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14025){
				json.put("status", "评论失败");
				json.put("code", 10823);
				json.put("error_message", "评论失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		}else{
			json.put("status", "invalid_request");
			json.put("code", Integer.valueOf(10010));
			json.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		return null;
		
//		String path1 = getClass().getResource("/../../META-INF/getui.json").getPath();
//		JSONObject jsonObject = ParseFile.parseJson(path1);
//		String appId = jsonObject.getString("appId");
//		String appKey = jsonObject.getString("appKey");
//		String masterSecret = jsonObject.getString("masterSecret");
//		User user = (User) this.userDao.get(loginUserid);
//		Story story = (Story) this.storyDao.get(storyId);
//		if (!Strings.isNullOrEmpty(commentModel.getString("content"))) {
//			if (!story.getStatus().equals("removed")) {
//				Comment comment = new Comment();
//
//				comment.setContent(commentModel.getString("content"));
//				comment.setStory(story);
//				comment.setUser(user);
//				comment.setStatus("enabled");
//				if(commentModel.containsKey("comment_image")){
//					comment.setComment_image(commentModel.getString("comment_image"));
//				}
//				
//				this.commentDao.save(comment);
//				story.setLast_comment_date(new Date());
//				storyDao.update(story);
//				if (!loginUserid.equals(story.getUser().getId())) {
//					Notification notification = new Notification();
//					notification.setSenderId(loginUserid);
//					notification.setRecipientId((Long) story.getUser().getId());
//					notification.setNotificationType(5);
//					notification.setObjectType(2);
//					notification.setObjectId((Long) comment.getId());
//					notification.setRead_already(false);
//					notification.setStatus("enabled");
//					this.notificationDao.save(notification);
//					Configuration conf = this.configurationDao.getConfByUserId((Long) story.getUser().getId());
//					if (conf.isNew_comment_on_your_story_push()) {
//						int count = this.notificationDao.getNotificationByRecipientId((Long) story.getUser().getId());
//						List<PushNotification> list = this.pushNotificationDao
//								.getPushNotificationByUserid((Long) story.getUser().getId());
//						try {
//							String content = user.getUsername() + "�������ҵĹ���";
//							JSONObject json = new JSONObject();
//							json.put("story_id", story.getId());
//							json.put("comment_id", comment.getId());
//							PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, count, content,
//									json.toString());
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
//
//				CommentModel commentModels = new CommentModel();
//				if (comment != null) {
//					commentModels = getCommentModel(comment);
//				}
//				log.debug("*** comment information ***" + JSONObject.fromObject(commentModels));
//				return Response.status(Response.Status.CREATED).entity(commentModels).build();
//			}
//			JSONObject json = new JSONObject();
//			json.put("status", "invalid_story");
//			json.put("code", Integer.valueOf(10051));
//			json.put("error_message", "The story has been deleted, can't comment");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//
//		JSONObject json = new JSONObject();
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}

	public Response createReplyComment(JSONObject replyComment, Long storyId, Long commentId, Long loginUserid,HttpServletRequest request) {
		

		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		JSONObject json = new JSONObject();
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		map.put("from_channel", "30");
		map.put("user_id", loginUserid.toString());
		map.put("data_type", "40");
		map.put("device", device);
		map.put("data_id", storyId.toString());
		map.put("ip", ip);
		map.put("parent_id", commentId.toString());
		map.put("comment_content", replyComment.getString("content"));
		if(replyComment.containsKey("comment_image")){
			map.put("comment_images", replyComment.getString("comment_image"));
		}
		
		String params = "";
		try {
			params = carPublicParam(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = HttpUtil.sendPostStr(car_url+"/comment/create", params);
		
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				JSONObject data = resJson.getJSONObject("data");
				Iterator<String> iter = data.keys();
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				while(iter.hasNext()){
					String key = iter.next();
					String val = data.getString(key);
					if(Strings.isNullOrEmpty(val) || val.equals("null")){
						delArray.add(key);
					}
				}
				
				JSONObject cJson = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					cJson = JSONObject.fromObject(data, configs);
				} else {
					cJson = JSONObject.fromObject(data);
				}
				cJson.put("top_id", replyComment.getString("top_id"));
				cJson.put("parent_id", replyComment.getString("parent_id"));
				cJson.put("parent_user_id", replyComment.getString("parent_user_id"));
				cJson.put("parent_user_name", replyComment.getString("parent_user_name"));
				cJson.put("parent_user_avatar", replyComment.getString("parent_user_avatar"));
				cJson.put("id", cJson.getString("comment_id"));
				cJson.put("creator_id", loginUserid);
				cJson.put("creator_name", cJson.getString("username"));
				cJson.put("creator_avatar", cJson.getString("avatar"));
				cJson.put("created_at", cJson.getString("comment_time"));
				json.put("status", "success");
				json.put("comment",cJson);
				return Response.status(Response.Status.CREATED).entity(json).build();
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14022){
				json.put("status", "对象类型错误");
				json.put("code", 10821);
				json.put("error_message", "对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 140023){
				json.put("status", "对象不存在");
				json.put("code", 10822);
				json.put("error_message", "对象不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14025){
				json.put("status", "评论失败");
				json.put("code", 10823);
				json.put("error_message", "评论失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14029){
				json.put("status", "该对象不允许评论");
				json.put("code", 10824);
				json.put("error_message", "该对象不允许评论");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		}else{
			json.put("status", "invalid_request");
			json.put("code", Integer.valueOf(10010));
			json.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		return null;
		
//		String path1 = getClass().getResource("/../../META-INF/getui.json").getPath();
//		JSONObject jsonObject = ParseFile.parseJson(path1);
//		String appId = jsonObject.getString("appId");
//		String appKey = jsonObject.getString("appKey");
//		String masterSecret = jsonObject.getString("masterSecret");
//		User user = (User) this.userDao.get(loginUserid);
//		Story story = (Story) this.storyDao.get(storyId);
//		if (!Strings.isNullOrEmpty(commentModel.getString("content"))) {
//			if (!story.getStatus().equals("removed")) {
//				Comment comment = new Comment();
//
//				comment.setContent(commentModel.getString("content"));
//				comment.setStory(story);
//				comment.setUser(user);
//				comment.setStatus("enabled");
//				if(commentModel.containsKey("comment_image")){
//					comment.setComment_image(commentModel.getString("comment_image"));
//				}
//				
//				this.commentDao.save(comment);
//				story.setLast_comment_date(new Date());
//				storyDao.update(story);
//				if (!loginUserid.equals(story.getUser().getId())) {
//					Notification notification = new Notification();
//					notification.setSenderId(loginUserid);
//					notification.setRecipientId((Long) story.getUser().getId());
//					notification.setNotificationType(5);
//					notification.setObjectType(2);
//					notification.setObjectId((Long) comment.getId());
//					notification.setRead_already(false);
//					notification.setStatus("enabled");
//					this.notificationDao.save(notification);
//					Configuration conf = this.configurationDao.getConfByUserId((Long) story.getUser().getId());
//					if (conf.isNew_comment_on_your_story_push()) {
//						int count = this.notificationDao.getNotificationByRecipientId((Long) story.getUser().getId());
//						List<PushNotification> list = this.pushNotificationDao
//								.getPushNotificationByUserid((Long) story.getUser().getId());
//						try {
//							String content = user.getUsername() + "�������ҵĹ���";
//							JSONObject json = new JSONObject();
//							json.put("story_id", story.getId());
//							json.put("comment_id", comment.getId());
//							PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, count, content,
//									json.toString());
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
//
//				CommentModel commentModels = new CommentModel();
//				if (comment != null) {
//					commentModels = getCommentModel(comment);
//				}
//				log.debug("*** comment information ***" + JSONObject.fromObject(commentModels));
//				return Response.status(Response.Status.CREATED).entity(commentModels).build();
//			}
//			JSONObject json = new JSONObject();
//			json.put("status", "invalid_story");
//			json.put("code", Integer.valueOf(10051));
//			json.put("error_message", "The story has been deleted, can't comment");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//
//		JSONObject json = new JSONObject();
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	
		
		
//		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
//		JSONObject jsonObject = ParseFile.parseJson(path);
//		String appId = jsonObject.getString("appId");
//		String appKey = jsonObject.getString("appKey");
//		String masterSecret = jsonObject.getString("masterSecret");
//		Story story = (Story) this.storyDao.get(storyId);
//		User user = (User) this.userDao.get(loginUserid);
//		if (!Strings.isNullOrEmpty(replyComment.getString("content"))) {
//			Comment comment = new Comment();
//
//			Comment com = this.commentDao.get(commentId);
//
//			if (!story.getStatus().equals("removed")) {
//				if (!com.getStatus().equals("disabled")) {
//					if (replyComment.containsKey("target_user_id") ) {
//						comment.setContent(replyComment.getString("content"));
//						comment.setStory(story);
//						comment.setUser(user);
//						comment.setTarget_user_id(replyComment.getLong("target_user_id"));
//						comment.setStatus("enabled");
//						comment.setTarget_comment_id(commentId);
//						if(replyComment.containsKey("comment_image")){
//							comment.setComment_image(replyComment.getString("comment_image"));
//						}
//						
//					} else {
//						comment.setContent(replyComment.getString("content"));
//						comment.setStory(story);
//						comment.setUser(user);
//						comment.setStatus("enabled");
//						comment.setTarget_comment_id(commentId);
//						if(replyComment.containsKey("comment_image")){
//							comment.setComment_image(replyComment.getString("comment_image"));
//						}
//						
//					}
//
//					this.commentDao.save(comment);
//					story.setLast_comment_date(new Date());
//					storyDao.update(story);
//					Notification notification = new Notification();
//					notification.setSenderId(loginUserid);
//					if (comment.getTarget_user_id() != null) {
//						notification.setRecipientId(comment.getTarget_user_id());
//					} else {
//						notification.setRecipientId(com.getUser().getId());
//					}
//
//					notification.setNotificationType(6);
//					notification.setObjectType(2);
//					notification.setObjectId((Long) comment.getId());
//					notification.setRead_already(false);
//					notification.setStatus("enabled");
//					this.notificationDao.save(notification);
//					Configuration conf = this.configurationDao.getConfByUserId(com.getUser().getId());
//					if (conf.isNew_comment_on_your_comment_push()) {
//						int count = 0;
//						List<PushNotification> list = null;
//						if (comment.getTarget_user_id() != null) {
//							count = this.notificationDao.getNotificationByRecipientId(comment.getTarget_user_id());
//							list = this.pushNotificationDao.getPushNotificationByUserid(comment.getTarget_user_id());
//						} else {
//							count = this.notificationDao.getNotificationByRecipientId(com.getUser().getId());
//							list = this.pushNotificationDao.getPushNotificationByUserid(com.getUser().getId());
//						}
//						try {
//							String content = user.getUsername() + "�ظ�����";
//							JSONObject json = new JSONObject();
//							json.put("story_id", story.getId());
//							json.put("comment_id", comment.getId());
//							PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, count, content,
//									json.toString());
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//
//					ReplyCommentModel commentModels = new ReplyCommentModel();
//					if (comment != null) {
//						commentModels = getReplyCommentModel(comment);
//					}
//					log.debug("*** comment information ***" + JSONObject.fromObject(commentModels));
//					return Response.status(Response.Status.CREATED).entity(commentModels).build();
//				}
//				JSONObject json = new JSONObject();
//				json.put("status", "invalid_comment");
//				json.put("code", Integer.valueOf(10052));
//				json.put("error_message", "Comment has been deleted, can't reply");
//				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//			}
//
//			JSONObject json = new JSONObject();
//			json.put("status", "invalid_story");
//			json.put("code", Integer.valueOf(10050));
//			json.put("error_message", "The story has been deleted, can't comment");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//
//		JSONObject json = new JSONObject();
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}
	
	@Override
	public Response createChatComment(JSONObject replyComment, Long storyId, Long commentId, Long userId,
			Long loginUserid, HttpServletRequest request) {

		

		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		JSONObject json = new JSONObject();
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		map.put("from_channel", "30");
		map.put("user_id", loginUserid.toString());
		map.put("data_type", "10");
		map.put("device", device);
		map.put("data_id", userId.toString());
		map.put("ip", ip);
		map.put("parent_id", commentId.toString());
		map.put("comment_content", replyComment.getString("content"));
		if(replyComment.containsKey("comment_image")){
			map.put("comment_images", replyComment.getString("comment_image"));
		}
		
		String params = "";
		try {
			params = carPublicParam(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = HttpUtil.sendPostStr(car_url+"/comment/create", params);
		
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				JSONObject data = resJson.getJSONObject("data");
				Iterator<String> iter = data.keys();
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				while(iter.hasNext()){
					String key = iter.next();
					String val = data.getString(key);
					if(Strings.isNullOrEmpty(val) || val.equals("null")){
						delArray.add(key);
					}
				}
				
				JSONObject cJson = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					cJson = JSONObject.fromObject(data, configs);
				} else {
					cJson = JSONObject.fromObject(data);
				}
				
				json.put("status", "success");
				json.put("comment",cJson);
				return Response.status(Response.Status.CREATED).entity(json).build();
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14022){
				json.put("status", "对象类型错误");
				json.put("code", 10821);
				json.put("error_message", "对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 140023){
				json.put("status", "对象不存在");
				json.put("code", 10822);
				json.put("error_message", "对象不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14025){
				json.put("status", "评论失败");
				json.put("code", 10823);
				json.put("error_message", "评论失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14029){
				json.put("status", "该对象不允许评论");
				json.put("code", 10824);
				json.put("error_message", "该对象不允许评论");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		}else{
			json.put("status", "invalid_request");
			json.put("code", Integer.valueOf(10010));
			json.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		return null;
		
	
	}

	public CommentModel getComment(Long commentId) {
		log.debug("**** get comment ****");
		Comment comment = (Comment) this.commentDao.get(commentId);
		CommentModel commentModel = new CommentModel();
		if (comment != null) {
			commentModel = getCommentModel(comment);
		}
		log.debug("**** get comment ****" + JSONObject.fromObject(commentModel));
		return commentModel;
	}

	public Response getComments(Long storyId, HttpServletRequest request) {
		log.info("**** get comments ****" + storyId);
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		map.put("data_type", "40");
		map.put("device", device);
		map.put("data_id", storyId.toString());
		map.put("ip", ip);
		
		
		List<CommentModel> commentModelList = new ArrayList<CommentModel>();
		int count = 20;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
			map.put("page_count", String.valueOf(count));
		} else {
			if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				map.put("page_count", String.valueOf(count));
			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				map.put("page_count", String.valueOf(count));
				map.put("prev_id", String.valueOf(max_id));
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				map.put("page_count", String.valueOf(count));
				map.put("prev_id", String.valueOf(max_id));
			}
		}
		
		String params = "";
		try {
			params = carPublicParam(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = HttpUtil.sendGetStr(car_url+"/comment/get-list", params);
		log.info("****comment-result ****" + result);
		JSONObject json = new JSONObject();
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				Object obje = resJson.get("data");
				JSONArray commentArr = null;
				if(obje.toString().equals("[]")){
					JSONObject nullJson = new JSONObject();
					return Response.status(Response.Status.CREATED).entity(nullJson).build();
				}else{
					JSONObject o = JSONObject.fromObject(obje);
					JSONArray arr = o.getJSONArray("comments");
					List<JSONObject> commentListJson = new ArrayList<JSONObject>();
					if(arr != null && arr.size() > 0){
						for(Object obj:arr){
							JSONObject commentJ = JSONObject.fromObject(obj);
							Iterator<String> iter = commentJ.keys();
							JsonConfig configs = new JsonConfig();
							List<String> delArray = new ArrayList<String>();
							while(iter.hasNext()){
								String key = iter.next();
								String val = commentJ.getString(key);
								if(Strings.isNullOrEmpty(val) || val.equals("null")){
									delArray.add(key);
								}
							}
							
							JSONObject cJson = null;
							if ((delArray != null) && (delArray.size() > 0)) {
								configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
								configs.setIgnoreDefaultExcludes(false);
								configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

								cJson = JSONObject.fromObject(commentJ, configs);
							} else {
								cJson = JSONObject.fromObject(commentJ);
							}
							Long created_at = Long.parseLong(cJson.getString("created_at"));
							cJson.remove("created_at");
							cJson.put("created_at", created_at);
							commentListJson.add(cJson);
						}
					}
					o.remove("comments");
					o.put("comments", commentListJson);
					
					return Response.status(Response.Status.CREATED).entity(commentListJson).build();
				}
				
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 10012){
				json.put("status", "获取作者信息失败");
				json.put("code", 10840);
				json.put("error_message", "获取作者信息失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		}else{
			json.put("status", "PHP Warning");
			json.put("code", 10626);
			json.put("error_message", "PHP Warning");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		log.debug("***** get comment list *****" + JSONArray.fromObject(commentModelList));
		return null;
	}

	public Response getRepliesComments(Long storyId, Long commentId, HttpServletRequest request) {
		
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		map.put("data_type", "40");
		map.put("device", device);
		map.put("data_id", storyId.toString());
		map.put("ip", ip);
		map.put("top_id", commentId.toString());
		
		
		List<CommentModel> commentModelList = new ArrayList<CommentModel>();
		int count = 2;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
			map.put("sub_page_count", String.valueOf(count));
		} else {
			if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				map.put("sub_page_count", String.valueOf(count));
			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				map.put("sub_page_count", String.valueOf(count));
				map.put("prev_id", String.valueOf(max_id));
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				map.put("sub_page_count", String.valueOf(count));
				map.put("prev_id", String.valueOf(max_id));
			}
		}
		
		String params = "";
		try {
			params = carPublicParam(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = HttpUtil.sendGetStr(car_url+"/comment/get-list", params);
		JSONObject json = new JSONObject();
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			log.info("reply comment******"+resJson);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				Object dataObj = resJson.get("data");
				if(dataObj.toString().length() > 2){
					JSONObject o = resJson.getJSONObject("data");
					JSONArray arr = o.getJSONArray("comments");
					List<JSONObject> commentListJson = new ArrayList<JSONObject>();
					if(arr != null && arr.size() > 0){
						for(Object obj:arr){
							JSONObject commentJ = JSONObject.fromObject(obj);
							Iterator<String> iter = commentJ.keys();
							JsonConfig configs = new JsonConfig();
							List<String> delArray = new ArrayList<String>();
							while(iter.hasNext()){
								String key = iter.next();
								String val = commentJ.getString(key);
								if(Strings.isNullOrEmpty(val) || val.equals("null")){
									delArray.add(key);
								}
							}
							
							JSONObject cJson = null;
							if ((delArray != null) && (delArray.size() > 0)) {
								configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
								configs.setIgnoreDefaultExcludes(false);
								configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

								cJson = JSONObject.fromObject(commentJ, configs);
							} else {
								cJson = JSONObject.fromObject(commentJ);
							}
							commentListJson.add(cJson);
						}
					}
					o.remove("comments");
					o.put("comments", commentListJson);
					
					return Response.status(Response.Status.CREATED).entity(o).build();
				}else{
					return Response.status(Response.Status.CREATED).entity(json).build();
				}
				
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 10012){
				json.put("status", "获取作者信息失败");
				json.put("code", 10840);
				json.put("error_message", "获取作者信息失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		}else{
			json.put("status", "PHP Warning");
			json.put("code", 10626);
			json.put("error_message", "PHP Warning");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		log.debug("***** get comment list *****" + JSONArray.fromObject(commentModelList));
		return null;
	
		
		
//		
//		log.debug("**** get reply comments ****" + storyId);
//		String countStr = request.getParameter("count");
//		String maxIdStr = request.getParameter("max_id");
//		List<ReplyCommentModel> commentModelList = new ArrayList<ReplyCommentModel>();
//		int count = 20;
//		ReplyCommentModel commentModel = null;
//		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
//			List<Comment> commentList = this.commentDao.getReplyComments(commentId, count);
//			if ((commentList != null) && (commentList.size() > 0))
//				for (Comment comment : commentList) {
//					commentModel = getReplyCommentModel(comment);
//					commentModelList.add(commentModel);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			List<Comment> commentList = this.commentDao.getReplyComments(commentId, count);
//			if ((commentList != null) && (commentList.size() > 0))
//				for (Comment comment : commentList) {
//					commentModel = getReplyCommentModel(comment);
//					commentModelList.add(commentModel);
//				}
//		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			List<Comment> commentList = this.commentDao.getReplyComments(commentId, max_id, count);
//			if ((commentList != null) && (commentList.size() > 0))
//				for (Comment comment : commentList) {
//					commentModel = getReplyCommentModel(comment);
//					commentModelList.add(commentModel);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			List<Comment> commentList = this.commentDao.getReplyComments(commentId, max_id, count);
//			if ((commentList != null) && (commentList.size() > 0)) {
//				for (Comment comment : commentList) {
//					commentModel = getReplyCommentModel(comment);
//					commentModelList.add(commentModel);
//				}
//			}
//		}
//		log.debug("***** get reply comment list *****" + JSONArray.fromObject(commentModelList));
//		return commentModelList;
	}

	public Response deleteComment(Long storyId, Long commentId, Long loginUserid,HttpServletRequest request) {
		JSONObject json = new JSONObject();
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		map.put("user_id", loginUserid.toString());
		map.put("device", device);
		map.put("data_id", commentId.toString());
		map.put("ip",ip);
		String params = "";
		try {
			params = carPublicParam(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = HttpUtil.sendGetStr(car_url+"/comment/delete", params);
		
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				json.put("status","success");
				return Response.status(Response.Status.CREATED).entity(json).build();
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14026){
				json.put("status", "评论不存在");
				json.put("code", 10831);
				json.put("error_message", "评论不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14028){
				json.put("status", "删除评论失败");
				json.put("code", 10832);
				json.put("error_message", "删除评论失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		}else{
			json.put("status", "invalid_request");
			json.put("code", Integer.valueOf(10010));
			json.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		return null;
//		Comment comment = this.commentDao.getCommentByIdAndLoginUserid(commentId, loginUserid);
//		int count = 0;
//		if (comment != null) {
//			if ((comment.getTarget_comment_id() == null) && (comment.getTarget_user_id() == null)) {
//				List<Comment> commentList = this.commentDao.getReplyCommentsById((Long) comment.getId());
//				if ((commentList != null) && (commentList.size() > 0)) {
//					for (Comment c : commentList) {
//						c.setStatus("disabled");
//						this.commentDao.update(c);
//					}
//				}
//			}
//
//			comment.setStatus("disabled");
//			this.commentDao.deleteComment(comment);
//			count = this.commentDao.getCommentCountById(storyId);
//			notificationDao.disableNotification(2, commentId);
//		} else {
//			JSONObject jo = new JSONObject();
//			jo.put("status", "invalid_request");
//			jo.put("code", Integer.valueOf(10010));
//			jo.put("error_message", "Invalid payload parameters");
//			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
//		}
//		JSONObject json = new JSONObject();
//		json.put("comment_count", Integer.valueOf(count));
//		return Response.status(Response.Status.OK).entity(json).build();
	}

	public Response createLikes(Long storyId, Long loginUserid,HttpServletRequest request) {
		log.debug("*** create likes ***");
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		JSONObject json = new JSONObject();
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("car_url");
		Map<String,String> storyJson = new HashMap<String,String>();
		storyJson.put("user_id", loginUserid.toString());
		storyJson.put("content_id", storyId.toString());
		storyJson.put("device", device);
		storyJson.put("ip",ip);
		String params = "";
		try {
			params = carPublicParam(storyJson);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = HttpUtil.sendPostStr(url+"/content/like", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				JSONObject data = resJson.getJSONObject("data");
				return Response.status(Response.Status.CREATED).entity(data).build();
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14010){
				json.put("status", "内容不存在");
				json.put("code", 10850);
				json.put("error_message", "内容不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code_res == 14021){
				json.put("status", "操作失败");
				json.put("code", 10851);
				json.put("error_message", "操作失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		}else{
			json.put("status", "缺少参数");
			json.put("code", 10810);
			json.put("error_message", "缺少参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
			
	

	
		return null;
//		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
//		JSONObject jsonObject = ParseFile.parseJson(path);
//		String appId = jsonObject.getString("appId");
//		String appKey = jsonObject.getString("appKey");
//		String masterSecret = jsonObject.getString("masterSecret");
//		User user = (User) this.userDao.get(loginUserid);
//		Story story = (Story) this.storyDao.get(storyId);
//		Likes likes = new Likes();
//		likes.setCreateTime(new Date());
//		likes.setLike_users(user);
//		likes.setLike_story(story);
//		this.likesDao.save(likes);
//		Notification notification = this.notificationDao.getNotificationByAction(storyId, loginUserid, 1, 2);
//		if (notification != null) {
//			notification.setCreate_at(new Date());
//			this.notificationDao.update(notification);
//		} else {
//			notification = new Notification();
//			notification.setSenderId(loginUserid);
//			notification.setRecipientId((Long) story.getUser().getId());
//			notification.setNotificationType(2);
//			notification.setObjectType(1);
//			notification.setObjectId(storyId);
//			notification.setRead_already(false);
//			notification.setStatus("enabled");
//			this.notificationDao.save(notification);
//		}
//		Configuration conf = this.configurationDao.getConfByUserId((Long) story.getUser().getId());
//		if(conf != null){
//			if (conf.isNew_favorite_from_following_push()) {
//				int counts = this.notificationDao.getNotificationByRecipientId((Long) story.getUser().getId());
//				List<PushNotification> list = this.pushNotificationDao
//						.getPushNotificationByUserid((Long) story.getUser().getId());
//				try {
//					String content = user.getUsername() + "ϲ�����ҵĹ���";
////					JSONObject json = new JSONObject();
//					json.put("story_id", story.getId());
//					PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content, json.toString());
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		int count = this.likesDao.likeStoryCount(storyId);
//		JSONObject jo = new JSONObject();
//		jo.put("like_count", Integer.valueOf(count));
//		
//		List<FMap> fmapList = fmapDao.getFMapList(storyId);
//		if(fmapList != null && fmapList.size() > 0){
//			UserCentre uc = userCentreDao.getUserCentreByUserId(loginUserid);
//			int centre_id = 0;
//			if(uc != null){
//				centre_id = uc.getCentre_id();
//			}else{
//				LinkAccounts la = linkAccountsDao.getLinkAccountsByUseridAndService(loginUserid, "fblife");
//				centre_id = Integer.parseInt(la.getUuid());
//			}
//			if(centre_id > 0){
//				Map<String,String> map = new HashMap<String, String>();
//				map.put("user_id", String.valueOf(centre_id));
//				map.put("story_id", String.valueOf(storyId));
//				map.put("recognition_type", "10");
//				map.put("ip",ip);
//				String params = "";
//				try {
//					params = publicParam(map);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				HttpUtil.sendPostStr(url+"/v1/info/info-wiki/add-wiki-recognition", params);
//			}
//			
//		}
//		return Response.status(Response.Status.CREATED).entity(jo).build();
	}

	public Response deleteLike(Long storyId, Long loginUserid) {

		int count = 0;
		JSONObject jo = new JSONObject();
		jo.put("like_count", Integer.valueOf(count));
		return Response.status(Response.Status.OK).entity(jo).build();
	}

	public Response createRepost(Long storyId, Long loginUserid,HttpServletRequest request) {
		log.debug("*** repost story ***");
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		JSONObject resp = new JSONObject();
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("data_type", "40");
		map.put("data_id", String.valueOf(storyId));
		map.put("ip", ip);
		map.put("device", device);
		map.put("user_id", String.valueOf(loginUserid));
		map.put("collect_type", "1");
		
		String params = "";
		try {
			params = carPublicParam(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = HttpUtil.sendPostStr(car_url+"/member/collect-data", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){
				resp.put("status", "success");
				return Response.status(Response.Status.OK).entity(resp).build();
			}else if (code == 11001){
				resp.put("status", "用户不存在");
				resp.put("code", 10650);
				resp.put("error_message", "用户不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11006){
				resp.put("status", "不能重复操作");
				resp.put("code", 10550);
				resp.put("error_message", "不能重复操作");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11008){
				resp.put("status", "保存数据操作日志失败");
				resp.put("code", 10551);
				resp.put("error_message", "保存数据操作日志失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11018){
				resp.put("status", "对象类型错误");
				resp.put("code", 10552);
				resp.put("error_message", "对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11019){
				resp.put("status", "对象不存在");
				resp.put("code", 10553);
				resp.put("error_message", "对象不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11021){
				resp.put("status", "收藏类型错误");
				resp.put("code", 10554);
				resp.put("error_message", "收藏类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11022){
				resp.put("status", "收藏失败");
				resp.put("code", 10555);
				resp.put("error_message", "收藏失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
		
//		Republish report = republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
//		JSONObject json1 = new JSONObject();
//		if (report != null) {
//			json1.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json1).build();
//		}
//		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
//		JSONObject jsonObject = ParseFile.parseJson(path);
//		String appId = jsonObject.getString("appId");
//		String appKey = jsonObject.getString("appKey");
//		String masterSecret = jsonObject.getString("masterSecret");
//		int count = 0;
//		User user = (User) this.userDao.get(loginUserid);
//		Story story = (Story) this.storyDao.get(storyId);
//		Republish r = new Republish();
//		r.setCreateTime(new Date());
//		r.setRepost_users(user);
//		r.setRepost_story(story);
//		r.setType("repost");
//		this.republishDao.save(r);
//		Timeline timeline = new Timeline();
//		timeline.setCreatorId(loginUserid);
//		timeline.setTargetUserId(loginUserid);
//		timeline.setStory(story);
//		timeline.setType("repost");
//		timeline.setReferenceId(storyId);
//		timeline.setCreateTime(new Date());
//		this.timelineDao.save(timeline);
//		Notification notification = this.notificationDao.getNotificationByAction(storyId, loginUserid, 1, 3);
//		if (notification != null) {
//			notification.setCreate_at(new Date());
//			this.notificationDao.update(notification);
//		} else {
//			notification = new Notification();
//			notification.setSenderId(loginUserid);
//			notification.setRecipientId((Long) story.getUser().getId());
//			notification.setNotificationType(3);
//			notification.setObjectType(1);
//			notification.setObjectId(storyId);
//			notification.setRead_already(false);
//			notification.setStatus("enabled");
//			this.notificationDao.save(notification);
//		}
//
//		Configuration conf = this.configurationDao.getConfByUserId((Long) story.getUser().getId());
//		if (conf.isReposted_my_story_push()) {
//			int counts = this.notificationDao.getNotificationByRecipientId((Long) story.getUser().getId());
//			List<PushNotification> list = this.pushNotificationDao
//					.getPushNotificationByUserid((Long) story.getUser().getId());
//			try {
//				String content = user.getUsername() + " �ղ�����Ҽ�";
//				JSONObject json = new JSONObject();
//				json.put("user_id", user.getId());
//				PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content, json.toString());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		JSONObject jo = new JSONObject();
//		Set<User> suser = story.getRepost_users();
//		if (suser != null && suser.size() > 0) {
//			count = suser.size();
//		}
		// count = this.republishDao.count(storyId);
		log.debug("*** repost story success ***");
		return null;
	}

	public Response deleteRepost(Long storyId, Long loginUserid,HttpServletRequest request) {

		log.debug("*** repost story ***");
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		JSONObject resp = new JSONObject();
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("data_type", "40");
		map.put("data_id", String.valueOf(storyId));
		map.put("ip", ip);
		map.put("device", device);
		map.put("user_id", String.valueOf(loginUserid));
		map.put("collect_type", "-1");
		
		String params = "";
		try {
			params = carPublicParam(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = HttpUtil.sendPostStr(car_url+"/member/collect-data", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){
				resp.put("status", "success");
				return Response.status(Response.Status.OK).entity(resp).build();
			}else if (code == 11001){
				resp.put("status", "用户不存在");
				resp.put("code", 10650);
				resp.put("error_message", "用户不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11006){
				resp.put("status", "不能重复操作");
				resp.put("code", 10550);
				resp.put("error_message", "不能重复操作");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11008){
				resp.put("status", "保存数据操作日志失败");
				resp.put("code", 10551);
				resp.put("error_message", "保存数据操作日志失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11018){
				resp.put("status", "对象类型错误");
				resp.put("code", 10552);
				resp.put("error_message", "对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11019){
				resp.put("status", "对象不存在");
				resp.put("code", 10553);
				resp.put("error_message", "对象不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11021){
				resp.put("status", "收藏类型错误");
				resp.put("code", 10554);
				resp.put("error_message", "收藏类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if (code == 11023){
				resp.put("status", "取消收藏失败");
				resp.put("code", 10556);
				resp.put("error_message", "取消收藏失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
		
//		Republish report = republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
//		JSONObject json1 = new JSONObject();
//		if (report != null) {
//			json1.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json1).build();
//		}
//		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
//		JSONObject jsonObject = ParseFile.parseJson(path);
//		String appId = jsonObject.getString("appId");
//		String appKey = jsonObject.getString("appKey");
//		String masterSecret = jsonObject.getString("masterSecret");
//		int count = 0;
//		User user = (User) this.userDao.get(loginUserid);
//		Story story = (Story) this.storyDao.get(storyId);
//		Republish r = new Republish();
//		r.setCreateTime(new Date());
//		r.setRepost_users(user);
//		r.setRepost_story(story);
//		r.setType("repost");
//		this.republishDao.save(r);
//		Timeline timeline = new Timeline();
//		timeline.setCreatorId(loginUserid);
//		timeline.setTargetUserId(loginUserid);
//		timeline.setStory(story);
//		timeline.setType("repost");
//		timeline.setReferenceId(storyId);
//		timeline.setCreateTime(new Date());
//		this.timelineDao.save(timeline);
//		Notification notification = this.notificationDao.getNotificationByAction(storyId, loginUserid, 1, 3);
//		if (notification != null) {
//			notification.setCreate_at(new Date());
//			this.notificationDao.update(notification);
//		} else {
//			notification = new Notification();
//			notification.setSenderId(loginUserid);
//			notification.setRecipientId((Long) story.getUser().getId());
//			notification.setNotificationType(3);
//			notification.setObjectType(1);
//			notification.setObjectId(storyId);
//			notification.setRead_already(false);
//			notification.setStatus("enabled");
//			this.notificationDao.save(notification);
//		}
//
//		Configuration conf = this.configurationDao.getConfByUserId((Long) story.getUser().getId());
//		if (conf.isReposted_my_story_push()) {
//			int counts = this.notificationDao.getNotificationByRecipientId((Long) story.getUser().getId());
//			List<PushNotification> list = this.pushNotificationDao
//					.getPushNotificationByUserid((Long) story.getUser().getId());
//			try {
//				String content = user.getUsername() + " �ղ�����Ҽ�";
//				JSONObject json = new JSONObject();
//				json.put("user_id", user.getId());
//				PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content, json.toString());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		JSONObject jo = new JSONObject();
//		Set<User> suser = story.getRepost_users();
//		if (suser != null && suser.size() > 0) {
//			count = suser.size();
//		}
		// count = this.republishDao.count(storyId);
		log.debug("*** repost story success ***");
		return null;
	
	}

	public CommentModel getCommentModel(Comment comment) {
		CommentModel commentModel = new CommentModel();
		commentModel.setId((Long) comment.getId());
		commentModel.setContent(comment.getContent());
		commentModel.setCreated_time(comment.getCreated_time());
		commentModel.setStory_id((Long) comment.getStory().getId());
		if(!Strings.isNullOrEmpty(comment.getComment_image())){
			commentModel.setComment_image(JSONObject.fromObject(comment.getComment_image()));
		}
		User user = comment.getUser();
		Set<Collection> cSet = comment.getStory().getCollections();
		if(cSet != null && cSet.size() > 0){
			Collection c = cSet.iterator().next();
			if(true){
				commentModel.setComment_user_type("collection");
			}else{
				if(user.getUser_type().equals("admin") 
						|| user.getUser_type().equals("super_admin")){
					commentModel.setComment_user_type("admin");
				}else{
					if(comment.getStory().getUser().getId() 
							== user.getId() && 
							comment.getStory().getUser().getId().equals(user.getId())){
						commentModel.setComment_user_type("author");
					}else{
						commentModel.setComment_user_type("normal");
					}
				} 
				
			}
		}else{

			if(user.getUser_type().equals("admin") 
					|| user.getUser_type().equals("super_admin")){
				commentModel.setComment_user_type("admin");
			}else{
				if(comment.getStory().getUser().getId() 
						== user.getId() && 
						comment.getStory().getUser().getId().equals(user.getId())){
					commentModel.setComment_user_type("author");
				}else{
					commentModel.setComment_user_type("normal");
				}
			} 
			
		
		}
		
		
		JSONObject userIntro = new JSONObject();

		userIntro.put("id", user.getId());
		userIntro.put("username", user.getUsername());
		JSONObject avatarJson = null;
		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
			avatarJson = JSONObject.fromObject(user.getAvatarImage());
		}
		userIntro.put("avatar_image", avatarJson);
		commentModel.setAuthor(userIntro);
		if (comment.getTarget_user_id() != null) {
			User targetUser = (User) this.userDao.get(comment.getTarget_user_id());
			JSONObject targetUserJson = new JSONObject();
			targetUserJson.put("id", targetUser.getId());
			targetUserJson.put("username", targetUser.getUsername());
			commentModel.setTarget_user(targetUserJson);
		}

		return commentModel;
	}

	public CommentStoryModel getCommentStoryModel(Comment comment) {
		CommentStoryModel commentModel = new CommentStoryModel();
		commentModel.setId((Long) comment.getId());
		commentModel.setContent(comment.getContent());
		commentModel.setCreated_time(comment.getCreated_time());
		commentModel.setStory_id((Long) comment.getStory().getId());
		if(!Strings.isNullOrEmpty(comment.getComment_image())){
			commentModel.setComment_image(JSONObject.fromObject(comment.getComment_image()));
		}
		User user = comment.getUser();
		JSONObject userIntro = new JSONObject();
		Set<Collection> cSet = comment.getStory().getCollections();
		if(cSet != null && cSet.size() > 0){
			Collection c = cSet.iterator().next();
			if(true){
				commentModel.setComment_user_type("collection");
			}else{
				if(user.getUser_type().equals("admin") 
						|| user.getUser_type().equals("super_admin")){
					commentModel.setComment_user_type("admin");
				}else{
					if(comment.getStory().getUser().getId() 
							== user.getId() && 
							comment.getStory().getUser().getId().equals(user.getId())){
						commentModel.setComment_user_type("author");
					}else{
						commentModel.setComment_user_type("normal");
					}
				} 
				
			}
		}else{

			if(user.getUser_type().equals("admin") 
					|| user.getUser_type().equals("super_admin")){
				commentModel.setComment_user_type("admin");
			}else{
				if(comment.getStory().getUser().getId() 
						== user.getId() && 
						comment.getStory().getUser().getId().equals(user.getId())){
					commentModel.setComment_user_type("author");
				}else{
					commentModel.setComment_user_type("normal");
				}
			} 
			
		
		}
		userIntro.put("id", user.getId());
		userIntro.put("username", user.getUsername());
		JSONObject avatarJson = null;
		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
			avatarJson = JSONObject.fromObject(user.getAvatarImage());
		}
		userIntro.put("avatar_image", avatarJson);
		commentModel.setAuthor(userIntro);

		return commentModel;
	}

	public JSONObject getCommentSummaryModel(Comment comment) {
		CommentSummaryModel commentModel = new CommentSummaryModel();
		commentModel.setId(comment.getId());
		commentModel.setContent(comment.getContent());
		commentModel.setCreated_time(comment.getCreated_time());
		commentModel.setStory_id(comment.getStory().getId());
		if(!Strings.isNullOrEmpty(comment.getComment_image())){
			commentModel.setComment_image(JSONObject.fromObject(comment.getComment_image()));
		}
		User user = comment.getUser();
		JSONObject userIntro = new JSONObject();

		userIntro.put("id", user.getId());
		userIntro.put("username", user.getUsername());
		JSONObject avatarJson = null;
		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
			avatarJson = JSONObject.fromObject(user.getAvatarImage());
		}
		userIntro.put("avatar_image", avatarJson);
		commentModel.setAuthor(userIntro);
		
		JsonConfig configs = new JsonConfig();
		List<String> delArray = new ArrayList<String>();
		if (Strings.isNullOrEmpty(comment.getComment_image())) {
			delArray.add("comment_image");
		} 

		JSONObject commentJson = null;
		if ((delArray != null) && (delArray.size() > 0)) {
			configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
			configs.setIgnoreDefaultExcludes(false);
			configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

			commentJson = JSONObject.fromObject(commentModel, configs);
		} else {
			commentJson = JSONObject.fromObject(commentModel);
		}

		return commentJson;
	}

	public ReplyCommentModel getReplyCommentModel(Comment comment) {
		ReplyCommentModel commentModel = new ReplyCommentModel();
		commentModel.setId((Long) comment.getId());
		commentModel.setContent(comment.getContent());
		commentModel.setCreated_time(comment.getCreated_time());
		commentModel.setStory_id((Long) comment.getStory().getId());
		commentModel.setTarget_comment_id(comment.getTarget_comment_id());
		User user = comment.getUser();
		Set<Collection> cSet = comment.getStory().getCollections();
		if(cSet != null && cSet.size() > 0){
			Collection c = cSet.iterator().next();
			if(true){
				commentModel.setComment_user_type("collection");
			}else{
				if(user.getUser_type().equals("admin") 
						|| user.getUser_type().equals("super_admin")){
					commentModel.setComment_user_type("admin");
				}else{
					if(comment.getStory().getUser().getId() 
							== user.getId() && 
							comment.getStory().getUser().getId().equals(user.getId())){
						commentModel.setComment_user_type("author");
					}else{
						commentModel.setComment_user_type("normal");
					}
				} 
				
			}
		}else{

			if(user.getUser_type().equals("admin") 
					|| user.getUser_type().equals("super_admin")){
				commentModel.setComment_user_type("admin");
			}else{
				if(comment.getStory().getUser().getId() 
						== user.getId() && 
						comment.getStory().getUser().getId().equals(user.getId())){
					commentModel.setComment_user_type("author");
				}else{
					commentModel.setComment_user_type("normal");
				}
			} 
			
		
		}
		JSONObject userIntro = new JSONObject();

		userIntro.put("id", user.getId());
		userIntro.put("username", user.getUsername());
		JSONObject avatarJson = null;
		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
			avatarJson = JSONObject.fromObject(user.getAvatarImage());
		}
		userIntro.put("avatar_image", avatarJson);
		commentModel.setAuthor(userIntro);
		
		if (comment.getTarget_user_id() != null) {
			User targetUser = (User) this.userDao.get(comment.getTarget_user_id());
			JSONObject targetUserJson = new JSONObject();
			targetUserJson.put("id", targetUser.getId());
			targetUserJson.put("username", targetUser.getUsername());
			targetUserJson.put("user_type", targetUser.getUser_type());
			commentModel.setTarget_user(targetUserJson);
		}
		
		if(!Strings.isNullOrEmpty(comment.getComment_image())){
			commentModel.setComment_image(comment.getComment_image());
		}

		return commentModel;
	}

	public StoryModel returnStoryModel(Story story) {
		StoryModel storyModel = new StoryModel();
		storyModel.setId((Long) story.getId());
		int likesCount = 0;
		int repostStoryCount = this.republishDao.userRepostCount((Long) story.getUser().getId());
		User user = story.getUser();
		JSONObject avatarImageJson = new JSONObject();
		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
		}

		JSONObject coverImageJson = new JSONObject();
		if (!Strings.isNullOrEmpty(user.getCoverImage())) {
			coverImageJson = JSONObject.fromObject(user.getCoverImage());
		}
		int storyCount = this.storyDao.getStoryCount((Long) user.getId());
		int follower_Count =0;
		int following_count = 0;

		JSONObject authorJson = new JSONObject();
		authorJson.put("id", user.getId());
		authorJson.put("username", user.getUsername());
		authorJson.put("email", user.getEmail());
		authorJson.put("created_time", user.getCreated_time());
		authorJson.put("status", user.getStatus());
		authorJson.put("introduction", user.getIntroduction());
		authorJson.put("avatar_image", avatarImageJson);
		authorJson.put("cover_image", coverImageJson);
		authorJson.put("likes_count", Integer.valueOf(likesCount));
		authorJson.put("reposts_count", Integer.valueOf(repostStoryCount));
		authorJson.put("stories_count", Integer.valueOf(storyCount));
		authorJson.put("followers_count", Integer.valueOf(follower_Count));
		authorJson.put("following_count", Integer.valueOf(following_count));
		authorJson.put("user_type", user.getUser_type());
		if (!Strings.isNullOrEmpty(user.getWebsite()))
			authorJson.put("website", user.getWebsite());
		else {
			authorJson.put("website", null);
		}

		if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
			Set<PublisherInfo> publisherSet = user.getPublisherInfos();
			List<PublisherInfoModel> publisherList = null;
			PublisherInfoModel pim = null;
			if ((publisherSet != null) && (publisherSet.size() > 0)) {
				publisherList = new ArrayList<PublisherInfoModel>();
				for (PublisherInfo pi : publisherSet) {
					pim = new PublisherInfoModel();
					pim.setType(pi.getType());
					pim.setContent(pi.getContent());
					publisherList.add(pim);
				}
			}

			authorJson.put("publisher_info", publisherList);
		}
		storyModel.setAuthor(authorJson);

		storyModel.setCreated_time(story.getCreated_time());
		storyModel.setUpdate_time(story.getUpdate_time());

		JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
		log.debug("***story.getCover_page()***" + jsonObject);
		String type = jsonObject.getString("type");

		if (type.equals("text")) {
			TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
			storyModel.setCover_media(JSONObject.fromObject(coverMedia));
		} else if (type.equals("image")) {
			ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
			storyModel.setCover_media(JSONObject.fromObject(coverMedia));
		} else if (type.equals("multimedia")) {
			storyModel.setCover_media(jsonObject);
		}

		List<StoryElement> storyElements = new ArrayList<StoryElement>();
		List<StoryElement> seSet = story.getElements();
		if ((seSet != null) && (seSet.size() > 0)) {
			JSONObject content = null;
			for (StoryElement element : seSet) {
				content = JSONObject.fromObject(element.getContents());
				String types = content.getString("type");
				if (types.equals("text")) {
					TextCover cm = (TextCover) JSONObject.toBean(content, TextCover.class);
					element.setContent(cm);
				} else if (types.equals("image")) {
					ImageCover cm = (ImageCover) JSONObject.toBean(content, ImageCover.class);
					element.setContent(cm);
				} else if (types.equals("location")) {
					LocationModel locationModel = (LocationModel) JSONObject.toBean(content, LocationModel.class);
					element.setContent(locationModel);
				} else if (types.equals("link")) {
					String media = content.getString("media");
					JSONObject mediaJSON = JSONObject.fromObject(media);
					if (mediaJSON.containsKey("image")) {
						LinkModel linkModel = (LinkModel) JSONObject.toBean(content, LinkModel.class);
						element.setContent(linkModel);
					} else {
						LinkModels linkModel = (LinkModels) JSONObject.toBean(content, LinkModels.class);
						element.setContent(linkModel);
					}

				}
				storyElements.add(element);
			}
		}

		JsonConfig config = new JsonConfig();
		config.setExcludes(new String[] { "storyinfo", "contents" });
		config.setIgnoreDefaultExcludes(false);
		config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		log.debug("***get Elements *****" + JSONArray.fromObject(story.getElements(), config));
		if ((storyElements != null) && (storyElements.size() > 0)) {
			storyModel.setElements(JSONArray.fromObject(storyElements, config));
		}

		List<Comment> commentList = this.commentDao.getAllByStoryId((Long) story.getId());
		storyModel.setCommnents_enables(story.getComments_enabled());

		if (!Strings.isNullOrEmpty(story.getTinyURL()))
			storyModel.setUrl(story.getTinyURL());
		else {
			storyModel.setUrl(null);
		}
		storyModel.setView_count(story.getViewTimes());
		storyModel.setTitle(story.getTitle());

		storyModel.setImage_count(story.getImage_count());
		storyModel.setComment_count(commentList.size());
		Set<Story> sSet = user.getRepost_story();
		List<Story> rsList = new ArrayList<Story>();
		if (sSet != null && sSet.size() > 0) {
			Iterator<Story> it = sSet.iterator();
			while (it.hasNext()) {
				rsList.add(it.next());
			}
			if (rsList.contains(story)) {
				storyModel.setRepost_by_current_user(true);
			} else {
				storyModel.setRepost_by_current_user(false);
			}
		} else {
			storyModel.setRepost_by_current_user(false);
		}

		int repostCount = 0;// this.republishDao.count((Long)story.getId());
		Set<User> suser = story.getRepost_users();
		if (suser != null && suser.size() > 0) {
			repostCount = suser.size();
		}
		storyModel.setRepost_count(repostCount);
		/*
		 * Republish repost = this.republishDao.getRepostByUserIdAndStoryId(
		 * (Long)story .getUser().getId(), (Long)story.getId()); if (repost !=
		 * null) storyModel.setRepost_by_current_user(true); else {
		 * storyModel.setRepost_by_current_user(false); }
		 */

		return storyModel;
	}

//	public EventModel getEventModelListByLoginUserid(Timeline timeline, Long loginUserid, JSONArray collection_id) {
//		try {
//			EventModel event = new EventModel();
//			event.setId((Long) timeline.getId());
//			event.setEvent_time(timeline.getCreateTime());
//			event.setEvent_type(timeline.getType());
//			JSONObject contentJson = new JSONObject();
//			Long storyId = (Long) timeline.getStory().getId();
//			Story story = this.storyDao.getStoryByIdAndStatus(storyId, "publish", "disabled");
//			StoryPageModel storyModel = new StoryPageModel();
//
//			if (story != null) {
//				Object config;
//				Object storyJson;
//				if (((Long) story.getUser().getId()).equals(loginUserid)) {
//					storyModel.setId(storyId);
//					storyModel.setImage_count(story.getImage_count());
//					storyModel.setSummary(story.getSummary());
//					/*
//					 * int likesCount = this.likesDao.userLikesCount(
//					 * (Long)story.getUser() .getId()); int repostStoryCount =
//					 * this.republishDao.userRepostCount( (Long)story
//					 * .getUser().getId());
//					 */
//					User user = story.getUser();
//					JSONObject avatarImageJson = null;
//					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
//					}
//					JSONObject coverImageJson = null;
//					if (!Strings.isNullOrEmpty(user.getCoverImage())) {
//						coverImageJson = JSONObject.fromObject(user.getCoverImage());
//					}
//
//					JSONObject authorJson = new JSONObject();
//					authorJson.put("id", user.getId());
//					authorJson.put("username", user.getUsername());
//					authorJson.put("email", user.getEmail());
//					authorJson.put("created_time", user.getCreated_time());
//					authorJson.put("status", user.getStatus());
//					authorJson.put("introduction", user.getIntroduction());
//					authorJson.put("avatar_image", avatarImageJson);
//					authorJson.put("cover_image", coverImageJson);
//					authorJson.put("user_type", user.getUser_type());
//					if (!Strings.isNullOrEmpty(user.getWebsite()))
//						authorJson.put("website", user.getWebsite());
//					else {
//						authorJson.put("website", null);
//					}
//					if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
//						Set<PublisherInfo> publisherSet = user.getPublisherInfos();
//						List<PublisherInfoModel> publisherList = null;
//						PublisherInfoModel pim = null;
//						if ((publisherSet != null) && (publisherSet.size() > 0)) {
//							publisherList = new ArrayList<PublisherInfoModel>();
//							for (PublisherInfo pi : publisherSet) {
//								pim = new PublisherInfoModel();
//								pim.setType(pi.getType());
//								pim.setContent(pi.getContent());
//								publisherList.add(pim);
//							}
//						}
//
//						authorJson.put("publisher_info", publisherList);
//					}
//
//					if (timeline.getCreatorId().equals(timeline.getTargetUserId())) {
//						authorJson.put("followed_by_current_user", Boolean.valueOf(false));
//						authorJson.put("is_following_current_user", Boolean.valueOf(false));
//					} else {
//						authorJson.put("followed_by_current_user", Boolean.valueOf(true));
//						Follow follow = this.followDao.getFollow(timeline.getCreatorId(), timeline.getTargetUserId());
//						if (follow != null)
//							authorJson.put("is_following_current_user", Boolean.valueOf(true));
//						else {
//							authorJson.put("is_following_current_user", Boolean.valueOf(false));
//						}
//					}
//
//					storyModel.setAuthor(authorJson);
//					storyModel.setCreated_time(story.getCreated_time());
//					storyModel.setUpdate_time(story.getUpdate_time());
//					boolean flag_collection = false;
//					JSONArray collectArr = new JSONArray();
//					if (collection_id != null) {
//						Object[] cArr = collection_id.toArray();
//						if(cArr != null && cArr.length > 0){
//							for(Object cid:cArr){
//								Long param_cid = Long.parseLong(cid.toString());
//								Collection collect = collectionDao.getCollectionById(param_cid);
//								CollectionIntro ci = new CollectionIntro();
////								ci.setId((Long) collect.getId());
////								ci.setCollection_name(collect.getCollectionName());
//								ci.setCover_image(JSONObject.fromObject(collect.getCover_image()));
//								
//								User u = userDao.get(0l);
//								JSONObject author = new JSONObject();
//								author.put("id", u.getId());
//								author.put("username", u.getUsername());
//								ci.setAuthor(author);
//								
//								JsonConfig configs = new JsonConfig();
//								List<String> delArray = new ArrayList<String>();
////								if(!Strings.isNullOrEmpty(collect.getInfo())){
////									ci.setInfo(collect.getInfo());
////								}else{
////									delArray.add("info");
////								}
//								JSONObject collectionJson = null;
//								if ((delArray != null) && (delArray.size() > 0)) {
//									configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//									configs.setIgnoreDefaultExcludes(false);
//									configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//									collectionJson = JSONObject.fromObject(ci, configs);
//								} else {
//									collectionJson = JSONObject.fromObject(ci);
//								}
//								
//								collectArr.add(collectionJson);
//							}
//						}
//						
//						
//					} else {
//						storyModel.setCollection(null);
//					}
//
//					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
//					log.debug("***story.getCover_page()***" + jsonObject);
//					String type = jsonObject.getString("type");
//
//					if (type.equals("text")) {
//						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
//						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
//						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//					} else if (type.equals("image")) {
//						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
//						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//					} else if (type.equals("multimedia")) {
//						storyModel.setCover_media(jsonObject);
//					}else if(type.equals("line")){
//						LineCover coverMedia = (LineCover) JSONObject.toBean(jsonObject, LineCover.class);
//						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//					}
//
//					List<StoryElement> storyElements = new ArrayList<StoryElement>();
//					List<StoryElement> seSet = story.getElements();
//					if ((seSet != null) && (seSet.size() > 0)) {
//						JSONObject content = null;
//						for (StoryElement element : seSet) {
//							content = JSONObject.fromObject(element.getContents());
//							String types = content.getString("type");
//							if (types.equals("text")) {
//								TextCover cm = (TextCover) JSONObject.toBean(content, TextCover.class);
//								element.setContent(cm);
//							} else if (types.equals("image")) {
//								ImageCover cm = (ImageCover) JSONObject.toBean(content, ImageCover.class);
//								element.setContent(cm);
//							} else if (types.equals("location")) {
//								LocationModel locationModel = (LocationModel) JSONObject.toBean(content,
//										LocationModel.class);
//								element.setContent(locationModel);
//							} else if (types.equals("link")) {
//								String media = content.getString("media");
//								JSONObject mediaJSON = JSONObject.fromObject(media);
//								if (mediaJSON.containsKey("image")) {
//									LinkModel linkModel = (LinkModel) JSONObject.toBean(content, LinkModel.class);
//									element.setContent(linkModel);
//								} else {
//									LinkModels linkModel = (LinkModels) JSONObject.toBean(content, LinkModels.class);
//									element.setContent(linkModel);
//								}
//
//							}else if (types.equals("video")){
//								JSONObject media = content.getJSONObject("media");
//								if(media.containsKey("iframe_code")){
//									IframeCover iframeMedia = (IframeCover) JSONObject.toBean(content, IframeCover.class);
//									element.setContent(iframeMedia);
//								}else{
//									VideoCover videoMedia = (VideoCover) JSONObject.toBean(content, VideoCover.class);
//									element.setContent(videoMedia);
//								}
//								
//							}else if (types.equals("line")){
//								LineCover lineMedia = (LineCover) JSONObject.toBean(content, LineCover.class);
//								element.setContent(lineMedia);
//							}
//							storyElements.add(element);
//						}
//					}
//
//					config = new JsonConfig();
//					((JsonConfig) config).setExcludes(new String[] { "storyinfo", "contents" });
//					((JsonConfig) config).setIgnoreDefaultExcludes(false);
//					((JsonConfig) config).setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//					log.debug("***get Elements *****" + JSONArray.fromObject(story.getElements(), (JsonConfig) config));
//					if ((storyElements != null) && (storyElements.size() > 0)) {
//						storyModel.setElements(JSONArray.fromObject(storyElements, (JsonConfig) config));
//					}
//
//					storyModel.setCommnents_enables(story.getComments_enabled());
//					if (!Strings.isNullOrEmpty(story.getTinyURL())) {
//						storyModel.setUrl(story.getTinyURL());
//					}
//					storyModel.setView_count(story.getViewTimes());
//					storyModel.setTitle(story.getTitle());
//
//					int count = 0;
//					Set<Comment> cSet = story.getComments();
//					if (cSet != null && cSet.size() > 0) {
//						count = cSet.size();
//					}
//					storyModel.setComment_count(count);
//
//					List<Comment> commentList = this.commentDao.getCommentByStoryIdNewThree((Long) story.getId());
//					if ((commentList != null) && (commentList.size() > 0)) {
//						List<JSONObject> commentModelList = new ArrayList<JSONObject>();
//						JSONObject commentModel = null;
//						for (Comment c : commentList) {
//							commentModel = getCommentSummaryModel(c);
//							commentModelList.add(commentModel);
//						}
//						storyModel.setComments(commentModelList);
//					}
//					List<Story> storyList = this.storyDao.getStoriesByRandThree(story.getId());
//					if ((storyList != null) && (storyList.size() > 0)) {
//						List<StoryIntro> recommendations = new ArrayList<StoryIntro>();
//						StoryIntro intro = null;
//						for (Story s : storyList) {
//							intro = new StoryIntro();
//							intro.setId((Long) s.getId());
//							intro.setTitle(s.getTitle());
//							if (!Strings.isNullOrEmpty(s.getCover_page()))
//								intro.setCover_media(JSONObject.fromObject(s.getCover_page()));
//							else {
//								intro.setCover_media(null);
//							}
//							intro.setCollectionId(Long.valueOf(1L));
//							recommendations.add(intro);
//						}
//						storyModel.setRecommendation(recommendations);
//					}
//
//					JsonConfig configs = new JsonConfig();
//					List<String> delArray = new ArrayList<String>();
//					
//					if (Strings.isNullOrEmpty(story.getResource())) {
//						delArray.add("resource");
//					}
//					if (Strings.isNullOrEmpty(story.getTinyURL())) {
//						delArray.add("url");
//					}
//					if (collection_id == null) {
//						delArray.add("collection");
//					}
//					
//					if(!flag_collection){
//						delArray.add("collection");
//					}
//					if (Strings.isNullOrEmpty(story.getTitle())) {
//						delArray.add("title");
//					}
//					storyJson = null;
//					if ((delArray != null) && (delArray.size() > 0)) {
//						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//						configs.setIgnoreDefaultExcludes(false);
//						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//						storyJson = JSONObject.fromObject(storyModel, configs);
//					} else {
//						storyJson = JSONObject.fromObject(storyModel);
//					}
//
//					contentJson.put("story", storyJson);
//				} else if (story.getStatus().equals("publish")) {
//					storyModel.setId(storyId);
//					storyModel.setImage_count(story.getImage_count());
//					/*
//					 * int likesCount = this.likesDao.userLikesCount(
//					 * (Long)story .getUser().getId()); int repostStoryCount =
//					 * this.republishDao
//					 * .userRepostCount((Long)story.getUser().getId());
//					 */
//					User user = story.getUser();
//					JSONObject avatarImageJson = null;
//					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
//					}
//					JSONObject coverImageJson = null;
//					if (!Strings.isNullOrEmpty(user.getCoverImage())) {
//						coverImageJson = JSONObject.fromObject(user.getCoverImage());
//					}
//					/*
//					 * int storyCount =
//					 * this.storyDao.getStoryCount((Long)user.getId()); int
//					 * follower_Count = this.followDao.userFollowedCount(
//					 * (Long)user .getId()); int following_count =
//					 * this.followDao.userFollowCount( (Long)user .getId());
//					 */
//
//					JSONObject authorJson = new JSONObject();
//					authorJson.put("id", user.getId());
//					authorJson.put("username", user.getUsername());
//					authorJson.put("email", user.getEmail());
//					authorJson.put("created_time", user.getCreated_time());
//					authorJson.put("status", user.getStatus());
//					authorJson.put("introduction", user.getIntroduction());
//					authorJson.put("avatar_image", avatarImageJson);
//					authorJson.put("cover_image", coverImageJson);
//					/*
//					 * authorJson.put("likes_count",
//					 * Integer.valueOf(likesCount));
//					 * authorJson.put("reposts_count",
//					 * Integer.valueOf(repostStoryCount));
//					 * authorJson.put("stories_count",
//					 * Integer.valueOf(storyCount));
//					 * authorJson.put("followers_count",
//					 * Integer.valueOf(follower_Count));
//					 * authorJson.put("following_count",
//					 * Integer.valueOf(following_count));
//					 */
//					authorJson.put("user_type", user.getUser_type());
//					if (!Strings.isNullOrEmpty(user.getWebsite()))
//						authorJson.put("website", user.getWebsite());
//					else {
//						authorJson.put("website", null);
//					}
//					if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
//						Set<PublisherInfo> publisherSet = user.getPublisherInfos();
//						List<PublisherInfoModel> publisherList = null;
//						PublisherInfoModel pim = null;
//						if ((publisherSet != null) && (publisherSet.size() > 0)) {
//							publisherList = new ArrayList<PublisherInfoModel>();
//							for (PublisherInfo pi : publisherSet) {
//								pim = new PublisherInfoModel();
//								pim.setType(pi.getType());
//								pim.setContent(pi.getContent());
//								publisherList.add(pim);
//							}
//						}
//
//						authorJson.put("publisher_info", publisherList);
//					}
//
//					if (timeline.getCreatorId().equals(timeline.getTargetUserId())) {
//						authorJson.put("followed_by_current_user", Boolean.valueOf(false));
//						authorJson.put("is_following_current_user", Boolean.valueOf(false));
//					} else {
//						authorJson.put("followed_by_current_user", Boolean.valueOf(true));
//						Follow follow = this.followDao.getFollow(timeline.getCreatorId(), timeline.getTargetUserId());
//						if (follow != null)
//							authorJson.put("is_following_current_user", Boolean.valueOf(true));
//						else {
//							authorJson.put("is_following_current_user", Boolean.valueOf(false));
//						}
//					}
//
//					storyModel.setAuthor(authorJson);
//					storyModel.setCreated_time(story.getCreated_time());
//					storyModel.setUpdate_time(story.getUpdate_time());
//
//					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
//					log.debug("***story.getCover_page()***" + jsonObject);
//					String type = jsonObject.getString("type");
//
//					if (type.equals("text")) {
//						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
//						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
//						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//					} else if (type.equals("image")) {
//						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
//						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//					} else if (type.equals("multimedia")) {
//						storyModel.setCover_media(jsonObject);
//					}
//
//					List<StoryElement> storyElements = new ArrayList<StoryElement>();
//					List<StoryElement> seSet = story.getElements();
//					if ((seSet != null) && (seSet.size() > 0)) {
//						JSONObject content = null;
//						for (StoryElement element : seSet) {
//							content = JSONObject.fromObject(element.getContents());
//							String types = content.getString("type");
//							if (types.equals("text")) {
//								TextCover cm = (TextCover) JSONObject.toBean(content, TextCover.class);
//								element.setContent(cm);
//							} else if (types.equals("image")) {
//								ImageCover cm = (ImageCover) JSONObject.toBean(content, ImageCover.class);
//								element.setContent(cm);
//							} else if (types.equals("location")) {
//								LocationModel locationModel = (LocationModel) JSONObject.toBean(content,
//										LocationModel.class);
//								element.setContent(locationModel);
//							} else if (types.equals("link")) {
//								String media = content.getString("media");
//								JSONObject mediaJSON = JSONObject.fromObject(media);
//								if (mediaJSON.containsKey("image")) {
//									LinkModel linkModel = (LinkModel) JSONObject.toBean(content, LinkModel.class);
//									element.setContent(linkModel);
//								} else {
//									LinkModels linkModel = (LinkModels) JSONObject.toBean(content, LinkModels.class);
//									element.setContent(linkModel);
//								}
//
//							} else if (types.equals("video")){
//								
//
//								JSONObject media = content.getJSONObject("media");
//								if(media.containsKey("iframe_code")){
//									IframeCover iframeMedia = (IframeCover) JSONObject.toBean(content, IframeCover.class);
//									element.setContent(iframeMedia);
//								}else{
//									VideoCover videoMedia = (VideoCover) JSONObject.toBean(content, VideoCover.class);
//									element.setContent(videoMedia);
//								}
//								
//							} else if (types.equals("line")){
//								LineCover lineMedia = (LineCover) JSONObject.toBean(content, LineCover.class);
//								element.setContent(lineMedia);
//							}
//							storyElements.add(element);
//						}
//					}
//
//					JsonConfig config1 = new JsonConfig();
//					config1.setExcludes(new String[] { "storyinfo", "contents" });
//					config1.setIgnoreDefaultExcludes(false);
//					config1.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//					if ((storyElements != null) && (storyElements.size() > 0)) {
//						storyModel.setElements(JSONArray.fromObject(storyElements, config1));
//					}
//
//					storyModel.setCommnents_enables(story.getComments_enabled());
//					if (!Strings.isNullOrEmpty(story.getTinyURL()))
//						storyModel.setUrl(story.getTinyURL());
//					else {
//						storyModel.setUrl(null);
//					}
//					storyModel.setView_count(story.getViewTimes());
//					storyModel.setTitle(story.getTitle());
//
//					int count = 0;
//					Set<Comment> cSet = story.getComments();
//					if (cSet != null && cSet.size() > 0) {
//						count = cSet.size();
//					}
//					storyModel.setComment_count(count);
//					/*
//					 * Likes likes1 = this.likesDao.getLikeByUserIdAndStoryId(
//					 * loginUserid, storyId); if (likes1 != null)
//					 * storyModel.setLiked_by_current_user(true); else {
//					 * storyModel.setLiked_by_current_user(false); } int
//					 * likeCount = this.likesDao.likeStoryCount(storyId);
//					 * storyModel.setLike_count(likeCount); int repostCount =
//					 * this.republishDao.count(storyId);
//					 * storyModel.setRepost_count(repostCount);
//					 */
//					Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
//					if (repost != null)
//						storyModel.setRepost_by_current_user(true);
//					else {
//						storyModel.setRepost_by_current_user(false);
//					}
//
//					List<Comment> commentList = this.commentDao.getCommentByStoryIdNewThree((Long) story.getId());
//					if ((commentList != null) && (commentList.size() > 0)) {
//						List<JSONObject> commentModelList = new ArrayList<JSONObject>();
//						JSONObject commentModel = null;
//						for (Comment c : commentList) {
//							commentModel = getCommentSummaryModel(c);
//							commentModelList.add(commentModel);
//						}
//						storyModel.setComments(commentModelList);
//					}
//					List<Story> storyList = this.storyDao.getStoriesByRandThree(story.getId());
//					if ((storyList != null) && (storyList.size() > 0)) {
//						List<StoryIntro> recommendations = new ArrayList<StoryIntro>();
//						StoryIntro intro = null;
//						for (Story s : storyList) {
//							intro = new StoryIntro();
//							intro.setId((Long) s.getId());
//							intro.setTitle(s.getTitle());
//							if (!Strings.isNullOrEmpty(s.getCover_page()))
//								intro.setCover_media(JSONObject.fromObject(s.getCover_page()));
//							else {
//								intro.setCover_media(null);
//							}
//							intro.setCollectionId(Long.valueOf(1L));
//							recommendations.add(intro);
//						}
//						storyModel.setRecommendation(recommendations);
//					}
//
//					JsonConfig configs = new JsonConfig();
//					List<String> delArray = new ArrayList<String>();
//					if (Strings.isNullOrEmpty(story.getResource())) {
//						delArray.add("resource");
//					}
//					if (Strings.isNullOrEmpty(story.getTinyURL())) {
//						delArray.add("url");
//					}
//					if (Strings.isNullOrEmpty(story.getTitle())) {
//						delArray.add("title");
//					}
//					if ((storyElements == null) || (storyElements.size() == 0)) {
//						delArray.add("elements");
//					}
//					JSONObject storyJson1 = null;
//					if ((delArray != null) && (delArray.size() > 0)) {
//						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//						configs.setIgnoreDefaultExcludes(false);
//						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//						storyJson1 = JSONObject.fromObject(storyModel, configs);
//					} else {
//						storyJson1 = JSONObject.fromObject(storyModel);
//					}
//					contentJson.put("story", storyJson1);
//				}
//
//				if (timeline.getType().equals("post")) {
//					event.setContent(contentJson);
//				} else if (timeline.getType().equals("repost")) {
//					UserIntro userIntro = new UserIntro();
//					User user = (User) this.userDao.get(timeline.getCreatorId());
//					if (user != null) {
//						userIntro.setId((Long) user.getId());
//						userIntro.setUsername(user.getUsername());
//						userIntro.setIntroduction(user.getIntroduction());
//						userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
//						userIntro.setUser_type(user.getUser_type());
//						contentJson.put("repost_by", userIntro);
//					}
//					event.setContent(contentJson);
//				} else if (timeline.getType().equals("like")) {
//					UserIntro userIntro = new UserIntro();
//					User user = (User) this.userDao.get(timeline.getCreatorId());
//					if (user != null) {
//						userIntro.setId((Long) user.getId());
//						userIntro.setUsername(user.getUsername());
//						userIntro.setIntroduction(user.getIntroduction());
//						userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
//						userIntro.setUser_type(user.getUser_type());
//						contentJson.put("like_by", userIntro);
//					}
//					event.setContent(contentJson);
//				}
//			}
//
//			return event;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return null;
//	}

//	public EventModel getEventModelListByLoginid(Timeline timeline, Long loginUserid) {
//		try {
//			EventModel event = new EventModel();
//			event.setId((Long) timeline.getId());
//			event.setEvent_time(timeline.getCreateTime());
//			event.setEvent_type(timeline.getType());
//			JSONObject contentJson = new JSONObject();
//			Long storyId = (Long) timeline.getStory().getId();
//			Story story = this.storyDao.getStoryByIdAndStatus(storyId, "publish", "disabled");
//			StoryEvent storyModel = new StoryEvent();
//
//			if (story != null) {
//				if (((Long) story.getUser().getId()).equals(loginUserid)) {
//					storyModel.setId(storyId);
//					storyModel.setImage_count(story.getImage_count());
//					storyModel.setUrl(story.getTinyURL());
//					storyModel.setResource(story.getResource());
//					User user = story.getUser();
//					JSONObject authorJson = new JSONObject();
//					JSONObject coverImageJson = null;
//					if (!Strings.isNullOrEmpty(user.getCoverImage())) {
//						coverImageJson = JSONObject.fromObject(user.getCoverImage());
//					}
//
//					JSONObject avatarImageJson = null;
//					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
//					}
//					authorJson.put("id", user.getId());
//					authorJson.put("cover_image", coverImageJson);
//					authorJson.put("username", user.getUsername());
//					authorJson.put("introduction", user.getIntroduction());
//					authorJson.put("avatar_image", avatarImageJson);
//					authorJson.put("user_type", user.getUser_type());
//					if (!Strings.isNullOrEmpty(user.getWebsite()))
//						authorJson.put("website", user.getWebsite());
//					else {
//						authorJson.put("website", null);
//					}
//					if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
//						Set<PublisherInfo> publisherSet = user.getPublisherInfos();
//						List<PublisherInfoModel> publisherList = null;
//						PublisherInfoModel pim = null;
//						if ((publisherSet != null) && (publisherSet.size() > 0)) {
//							publisherList = new ArrayList<PublisherInfoModel>();
//							for (PublisherInfo pi : publisherSet) {
//								pim = new PublisherInfoModel();
//								pim.setType(pi.getType());
//								pim.setContent(pi.getContent());
//								publisherList.add(pim);
//							}
//						}
//
//						authorJson.put("publisher_info", publisherList);
//					}
//					storyModel.setAuthor(authorJson);
//					int repost_count = republishDao.count(story.getId());
//					storyModel.setRepost_count(repost_count);
//					int count = this.commentDao.getCommentCountById(story.getId());
//					storyModel.setComment_count(count);
//					storyModel.setCreated_time(story.getCreated_time());
//					/*Collection collection = this.collectionStoryDao.getCollectionByStoryId(storyId);
//					if (collection != null) {
//						CollectionIntro ci = new CollectionIntro();
//						ci.setId((Long) collection.getId());
//						ci.setCollection_name(collection.getCollectionName());
//						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
//						ci.setInfo(collection.getInfo());
//						User author = userDao.get(collection.getUser().getId());
//						JSONObject json = new JSONObject();
//						json.put("id", author.getId());
//						json.put("username", author.getUsername());
//						ci.setAuthor(json);
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						JSONObject collectionJson = null;
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							collectionJson = JSONObject.fromObject(ci, configs);
//						} else {
//							collectionJson = JSONObject.fromObject(ci);
//						}
//
//						storyModel.setCollection(collectionJson);
//					}*/
//					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
//					log.debug("***story.getCover_page()***" + jsonObject);
//					String type = jsonObject.getString("type");
//
//					if (type.equals("text")) {
//						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
//						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
//						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//					} else if (type.equals("image")) {
//						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
//						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//					} else if (type.equals("multimedia")) {
//						storyModel.setCover_media(jsonObject);
//					}
//
//					storyModel.setTitle(story.getTitle());
//
//					storyModel.setAuthor(authorJson);
//					storyModel.setSummary(story.getSummary());
//					Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
//					if (repost != null)
//						storyModel.setRepost_by_current_user(true);
//					else {
//						storyModel.setRepost_by_current_user(false);
//					}
//					JsonConfig configs = new JsonConfig();
//					List<String> delArray = new ArrayList<String>();
//
//					if (Strings.isNullOrEmpty(story.getTitle())) {
//						delArray.add("title");
//					}
//
//					if (Strings.isNullOrEmpty(story.getSummary())) {
//						delArray.add("summary");
//					}
//					JSONObject storyJson = null;
//					if ((delArray != null) && (delArray.size() > 0)) {
//						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//						configs.setIgnoreDefaultExcludes(false);
//						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//						storyJson = JSONObject.fromObject(storyModel, configs);
//					} else {
//						storyJson = JSONObject.fromObject(storyModel);
//					}
//
//					contentJson.put("story", storyJson);
//				} else if (story.getStatus().equals("publish")) {
//					storyModel.setId(storyId);
//					storyModel.setImage_count(story.getImage_count());
//					storyModel.setUrl(story.getTinyURL());
//					storyModel.setResource(story.getResource());
//					User user = story.getUser();
//					JSONObject authorJson = new JSONObject();
//					JSONObject coverImageJson = null;
//					if (!Strings.isNullOrEmpty(user.getCoverImage())) {
//						coverImageJson = JSONObject.fromObject(user.getCoverImage());
//					}
//
//					JSONObject avatarImageJson = null;
//					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
//					}
//					authorJson.put("id", user.getId());
//					authorJson.put("cover_image", coverImageJson);
//					authorJson.put("username", user.getUsername());
//					authorJson.put("introduction", user.getIntroduction());
//					authorJson.put("avatar_image", avatarImageJson);
//					authorJson.put("user_type", user.getUser_type());
//					if (!Strings.isNullOrEmpty(user.getWebsite()))
//						authorJson.put("website", user.getWebsite());
//					else {
//						authorJson.put("website", null);
//					}
//					if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
//						Set<PublisherInfo> publisherSet = user.getPublisherInfos();
//						List<PublisherInfoModel> publisherList = null;
//						PublisherInfoModel pim = null;
//						if ((publisherSet != null) && (publisherSet.size() > 0)) {
//							publisherList = new ArrayList<PublisherInfoModel>();
//							for (PublisherInfo pi : publisherSet) {
//								pim = new PublisherInfoModel();
//								pim.setType(pi.getType());
//								pim.setContent(pi.getContent());
//								publisherList.add(pim);
//							}
//						}
//
//						authorJson.put("publisher_info", publisherList);
//					}
//					storyModel.setAuthor(authorJson);
//					int count = this.commentDao.getCommentCountById((Long) story.getId());
//					int repost_count = republishDao.count(story.getId());
//					storyModel.setRepost_count(repost_count);
//					storyModel.setComment_count(count);
//					storyModel.setCreated_time(story.getCreated_time());
//					Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
//					if (repost != null)
//						storyModel.setRepost_by_current_user(true);
//					else {
//						storyModel.setRepost_by_current_user(false);
//					}
//					/*Collection collection = this.collectionStoryDao.getCollectionByStoryId(storyId);
//					if (collection != null) {
//						CollectionIntro ci = new CollectionIntro();
//						ci.setId((Long) collection.getId());
//						ci.setCollection_name(collection.getCollectionName());
//						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
//						ci.setInfo(collection.getInfo());
//
//						User author = userDao.get(collection.getUser().getId());
//						JSONObject json = new JSONObject();
//						json.put("id", author.getId());
//						json.put("username", author.getUsername());
//						ci.setAuthor(json);
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						JSONObject collectionJson = null;
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							collectionJson = JSONObject.fromObject(ci, configs);
//						} else {
//							collectionJson = JSONObject.fromObject(ci);
//						}
//
//						storyModel.setCollection(collectionJson);
//					}*/
//					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
//					log.debug("***story.getCover_page()***" + jsonObject);
//					String type = jsonObject.getString("type");
//
//					if (type.equals("text")) {
//						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
//						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
//						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//					} else if (type.equals("image")) {
//						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
//						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//					} else if (type.equals("multimedia")) {
//						storyModel.setCover_media(jsonObject);
//					}
//
//					storyModel.setTitle(story.getTitle());
//					storyModel.setSummary(story.getSummary());
//					JsonConfig configs = new JsonConfig();
//					List<String> delArray1 = new ArrayList<String>();
//					if (Strings.isNullOrEmpty(story.getTitle())) {
//						delArray1.add("title");
//					}
//
//					if (Strings.isNullOrEmpty(story.getSummary())) {
//						delArray1.add("summary");
//					}
//					JSONObject storyJson = null;
//					if ((delArray1 != null) && (delArray1.size() > 0)) {
//						configs.setExcludes((String[]) delArray1.toArray(new String[delArray1.size()]));
//						configs.setIgnoreDefaultExcludes(false);
//						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//						storyJson = JSONObject.fromObject(storyModel, configs);
//					} else {
//						storyJson = JSONObject.fromObject(storyModel);
//					}
//					contentJson.put("story", storyJson);
//				}
//
//				if (timeline.getType().equals("post")) {
//					event.setContent(contentJson);
//				} else if (timeline.getType().equals("repost")) {
//					UserIntro userIntro = new UserIntro();
//					User user = (User) this.userDao.get(timeline.getCreatorId());
//					if (user != null) {
//						userIntro.setId((Long) user.getId());
//						userIntro.setUsername(user.getUsername());
//						userIntro.setIntroduction(user.getIntroduction());
//						userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
//						userIntro.setUser_type(user.getUser_type());
//						contentJson.put("repost_by", userIntro);
//					}
//					event.setContent(contentJson);
//				}
//			}
//
//			return event;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return null;
//	}

//	public Response reportStory(Long storyId, Long loginUserid) {
//		Story story = (Story) this.storyDao.get(storyId);
//		Report report = this.reportDao.getReportByStoryIdAndUserId(storyId, loginUserid);
//		JSONObject json = new JSONObject();
//		if (report != null) {
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		if (story != null) {
//			Report r = new Report();
//			r.setSender_id(loginUserid);
//			r.setRecipient_id((Long) story.getUser().getId());
//			r.setType("report_story");
//			r.setObject_type(1);
//			r.setObject_id((Long) story.getId());
//			r.setStatus("new");
//			this.reportDao.save(r);
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "no_resource");
//		json.put("code", Integer.valueOf(10012));
//		json.put("error_message", "The story does not exist");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}

//	public List<UserIntro> getLikeStoryUserByStoryId(Long storyId, HttpServletRequest request, Long loginUserid) {
//		String countStr = request.getParameter("count");
//		String sinceIdStr = request.getParameter("since_id");
//		String maxIdStr = request.getParameter("max_id");
//
//		List<UserIntro> userLikeList = new ArrayList<UserIntro>();
//		UserIntro userIntro = null;
//		int count = 20;
//
//		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			List<User> userList = this.userDao.getUsersByStoryIdAndNull(storyId, count);
//			if ((userList != null) && (userList.size() > 0))
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userLikeList.add(userIntro);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			List<User> userList = this.userDao.getUsersByStoryIdAndNull(storyId, count);
//			if ((userList != null) && (userList.size() > 0))
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userLikeList.add(userIntro);
//				}
//		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			Likes like = this.likesDao.getLikeByUserIdAndStoryId(since_id, storyId);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String createTime = sdf.format(new Date(like.getCreateTime().longValue()));
//			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) like.getId(), createTime,
//					count, 1);
//			if ((userList != null) && (userList.size() > 0))
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userLikeList.add(userIntro);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			Likes like = this.likesDao.getLikeByUserIdAndStoryId(since_id, storyId);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String createTime = sdf.format(new Date(like.getCreateTime().longValue()));
//			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) like.getId(), createTime,
//					count, 1);
//			if ((userList != null) && (userList.size() > 0))
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userLikeList.add(userIntro);
//				}
//		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			Likes like = this.likesDao.getLikeByUserIdAndStoryId(max_id, storyId);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String createTime = sdf.format(new Date(like.getCreateTime().longValue()));
//			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) like.getId(), createTime,
//					count, 2);
//			if ((userList != null) && (userList.size() > 0))
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userLikeList.add(userIntro);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			Likes like = this.likesDao.getLikeByUserIdAndStoryId(max_id, storyId);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String createTime = sdf.format(new Date(like.getCreateTime().longValue()));
//			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) like.getId(), createTime,
//					count, 2);
//			if ((userList != null) && (userList.size() > 0)) {
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userLikeList.add(userIntro);
//				}
//
//			}
//
//		}
//
//		return userLikeList;
//	}

//	public List<UserIntro> getRepostStoryUserByStoryId(Long storyId, HttpServletRequest request, Long loginUserid) {
//		String countStr = request.getParameter("count");
//		String sinceIdStr = request.getParameter("since_id");
//		String maxIdStr = request.getParameter("max_id");
//		List<UserIntro> userRepostList = new ArrayList<UserIntro>();
//		UserIntro userIntro = null;
//		int count = 20;
//
//		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			List<User> userList = this.userDao.getRepostUsersByStoryId(storyId, count);
//			if ((userList != null) && (userList.size() > 0))
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userRepostList.add(userIntro);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			List<User> userList = this.userDao.getRepostUsersByStoryId(storyId, count);
//			if ((userList != null) && (userList.size() > 0))
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userRepostList.add(userIntro);
//				}
//		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			Republish r = this.republishDao.getRepostByUserIdAndStoryId(since_id, storyId);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String createTime = sdf.format(new Date(r.getCreateTime().longValue()));
//			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) r.getId(), createTime, count,
//					1);
//			if ((userList != null) && (userList.size() > 0))
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userRepostList.add(userIntro);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			Republish r = this.republishDao.getRepostByUserIdAndStoryId(since_id, storyId);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String createTime = sdf.format(new Date(r.getCreateTime().longValue()));
//			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) r.getId(), createTime, count,
//					1);
//			if ((userList != null) && (userList.size() > 0))
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userRepostList.add(userIntro);
//				}
//		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			Republish r = this.republishDao.getRepostByUserIdAndStoryId(max_id, storyId);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String createTime = sdf.format(new Date(r.getCreateTime().longValue()));
//			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) r.getId(), createTime, count,
//					2);
//			if ((userList != null) && (userList.size() > 0))
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userRepostList.add(userIntro);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			Republish r = this.republishDao.getRepostByUserIdAndStoryId(max_id, storyId);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String createTime = sdf.format(new Date(r.getCreateTime().longValue()));
//			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) r.getId(), createTime, count,
//					2);
//			if ((userList != null) && (userList.size() > 0)) {
//				for (User u : userList) {
//					userIntro = getUserIntro(u, loginUserid);
//					userRepostList.add(userIntro);
//				}
//			}
//		}
//
//		return userRepostList;
//	}

//	public Response reportComment(Long commentId, Long storyId, Long loginUserid) {
//		Comment comment = (Comment) this.commentDao.get(commentId);
//		Report report = this.reportDao.getReportByCommentIdAndUserId(commentId, loginUserid);
//		JSONObject json = new JSONObject();
//		if (report != null) {
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		if (comment != null) {
//			Report r = new Report();
//			r.setSender_id(loginUserid);
//			r.setRecipient_id((Long) comment.getUser().getId());
//			r.setType("report_comment");
//			r.setObject_type(2);
//			r.setObject_id((Long) comment.getId());
//			r.setStatus("new");
//			this.reportDao.save(r);
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "no_resource");
//		json.put("code", Integer.valueOf(10053));
//		json.put("error_message", "The comment does not exist");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}



	public String lastWeek() {
		Date date = new Date();
		int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
		int month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
		int day = Integer.parseInt(new SimpleDateFormat("dd").format(date)) - 3;

		if (day < 1) {
			month -= 1;
			if (month == 0) {
				year -= 1;
				month = 12;
			}
			if (month == 4 || month == 6 || month == 9 || month == 11) {
				day = 30 + day;
			} else
				if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
				day = 31 + day;
			} else if (month == 2) {
				if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
					day = 29 + day;
				else
					day = 28 + day;
			}
		}
		String y = year + "";
		String m = "";
		String d = "";
		if (month < 10)
			m = "0" + month;
		else
			m = month + "";
		if (day < 10)
			d = "0" + day;
		else
			d = day + "";

		return y + "-" + m + "-" + d;
	}

	public JSONObject parseJson(String path) {
		String sets = ReadFile(path);
		JSONObject jo = JSONObject.fromObject(sets);
		return jo;
	}

	public String ReadFile(String path) {
		File file = new File(path);
		BufferedReader reader = null;
		String laststr = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;

			while ((tempString = reader.readLine()) != null) {
				laststr = laststr + tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();

			if (reader != null)
				try {
					reader.close();
				} catch (IOException localIOException1) {
				}
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException localIOException2) {
				}
		}
		return laststr;
	}


	@Override
	public Response addResource(Long storyId, String resource) {
		JSONObject json = new JSONObject();
		if (storyId != null && !Strings.isNullOrEmpty(resource)) {
			try {
				resource = URLDecoder.decode(resource, "UTF-8");
				System.out.println("resource-->" + resource);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			storyDao.updateStoryResource(storyId, resource);
			json.put("status", "success");
			return Response.status(Response.Status.OK).entity(json).build();
		} else {
			json.put("status", "invalid_request");
			json.put("code", Integer.valueOf(10010));
			json.put("error_message", "Invalid request payload");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
	}

	@Override
	public Response commentcounts(Long storyId) {
		JSONObject json = new JSONObject();
		if (storyId != null && storyId > 0) {
			int count = commentDao.getCommentCountById(storyId);
			json.put("comment_count", count);
			return Response.status(Response.Status.OK).entity(json).build();
		} else {
			json.put("status", "no_resource");
			json.put("code", 10015);
			json.put("error_message", "The comment counts does not exist");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}

	}

	@Override
	public Response comment_counts(Long storyId) {
		JSONObject json = new JSONObject();
		if (storyId != null && storyId > 0) {
			int count = commentDao.getCommentCountById(storyId);
			json.put("comment_count", count);
			return Response.status(Response.Status.OK).entity(json).build();
		} else {
			json.put("status", "no_resource");
			json.put("code", 10015);
			json.put("error_message", "The comment counts does not exist");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}

	}

	public String publicParam(Map<String,String> param) throws Exception{
		FBEncryption fb = new FBEncryption("20161206100527xEhf0s8Vj3j_uSMrI1eOHU--8k1LwR0o", 
				"20161206100534Z_qxIRa6BBMophlaZMNwSVwJMGmL_ptB");
		
		param.put("channel","40");
		Map<String,String> map = fb.signature(param);
		boolean bool = fb.checkSignature(map);
		System.out.println("bool--->>>"+bool);
		Set<String> keys = map.keySet();
		Iterator<String> iter = keys.iterator();
		StringBuffer sb = new StringBuffer();
		while(iter.hasNext()){
			String key = iter.next();
			sb.append(key+"="+map.get(key)+"&");
		}
		String res = sb.toString();
		String result = res.substring(0,res.length()-1);
		return result;
	}

	@Override
	public List<UserIntro> getLikeStoryUserByStoryId(Long paramLong1, HttpServletRequest paramHttpServletRequest,
			Long paramLong2) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public Response getFbInvitation(Long loginUserid, String fbToken,JSONObject invitation) throws Exception {
//		//e������url
//		String content = getClass().getResource("/../../META-INF/fb.json").getPath();
//		JSONObject contentJson = ParseFile.parseJson(content);
//		String url = contentJson.getString("url");
//		
//		String authcode = fbToken;
//		Long tid = invitation.getLong("tid");
//		Long pid = invitation.getLong("pid");
//		invitation.put("authcode", authcode);
//		invitation.put("reqTime",new Date().getTime());
//		invitation.put("serviceName", "fd_viewthread");
//		JSONObject invitation_copy = JSONObject.fromObject(invitation.toString());
//		System.out.println("tag-->"+invitation);
//		String splitSign = HttpUtil.splitJSONCopy(invitation);
//		String privateKey = HttpUtil.getPrivateKey(new Date());
//		splitSign = splitSign + "&private_key=" +privateKey;
//		System.out.println("splitSign-->"+splitSign);
//		String sign = HttpUtil.getMD5Str(splitSign);
//		System.out.println("sign-->"+sign);
//		invitation_copy.put("sign", sign);
//		System.out.println("tag-->>>>>"+invitation_copy.toString());
//		String responseData = HttpUtil.sendPostStr(url+"bbsapinew/fd_viewthread.php", invitation_copy.toString());
//		JSONObject responseJson = JSONObject.fromObject(responseData);
//		JSONObject rspInfo = responseJson.getJSONObject("rspInfo");
//		
//		if(rspInfo.getInt("rspCode") == 1000){
//			JSONObject story = new JSONObject();
//			Story storyObj = storyDao.getStoryByTidAndPid(tid,pid);
//			JSONObject rspData = responseJson.getJSONObject("rspData");
//			JSONObject postinfo = rspData.getJSONObject("postinfo");
//			if(postinfo.containsKey("subject")){
//				String subject = postinfo.getString("subject");
//				if(!Strings.isNullOrEmpty(subject)){
//					story.put("title", subject);
//				}
//			}
//			if(postinfo.containsKey("new_message")){
//				Object obj = postinfo.get("new_message");
//				if(obj != null){
//					JSONObject elementJson = JSONObject.fromObject(obj);
//					JSONArray elementArr = JSONArray.fromObject(elementJson.get("elements"));
//					JSONObject cover = JSONObject.fromObject(storyObj.getCover_page());
//					if(cover.getString("type").equals("image")){
//						JSONObject coverJson = JSONObject.fromObject(elementArr.get(0));
//						story.put("cover_media", JSONObject.fromObject(coverJson.get("content")));
//						elementArr.remove(0);
//						story.put("elements", elementArr);
//					}else{
//						story.put("cover_media", JSONObject.fromObject(storyObj.getCover_page()));
//						story.put("elements", elementArr);
//					}
//					
//				}
//			}
//			
//			story.put("id", storyObj.getId());
//			story.put("tid", rspData.getInt("tid"));
//			story.put("pid", rspData.getInt("pid"));
//			story.put("fid", rspData.getInt("fid"));
//			return Response.status(Response.Status.OK).entity(story).build();
//		}else{
//			JSONObject jo = new JSONObject();
//			jo.put("status", rspInfo.getString("rspType"));
//			jo.put("code", rspInfo.getString("rspCode"));
//			jo.put("error_message", rspInfo.getString("rspDesc"));
//			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
//		}
//	}

//	public Response createLike(JSONObject json, HttpServletRequest request) {
//		if(json != null){
//			Long story_id = json.getLong("story_id");
//			int centre_id = json.getInt("user_id");
//			String recognition_type = json.getString("recognition_type");
//			UserCentre uc = userCentreDao.getUserCentreByCentreId(centre_id);
//			Long user_id = 0l;
//			if(uc == null){
//				User u = new User();
//				String chars = "abcde0f12g3hi4jk5l6m7n8o9pqrstuvwxyz";
//		    	StringBuffer sb = new StringBuffer();
//		    	for(int i=0;i<10;i++){
//		    		char c = chars.charAt((int)(Math.random() * 10));
//		    		sb.append(c);
//		    	}
//		    	u.setPassword(sb.toString());
//		    	u.setUsername("username");
//		    	u.setSalt(initSalt().toString());
//		    	u.setStatus("enabled");
//		    	u.setUser_type("normal");
//				
//		    	this.userDao.save(u);
//		    	user_id = u.getId();
//		    	UserCentre userc = new UserCentre();
//		    	userc.setCentre_id(centre_id);
//		    	userc.setUser_id(u.getId());
//				userCentreDao.save(userc);
//			}else{
//				user_id = uc.getUser_id();
//			}
//			
//			
//			if(recognition_type.equals("-1")){
//				likesDao.deleteLike(user_id, story_id);
//				JSONObject jo = new JSONObject();
//				jo.put("code", 10000);
//				jo.put("msg", "�ɹ�");
//				
//				return Response.status(Response.Status.OK).entity(jo).build();
//			}else if(recognition_type.equals("10")){
//				String path = getClass().getResource("/../../META-INF/getui.json").getPath();
//				JSONObject jsonObject = ParseFile.parseJson(path);
//				String appId = jsonObject.getString("appId");
//				String appKey = jsonObject.getString("appKey");
//				String masterSecret = jsonObject.getString("masterSecret");
//				User user = (User) this.userDao.get(user_id);
//				Story story = (Story) this.storyDao.get(story_id);
//				Likes likes = new Likes();
//				likes.setCreateTime(new Date());
//				likes.setLike_users(user);
//				likes.setLike_story(story);
//				this.likesDao.save(likes);
//				Notification notification = this.notificationDao.getNotificationByAction(story_id, user_id, 1, 2);
//				if (notification != null) {
//					notification.setCreate_at(new Date());
//					this.notificationDao.update(notification);
//				} else {
//					notification = new Notification();
//					notification.setSenderId(user_id);
//					notification.setRecipientId((Long) story.getUser().getId());
//					notification.setNotificationType(2);
//					notification.setObjectType(1);
//					notification.setObjectId(story_id);
//					notification.setRead_already(false);
//					notification.setStatus("enabled");
//					this.notificationDao.save(notification);
//				}
//				Configuration conf = this.configurationDao.getConfByUserId((Long) story.getUser().getId());
//				if (conf.isNew_favorite_from_following_push()) {
//					int counts = this.notificationDao.getNotificationByRecipientId((Long) story.getUser().getId());
//					List<PushNotification> list = this.pushNotificationDao
//							.getPushNotificationByUserid((Long) story.getUser().getId());
//					try {
//						String content = user.getUsername() + "ϲ�����ҵĹ���";
//						JSONObject json1 = new JSONObject();
//						json1.put("story_id", story.getId());
//						PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content, json1.toString());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//
//				JSONObject jo = new JSONObject();
//				jo.put("code", 10000);
//				jo.put("msg", "�ɹ�");
//				
//				return Response.status(Response.Status.CREATED).entity(jo).build();
//			}else{
//				return null;
//			}
//		}else{
//			JSONObject jo = new JSONObject();
//			jo.put("code", 10003);
//			jo.put("msg", "��������");
//			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
//		}
//		
//		
//	
//	}
	
	public String carPublicParam(Map<String,String> param) throws Exception{
		FBEncryption fb = new FBEncryption("20170613101453sXY83vwjIqH1v23xK3yUY84b-X5Vy5YP", 
				"20170613101501JKRhtzWDpeqZqww8bfHyRenvUwPHnHYc");
		
		param.put("channel","30");
		Map<String,String> map = fb.signature(param);
		boolean bool = fb.checkSignature(map);
		System.out.println("bool--->>>"+bool);
		Set<String> keys = map.keySet();
		Iterator<String> iter = keys.iterator();
		StringBuffer sb = new StringBuffer();
		while(iter.hasNext()){
			String key = iter.next();
			sb.append(key+"="+map.get(key)+"&");
		}
		String res = sb.toString();
		String result = res.substring(0,res.length()-1);
		return result;
	}

	
	
	

}
