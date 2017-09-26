package com.friend.rest.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import com.friend.rest.ApiHttpClient;
import com.friend.rest.common.EncryptionUtil;
import com.friend.rest.common.FBEncryption;
import com.friend.rest.common.HttpUtil;
import com.friend.rest.common.ParseFile;
import com.friend.rest.common.PushNotificationUtil;
import com.friend.rest.dao.CollectionDao;
import com.friend.rest.dao.CollectionStoryDao;
import com.friend.rest.dao.CommentDao;
import com.friend.rest.dao.ConfigurationDao;
import com.friend.rest.dao.DistrictsDao;
import com.friend.rest.dao.FeatureUserDao;
import com.friend.rest.dao.FeedbackDao;
import com.friend.rest.dao.NotificationDao;
import com.friend.rest.dao.PushNotificationDao;
import com.friend.rest.dao.RepublishDao;
import com.friend.rest.dao.SlideDao;
import com.friend.rest.dao.StoryDao;
import com.friend.rest.dao.TimelineDao;
import com.friend.rest.dao.UserDao;
import com.friend.rest.model.Collection;
import com.friend.rest.model.CollectionStory;
import com.friend.rest.model.Comment;
import com.friend.rest.model.Configuration;
import com.friend.rest.model.Districts;
import com.friend.rest.model.FeatureUser;
import com.friend.rest.model.Feedback;
import com.friend.rest.model.FormatType;
import com.friend.rest.model.Notification;
import com.friend.rest.model.PublisherInfo;
import com.friend.rest.model.PushNotification;
import com.friend.rest.model.Republish;
import com.friend.rest.model.SdkHttpResult;
import com.friend.rest.model.Slide;
import com.friend.rest.model.Story;
import com.friend.rest.model.StoryElement;
import com.friend.rest.model.Timeline;
import com.friend.rest.model.User;
import com.friend.rest.service.model.CollectionIntro;
import com.friend.rest.service.model.CollectionIntroLast;
import com.friend.rest.service.model.CollectionIntros;
import com.friend.rest.service.model.CommentSummaryModel;
import com.friend.rest.service.model.CoverMedia;
import com.friend.rest.service.model.EventModel;
import com.friend.rest.service.model.GetuiModel;
import com.friend.rest.service.model.ImageCover;
import com.friend.rest.service.model.LinkModel;
import com.friend.rest.service.model.LinkModels;
import com.friend.rest.service.model.LocationModel;
import com.friend.rest.service.model.MailSenderInfo;
import com.friend.rest.service.model.PasswordModel;
import com.friend.rest.service.model.PublisherInfoModel;
import com.friend.rest.service.model.SimpleMailSender;
import com.friend.rest.service.model.SlideModel;
import com.friend.rest.service.model.StoryEvent;
import com.friend.rest.service.model.StoryEventNew;
import com.friend.rest.service.model.StoryHome;
import com.friend.rest.service.model.StoryHomeCopy;
import com.friend.rest.service.model.StoryIntro;
import com.friend.rest.service.model.StoryPageModel;
import com.friend.rest.service.model.TextCover;
import com.friend.rest.service.model.UserCollectionModel;
import com.friend.rest.service.model.UserFeatur;
import com.friend.rest.service.model.UserIntro;
import com.friend.rest.service.model.UserModel;
import com.friend.rest.service.model.UserParentModel;
import com.friend.rest.service.model.UserPhone;
import com.friend.rest.service.model.UserPublisherModel;
import com.google.common.base.Strings;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@Transactional
public class UserServiceImpl implements UserService {
	private static final Log log = LogFactory.getLog(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private TimelineDao timelineDao;

	@Autowired
	private StoryDao storyDao;

	@Autowired
	private RepublishDao republishDao;


	@Autowired
	private ConfigurationDao configurationDao;


	@Autowired
	private NotificationDao notificationDao;

	@Autowired
	private CommentDao commentDao;


	@Autowired
	private PushNotificationDao pushNotificationDao;

	@Autowired
	private CollectionDao collectionDao;
	
	@Autowired
	private FeedbackDao feedbackDao;

	@Autowired
	private CollectionStoryDao collectionStoryDao;
	
	@Autowired
	private SlideDao slideDao;
	
	@Autowired
	private FeatureUserDao featureUserDao;
	
	

	public Response create(JSONObject user,String appVersion,HttpServletRequest request) {
		int centre_id = 0;
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = parseJson(urlkey);
		String url = jsonObject.getString("url");
		String car_url = jsonObject.getString("car_url");
		String device = request.getHeader("X-Tella-Request-Device");
		if(!Strings.isNullOrEmpty(device)){
			device = "20";
		}
		String fbToken = "";
		String script = "";
		String username = "";
		String avatar_image = "";
		try {
			if (user != null) {
				if (Strings.isNullOrEmpty(user.getString("username").trim())) {
					JSONObject jo = new JSONObject();
					jo.put("status", "username_null");
					jo.put("code",10032);
					jo.put("error_message", "Username is not null");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}
				if (user.containsKey("phone")) {
					JSONObject resp = new JSONObject();
					String zone = user.get("zone").toString();
					String phone = user.get("phone").toString();
					String code = user.get("code").toString();
				    String user_name = user.get("username").toString();
					if ((!Strings.isNullOrEmpty(zone)) && (!Strings.isNullOrEmpty(phone))
							&& (!Strings.isNullOrEmpty(code))) {
						Map<String,String> param = new HashMap<String,String>();
						param.put("mobile", phone);
						param.put("code", code);
						param.put("ip",ip);
						String regParams = publicParam(param);
						String result = HttpUtil.sendPostStr(url+"/customer/account/check-mobile-for-register", regParams);
						
						if (!Strings.isNullOrEmpty(result)) {
							JSONObject json = JSONObject.fromObject(result);
							int status = json.getInt("code");
							if (status == 10000) {
								Map<String,String> registerParam = new HashMap<String, String>();
								registerParam.put("mobile", phone);
								registerParam.put("code", code);
								if(!Strings.isNullOrEmpty(user_name)){
									registerParam.put("username", user_name);
								}else{
									registerParam.put("username", "fd_"+phone);
								}
								
								registerParam.put("password", user.getString("password"));
								registerParam.put("ip",ip);
								registerParam.put("device",device);
								
								String params = publicParam(registerParam);
								String register_result = HttpUtil.sendPostStr(url+"/customer/account/register-and-login", params);
								JSONObject reg_res_json = JSONObject.fromObject(register_result);
								int res_code = reg_res_json.getInt("code");
								if(res_code == 10000){
									JSONObject data = reg_res_json.getJSONObject("data");
									centre_id = data.getInt("userid");
									fbToken = data.getString("token");
									username = data.getString("username");
									avatar_image = data.getString("header_picture_small");
									if(device.equals("10")){
										script = data.getString("script");
									}
									Map<String,String> user_map = new HashMap<String, String>();
									user_map.put("ip", ip);
									user_map.put("device", device);
									user_map.put("user_id", String.valueOf(centre_id));
									user_map.put("user_name", username);
									String user_params = carPublicParam(user_map);
									String user_result = HttpUtil.sendPostStr(car_url+"/member/create", user_params);
									if(!Strings.isNullOrEmpty(user_result)){
										JSONObject user_json = JSONObject.fromObject(user_result);
										int user_code = user_json.getInt("code");
									}
									
								}
								
							} else if(status == 10001) {	
								resp.put("status", "缺少参数");
								resp.put("code", 10600);
								resp.put("error_message", "缺少参数");
								return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
							}else if(status == 10110) {	
								resp.put("status", "手机号格式错误");
								resp.put("code", 10601);
								resp.put("error_message", "手机号格式错误");
								return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
							}else if(status == 10111) {	
								resp.put("status", "该手机号已注册");
								resp.put("code", 10602);
								resp.put("error_message", "该手机号已注册");
								return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
							}else if(status == 10112) {	
								resp.put("status", "验证码不正确");
								resp.put("code", 10603);
								resp.put("error_message", "验证码不正确");
								return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
							}else if(status == 10114) {	
								resp.put("status", "用户名已存在");
								resp.put("code", 10628);
								resp.put("error_message", "用户名已存在");
								return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
							}
						} else {
							resp.put("status", "用户中心报错");
							resp.put("code", 10604);
							resp.put("error_message", "用户中心报错");
							return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
						}
					} else {
						resp.put("status", "request_invalid");
						resp.put("code", Integer.valueOf(10010));
						resp.put("error_message", "request is invalid");
						return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
					}

				}


//				Configuration c = new Configuration();
//				c.setNew_admin_push(true);
//				c.setNew_comment_on_your_comment_push(true);
//				c.setNew_comment_on_your_story_push(true);
//				c.setNew_favorite_from_following_push(true);
//				c.setNew_follower_push(true);
//				c.setNew_story_from_following_push(true);
//				c.setRecommended_my_story_push(true);
//				c.setReposted_my_story_push(true);
//				
//				c.setUserId((Long) u.getId());
				
//				this.configurationDao.save(c);
//				List<User> userList = this.userDao.getUserByUserType();
//				List<PushNotification> pnList = new ArrayList<PushNotification>();
//				Notification n = null;
//				List<Notification> notificationList = new ArrayList<Notification>();
//				Configuration conf;
//				if ((userList != null) && (userList.size() > 0)) {
//					for (User admin : userList) {
//						n = new Notification();
//						n.setRecipientId((Long) admin.getId());
//						n.setSenderId((Long) u.getId());
//						n.setNotificationType(8);
//						n.setObjectType(3);
//						n.setObjectId((Long) admin.getId());
//						n.setStatus("enabled");
//						n.setRead_already(true);
//						notificationList.add(n);
//						conf = this.configurationDao.getConfByUserId((Long) admin.getId());
//						if (conf.isNew_admin_push()) {
//							List<PushNotification> list = this.pushNotificationDao
//									.getPushNotificationByUserid(admin.getId());
//							pnList.addAll(list);
//						}
//					}
//				}
//				this.notificationDao.saveNotifications(notificationList);
//				Map<String, Integer> map = new HashMap<String, Integer>();
//				if ((pnList != null) && (pnList.size() > 0)) {
//					for (PushNotification pn : pnList) {
//						int count = this.notificationDao.getNotificationByRecipientId(pn.getUserId());
//						map.put(pn.getClientId(), Integer.valueOf(count));
//					}
//				}
//				String content = u.getUsername() + "注册了";
//				JSONObject json = new JSONObject();
//				json.put("user_id",u.getId());
//				PushNotificationUtil.pushInfoAllFollow(gm.getAppId(), gm.getAppKey(), gm.getMasterSecret(), pnList, map, content,json.toString());
				
				if (user.containsKey("link_account")) {
					JSONObject link = JSONObject.fromObject(user.get("link_account"));
					
					Map<String,String> param = new HashMap<String, String>();
					
//					param.put("third_party_openid", la.getUnion_id().toString());
					param.put("device", device);
					param.put("ip", ip);
//					String thried = la.getService();
					
//					if(thried.equals("wechat")){
//						param.put("third_party_channel", "10");
//					}else if(thried.equals("weibo")){
//						param.put("third_party_channel", "20");
//					}
					String params = null;
					try {
						params = publicParam(param);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String result = HttpUtil.sendPostStr(url+"/customer/account/login-third-party-beegree", params);
					JSONObject resp = new JSONObject();
					if (!Strings.isNullOrEmpty(result)) {
						JSONObject res_json = JSONObject.fromObject(result);
						int status = res_json.getInt("code");
						if (status == 10000) {
							JSONObject data = res_json.getJSONObject("data");
							int centreid = data.getInt("userid");
							String fb_token = data.getString("token");
							username = data.getString("username");
							avatar_image = data.getString("header_picture_small");
							resp.put("userid", centreid);
							resp.put("access_token", fb_token);
							resp.put("username", username);
							resp.put("avatar_image", avatar_image);
							return Response.status(Response.Status.OK).entity(resp).build();
						} else if(status == 10001) {	
							resp.put("status", "缺少参数");
							resp.put("code", 10600);
							resp.put("error_message", "缺少参数");
							return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
						}else if(status == 10110) {	
							resp.put("status", "手机号格式错误");
							resp.put("code", 10601);
							resp.put("error_message", "手机号格式错误");
							return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
						}else if(status == 10111) {	
							resp.put("status", "该手机号已注册");
							resp.put("code", 10602);
							resp.put("error_message", "该手机号已注册");
							return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
						}else if(status == 10112) {	
							resp.put("status", "验证码不正确");
							resp.put("code", 10603);
							resp.put("error_message", "验证码不正确");
							return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
						}
					} else {
						resp.put("status", "用户中心报错");
						resp.put("code", 10604);
						resp.put("error_message", "用户中心报错");
						return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
					}
				}

				System.out.println("create successful");
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("status", "invalid request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "请求参数有误");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}

		JSONObject json = new JSONObject();
		
		json.put("userid", centre_id);
		json.put("access_token", fbToken);
		json.put("username",username);
		if(!Strings.isNullOrEmpty(device) 
				&& device.equals("10") &&
				!Strings.isNullOrEmpty(script)){
			json.put("script",script);
		}
		if(!Strings.isNullOrEmpty(avatar_image)){
			json.put("avatar_image",avatar_image);
		}
		return Response.status(Response.Status.CREATED).entity(json).build();
	}

	public Response get(Long userId, Long loginUserid,String appVersion,HttpServletRequest request)throws Exception {
		JSONObject jo = new JSONObject();
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(!Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		if(loginUserid != null && loginUserid > 0){

			String car_path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
			JSONObject urlJson = ParseFile.parseJson(car_path);
			String car_url = urlJson.getString("car_url");
			Map<String,String> map = new HashMap<String, String>();
			map.put("ip", ip);
			map.put("device", device);
			map.put("login_id", loginUserid.toString());
			map.put("id", userId.toString());
			map.put("add_view","0");
			String params = carPublicParam(map);
			String result = HttpUtil.sendGetStr(car_url+"/member/get", params);
			if(!Strings.isNullOrEmpty(result)){

				JSONObject resJson = JSONObject.fromObject(result);
				int code = resJson.getInt("code");
				if(code == 10000){
					JSONObject data = resJson.getJSONObject("data");
					data.put("user_type", "normal");
					return Response.status(Response.Status.OK).entity(data).build();
				}else if(code == 10001){
					jo.put("status", "缺少参数");
					jo.put("code", 10600);
					jo.put("error_message", "缺少参数");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 11001){
					jo.put("status", "用户不存在");
					jo.put("code", 10650);
					jo.put("error_message", "用户不存在");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}
			
			}
			JSONObject json = new JSONObject();
			json.put("status", "no_resource");
			json.put("code", Integer.valueOf(10011));
			json.put("error_message", "The user does not exist");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		
		}
		


		JSONObject json = new JSONObject();
		json.put("status", "no_resource");
		json.put("code", Integer.valueOf(10011));
		json.put("error_message", "The user does not exist");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}
	
	
	public UserParentModel getUser(Long userId, Long loginUserid) {
		log.debug("****get user function**********");
		log.debug("*** loginUserid ****" + loginUserid + " *** userId" + userId);
		
		User user = null;
		String website = null;
		

		user = (User) this.userDao.get(userId);
		if (user != null) {
			int repostCount = 0;//this.republishDao.userRepostCount(userId);
			Set<Story> sSet = user.getRepost_story();
			if(sSet != null && sSet.size() > 0){
				repostCount = sSet.size();
			}
			
			int storyCount = this.storyDao.getStoryCount((Long) user.getId());
			boolean followed_by_current_user = false;
			boolean is_following_current_user = false;
			
			JSONObject avatarImageJson = null;
			if ((user.getAvatarImage() != null) && (!user.getAvatarImage().equals(""))) {
				avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
			}

			JSONObject coverImageJson = null;
			if ((user.getCoverImage() != null) && (!user.getCoverImage().equals(""))) {
				coverImageJson = JSONObject.fromObject(user.getCoverImage());
			}

			if (!Strings.isNullOrEmpty(user.getWebsite())) {
				website = user.getWebsite();
			}

			/*if (!Strings.isNullOrEmpty(user.getProfile_url())) {
				profileUrl = user.getProfile_url();
			}*/
			Set<Collection> followed_collection = user.getCollections();
			List<CollectionIntroLast> followed_collection_info = new ArrayList<CollectionIntroLast>();
			CollectionIntroLast cis = null;
			if(followed_collection != null 
					&& followed_collection.size() > 0){
				Iterator<Collection> it = followed_collection.iterator();
				while(it.hasNext()){
					Collection collections = it.next();

					cis = new CollectionIntroLast();
//					cis.setId(collections.getId());
//					cis.setCollection_name(collections.getCollectionName());
					cis.setCover_image(JSONObject.fromObject(collections.getCover_image()));
//					cis.setInfo(collections.getInfo());
					Set<User> uSet = null;//collections.getUsers();
					Iterator<User> its = uSet.iterator();
					if(its.hasNext()){
						boolean flag = false;
						while(its.hasNext()){
							User u = its.next();
							if(u.getId().equals(loginUserid) 
									&& u.getId() == loginUserid){
								flag = true;
								break;
							}
						}
						cis.setIs_followed_by_current_user(flag);
					}else{
						cis.setIs_followed_by_current_user(false);
					}
					followed_collection_info.add(cis);
				
				}
			}
			
			UserParentModel userModel = null;
			if (user.getUser_type().equals("normal") || user.getUser_type().equals("vip") || user.getUser_type().equals("admin")
					|| user.getUser_type().equals("super_admin") || user.getUser_type().equals("official")) {
				userModel = new UserCollectionModel((Long) user.getId(), user.getUsername(), user.getEmail(),
						user.getCreated_time(), user.getStatus(), user.getIntroduction(), avatarImageJson,
						coverImageJson,  repostCount, storyCount, 0, 0, website,
						followed_by_current_user, is_following_current_user,  user.getUser_type(),user.getGender(),followed_collection_info);
			} else if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
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
				userModel = new UserPublisherModel((Long) user.getId(), user.getUsername(), user.getEmail(),
						user.getCreated_time(), user.getStatus(), user.getIntroduction(), avatarImageJson,
						coverImageJson,  repostCount, storyCount, 0, 0, website,
						followed_by_current_user, is_following_current_user,  user.getUser_type(),
						publisherList,user.getGender());
			}

			log.debug("****get user**********" + JSONObject.fromObject(userModel));
			log.debug("*** loginUserid ****" + loginUserid + " *** userId" + userId);
			return userModel;
		}

		JSONObject json = new JSONObject();
		json.put("status", "no_resource");
		json.put("code", Integer.valueOf(10011));
		json.put("error_message", "The user does not exist");
		return null;
	}
	
	

	public Response userLogin(JSONObject userJson,HttpServletRequest request)throws Exception {
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String email = userJson.containsKey("email")?userJson.getString("email"):null;
		String fbname = userJson.containsKey("fbname")?userJson.getString("fbname"):null;
		String password = userJson.containsKey("password")?userJson.getString("password"):null;
		String zone = userJson.containsKey("zone")?userJson.getString("zone"):null;
		String phone = userJson.containsKey("phone")?userJson.getString("phone"):null;
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String url = jsonObject.getString("url");
		String car_url = jsonObject.getString("car_url");
		JSONObject jo = new JSONObject();
		JSONObject auth = new JSONObject();
		int centre_id = 0;
		String fbToken = "";
		String script = "";
		String fbusername = "";
		String device = String.valueOf(userJson.get("device"));
		
		
		Map<String,String> param = new HashMap<String, String>();
		param.put("ip", ip);
		if (!Strings.isNullOrEmpty(email)) {
			if (Strings.isNullOrEmpty(password)) {
				jo.put("status", "invalid_password");
				jo.put("code", Integer.valueOf(10007));
				jo.put("error_message", "invalid password");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}

			try {
//				password = Base64Utils.encodeToString(password.getBytes());
//				user = this.userDao.loginUser(email, password);
				
				param.put("username", email);
				param.put("password", password);
				param.put("device", device);
				if(device.equals("10")){
					param.put("remember_me", "1");
				}
				String params = publicParam(param);
				String result = HttpUtil.sendPostStr(url+"/customer/account/login", params);
				JSONObject resp_json = JSONObject.fromObject(result);
				int code = resp_json.getInt("code");
				//user = this.userDao.loginByPhone(zone, phone, password);
				if(code == 10000){
					JSONObject data = resp_json.getJSONObject("data");
					centre_id = data.getInt("userid");
					fbToken = data.getString("token");
					fbusername = data.getString("username");
					if(device.equals("10")){
						script = data.getString("script");
					}
					
					Map<String,String> createMap = new HashMap<String, String>();
					createMap.put("ip", ip);
					createMap.put("device", device);
					createMap.put("user_id", String.valueOf(centre_id));
					createMap.put("user_name", fbusername);
					String createParam = carPublicParam(createMap);
					String createResult = HttpUtil.sendPostStr(car_url+"/member/create", createParam);
					
				}else if(code == 10001){
					jo.put("status", "缺少参数");
					jo.put("code", 10600);
					jo.put("error_message", "缺少参数");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 10110){
					jo.put("status", "您输入的账号不存在，请先注册");
					jo.put("code", 10605);
					jo.put("error_message", "您输入的账号不存在，请先注册");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 10111){
					jo.put("status", "密码不存在");
					jo.put("code", 10606);
					jo.put("error_message", "密码不存在");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 10112){
					jo.put("status", "用户名或密码不正确");
					jo.put("code", 10607);
					jo.put("error_message", "用户名或密码不正确");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}


			} catch (Exception e) {
				jo.put("status", "invalid_password");
				jo.put("code", Integer.valueOf(10007));
				jo.put("error_message", "invalid password");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}

//			String avatarImage = user.getAvatarImage();
//			if(!Strings.isNullOrEmpty(avatarImage)){
//				auth.put("avatar_image",JSONObject.fromObject(avatarImage));
//			}
			auth.put("userid", centre_id);
			auth.put("access_token", fbToken);
			auth.put("username", fbusername);
//			auth.put("token_timestamp", Long.valueOf(Long.parseLong(timestamp)));
//			auth.put("token", fbToken);
//			auth.put("fbid",centre_id);
			if(device.equals("10")){
				auth.put("script",script);
			}
		} else if ((!Strings.isNullOrEmpty(phone)) && (!Strings.isNullOrEmpty(zone))) {
			if (Strings.isNullOrEmpty(password)) {
				jo.put("status", "invalid_password");
				jo.put("code", Integer.valueOf(10007));
				jo.put("error_message", "invalid password");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}
			//---

			try {
				param.put("username", phone);
				param.put("password", password);
				param.put("device", String.valueOf(userJson.getInt("device")));
				if(device.equals("10")){
					param.put("remember_me", "1");
				}
				String params = publicParam(param);
				String result = HttpUtil.sendPostStr(url+"/customer/account/login", params);
				JSONObject resp_json = JSONObject.fromObject(result);
				int code = resp_json.getInt("code");
				//user = this.userDao.loginByPhone(zone, phone, password);
				if(code == 10000){
					JSONObject data = resp_json.getJSONObject("data");
					centre_id = data.getInt("userid");
					fbToken = data.getString("token");
					fbusername = data.getString("username");
					if(device.equals("10")){
						script = data.getString("script");
					}
					Map<String,String> createMap = new HashMap<String, String>();
					createMap.put("ip", ip);
					createMap.put("device", device);
					createMap.put("user_id", String.valueOf(centre_id));
					createMap.put("user_name", fbusername);
					String createParam = carPublicParam(createMap);
					String createResult = HttpUtil.sendPostStr(car_url+"/member/create", createParam);
				}else if(code == 10001){
					jo.put("status", "缺少参数");
					jo.put("code", 10600);
					jo.put("error_message", "缺少参数");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 10004){
					jo.put("status", "校验签名不通过");
					jo.put("code", 10620);
					jo.put("error_message", "校验签名不通过");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 10110){
					jo.put("status", "您输入的账号不存在，请先注册");
					jo.put("code", 10605);
					jo.put("error_message", "您输入的账号不存在，请先注册");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 10111){
					jo.put("status", "密码不存在");
					jo.put("code", 10606);
					jo.put("error_message", "密码不存在");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 10112){
					jo.put("status", "用户名或密码不正确");
					jo.put("code", 10607);
					jo.put("error_message", "用户名或密码不正确");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}

			} catch (Exception e) {
				jo.put("status", "invalid_password");
				jo.put("code", Integer.valueOf(10007));
				jo.put("error_message", "invalid password");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}
		
			//---


//			String avatarImage = user.getAvatarImage();
//			if(!Strings.isNullOrEmpty(avatarImage)){
//				auth.put("avatar_image",JSONObject.fromObject(avatarImage));
//			}
			auth.put("userid", centre_id);
			auth.put("access_token", fbToken);
			auth.put("username", fbusername);
//			auth.put("token_timestamp", Long.valueOf(Long.parseLong(timestamp)));
//			auth.put("token", fbToken);
//			auth.put("fbid", centre_id);
			if(device.equals("10")){
				auth.put("script",script);
			}
		}else if(!Strings.isNullOrEmpty(fbname)){

			if (Strings.isNullOrEmpty(password)) {
				jo.put("status", "invalid_password");
				jo.put("code", Integer.valueOf(10007));
				jo.put("error_message", "invalid password");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}

			


			try {
				param.put("username", fbname);
				param.put("password", password);
				param.put("device", String.valueOf(userJson.getInt("device")));
				if(device.equals("10")){
					param.put("remember_me", "1");
				}
				String params = publicParam(param);
				String result = HttpUtil.sendPostStr(url+"/customer/account/login", params);
				JSONObject resp_json = JSONObject.fromObject(result);
				int code = resp_json.getInt("code");
				//user = this.userDao.loginByPhone(zone, phone, password);
				if(code == 10000){
					JSONObject data = resp_json.getJSONObject("data");
					centre_id = data.getInt("userid");
					fbToken = data.getString("token");
					if(device.equals("10")){
						script = data.getString("script");
					}
					
					Map<String,String> createMap = new HashMap<String, String>();
					createMap.put("ip", ip);
					createMap.put("device", device);
					createMap.put("user_id", String.valueOf(centre_id));
					createMap.put("user_name", fbusername);
					String createParam = carPublicParam(createMap);
					String createResult = HttpUtil.sendPostStr(car_url+"/member/create", createParam);
					
				}else if(code == 10001){
					jo.put("status", "缺少参数");
					jo.put("code", 10600);
					jo.put("error_message", "缺少参数");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 10110){
					jo.put("status", "您输入的账号不存在，请先注册");
					jo.put("code", 10605);
					jo.put("error_message", "您输入的账号不存在，请先注册");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 10111){
					jo.put("status", "密码不存在");
					jo.put("code", 10606);
					jo.put("error_message", "密码不存在");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 10112){
					jo.put("status", "用户名或密码不正确");
					jo.put("code", 10607);
					jo.put("error_message", "用户名或密码不正确");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}

			} catch (Exception e) {
				jo.put("status", "invalid_password");
				jo.put("code", Integer.valueOf(10007));
				jo.put("error_message", "invalid password");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}
		
		
			
			//---


//			String avatarImage = user.getAvatarImage();
//			if(!Strings.isNullOrEmpty(avatarImage)){
//				auth.put("avatar_image",JSONObject.fromObject(avatarImage));
//			}
			auth.put("userid", centre_id);
			auth.put("access_token", fbToken);
			auth.put("username", fbname);
//			auth.put("token_timestamp", Long.valueOf(Long.parseLong(timestamp)));
//			auth.put("token", fbToken);
//			auth.put("fbid", centre_id);
			if(device.equals("10")){
				auth.put("script",script);
			}
		}

		System.out.println(auth.toString());
		return Response.status(Response.Status.OK).entity(auth).build();
	}

	public Response updateUser(Long userId, JSONObject user, Long loginUserid) {
		try {
			System.out.println(user.toString());
			if (loginUserid.equals(userId)) {
				User u = (User) this.userDao.get(userId);
				if(user.containsKey("username")){
					if (user.get("username") != null) {
						u.setUsername(user.getString("username"));
					}
				}
				if(user.containsKey("introduction")){
					if (user.get("introduction") != null)
						u.setIntroduction(user.getString("introduction"));
					else {
						u.setIntroduction(null);
					}
				}
				

				if (user.get("avatar_image") != null) {
					u.setAvatarImage(user.get("avatar_image").toString());
				}
				
				if(user.containsKey("introduction")){
					if (user.get("website") != null)
						u.setWebsite(user.getString("website"));
					else {
						u.setWebsite(null);
					}
				}
				
				if (user.get("cover_image") != null) {
					u.setCoverImage(user.get("cover_image").toString());
				}

				if(user.containsKey("gender")){
					if(user.get("gender") != null){
						u.setGender(user.getString("gender"));
					}
				}
				
				this.userDao.update(u);
				log.debug("***update user success******");
			} else {
				JSONObject json1 = new JSONObject();
				json1.put("status", "invalid_request");
				json1.put("code", Integer.valueOf(10010));
				json1.put("error_message", "Invalid payload parameters");
				return Response.status(Response.Status.BAD_REQUEST).entity(json1).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject json1 = new JSONObject();
			json1.put("status", "invalid_request");
			json1.put("code", Integer.valueOf(10010));
			json1.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(json1).build();
		}

		JSONObject jo = new JSONObject();
		jo.put("status", "success");
		return Response.status(Response.Status.OK).entity(jo).build();
	}

	public Response updatePassword(Long loginUserid, Long userId, PasswordModel pwdModel,HttpServletRequest request) {
		log.debug("*** update password ***");
		JSONObject json = new JSONObject();
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = parseJson(urlkey);
		String url = jsonObject.getString("url");
		String ip = request.getHeader("X-Real-IP");
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		if (userId.equals(loginUserid)) {
			Map<String,String> param = new HashMap<String,String>();
			param.put("old_password", pwdModel.getCurrent_password());
			param.put("password", pwdModel.getNew_password());
			param.put("repeat_password", pwdModel.getNew_password());
			param.put("ip", ip);
			String params = "";
			try {
				params = publicParam(param);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String result = HttpUtil.sendPostStr(url+"/customer/account/update-password", params);
			JSONObject resp_json = JSONObject.fromObject(result);
			int code = resp_json.getInt("code");
			//user = this.userDao.loginByPhone(zone, phone, password);
			if(code == 10000){
				User user = (User) this.userDao.get(loginUserid);
				String current_pwd = Base64Utils.encodeToString(pwdModel.getCurrent_password().getBytes());
				if (user.getPassword().toUpperCase().equals(current_pwd.toUpperCase())) {
					user.setPassword(Base64Utils.encodeToString(pwdModel.getNew_password().getBytes()));
					this.userDao.update(user);
					String raw = user.getId() + user.getCreated_time().toString();
					String token = EncryptionUtil.hashMessage(raw);

					System.out.println("userId--->" + user.getId());
					json.put("userid", user.getId());
					json.put("access_token", token);
					json.put("token_timestamp", user.getCreated_time());
				}
			}else if(code == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10600);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code == 10110){
				json.put("status", "您输入的账号不存在，请先注册");
				json.put("code", 10605);
				json.put("error_message", "您输入的账号不存在，请先注册");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code == 10115){
				json.put("status", "原密码格式错误");
				json.put("code", 10610);
				json.put("error_message", "原密码格式错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code == 10116){
				json.put("status", "新密码格式错误");
				json.put("code", 10611);
				json.put("error_message", "新密码格式错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code == 10117){
				json.put("status", "确认密码与新密码不一致");
				json.put("code", 10612);
				json.put("error_message", "确认密码与新密码不一致");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code == 10118){
				json.put("status", "原密码错误");
				json.put("code", 10613);
				json.put("error_message", "原密码错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else if(code == 10120){
				json.put("status", "密码修改失败");
				json.put("code", 10614);
				json.put("error_message", "密码修改失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
			

		}

		System.out.println(json.toString());
		return Response.status(Response.Status.OK).entity(json).build();
	}

	public Response deleteUser(Long userId) {
		log.debug("****start delete user ****");
		try {
			this.userDao.disableUser(userId);
			System.out.println("delete success");
			log.debug("****delete user success ****");
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_MODIFIED).entity("User deleted").build();
		}

		return Response.status(Response.Status.OK).entity("User Deleted").build();
	}

	public List<JSONObject> getAllStories(Long userId, HttpServletRequest request, Long loginUserid) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<JSONObject> storyModelList = new ArrayList<JSONObject>();
		int count = 20;
		String type = "publish";
		JSONObject storyModel = null;
		User loginUser = userDao.get(loginUserid);
				
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Story> storyList = this.storyDao.getStoriesPageByNull(userId, count, type, loginUserid);
			if ((storyList != null) && (storyList.size() > 0))
				for (Story story : storyList) {
					storyModel = getStoryEventByLoginId(story, loginUserid,loginUser);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			List<Story> storyList = this.storyDao.getStoriesPageByNull(userId, count, type, loginUserid);
			if ((storyList != null) && (storyList.size() > 0))
				for (Story story : storyList) {
					storyModel = getStoryEventByLoginId(story, loginUserid,loginUser);
					storyModelList.add(storyModel);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Story> storyList = this.storyDao.getStoriesPageByStoryId(userId, count, since_id, 1, type,
					loginUserid);
			if ((storyList != null) && (storyList.size() > 0))
				for (Story story : storyList) {
					storyModel = getStoryEventByLoginId(story, loginUserid,loginUser);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Story> storyList = this.storyDao.getStoriesPageByStoryId(userId, count, since_id, 1, type,
					loginUserid);
			if ((storyList != null) && (storyList.size() > 0))
				for (Story story : storyList) {
					storyModel = getStoryEventByLoginId(story, loginUserid,loginUser);
					storyModelList.add(storyModel);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Story> storyList = this.storyDao.getStoriesPageByStoryId(userId, count, max_id, 2, type, loginUserid);
			if ((storyList != null) && (storyList.size() > 0))
				for (Story story : storyList) {
					storyModel = getStoryEventByLoginId(story, loginUserid,loginUser);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Story> storyList = this.storyDao.getStoriesPageByStoryId(userId, count, max_id, 2, type, loginUserid);
			if ((storyList != null) && (storyList.size() > 0)) {
				for (Story story : storyList) {
					storyModel = getStoryEventByLoginId(story, loginUserid,loginUser);
					storyModelList.add(storyModel);
				}
			}
		}
		log.debug("*** get stories list***" + JSONArray.fromObject(storyModelList));
		return storyModelList;
	}

	public List<EventModel> getTimelinesByUserId(Long loginUserid, HttpServletRequest request) {
		log.debug("*** Get Home Timeline of the Authenticated User ***");
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<EventModel> eventList = new ArrayList<EventModel>();
		String followingId = loginUserid + "";
		int count = 20;
		User loginUser = userDao.get(loginUserid);
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(loginUserid, count, followingId);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(loginUserid, count, followingId);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(loginUserid, since_id, count, 1,
					followingId); 
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(loginUserid, since_id, count, 1,
					followingId);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(loginUserid, max_id, count, 2,
					followingId);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(loginUserid, max_id, count, 2,
					followingId);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		}
		log.debug("*** eventList ***" + JSONArray.fromObject(eventList));

		return eventList;
	}

	public List<StoryEvent> getRepostStories(Long userId,Long loginUserid, HttpServletRequest request) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<StoryEvent> storyModelList = new ArrayList<StoryEvent>();
		int count = 20;
		Story story = null;
		StoryEvent storyModel = null;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Republish> republishList = this.republishDao.getRepublishesPageByUserId(userId, count);
			if ((republishList != null) && (republishList.size() > 0))
				for (Republish r : republishList) {
					story = r.getRepost_story();//(Story) this.storyDao.get(r.getStoryId());
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			List<Republish> republishList = this.republishDao.getRepublishesPageByUserId(userId, count);
			if ((republishList != null) && (republishList.size() > 0))
				for (Republish r : republishList) {
					story = r.getRepost_story();//(Story) this.storyDao.get(r.getStoryId());
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Republish> republishList = this.republishDao.getRepublishesPageByUserId(userId, count, since_id, 1);
			if ((republishList != null) && (republishList.size() > 0))
				for (Republish r : republishList) {
					story = r.getRepost_story();//(Story) this.storyDao.get(r.getStoryId());
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Republish> republishList = this.republishDao.getRepublishesPageByUserId(userId, count, since_id, 1);
			if ((republishList != null) && (republishList.size() > 0))
				for (Republish r : republishList) {
					story = r.getRepost_story();//(Story) this.storyDao.get(r.getStoryId());
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Republish> republishList = this.republishDao.getRepublishesPageByUserId(userId, count, max_id, 2);
			if ((republishList != null) && (republishList.size() > 0))
				for (Republish r : republishList) {
					story = r.getRepost_story();//(Story) this.storyDao.get(r.getStoryId());
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Republish> republishList = this.republishDao.getRepublishesPageByUserId(userId, count, max_id, 2);
			if ((republishList != null) && (republishList.size() > 0)) {
				for (Republish r : republishList) {
					story = r.getRepost_story();//(Story) this.storyDao.get(r.getStoryId());
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
			}
		}

		return storyModelList;
	}

	public List<StoryEvent> getLikeStories(Long userId,Long loginUserid, HttpServletRequest request) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<StoryEvent> storyModelList = new ArrayList<StoryEvent>();
		int count = 20;
		Story story = null;
		StoryEvent storyModel = null;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
		}

		return storyModelList;
	}

	public Response createFollow(Long userId, Long loginUserid,HttpServletRequest request)throws Exception {
		log.debug("*** start create follow ***");
		JSONObject resp = new JSONObject();
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(!Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("car_url");
		Map<String,String> userJson = new HashMap<String,String>();
		userJson.put("user_id", loginUserid.toString());
		userJson.put("ip", ip);
		userJson.put("device", device);
		userJson.put("to_user_id", userId.toString());
		userJson.put("follow_type", "1");
		String params = carPublicParam(userJson);
		String result = HttpUtil.sendPostStr(url+"/member/follow", params);
		if (!Strings.isNullOrEmpty(result)) {
			JSONObject json = JSONObject.fromObject(result);
			int status = json.getInt("code");
			if (status == 10000) {

				resp.put("status", "success");
				return Response.status(Response.Status.OK).entity(resp).build();
			} else if(status == 11001) {	
				resp.put("status", "用户不存在");
				resp.put("code", 10840);
				resp.put("error_message", "用户不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 11003) {	
				resp.put("status", "对方用户不存在");
				resp.put("code", 10841);
				resp.put("error_message", "对方用户不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 11006) {	
				resp.put("status", "不能重复操作");
				resp.put("code", 10842);
				resp.put("error_message", "不能重复操作");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 11008) {	
				resp.put("status", "保存数据操作日志失败");
				resp.put("code", 10843);
				resp.put("error_message", "保存数据操作日志失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 11014) {	
				resp.put("status", "关注类型错误");
				resp.put("code", 10844);
				resp.put("error_message", "关注类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 11016) {	
				resp.put("status", "关注失败");
				resp.put("code", 10845);
				resp.put("error_message", "关注失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		} else {
			resp.put("status", "用户中心报错");
			resp.put("code", 10604);
			resp.put("error_message", "用户中心报错");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
		return null;
	}

	public Response isExistPhone(HttpServletRequest request) {
		String zone = request.getParameter("zone");
		String phone = request.getParameter("phone");
		JSONObject json = new JSONObject();
		if ((!Strings.isNullOrEmpty(zone)) && (!Strings.isNullOrEmpty(phone))) {
			User user = this.userDao.getUserByPhoneAndZone(zone, phone);
			if (user != null) {
				json.put("status", "Phone_exist");
				json.put("code", Integer.valueOf(10094));
				json.put("error_message", "The phone is exist");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
			json.put("status", "success");
			return Response.status(Response.Status.OK).entity(json).build();
		}

		json.put("status", "invalid_request");
		json.put("code", Integer.valueOf(10010));
		json.put("error_message", "Invalid payload parameters");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}

	public Response deleteFollow(Long userId, Long loginUserid, HttpServletRequest request)throws Exception {
		JSONObject resp = new JSONObject();
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(!Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("car_url");
		Map<String,String> userJson = new HashMap<String,String>();
		userJson.put("user_id", loginUserid.toString());
		userJson.put("ip", ip);
		userJson.put("device", device);
		userJson.put("to_user_id", userId.toString());
		userJson.put("follow_type", "-1");//-1-无关，10-熟悉，20-仰慕
		String params = carPublicParam(userJson);
		String result = HttpUtil.sendPostStr(url+"/member/follow", params);
		if (!Strings.isNullOrEmpty(result)) {
			JSONObject json = JSONObject.fromObject(result);
			int status = json.getInt("code");
			if (status == 10000) {

				resp.put("status", "success");
				return Response.status(Response.Status.OK).entity(resp).build();
			} else if(status == 11001) {	
				resp.put("status", "用户不存在");
				resp.put("code", 10840);
				resp.put("error_message", "用户不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 11003) {	
				resp.put("status", "对方用户不存在");
				resp.put("code", 10841);
				resp.put("error_message", "对方用户不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 11006) {	
				resp.put("status", "不能重复操作");
				resp.put("code", 10842);
				resp.put("error_message", "不能重复操作");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 11008) {	
				resp.put("status", "保存数据操作日志失败");
				resp.put("code", 10843);
				resp.put("error_message", "保存数据操作日志失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 11014) {	
				resp.put("status", "关注类型错误");
				resp.put("code", 10844);
				resp.put("error_message", "关注类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 11016) {	
				resp.put("status", "关注失败");
				resp.put("code", 10845);
				resp.put("error_message", "关注失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		} else {
			resp.put("status", "用户中心报错");
			resp.put("code", 10604);
			resp.put("error_message", "用户中心报错");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
		return null;
	}

	public Response updateConfiguration(Long userId, Long loginUserid, Configuration configuration) {
		try {
			if (loginUserid.equals(userId)) {
				System.out.println(configuration.isNew_comment_on_your_comment_push() + "-->"
						+ configuration.isNew_favorite_from_following_push());
				Configuration conf = this.configurationDao.getConfByUserId(userId);
				conf.setNew_comment_on_your_comment_push(configuration.isNew_comment_on_your_comment_push());
				conf.setNew_comment_on_your_story_push(configuration.isNew_comment_on_your_story_push());
				conf.setNew_favorite_from_following_push(configuration.isNew_favorite_from_following_push());
				conf.setNew_follower_push(configuration.isNew_follower_push());
				conf.setNew_story_from_following_push(configuration.isNew_story_from_following_push());
				conf.setRecommended_my_story_push(configuration.isRecommended_my_story_push());
				conf.setReposted_my_story_push(configuration.isReposted_my_story_push());
				conf.setNew_admin_push(configuration.isNew_admin_push());
				conf.setRecommended_my_story_slide_push(configuration.isRecommended_my_story_slide_push());
				
				this.configurationDao.update(conf);
				JSONObject jo = new JSONObject();
				jo.put("status", "success");
				return Response.status(Response.Status.OK).entity(jo).build();
			}
			JSONObject jo = new JSONObject();
			jo.put("status", "invalid_request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("status", "invalid_request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
	}

	public byte[] initSalt() {
		byte[] b = new byte[8];
		Random random = new Random();
		random.nextBytes(b);
		return b;
	}

	public Response noData() {
		log.debug("######################debug###############");
		JSONObject jo = new JSONObject();
		jo.put("status", "no_data");
		jo.put("code", Integer.valueOf(10020));
		jo.put("error_message", "no logined in");
		log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$info$$$");
		return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
	}

	public Response noUser() {
		log.debug("*** have no authentication message***");
		JSONObject jo = new JSONObject();
		jo.put("status", "no_user");
		jo.put("code", Integer.valueOf(10021));
		jo.put("error_message", "user does not exist");
		return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
	}

	public Response noAuthority() {
		log.debug("*** have no authentication message***");
		JSONObject jo = new JSONObject();
		jo.put("status", "no_authority");
		jo.put("code", Integer.valueOf(10085));
		jo.put("error_message", "There is no access to");
		return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
	}

	public Response invalidToken() {
		JSONObject jo = new JSONObject();
		jo.put("status", "invalid_token");
		jo.put("code", Integer.valueOf(10022));
		jo.put("error_message", "invalid token");
		return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
	}
	
	public Response invalidVersion() {
		JSONObject jo = new JSONObject();
		jo.put("status", "old_version");
		jo.put("code", 10108);
		jo.put("error_message", "please update a application version");
		return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
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

	public Response getFollowingByUserId(Long user_id, HttpServletRequest request, Long loginUserid) {
		log.debug("***get user_id 关注的user info");
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(!Strings.isNullOrEmpty(device)){
			device = "20";
		}
		int count = 20;
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		Map<String,String> sb = new HashMap<String,String>();
		sb.put("ip", ip);
		sb.put("device", device);
		sb.put("user_id",user_id.toString());
		
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("car_url");
		JSONObject jo = new JSONObject();
		String result = "";
		if ((Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			sb.put("page_count",String.valueOf(count));
			
		} else if ((!Strings.isNullOrEmpty(countStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
			sb.put("page_count",String.valueOf(count));
				count = Integer.parseInt(countStr);
			} else if ((Strings.isNullOrEmpty(countStr)) 
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				int  max_id = Integer.parseInt(maxIdStr);
				sb.put("page_count",String.valueOf(count));
				sb.put("max_id",String.valueOf(max_id));
			} else if ((!Strings.isNullOrEmpty(countStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				int  max_id = Integer.parseInt(maxIdStr);
				sb.put("page_count",String.valueOf(count));
				sb.put("max_id",String.valueOf(max_id));
				result = HttpUtil.sendGetStr(url+"/member/follow-list", sb.toString());
			}
		String params = "";
		try {
			params = carPublicParam(sb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = HttpUtil.sendGetStr(url+"/member/follow-list", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){
				Object data = resJson.get("data");
				return Response.status(Response.Status.OK).entity(data).build();
			}else if(code == 10001){
				jo.put("status", "缺少参数");
				jo.put("code", 10600);
				jo.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}else if(code == 11020){
				jo.put("status", "获取会员信息失败");
				jo.put("code", 10638);
				jo.put("error_message", "获取会员信息失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}
		}else{
			jo.put("status", "获取会员信息失败");
			jo.put("code", 10638);
			jo.put("error_message", "获取会员信息失败");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
		return null;
	}

	public Response getFollowersByUserId(Long user_id, HttpServletRequest request, Long loginUserid) {
		log.debug("**** user_id 的粉�? ****");

		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		int count = 20;
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(!Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		Map<String,String> sb = new HashMap<String,String>();
		sb.put("ip", ip);
		sb.put("device", device);
		sb.put("user_id",user_id.toString());
		
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("car_url");
		JSONObject jo = new JSONObject();
		String result = "";
		if ((Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			sb.put("page_count",String.valueOf(count));
			
		} else if ((!Strings.isNullOrEmpty(countStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
			sb.put("page_count",String.valueOf(count));
				count = Integer.parseInt(countStr);
			} else if ((Strings.isNullOrEmpty(countStr)) 
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				int  max_id = Integer.parseInt(maxIdStr);
				sb.put("page_count",String.valueOf(count));
				sb.put("max_id",String.valueOf(max_id));
			} else if ((!Strings.isNullOrEmpty(countStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				int  max_id = Integer.parseInt(maxIdStr);
				sb.put("page_count",String.valueOf(count));
				sb.put("max_id",String.valueOf(max_id));
				
			}
		String params = "";
		try {
			params = carPublicParam(sb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = HttpUtil.sendGetStr(url+"/member/be-followed-list", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){
				Object data = resJson.get("data");
				return Response.status(Response.Status.OK).entity(data).build();
			}else if(code == 10001){
				jo.put("status", "缺少参数");
				jo.put("code", 10600);
				jo.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}else if(code == 11020){
				jo.put("status", "获取会员信息失败");
				jo.put("code", 10638);
				jo.put("error_message", "获取会员信息失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}
		}else{
			jo.put("status", "获取会员信息失败");
			jo.put("code", 10638);
			jo.put("error_message", "获取会员信息失败");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
		return null;
	}

	public List<StoryPageModel> draftsStory(Long loginUserid) {
		List<StoryPageModel> storyModelList = new ArrayList<StoryPageModel>();

		StoryPageModel storyModel = null;
		List<Story> storyList = this.storyDao.getDraftStories(loginUserid);
		if ((storyList != null) && (storyList.size() > 0)) {
			for (Story story : storyList) {
				storyModel = getStoryModelByStoryLoginUser(story, loginUserid);
				storyModelList.add(storyModel);
			}
		}

		return storyModelList;
	}

	public boolean matchEmail(String email) {
		String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(email);
		boolean isMatched = matcher.matches();
		return isMatched;
	}

	public StoryPageModel getStoryModelByStoryLoginUser(Story story, Long loginUserid) {
		StoryPageModel storyModel = new StoryPageModel();
		storyModel.setId((Long) story.getId());
		storyModel.setImage_count(story.getImage_count());
		int repostStoryCount = this.republishDao.userRepostCount((Long) story.getUser().getId());
		User user = story.getUser();


		boolean followed_by_current_user = false;
		boolean is_following_current_user = false;
		JSONObject avatarImageJson = null;
		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
		}

		JSONObject coverImageJson = null;
		if (!Strings.isNullOrEmpty(user.getCoverImage())) {
			coverImageJson = JSONObject.fromObject(user.getCoverImage());
		}
		int storyCount = this.storyDao.getStoryCount((Long) user.getId());
		JSONObject authorJson = new JSONObject();
		authorJson.put("id", user.getId());
		authorJson.put("username", user.getUsername());
		authorJson.put("email", user.getEmail());
		authorJson.put("created_time", user.getCreated_time());
		authorJson.put("status", user.getStatus());
		authorJson.put("introduction", user.getIntroduction());
		authorJson.put("avatar_image", avatarImageJson);
		authorJson.put("cover_image", coverImageJson);
		authorJson.put("likes_count", Integer.valueOf(0));
		authorJson.put("reposts_count", Integer.valueOf(repostStoryCount));
		authorJson.put("stories_count", Integer.valueOf(storyCount));
		authorJson.put("followers_count", Integer.valueOf(0));
		authorJson.put("following_count", Integer.valueOf(0));
		authorJson.put("user_type", user.getUser_type());
		if (!Strings.isNullOrEmpty(user.getWebsite()))
			authorJson.put("website", user.getWebsite());
		else {
			authorJson.put("website", null);
		}

		authorJson.put("followed_by_current_user", Boolean.valueOf(followed_by_current_user));
		authorJson.put("is_following_current_user", Boolean.valueOf(is_following_current_user));

		storyModel.setAuthor(authorJson);
		storyModel.setCreated_time(story.getCreated_time());
		storyModel.setUpdate_time(story.getUpdate_time());
		/*Collection collection = this.collectionStoryDao.getCollectionByStoryId((Long) story.getId());
		if (collection != null) {
			CollectionIntro ci = new CollectionIntro();
			ci.setId((Long) collection.getId());
			ci.setCollection_name(collection.getCollectionName());
			ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
			ci.setInfo(collection.getInfo());
			User u = collection.getUser();
			JSONObject author = new JSONObject();
			author.put("id", u.getId());
			author.put("username", u.getUsername());
			ci.setAuthor(author);
			JsonConfig configs = new JsonConfig();
			List<String> delArray = new ArrayList<String>();
			
			
			JSONObject collectionJson = null;
			if ((delArray != null) && (delArray.size() > 0)) {
				configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
				configs.setIgnoreDefaultExcludes(false);
				configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

				collectionJson = JSONObject.fromObject(ci, configs);
			} else {
				collectionJson = JSONObject.fromObject(ci);
			}
			
			storyModel.setCollection(collectionJson);
		}*/
		storyModel.setSummary(story.getSummary());
		JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
		String type = jsonObject.getString("type");
		CoverMedia coverMedia = null;
		
		if (type.equals("text")) {
			coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
			storyModel.setCover_media(JSONObject.fromObject(coverMedia));
		} else if (type.equals("image")) {
			coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
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
					TextCover textCover = (TextCover) JSONObject.toBean(content, TextCover.class);
					log.debug("*** element TextCover type ***" + textCover.getType());
					element.setContent(textCover);
				} else if (types.equals("image")) {
					ImageCover imageCover = (ImageCover) JSONObject.toBean(content, ImageCover.class);
					log.debug("*** element ImageCover type ***" + imageCover.getType());
					element.setContent(imageCover);
				}else if(types.equals("location")){
		        	   LocationModel locationModel = (LocationModel)
		        			   JSONObject.toBean(content,LocationModel.class);
		        	   element.setContent(locationModel);
		           }else if(types.equals("link")){
		        	   String media = content.getString("media");
		        	   JSONObject mediaJSON = JSONObject.fromObject(media);
		        	   if(mediaJSON.containsKey("image")){
		        		   LinkModel linkModel = (LinkModel)
		            			   JSONObject.toBean(content,LinkModel.class);
		            	   element.setContent(linkModel);
		        	   }else{
		        		   LinkModels linkModel = (LinkModels)
		            			   JSONObject.toBean(content,LinkModels.class);
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

		storyModel.setCommnents_enables(story.getComments_enabled());
		if (!Strings.isNullOrEmpty(story.getTinyURL()))
			storyModel.setUrl(story.getTinyURL());
		else {
			storyModel.setUrl(null);
		}

		storyModel.setView_count(story.getViewTimes());
		storyModel.setTitle(story.getTitle());

		int count = this.commentDao.getCommentCountById((Long) story.getId());
		storyModel.setComment_count(count);
		/*int repostCount = this.republishDao.count((Long) story.getId());
		storyModel.setRepost_count(repostCount);*/
		Set<Story> sSet = user.getRepost_story();
		List<Story> rsList = new ArrayList<Story>();
		if(sSet != null && sSet.size() > 0 ){
			Iterator<Story> it = sSet.iterator();
			while(it.hasNext()){
				rsList.add(it.next());
			}
			if(rsList.contains(story)){
				storyModel.setRepost_by_current_user(true);
			}else{
				storyModel.setRepost_by_current_user(false);
			}
		}else{
			storyModel.setRepost_by_current_user(false);
		}

		List<Comment> commentList = this.commentDao.getCommentByStoryIdNewThree((Long) story.getId());
		if ((commentList != null) && (commentList.size() > 0)) {
			List<JSONObject> commentModelList = new ArrayList<JSONObject>();
			JSONObject commentModel = null;
			for (Comment c : commentList) {
				commentModel = getCommentModel(c);
				commentModelList.add(commentModel);
			}
			storyModel.setComments(commentModelList);
		}
		List<Story> storyList = this.storyDao.getStoriesByRandThree(story.getId());
		if ((storyList != null) && (storyList.size() > 0)) {
			List<StoryIntro> recommendations = new ArrayList<StoryIntro>();
			StoryIntro intro = null;
			for (Story s : storyList) {
				intro = new StoryIntro();
				intro.setId((Long) s.getId());
				intro.setTitle(s.getTitle());
				intro.setImage_count(s.getImage_count());
				if (!Strings.isNullOrEmpty(s.getCover_page()))
					intro.setCover_media(JSONObject.fromObject(s.getCover_page()));
				else {
					intro.setCover_media(null);
				}
				intro.setCollectionId(Long.valueOf(1L));
				recommendations.add(intro);
			}
			storyModel.setRecommendation(recommendations);
		}

		return storyModel;
	}

	public JSONObject getStoryEventByLoginId(Story story, Long loginUserid,User loginUser) {
		StoryEvent storyModel = new StoryEvent();
		JSONObject storyJson = null;
		if (((Long) story.getUser().getId()).equals(loginUserid)) {
			storyModel.setId((Long) story.getId());
			storyModel.setImage_count(story.getImage_count());
			User user = story.getUser();
			JSONObject authorJson = new JSONObject();
			JSONObject coverImageJson = null;
			if (!Strings.isNullOrEmpty(user.getCoverImage())) {
				coverImageJson = JSONObject.fromObject(user.getCoverImage());
			}

			JSONObject avatarImageJson = null;
			if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
				avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
			}
			authorJson.put("id", user.getId());
			authorJson.put("cover_image", coverImageJson);
			authorJson.put("username", user.getUsername());
			authorJson.put("introduction", user.getIntroduction());
			authorJson.put("avatar_image", avatarImageJson);
			authorJson.put("user_type", user.getUser_type());
			if (!Strings.isNullOrEmpty(user.getWebsite()))
				authorJson.put("website", user.getWebsite());
			else {
				authorJson.put("website", null);
			}
			storyModel.setAuthor(authorJson);
			storyModel.setCreated_time(story.getCreated_time());

			Collection collection = this.collectionStoryDao.getCollectionByStoryId((Long) story.getId());
			if (collection != null) {
				CollectionIntro ci = new CollectionIntro();
//				ci.setId((Long) collection.getId());
//				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
//				ci.setInfo(collection.getInfo());
				User author = new User();
				JSONObject json = new JSONObject();
				json.put("id",author.getId());
				json.put("username",author.getUsername());
				ci.setAuthor(json);
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				
				
				JSONObject collectionJson = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					collectionJson = JSONObject.fromObject(ci, configs);
				} else {
					collectionJson = JSONObject.fromObject(ci);
				}
				
				storyModel.setCollection(collectionJson);
			}
			storyModel.setSummary(story.getSummary());

			JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
			log.debug("***story.getCover_page()***" + jsonObject);
			String type = jsonObject.getString("type");

			if (type.equals("text")) {
				TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
				log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
				storyModel.setCover_media(JSONObject.fromObject(coverMedia));
			} else if (type.equals("image")) {
				ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
				storyModel.setCover_media(JSONObject.fromObject(coverMedia));
			} else if (type.equals("multimedia")) {
				storyModel.setCover_media(jsonObject);
			}

			storyModel.setTitle(story.getTitle());

			storyModel.setAuthor(authorJson);
			int count = this.commentDao.getCommentCountById((Long) story.getId());
			storyModel.setComment_count(count);
			storyModel.setRecommend_date(story.getRecommend_date());
			Set<Story> sSet = user.getRepost_story();
			List<Story> rsList = new ArrayList<Story>();
			if(sSet != null && sSet.size() > 0 ){
				Iterator<Story> it = sSet.iterator();
				while(it.hasNext()){
					rsList.add(it.next());
				}
				if(rsList.contains(story)){
					storyModel.setRepost_by_current_user(true);
				}else{
					storyModel.setRepost_by_current_user(false);
				}
			}else{
				storyModel.setRepost_by_current_user(false);
			}
			JsonConfig configs = new JsonConfig();
			List<String> delArray = new ArrayList<String>();
			if (Strings.isNullOrEmpty(story.getTitle())) {
				delArray.add("title");
			}
			if (collection == null) {
				delArray.add("collection");
			}
			if (Strings.isNullOrEmpty(story.getSummary())) {
				delArray.add("summary");
			}

			if ((delArray != null) && (delArray.size() > 0)) {
				configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
				configs.setIgnoreDefaultExcludes(false);
				configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

				storyJson = JSONObject.fromObject(storyModel, configs);
			} else {
				storyJson = JSONObject.fromObject(storyModel);
			}

		} else if (story.getStatus().equals("publish")) {
			storyModel.setId((Long) story.getId());
			storyModel.setImage_count(story.getImage_count());
			User user = story.getUser();
			JSONObject authorJson = new JSONObject();
			JSONObject coverImageJson = null;
			if (!Strings.isNullOrEmpty(user.getCoverImage())) {
				coverImageJson = JSONObject.fromObject(user.getCoverImage());
			}

			JSONObject avatarImageJson = null;
			if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
				avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
			}
			authorJson.put("id", user.getId());
			authorJson.put("cover_image", coverImageJson);
			authorJson.put("username", user.getUsername());
			authorJson.put("introduction", user.getIntroduction());
			authorJson.put("avatar_image", avatarImageJson);
			authorJson.put("user_type", user.getUser_type());
			if (!Strings.isNullOrEmpty(user.getWebsite()))
				authorJson.put("website", user.getWebsite());
			else {
				authorJson.put("website", null);
			}
			Set<Story> sSet = loginUser.getRepost_story();
			List<Story> rsList = new ArrayList<Story>();
			if(sSet != null && sSet.size() > 0 ){
				Iterator<Story> it = sSet.iterator();
				while(it.hasNext()){
					rsList.add(it.next());
				}
				if(rsList.contains(story)){
					storyModel.setRepost_by_current_user(true);
				}else{
					storyModel.setRepost_by_current_user(false);
				}
			}else{
				storyModel.setRepost_by_current_user(false);
			}
			storyModel.setAuthor(authorJson);
			int count = this.commentDao.getCommentCountById((Long) story.getId());
			storyModel.setComment_count(count);
			Collection collection = this.collectionStoryDao.getCollectionByStoryId((Long) story.getId());
			if (collection != null) {
				CollectionIntro ci = new CollectionIntro();
//				ci.setId((Long) collection.getId());
//				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
//				ci.setInfo(collection.getInfo());
				User author = null;//collection.getUser();
				JSONObject json = new JSONObject();
				json.put("id",author.getId());
				json.put("username",author.getUsername());
				ci.setAuthor(json);
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				
				JSONObject collectionJson = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					collectionJson = JSONObject.fromObject(ci, configs);
				} else {
					collectionJson = JSONObject.fromObject(ci);
				}
				
				storyModel.setCollection(collectionJson);
			}
			storyModel.setSummary(story.getSummary());
			storyModel.setCreated_time(story.getCreated_time());

			JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
			log.debug("***story.getCover_page()***" + jsonObject);
			String type = jsonObject.getString("type");

			if (type.equals("text")) {
				TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
				log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
				storyModel.setCover_media(JSONObject.fromObject(coverMedia));
			} else if (type.equals("image")) {
				ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
				storyModel.setCover_media(JSONObject.fromObject(coverMedia));
			} else if (type.equals("multimedia")) {
				storyModel.setCover_media(jsonObject);
			}

			storyModel.setTitle(story.getTitle());

			storyModel.setRecommend_date(story.getRecommend_date());

			JsonConfig configs = new JsonConfig();
			List<String> delArray = new ArrayList<String>();
			if (Strings.isNullOrEmpty(story.getTitle())) {
				delArray.add("title");
			}
			if (collection == null) {
				delArray.add("collection");
			}
			if (Strings.isNullOrEmpty(story.getSummary())) {
				delArray.add("summary");
			}
			if ((delArray != null) && (delArray.size() > 0)) {
				configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
				configs.setIgnoreDefaultExcludes(false);
				configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

				storyJson = JSONObject.fromObject(storyModel, configs);
			} else {
				storyJson = JSONObject.fromObject(storyModel);
			}

		}

		return storyJson;
	}

	public EventModel getEventModelListByLoginUserid(Timeline timeline, Long loginUserid) {
		try {
			EventModel event = new EventModel();
			event.setId((Long) timeline.getId());
			event.setEvent_time(timeline.getCreateTime());
			event.setEvent_type(timeline.getType());
			JSONObject contentJson = new JSONObject();
			Long storyId = (Long) timeline.getStory().getId();
			Story story = this.storyDao.getStoryByIdAndStatus(storyId, "publish", "disabled");
			StoryPageModel storyModel = new StoryPageModel();

			if (story != null) {
				if (((Long) story.getUser().getId()).equals(loginUserid)) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					int repostStoryCount = this.republishDao.userRepostCount((Long) story.getUser().getId());
					User user = story.getUser();
					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
					}
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(user.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(user.getCoverImage());
					}
					int storyCount = this.storyDao.getStoryCount((Long) user.getId());

					JSONObject authorJson = new JSONObject();
					authorJson.put("id", user.getId());
					authorJson.put("username", user.getUsername());
					authorJson.put("email", user.getEmail());
					authorJson.put("created_time", user.getCreated_time());
					authorJson.put("status", user.getStatus());
					authorJson.put("introduction", user.getIntroduction());
					authorJson.put("avatar_image", avatarImageJson);
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("reposts_count", Integer.valueOf(repostStoryCount));
					authorJson.put("stories_count", Integer.valueOf(storyCount));
					authorJson.put("user_type", user.getUser_type());
					if (!Strings.isNullOrEmpty(user.getWebsite()))
						authorJson.put("website", user.getWebsite());
					else {
						authorJson.put("website", null);
					}

					if (timeline.getCreatorId().equals(timeline.getTargetUserId())) {
						authorJson.put("followed_by_current_user", Boolean.valueOf(false));
						authorJson.put("is_following_current_user", Boolean.valueOf(false));
					} else {
						authorJson.put("followed_by_current_user", Boolean.valueOf(true));
					}

					storyModel.setAuthor(authorJson);
					storyModel.setCreated_time(story.getCreated_time());
					storyModel.setUpdate_time(story.getUpdate_time());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
					log.debug("***story.getCover_page()***" + jsonObject);
					String type = jsonObject.getString("type");

					if (type.equals("text")) {
						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
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
							}else if(types.equals("location")){
					        	   LocationModel locationModel = (LocationModel)
					        			   JSONObject.toBean(content,LocationModel.class);
					        	   element.setContent(locationModel);
					           }else if(types.equals("link")){
					        	   String media = content.getString("media");
					        	   JSONObject mediaJSON = JSONObject.fromObject(media);
					        	   if(mediaJSON.containsKey("image")){
					        		   LinkModel linkModel = (LinkModel)
					            			   JSONObject.toBean(content,LinkModel.class);
					            	   element.setContent(linkModel);
					        	   }else{
					        		   LinkModels linkModel = (LinkModels)
					            			   JSONObject.toBean(content,LinkModels.class);
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

					storyModel.setCommnents_enables(story.getComments_enabled());
					if (!Strings.isNullOrEmpty(story.getTinyURL())) {
						storyModel.setUrl(story.getTinyURL());
					}
					storyModel.setView_count(story.getViewTimes());
					storyModel.setTitle(story.getTitle());

					int count = 0;
		           Set<Comment> cSet = story.getComments();
		           if(cSet != null && cSet.size() > 0){
		        	   count = cSet.size();
		           }
					storyModel.setComment_count(count);
					/*int repostCount = this.republishDao.count(storyId);
					storyModel.setRepost_count(repostCount);*/
					/*Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
					if (repost != null)
						storyModel.setRepost_by_current_user(true);
					else {
						storyModel.setRepost_by_current_user(false);
					}*/
					
					Set<Story> sSet = user.getRepost_story();
					List<Story> rsList = new ArrayList<Story>();
					if(sSet != null && sSet.size() > 0 ){
						Iterator<Story> it = sSet.iterator();
						while(it.hasNext()){
							rsList.add(it.next());
						}
						if(rsList.contains(story)){
							storyModel.setRepost_by_current_user(true);
						}else{
							storyModel.setRepost_by_current_user(false);
						}
					}else{
						storyModel.setRepost_by_current_user(false);
					}

					List<Comment> commentList = this.commentDao.getCommentByStoryIdNewThree((Long) story.getId());
					if ((commentList != null) && (commentList.size() > 0)) {
						List<JSONObject> commentModelList = new ArrayList<JSONObject>();
						JSONObject commentModel = null;
						for (Comment c : commentList) {
							commentModel = getCommentModel(c);
							commentModelList.add(commentModel);
						}
						storyModel.setComments(commentModelList);
					}
					List<Story> storyList = this.storyDao.getStoriesByRandThree(story.getId());
					if ((storyList != null) && (storyList.size() > 0)) {
						List<StoryIntro> recommendations = new ArrayList<StoryIntro>();
						StoryIntro intro = null;
						for (Story s : storyList) {
							intro = new StoryIntro();
							intro.setId((Long) s.getId());
							intro.setTitle(s.getTitle());
							if (!Strings.isNullOrEmpty(s.getCover_page()))
								intro.setCover_media(JSONObject.fromObject(s.getCover_page()));
							else {
								intro.setCover_media(null);
							}
							intro.setCollectionId(Long.valueOf(1L));
							recommendations.add(intro);
						}
						storyModel.setRecommendation(recommendations);
					}

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getTinyURL())) {
						delArray.add("url");
					}
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}

					contentJson.put("story", storyJson);
				} else if (story.getStatus().equals("publish")) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					int repostStoryCount = this.republishDao.userRepostCount((Long) story.getUser().getId());
					User user = story.getUser();
					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
					}
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(user.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(user.getCoverImage());
					}
					int storyCount = this.storyDao.getStoryCount((Long) user.getId());

					JSONObject authorJson = new JSONObject();
					authorJson.put("id", user.getId());
					authorJson.put("username", user.getUsername());
					authorJson.put("email", user.getEmail());
					authorJson.put("created_time", user.getCreated_time());
					authorJson.put("status", user.getStatus());
					authorJson.put("introduction", user.getIntroduction());
					authorJson.put("avatar_image", avatarImageJson);
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("reposts_count", Integer.valueOf(repostStoryCount));
					authorJson.put("stories_count", Integer.valueOf(storyCount));
					authorJson.put("user_type", user.getUser_type());
					if (!Strings.isNullOrEmpty(user.getWebsite()))
						authorJson.put("website", user.getWebsite());
					else {
						authorJson.put("website", null);
					}

					if (timeline.getCreatorId().equals(timeline.getTargetUserId())) {
						authorJson.put("followed_by_current_user", Boolean.valueOf(false));
						authorJson.put("is_following_current_user", Boolean.valueOf(false));
					} else {
						authorJson.put("followed_by_current_user", Boolean.valueOf(true));
					}

					storyModel.setAuthor(authorJson);
					storyModel.setCreated_time(story.getCreated_time());
					storyModel.setUpdate_time(story.getUpdate_time());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
					log.debug("***story.getCover_page()***" + jsonObject);
					String type = jsonObject.getString("type");

					if (type.equals("text")) {
						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
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
						for (StoryElement element : seSet) {
							JSONObject content = JSONObject.fromObject(element.getContents());
							String types = content.getString("type");
							if (types.equals("text")) {
								TextCover cm = (TextCover) JSONObject.toBean(content, TextCover.class);
								element.setContent(cm);
							} else if (types.equals("image")) {
								ImageCover cm = (ImageCover) JSONObject.toBean(content, ImageCover.class);
								element.setContent(cm);
							}else if(types.equals("location")){
					        	   LocationModel locationModel = (LocationModel)
					        			   JSONObject.toBean(content,LocationModel.class);
					        	   element.setContent(locationModel);
					           }else if(types.equals("link")){
					        	   String media = content.getString("media");
					        	   JSONObject mediaJSON = JSONObject.fromObject(media);
					        	   if(mediaJSON.containsKey("image")){
					        		   LinkModel linkModel = (LinkModel)
					            			   JSONObject.toBean(content,LinkModel.class);
					            	   element.setContent(linkModel);
					        	   }else{
					        		   LinkModels linkModel = (LinkModels)
					            			   JSONObject.toBean(content,LinkModels.class);
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

					storyModel.setCommnents_enables(story.getComments_enabled());
					if (!Strings.isNullOrEmpty(story.getTinyURL()))
						storyModel.setUrl(story.getTinyURL());
					else {
						storyModel.setUrl(null);
					}
					storyModel.setView_count(story.getViewTimes());
					storyModel.setTitle(story.getTitle());

					int count = 0;
			           Set<Comment> cSet = story.getComments();
			           if(cSet != null && cSet.size() > 0){
			        	   count = cSet.size();
			           }
					storyModel.setComment_count(count);
					/*Likes likes = this.likesDao.getLikeByUserIdAndStoryId(loginUserid, storyId);
					if (likes != null)
						storyModel.setLiked_by_current_user(true);
					else {
						storyModel.setLiked_by_current_user(false);
					}
					int likeCount = this.likesDao.likeStoryCount(storyId);
					storyModel.setLike_count(likeCount);
					int repostCount = this.republishDao.count(storyId);
					storyModel.setRepost_count(repostCount);*/
					/*Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
					if (repost != null)
						storyModel.setRepost_by_current_user(true);
					else {
						storyModel.setRepost_by_current_user(false);
					}*/
					
					Set<Story> sSet = user.getRepost_story();
					List<Story> rsList = new ArrayList<Story>();
					if(sSet != null && sSet.size() > 0 ){
						Iterator<Story> it = sSet.iterator();
						while(it.hasNext()){
							rsList.add(it.next());
						}
						if(rsList.contains(story)){
							storyModel.setRepost_by_current_user(true);
						}else{
							storyModel.setRepost_by_current_user(false);
						}
					}else{
						storyModel.setRepost_by_current_user(false);
					}

					List<Comment> commentList = this.commentDao.getCommentByStoryIdNewThree((Long) story.getId());
					if ((commentList != null) && (commentList.size() > 0)) {
						List<JSONObject> commentModelList = new ArrayList<JSONObject>();
						JSONObject commentModel = null;
						for (Comment c : commentList) {
							commentModel = getCommentModel(c);
							commentModelList.add(commentModel);
						}
						storyModel.setComments(commentModelList);
					}
					List<Story> storyList = this.storyDao.getStoriesByRandThree(story.getId());
					if ((storyList != null) && (storyList.size() > 0)) {
						List<StoryIntro> recommendations = new ArrayList<StoryIntro>();
						StoryIntro intro = null;
						for (Story s : storyList) {
							intro = new StoryIntro();
							intro.setId((Long) s.getId());
							intro.setTitle(s.getTitle());
							if (!Strings.isNullOrEmpty(s.getCover_page()))
								intro.setCover_media(JSONObject.fromObject(s.getCover_page()));
							else {
								intro.setCover_media(null);
							}
							intro.setCollectionId(Long.valueOf(1L));
							recommendations.add(intro);
						}
						storyModel.setRecommendation(recommendations);
					}

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getTinyURL())) {
						delArray.add("url");
					}
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}
					contentJson.put("story", storyJson);
				}

				if (timeline.getType().equals("post")) {
					event.setContent(contentJson);
				} else if (timeline.getType().equals("repost")) {
					UserIntro userIntro = new UserIntro();
					User user = (User) this.userDao.get(timeline.getCreatorId());
					if (user != null) {
						userIntro.setId((Long) user.getId());
						userIntro.setUsername(user.getUsername());
						userIntro.setIntroduction(user.getIntroduction());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
						}

						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						if (Strings.isNullOrEmpty(user.getAvatarImage())) {
							delArray.add("avatar_image");
						}
						JSONObject userIntroJson = null;
						if ((delArray != null) && (delArray.size() > 0)) {
							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
							configs.setIgnoreDefaultExcludes(false);
							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							userIntroJson = JSONObject.fromObject(userIntro, configs);
						} else {
							userIntroJson = JSONObject.fromObject(userIntro);
						}
						contentJson.put("repost_by", userIntroJson);
					}
					event.setContent(contentJson);
				} else if (timeline.getType().equals("like")) {
					UserIntro userIntro = new UserIntro();
					User user = (User) this.userDao.get(timeline.getCreatorId());
					if (user != null) {
						userIntro.setId((Long) user.getId());
						userIntro.setUsername(user.getUsername());
						userIntro.setIntroduction(user.getIntroduction());
						userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
						contentJson.put("like_by", userIntro);
					}
					event.setContent(contentJson);
				}
			}

			return event;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public EventModel getEventModelCopy(Timeline timeline) {
		try {
			EventModel event = new EventModel();
			event.setId(timeline.getId());
			event.setEvent_time(timeline.getCreateTime());
			event.setEvent_type(timeline.getType());
			JSONObject contentJson = new JSONObject();
			Story story = timeline.getStory();
			//Story story = storyDao.getStoryByIdAndStatus(storyId, "publish");
			StoryHomeCopy storyModel = new StoryHomeCopy();

			if (story != null) {
				Long storyId = story.getId();
				if (story.getStatus().equals("publish")) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					User user = story.getUser();
					JSONObject authorJson = new JSONObject();
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(user.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(user.getCoverImage());
					}

					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
					}
					authorJson.put("id", user.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", user.getUsername());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}
					authorJson.put("user_type", user.getUser_type());

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
					int count = 0;
					/*Set<Comment> coSet = story.getComments();
					
					if(coSet != null && coSet.size() > 0){
						count = coSet.size();
					}*/
					count = this.commentDao.getCommentCountById(story.getId());
					storyModel.setComment_count(count);
					//Set<Collection> cSet = story.getCollections();
					//Collection collection = this.collectionStoryDao.getCollectionByStoryId(storyId);
					Collection collection = null;
					/*if (cSet != null && cSet.size() > 0) {
						Iterator<Collection> it = cSet.iterator();
						collection = it.next();
						CollectionIntros ci = new CollectionIntros();
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setInfo(collection.getInfo());
						storyModel.setCollection(ci);
					}*/
					storyModel.setSummary(story.getSummary());
					storyModel.setCreated_time(story.getCreated_time());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
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

					storyModel.setTitle(story.getTitle());

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if (collection == null) {
						delArray.add("collection");
					}
					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}
					contentJson.put("story", storyJson);
				}

				event.setContent(contentJson);
			}

			return event;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public EventModel getEventModel(Timeline timeline) {
		try {
			EventModel event = new EventModel();
			event.setId(timeline.getId());
			event.setEvent_time(timeline.getCreateTime());
			event.setEvent_type(timeline.getType());
			JSONObject contentJson = new JSONObject();
			Story story = timeline.getStory();
			//Story story = storyDao.getStoryByIdAndStatus(storyId, "publish");
			StoryHomeCopy storyModel = new StoryHomeCopy();

			if (story != null) {
				Long storyId = story.getId();
				if (story.getStatus().equals("publish")) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					User user = story.getUser();
					JSONObject authorJson = new JSONObject();
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(user.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(user.getCoverImage());
					}

					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
					}
					authorJson.put("id", user.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", user.getUsername());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}
					authorJson.put("user_type", user.getUser_type());

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
					int count = 0;
					/*Set<Comment> coSet = story.getComments();
					
					if(coSet != null && coSet.size() > 0){
						count = coSet.size();
					}*/
					count = this.commentDao.getCommentCountById(story.getId());
					storyModel.setComment_count(count);
					
					Set<User> like_set = story.getLike_users();
					if(like_set != null && like_set.size() > 0){
						storyModel.setLike_count(like_set.size());
					}else{
						storyModel.setLike_count(0);
					}
					
					
					storyModel.setSummary(story.getSummary());
					storyModel.setCreated_time(story.getCreated_time());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
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

					storyModel.setTitle(story.getTitle());
					
					storyModel.setRecommend_date(story.getRecommend_date());
					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					/*Set<Columns> cSet = story.getColumns();
					if(cSet != null && cSet.size() > 0){
						Iterator<Columns> iter = cSet.iterator();
						if(iter.hasNext()){
							Columns c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("column_name", c.getColumn_name());
							storyModel.setColumns(json);
						}
					}else{
						delArray.add("columns");
					}*/
					Set<Collection> cSet = story.getCollections();
					if(cSet != null && cSet.size() > 0){
						Iterator<Collection> iter = cSet.iterator();
						List<JSONObject> collections = new ArrayList<JSONObject>();
						while(iter.hasNext()){
							Collection c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
//							json.put("collection_name", c.getCollectionName());
							collections.add(json);
						}
						storyModel.setCollections(collections);
					}else{
						delArray.add("collections");
					}
					
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}
					contentJson.put("story", storyJson);
				}

				event.setContent(contentJson);
			}

			return event;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public EventModel getEventModelTest(Timeline timeline){

		try {
			EventModel event = new EventModel();
			event.setId(timeline.getId());
			event.setEvent_time(timeline.getCreateTime());
			event.setEvent_type(timeline.getType());
			JSONObject contentJson = new JSONObject();
			Story story = timeline.getStory();
			//Story story = storyDao.getStoryByIdAndStatus(storyId, "publish");
			StoryHome storyModel = new StoryHome();

			if (story != null) {
				Long storyId = story.getId();
				if (story.getStatus().equals("publish")) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					User user = story.getUser();
					JSONObject authorJson = new JSONObject();
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(user.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(user.getCoverImage());
					}

					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
					}
					authorJson.put("id", user.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", user.getUsername());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}
					authorJson.put("user_type", user.getUser_type());

					/*if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
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
					}*/
					storyModel.setAuthor(authorJson);
					/*int count = this.commentDao.getCommentCountById((Long) story.getId());
					storyModel.setComment_count(count);*/
					Collection collection = collectionStoryDao.getCollectionByStoryId(storyId);
					if (collection != null) {
						CollectionIntros ci = new CollectionIntros();
//						ci.setId((Long) collection.getId());
//						ci.setCollection_name(collection.getCollectionName());
//						ci.setInfo(collection.getInfo());
						storyModel.setCollection(ci);
					}
					storyModel.setSummary(story.getSummary());
					storyModel.setCreated_time(story.getCreated_time());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
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

					storyModel.setTitle(story.getTitle());

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if (collection == null) {
						delArray.add("collection");
					}
					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}
					contentJson.put("story", storyJson);
				}

				event.setContent(contentJson);
			}

			return event;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	
	}

	public EventModel getEventModelByUser(Timeline timeline, Long userId) {
		try {
			EventModel event = new EventModel();
			event.setId((Long) timeline.getId());
			event.setEvent_time(timeline.getCreateTime());
			event.setEvent_type(timeline.getType());
			JSONObject contentJson = new JSONObject();
			Long storyId = (Long) timeline.getStory().getId();
			Story story = timeline.getStory();//this.storyDao.getStoryByIdAndStatus(storyId, "publish", "disabled");
			StoryEvent storyModel = new StoryEvent();

			if ((story != null) && (story.getStatus().equals("publish"))) {
				storyModel.setId(storyId);
				storyModel.setImage_count(story.getImage_count());
				storyModel.setUrl(story.getTinyURL());
				User user = story.getUser();
				JSONObject authorJson = new JSONObject();
				JSONObject coverImageJson = null;
				if (!Strings.isNullOrEmpty(user.getCoverImage())) {
					coverImageJson = JSONObject.fromObject(user.getCoverImage());
				}

				JSONObject avatarImageJson = null;
				if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
					avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
				}
				authorJson.put("id", user.getId());
				authorJson.put("cover_image", coverImageJson);
				authorJson.put("username", user.getUsername());
				authorJson.put("introduction", user.getIntroduction());
				authorJson.put("avatar_image", avatarImageJson);
				authorJson.put("user_type", user.getUser_type());
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
				if (!Strings.isNullOrEmpty(user.getWebsite()))
					authorJson.put("website", user.getWebsite());
				else {
					authorJson.put("website", null);
				}
				//Republish repost = this.republishDao.getRepostByUserIdAndStoryId(userId, storyId);
				Set<Story> sSet = user.getRepost_story();
				List<Story> rsList = new ArrayList<Story>();
				if(sSet != null && sSet.size() > 0 ){
					Iterator<Story> it = sSet.iterator();
					while(it.hasNext()){
						rsList.add(it.next());
					}
					if(rsList.contains(story)){
						storyModel.setRepost_by_current_user(true);
					}else{
						storyModel.setRepost_by_current_user(false);
					}
				}else{
					storyModel.setRepost_by_current_user(false);
				}
				
				storyModel.setAuthor(authorJson);
				int count = this.commentDao.getCommentCountById((Long) story.getId());
				storyModel.setComment_count(count);
				Collection collection = this.collectionStoryDao.getCollectionByStoryId(storyId);
				if (collection != null) {
					CollectionIntro ci = new CollectionIntro();
//					ci.setId((Long) collection.getId());
//					ci.setCollection_name(collection.getCollectionName());
					ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
//					ci.setInfo(collection.getInfo());
					User author = new User();
					JSONObject json = new JSONObject();
					json.put("id",author.getId());
					json.put("username",author.getUsername());
					ci.setAuthor(json);
					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					
					
					JSONObject collectionJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						collectionJson = JSONObject.fromObject(ci, configs);
					} else {
						collectionJson = JSONObject.fromObject(ci);
					}
					
					storyModel.setCollection(collectionJson);
				}
				storyModel.setSummary(story.getSummary());
				storyModel.setCreated_time(story.getCreated_time());

				JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
				log.debug("***story.getCover_page()***" + jsonObject);
				String type = jsonObject.getString("type");

				if (type.equals("text")) {
					TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
					log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
					storyModel.setCover_media(JSONObject.fromObject(coverMedia));
				} else if (type.equals("image")) {
					ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
					storyModel.setCover_media(JSONObject.fromObject(coverMedia));
				} else if (type.equals("multimedia")) {
					storyModel.setCover_media(jsonObject);
				}

				storyModel.setTitle(story.getTitle());

				storyModel.setRecommend_date(story.getRecommend_date());

				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				if (Strings.isNullOrEmpty(story.getTitle())) {
					delArray.add("title");
				}
				if (collection == null) {
					delArray.add("collection");
				}
				if (Strings.isNullOrEmpty(story.getSummary())) {
					delArray.add("summary");
				}
				JSONObject storyJson = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					storyJson = JSONObject.fromObject(storyModel, configs);
				} else {
					storyJson = JSONObject.fromObject(storyModel);
				}
				contentJson.put("story", storyJson);
			}

			if (timeline.getType().equals("post")) {
				event.setContent(contentJson);
			} else if (timeline.getType().equals("repost")) {
				UserIntro userIntro = new UserIntro();
				User user = (User) this.userDao.get(timeline.getCreatorId());

				if (user != null) {
					userIntro.setId((Long) user.getId());
					userIntro.setUsername(user.getUsername());
					userIntro.setIntroduction(user.getIntroduction());
					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
						userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
					}

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(user.getAvatarImage())) {
						delArray.add("avatar_image");
					}
					JSONObject userIntroJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						userIntroJson = JSONObject.fromObject(userIntro, configs);
					} else {
						userIntroJson = JSONObject.fromObject(userIntro);
					}
					contentJson.put("repost_by", userIntroJson);
				}
				event.setContent(contentJson);
			}

			return event;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public JSONObject getCommentModel(Comment comment) {
		CommentSummaryModel commentModel = new CommentSummaryModel();
		commentModel.setId((Long) comment.getId());
		commentModel.setContent(comment.getContent());
		commentModel.setCreated_time(comment.getCreated_time());
		commentModel.setStory_id((Long) comment.getStory().getId());
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

//	public Response loginLinkAccounts(LinkAccounts la,HttpServletRequest request) {
//		return null;
//	}

//	public Response bindingUser(LinkAccounts la, Long loginUserid) {
//		JSONObject json = new JSONObject();
//		if (la != null) {
//			String uuid = la.getUuid();
//			Object[] link = this.linkAccountsDao.getLinkAccountsByUUID(uuid);
//			if (link != null) {
//				json.put("status", "invalid_link_account");
//				json.put("code", Integer.valueOf(10081));
//				json.put("error_message", "Link account already exist.");
//				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//			}
//			LinkAccounts linkAccounts = new LinkAccounts();
//			linkAccounts.setAuth_token(la.getAuth_token());
//			linkAccounts.setAvatar_url(la.getAvatar_url());
//			linkAccounts.setDescription(la.getDescription());
//			linkAccounts.setRefreshed_at(la.getRefreshed_at());
//			linkAccounts.setService(la.getService());
//			linkAccounts.setUser_id(loginUserid);
//			linkAccounts.setUuid(la.getUuid());
//			this.linkAccountsDao.save(linkAccounts);
//			json.put("id", linkAccounts.getId());
//			json.put("service", linkAccounts.getService());
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//
//		json.put("status", "invalid_parameter");
//		json.put("code", Integer.valueOf(10058));
//		json.put("error_message", "Invalid parameter");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}

//	public Response unbindingUser(Long linkId,Long loginUserid) {
//		JSONObject json = new JSONObject();
//		User user = userDao.get(loginUserid);
//		List<LinkAccounts> laList = linkAccountsDao.getLinkAccountsByUserid(loginUserid);
//		LinkAccounts la = linkAccountsDao.get(linkId);
//		if(!Strings.isNullOrEmpty(user.getPhone())){
//			if(la != null){
//				this.linkAccountsDao.delete(linkId);
//			}
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}else{
//			if(laList != null && laList.size() > 1){
//				if(la != null){
//					this.linkAccountsDao.delete(linkId);
//				}
//				json.put("status", "success");
//				return Response.status(Response.Status.OK).entity(json).build();
//			}else{
//				json.put("status", "must one bind");
//				json.put("code",10109);
//				json.put("error_message", "at least one contact method information is required");
//				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//			}
//		}
//		
//	}

	public String getToken(String ak, String sk, String bucket) throws Exception {
		Mac mac = new Mac(ak, sk);
		PutPolicy policy = new PutPolicy(bucket);
		String token = policy.token(mac);
		return token;
	}

	public StoryEvent getStoryEventByStoryLoginUser(Story story,Long userid, Long loginUserid) {
		StoryEvent storyModel = new StoryEvent();
		storyModel.setId((Long) story.getId());
		storyModel.setImage_count(story.getImage_count());
		User user = story.getUser();
		JSONObject authorJson = new JSONObject();
		JSONObject coverImageJson = null;
		if (!Strings.isNullOrEmpty(user.getCoverImage())) {
			coverImageJson = JSONObject.fromObject(user.getCoverImage());
		}

		JSONObject avatarImageJson = null;
		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
		}
		authorJson.put("id", user.getId());
		authorJson.put("cover_image", coverImageJson);
		authorJson.put("username", user.getUsername());
		authorJson.put("introduction", user.getIntroduction());
		authorJson.put("avatar_image", avatarImageJson);
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
		if(userid.equals(loginUserid) && userid == loginUserid){
			storyModel.setLiked_by_current_user(true);
		}else{
			
		}
		
		Set<Collection> collections = story.getCollections();
		if(collections != null && collections.size() > 0){
			Collection collection = collections.iterator().next();
			if (collection != null) {
				CollectionIntro ci = new CollectionIntro();
//				ci.setId((Long) collection.getId());
//				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
//				ci.setInfo(collection.getInfo());
				User author = new User();//userDao.get(collection.getAuthorId());
				JSONObject json = new JSONObject();
				json.put("id",author.getId());
				json.put("username",author.getUsername());
				ci.setAuthor(json);
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				
				
				JSONObject collectionJson = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					collectionJson = JSONObject.fromObject(ci, configs);
				} else {
					collectionJson = JSONObject.fromObject(ci);
				}
				
				storyModel.setCollection(collectionJson);
			}
		}
		//Collection collection = this.collectionStoryDao.getCollectionByStoryId((Long) story.getId());
		
		storyModel.setSummary(story.getSummary());

		JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
		String type = jsonObject.getString("type");
		CoverMedia coverMedia = null;
		if (type.equals("text")) {
			coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
			storyModel.setCover_media(JSONObject.fromObject(coverMedia));
		} else if (type.equals("image")) {
			coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
			storyModel.setCover_media(JSONObject.fromObject(coverMedia));
		} else if (type.equals("multimedia")) {
			storyModel.setCover_media(jsonObject);
		}

		storyModel.setTitle(story.getTitle());

		return storyModel;
	}

	public Response createFollows(Long loginUserid, JSONObject arr) throws Exception {
		JSONObject json = new JSONObject();
		if (arr != null) {
			JSONArray userArr = JSONArray.fromObject(arr.get("user_id"));

			String path = getClass().getResource("/../../META-INF/getui.json").getPath();
			JSONObject jsonObject = ParseFile.parseJson(path);
			String appId = jsonObject.getString("appId");
			String appKey = jsonObject.getString("appKey");
			String masterSecret = jsonObject.getString("masterSecret");
			User user = (User) this.userDao.get(loginUserid);
			if (userArr != null) {
				Object[] userids = userArr.toArray();
				for (Object oid : userids) {
					Long id = (Long) oid;
					this.userDao.saveOrUpdate(user);
					Notification notification = this.notificationDao.getNotificationByAction(id, loginUserid, 3, 1);
					if (notification != null) {
						notification.setCreate_time(new Date());
						this.notificationDao.update(notification);
					} else {
						notification = new Notification();
//						notification.setSenderId(loginUserid);
						notification.setRecipientId(id);
						notification.setNotificationType(1);
						notification.setObjectType(3);
						notification.setObjectId(id);
						notification.setRead_already(false);
						notification.setStatus("enabled");
						this.notificationDao.save(notification);
					}

					this.notificationDao.saveOrUpdate(notification);
					Configuration conf = this.configurationDao.getConfByUserId(id);
					if (conf.isNew_follower_push()) {
						List<PushNotification> list = this.pushNotificationDao.getPushNotificationByUserid(id);
						int counts = this.notificationDao.getNotificationByRecipientId(id);
						String content = user.getUsername() + "关注了你";
						JSONObject j = new JSONObject();
						j.put("user_id",user.getId());
						PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content,j.toString());
					}
				}
				json.put("status", "success");
				return Response.status(Response.Status.CREATED).entity(json).build();
			}
			json.put("status", "invalid request");
			json.put("code", Integer.valueOf(10010));
			json.put("error_message", "Invalid request payload");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}

		json.put("status", "invalid request");
		json.put("code", Integer.valueOf(10010));
		json.put("error_message", "Invalid request payload");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}

	public List<EventModel> getTimelinesByRecommand(Long loginUserid, HttpServletRequest request) {
		log.debug("*** Get Home Timeline of the Authenticated User ***");
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		String date = request.getParameter("date");
		String date_before = request.getParameter("date_before");
		List<EventModel> eventList = new ArrayList<EventModel>();
		String followingId = loginUserid + "";
		int count = 20;
		User loginUser = userDao.get(loginUserid);
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByRecommand(loginUserid, count, followingId,date,date_before);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByRecommand(loginUserid, count, followingId,date,date_before);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByRecommand(loginUserid, since_id, count, 1,
					followingId,date,date_before);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByRecommand(loginUserid, since_id, count, 1,
					followingId,date,date_before);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByRecommand(loginUserid, max_id, count, 2,
					followingId,date,date_before);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByRecommand(loginUserid, max_id, count, 2,
					followingId,date,date_before);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelListByLoginid(timeline, loginUserid,loginUser);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		}
		log.debug("*** eventList ***" + JSONArray.fromObject(eventList));

		return eventList;
	}

	public List<EventModel> getTimelinesByUser(Long userId,HttpServletRequest request,Long loginUserid) {
		log.debug("*** Get Home Timeline of the Authenticated User ***");
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<EventModel> eventList = new ArrayList<EventModel>();
		int count = 20;
		String followingId = userId + "";
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(userId, count,
					followingId);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelByUser(timeline, loginUserid);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(userId, count,
					followingId);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelByUser(timeline, loginUserid);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(userId, since_id, count, 1,
					followingId);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelByUser(timeline, loginUserid);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(userId, since_id, count, 1,
					followingId);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelByUser(timeline, loginUserid);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(userId, max_id, count, 2,
					followingId);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelByUser(timeline, loginUserid);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Timeline> timelineList = this.timelineDao.getTimelinesPageByUserId(userId, max_id, count, 2,
					followingId);
			if ((timelineList != null) && (timelineList.size() > 0)) {
				EventModel event = null;
				for (Timeline timeline : timelineList) {
					event = getEventModelByUser(timeline, loginUserid);
					if (event.getContent() != null)
						eventList.add(event);
				}
			}
		}
		log.debug("*** eventList ***" + JSONArray.fromObject(eventList));

		return eventList;
	}

	public Response forgetPassword(HttpServletRequest request) throws Exception {
		String email = request.getParameter("email");
		JSONObject json = new JSONObject();
		if ((!Strings.isNullOrEmpty(email)) && (matchEmail(email))) {
			User user = this.userDao.getUserByEamil(email);
			if (user != null) {
				MailSenderInfo mailInfo = new MailSenderInfo();
				mailInfo.setMailServerHost("smtp.exmail.qq.com");
				mailInfo.setMailServerPort("465");
				mailInfo.setValidate(true);
				mailInfo.setUserName("password@onepage.mobi");
				mailInfo.setPassword("sfdr2014");
				mailInfo.setFromAddress("password@onepage.mobi");
				mailInfo.setToAddress(email);
				mailInfo.setSubject("找回密码");
				StringBuffer sb = new StringBuffer();
				String password = getStringRandom(8);
				sb.append("<div><span> 您的密码是：" + password + ",请登录后尽快修改密码�?</span></div>");

				mailInfo.setContent(sb.toString());

				//SimpleMailSender sms = new SimpleMailSender();
				SimpleMailSender.sendHtmlMail(mailInfo);
				String psw = EncryptionUtil.sha256(password);
				user.setPassword(psw);
				this.userDao.update(user);
				json.put("status", "success");
				return Response.status(Response.Status.OK).entity(json).build();
			}
			json.put("status", "invalid_email");
			json.put("code", Integer.valueOf(10090));
			json.put("error_message", "Email doesn't exist");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}

		json.put("status", "invalid_email");
		json.put("code", Integer.valueOf(10090));
		json.put("error_message", "Email doesn't exist");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}

	public String getStringRandom(int length) {
		String val = "";
		Random random = new Random();

		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";

			if ("char".equalsIgnoreCase(charOrNum)) {
				int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val = val + (char) (random.nextInt(26) + temp);
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				val = val + String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}

	public Response forgetPhone(HttpServletRequest request,String appVersion,String device) throws Exception {
		String phone = request.getParameter("phone");
		String zone = request.getParameter("zone");
		String code = request.getParameter("code");
		String timestamp = request.getParameter("timestamp");
		String password = request.getParameter("password");
		 String url = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = parseJson(url);
		String urlKey = jsonObject.getString("url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		
		if ((!Strings.isNullOrEmpty(phone)) && (!Strings.isNullOrEmpty(zone)) && (!Strings.isNullOrEmpty(code))
				&& (!Strings.isNullOrEmpty(password))) {

			Map<String,String> param = new HashMap<String, String>();
			param.put("mobile",phone);
			param.put("code",code);
			param.put("ip",ip);
			String paramMobile = publicParam(param);
			String result = HttpUtil.sendPostStr(urlKey+"/customer/account/check-mobile-for-reset-password", paramMobile);
			if (!Strings.isNullOrEmpty(result)) {
				JSONObject json = JSONObject.fromObject(result);
				int status = json.getInt("code");
				if (status == 10000) {
					Map<String,String> param_update = new HashMap<String, String>();
					param_update.put("mobile",phone);
					param_update.put("code",code);
					param_update.put("password", password);
					param_update.put("repeat_password", password);
					param_update.put("ip", ip);
					String passParam = publicParam(param_update);
					String result_update = HttpUtil.sendPostStr(urlKey+"/customer/account/reset-password", passParam);
					if(!Strings.isNullOrEmpty(result_update)){
						JSONObject result_res = JSONObject.fromObject(result_update);
						int code_res = result_res.getInt("code");
						if(code_res == 10000){
							Map<String,String> param_login = new HashMap<String, String>();
							param_login.put("username", phone);
							param_login.put("password", password);
							param_login.put("device", device);
							param_login.put("ip", ip);
							String params_login = publicParam(param_login);
							String res_login = HttpUtil.sendPostStr(urlKey+"/customer/account/login", params_login);
							JSONObject resp_json = JSONObject.fromObject(res_login);
							int code_login = resp_json.getInt("code");
			
							
							if(code_login == 10000){
								JSONObject data = resp_json.getJSONObject("data");
								int centre_id = data.getInt("userid");
								String fbToken = data.getString("token");
								JSONObject auth = new JSONObject();
								auth.put("userid", centre_id);
								auth.put("access_token", fbToken);
								auth.put("username", data.getString("username"));
								return Response.status(Response.Status.OK).entity(auth).build();
							}else if(code_login == 10001){
								JSONObject jo = new JSONObject();
								jo.put("status", "缺少参数");
								jo.put("code", 10600);
								jo.put("error_message", "缺少参数");
								return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
							}else if(code_login == 10004){
								JSONObject jo = new JSONObject();
								jo.put("status", "校验签名不通过");
								jo.put("code", 10620);
								jo.put("error_message", "校验签名不通过");
								return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
							}else if(code_login == 10110){
								JSONObject jo = new JSONObject();
								jo.put("status", "您输入的账号不存在，请先注册");
								jo.put("code", 10605);
								jo.put("error_message", "您输入的账号不存在，请先注册");
								return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
							}else if(code_login == 10111){
								JSONObject jo = new JSONObject();
								jo.put("status", "密码不存在");
								jo.put("code", 10606);
								jo.put("error_message", "密码不存在");
								return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
							}else if(code_login == 10112){
								JSONObject jo = new JSONObject();
								jo.put("status", "用户名或密码不正确");
								jo.put("code", 10607);
								jo.put("error_message", "用户名或密码不正确");
								return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
							}
							
						}else if(code_res == 10001){
							json.put("status", "缺少参数");
							json.put("code", 10600);
							json.put("error_message", "缺少参数");
							return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
						}else if(code_res == 10110){
							json.put("status", "您输入的账号不存在，请先注册");
							json.put("code", 10605);
							json.put("error_message", "您输入的账号不存在，请先注册");
							return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
						}else if(code_res == 10115){
							json.put("status", "原密码格式错误");
							json.put("code", 10610);
							json.put("error_message", "原密码格式错误");
							return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
						}else if(code_res == 10116){
							json.put("status", "新密码格式错误");
							json.put("code", 10611);
							json.put("error_message", "新密码格式错误");
							return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
						}else if(code_res == 10117){
							json.put("status", "确认密码与新密码不一致");
							json.put("code", 10612);
							json.put("error_message", "确认密码与新密码不一致");
							return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
						}else if(code_res == 10118){
							json.put("status", "原密码错误");
							json.put("code", 10613);
							json.put("error_message", "原密码错误");
							return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
						}else if(code_res == 10120){
							json.put("status", "密码修改失败");
							json.put("code", 10614);
							json.put("error_message", "密码修改失败");
							return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
						}else{
							json.put("status", "校验码不正确");
							json.put("code", 10626);
							json.put("error_message", "校验码不正确");
							return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
						}
					}
					
				}else if(status == 10001){
					json.put("status", "缺少参数");
					json.put("code", 10651);
					json.put("error_message", "缺少参数");
					return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
				}else if(status == 10110){
					json.put("status", "手机号格式错误");
					json.put("code", 10650);
					json.put("error_message", "手机号格式错误");
					return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
				}
				
			} else {
				JSONObject j = new JSONObject();
				j.put("status", "Invalid_phone");
				j.put("code", Integer.valueOf(10091));
				j.put("error_message", "phone or code is not true");
				return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
			}
		

			return null;
		}
		JSONObject j = new JSONObject();
		j.put("status", "Invalid_phone");
		j.put("code", Integer.valueOf(10091));
		j.put("error_message", "phone or code is not true");
		return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
	}

	public String requestData(String address, String params) {
		HttpURLConnection conn = null;
		try {
			TrustManager[] trustAllCerts = { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());

			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					return urlHostName.equals(session.getPeerHost());
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(hv);

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			URL url = new URL(address);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			if (params != null) {
				conn.setDoOutput(true);
				DataOutputStream out = new DataOutputStream(conn.getOutputStream());
				out.write(params.getBytes(Charset.forName("UTF-8")));
				out.flush();
				out.close();
			}
			conn.connect();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String result = parsRtn(conn.getInputStream());
				return result;
			}
			System.out.println(conn.getResponseCode() + " " + conn.getResponseMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return null;
	}

	private String parsRtn(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		boolean first = true;
		while ((line = reader.readLine()) != null) {
			if (first)
				first = false;
			else {
				buffer.append("\n");
			}
			buffer.append(line);
		}
		return buffer.toString();
	}

	public Response getUserByUserName(HttpServletRequest request, Long loginUserid) {
		String username = request.getParameter("user_name");
		System.out.println("username--->>>>>>>>>>>>>>>>>>"+username);
		String website = null;
		if (!Strings.isNullOrEmpty(username)) {
			try {
				username = new String(username.getBytes("utf-8"), "utf-8");
				System.out.println("username--->>>>>>>>>>>>>>>>>>"+username);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			List<User> userList = this.userDao.getUserByName(username);
			List<UserParentModel> upmList = new ArrayList<UserParentModel>();
			if ((userList != null) && (userList.size() > 0)) {
				for (User user : userList) {
					Long userId = (Long) user.getId();
					
					int repostCount = 0;//this.republishDao.userRepostCount(userId);
					Set<Story> sSet = user.getRepost_story();
					if(sSet != null && sSet.size() > 0){
						repostCount = sSet.size();
					}
					int storyCount = this.storyDao.getStoryCount((Long) user.getId());
//					Follow loginFollow = this.followDao.getFollow(loginUserid, userId);
//					boolean followed_by_current_user = false;
//					boolean is_following_current_user = false;
//					if (loginFollow != null) {
//						followed_by_current_user = true;
//					}
//					Follow currentFollow = this.followDao.getFollow(userId, loginUserid);
//					if (currentFollow != null) {
//						is_following_current_user = true;
//					}
					JSONObject avatarImageJson = null;
					if ((user.getAvatarImage() != null) && (!user.getAvatarImage().equals(""))) {
						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
					}

					JSONObject coverImageJson = null;
					if ((user.getCoverImage() != null) && (!user.getCoverImage().equals(""))) {
						coverImageJson = JSONObject.fromObject(user.getCoverImage());
					}

					if (!Strings.isNullOrEmpty(user.getWebsite())) {
						website = user.getWebsite();
					}

					UserParentModel userModel = null;
					if ((user.getUser_type().equals("normal")) || (user.getUser_type().equals("vip"))
							|| (user.getUser_type().equals("official")) || 
							(user.getUser_type().equals("admin"))
							|| (user.getUser_type().equals("super_admin"))) {
						userModel = new UserModel((Long) user.getId(), user.getUsername(), user.getEmail(),
								user.getCreated_time(), user.getStatus(), user.getIntroduction(), avatarImageJson,
								coverImageJson,  repostCount, storyCount, 0, 0,
								website, false, false,
								user.getUser_type(),user.getGender());
					} else if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
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
						userModel = new UserPublisherModel((Long) user.getId(), user.getUsername(), user.getEmail(),
								user.getCreated_time(), user.getStatus(), user.getIntroduction(), avatarImageJson,
								coverImageJson,  repostCount, storyCount, 0, 0,
								website, false,false, 
								user.getUser_type(), publisherList,user.getGender());
					}

					upmList.add(userModel);
				}

				System.out.println("search--->>>" + JSONArray.fromObject(upmList));
			}

			return Response.status(Response.Status.OK).entity(upmList).build();
		}

		JSONObject json = new JSONObject();
		json.put("status", "no_resource");
		json.put("code", Integer.valueOf(10011));
		json.put("error_message", "The user does not exist");
		return Response.status(Response.Status.OK).entity(json).build();
	}

	@Override
	public Response profile(Long userId, Long loginUserid,HttpServletRequest request)throws Exception {
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		
		JSONObject resp = new JSONObject();
		int count = 20;
		JSONObject profile = new JSONObject();
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		if(!Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("relation_type", "20");
		map.put("ip", ip);
		map.put("device", device);
		map.put("author_id", String.valueOf(userId));
		
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
			map.put("page_count", String.valueOf(count));
		} else if ((!Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			map.put("page_count", String.valueOf(count));
		} else if ((Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", String.valueOf(max_id));
		} else if ((!Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", String.valueOf(max_id));
		}
		String params = carPublicParam(map);
		String result = HttpUtil.sendGetStr(car_url+"/content/get-list", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){
				JSONArray data = resJson.getJSONArray("data");
				JSONObject contentJson = null;
				JSONObject eventJson = null;
				JSONObject storyJson = null;
				List<JSONObject> cJsonList = new ArrayList<JSONObject>();
				if(data != null && data.size() > 0){
					for(Object o:data){
						contentJson = JSONObject.fromObject(o);
						eventJson = new JSONObject();
						storyJson = new JSONObject();
						Iterator<String> iter = contentJson.keys();
						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						while(iter.hasNext()){
							String key = iter.next();
							String val = contentJson.getString(key);
							if(Strings.isNullOrEmpty(val) || val.equals("null")){
								delArray.add(key);
							}
						}
						
						JSONObject cJson = null;
						if ((delArray != null) && (delArray.size() > 0)) {
							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
							configs.setIgnoreDefaultExcludes(false);
							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							cJson = JSONObject.fromObject(contentJson, configs);
						} else {
							cJson = JSONObject.fromObject(contentJson);
						}
						storyJson.put("story", cJson);
						eventJson.put("content", storyJson);
						eventJson.put("event_type", "post");
						eventJson.put("event_time", contentJson.getLong("created_at"));
						cJsonList.add(eventJson);
					}
					
				}
				profile.put("event",cJsonList);
			}else if (code == 14005){
				resp.put("status", "关系对象类型错误");
				resp.put("code", 10504);
				resp.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
		
		Map<String,String> userMap = new HashMap<String, String>();
		
		userMap.put("ip", ip);
		userMap.put("device", device);
		userMap.put("id", userId.toString());
		userMap.put("login_id", loginUserid.toString());
		
		String userParams = carPublicParam(userMap);
		String user_result = HttpUtil.sendGetStr(car_url+"/member/get", userParams);
		
		if(!Strings.isNullOrEmpty(user_result)){
			JSONObject resJson = JSONObject.fromObject(user_result);
			int code = resJson.getInt("code");
			if(code == 10000){
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
				
				JSONObject uJson = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					uJson = JSONObject.fromObject(data, configs);
				} else {
					uJson = JSONObject.fromObject(data);
				}
			
				profile.put("user",uJson);
			}else if (code == 14005){
				resp.put("status", "关系对象类型错误");
				resp.put("code", 10504);
				resp.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
		System.out.println("-->"+profile);
		return Response.status(Response.Status.OK).entity(profile).build();
	}
	
	public JSONObject getStoryJson(Story story,Long loginUserid,User loginUser){
		StoryEvent storyModel = new StoryEvent();
        storyModel.setId(story.getId());
        storyModel.setImage_count(story.getImage_count());
        storyModel.setUrl(story.getTinyURL());
        User user = story.getUser();
        JSONObject authorJson = new JSONObject();
        JSONObject coverImageJson = null;
        if (!Strings.isNullOrEmpty(user.getCoverImage())) {
          coverImageJson = JSONObject.fromObject(user
            .getCoverImage());
        }

        JSONObject avatarImageJson = null;
        if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
          avatarImageJson = JSONObject.fromObject(user
            .getAvatarImage());
        }
        authorJson.put("id", user.getId());
        authorJson.put("cover_image", coverImageJson);
        authorJson.put("username", user.getUsername());
        authorJson.put("introduction", user.getIntroduction());
        authorJson.put("avatar_image", avatarImageJson);
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
        Set<Comment> set = story.getComments();
		storyModel.setComment_count(set.size());
        storyModel.setCreated_time(story.getCreated_time());
        if(loginUserid != null && loginUserid > 0){
        	if(loginUser != null){
        		Set<Story> sSet = loginUser.getRepost_story();
    			List<Story> rsList = new ArrayList<Story>();
    			if(sSet != null && sSet.size() > 0 ){
    				Iterator<Story> it = sSet.iterator();
    				while(it.hasNext()){
    					rsList.add(it.next());
    				}
    				if(rsList.contains(story)){
    					storyModel.setRepost_by_current_user(true);
    				}else{
    					storyModel.setRepost_by_current_user(false);
    				}
    			}else{
    				storyModel.setRepost_by_current_user(false);
    			}
        	}else{
        		storyModel.setRepost_by_current_user(false);
        	}
        	
        }else{
        	storyModel.setRepost_by_current_user(false);
        }
       
        Collection collection = null;
        Set<Collection> cSet = story.getCollections();
        if(cSet != null && cSet.size() > 0){
        	collection = cSet.iterator().next();
        	if (collection != null) {
				CollectionIntro ci = new CollectionIntro();
//				ci.setId((Long) collection.getId());
//				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
//				ci.setInfo(collection.getInfo());
				User author = new User();//userDao.get(collection.getAuthorId());
				JSONObject json = new JSONObject();
				json.put("id",author.getId());
				json.put("username",author.getUsername());
				ci.setAuthor(json);
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				
				
				JSONObject collectionJson = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					collectionJson = JSONObject.fromObject(ci, configs);
				} else {
					collectionJson = JSONObject.fromObject(ci);
				}
				
				storyModel.setCollection(collectionJson);
			}
        }
			
        JSONObject jsonObject = JSONObject.fromObject(story
          .getCover_page());
        log.debug("***story.getCover_page()***" + jsonObject);
        String type = jsonObject.getString("type");

        if (type.equals("text")) {
          TextCover coverMedia = (TextCover)
            JSONObject.toBean(jsonObject, TextCover.class);
          log.debug("****get cover media **********" + 
            JSONObject.fromObject(coverMedia));
          storyModel.setCover_media(
            JSONObject.fromObject(coverMedia));
        } else if (type.equals("image")) {
          ImageCover coverMedia = (ImageCover)
            JSONObject.toBean(jsonObject, ImageCover.class);
          storyModel.setCover_media(
            JSONObject.fromObject(coverMedia));
        } else if (type.equals("multimedia")) {
          storyModel.setCover_media(jsonObject);
        }

        storyModel.setTitle(story.getTitle());
        storyModel.setSummary(story.getSummary());
        JsonConfig configs = new JsonConfig();
        List<String> delArray1 = new ArrayList<String>();
        if (Strings.isNullOrEmpty(story.getTitle())) {
          delArray1.add("title");
        }
        if(collection == null){
     	   delArray1.add("collection");
        }
        
        if (Strings.isNullOrEmpty(story.getSummary())) {
            delArray1.add("summary");
          }
        JSONObject storyJson = null;
        if ((delArray1 != null) && (delArray1.size() > 0)) {
          configs.setExcludes(
            (String[])delArray1
            .toArray(new String[delArray1.size()]));
          configs.setIgnoreDefaultExcludes(false);
          configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

          storyJson = JSONObject.fromObject(storyModel, 
            configs);
        } else {
          storyJson = JSONObject.fromObject(storyModel);
        }
        
        return storyJson;
	}
	//推荐
	@Override
	public Response getTimelinesBySlides(Long loginUserid, HttpServletRequest request,HttpServletResponse response,String appVersion)throws Exception {
		
		log.debug("*** Get Home Timeline of the Authenticated User ***");
		JSONObject resp = new JSONObject();
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		String loginUserids = "";
		if(obj != null){
			loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		
//		List<Slide> slideList = null;
		List<SlideModel> smList = new ArrayList<SlideModel>();
		JSONObject homepage = new JSONObject();
//		List<EventModel> emList = new ArrayList<EventModel>();
		int count = 20;
		String path1 = getClass().getResource("/../../META-INF/image.json").getPath();
		JSONObject json = ParseFile.parseJson(path1);
		String url = json.getString("url");
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
		
		map.put("relation_type", "20");
		map.put("ip", ip);
		map.put("device", device);
		map.put("is_highlighted", "1");
		
		
		if(loginUserid == null || loginUserid == 0l){
//			SlideModel sm = null;
			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
				map.put("page_count", String.valueOf(count));
			} else if ((!Strings.isNullOrEmpty(countStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				map.put("page_count", String.valueOf(count));
//				slideList = slideDao.getSlideList();
//				if(slideList != null && slideList.size() > 0){
//					for(Slide s:slideList){
//						sm = getSlideModel(s);
//						smList.add(sm);
//					}
//				}
//				homepage.put("slides",smList);
//				homepage.put("events",emList);
			} else if ((Strings.isNullOrEmpty(countStr)) 
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				map.put("page_count", String.valueOf(count));
				map.put("prev_id", String.valueOf(max_id));
			} else if ((!Strings.isNullOrEmpty(countStr)) 
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				map.put("page_count", String.valueOf(count));
				map.put("prev_id", String.valueOf(max_id));
			}
			String params = carPublicParam(map);
			String result = HttpUtil.sendGetStr(car_url+"/content/get-list", params);
			log.debug("*** content list ***"+result);
			if(!Strings.isNullOrEmpty(result)){
				JSONObject resJson = JSONObject.fromObject(result);
				int code = resJson.getInt("code");
				if(code == 10000){
					
					JSONArray data = resJson.getJSONArray("data");
					JSONObject contentJson = null;
					JSONObject eventJson = null;
					JSONObject storyJson = null;
					List<JSONObject> cJsonList = new ArrayList<JSONObject>();
					if(data != null && data.size() > 0){
						for(Object o:data){
							contentJson = JSONObject.fromObject(o);
							eventJson = new JSONObject();
							storyJson = new JSONObject();
							Iterator<String> iter = contentJson.keys();
							JsonConfig configs = new JsonConfig();
							List<String> delArray = new ArrayList<String>();
							while(iter.hasNext()){
								String key = iter.next();
								String val = contentJson.getString(key);
								if(Strings.isNullOrEmpty(val) || val.equals("null")){
									delArray.add(key);
								}
							}
							
							JSONObject cJson = null;
							if ((delArray != null) && (delArray.size() > 0)) {
								configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
								configs.setIgnoreDefaultExcludes(false);
								configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

								cJson = JSONObject.fromObject(contentJson, configs);
							} else {
								cJson = JSONObject.fromObject(contentJson);
							}
							storyJson.put("story", cJson);
							eventJson.put("content", storyJson);
							eventJson.put("event_type", "post");
							eventJson.put("event_time", contentJson.getLong("created_at"));
							cJsonList.add(eventJson);
						}
						
					}
					homepage.put("events",cJsonList);	
				
			
				}else if (code == 14005){
					resp.put("status", "关系对象类型错误");
					resp.put("code", 10504);
					resp.put("error_message", "关系对象类型错误");
					return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
				}
			}
		}else{
			map.put("user_id", loginUserid.toString());
			if ((Strings.isNullOrEmpty(countStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				
				map.put("page_count", String.valueOf(count));
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				map.put("page_count", String.valueOf(count));

			}  else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				map.put("page_count", String.valueOf(count));
				map.put("prev_id", String.valueOf(max_id));
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				map.put("page_count", String.valueOf(count));
				map.put("prev_id", String.valueOf(max_id));
			}
			String params = carPublicParam(map);
			String result = HttpUtil.sendGetStr(car_url+"/content/get-list", params);
			if(!Strings.isNullOrEmpty(result)){
				JSONObject resJson = JSONObject.fromObject(result);
				int code = resJson.getInt("code");
				if(code == 10000){
					JSONArray data = resJson.getJSONArray("data");
					JSONObject contentJson = null;
					JSONObject eventJson = null;
					JSONObject storyJson = null;
					List<JSONObject> cJsonList = new ArrayList<JSONObject>();
					if(data != null && data.size() > 0){
						for(Object o:data){
							contentJson = JSONObject.fromObject(o);
							eventJson = new JSONObject();
							storyJson = new JSONObject();
							Iterator<String> iter = contentJson.keys();
							JsonConfig configs = new JsonConfig();
							List<String> delArray = new ArrayList<String>();
							while(iter.hasNext()){
								String key = iter.next();
								String val = contentJson.getString(key);
								if(Strings.isNullOrEmpty(val) || val.equals("null")){
									delArray.add(key);
								}
							}
							
							JSONObject cJson = null;
							if ((delArray != null) && (delArray.size() > 0)) {
								configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
								configs.setIgnoreDefaultExcludes(false);
								configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

								cJson = JSONObject.fromObject(contentJson, configs);
							} else {
								cJson = JSONObject.fromObject(contentJson);
							}
							storyJson.put("story", cJson);
							eventJson.put("content", storyJson);
							eventJson.put("event_type", "post");
							eventJson.put("event_time", contentJson.getLong("created_at"));
							cJsonList.add(eventJson);
						}
						
					}
					homepage.put("events",cJsonList);
				}else if (code == 14005){
					resp.put("status", "关系对象类型错误");
					resp.put("code", 10504);
					resp.put("error_message", "关系对象类型错误");
					return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
				}
			}
			
		}
		System.out.println("homepage-->"+homepage);
		return Response.status(Response.Status.OK).entity(homepage).build();
	}
	

	public EventModel getEventModelListByLoginidObject(Timeline timeline, Long loginUserid,CollectionStory cs,Republish republish) {
		try {
			EventModel event = new EventModel();
			event.setId((Long) timeline.getId());
			event.setEvent_time(timeline.getCreateTime());
			event.setEvent_type(timeline.getType());
			JSONObject contentJson = new JSONObject();
			Story story = timeline.getStory();
			
			StoryEvent storyModel = new StoryEvent();

			if (story != null) {
				Long storyId = story.getId();
				User u = story.getUser();
				if (u.getId().equals(loginUserid)) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					//User user = story.getUser();
					JSONObject authorJson = new JSONObject();
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(u.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(u.getCoverImage());
					}

					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(u.getAvatarImage());
					}
					authorJson.put("id", u.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", u.getUsername());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}

					authorJson.put("user_type", u.getUser_type());
					storyModel.setAuthor(authorJson);
					storyModel.setCreated_time(story.getCreated_time());
					Collection collection = cs.getCollection();
					if (collection != null) {
						CollectionIntro ci = new CollectionIntro();
//						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollection_name());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
//						ci.setInfo(collection.getInfo());
						User author = new User(); //userDao.get(collection.getAuthorId());
						JSONObject json = new JSONObject();
						json.put("id",author.getId());
						json.put("username",author.getUsername());
						ci.setAuthor(json);
						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						
						
						JSONObject collectionJson = null;
						if ((delArray != null) && (delArray.size() > 0)) {
							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
							configs.setIgnoreDefaultExcludes(false);
							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							collectionJson = JSONObject.fromObject(ci, configs);
						} else {
							collectionJson = JSONObject.fromObject(ci);
						}
						
						storyModel.setCollection(collectionJson);
					}
					storyModel.setSummary(story.getSummary());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
					log.debug("***story.getCover_page()***" + jsonObject);
					String type = jsonObject.getString("type");

					if (type.equals("text")) {
						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("image")) {
						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("multimedia")) {
						storyModel.setCover_media(jsonObject);
					}

					storyModel.setTitle(story.getTitle());

					storyModel.setAuthor(authorJson);
					storyModel.setRecommend_date(story.getRecommend_date());
					if(loginUserid != null && loginUserid > 0){
						if (republish != null)
							storyModel.setRepost_by_current_user(true);
						else {
							storyModel.setRepost_by_current_user(false);
						}
					}else{
						storyModel.setRepost_by_current_user(false);
					}
					
					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if (collection == null) {
						delArray.add("collection");
					}
					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}

					contentJson.put("story", storyJson);
				} else if (story.getStatus().equals("publish")) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					JSONObject authorJson = new JSONObject();
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(u.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(u.getCoverImage());
					}

					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(u.getAvatarImage());
					}
					authorJson.put("id", u.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", u.getUsername());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}
					authorJson.put("user_type", u.getUser_type());
					if(loginUserid != null && loginUserid > 0){
						if (republish != null)
							storyModel.setRepost_by_current_user(true);
						else {
							storyModel.setRepost_by_current_user(false);
						}
					}else{
						storyModel.setRepost_by_current_user(false);
					}
					
					storyModel.setAuthor(authorJson);
					Collection collection = cs.getCollection();
					if (collection != null) {
						CollectionIntro ci = new CollectionIntro();
//						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollection_name());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
//						ci.setInfo(collection.getInfo());
						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						
						
						JSONObject collectionJson = null;
						if ((delArray != null) && (delArray.size() > 0)) {
							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
							configs.setIgnoreDefaultExcludes(false);
							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							collectionJson = JSONObject.fromObject(ci, configs);
						} else {
							collectionJson = JSONObject.fromObject(ci);
						}
						
						storyModel.setCollection(collectionJson);
					}
					storyModel.setSummary(story.getSummary());
					storyModel.setCreated_time(story.getCreated_time());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
					log.debug("***story.getCover_page()***" + jsonObject);
					String type = jsonObject.getString("type");

					if (type.equals("text")) {
						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("image")) {
						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("multimedia")) {
						storyModel.setCover_media(jsonObject);
					}

					storyModel.setTitle(story.getTitle());

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if (collection == null) {
						delArray.add("collection");
					}
					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}
					contentJson.put("story", storyJson);
				}
				if (timeline.getType().equals("recommandation")) {
					event.setContent(contentJson);
				}else if (timeline.getType().equals("post")) {
					event.setContent(contentJson);
				} else if (timeline.getType().equals("repost")) {
					UserIntro userIntro = new UserIntro();
					User user = (User) this.userDao.get(timeline.getCreatorId());
					JSONObject userIntroJson = null;
					if (user != null) {
						userIntro.setId((Long) user.getId());
						userIntro.setUsername(user.getUsername());
						userIntro.setIntroduction(user.getIntroduction());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
						}

						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						if (Strings.isNullOrEmpty(user.getAvatarImage())) {
							delArray.add("avatar_image");
						}

						if ((delArray != null) && (delArray.size() > 0)) {
							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
							configs.setIgnoreDefaultExcludes(false);
							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							userIntroJson = JSONObject.fromObject(userIntro, configs);
						} else {
							userIntroJson = JSONObject.fromObject(userIntro);
						}
					}

					contentJson.put("repost_by", userIntroJson);
					event.setContent(contentJson);
				}
			}

			return event;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	public EventModel getEventModelListByLoginid(Timeline timeline, 
			Long loginUserid,User loginUser) {
		try {
			EventModel event = new EventModel();
			event.setId((Long) timeline.getId());
			event.setEvent_time(timeline.getCreateTime());
			event.setEvent_type(timeline.getType());
			JSONObject contentJson = new JSONObject();
			Story story = timeline.getStory();
			StoryEventNew storyModel = new StoryEventNew();

			if (story != null) {
				Long storyId = story.getId();
				User u = story.getUser();
				Collection collection = null;
				if (u.getId().equals(loginUserid)) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					JSONObject authorJson = new JSONObject();
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(u.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(u.getCoverImage());
					}

					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(u.getAvatarImage());
					}
					authorJson.put("id", u.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", u.getUsername());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}
					
					authorJson.put("user_type", u.getUser_type());
					storyModel.setAuthor(authorJson);
					storyModel.setCreated_time(story.getCreated_time());
					Set<User> like_set = story.getLike_users();
					if(like_set != null && like_set.size() > 0){
						storyModel.setLike_count(like_set.size());
					}else{
						storyModel.setLike_count(0);
					}
					
					
					storyModel.setSummary(story.getSummary());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
					log.debug("***story.getCover_page()***" + jsonObject);
					String type = jsonObject.getString("type");

					if (type.equals("text")) {
						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("image")) {
						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("multimedia")) {
						storyModel.setCover_media(jsonObject);
					}

					storyModel.setTitle(story.getTitle());

					int count = 0;//this.commentDao.getCommentCountById((Long) story.getId());
					Set<Comment> coSet = story.getComments();
					if(coSet != null && coSet.size() > 0){
						Iterator<Comment> it = coSet.iterator();
						while(it.hasNext()){
							Comment c = it.next();
							if(c.getStatus().equals("enabled")){
								count++;
							}
						}
					}
					storyModel.setComment_count(count);
					storyModel.setRecommend_date(story.getRecommend_date());
					
					storyModel.setRepost_by_current_user(false);
					
					Set<User> uSet = story.getLike_users();
					if(uSet != null && uSet.size() > 0){
						storyModel.setLike_count(uSet.size());
					}
					
					Set<Story> likeStory = loginUser.getLike_story();
					List<Story> lsList = new ArrayList<Story>();
					if(likeStory != null && likeStory.size() > 0 ){
						Iterator<Story> it = likeStory.iterator();
						while(it.hasNext()){
							lsList.add(it.next());
						}
						if(lsList.contains(story)){
							storyModel.setLiked_by_current_user(true);;
						}else{
							storyModel.setLiked_by_current_user(false);
						}
					}else{
						storyModel.setLiked_by_current_user(false);
					}
					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					/*Set<Columns> colSet = story.getColumns();
					if(colSet != null && colSet.size() > 0){
						Iterator<Columns> iter = colSet.iterator();
						Columns c = iter.next();
						JSONObject columnsJson = new JSONObject();
						columnsJson.put("id",c.getId());
						columnsJson.put("column_name",c.getColumn_name());
						storyModel.setColumns(columnsJson);
					}else{
						delArray.add("columns");
					}*/
					
					Set<Collection> cSet = story.getCollections();
					if(cSet != null && cSet.size() > 0){
						Iterator<Collection> iter = cSet.iterator();
						List<JSONObject> collections = new ArrayList<JSONObject>();
						while(iter.hasNext()){
							Collection c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("collection_name", c.getCollection_name());
							collections.add(json);
						}
						storyModel.setCollections(collections);
					}else{
						delArray.add("collections");
					}
					
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if (collection == null) {
						delArray.add("collection");
					}
					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}

					contentJson.put("story", storyJson);
				} else if (story.getStatus().equals("publish")) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					JSONObject authorJson = new JSONObject();
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(u.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(u.getCoverImage());
					}

					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(u.getAvatarImage());
					}
					authorJson.put("id", u.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", u.getUsername());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}
					authorJson.put("user_type", u.getUser_type());
					if ((u.getUser_type().equals("publisher")) || (u.getUser_type().equals("media"))) {
						Set<PublisherInfo> publisherSet = u.getPublisherInfos();
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
					if(loginUser != null){
						Set<Story> sSet = loginUser.getRepost_story();
						List<Story> rsList = new ArrayList<Story>();
						if(sSet != null && sSet.size() > 0 ){
							Iterator<Story> it = sSet.iterator();
							while(it.hasNext()){
								rsList.add(it.next());
							}
							if(rsList.contains(story)){
								storyModel.setRepost_by_current_user(true);
							}else{
								storyModel.setRepost_by_current_user(false);
							}
						}else{
							storyModel.setRepost_by_current_user(false);
						}
						
						Set<User> like_set = story.getLike_users();
						if(like_set != null && like_set.size() > 0){
							storyModel.setLike_count(like_set.size());
						}else{
							storyModel.setLike_count(0);
						}
						Set<Story> likeStory = loginUser.getLike_story();
						List<Story> lsList = new ArrayList<Story>();
						if(likeStory != null && likeStory.size() > 0 ){
							Iterator<Story> it = likeStory.iterator();
							while(it.hasNext()){
								lsList.add(it.next());
							}
							if(lsList.contains(story)){
								storyModel.setLiked_by_current_user(true);;
							}else{
								storyModel.setLiked_by_current_user(false);
							}
						}else{
							storyModel.setRepost_by_current_user(false);
							storyModel.setLiked_by_current_user(false);
						}
						
					}else{
						storyModel.setRepost_by_current_user(false);
						storyModel.setLiked_by_current_user(false);
					}
					
					storyModel.setAuthor(authorJson);
					int count = 0;//this.commentDao.getCommentCountById((Long) story.getId());
					Set<Comment> coSet = story.getComments();
					if(coSet != null && coSet.size() > 0){
						Iterator<Comment> it = coSet.iterator();
						while(it.hasNext()){
							Comment c = it.next();
							if(c.getStatus().equals("enabled")){
								count++;
							}
						}
					}
					storyModel.setComment_count(count);
					
					storyModel.setSummary(story.getSummary());
					storyModel.setCreated_time(story.getCreated_time());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
					log.debug("***story.getCover_page()***" + jsonObject);
					String type = jsonObject.getString("type");

					if (type.equals("text")) {
						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("image")) {
						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("multimedia")) {
						storyModel.setCover_media(jsonObject);
					}

					storyModel.setTitle(story.getTitle());

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					/*Set<Columns> colSet = story.getColumns();
					if(colSet != null && colSet.size() > 0){
						Iterator<Columns> iter = colSet.iterator();
						Columns c = iter.next();
						JSONObject columnsJson = new JSONObject();
						columnsJson.put("id",c.getId());
						columnsJson.put("column_name",c.getColumn_name());
						storyModel.setColumns(columnsJson);
					}else{
						delArray.add("columns");
					}*/
					
					Set<Collection> cSet = story.getCollections();
					if(cSet != null && cSet.size() > 0){
						Iterator<Collection> iter = cSet.iterator();
						List<JSONObject> collections = new ArrayList<JSONObject>();
						while(iter.hasNext()){
							Collection c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("collection_name", c.getCollection_name());
							collections.add(json);
						}
						storyModel.setCollections(collections);
					}else{
						delArray.add("collections");
					}
					
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if (collection == null) {
						delArray.add("collection");
					}
					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}
					contentJson.put("story", storyJson);
				}
				if (timeline.getType().equals("recommandation")) {
					event.setContent(contentJson);
				}else if (timeline.getType().equals("post")) {
					event.setContent(contentJson);
				} else if (timeline.getType().equals("repost")) {
					UserIntro userIntro = new UserIntro();
					User user = (User) this.userDao.get(timeline.getCreatorId());
					JSONObject userIntroJson = null;
					if (user != null) {
						userIntro.setId((Long) user.getId());
						userIntro.setUsername(user.getUsername());
						userIntro.setIntroduction(user.getIntroduction());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
						}

						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						if (Strings.isNullOrEmpty(user.getAvatarImage())) {
							delArray.add("avatar_image");
						}

						if ((delArray != null) && (delArray.size() > 0)) {
							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
							configs.setIgnoreDefaultExcludes(false);
							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							userIntroJson = JSONObject.fromObject(userIntro, configs);
						} else {
							userIntroJson = JSONObject.fromObject(userIntro);
						}
					}

					contentJson.put("repost_by", userIntroJson);
					event.setContent(contentJson);
				}
			}

			return event;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	public EventModel getEventModelListByLoginidNew(Timeline timeline, 
			Long loginUserid,User loginUser) {
		try {
			EventModel event = new EventModel();
			event.setId((Long) timeline.getId());
			event.setEvent_time(timeline.getCreateTime());
			event.setEvent_type(timeline.getType());
			JSONObject contentJson = new JSONObject();
			Story story = timeline.getStory();
			StoryEventNew storyModel = new StoryEventNew();

			if (story != null) {
				Long storyId = story.getId();
				User u = story.getUser();
				if (u.getId().equals(loginUserid)) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					JSONObject authorJson = new JSONObject();
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(u.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(u.getCoverImage());
					}

					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(u.getAvatarImage());
					}
					authorJson.put("id", u.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", u.getUsername());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}
					
					authorJson.put("user_type", u.getUser_type());
					storyModel.setAuthor(authorJson);
					storyModel.setCreated_time(story.getCreated_time());
					Set<User> like_set = story.getLike_users();
					if(like_set != null && like_set.size() > 0){
						storyModel.setLike_count(like_set.size());
					}else{
						storyModel.setLike_count(0);
					}
					
					
					
					
					
					storyModel.setSummary(story.getSummary());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
					log.debug("***story.getCover_page()***" + jsonObject);
					String type = jsonObject.getString("type");

					if (type.equals("text")) {
						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("image")) {
						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("multimedia")) {
						storyModel.setCover_media(jsonObject);
					}

					storyModel.setTitle(story.getTitle());

					int count = 0;//this.commentDao.getCommentCountById((Long) story.getId());
					Set<Comment> coSet = story.getComments();
					if(coSet != null && coSet.size() > 0){
						Iterator<Comment> it = coSet.iterator();
						while(it.hasNext()){
							Comment c = it.next();
							if(c.getStatus().equals("enabled")){
								count++;
							}
						}
					}
					storyModel.setComment_count(count);
					storyModel.setRecommend_date(story.getRecommend_date());
					
					storyModel.setRepost_by_current_user(false);
					
					Set<User> uSet = story.getLike_users();
					if(uSet != null && uSet.size() > 0){
						storyModel.setLike_count(uSet.size());
					}
					
					Set<Story> likeStory = loginUser.getLike_story();
					List<Story> lsList = new ArrayList<Story>();
					if(likeStory != null && likeStory.size() > 0 ){
						Iterator<Story> it = likeStory.iterator();
						while(it.hasNext()){
							lsList.add(it.next());
						}
						if(lsList.contains(story)){
							storyModel.setLiked_by_current_user(true);;
						}else{
							storyModel.setLiked_by_current_user(false);
						}
					}else{
						storyModel.setLiked_by_current_user(false);
					}
					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					
					/*Set<Columns> colSet = story.getColumns();
					if(colSet != null && colSet.size() > 0){
						Iterator<Columns> iter = colSet.iterator();
						if(iter.hasNext()){
							Columns c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("column_name", c.getColumn_name());
							storyModel.setColumns(json);
						}
					}else{
						delArray.add("columns");
					}*/
					
					Set<Collection> cSet = story.getCollections();
					List<JSONObject> collections = new ArrayList<JSONObject>();
					if(cSet != null && cSet.size() > 0){
						Iterator<Collection> iter = cSet.iterator();
						while(iter.hasNext()){
							Collection c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("collection_name", c.getCollection_name());
							collections.add(json);
							
						}
						storyModel.setCollections(collections);
					}else{
						delArray.add("collections");
					}
					
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}

					contentJson.put("story", storyJson);
				} else if (story.getStatus().equals("publish")) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					JSONObject authorJson = new JSONObject();
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(u.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(u.getCoverImage());
					}

					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(u.getAvatarImage());
					}
					authorJson.put("id", u.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", u.getUsername());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}
					authorJson.put("user_type", u.getUser_type());
					if ((u.getUser_type().equals("publisher")) || (u.getUser_type().equals("media"))) {
						Set<PublisherInfo> publisherSet = u.getPublisherInfos();
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
					if(loginUser != null){
						Set<Story> sSet = loginUser.getRepost_story();
						List<Story> rsList = new ArrayList<Story>();
						if(sSet != null && sSet.size() > 0 ){
							Iterator<Story> it = sSet.iterator();
							while(it.hasNext()){
								rsList.add(it.next());
							}
							if(rsList.contains(story)){
								storyModel.setRepost_by_current_user(true);
							}else{
								storyModel.setRepost_by_current_user(false);
							}
						}else{
							storyModel.setRepost_by_current_user(false);
						}
						
						Set<User> like_set = story.getLike_users();
						if(like_set != null && like_set.size() > 0){
							storyModel.setLike_count(like_set.size());
						}else{
							storyModel.setLike_count(0);
						}
						Set<Story> likeStory = loginUser.getLike_story();
						List<Story> lsList = new ArrayList<Story>();
						if(likeStory != null && likeStory.size() > 0 ){
							Iterator<Story> it = likeStory.iterator();
							while(it.hasNext()){
								lsList.add(it.next());
							}
							if(lsList.contains(story)){
								storyModel.setLiked_by_current_user(true);;
							}else{
								storyModel.setLiked_by_current_user(false);
							}
						}else{
							storyModel.setRepost_by_current_user(false);
							storyModel.setLiked_by_current_user(false);
						}
						
					}else{
						storyModel.setRepost_by_current_user(false);
						storyModel.setLiked_by_current_user(false);
					}
					
					storyModel.setAuthor(authorJson);
					int count = 0;//this.commentDao.getCommentCountById((Long) story.getId());
					Set<Comment> coSet = story.getComments();
					if(coSet != null && coSet.size() > 0){
						Iterator<Comment> it = coSet.iterator();
						while(it.hasNext()){
							Comment c = it.next();
							if(c.getStatus().equals("enabled")){
								count++;
							}
						}
					}
					storyModel.setComment_count(count);
					storyModel.setSummary(story.getSummary());
					storyModel.setCreated_time(story.getCreated_time());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
					log.debug("***story.getCover_page()***" + jsonObject);
					String type = jsonObject.getString("type");

					if (type.equals("text")) {
						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("image")) {
						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("multimedia")) {
						storyModel.setCover_media(jsonObject);
					}

					storyModel.setTitle(story.getTitle());

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					/*Set<Columns> colSet = story.getColumns();
					if(colSet != null && colSet.size() > 0){
						Iterator<Columns> iter = colSet.iterator();
						if(iter.hasNext()){
							Columns c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("column_name", c.getColumn_name());
							storyModel.setColumns(json);
						}
					}else{
						delArray.add("columns");
					}*/
					
					Set<Collection> cSet = story.getCollections();
					if(cSet != null && cSet.size() > 0){
						Iterator<Collection> iter = cSet.iterator();
						List<JSONObject> collections = new ArrayList<JSONObject>();
						while(iter.hasNext()){
							Collection c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("collection_name", c.getCollection_name());
							collections.add(json);
						}
						storyModel.setCollections(collections);
					}else{
						delArray.add("collections");
					}
					
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}
					contentJson.put("story", storyJson);
				}
				if (timeline.getType().equals("recommandation")) {
					event.setContent(contentJson);
				}else if (timeline.getType().equals("post")) {
					event.setContent(contentJson);
				} else if (timeline.getType().equals("repost")) {
					UserIntro userIntro = new UserIntro();
					User user = (User) this.userDao.get(timeline.getCreatorId());
					JSONObject userIntroJson = null;
					if (user != null) {
						userIntro.setId((Long) user.getId());
						userIntro.setUsername(user.getUsername());
						userIntro.setIntroduction(user.getIntroduction());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
						}

						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						if (Strings.isNullOrEmpty(user.getAvatarImage())) {
							delArray.add("avatar_image");
						}

						if ((delArray != null) && (delArray.size() > 0)) {
							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
							configs.setIgnoreDefaultExcludes(false);
							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							userIntroJson = JSONObject.fromObject(userIntro, configs);
						} else {
							userIntroJson = JSONObject.fromObject(userIntro);
						}
					}

					contentJson.put("repost_by", userIntroJson);
					event.setContent(contentJson);
				}
			}

			return event;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public EventModel getEventModelListByLoginidCopy(Timeline timeline, Long loginUserid,User loginUser) {

		try {
			EventModel event = new EventModel();
			event.setId((Long) timeline.getId());
			event.setEvent_time(timeline.getCreateTime());
			event.setEvent_type(timeline.getType());
			JSONObject contentJson = new JSONObject();
			Story story = timeline.getStory();
			StoryEvent storyModel = new StoryEvent();

			if (story != null) {
				Long storyId = story.getId();
				User u = story.getUser();
				Collection collection = null;
				if (u.getId().equals(loginUserid)) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					JSONObject authorJson = new JSONObject();
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(u.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(u.getCoverImage());
					}

					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(u.getAvatarImage());
					}
					authorJson.put("id", u.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", u.getUsername());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}
					
					if ((u.getUser_type().equals("publisher")) || (u.getUser_type().equals("media"))) {
						Set<PublisherInfo> publisherSet = u.getPublisherInfos();
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
					/*Set<Collection> cSet = story.getCollections();
					if(cSet != null && cSet.size() > 0){
						Iterator<Collection> it = cSet.iterator();
						collection = it.next();
						if (collection != null) {
							CollectionIntro ci = new CollectionIntro();
							ci.setId((Long) collection.getId());
							ci.setCollection_name(collection.getCollectionName());
							ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
							ci.setInfo(collection.getInfo());

							storyModel.setCollection(ci);
						}
					}*/
					
					storyModel.setSummary(story.getSummary());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
					log.debug("***story.getCover_page()***" + jsonObject);
					String type = jsonObject.getString("type");

					if (type.equals("text")) {
						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("image")) {
						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("multimedia")) {
						storyModel.setCover_media(jsonObject);
					}

					storyModel.setTitle(story.getTitle());

					storyModel.setAuthor(authorJson);
					int count = 0;//this.commentDao.getCommentCountById((Long) story.getId());
					Set<Comment> coSet = story.getComments();
					if(coSet != null && coSet.size() > 0){
						Iterator<Comment> it = coSet.iterator();
						while(it.hasNext()){
							Comment c = it.next();
							if(c.getStatus().equals("enabled")){
								count++;
							}
						}
					}
					storyModel.setComment_count(count);
					storyModel.setRecommend_date(story.getRecommend_date());
					storyModel.setRepost_by_current_user(false);
					Set<Story> likeStory = loginUser.getLike_story();
					List<Story> lsList = new ArrayList<Story>();
					if(likeStory != null && likeStory.size() > 0 ){
						Iterator<Story> it = likeStory.iterator();
						while(it.hasNext()){
							lsList.add(it.next());
						}
						if(lsList.contains(story)){
							storyModel.setLiked_by_current_user(true);;
						}else{
							storyModel.setLiked_by_current_user(false);
						}
					}else{
						storyModel.setLiked_by_current_user(false);
					}
					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if (collection == null) {
						delArray.add("collection");
					}
					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}

					contentJson.put("story", storyJson);
				} else if (story.getStatus().equals("publish")) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					JSONObject authorJson = new JSONObject();
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(u.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(u.getCoverImage());
					}

					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(u.getAvatarImage());
					}
					authorJson.put("id", u.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", u.getUsername());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}
					authorJson.put("user_type", u.getUser_type());
					if ((u.getUser_type().equals("publisher")) || (u.getUser_type().equals("media"))) {
						Set<PublisherInfo> publisherSet = u.getPublisherInfos();
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
					if(loginUser != null){
						Set<Story> sSet = loginUser.getRepost_story();
						List<Story> rsList = new ArrayList<Story>();
						if(sSet != null && sSet.size() > 0 ){
							Iterator<Story> it = sSet.iterator();
							while(it.hasNext()){
								rsList.add(it.next());
							}
							if(rsList.contains(story)){
								storyModel.setRepost_by_current_user(true);
							}else{
								storyModel.setRepost_by_current_user(false);
							}
						}else{
							storyModel.setRepost_by_current_user(false);
						}
						
						Set<Story> likeStory = loginUser.getLike_story();
						List<Story> lsList = new ArrayList<Story>();
						if(likeStory != null && likeStory.size() > 0 ){
							Iterator<Story> it = likeStory.iterator();
							while(it.hasNext()){
								lsList.add(it.next());
							}
							if(lsList.contains(story)){
								storyModel.setLiked_by_current_user(true);;
							}else{
								storyModel.setLiked_by_current_user(false);
							}
						}else{
							storyModel.setLiked_by_current_user(false);
						}
					}else{
						storyModel.setRepost_by_current_user(false);
						storyModel.setLiked_by_current_user(false);
					}
					
					storyModel.setAuthor(authorJson);
					int count = 0;//this.commentDao.getCommentCountById((Long) story.getId());
					Set<Comment> coSet = story.getComments();
					if(coSet != null && coSet.size() > 0){
						Iterator<Comment> it = coSet.iterator();
						while(it.hasNext()){
							Comment c = it.next();
							if(c.getStatus().equals("enabled")){
								count++;
							}
						}
					}
					storyModel.setComment_count(count);
					storyModel.setSummary(story.getSummary());
					storyModel.setCreated_time(story.getCreated_time());

					JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
					log.debug("***story.getCover_page()***" + jsonObject);
					String type = jsonObject.getString("type");

					if (type.equals("text")) {
						TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
						log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("image")) {
						ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
					} else if (type.equals("multimedia")) {
						storyModel.setCover_media(jsonObject);
					}

					storyModel.setTitle(story.getTitle());

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if (collection == null) {
						delArray.add("collection");
					}
					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson = JSONObject.fromObject(storyModel);
					}
					contentJson.put("story", storyJson);
				}
				if (timeline.getType().equals("recommandation")) {
					event.setContent(contentJson);
				}else if (timeline.getType().equals("post")) {
					event.setContent(contentJson);
				} else if (timeline.getType().equals("repost")) {
					UserIntro userIntro = new UserIntro();
					User user = (User) this.userDao.get(timeline.getCreatorId());
					JSONObject userIntroJson = null;
					if (user != null) {
						userIntro.setId((Long) user.getId());
						userIntro.setUsername(user.getUsername());
						userIntro.setIntroduction(user.getIntroduction());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
						}

						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						if (Strings.isNullOrEmpty(user.getAvatarImage())) {
							delArray.add("avatar_image");
						}

						if ((delArray != null) && (delArray.size() > 0)) {
							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
							configs.setIgnoreDefaultExcludes(false);
							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							userIntroJson = JSONObject.fromObject(userIntro, configs);
						} else {
							userIntroJson = JSONObject.fromObject(userIntro);
						}
					}

					contentJson.put("repost_by", userIntroJson);
					event.setContent(contentJson);
				}
			}

			return event;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	
	}
	public SlideModel getSlideModel(Slide slide){
//		SlideModel sm = new SlideModel();
//		sm.setId(slide.getId());
//		String type = slide.getType();
//		Long id = slide.getReference_id();
//		JSONObject slideJson = new JSONObject();
//		if(type.equals("story")){
//			Story story = storyDao.get(id);
//			JSONObject json = getSlideStoryByStory(story);
//			slideJson.put("story", json);
//		}else if(type.equals("user")){
//			User user = userDao.get(id);
//			JSONObject json = new JSONObject();
//			json.put("id",user.getId());
//			json.put("username", user.getUsername());
//			json.put("user_type",user.getUser_type());
//			JSONObject avatarImageJson = null;
//			if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//				avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
//			}
//			if (avatarImageJson != null) {
//				json.put("avatar_image", avatarImageJson);
//			}
//			slideJson.put("user",json);
//		}else if(type.equals("collection")){
//			Collection collection = collectionDao.get(Integer.parseInt(id.toString()));
//			
//			CollectionIntro ci = new CollectionIntro();
// 	         ci.setId((Long.parseLong(collection.getId()));
// 	         ci.setCollection_name(collection.getCollectionName());
// 	         ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
// 	         ci.setInfo(collection.getInfo());
//			User u = collection.getUser();//.getId()userDao.get(collection.getAuthorId());
//			JSONObject author = new JSONObject();
//			author.put("id", u.getId());
//			author.put("username", u.getUsername());
//			if(!Strings.isNullOrEmpty(u.getAvatarImage())){
//				author.put("avatar_image",JSONObject.fromObject(u.getAvatarImage()));
//			}
//			
//			ci.setAuthor(author);
//			JsonConfig configs = new JsonConfig();
//			List<String> delArray = new ArrayList<String>();
//			
//			
//			JSONObject collectionJ = null;
//			if ((delArray != null) && (delArray.size() > 0)) {
//				configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//				configs.setIgnoreDefaultExcludes(false);
//				configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//				collectionJ = JSONObject.fromObject(ci, configs);
//			} else {
//				collectionJ = JSONObject.fromObject(ci);
//			}
//			
//			
//			slideJson.put("collection", collectionJ);
//		}else if(type.equals("url")){
//			slideJson.put("url",slide.getUrl());
//		}
//		sm.setSlide(slideJson);
//		sm.setType(slide.getType());
//		JSONObject slide_image = JSONObject.fromObject(slide.getSlide_image());
//		sm.setSlide_image(slide_image);
		return null;
	}

	@Override
	public Response linkPhone(HttpServletRequest request,Long loginUserid,String appVersion) {
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String phone = request.getParameter("phone");
		String zone = request.getParameter("zone");
		String code = request.getParameter("code");
		String timestamp = request.getParameter("token_timestamp");
		String password = request.getParameter("password");
		JSONObject resp = new JSONObject();
		if(loginUserid != null){
			User u = userDao.get(loginUserid);
			if(u != null){
				if ((!Strings.isNullOrEmpty(zone)) && (!Strings.isNullOrEmpty(phone))
						&& (!Strings.isNullOrEmpty(code))) {
					
					String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
					JSONObject jsonObject = parseJson(urlkey);
					String url = jsonObject.getString("url");
					Map<String,String> param = new HashMap<String,String>();
					param.put("mobile", phone);
					param.put("code", code);
					param.put("ip",ip);
					String linkParam = "";
					try {
						linkParam = publicParam(param);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String result = HttpUtil.sendPostStr(url+"/customer/account/check-mobile-for-register", linkParam);
					int centre_id = 0;
					if (!Strings.isNullOrEmpty(result)) {
						JSONObject json = JSONObject.fromObject(result);
						int status = json.getInt("code");
						if (status == 10000) {
							u.setZone(zone);
							u.setPhone(phone);
							Map<String,String> registerParam = new HashMap<String, String>();
							registerParam.put("mobile", phone);
							registerParam.put("code", code);
							registerParam.put("username", phone);
							registerParam.put("password", password);
							registerParam.put("ip",ip);
							String registerParams = "";
							try {
								registerParams = publicParam(registerParam);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							String register_result = HttpUtil.sendPostStr(url+"/customer/account/register", registerParams);
							JSONObject reg_res_json = JSONObject.fromObject(register_result);
							int res_code = reg_res_json.getInt("code");
							if(res_code == 10000){
								JSONObject data = reg_res_json.getJSONObject("data");
								centre_id = data.getInt("userid");
							}
							
						} else if(status == 10001) {	
							resp.put("status", "缺少参数");
							resp.put("code", 10600);
							resp.put("error_message", "缺少参数");
							return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
						}else if(status == 10110) {	
							resp.put("status", "手机号格式错误");
							resp.put("code", 10601);
							resp.put("error_message", "手机号格式错误");
							return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
						}else if(status == 10111) {	
							resp.put("status", "该手机号已注册");
							resp.put("code", 10602);
							resp.put("error_message", "该手机号已注册");
							return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
						}else if(status == 10112) {	
							resp.put("status", "验证码不正确");
							resp.put("code", 10603);
							resp.put("error_message", "验证码不正确");
							return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
						}
					} else {
						resp.put("status", "用户中心报错");
						resp.put("code", 10604);
						resp.put("error_message", "用户中心报错");
						return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
					}
				 
					u.setZone(zone);
					u.setPhone(phone);
					u.setPassword(Base64Utils.encodeToString(password.getBytes()));
					userDao.update(u);
					String raw = u.getId() + timestamp;
					String token = EncryptionUtil.hashMessage(raw);
					
					JSONObject j = new JSONObject();
					j.put("userid", u.getId());
					j.put("access_token", token);
					j.put("token_timestamp", Long.parseLong(timestamp));
					return Response.status(Response.Status.OK).entity(j).build();
					
				} else {
					JSONObject jo = new JSONObject();
					jo.put("status", "request_invalid");
					jo.put("code", Integer.valueOf(10010));
					jo.put("error_message", "request is invalid");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}
			}else{
				JSONObject json = new JSONObject();
				json.put("status", "no_resource");
				json.put("code",10011);
				json.put("error_message", "The user does not exist");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
			
		}else{
			JSONObject json = new JSONObject();
			List<User> userList = userDao.getUserByPhone(phone);
			if(userList != null && userList.size() > 0){
				json.put("status", "Phone_exist");
				json.put("code", 10094);
				json.put("error_message", "The phone is exist");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}else{
				json.put("status", "success");
				return Response.status(Response.Status.OK).entity(json).build();
			}
		
		}
	}

	@Override
	public Response unlinkPhone(HttpServletRequest request,Long loginUserid) {
		return null;
	}
	
	public JSONObject getStoryEventByStory(Story story){
		StoryEventNew storyModel = new StoryEventNew();
		storyModel.setId(story.getId());
		storyModel.setImage_count(story.getImage_count());
		storyModel.setUrl(story.getTinyURL());
		User user = story.getUser();
		JSONObject authorJson = new JSONObject();
		JSONObject coverImageJson = null;
		if (!Strings.isNullOrEmpty(user.getCoverImage())) {
			coverImageJson = JSONObject.fromObject(user.getCoverImage());
		}

		JSONObject avatarImageJson = null;
		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
		}
		authorJson.put("id", user.getId());
		authorJson.put("cover_image", coverImageJson);
		authorJson.put("username", user.getUsername());
		authorJson.put("introduction", user.getIntroduction());
		if (avatarImageJson != null) {
			authorJson.put("avatar_image", avatarImageJson);
		}
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
		Set<User> like_set = story.getLike_users();
		if(like_set != null && like_set.size() > 0){
			storyModel.setLike_count(like_set.size());
		}else{
			storyModel.setLike_count(0);
		}
		int count = 0;
		Set<Comment> coSet = story.getComments();
		if(coSet != null && coSet.size() > 0){
			Iterator<Comment> it = coSet.iterator();
			while(it.hasNext()){
				Comment c = it.next();
				if(c.getStatus().equals("enabled")){
					count++;
				}
			}
		}
		storyModel.setComment_count(count);
		storyModel.setSummary(story.getSummary());
		storyModel.setCreated_time(story.getCreated_time());

		JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
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

		storyModel.setTitle(story.getTitle());

		storyModel.setRecommend_date(story.getRecommend_date());

		JsonConfig configs = new JsonConfig();
		List<String> delArray = new ArrayList<String>();
		/*Set<Columns> colSet = story.getColumns();
		if(colSet != null && colSet.size() > 0){
			Iterator<Columns> iter = colSet.iterator();
			Columns c = iter.next();
			JSONObject columnsJson = new JSONObject();
			columnsJson.put("id",c.getId());
			columnsJson.put("column_name",c.getColumn_name());
			storyModel.setColumns(columnsJson);
		}else{
			delArray.add("columns");
		}*/
		
		Set<Collection> cSet = story.getCollections();
		if(cSet != null && cSet.size() > 0){
			Iterator<Collection> iter = cSet.iterator();
			List<JSONObject> collections = new ArrayList<JSONObject>();
			while(iter.hasNext()){
				Collection c = iter.next();
				JSONObject json = new JSONObject();
				json.put("id",c.getId());
				json.put("collection_name", c.getCollection_name());
				collections.add(json);
			}
			storyModel.setCollections(collections);
		}else{
			delArray.add("collections");
		}
		
		if (Strings.isNullOrEmpty(story.getTitle())) {
			delArray.add("title");
		}
		if (Strings.isNullOrEmpty(story.getSummary())) {
			delArray.add("summary");
		}
		JSONObject storyJson = null;
		if ((delArray != null) && (delArray.size() > 0)) {
			configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
			configs.setIgnoreDefaultExcludes(false);
			configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

			storyJson = JSONObject.fromObject(storyModel, configs);
		} else {
			storyJson = JSONObject.fromObject(storyModel);
		}
		
		return storyJson;
	}

	@Override
	public JSONObject getTimelinesByTimesquare(Long loginUserid, HttpServletRequest request) {

		log.debug("*** Get Home Timeline of the Authenticated User ***");
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<Timeline> timelineList = null; 
		List<Slide> slideList = null;
		List<SlideModel> smList = new ArrayList<SlideModel>();
		JSONObject homepage = new JSONObject();
		List<EventModel> emList = new ArrayList<EventModel>();
		int count = 20;
		if(loginUserid == null || loginUserid == 0l){
			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				timelineList = timelineDao.getTimelineBySquare(count);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						EventModel em = getEventModelCopy(tl);
						emList.add(em);
					}
				}
				slideList = slideDao.getSlideLists();
				if(slideList != null && slideList.size() > 0){
					for(Slide s:slideList){
						SlideModel sm = getSlideModel(s);
						smList.add(sm);
					}
				}
				homepage.put("slides",smList);
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				timelineList = timelineDao.getTimelineBySquare(count);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						EventModel em = getEventModelCopy(tl);
						emList.add(em);
					}
				}
				slideList = slideDao.getSlideLists();
				if(slideList != null && slideList.size() > 0){
					for(Slide s:slideList){
						SlideModel sm = getSlideModel(s);
						smList.add(sm);
					}
				}
				homepage.put("slides",smList);
				homepage.put("events",emList);
			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				timelineList = timelineDao.getTimelineBySquare(since_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						EventModel em = getEventModelCopy(tl);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				timelineList = timelineDao.getTimelineBySquare(since_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						EventModel em = getEventModelCopy(tl);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineBySquare(max_id,count,2);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						EventModel em = getEventModelCopy(tl);
						emList.add(em);
					}
				}
				
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineBySquare(max_id,count,2);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						EventModel em = getEventModelCopy(tl);
						emList.add(em);
					}
				}
				
				homepage.put("events",emList);
			}
		}else{
			User loginUser = userDao.get(loginUserid);
			EventModel em = null;
			SlideModel sm = null;
			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				timelineList = timelineDao.getTimelineBySquare(count);
				
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidCopy(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				slideList = slideDao.getSlideLists();
				
				if(slideList != null && slideList.size() > 0){
					for(Slide s:slideList){
						sm = getSlideModel(s);
						smList.add(sm);
					}
				}
				homepage.put("slides",smList);
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				timelineList = timelineDao.getTimelineBySquare(count);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidCopy(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				slideList = slideDao.getSlideLists();
				if(slideList != null && slideList.size() > 0){
					for(Slide s:slideList){
						sm = getSlideModel(s);
						smList.add(sm);
					}
				}
				homepage.put("slides",smList);
				homepage.put("events",emList);
			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				timelineList = timelineDao.getTimelineBySquare(since_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidCopy(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				timelineList = timelineDao.getTimelineBySquare(since_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidCopy(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineBySquare(max_id,count,2);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidCopy(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineBySquare(max_id,count,2);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidCopy(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				
				homepage.put("events",emList);
			}
		}
		return homepage;
	
	}
	
	@Override
	public Response getStoryByCollection(Long loginUserid, HttpServletRequest request)throws Exception {

		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
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
		if(!Strings.isNullOrEmpty(device)){
			device = "20";
		}
		JSONObject homepage = new JSONObject();
		Map<String,String> map = new HashMap<String, String>();
		int count = 20;
		map.put("user_id", loginUserid.toString());
		map.put("ip", ip);
		map.put("device", device);
		if ((Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			map.put("page_count", String.valueOf(count));
		} else if ((!Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			map.put("page_count", String.valueOf(count));
		} else if ((Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", max_id.toString());
		} else if ((!Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", max_id.toString());
		}
		JSONObject json = new JSONObject();
		String params = carPublicParam(map);
		String result = HttpUtil.sendGetStr(car_url+"/member/follow-data-timeline", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code_res = resJson.getInt("code");
			if(code_res == 10000){
				JSONArray data = resJson.getJSONArray("data");
				JSONObject contentJson = null;
				JSONObject eventJson = null;
				JSONObject storyJson = null;
				List<JSONObject> cJsonList = new ArrayList<JSONObject>();
				if(data != null && data.size() > 0){
					for(Object o:data){
						contentJson = JSONObject.fromObject(o);
						eventJson = new JSONObject();
						storyJson = new JSONObject();
						Iterator<String> iter = contentJson.keys();
						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						while(iter.hasNext()){
							String key = iter.next();
							String val = contentJson.getString(key);
							if(Strings.isNullOrEmpty(val) || val.equals("null")){
								delArray.add(key);
							}
						}
						
						JSONObject cJson = null;
						if ((delArray != null) && (delArray.size() > 0)) {
							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
							configs.setIgnoreDefaultExcludes(false);
							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							cJson = JSONObject.fromObject(contentJson, configs);
						} else {
							cJson = JSONObject.fromObject(contentJson);
						}
						storyJson.put("story", cJson);
						eventJson.put("content", storyJson);
						eventJson.put("event_type", "post");
						eventJson.put("event_time", contentJson.getLong("created_at"));
						cJsonList.add(eventJson);
					}
					
				}
				homepage.put("events",cJsonList);
			}else if(code_res == 10001){
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		}
		return Response.status(Response.Status.OK).entity(homepage).build();
	
	
	}

	@Override
	public Response contacts(JSONObject json,Long loginUserid) {
		JSONArray arr = JSONArray.fromObject(json.getString("phone_number"));
		Object[] obj = arr.toArray();
		List<UserPhone> upList = new ArrayList<UserPhone>();
		if(obj != null && obj.length > 0){
			for(Object o:obj){
				String str = o.toString();
				List<User> userList = userDao.getUserByPhone(str);
				UserPhone up = null;
				if(userList != null && userList.size() > 0){
					for(User u:userList){
						up = new UserPhone();
						up.setId(u.getId());
						up.setUsername(u.getUsername());
						up.setPhone(u.getPhone());
						up.setZone(u.getZone());
						if(!Strings.isNullOrEmpty(u.getAvatarImage())){
							up.setAvatar_image(JSONObject.fromObject(u.getAvatarImage()));
						}else{
							up.setAvatar_image(null);
						}
						
						upList.add(up);
					}
				}
			}
		}
		return Response.status(Response.Status.OK).entity(upList).build();
	}

	@Override
	public Response createFeedback(JSONObject feedback) {
		Feedback f = new Feedback();
		JSONObject json = new JSONObject();
		if(feedback.containsKey("info") || feedback.containsKey("cover_image")){
			if(!Strings.isNullOrEmpty(feedback.getString("user_id"))){
				f.setUser_id(Long.parseLong(feedback.getString("user_id")));
			}else{
				f.setUser_id(0l);
			}
			if(feedback.containsKey("info")){
				if(!Strings.isNullOrEmpty(feedback.getString("info")) ){
					f.setInfo(feedback.getString("info"));
				}
			}
			
			if(feedback.containsKey("cover_image")){
				if(!Strings.isNullOrEmpty(feedback.getString("cover_image"))){
					f.setCover_image(feedback.getString("cover_image"));
				}
			}
			
			feedbackDao.save(f);
			json.put("status","success");
			return Response.status(Response.Status.OK).entity(json).build();
				
			
		}else{
			json.put("status","invalid_request");
			json.put("code",10010);
			json.put("error_message","Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		
		
	}

	@Override
	public JSONObject getTest(Long loginUserid, HttpServletRequest request) {
		System.out.println("test java cpu");
		return null;
	}

	@Override
	public Response featured(Long userId, Long loginUserid) {
		List<UserFeatur> ufList = new ArrayList<UserFeatur>();
		if(userId != null && userId > 0){
			
		}else{
			List<FeatureUser> fuList = featureUserDao.getRandomFeatureUser(5);
			
			if(fuList != null && fuList.size() > 0){
				UserFeatur uf = null;
				for(FeatureUser fu:fuList){
					User user = userDao.get(fu.getUserId());
					uf = new UserFeatur();
//					Follow loginFollow = this.followDao.getFollow(loginUserid, user.getId());
					boolean followed_by_current_user = false;
					uf.setUsername(user.getUsername());
					uf.setIntroduction(user.getIntroduction());
					uf.setId(user.getId());
//					if (loginFollow != null) {
//						followed_by_current_user = true;
//					}
					uf.setFollowed_by_current_user(followed_by_current_user);
					JSONObject avatarImageJson = null;
					if ((user.getAvatarImage() != null) && (!user.getAvatarImage().equals(""))) {
						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
					}
					uf.setAvatar_image(avatarImageJson);
					JSONObject coverImageJson = null;
					if ((user.getCoverImage() != null) && (!user.getCoverImage().equals(""))) {
						coverImageJson = JSONObject.fromObject(user.getCoverImage());
					}
					uf.setCover_image(coverImageJson);
					ufList.add(uf);
				}
			}
		}
		return Response.status(Response.Status.OK).entity(ufList).build();
	}
	
	public JSONObject getSlideStoryByStory(Story story){
		JSONObject storyModel = new JSONObject();
		storyModel.put("id",story.getId());
		storyModel.put("image_count",story.getImage_count());
		storyModel.put("url",story.getTinyURL());
		User user = story.getUser();
		JSONObject authorJson = new JSONObject();
		JSONObject coverImageJson = null;
		if (!Strings.isNullOrEmpty(user.getCoverImage())) {
			coverImageJson = JSONObject.fromObject(user.getCoverImage());
		}

		JSONObject avatarImageJson = null;
		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
		}
		authorJson.put("id", user.getId());
		authorJson.put("cover_image", coverImageJson);
		authorJson.put("username", user.getUsername());
		authorJson.put("introduction", user.getIntroduction());
		if (avatarImageJson != null) {
			authorJson.put("avatar_image", avatarImageJson);
		}
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
		storyModel.put("author",authorJson);


		JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
		String type = jsonObject.getString("type");

		if (type.equals("text")) {
			TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
			storyModel.put("cover_media",JSONObject.fromObject(coverMedia));
		} else if (type.equals("image")) {
			ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
			storyModel.put("cover_media",JSONObject.fromObject(coverMedia));
		} else if (type.equals("multimedia")) {
			storyModel.put("cover_media",jsonObject);
		}


		if (!Strings.isNullOrEmpty(story.getTitle())) {
			storyModel.put("title",story.getTitle());
		}
		
		
		return storyModel;
	}

	@Override
	public Response profile_stories(Long userId,Long loginUserid,HttpServletRequest request)throws Exception{

		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		JSONObject resp = new JSONObject();
		int count = 20;
		JSONObject profile = new JSONObject();
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(!Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("relation_type", "20");
		map.put("ip", ip);
		map.put("device", device);
		map.put("author_id", String.valueOf(userId));
		map.put("user_id", String.valueOf(loginUserid));
		
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
			map.put("page_count", String.valueOf(count));
		} else if ((!Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			map.put("page_count", String.valueOf(count));
		} else if ((Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", String.valueOf(max_id));
		} else if ((!Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", String.valueOf(max_id));
		}
		String params = carPublicParam(map);
		String result = HttpUtil.sendGetStr(car_url+"/content/get-list", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){

				JSONArray data = resJson.getJSONArray("data");
				JSONObject contentJson = null;
				JSONObject eventJson = null;
				JSONObject storyJson = null;
				List<JSONObject> cJsonList = new ArrayList<JSONObject>();
				if(data != null && data.size() > 0){
					for(Object o:data){
						contentJson = JSONObject.fromObject(o);
						eventJson = new JSONObject();
						storyJson = new JSONObject();
						Iterator<String> iter = contentJson.keys();
						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						while(iter.hasNext()){
							String key = iter.next();
							String val = contentJson.getString(key);
							if(Strings.isNullOrEmpty(val) || val.equals("null")){
								delArray.add(key);
							}
						}
						
						JSONObject cJson = null;
						if ((delArray != null) && (delArray.size() > 0)) {
							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
							configs.setIgnoreDefaultExcludes(false);
							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							cJson = JSONObject.fromObject(contentJson, configs);
						} else {
							cJson = JSONObject.fromObject(contentJson);
						}
						storyJson.put("story", cJson);
						eventJson.put("content", storyJson);
						eventJson.put("event_type", "post");
						eventJson.put("event_time", contentJson.getLong("created_at"));
						cJsonList.add(eventJson);
					}
					
				}
				profile.put("event",cJsonList);
			
			
			}else if (code == 14005){
				resp.put("status", "关系对象类型错误");
				resp.put("code", 10504);
				resp.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
		
		Map<String,String> userMap = new HashMap<String, String>();
		
		userMap.put("ip", ip);
		userMap.put("device", device);
		userMap.put("id", userId.toString());
		if(loginUserid != null && loginUserid > 0){
			userMap.put("login_id", loginUserid.toString());
		}
		
		
		String userParams = carPublicParam(userMap);
		String user_result = HttpUtil.sendGetStr(car_url+"/member/get", userParams);
		
		if(!Strings.isNullOrEmpty(user_result)){
			JSONObject resJson = JSONObject.fromObject(user_result);
			int code = resJson.getInt("code");
			if(code == 10000){
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
				profile.put("user",cJson);
			}else if (code == 14005){
				resp.put("status", "关系对象类型错误");
				resp.put("code", 10504);
				resp.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
		
		return Response.status(Response.Status.OK).entity(profile).build();
	
	
	}

	@Override
	public Response profile_repost(Long userId,Long loginUserid,HttpServletRequest request)throws Exception {
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		JSONObject resp = new JSONObject();
		int count = 20;
		JSONObject profile = new JSONObject();
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(!Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("data_type", "40");
		map.put("ip", ip);
		map.put("device", device);
		map.put("user_id", String.valueOf(userId));
		
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
			map.put("page_count", String.valueOf(count));
		} else if ((!Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			map.put("page_count", String.valueOf(count));
		} else if ((Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", String.valueOf(max_id));
		} else if ((!Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", String.valueOf(max_id));
		}
		String params = carPublicParam(map);
		String result = HttpUtil.sendGetStr(car_url+"/member/collect-data-list", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){

				JSONArray arr = resJson.getJSONArray("data");
				List<JSONObject> eventJsonList = new ArrayList<JSONObject>();
				JSONObject storyJson = null;
				JSONObject eventJson = null;
				JSONObject data = null;
				if(arr != null && arr.size() > 0){
					for(Object o:arr){
						data = JSONObject.fromObject(o);
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
						
						storyJson = new JSONObject();
						eventJson = new JSONObject();
						storyJson.put("story", cJson);
						eventJson.put("content", storyJson);
						eventJson.put("event_type", "repost");
						eventJson.put("event_time", cJson.getLong("created_at"));
						eventJsonList.add(eventJson);
					}
				}
				profile.put("event",eventJsonList);
			
			}else if (code == 14005){
				resp.put("status", "关系对象类型错误");
				resp.put("code", 10504);
				resp.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
		
//		Map<String,String> userMap = new HashMap<String, String>();
//		
//		userMap.put("ip", ip);
//		userMap.put("device", device);
//		userMap.put("id", userId.toString());
//		userMap.put("login_id", loginUserid.toString());
//		
//		String userParams = carPublicParam(userMap);
//		String user_result = HttpUtil.sendGetStr(car_url+"/member/get", userParams);
//		
//		if(!Strings.isNullOrEmpty(user_result)){
//			JSONObject resJson = JSONObject.fromObject(user_result);
//			int code = resJson.getInt("code");
//			if(code == 10000){
//				Object data = resJson.get("data");
//				profile.put("user",data);
//			}else if (code == 14005){
//				resp.put("status", "关系对象类型错误");
//				resp.put("code", 10504);
//				resp.put("error_message", "关系对象类型错误");
//				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
//			}
//		}
		
		return Response.status(Response.Status.OK).entity(profile).build();
	}
	
	@Override
	public Response profile_collection(Long userId, Long loginUserid, HttpServletRequest request)throws Exception {
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		Long userid = 0l;
		if(userId == loginUserid 
				&& userId.equals(loginUserid)){
			userid = loginUserid;
		}else{
			userid = userId;
		}

		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		JSONObject resp = new JSONObject();
		int count = 20;
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		if(!Strings.isNullOrEmpty(device)){
			device = "20";
		}
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("data_type", "40");
		map.put("ip", ip);
		map.put("device", device);
		map.put("user_id", String.valueOf(userid));
		
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
			map.put("page_count", String.valueOf(count));
		} else if ((!Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			map.put("page_count", String.valueOf(count));
		} else if ((Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", String.valueOf(max_id));
		} else if ((!Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", String.valueOf(max_id));
		}
		String params = carPublicParam(map);
		String result = HttpUtil.sendGetStr(car_url+"/member/follow-data-list", params);
		List<JSONObject> cList = new ArrayList<JSONObject>();
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){
				JSONArray data = resJson.getJSONArray("data");
				JSONObject collectionJson = null;
				JSONObject coJson = null;
				if(data != null && data.size() > 0){
					for(Object o:data){
						coJson = new JSONObject();
						collectionJson = JSONObject.fromObject(o);
						int c_id = collectionJson.getInt("data_id");
						int follow_count = collectionJson.getInt("follow_count");
						int content_count = collectionJson.getInt("content_count");
						
						Collection c = collectionDao.get(c_id);
						coJson.put("id", c.getId());
						coJson.put("collection_name", c.getCollection_name());
						coJson.put("cover_image", JSONObject.fromObject(c.getCover_image()));
						int type = c.getType();
						if(type == 60){
							coJson.put("logo", JSONObject.fromObject(c.getLogo()));
						}
						coJson.put("type", type);
						
						coJson.put("follow_count", follow_count);
						coJson.put("content_count", content_count);
						cList.add(coJson);
					}
				}
			}else if(code == 10001){
				resp.put("status", "缺少参数");
				resp.put("code", 10627);
				resp.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
		
		
		
		return Response.status(Response.Status.OK).entity(cList).build();
	
	}

	@Override
	public JSONObject rong_token(Long loginUserid) {
		String appkey = getClass().getResource("/../../META-INF/rong.json").getPath();
		JSONObject jsonObject = parseJson(appkey);
		String key = jsonObject.getString("key");
		String secret = jsonObject.getString("secret");
		User user = userDao.get(loginUserid);
		SdkHttpResult result = null;
		JSONObject json = null;
		try {
			result = ApiHttpClient.getToken(key, secret, loginUserid.toString(), user.getUsername(), "", FormatType.json);
			json = JSONObject.fromObject(result.getResult());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	
	}

	public JSONObject getCollectionJSON(Collection collection){
//		 CollectionIntro ci = new CollectionIntro();
//	         ci.setId((Integer)collection.getId());
//	         ci.setCollection_name(collection.getCollectionName());
//	         ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
//	         ci.setInfo(collection.getInfo());
//				User u = collection.getUser();//userDao.get(collection.getAuthorId());
//				JSONObject author = new JSONObject();
//				author.put("id", u.getId());
//				author.put("username", u.getUsername());
//				author.put("avatar_image", JSONObject.fromObject(u.getAvatarImage()));
//				ci.setAuthor(author);
//				JsonConfig configs = new JsonConfig();
//				List<String> delArray = new ArrayList<String>();
//				
//				
//				JSONObject collectionJ = null;
//				if ((delArray != null) && (delArray.size() > 0)) {
//					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//					configs.setIgnoreDefaultExcludes(false);
//					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//					collectionJ = JSONObject.fromObject(ci, configs);
//				} else {
//					collectionJ = JSONObject.fromObject(ci);
//				}
           return null;
	}

	@Override
	public JSONObject getChatUser(Long userId) {
		JSONObject json = new JSONObject();
		if(userId != null && userId > 0){
			User user = (User) this.userDao.get(userId);
			json.put("id",user.getId());
			json.put("username", user.getUsername());
			if(!Strings.isNullOrEmpty(user.getAvatarImage())){
				json.put("avatar_image",JSONObject.fromObject(user.getAvatarImage()));
			}
			
		}
		
		return json;
	}

//	@Override
//	public void push_info(JSONObject jsonObject) {
//		if(jsonObject != null){
//			Long userId = jsonObject.getLong("userId");
//			String username = jsonObject.getString("username");
//			String content = jsonObject.getString("content");
//			List<PushNotification> pnList = pushNotificationDao.getPushNotificationByUserid(userId);
//			GetuiModel gm = getGetuiInfo();
//			JSONObject json = new JSONObject();
//			json.put("user_id", userId);
//			content = username + ":" + content;
//			try {
//				PushNotificationUtil.pushInfo(gm.getAppId(),gm.getAppKey(), 
//						gm.getMasterSecret(), pnList, 1, content,json.toString());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		
//	}

	public GetuiModel getGetuiInfo(){
		GetuiModel gm = new GetuiModel();
		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
		JSONObject json1 = ParseFile.parseJson(path);
		String appId = json1.getString("appId");
		String appKey = json1.getString("appKey");
		String masterSecret = json1.getString("masterSecret");
		gm.setAppId(appId);
		gm.setAppKey(appKey);
		gm.setMasterSecret(masterSecret);
		return gm;
		
	}
	
	public boolean isAvailableVersion(String appVersion){
		String path4 = getClass().getResource("/../../META-INF/version.json").getPath();
		JSONObject v = ParseFile.parseJson(path4);
		String version = v.getString("version");
		boolean flagVersion = false;
		if(!Strings.isNullOrEmpty(version)){
			String[] vArr = version.split("\\.");
			String[] vaArr = appVersion.split("\\.");
			if(version.equals(appVersion)){
				flagVersion = false;
			}else{
				if(!vArr[0].equals(vaArr[0])){
					if(Integer.parseInt(vArr[0]) > Integer.parseInt(vaArr[0])){
						flagVersion = false;
					}else{
						flagVersion = true;
					}
				}else{
					if(!vArr[1].equals(vaArr[1])){
						if(Integer.parseInt(vArr[1]) > Integer.parseInt(vaArr[1])){
							flagVersion = false;
						}else{
							flagVersion = true;
						}
					}else{
						if(!vArr[2].equals(vaArr[2])){
							if(Integer.parseInt(vArr[2]) > Integer.parseInt(vaArr[2])){
								flagVersion = false;
							}else{
								flagVersion = true;
							}
						}
					}
				}
			}
		}
		
		return flagVersion;
	}

	 
    
   
	@Override
	public Response getAuthCode(JSONObject jsonObject)throws Exception {
		JSONObject jo = new JSONObject();
		if(jsonObject != null){
			String phone = jsonObject.getString("phone");
			User user = userDao.getUserByPhoneAndZone("86",phone);
			if(user != null){
				
				jo.put("status", "phone_exists");
				jo.put("code", Integer.valueOf(10094));
				jo.put("error_message", "Phone already used");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}else{
				String appkey = "appkey=154468ccc7b01";
				String params = appkey + "&phone="+phone+"&&zone=86"; 
				String result = requestData("https://web.sms.mob.com/sms/sendmsg",
						params);
				return Response.status(Response.Status.OK).entity(result).build();
			}
			
		}else{
			jo.put("status", "request_invalid");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "request is invalid");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
		
	}

	@Override
	public Response privateChat(JSONObject chat,Long loginUserid) throws Exception {
		return null;
	}

	@Override
	public Response getAllChat(Long userId, Long loginUserid) {
		return null;
	}

	@Override
	public Response get_auth(JSONObject fbInfo,HttpServletRequest request) throws Exception {
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String url = jsonObject.getString("url");
		url = url+"/customer/account/check-fd-token";
		JSONObject resultJson = new JSONObject();
		FBEncryption fb = new FBEncryption("20161206100527xEhf0s8Vj3j_uSMrI1eOHU--8k1LwR0o", 
				"20161206100534Z_qxIRa6BBMophlaZMNwSVwJMGmL_ptB");
		if(fbInfo != null){
			String token_param = fbInfo.getString("token");
			Map<String,String> param = new HashMap<String, String>();
			param.put("fd_token", token_param);
			param.put("ip",ip);
			param.put("channel","40");
			Map<String,String> map = fb.signature(param);
			StringBuffer sbs = new StringBuffer();
			Set<String> keys = map.keySet();
			Iterator<String> iter = keys.iterator();
			while(iter.hasNext()){
				String key = iter.next();
				if(key.equals("sign") || key.equals("fd_token")){
					sbs.append(key+"="+URLEncoder.encode(map.get(key),"utf-8")+"&");
				}else{
					sbs.append(key+"="+map.get(key)+"&");
				}
				
			}
			String params_str = sbs.toString();
			String params = params_str.substring(0,params_str.length()-1);
			String respInfo = HttpUtil.sendGet(url+"?"+params);
			JSONObject resp = JSONObject.fromObject(respInfo);
			if(resp.getInt("code") == 10000){
				Object[] obj = null;//linkAccountsDao.getLinkAccountsByUUIDAndService(fbInfo.getString("fb_uid"), "fblife");
				long timestamp = fbInfo.getLong("timestamp");
				if(obj != null){
//					LinkAccounts linkAccounts = (LinkAccounts)obj[0];
//					Long userId = linkAccounts.getUser_id();
					User user = userDao.get(0l);
					String raw = user.getId().toString() + timestamp;
					String token = EncryptionUtil.hashMessage(raw);

					resultJson.put("userid", user.getId());
					resultJson.put("access_token", token);
					resultJson.put("token_timestamp", timestamp);
					return Response.status(Response.Status.OK).entity(resultJson).build();
				}else{
					User u = new User();
					String chars = "abcde0f12g3hi4jk5l6m7n8o9pqrstuvwxyz";
			    	StringBuffer sb = new StringBuffer();
			    	for(int i=0;i<10;i++){
			    		char c = chars.charAt((int)(Math.random() * 36));
			    		sb.append(c);
			    	}
					String pwd = Base64Utils.encodeToString(sb.toString().getBytes());
					u.setPassword(pwd);

					u.setUsername(fbInfo.getString("user_name"));
					u.setSalt(initSalt().toString());
					u.setStatus("enabled");
					u.setUser_type("normal");
					this.userDao.save(u);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
					String raw = u.getId().toString() + timestamp;
					String token = EncryptionUtil.hashMessage(raw);

					resultJson.put("userid", u.getId());
					resultJson.put("access_token", token);
					resultJson.put("token_timestamp", timestamp);
					return Response.status(Response.Status.OK).entity(resultJson).build();
				}
			}else{
				resultJson.put("status", "invalid_token");
				resultJson.put("code", 10108);
				resultJson.put("error_message", "token验证不正确");
				return Response.status(Response.Status.BAD_REQUEST).entity(resultJson).build();
			}
		}else{
			resultJson.put("status", "invalid_param");
			resultJson.put("code", 10010);
			resultJson.put("error_message", "不合法的参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resultJson).build();
		}
	}

	@Override
	public Response get_code(JSONObject phoneInfo, HttpServletRequest request) throws Exception {
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String url = jsonObject.getString("url");
		JSONObject resp = new JSONObject();
		if(phoneInfo != null){
			String phone = phoneInfo.getString("phone");
			Map<String,String> param = new HashMap<String, String>();
			param.put("mobile",phone);
			param.put("ip",ip);
			String params = publicParam(param);
			String result = HttpUtil.sendPostStr(url+"/customer/account/get-code-for-reset-password", params);
			JSONObject res = JSONObject.fromObject(result);
			int code = res.getInt("code");
			if(res.getInt("code") == 10000){
				resp.put("status","success");
				return Response.status(Response.Status.OK).entity(resp).build();
			}else if(code == 10111){
				resp.put("status", "手机号未注册");
				resp.put("code", 10627);
				resp.put("error_message", "手机号未注册");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else{
				resp.put("status", "invalid_param");
				resp.put("code", 10010);
				resp.put("error_message", "不合法的参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}else{
			resp.put("status", "invalid_param");
			resp.put("code", 10010);
			resp.put("error_message", "不合法的参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		} 
	}

	@Override
	public Response get_register_code(JSONObject phoneInfo, HttpServletRequest request) throws Exception {
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String url = jsonObject.getString("url");
		JSONObject resp = new JSONObject();
		if(phoneInfo != null){
			String phone = phoneInfo.getString("phone");
			Map<String,String> param = new HashMap<String, String>();
			param.put("mobile",phone);
			param.put("ip",ip);
			String params = publicParam(param);
			String result = HttpUtil.sendPostStr(url+"/customer/account/get-code-for-register", params);
			JSONObject res = JSONObject.fromObject(result);
			if(res.getInt("code") == 10000){
				resp.put("status","success");
				return Response.status(Response.Status.OK).entity(resp).build();
			}else if(res.getInt("code") == 10001){
				resp.put("status", "缺少参数");
				resp.put("code", 10627);
				resp.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(res.getInt("code") == 10110){
				resp.put("status", "手机号格式错误");
				resp.put("code", 10628);
				resp.put("error_message", "手机号格式错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(res.getInt("code") == 10111){
				resp.put("status", "该手机号已注册");
				resp.put("code", 10628);
				resp.put("error_message", "该手机号已注册");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(res.getInt("code") == 10120){
				resp.put("status", "该手机号已超出当日请求验证码最大次数");
				resp.put("code", 10629);
				resp.put("error_message", "该手机号已超出当日请求验证码最大次数");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(res.getInt("code") == 10121){
				resp.put("status", "验证码发送失败");
				resp.put("code", 10630);
				resp.put("error_message", "验证码发送失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else{
				resp.put("status", "invalid_param");
				resp.put("code", 10010);
				resp.put("error_message", "不合法的参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}else{
			resp.put("status", "invalid_param");
			resp.put("code", 10010);
			resp.put("error_message", "不合法的参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		} 
	}
	
	@Override
	public Response get_bind_code(JSONObject phoneInfo, HttpServletRequest request) throws Exception {
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String url = jsonObject.getString("url");
		JSONObject resp = new JSONObject();
		if(phoneInfo != null){
			String phone = phoneInfo.getString("phone");
			Map<String,String> param = new HashMap<String, String>();
			param.put("mobile",phone);
			param.put("ip",ip);
			String params = publicParam(param);
			String result = HttpUtil.sendPostStr(url+"/customer/account/get-code-for-auth-mobile", params);
			JSONObject res = JSONObject.fromObject(result);
			if(res.getInt("code") == 10000){
				resp.put("status","success");
				return Response.status(Response.Status.OK).entity(resp).build();
			}else{
				resp.put("status", "invalid_param");
				resp.put("code", 10010);
				resp.put("error_message", "不合法的参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}else{
			resp.put("status", "invalid_param");
			resp.put("code", 10010);
			resp.put("error_message", "不合法的参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		} 
	}
	
	@Override
	public Response check_user(HttpServletRequest request) throws Exception {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		JSONObject resp = new JSONObject();
		if(!Strings.isNullOrEmpty(username) 
				&& !Strings.isNullOrEmpty(password)){
//			String username = userInfo.getString("username");
//			String password = userInfo.getString("password");
			//int centre_id = userInfo.getInt("userid");
			User user = null;
			Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = pattern.matcher(username);
			String check = 
						"^([a-z0-9A-Z]+[-|\\.]?)@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"; 
			Pattern email = Pattern.compile(check);
			Matcher emailM = email.matcher(username);
			if(m.matches()){
				user = userDao.loginByPhone("86",username, EncryptionUtil.sha256(password).toUpperCase());
			} else if(emailM.matches()){
				user = userDao.loginUser(username, EncryptionUtil.sha256(password).toUpperCase());
			}
			
			if(user != null){
				return Response.status(Response.Status.OK).entity("true").build();
			}else{
				resp.put("status", "invalid_user");
				resp.put("code", 10007);
				resp.put("error_message", "invalid user");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}else{
			resp.put("status", "invalid_param");
			resp.put("code", 10010);
			resp.put("error_message", "不合法的参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response check_token(JSONObject tokenInfo,HttpServletRequest request) throws Exception {
		
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = parseJson(urlkey);
		String url = jsonObject.getString("token_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		JSONObject res = new JSONObject();
		if(tokenInfo != null){
			String token = tokenInfo.getString("token");
			Map<String,String> param = new HashMap<String, String>();
			param.put("token",token);
			param.put("ip",ip);
			String params = publicParam(param);
			String result = HttpUtil.sendGet(url+"/customer/info/get-by-token?"+params);
			JSONObject resp = JSONObject.fromObject(result);
			int code = resp.getInt("code");
			if(code == 10000){
				JSONObject centre_info = resp.getJSONObject("data");
				int centre_id = centre_info.getInt("userid");
				String fb_token = token;
//				UserCentre uc = userCentreDao.getUserCentreByCentreId(centre_id);
				User uc = null;
				if(uc != null){
//					Long userid = uc.getUser_id();
					User user = userDao.get(0l);
					JSONObject auth = new JSONObject();
					String raw = user.getId() + user.getCreated_time().toString();
					String tokens = EncryptionUtil.hashMessage(raw);

					System.out.println("userId--->" + user.getId());
					String avatarImage = user.getAvatarImage();
					if(!Strings.isNullOrEmpty(avatarImage)){
						auth.put("avatar_image",JSONObject.fromObject(avatarImage));
					}
					auth.put("userid", user.getId());
					auth.put("access_token", tokens);
					auth.put("username", user.getUsername());
					auth.put("token_timestamp", user.getCreated_time());
					auth.put("fbid", centre_id);
					auth.put("token", fb_token);
					return Response.status(Response.Status.OK).entity(auth).build();
				}else{
					GetuiModel gm = getGetuiInfo();
					if(centre_info.containsKey("mobile")){
						String fbname = centre_info.getString("username");
						String phone = centre_info.getString("mobile");
						if(!Strings.isNullOrEmpty(phone)){
							User u = new User();
							u.setPhone(phone);
							u.setZone("86");
							
							String chars = "abcde0f12g3hi4jk5l6m7n8o9pqrstuvwxyz";
					    	StringBuffer sb = new StringBuffer();
					    	for(int i=0;i<10;i++){
					    		char c = chars.charAt((int)(Math.random() * 36));
					    		sb.append(c);
					    	}
							String pwd = Base64Utils.encodeToString(sb.toString().getBytes());
							u.setPassword(pwd);
							
							//---
							if(!Strings.isNullOrEmpty(fbname)){
								u.setUsername(fbname);
								u.setFbname(fbname);
							}else if(centre_info.containsKey("nickname")){
								String nickname = centre_info.getString("nickname");
								if(!Strings.isNullOrEmpty(nickname)){
									u.setUsername(nickname);
								}else{
									u.setUsername(phone);
								}
							}else{
								u.setUsername(phone);
							}
							
							u.setSalt(initSalt().toString());
							u.setStatus("enabled");
							u.setUser_type("normal");
							this.userDao.save(u);
							Configuration c = new Configuration();
							c.setNew_admin_push(true);
							c.setNew_comment_on_your_comment_push(true);
							c.setNew_comment_on_your_story_push(true);
							c.setNew_favorite_from_following_push(true);
							c.setNew_follower_push(true);
							c.setNew_story_from_following_push(true);
							c.setRecommended_my_story_push(true);
							c.setReposted_my_story_push(true);
							
							c.setUserId((Long) u.getId());
							
							this.configurationDao.save(c);
							List<User> userList = this.userDao.getUserByUserType();
							List<PushNotification> pnList = new ArrayList<PushNotification>();
							Notification n = null;
							List<Notification> notificationList = new ArrayList<Notification>();
							Configuration conf;
							if ((userList != null) && (userList.size() > 0)) {
								for (User admin : userList) {
									n = new Notification();
									n.setRecipientId((Long) admin.getId());
//									n.setSenderId((Long) u.getId());
									n.setNotificationType(8);
									n.setObjectType(3);
									n.setObjectId((Long) admin.getId());
									n.setStatus("enabled");
									n.setRead_already(true);
									notificationList.add(n);
									conf = this.configurationDao.getConfByUserId((Long) admin.getId());
									if (conf.isNew_admin_push()) {
										List<PushNotification> list = this.pushNotificationDao
												.getPushNotificationByUserid(admin.getId());
										pnList.addAll(list);
									}
								}
							}
							this.notificationDao.saveNotifications(notificationList);
							Map<String, Integer> map = new HashMap<String, Integer>();
							if ((pnList != null) && (pnList.size() > 0)) {
								for (PushNotification pn : pnList) {
									int count = this.notificationDao.getNotificationByRecipientId(pn.getUserId());
									map.put(pn.getClientId(), Integer.valueOf(count));
								}
							}
							String content = u.getUsername() + "注册了";
							JSONObject json = new JSONObject();
							json.put("user_id",u.getId());
							PushNotificationUtil.pushInfoAllFollow(gm.getAppId(), gm.getAppKey(), gm.getMasterSecret(), pnList, map, content,json.toString());
							
							//--
							JSONObject auth = new JSONObject();
							auth.put("userid", u.getId());
							String raw = u.getId() + u.getCreated_time().toString();

							String tokens = EncryptionUtil.hashMessage(raw);
							auth.put("access_token", tokens);
							auth.put("token_timestamp", u.getCreated_time());
							auth.put("username",u.getUsername());
							auth.put("fbid",centre_id);
							auth.put("token",fb_token);
							String avatarImage = u.getAvatarImage();
							if(!Strings.isNullOrEmpty(avatarImage)){
								auth.put("avatar_image",JSONObject.fromObject(avatarImage));
							}
							return Response.status(Response.Status.OK).entity(auth).build();
							
						}else{

							User u = new User();
							
							if(!Strings.isNullOrEmpty(fbname)){
								u.setFbname(fbname);
								String chars = "abcde0f12g3hi4jk5l6m7n8o9pqrstuvwxyz";
						    	StringBuffer sb = new StringBuffer();
						    	for(int i=0;i<10;i++){
						    		char c = chars.charAt((int)(Math.random() * 36));
						    		sb.append(c);
						    	}
								String pwd = Base64Utils.encodeToString(sb.toString().getBytes());
								u.setPassword(pwd);
								
								//---
								if(centre_info.containsKey("nickname")){
									String nickname = centre_info.getString("nickname");
									if(!Strings.isNullOrEmpty(nickname)){
										u.setUsername(nickname);
									}else{
										u.setUsername(fbname);
									}
								}else{
									u.setUsername(fbname);
								}
								u.setSalt(initSalt().toString());
								u.setStatus("enabled");
								u.setUser_type("normal");
								this.userDao.save(u);
								Configuration c = new Configuration();
								c.setNew_admin_push(true);
								c.setNew_comment_on_your_comment_push(true);
								c.setNew_comment_on_your_story_push(true);
								c.setNew_favorite_from_following_push(true);
								c.setNew_follower_push(true);
								c.setNew_story_from_following_push(true);
								c.setRecommended_my_story_push(true);
								c.setReposted_my_story_push(true);
								
								c.setUserId((Long) u.getId());
								
								this.configurationDao.save(c);
								List<User> userList = this.userDao.getUserByUserType();
								List<PushNotification> pnList = new ArrayList<PushNotification>();
								Notification n = null;
								List<Notification> notificationList = new ArrayList<Notification>();
								Configuration conf;
								if ((userList != null) && (userList.size() > 0)) {
									for (User admin : userList) {
										n = new Notification();
										n.setRecipientId((Long) admin.getId());
//										n.setSenderId((Long) u.getId());
										n.setNotificationType(8);
										n.setObjectType(3);
										n.setObjectId((Long) admin.getId());
										n.setStatus("enabled");
										n.setRead_already(true);
										notificationList.add(n);
										conf = this.configurationDao.getConfByUserId((Long) admin.getId());
										if (conf.isNew_admin_push()) {
											List<PushNotification> list = this.pushNotificationDao
													.getPushNotificationByUserid(admin.getId());
											pnList.addAll(list);
										}
									}
								}
								this.notificationDao.saveNotifications(notificationList);
								Map<String, Integer> map = new HashMap<String, Integer>();
								if ((pnList != null) && (pnList.size() > 0)) {
									for (PushNotification pn : pnList) {
										int count = this.notificationDao.getNotificationByRecipientId(pn.getUserId());
										map.put(pn.getClientId(), Integer.valueOf(count));
									}
								}
								String content = u.getUsername() + "注册了";
								JSONObject json = new JSONObject();
								json.put("user_id",u.getId());
								PushNotificationUtil.pushInfoAllFollow(gm.getAppId(), gm.getAppKey(), gm.getMasterSecret(), pnList, map, content,json.toString());
								
								//--
								JSONObject auth = new JSONObject();
								auth.put("userid", u.getId());
								String raw = u.getId() + u.getCreated_time().toString();

								String tokens = EncryptionUtil.hashMessage(raw);
								auth.put("access_token", tokens);
								auth.put("token_timestamp", u.getCreated_time());
								auth.put("username",u.getUsername());
								auth.put("fbid",centre_id);
								auth.put("token",fb_token);
								String avatarImage = u.getAvatarImage();
								if(!Strings.isNullOrEmpty(avatarImage)){
									auth.put("avatar_image",JSONObject.fromObject(avatarImage));
								}
								return Response.status(Response.Status.OK).entity(auth).build();
							}else{
								res.put("status", "request error");
								res.put("code", Integer.valueOf(10010));
								res.put("error_message", "request is invalid");
								return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
							}
							
						}
					}else if(centre_info.containsKey("username")){
						User u = new User();
						String fbname = centre_info.getString("username");
						if(!Strings.isNullOrEmpty(fbname)){
							u.setFbname(fbname);
							String chars = "abcde0f12g3hi4jk5l6m7n8o9pqrstuvwxyz";
					    	StringBuffer sb = new StringBuffer();
					    	for(int i=0;i<10;i++){
					    		char c = chars.charAt((int)(Math.random() * 36));
					    		sb.append(c);
					    	}
							String pwd = Base64Utils.encodeToString(sb.toString().getBytes());
							u.setPassword(pwd);
							
							//---
							if(centre_info.containsKey("nickname")){
								String nickname = centre_info.getString("nickname");
								if(!Strings.isNullOrEmpty(nickname)){
									u.setUsername(nickname);
								}else{
									u.setUsername(fbname);
								}
							}else{
								u.setUsername(fbname);
							}
							u.setSalt(initSalt().toString());
							u.setStatus("enabled");
							u.setUser_type("normal");
							this.userDao.save(u);
							Configuration c = new Configuration();
							c.setNew_admin_push(true);
							c.setNew_comment_on_your_comment_push(true);
							c.setNew_comment_on_your_story_push(true);
							c.setNew_favorite_from_following_push(true);
							c.setNew_follower_push(true);
							c.setNew_story_from_following_push(true);
							c.setRecommended_my_story_push(true);
							c.setReposted_my_story_push(true);
							
							c.setUserId((Long) u.getId());
							
							this.configurationDao.save(c);
							List<User> userList = this.userDao.getUserByUserType();
							List<PushNotification> pnList = new ArrayList<PushNotification>();
							Notification n = null;
							List<Notification> notificationList = new ArrayList<Notification>();
							Configuration conf;
							if ((userList != null) && (userList.size() > 0)) {
								for (User admin : userList) {
									n = new Notification();
									n.setRecipientId((Long) admin.getId());
//									n.setSenderId((Long) u.getId());
									n.setNotificationType(8);
									n.setObjectType(3);
									n.setObjectId((Long) admin.getId());
									n.setStatus("enabled");
									n.setRead_already(true);
									notificationList.add(n);
									conf = this.configurationDao.getConfByUserId((Long) admin.getId());
									if (conf.isNew_admin_push()) {
										List<PushNotification> list = this.pushNotificationDao
												.getPushNotificationByUserid(admin.getId());
										pnList.addAll(list);
									}
								}
							}
							this.notificationDao.saveNotifications(notificationList);
							Map<String, Integer> map = new HashMap<String, Integer>();
							if ((pnList != null) && (pnList.size() > 0)) {
								for (PushNotification pn : pnList) {
									int count = this.notificationDao.getNotificationByRecipientId(pn.getUserId());
									map.put(pn.getClientId(), Integer.valueOf(count));
								}
							}
							String content = u.getUsername() + "注册了";
							JSONObject json = new JSONObject();
							json.put("user_id",u.getId());
							PushNotificationUtil.pushInfoAllFollow(gm.getAppId(), gm.getAppKey(), gm.getMasterSecret(), pnList, map, content,json.toString());
							
							//--
							JSONObject auth = new JSONObject();
							auth.put("userid", u.getId());
							String raw = u.getId() + u.getCreated_time().toString();

							String tokens = EncryptionUtil.hashMessage(raw);
							auth.put("access_token", tokens);
							auth.put("token_timestamp", u.getCreated_time());
							auth.put("username",u.getUsername());
							auth.put("fbid",centre_id);
							auth.put("token",fb_token);
							String avatarImage = u.getAvatarImage();
							if(!Strings.isNullOrEmpty(avatarImage)){
								auth.put("avatar_image",JSONObject.fromObject(avatarImage));
							}
							return Response.status(Response.Status.OK).entity(auth).build();
						}else{
							res.put("status", "request error");
							res.put("code", Integer.valueOf(10010));
							res.put("error_message", "request is invalid");
							return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
						}
					}
				}
			}else if(code == 10004){
				res.put("status", "校验码错误");
				res.put("code", 10640);
				res.put("error_message", "校验码错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
			}else if(code == 10001){
				res.put("status", "缺少参数");
				res.put("code", 10641);
				res.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
			}else if(code == 10102){
				res.put("status", "token不存在");
				res.put("code", 10642);
				res.put("error_message",  "token不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
			}else if(code == 10103){
				res.put("status", "token已过期");
				res.put("code", 10643);
				res.put("error_message",  "token已过期");
				return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
			}else if(code == 10120){
				res.put("status", "系统内部错误");
				res.put("code", 10644);
				res.put("error_message",  "系统内部错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
			}else if(code == 10140){
				res.put("status", "用户信息不存在");
				res.put("code", 10645);
				res.put("error_message",  "用户信息不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
			}
		}else{
			res.put("status", "request_invalid"+ip);
			res.put("code", Integer.valueOf(10010));
			res.put("error_message", "request is invalid"+ip);
			return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
		}
		return null;
		
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

	@Override
	public Response error_fbtoken() {
		JSONObject jo = new JSONObject();
		jo.put("status", "token无效");
		jo.put("code", 10623);
		jo.put("error_message", "token无效");
		return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
	}

	@Override
	public Response logout(String fbToken,String device, HttpServletRequest request) throws Exception {
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = parseJson(urlkey);
		String url = jsonObject.getString("url");
		Map<String,String> param = new HashMap<String, String>();
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		param.put("ip", ip);
		param.put("token",fbToken);
		param.put("device", device);
		String params = publicParam(param);
		String result = HttpUtil.sendPostStr(url+"/customer/account/logout", params);
		JSONObject res = JSONObject.fromObject(result);
		int code = res.getInt("code");
		JSONObject json = new JSONObject();
		if(code == 10000){
			json.put("status", "success");
			return Response.status(Response.Status.OK).entity(json).build();
		}
		return null;
	}

	@Override
	public Response fbtoken() throws Exception {
		String urlkey = getClass().getResource("/../../META-INF/fb.json").getPath();
		JSONObject jsonObject = parseJson(urlkey);
		String url = jsonObject.getString("url");
		String result = HttpUtil.sendPostStr(url+"forum.php", "mod=qiniu&action=gettoken&uid=3217");
		
		return Response.status(Response.Status.OK).entity(result).build();
	}

	@Override
	public Response fbstory(JSONObject story) throws Exception {
		String urlkey = getClass().getResource("/../../META-INF/fb.json").getPath();
		JSONObject jsonObject = parseJson(urlkey);
		String url = jsonObject.getString("url");
		String result = HttpUtil.sendPostStr(url+"fb_editor/json2bbcode.php", "message="+story);
		
		return Response.status(Response.Status.OK).entity(result).build();
	}

	
	
}
