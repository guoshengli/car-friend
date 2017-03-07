package com.revolution.rest.service;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import com.google.common.base.Strings;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;
import com.revolution.rest.ApiHttpClient;
import com.revolution.rest.common.EncryptionUtil;
import com.revolution.rest.common.FBEncryption;
import com.revolution.rest.common.HttpUtil;
import com.revolution.rest.common.ParseFile;
import com.revolution.rest.common.PushNotificationUtil;
import com.revolution.rest.dao.ChatDao;
import com.revolution.rest.dao.CollectionDao;
import com.revolution.rest.dao.CollectionStoryDao;
import com.revolution.rest.dao.CommentDao;
import com.revolution.rest.dao.ConfigurationDao;
import com.revolution.rest.dao.ConversionDao;
import com.revolution.rest.dao.CoverPageDao;
import com.revolution.rest.dao.FeatureCollectionDao;
import com.revolution.rest.dao.FeatureUserDao;
import com.revolution.rest.dao.FeedbackDao;
import com.revolution.rest.dao.FollowDao;
import com.revolution.rest.dao.LikesDao;
import com.revolution.rest.dao.LinkAccountsDao;
import com.revolution.rest.dao.NotificationDao;
import com.revolution.rest.dao.PushNotificationDao;
import com.revolution.rest.dao.RepublishDao;
import com.revolution.rest.dao.SlideDao;
import com.revolution.rest.dao.StoryDao;
import com.revolution.rest.dao.TimelineDao;
import com.revolution.rest.dao.UserCentreDao;
import com.revolution.rest.dao.UserCollectionDao;
import com.revolution.rest.dao.UserDao;
import com.revolution.rest.model.Chat;
import com.revolution.rest.model.Collection;
import com.revolution.rest.model.CollectionStory;
import com.revolution.rest.model.Columns;
import com.revolution.rest.model.Comment;
import com.revolution.rest.model.Configuration;
import com.revolution.rest.model.Conversion;
import com.revolution.rest.model.Cover_page;
import com.revolution.rest.model.FeatureCollection;
import com.revolution.rest.model.FeatureUser;
import com.revolution.rest.model.Feedback;
import com.revolution.rest.model.Follow;
import com.revolution.rest.model.FollowId;
import com.revolution.rest.model.FormatType;
import com.revolution.rest.model.Likes;
import com.revolution.rest.model.LinkAccounts;
import com.revolution.rest.model.Notification;
import com.revolution.rest.model.PublisherInfo;
import com.revolution.rest.model.PushNotification;
import com.revolution.rest.model.Republish;
import com.revolution.rest.model.SdkHttpResult;
import com.revolution.rest.model.Slide;
import com.revolution.rest.model.Story;
import com.revolution.rest.model.StoryElement;
import com.revolution.rest.model.Timeline;
import com.revolution.rest.model.User;
import com.revolution.rest.model.UserCentre;
import com.revolution.rest.service.model.CollectionIntro;
import com.revolution.rest.service.model.CollectionIntroLast;
import com.revolution.rest.service.model.CollectionIntros;
import com.revolution.rest.service.model.CommentSummaryModel;
import com.revolution.rest.service.model.CoverMedia;
import com.revolution.rest.service.model.CoverPageModel;
import com.revolution.rest.service.model.EventModel;
import com.revolution.rest.service.model.GetuiModel;
import com.revolution.rest.service.model.ImageCover;
import com.revolution.rest.service.model.LinkAccountModel;
import com.revolution.rest.service.model.LinkModel;
import com.revolution.rest.service.model.LinkModels;
import com.revolution.rest.service.model.LocationModel;
import com.revolution.rest.service.model.MailSenderInfo;
import com.revolution.rest.service.model.PasswordModel;
import com.revolution.rest.service.model.ProfileModel;
import com.revolution.rest.service.model.ProfileVo;
import com.revolution.rest.service.model.PublisherInfoModel;
import com.revolution.rest.service.model.SimpleMailSender;
import com.revolution.rest.service.model.SlideModel;
import com.revolution.rest.service.model.StoryEvent;
import com.revolution.rest.service.model.StoryEventNew;
import com.revolution.rest.service.model.StoryHome;
import com.revolution.rest.service.model.StoryHomeCopy;
import com.revolution.rest.service.model.StoryIntro;
import com.revolution.rest.service.model.StoryPageModel;
import com.revolution.rest.service.model.TextCover;
import com.revolution.rest.service.model.UserCollectionModel;
import com.revolution.rest.service.model.UserFeatur;
import com.revolution.rest.service.model.UserIntro;
import com.revolution.rest.service.model.UserModel;
import com.revolution.rest.service.model.UserParentModel;
import com.revolution.rest.service.model.UserPhone;
import com.revolution.rest.service.model.UserPublisherModel;
import com.revolution.rest.service.model.UserQiNiuModel;

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
	private LikesDao likesDao;

	@Autowired
	private ConfigurationDao configurationDao;

	@Autowired
	private FollowDao followDao;

	@Autowired
	private NotificationDao notificationDao;

	@Autowired
	private CommentDao commentDao;

	@Autowired
	private LinkAccountsDao linkAccountsDao;

	@Autowired
	private PushNotificationDao pushNotificationDao;

	@Autowired
	private CollectionDao collectionDao;
	
	@Autowired
	private FeedbackDao feedbackDao;

	@Autowired
	private CollectionStoryDao collectionStoryDao;
	
	@Autowired
	private CoverPageDao coverPageDao;
	
	@Autowired
	private SlideDao slideDao;
	
	@Autowired
	private FeatureUserDao featureUserDao;
	
	@Autowired
	private FeatureCollectionDao featureCollectionDao;
	
	@Autowired
	private UserCollectionDao userCollectionDao;
	
	@Autowired
	private ChatDao chatDao;
	
	@Autowired
	private ConversionDao conversionDao;
	
	@Autowired
	private UserCentreDao userCentreDao;

	public Response create(JSONObject user,String appVersion,HttpServletRequest request) {
		User u = new User();
		int centre_id = 0;
		String ip = request.getRemoteAddr();
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = parseJson(urlkey);
		String url = jsonObject.getString("url");
		String device = request.getHeader("X-Tella-Request-Device");
		String fbToken = "";
		String script = "";
		try {
			GetuiModel gm = getGetuiInfo();
			if (user != null) {
				if (Strings.isNullOrEmpty(user.getString("username").trim())) {
					JSONObject jo = new JSONObject();
					jo.put("status", "username_null");
					jo.put("code",10032);
					jo.put("error_message", "Username is not null");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}
				if (user.containsKey("email")) {
					if (!Strings.isNullOrEmpty(user.getString("email"))) {
						if (matchEmail(user.getString("email"))) {
							User users = this.userDao.getUserByEamil(user.getString("email"));
							if (users != null) {
								JSONObject jo = new JSONObject();
								jo.put("status", "email_exists");
								jo.put("code", Integer.valueOf(10009));
								jo.put("error_message", "Email already used");
								return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
							}
						} else {
							JSONObject jo = new JSONObject();
							jo.put("status", "email_invalid");
							jo.put("code", Integer.valueOf(10050));
							jo.put("error_message", "Email is invalid");
							return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
						}

						u.setEmail(user.getString("email"));
					}
				} else
					u.setEmail(null);
				if (user.containsKey("phone")) {
					JSONObject resp = new JSONObject();
					String zone = user.get("zone").toString();
					String phone = user.get("phone").toString();
					String code = user.get("code").toString();
					if ((!Strings.isNullOrEmpty(zone)) && (!Strings.isNullOrEmpty(phone))
							&& (!Strings.isNullOrEmpty(code))) {
						User users = this.userDao.getUserByPhoneAndZone(zone, phone);
						if (users != null) {
							resp.put("status", "phone_exists");
							resp.put("code", Integer.valueOf(10094));
							resp.put("error_message", "Phone already used");
							return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
						}
						
						/*String path1 = getClass().getResource("/../../META-INF/version.json").getPath();
						JSONObject v = ParseFile.parseJson(path1);
						String version = v.getString("version");
						boolean flag = false;
						if(!Strings.isNullOrEmpty(version)){
							String[] vArr = version.split("\\.");
							String[] vaArr = appVersion.split("\\.");
							if(version.equals(appVersion)){
								flag = true;
							}else{
								if(!vArr[0].equals(vaArr[0])){
									if(Integer.parseInt(vArr[0]) > Integer.parseInt(vaArr[0])){
										flag = false;
									}else{
										flag = true;
									}
								}else{
									if(!vArr[1].equals(vaArr[1])){
										if(Integer.parseInt(vArr[1]) > Integer.parseInt(vaArr[1])){
											flag = false;
										}else{
											flag = true;
										}
									}else{
										if(!vArr[2].equals(vaArr[2])){
											if(Integer.parseInt(vArr[2]) > Integer.parseInt(vaArr[2])){
												flag = false;
											}else{
												flag = true;
											}
										}
									}
								}
							}
						}*/
						/*if(flag){
							appkey = getClass().getResource("/../../META-INF/phone2.json").getPath();
						}else{
							appkey = getClass().getResource("/../../META-INF/phone.json").getPath();
						}*/
						
						Map<String,String> param = new HashMap<String,String>();
						param.put("mobile", phone);
						param.put("code", code);
						param.put("ip",ip);
						String regParams = publicParam(param);
						String result = HttpUtil.sendPostStr(url+"/customer/account/check-mobile-for-register", regParams);
						/*if(flag){
							result = requestData("https://webapi.sms.mob.com/sms/verify", param);
						}else{
							result = requestData("https://web.sms.mob.com/sms/verify", param);
						}*/
						
						if (!Strings.isNullOrEmpty(result)) {
							JSONObject json = JSONObject.fromObject(result);
							int status = json.getInt("code");
							if (status == 10000) {
								u.setZone(zone);
								u.setPhone(phone);
								Map<String,String> registerParam = new HashMap<String, String>();
								registerParam.put("mobile", phone);
								registerParam.put("code", code);
								registerParam.put("username", "fd_"+phone);
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
									if(device.equals("10")){
										script = data.getString("script");
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

				if (!user.containsKey("password")) {
					String chars = "abcde0f12g3hi4jk5l6m7n8o9pqrstuvwxyz";
			    	StringBuffer sb = new StringBuffer();
			    	for(int i=0;i<10;i++){
			    		char c = chars.charAt((int)(Math.random() * 36));
			    		sb.append(c);
			    	}
					String pwd = Base64Utils.encodeToString(sb.toString().getBytes());
					u.setPassword(pwd);
				}else{
					String pwd = Base64Utils.encodeToString(user.getString("password").getBytes());
					u.setPassword(pwd);
				}

				u.setUsername(user.getString("username"));
				u.setSalt(initSalt().toString());
				u.setStatus("enabled");
				u.setUser_type("normal");
				
				if(centre_id > 0){
					this.userDao.save(u);
					UserCentre uc = new UserCentre();
					uc.setCentre_id(centre_id);
					uc.setUser_id(u.getId());
					userCentreDao.save(uc);
				}else{
					if(user.containsKey("phone")){
						JSONObject jo = new JSONObject();
						jo.put("status", "注册失败，请验证后再试");
						jo.put("code", 10609);
						jo.put("error_message", "注册失败，请验证后再试");
						return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
					}else{
						this.userDao.save(u);
					}
				}
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
				List<User> officialUser = userDao.getUserByUserType("official");
				if(officialUser != null && officialUser.size() > 0){
					for(User official:officialUser){
						Follow f = new Follow();
						FollowId fid = new FollowId();
						fid.setUser(u);
						fid.setFollower(official);
						f.setPk(fid);
						f.setCreateTime(new Date());
						followDao.save(f);
					}
				}
				List<User> userList = this.userDao.getUserByUserType();
				List<PushNotification> pnList = new ArrayList<PushNotification>();
				Notification n = null;
				List<Notification> notificationList = new ArrayList<Notification>();
				Configuration conf;
				if ((userList != null) && (userList.size() > 0)) {
					for (User admin : userList) {
						n = new Notification();
						n.setRecipientId((Long) admin.getId());
						n.setSenderId((Long) u.getId());
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
				if (user.containsKey("link_account")) {
					JSONObject link = JSONObject.fromObject(user.get("link_account"));
					LinkAccounts la = new LinkAccounts();
					la.setAuth_token(link.getString("auth_token"));
					if(link.containsKey("avatar_url")){
						la.setAvatar_url(link.getString("avatar_url"));
					}
					la.setDescription(link.getString("description"));
					la.setRefreshed_at(link.getString("refreshed_at"));
					la.setService(link.getString("service"));
					la.setUser_id(u.getId());
					la.setUuid(link.getString("uuid"));
					
					if(link.containsKey("union_id")){
						la.setUnion_id(link.getString("union_id"));
					}
					
					this.linkAccountsDao.save(la);
					
					Map<String,String> param = new HashMap<String, String>();
					
					param.put("third_party_openid", la.getUnion_id().toString());
					param.put("device", device);
					param.put("ip", ip);
					String thried = la.getService();
					
					if(thried.equals("wechat")){
						param.put("third_party_channel", "10");
					}else if(thried.equals("weibo")){
						param.put("third_party_channel", "20");
					}
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
							UserCentre uc = new UserCentre();
							uc.setCentre_id(centreid);
							uc.setUser_id(u.getId());
							userCentreDao.save(uc);
							String raw = u.getId() + u.getCreated_time().toString();
							String token = EncryptionUtil.hashMessage(raw);

							System.out.println("userId--->" + u.getId());
							resp.put("userid", u.getId());
							resp.put("access_token", token);
							resp.put("token_timestamp", u.getCreated_time());
							resp.put("token", fb_token);
							resp.put("fbid", centreid);
							System.out.println(resp.toString());
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
		json.put("userid", u.getId());
		String raw = u.getId() + u.getCreated_time().toString();

		String token = EncryptionUtil.hashMessage(raw);
		json.put("access_token", token);
		json.put("token_timestamp", u.getCreated_time());
		json.put("username",u.getUsername());
		json.put("token",fbToken);
		json.put("fbid",centre_id);
		if(!Strings.isNullOrEmpty(device) 
				&& device.equals("10") &&
				!Strings.isNullOrEmpty(script)){
			json.put("script",script);
		}
		String avatarImage = u.getAvatarImage();
		if(!Strings.isNullOrEmpty(avatarImage)){
			json.put("avatar_image",JSONObject.fromObject(avatarImage));
		}
		return Response.status(Response.Status.CREATED).entity(json).build();
	}

	public UserParentModel get(Long userId, Long loginUserid,String appVersion) {
		log.debug("****get user function**********");
		log.debug("*** loginUserid ****" + loginUserid + " *** userId" + userId);
		/**
		 * 版本控制
		 */
		boolean flagVersion = false;
		if(!Strings.isNullOrEmpty(appVersion)){
			flagVersion = isAvailableVersion(appVersion);
		}else{
			flagVersion = true;
		}
		
		
		User user = null;
		String website = null;
		String profileUrl = null;
		List<LinkAccountModel> linkList;
		if(loginUserid != null && loginUserid > 0){
			if (loginUserid.equals(userId)) {
				String path1 = getClass().getResource("/../../META-INF/url.json").getPath();
				String path2 = getClass().getResource("/../../META-INF/apple.json").getPath();
				JSONObject json1 = ParseFile.parseJson(path1);
				JSONObject json2 = ParseFile.parseJson(path2);
				String share_url_prefix = json1.getString("url");
				String appstore_link = json2.getString("appstore_link");
				user = (User) this.userDao.get(loginUserid);
				if (user != null) {
					Configuration c = this.configurationDao.getConfByUserId(userId);
					JSONObject nJson = new JSONObject();
					if (c != null) {
						nJson = JSONObject.fromObject(c);
					} else {
						JSONObject json = new JSONObject();
						json.put("status", "no_resource");
						json.put("code", Integer.valueOf(10011));
						json.put("error_message", "The user does not exist");
						return null;
					}
					nJson.remove("id");
					nJson.remove("userId");
					String path = getClass().getResource("/../../META-INF/qiniu.json").getPath();
					JSONObject jsonObject = parseJson(path);
					String ak = jsonObject.getString("ak");
					String sk = jsonObject.getString("sk");
					String bucket = jsonObject.getString("bucket");
					String token = null;
					try {
						token = getToken(ak, sk, bucket);
					} catch (Exception e) {
						e.printStackTrace();
					}
					String meta = jsonObject.getString("meta");
					JSONObject qiniuJson = new JSONObject();
					qiniuJson.put("upload_token", token);
					qiniuJson.put("meta", meta);

					//int likeCount = this.likesDao.userLikesCount(userId);
					int repostCount = 0;//this.republishDao.userRepostCount(userId);
					Set<Story> sSet = user.getRepost_story();
					if(sSet != null && sSet.size() > 0){
						repostCount = sSet.size();
					}
					
					//int storyCount = storyDao.getStoryCount(user.getId());//�?要剔�?
					int follower_Count = this.followDao.userFollowedCount(userId);
					int following_count = this.followDao.userFollowCount(userId);

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

					if (!Strings.isNullOrEmpty(user.getProfile_url())) {
						profileUrl = user.getProfile_url();
					}

					linkList = new ArrayList<LinkAccountModel>();
					List<LinkAccounts> link = this.linkAccountsDao.getLinkAccountsByUserid(loginUserid);
					LinkAccountModel lam = null;
					if ((link != null) && (link.size() > 0)) {
						for (LinkAccounts l : link) {
							lam = new LinkAccountModel();
							lam.setId((Long) l.getId());
							lam.setAuth_token(l.getAuth_token());
							lam.setDescription(l.getDescription());
							lam.setRefreshed_at(l.getRefreshed_at());
							lam.setService(l.getService());
							lam.setUuid(l.getUuid());
							lam.setUser_id(l.getUser_id());
							linkList.add(lam);
						}
					}
					List<CollectionIntroLast> collection_info = new ArrayList<CollectionIntroLast>();
					if(!flagVersion){
						CollectionIntroLast ci = null;
						List<Collection> cList = null;
						 if((user.getUser_type().equals("admin"))
									|| (user.getUser_type().equals("super_admin"))
									|| (user.getUser_type().equals("official"))){
							 cList = this.collectionDao.getCollections();
				    	 }else{
				    		 cList = collectionDao.getCollectionBynormal();
				    	 }
						//this.collectionDao.getCollections();
						if ((cList != null) && (cList.size() > 0)) {
							for (Collection co : cList) {
								ci = new CollectionIntroLast();
								ci.setId((Long) co.getId());
								ci.setCollection_name(co.getCollectionName());
								ci.setCover_image(JSONObject.fromObject(co.getCover_image()));
								ci.setInfo(co.getInfo());
								Set<User> uSet = co.getUsers();
								Iterator<User> it = uSet.iterator();
								if(it.hasNext()){
									boolean flag = false;
									while(it.hasNext()){
										User u = it.next();
										if(u.getId().equals(loginUserid) 
												&& u.getId() == loginUserid){
											flag = true;
											break;
										}
									}
									ci.setIs_followed_by_current_user(flag);
								}else{
									ci.setIs_followed_by_current_user(false);
								}
								
								Set<Story> storySet = co.getStories();
								int story_count = storySet.size();
								int followers_count = uSet.size();
								ci.setStory_count(story_count);
								ci.setFollowers_count(followers_count);
								User u = co.getUser();
								 JSONObject aiJson = null;
						         if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
						        	 aiJson = JSONObject.fromObject(u
						             .getAvatarImage());
						         }
						 
						         JSONObject ciJson = null;
						         if (!Strings.isNullOrEmpty(u.getCoverImage())) {
						        	 ciJson = 
						             JSONObject.fromObject(u.getCoverImage());
						         }
						         JSONObject authorJson = new JSONObject();
						         authorJson.put("id", u.getId());
						         authorJson.put("username", u.getUsername());
						         authorJson.put("email", u.getEmail());
						         authorJson.put("created_time", u.getCreated_time());
						         authorJson.put("status", u.getStatus());
						         authorJson.put("introduction", u.getIntroduction());
						         authorJson.put("avatar_image", aiJson);
						         authorJson.put("cover_image", ciJson);
						         ci.setAuthor(authorJson);
								 collection_info.add(ci);
							}
						}
					}
					List<Collection> followed_collection = userCollectionDao.getCollectionByUserid(loginUserid,"activity");
					
					List<CollectionIntroLast> followed_collection_info = new ArrayList<CollectionIntroLast>();
					CollectionIntroLast cis = null;
					if(followed_collection != null 
							&& followed_collection.size() > 0){
						for(Collection collections:followed_collection){

							cis = new CollectionIntroLast();
							cis.setId(collections.getId());
							cis.setCollection_name(collections.getCollectionName());
							cis.setCover_image(JSONObject.fromObject(collections.getCover_image()));
							cis.setInfo(collections.getInfo());
							Set<User> uSet = collections.getUsers();
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
							
							Set<Story> storySet = collections.getStories();
							int story_count = storySet.size();
							int followers_count = uSet.size();
							cis.setStory_count(story_count);
							cis.setFollowers_count(followers_count);
							User u = collections.getUser();//userDao.get(collections.getAuthorId());
							 JSONObject aiJson = null;
					         if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
					        	 aiJson = JSONObject.fromObject(u
					             .getAvatarImage());
					         }
					 
					         JSONObject ciJson = null;
					         if (!Strings.isNullOrEmpty(u.getCoverImage())) {
					        	 ciJson = 
					             JSONObject.fromObject(u.getCoverImage());
					         }
					         JSONObject authorJson = new JSONObject();
					         authorJson.put("id", u.getId());
					         authorJson.put("username", u.getUsername());
					         authorJson.put("email", u.getEmail());
					         authorJson.put("created_time", u.getCreated_time());
					         authorJson.put("status", u.getStatus());
					         authorJson.put("introduction", u.getIntroduction());
					         authorJson.put("avatar_image", aiJson);
					         authorJson.put("cover_image", ciJson);
					         cis.setAuthor(authorJson);
							followed_collection_info.add(cis);
						
						}
					}
					
					List<Cover_page> coverList = this.coverPageDao.getAll();
					List<CoverPageModel> cpmList = null;
					if ((coverList != null) && (coverList.size() > 0)) {
						cpmList = new ArrayList<CoverPageModel>();
						CoverPageModel cpm = null;
						for (Cover_page cp : coverList) {
							JSONObject image = JSONObject.fromObject(cp.getImage());
							cpm = new CoverPageModel();
							cpm.setName(image.getString("name"));
							cpm.setOriginal_size(image.getString("original_size"));
							cpmList.add(cpm);
						}
					}
					int story_count = 0;
					story_count = user.getStories().size();
					
					UserParentModel userModel = null;
					userModel = new UserQiNiuModel((Long) user.getId(), user.getUsername(), user.getEmail(),
							user.getZone(), user.getPhone(), user.getCreated_time(), user.getStatus(),
							user.getIntroduction(), avatarImageJson, coverImageJson,  repostCount,story_count,
							follower_Count, following_count, website, profileUrl, nJson, qiniuJson,
							user.getEmail_verified(), linkList, collection_info, cpmList,
							user.getUser_type(),share_url_prefix,appstore_link,user.getGender(),followed_collection_info);
					/*if ((user.getUser_type().equals("normal")) || (user.getUser_type().equals("vip")) || (user.getUser_type().equals("admin"))
							|| (user.getUser_type().equals("super_admin"))|| (user.getUser_type().equals("official"))) {
						
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
						userModel = new UserQiNiuPublisherModel((Long) user.getId(), user.getUsername(), user.getEmail(),
								user.getZone(), user.getPhone(), user.getCreated_time(), user.getStatus(),
								user.getIntroduction(), avatarImageJson, coverImageJson,  repostCount, storyCount,
								follower_Count, following_count, website, profileUrl, nJson, qiniuJson,
								user.getEmail_verified(), linkList, collection_info,  colorList, cpmList,
								user.getUser_type(), publisherList,share_url_prefix,appstore_link,user.getGender());
					}*/


					return userModel;
				}
				JSONObject json = new JSONObject();
				json.put("status", "no_resource");
				json.put("code", Integer.valueOf(10011));
				json.put("error_message", "The user does not exist");
				return null;
			}
		}
		

		user = (User) this.userDao.get(userId);
		if (user != null) {
			//int likeCount = this.likesDao.userLikesCount(userId);
			int repostCount = 0;//this.republishDao.userRepostCount(userId);
			Set<Story> sSet = user.getRepost_story();
			if(sSet != null && sSet.size() > 0){
				repostCount = sSet.size();
			}
			
			int storyCount = this.storyDao.getStoryCount((Long) user.getId());
			int follower_Count = this.followDao.userFollowedCount(userId);
			int following_count = this.followDao.userFollowCount(userId);
			Follow loginFollow = null;
			if(loginUserid != null && loginUserid > 0){
				loginFollow = this.followDao.getFollow(loginUserid, userId);
			}
			boolean followed_by_current_user = false;
			boolean is_following_current_user = false;
			if (loginFollow != null) {
				followed_by_current_user = true;
			}
			Follow currentFollow = null;
			if(loginUserid != null && loginUserid > 0){
				currentFollow = this.followDao.getFollow(userId, loginUserid);
			}
			
			if (currentFollow != null) {
				is_following_current_user = true;
			}
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

			if (!Strings.isNullOrEmpty(user.getProfile_url())) {
				profileUrl = user.getProfile_url();
			}
			List<CollectionIntroLast> followed_collection_info = null;
			/*List<Collection> followed_collection = userCollectionDao.getCollectionByUserid(userId,"activity");
			List<CollectionIntroLast> followed_collection_info = new ArrayList<CollectionIntroLast>();
			CollectionIntroLast cis = null;
			if(followed_collection != null 
					&& followed_collection.size() > 0){
				Iterator<Collection> it = followed_collection.iterator();
				while(it.hasNext()){
					Collection collections = it.next();

					cis = new CollectionIntroLast();
					cis.setId(collections.getId());
					cis.setCollection_name(collections.getCollectionName());
					cis.setCover_image(JSONObject.fromObject(collections.getCover_image()));
					cis.setInfo(collections.getInfo());
					cis.setAvatar_image(JSONObject.fromObject(collections.getAvatar_image()));
					cis.setCollection_type(collections.getCollection_type());
					Set<User> uSet = collections.getUsers();
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
					User u = collections.getUser();
					 JSONObject aiJson = null;
			         if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
			        	 aiJson = JSONObject.fromObject(u
			             .getAvatarImage());
			         }
			 
			         JSONObject ciJson = null;
			         if (!Strings.isNullOrEmpty(u.getCoverImage())) {
			        	 ciJson = 
			             JSONObject.fromObject(u.getCoverImage());
			         }
			         JSONObject authorJson = new JSONObject();
			         authorJson.put("id", u.getId());
			         authorJson.put("username", u.getUsername());
			         authorJson.put("email", u.getEmail());
			         authorJson.put("created_time", u.getCreated_time());
			         authorJson.put("status", u.getStatus());
			         authorJson.put("introduction", u.getIntroduction());
			         authorJson.put("avatar_image", aiJson);
			         authorJson.put("cover_image", ciJson);
			         cis.setAuthor(authorJson);
					cis.setIs_review(collections.isIs_review());
					followed_collection_info.add(cis);
				
				}
			}*/
			
			UserParentModel userModel = null;
			userModel = new UserCollectionModel((Long) user.getId(), user.getUsername(), user.getEmail(),
					user.getCreated_time(), user.getStatus(), user.getIntroduction(), avatarImageJson,
					coverImageJson,  repostCount, storyCount, follower_Count, following_count, website,
					followed_by_current_user, is_following_current_user,  user.getUser_type(),user.getGender(),followed_collection_info);
			/*if (user.getUser_type().equals("normal") || user.getUser_type().equals("vip") || user.getUser_type().equals("admin")
					|| user.getUser_type().equals("super_admin") || user.getUser_type().equals("official")) {
				
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
						coverImageJson,  repostCount, storyCount, follower_Count, following_count, website,
						followed_by_current_user, is_following_current_user,  user.getUser_type(),
						publisherList,user.getGender());
			}*/

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
			int follower_Count = this.followDao.userFollowedCount(userId);
			int following_count = this.followDao.userFollowCount(userId);
			Follow loginFollow = null;
			if(loginUserid != null && loginUserid > 0){
				loginFollow = this.followDao.getFollow(loginUserid, userId);
			}
			boolean followed_by_current_user = false;
			boolean is_following_current_user = false;
			if (loginFollow != null) {
				followed_by_current_user = true;
			}
			Follow currentFollow = null;
			if(loginUserid != null && loginUserid > 0){
				currentFollow = this.followDao.getFollow(userId, loginUserid);
			}
			
			if (currentFollow != null) {
				is_following_current_user = true;
			}
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
					cis.setId(collections.getId());
					cis.setCollection_name(collections.getCollectionName());
					cis.setCover_image(JSONObject.fromObject(collections.getCover_image()));
					cis.setInfo(collections.getInfo());
					Set<User> uSet = collections.getUsers();
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
						coverImageJson,  repostCount, storyCount, follower_Count, following_count, website,
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
						coverImageJson,  repostCount, storyCount, follower_Count, following_count, website,
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
		User user = null;
		String ip = request.getRemoteAddr();
		String email = userJson.containsKey("email")?userJson.getString("email"):null;
		String fbname = userJson.containsKey("fbname")?userJson.getString("fbname"):null;
		String password = userJson.containsKey("password")?userJson.getString("password"):null;
		String timestamp = userJson.containsKey("timestamp")?userJson.getString("timestamp"):null;
		String zone = userJson.containsKey("zone")?userJson.getString("zone"):null;
		String phone = userJson.containsKey("phone")?userJson.getString("phone"):null;
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String url = jsonObject.getString("url");
		JSONObject jo = new JSONObject();
		JSONObject auth = new JSONObject();
		int centre_id = 0;
		String fbToken = "";
		String script = "";
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
			User emailUser = this.userDao.getUserByEamil(email);

			if (emailUser != null) {
				try {
//					password = Base64Utils.encodeToString(password.getBytes());
//					user = this.userDao.loginUser(email, password);
					
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
						if(device.equals("10")){
							script = data.getString("script");
						}
						UserCentre uc = userCentreDao.getUserCentreByCentreId(centre_id);
						if(uc == null){
							UserCentre ucentre = new UserCentre();
							ucentre.setCentre_id(centre_id);
							ucentre.setUser_id(emailUser.getId());
							userCentreDao.save(ucentre);
						}
						user = emailUser;
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

					if (user == null){
						jo.put("status", "您输入的账号不存在，请先注册");
						jo.put("code", 10605);
						jo.put("error_message", "您输入的账号不存在，请先注册");
						return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
					}

				} catch (Exception e) {
					jo.put("status", "invalid_password");
					jo.put("code", Integer.valueOf(10007));
					jo.put("error_message", "invalid password");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}
			} else {
				jo.put("status", "invalid_email");
				jo.put("code", Integer.valueOf(10006));
				jo.put("error_message", "Email doesn't exist");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}

			String raw = user.getId() + timestamp;
			String token = EncryptionUtil.hashMessage(raw);

			System.out.println("userId--->" + user.getId());
			String avatarImage = user.getAvatarImage();
			if(!Strings.isNullOrEmpty(avatarImage)){
				auth.put("avatar_image",JSONObject.fromObject(avatarImage));
			}
			auth.put("userid", user.getId());
			auth.put("access_token", token);
			auth.put("username", user.getUsername());
			auth.put("token_timestamp", Long.valueOf(Long.parseLong(timestamp)));
			auth.put("token", fbToken);
			auth.put("fbid",centre_id);
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
					String fbusername = data.getString("username");
					if(device.equals("10")){
						script = data.getString("script");
					}
					List<User> uList = userDao.getUserByFbnameAndPhone(fbusername, phone);
					//User phoneUser = this.userDao.getUserByPhoneAndZone(zone, phone);
					if(uList != null && uList.size() > 0){
						User phoneUser = uList.get(0);
						UserCentre uc = userCentreDao.getUserCentreByCentreId(centre_id);
						if(uc == null){
							UserCentre ucentre = new UserCentre();
							ucentre.setCentre_id(centre_id);
							ucentre.setUser_id(phoneUser.getId());
							userCentreDao.save(ucentre);
						}
						if(Strings.isNullOrEmpty(phoneUser.getFbname())){
							phoneUser.setFbname(fbname);
							userDao.update(phoneUser);
						}
						user = phoneUser;
					}else{
					
						User u = new User();
						u.setZone(zone);
						u.setPhone(phone);
						u.setFbname(fbusername);
						String chars = "abcde0f12g3hi4jk5l6m7n8o9pqrstuvwxyz";
				    	StringBuffer sb = new StringBuffer();
				    	for(int i=0;i<10;i++){
				    		char c = chars.charAt((int)(Math.random() * 10));
				    		sb.append(c);
				    	}
				    	u.setPassword(password);
				    	u.setUsername(sb.toString());
				    	u.setSalt(initSalt().toString());
				    	u.setStatus("enabled");
				    	u.setUser_type("normal");
						
				    	this.userDao.save(u);
				    	
				    	UserCentre uc = new UserCentre();
						uc.setCentre_id(centre_id);
						uc.setUser_id(u.getId());
						userCentreDao.save(uc);
						user =u;
						
						//--
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
						List<User> officialUser = userDao.getUserByUserType("official");
						if(officialUser != null && officialUser.size() > 0){
							for(User official:officialUser){
								Follow f = new Follow();
								FollowId fid = new FollowId();
								fid.setUser(u);
								fid.setFollower(official);
								f.setPk(fid);
								f.setCreateTime(new Date());
								followDao.save(f);
							}
						}
						List<User> userList = this.userDao.getUserByUserType();
						List<PushNotification> pnList = new ArrayList<PushNotification>();
						Notification n = null;
						List<Notification> notificationList = new ArrayList<Notification>();
						Configuration conf;
						if ((userList != null) && (userList.size() > 0)) {
							for (User admin : userList) {
								n = new Notification();
								n.setRecipientId((Long) admin.getId());
								n.setSenderId((Long) u.getId());
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
						GetuiModel gm = getGetuiInfo();
						PushNotificationUtil.pushInfoAllFollow(gm.getAppId(), gm.getAppKey(), gm.getMasterSecret(), pnList, map, content,json.toString());
						//--
					}
					
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

				if (user == null){
					jo.put("status", "您输入的账号不存在，请先注册");
					jo.put("code", 10605);
					jo.put("error_message", "您输入的账号不存在，请先注册");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}
			} catch (Exception e) {
				jo.put("status", "invalid_password");
				jo.put("code", Integer.valueOf(10007));
				jo.put("error_message", "invalid password");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}
		
			//---

			String raw = user.getId() + timestamp;
			String token = EncryptionUtil.hashMessage(raw);

			System.out.println("userId--->" + user.getId());
			String avatarImage = user.getAvatarImage();
			if(!Strings.isNullOrEmpty(avatarImage)){
				auth.put("avatar_image",JSONObject.fromObject(avatarImage));
			}
			auth.put("userid", user.getId());
			auth.put("access_token", token);
			auth.put("username", user.getUsername());
			auth.put("token_timestamp", Long.valueOf(Long.parseLong(timestamp)));
			auth.put("token", fbToken);
			auth.put("fbid", centre_id);
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

			/*if (fbUser != null) {} else {
				jo.put("status", "invalid_email");
				jo.put("code", Integer.valueOf(10006));
				jo.put("error_message", "Email doesn't exist");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}*/
			
			//---
			


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
					UserCentre userFB = userCentreDao.getUserCentreByCentreId(centre_id);
					if(userFB != null){
						user = userDao.get(userFB.getUser_id());
						if(Strings.isNullOrEmpty(user.getFbname())){
							user.setFbname(fbname);
							userDao.update(user);
						}
					}else{
						Object[] objArr = linkAccountsDao.getLinkAccountsByUUIDAndService(String.valueOf(centre_id), "fblife");
						if(objArr != null && objArr.length > 0){
							User linkUser = (User)objArr[1];
							UserCentre uc = new UserCentre();
							uc.setCentre_id(centre_id);
							uc.setUser_id(linkUser.getId());
							userCentreDao.save(uc);
							user = linkUser;
						}else{
							String fbphone = data.getString("mobile");
							User fb_user = new User();
							String chars = "abcde0f12g3hi4jk5l6m7n8o9pqrstuvwxyz";
					    	StringBuffer sb = new StringBuffer();
					    	for(int i=0;i<10;i++){
					    		char c = chars.charAt((int)(Math.random() * 10));
					    		sb.append(c);
					    	}
					    	fb_user.setFbname(fbname);
					    	if(!Strings.isNullOrEmpty(fbphone)){
					    		fb_user.setZone("86");
						    	fb_user.setPhone(fbphone);
					    	}
					    	fb_user.setPassword(password);
					    	fb_user.setUsername(sb.toString());
					    	fb_user.setSalt(initSalt().toString());
					    	fb_user.setStatus("enabled");
					    	fb_user.setUser_type("normal");
							
					    	this.userDao.save(fb_user);
							UserCentre uc = new UserCentre();
							uc.setCentre_id(centre_id);
							uc.setUser_id(fb_user.getId());
							userCentreDao.save(uc);
							user =fb_user;
							
							//--
							Configuration c = new Configuration();
							c.setNew_admin_push(true);
							c.setNew_comment_on_your_comment_push(true);
							c.setNew_comment_on_your_story_push(true);
							c.setNew_favorite_from_following_push(true);
							c.setNew_follower_push(true);
							c.setNew_story_from_following_push(true);
							c.setRecommended_my_story_push(true);
							c.setReposted_my_story_push(true);
							
							c.setUserId((Long) fb_user.getId());
							
							this.configurationDao.save(c);
							List<User> officialUser = userDao.getUserByUserType("official");
							if(officialUser != null && officialUser.size() > 0){
								for(User official:officialUser){
									Follow f = new Follow();
									FollowId fid = new FollowId();
									fid.setUser(fb_user);
									fid.setFollower(official);
									f.setPk(fid);
									f.setCreateTime(new Date());
									followDao.save(f);
								}
							}
							List<User> userList = this.userDao.getUserByUserType();
							List<PushNotification> pnList = new ArrayList<PushNotification>();
							Notification n = null;
							List<Notification> notificationList = new ArrayList<Notification>();
							Configuration conf;
							if ((userList != null) && (userList.size() > 0)) {
								for (User admin : userList) {
									n = new Notification();
									n.setRecipientId((Long) admin.getId());
									n.setSenderId((Long) fb_user.getId());
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
							String content = fb_user.getUsername() + "注册了";
							JSONObject json = new JSONObject();
							json.put("user_id",fb_user.getId());
							GetuiModel gm = getGetuiInfo();
							PushNotificationUtil.pushInfoAllFollow(gm.getAppId(), gm.getAppKey(), gm.getMasterSecret(), pnList, map, content,json.toString());
							//--
						}
						
					}
					
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

				if (user == null){
					jo.put("status", "您输入的账号不存在，请先注册");
					jo.put("code", 10605);
					jo.put("error_message", "您输入的账号不存在，请先注册");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}
			} catch (Exception e) {
				jo.put("status", "invalid_password");
				jo.put("code", Integer.valueOf(10007));
				jo.put("error_message", "invalid password");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}
		
		
			
			//---

			String raw = user.getId() + timestamp;
			String token = EncryptionUtil.hashMessage(raw);

			System.out.println("userId--->" + user.getId());
			String avatarImage = user.getAvatarImage();
			if(!Strings.isNullOrEmpty(avatarImage)){
				auth.put("avatar_image",JSONObject.fromObject(avatarImage));
			}
			auth.put("userid", user.getId());
			auth.put("access_token", token);
			auth.put("username", user.getUsername());
			auth.put("token_timestamp", Long.valueOf(Long.parseLong(timestamp)));
			auth.put("token", fbToken);
			auth.put("fbid", centre_id);
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
		String ip = request.getRemoteAddr();
		if (userId.equals(loginUserid)) {
			UserCentre uc = userCentreDao.getUserCentreByUserId(loginUserid);
			int centre_id = uc.getCentre_id();
			Map<String,String> param = new HashMap<String,String>();
			param.put("id",String.valueOf(centre_id));
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
		List<Follow> followList = this.followDao.getFollowingsByUserId(loginUserid);
		if ((followList != null) && (followList.size() > 0)) {
			for (int i = 0; i < followList.size(); i++) {
				followingId = followingId + "," + ((Follow) followList.get(i)).getPk().getFollower().getId();
			}
		}
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
			List<Likes> likesList = this.likesDao.getLikesPageByUserId(userId, count);
			if ((likesList != null) && (likesList.size() > 0))
				for (Likes like : likesList) {
					story = like.getLike_story();
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			List<Likes> likesList = this.likesDao.getLikesPageByUserId(userId, count);
			if ((likesList != null) && (likesList.size() > 0))
				for (Likes like : likesList) {
					story = like.getLike_story();
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Likes> likesList = this.likesDao.getLikesPageByUserId(userId, count, since_id, 1);
			if ((likesList != null) && (likesList.size() > 0))
				for (Likes like : likesList) {
					story = like.getLike_story();
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Likes> likesList = this.likesDao.getLikesPageByUserId(userId, count, since_id, 1);
			if ((likesList != null) && (likesList.size() > 0))
				for (Likes like : likesList) {
					story = like.getLike_story();
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Likes> likesList = this.likesDao.getLikesPageByUserId(userId, count, max_id, 2);
			if ((likesList != null) && (likesList.size() > 0))
				for (Likes like : likesList) {
					story = like.getLike_story();
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Likes> likesList = this.likesDao.getLikesPageByUserId(userId, count, max_id, 2);
			if ((likesList != null) && (likesList.size() > 0)) {
				for (Likes like : likesList) {
					story = like.getLike_story();
					storyModel = getStoryEventByStoryLoginUser(story, userId,loginUserid);
					storyModelList.add(storyModel);
				}
			}
		}

		return storyModelList;
	}

	public Response createFollow(Long userId, Long loginUserid,HttpServletRequest request) {
		log.debug("*** start create follow ***");
		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String appId = jsonObject.getString("appId");
		String appKey = jsonObject.getString("appKey");
		String masterSecret = jsonObject.getString("masterSecret");
		int count = 0;
		User user = (User) this.userDao.get(loginUserid);
		User follower = (User) this.userDao.get(userId);
		try {
			if (loginUserid.equals(userId)) {
				JSONObject json = new JSONObject();
				json.put("status", "invalid_request");
				json.put("code", Integer.valueOf(10010));
				json.put("error_message", "The user can't focus on yourself");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
			Follow followExist = this.followDao.getFollow(loginUserid, userId);

			System.out.println("count--->save---userid loginid");
			if (followExist != null) {
				count = this.followDao.userFollowedCount(userId);
				System.out.println("count--->" + count);
			} else {
				Follow follow = new Follow();

				log.debug("*** user follow ***" + user.getUsername());
				FollowId pk = new FollowId();
				pk.setUser(user);
				pk.setFollower(follower);
				follow.setPk(pk);
				follow.setCreateTime(new Date());
				Set<Follow> followers = new HashSet<Follow>();
				followers.add(follow);
				user.setFollowers(followers);
				this.userDao.saveOrUpdate(user);
				Notification notification = this.notificationDao.getNotificationByAction(userId, loginUserid, 3, 1);
				if (notification != null) {
					notification.setCreate_at(new Date());
					notification.setStatus("enabled");
					this.notificationDao.update(notification);
				} else {
					notification = new Notification();
					notification.setSenderId(loginUserid);
					notification.setRecipientId(userId);
					notification.setNotificationType(1);
					notification.setObjectType(3);
					notification.setObjectId(userId);
					notification.setRead_already(false);
					notification.setStatus("enabled");
					this.notificationDao.save(notification);
				}

				this.notificationDao.saveOrUpdate(notification);
				Configuration conf = this.configurationDao.getConfByUserId(userId);
				if (conf.isNew_follower_push()) {
					List<PushNotification> list = this.pushNotificationDao.getPushNotificationByUserid(userId);
					int counts = this.notificationDao.getNotificationByRecipientId(userId);
					String content = user.getUsername() + "关注了你";
					JSONObject json = new JSONObject();
					json.put("user_id",user.getId());
					PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content,json.toString());
				}

				count = this.followDao.userFollowedCount(userId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject json = new JSONObject();
			json.put("status", "invalid_request");
			json.put("code", Integer.valueOf(10010));
			json.put("error_message", "invalid payload paramters");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		log.debug("*** create follow success");
		JSONObject jo = new JSONObject();
		jo.put("followers_count", Integer.valueOf(count));

		return Response.status(Response.Status.CREATED).entity(jo).build();
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

	public Response deleteFollow(Long userId, Long loginUserid) {
		try {
			this.followDao.deleteFollow(loginUserid, userId);
			this.timelineDao.deleteTimelines(userId, loginUserid);
			notificationDao.disableNotification(3,userId);
		} catch (Exception e) {
			JSONObject json = new JSONObject();
			json.put("status", "invalid_request");
			json.put("code", Integer.valueOf(10010));
			json.put("error_message", "invalid payload paramters");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		int count = this.followDao.userFollowedCount(userId);
		JSONObject jo = new JSONObject();
		jo.put("followers_count", Integer.valueOf(count));
		return Response.status(Response.Status.OK).entity(jo).build();
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

	public List<UserIntro> getFollowingByUserId(Long user_id, HttpServletRequest request, Long loginUserid) {
		log.debug("***get user_id 关注的user info");
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<UserIntro> userIntroList = new ArrayList<UserIntro>();
		int count = 20;
		User user = null;
		Follow f = null;
		Follow followed = null;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Follow> followList = this.followDao.getFollowingsPageByUserId(user_id, count);
			if ((followList != null) && (followList.size() > 0))
				for (Follow follow : followList) {
					user = follow.getPk().getFollower();

					UserIntro userIntro = new UserIntro();
					userIntro.setId((Long) user.getId());
					userIntro.setIntroduction(user.getIntroduction());
					userIntro.setUsername(user.getUsername());
					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
						JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
						userIntro.setAvatar_image(avatarJson);
					} else {
						userIntro.setAvatar_image(null);
					}
					f = this.followDao.getFollow((Long) follow.getPk().getFollower().getId(), user_id);
					if (f != null)
						userIntro.setIs_following_current_user(true);
					else {
						userIntro.setIs_following_current_user(false);
					}
					followed = this.followDao.getFollow(loginUserid, (Long) follow.getPk().getFollower().getId());
					if (followed != null)
						userIntro.setFollowed_by_current_user(true);
					else {
						userIntro.setFollowed_by_current_user(false);
					}
					userIntro.setUser_type(user.getUser_type());
					userIntroList.add(userIntro);
				}
		} else 
			if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				List<Follow> followList = this.followDao.getFollowingsPageByUserId(user_id, count);
				if ((followList != null) && (followList.size() > 0))
					for (Follow follow : followList) {
						user = follow.getPk().getFollower();
						UserIntro userIntro = new UserIntro();
						userIntro.setId((Long) user.getId());
						userIntro.setIntroduction(user.getIntroduction());
						userIntro.setUsername(user.getUsername());
						userIntro.setUser_type(user.getUser_type());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
							userIntro.setAvatar_image(avatarJson);
						} else {
							userIntro.setAvatar_image(null);
						}
						if(loginUserid != null && loginUserid > 0){
							f = this.followDao.getFollow((Long) follow.getPk().getFollower().getId(), loginUserid);
							if (f != null)
								userIntro.setIs_following_current_user(true);
							else {
								userIntro.setIs_following_current_user(false);
							}
							followed = this.followDao.getFollow(loginUserid, (Long) follow.getPk().getFollower().getId());
							if (followed != null)
								userIntro.setFollowed_by_current_user(true);
							else {
								userIntro.setFollowed_by_current_user(false);
							}
						}else{
							userIntro.setIs_following_current_user(false);
							userIntro.setFollowed_by_current_user(false);
						}
						
						userIntroList.add(userIntro);
					}
			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				List<Follow> followList = this.followDao.getFollowingsPageByUserId(user_id, count, since_id, 1);
				if ((followList != null) && (followList.size() > 0))
					for (Follow follow : followList) {
						user = follow.getPk().getFollower();
						UserIntro userIntro = new UserIntro();
						userIntro.setId((Long) user.getId());
						userIntro.setIntroduction(user.getIntroduction());
						userIntro.setUsername(user.getUsername());
						userIntro.setUser_type(user.getUser_type());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
							userIntro.setAvatar_image(avatarJson);
						} else {
							userIntro.setAvatar_image(null);
						}
						if(loginUserid != null && loginUserid > 0){
							f = this.followDao.getFollow((Long) follow.getPk().getFollower().getId(), loginUserid);
							if (f != null)
								userIntro.setIs_following_current_user(true);
							else {
								userIntro.setIs_following_current_user(false);
							}
							followed = this.followDao.getFollow(loginUserid, (Long) follow.getPk().getFollower().getId());
							if (followed != null)
								userIntro.setFollowed_by_current_user(true);
							else {
								userIntro.setFollowed_by_current_user(false);
							}
						}else{
							userIntro.setIs_following_current_user(false);
							userIntro.setFollowed_by_current_user(false);
						}
						
						userIntroList.add(userIntro);
					}
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				List<Follow> followList = this.followDao.getFollowingsPageByUserId(user_id, count, since_id, 1);
				if ((followList != null) && (followList.size() > 0))
					for (Follow follow : followList) {
						user = follow.getPk().getFollower();
						UserIntro userIntro = new UserIntro();
						userIntro.setId((Long) user.getId());
						userIntro.setIntroduction(user.getIntroduction());
						userIntro.setUsername(user.getUsername());
						userIntro.setUser_type(user.getUser_type());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
							userIntro.setAvatar_image(avatarJson);
						} else {
							userIntro.setAvatar_image(null);
						}
						if(loginUserid != null && loginUserid > 0){
							Follow following = this.followDao.getFollow((Long) follow.getPk().getFollower().getId(),
									loginUserid);
							if (following != null)
								userIntro.setIs_following_current_user(true);
							else {
								userIntro.setIs_following_current_user(false);
							}
							followed = this.followDao.getFollow(loginUserid, (Long) follow.getPk().getFollower().getId());
							if (followed != null)
								userIntro.setFollowed_by_current_user(true);
							else {
								userIntro.setFollowed_by_current_user(false);
							}
						}else{
							userIntro.setIs_following_current_user(false);
							userIntro.setFollowed_by_current_user(false);
						}
						

						userIntroList.add(userIntro);
					}
			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<Follow> followList = this.followDao.getFollowingsPageByUserId(user_id, count, max_id, 2);
				if ((followList != null) && (followList.size() > 0))
					for (Follow follow : followList) {
						user = follow.getPk().getFollower();
						UserIntro userIntro = new UserIntro();
						userIntro.setId((Long) user.getId());
						userIntro.setIntroduction(user.getIntroduction());
						userIntro.setUsername(user.getUsername());
						userIntro.setUser_type(user.getUser_type());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
							userIntro.setAvatar_image(avatarJson);
						} else {
							userIntro.setAvatar_image(null);
						}
						
						if(loginUserid != null && loginUserid > 0){
							f = this.followDao.getFollow((Long) follow.getPk().getFollower().getId(), user_id);
							if (f != null)
								userIntro.setIs_following_current_user(true);
							else {
								userIntro.setIs_following_current_user(false);
							}
							followed = this.followDao.getFollow(loginUserid, (Long) follow.getPk().getFollower().getId());
							if (followed != null)
								userIntro.setFollowed_by_current_user(true);
							else {
								userIntro.setFollowed_by_current_user(false);
							}
						}else{
							userIntro.setIs_following_current_user(false);
							userIntro.setFollowed_by_current_user(false);
						}
						
						userIntroList.add(userIntro);
					}
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<Follow> followList = this.followDao.getFollowingsPageByUserId(user_id, count, max_id, 2);
				if ((followList != null) && (followList.size() > 0)) {
					for (Follow follow : followList) {
						user = follow.getPk().getFollower();
						UserIntro userIntro = new UserIntro();
						userIntro.setId((Long) user.getId());
						userIntro.setIntroduction(user.getIntroduction());
						userIntro.setUsername(user.getUsername());
						userIntro.setUser_type(user.getUser_type());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
							userIntro.setAvatar_image(avatarJson);
						} else {
							userIntro.setAvatar_image(null);
						}
						
						if(loginUserid != null && loginUserid > 0 ){
							f = this.followDao.getFollow((Long) follow.getPk().getFollower().getId(), user_id);
							if (f != null)
								userIntro.setIs_following_current_user(true);
							else {
								userIntro.setIs_following_current_user(false);
							}
							followed = this.followDao.getFollow(loginUserid, (Long) follow.getPk().getFollower().getId());
							if (followed != null)
								userIntro.setFollowed_by_current_user(true);
							else {
								userIntro.setFollowed_by_current_user(false);
							}
						}else{
							userIntro.setIs_following_current_user(false);
							userIntro.setFollowed_by_current_user(false);
						}
						
						userIntroList.add(userIntro);
					}
				}
			}
		
		log.debug("%%% userIntro %%%" + JSONArray.fromObject(userIntroList));
		return userIntroList;
	}

	public List<UserIntro> getFollowersByUserId(Long user_id, HttpServletRequest request, Long loginUserid) {
		log.debug("**** user_id 的粉�? ****");

		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<UserIntro> userIntroList = new ArrayList<UserIntro>();
		int count = 20;
		User user = null;
		Follow f = null;
		Follow following = null;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Follow> followList = this.followDao.getFollowersPageByUserId(user_id, count);
			if ((followList != null) && (followList.size() > 0))
				for (Follow follow : followList) {
					user = follow.getPk().getUser();
					UserIntro userIntro = new UserIntro();
					userIntro.setId((Long) user.getId());
					userIntro.setIntroduction(user.getIntroduction());
					userIntro.setUsername(user.getUsername());
					userIntro.setUser_type(user.getUser_type());
					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
						JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
						userIntro.setAvatar_image(avatarJson);
					} else {
						userIntro.setAvatar_image(null);
					}
					if(loginUserid != null && loginUserid > 0){
						f = this.followDao.getFollow(loginUserid, (Long) follow.getPk().getUser().getId());
						if (f != null)
							userIntro.setFollowed_by_current_user(true);
						else {
							userIntro.setFollowed_by_current_user(false);
						}
						following = this.followDao.getFollow((Long) follow.getPk().getUser().getId(), loginUserid);
						if (following != null)
							userIntro.setIs_following_current_user(true);
						else {
							userIntro.setIs_following_current_user(false);
						}
					}else{
						userIntro.setFollowed_by_current_user(false);
						userIntro.setIs_following_current_user(false);
					}
					
					userIntroList.add(userIntro);
				}
		} else 
			if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				List<Follow> followList = this.followDao.getFollowersPageByUserId(user_id, count);
				if ((followList != null) && (followList.size() > 0))
					for (Follow follow : followList) {
						user = follow.getPk().getUser();
						UserIntro userIntro = new UserIntro();
						userIntro.setId((Long) user.getId());
						userIntro.setIntroduction(user.getIntroduction());
						userIntro.setUsername(user.getUsername());
						userIntro.setUser_type(user.getUser_type());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
							userIntro.setAvatar_image(avatarJson);
						} else {
							userIntro.setAvatar_image(null);
						}
						if(loginUserid != null && loginUserid > 0){
							f = this.followDao.getFollow(loginUserid, follow.getPk().getUser().getId());
							if (f != null)
								userIntro.setFollowed_by_current_user(true);
							else {
								userIntro.setFollowed_by_current_user(false);
							}

							following = this.followDao.getFollow((Long) follow.getPk().getUser().getId(), loginUserid);
							if (following != null)
								userIntro.setIs_following_current_user(true);
							else {
								userIntro.setIs_following_current_user(false);
							}
						}else{
							userIntro.setFollowed_by_current_user(false);
							userIntro.setIs_following_current_user(false);
						}
						userIntroList.add(userIntro);
					}
			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				List<Follow> followList = this.followDao.getFollowersPageByUserId(user_id, count, since_id, 1);
				if ((followList != null) && (followList.size() > 0))
					for (Follow follow : followList) {
						user = follow.getPk().getUser();
						UserIntro userIntro = new UserIntro();
						userIntro.setId((Long) user.getId());
						userIntro.setIntroduction(user.getIntroduction());
						userIntro.setUsername(user.getUsername());
						userIntro.setUser_type(user.getUser_type());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
							userIntro.setAvatar_image(avatarJson);
						} else {
							userIntro.setAvatar_image(null);
						}
						if(loginUserid != null && loginUserid > 0){
							f = this.followDao.getFollow(loginUserid, (Long) follow.getPk().getUser().getId());
							if (f != null)
								userIntro.setFollowed_by_current_user(true);
							else {
								userIntro.setFollowed_by_current_user(false);
							}
							following = this.followDao.getFollow((Long) follow.getPk().getUser().getId(), loginUserid);
							if (following != null)
								userIntro.setIs_following_current_user(true);
							else {
								userIntro.setIs_following_current_user(false);
							}
						}else{
							userIntro.setFollowed_by_current_user(false);
							userIntro.setIs_following_current_user(false);
						}
						
						userIntroList.add(userIntro);
					}
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				List<Follow> followList = this.followDao.getFollowersPageByUserId(user_id, count, since_id, 1);
				if ((followList != null) && (followList.size() > 0))
					for (Follow follow : followList) {
						user = follow.getPk().getUser();
						UserIntro userIntro = new UserIntro();
						userIntro.setId((Long) user.getId());
						userIntro.setIntroduction(user.getIntroduction());
						userIntro.setUsername(user.getUsername());
						userIntro.setUser_type(user.getUser_type());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
							userIntro.setAvatar_image(avatarJson);
						} else {
							userIntro.setAvatar_image(null);
						}
						if(loginUserid != null && loginUserid > 0){
							f = this.followDao.getFollow(loginUserid, (Long) follow.getPk().getUser().getId());
							if (f != null)
								userIntro.setFollowed_by_current_user(true);
							else {
								userIntro.setFollowed_by_current_user(false);
							}
							following = this.followDao.getFollow((Long) follow.getPk().getUser().getId(), loginUserid);
							if (following != null)
								userIntro.setIs_following_current_user(true);
							else {
								userIntro.setIs_following_current_user(false);
							}
						}else{
							userIntro.setFollowed_by_current_user(false);
							userIntro.setIs_following_current_user(false);
						}
						
						userIntroList.add(userIntro);
					}
			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<Follow> followList = this.followDao.getFollowersPageByUserId(user_id, count, max_id, 2);
				if ((followList != null) && (followList.size() > 0))
					for (Follow follow : followList) {
						user = follow.getPk().getUser();
						UserIntro userIntro = new UserIntro();
						userIntro.setId((Long) user.getId());
						userIntro.setIntroduction(user.getIntroduction());
						userIntro.setUsername(user.getUsername());
						userIntro.setUser_type(user.getUser_type());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
							userIntro.setAvatar_image(avatarJson);
						} else {
							userIntro.setAvatar_image(null);
						}
						if(loginUserid != null && loginUserid > 0){
							f = this.followDao.getFollow(loginUserid, (Long) follow.getPk().getUser().getId());
							if (f != null)
								userIntro.setFollowed_by_current_user(true);
							else {
								userIntro.setFollowed_by_current_user(false);
							}
							following = this.followDao.getFollow((Long) follow.getPk().getUser().getId(), loginUserid);
							if (following != null)
								userIntro.setIs_following_current_user(true);
							else {
								userIntro.setIs_following_current_user(false);
							}
						}else{
							userIntro.setFollowed_by_current_user(false);
							userIntro.setIs_following_current_user(false);
						}
						
						userIntroList.add(userIntro);
					}
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<Follow> followList = this.followDao.getFollowersPageByUserId(user_id, count, max_id, 2);
				if ((followList != null) && (followList.size() > 0)) {
					for (Follow follow : followList) {
						user = follow.getPk().getUser();
						UserIntro userIntro = new UserIntro();
						userIntro.setId((Long) user.getId());
						userIntro.setIntroduction(user.getIntroduction());
						userIntro.setUsername(user.getUsername());
						userIntro.setUser_type(user.getUser_type());
						if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
							JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
							userIntro.setAvatar_image(avatarJson);
						} else {
							userIntro.setAvatar_image(null);
						}
						if(loginUserid != null && loginUserid > 0){
							f = this.followDao.getFollow(loginUserid, follow.getPk().getUser().getId());
							if (f != null)
								userIntro.setFollowed_by_current_user(true);
							else {
								userIntro.setFollowed_by_current_user(false);
							}
							following = this.followDao.getFollow((Long) follow.getPk().getUser().getId(), loginUserid);
							if (following != null)
								userIntro.setIs_following_current_user(true);
							else {
								userIntro.setIs_following_current_user(false);
							}
						}else{
							userIntro.setFollowed_by_current_user(false);
							userIntro.setIs_following_current_user(false);
						}
						
						userIntroList.add(userIntro);
					}
				}
			}
		

		log.debug("*** userIntroList ***" + JSONArray.fromObject(userIntroList));
		return userIntroList;
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
		int likesCount = this.likesDao.userLikesCount((Long) story.getUser().getId());
		int repostStoryCount = this.republishDao.userRepostCount((Long) story.getUser().getId());
		User user = story.getUser();

		Follow loginUserFollowAuthor = this.followDao.getFollow(loginUserid, (Long) story.getUser().getId());
		Follow AuthorFollowLoginUser = this.followDao.getFollow((Long) story.getUser().getId(), loginUserid);

		boolean followed_by_current_user = false;
		boolean is_following_current_user = false;
		if (loginUserFollowAuthor != null) {
			followed_by_current_user = true;
		}

		if (AuthorFollowLoginUser != null) {
			is_following_current_user = true;
		}
		JSONObject avatarImageJson = null;
		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
		}

		JSONObject coverImageJson = null;
		if (!Strings.isNullOrEmpty(user.getCoverImage())) {
			coverImageJson = JSONObject.fromObject(user.getCoverImage());
		}
		int storyCount = this.storyDao.getStoryCount((Long) user.getId());
		int follower_Count = this.followDao.userFollowedCount((Long) user.getId());
		int following_count = this.followDao.userFollowCount((Long) user.getId());
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
				ci.setId((Long) collection.getId());
				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
				ci.setInfo(collection.getInfo());
				User author = collection.getUser();
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
				ci.setId((Long) collection.getId());
				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
				ci.setInfo(collection.getInfo());
				User author = collection.getUser();
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
					int likesCount = this.likesDao.userLikesCount((Long) story.getUser().getId());
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
					int follower_Count = this.followDao.userFollowedCount((Long) user.getId());
					int following_count = this.followDao.userFollowCount((Long) user.getId());

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

					if (timeline.getCreatorId().equals(timeline.getTargetUserId())) {
						authorJson.put("followed_by_current_user", Boolean.valueOf(false));
						authorJson.put("is_following_current_user", Boolean.valueOf(false));
					} else {
						authorJson.put("followed_by_current_user", Boolean.valueOf(true));
						Follow follow = this.followDao.getFollow(timeline.getCreatorId(), timeline.getTargetUserId());
						if (follow != null)
							authorJson.put("is_following_current_user", Boolean.valueOf(true));
						else {
							authorJson.put("is_following_current_user", Boolean.valueOf(false));
						}
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
					int likesCount = this.likesDao.userLikesCount((Long) story.getUser().getId());
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
					int follower_Count = this.followDao.userFollowedCount((Long) user.getId());
					int following_count = this.followDao.userFollowCount((Long) user.getId());

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

					if (timeline.getCreatorId().equals(timeline.getTargetUserId())) {
						authorJson.put("followed_by_current_user", Boolean.valueOf(false));
						authorJson.put("is_following_current_user", Boolean.valueOf(false));
					} else {
						authorJson.put("followed_by_current_user", Boolean.valueOf(true));
						Follow follow = this.followDao.getFollow(timeline.getCreatorId(), timeline.getTargetUserId());
						if (follow != null)
							authorJson.put("is_following_current_user", Boolean.valueOf(true));
						else {
							authorJson.put("is_following_current_user", Boolean.valueOf(false));
						}
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
					Set<Columns> cSet = story.getColumns();
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
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setInfo(collection.getInfo());
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
					ci.setId((Long) collection.getId());
					ci.setCollection_name(collection.getCollectionName());
					ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
					ci.setInfo(collection.getInfo());
					User author = collection.getUser();
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

	public Response loginLinkAccounts(LinkAccounts la,HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		String device = request.getHeader("X-Tella-Request-Device");
		JSONObject resp = new JSONObject();
		Object[] link = null;
		if (la != null) {
			String uuid = la.getUuid();
			String service = la.getService();
			link = this.linkAccountsDao.getLinkAccountsByUUIDAndService(uuid,service);
			
			if (link != null) {
				
				String url = getClass().getResource("/../../META-INF/user_centre.json").getPath();
				JSONObject jsonObject = parseJson(url);
				String urlKey = jsonObject.getString("url");
				
				User user = (User)link[1];
				LinkAccounts linkAccount = (LinkAccounts)link[0];
				linkAccount.setUnion_id(la.getUnion_id());
				if(!la.getAuth_token().equals(linkAccount.getAuth_token()) 
						&& la.getAuth_token() !=linkAccount.getAuth_token()){
					linkAccount.setAuth_token(la.getAuth_token());
				}
				linkAccountsDao.update(linkAccount);
				Map<String,String> param = new HashMap<String, String>();
				
				
				param.put("device", device);
				param.put("ip", ip);
				String thried = la.getService();
				if(!Strings.isNullOrEmpty(user.getPhone())){
					param.put("mobile", user.getPhone());
				}
				
				if(thried.equals("wechat")){
					param.put("third_party_channel", "10");
					param.put("third_party_unionid", la.getUnion_id().toString());
				}else if(thried.equals("weibo")){
					param.put("third_party_channel", "20");
					param.put("third_party_unionid", la.getUuid());
				}
				String params = null;
				try {
					params = publicParam(param);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String result = HttpUtil.sendPostStr(urlKey+"/customer/account/login-third-party-beegree", params);
				if (!Strings.isNullOrEmpty(result)) {
					JSONObject json = JSONObject.fromObject(result);
					int status = json.getInt("code");
					if (status == 10000) {
						JSONObject data = json.getJSONObject("data");
						int centre_id = data.getInt("userid");
						String fb_token = data.getString("token");
						UserCentre uc = new UserCentre();
						uc.setCentre_id(centre_id);
						uc.setUser_id(user.getId());
						userCentreDao.save(uc);
						String raw = user.getId() + user.getCreated_time().toString();
						String token = EncryptionUtil.hashMessage(raw);

						System.out.println("userId--->" + user.getId());
						resp.put("userid", user.getId());
						resp.put("access_token", token);
						resp.put("token_timestamp", user.getCreated_time());
						resp.put("fbid", centre_id);
						resp.put("token", fb_token);
						System.out.println(resp.toString());
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
			resp.put("status", "no_user");
			resp.put("code", Integer.valueOf(10080));
			resp.put("error_message", "Without this user.");

			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}

		resp.put("status", "invalid request");
		resp.put("code", Integer.valueOf(10010));
		resp.put("error_message", "Invalid request payload");
		return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
	}

	public Response bindingUser(LinkAccounts la, Long loginUserid) {
		JSONObject json = new JSONObject();
		if (la != null) {
			String uuid = la.getUuid();
			Object[] link = this.linkAccountsDao.getLinkAccountsByUUID(uuid);
			if (link != null) {
				json.put("status", "invalid_link_account");
				json.put("code", Integer.valueOf(10081));
				json.put("error_message", "Link account already exist.");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
			LinkAccounts linkAccounts = new LinkAccounts();
			linkAccounts.setAuth_token(la.getAuth_token());
			linkAccounts.setAvatar_url(la.getAvatar_url());
			linkAccounts.setDescription(la.getDescription());
			linkAccounts.setRefreshed_at(la.getRefreshed_at());
			linkAccounts.setService(la.getService());
			linkAccounts.setUser_id(loginUserid);
			linkAccounts.setUuid(la.getUuid());
			this.linkAccountsDao.save(linkAccounts);
			json.put("id", linkAccounts.getId());
			json.put("service", linkAccounts.getService());
			return Response.status(Response.Status.OK).entity(json).build();
		}

		json.put("status", "invalid_parameter");
		json.put("code", Integer.valueOf(10058));
		json.put("error_message", "Invalid parameter");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}

	public Response unbindingUser(Long linkId,Long loginUserid) {
		JSONObject json = new JSONObject();
		User user = userDao.get(loginUserid);
		List<LinkAccounts> laList = linkAccountsDao.getLinkAccountsByUserid(loginUserid);
		LinkAccounts la = linkAccountsDao.get(linkId);
		if(!Strings.isNullOrEmpty(user.getPhone())){
			if(la != null){
				this.linkAccountsDao.delete(linkId);
			}
			json.put("status", "success");
			return Response.status(Response.Status.OK).entity(json).build();
		}else{
			if(laList != null && laList.size() > 1){
				if(la != null){
					this.linkAccountsDao.delete(linkId);
				}
				json.put("status", "success");
				return Response.status(Response.Status.OK).entity(json).build();
			}else{
				json.put("status", "must one bind");
				json.put("code",10109);
				json.put("error_message", "at least one contact method information is required");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		}
		
	}

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
			Likes like = likesDao.getLikeByUserIdAndStoryId(loginUserid,story.getId());
			if(like != null){
				storyModel.setLiked_by_current_user(true);
			}else{
				storyModel.setLiked_by_current_user(false);
			}
			
		}
		
		Set<Collection> collections = story.getCollections();
		if(collections != null && collections.size() > 0){
			Collection collection = collections.iterator().next();
			if (collection != null) {
				CollectionIntro ci = new CollectionIntro();
				ci.setId((Long) collection.getId());
				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
				ci.setInfo(collection.getInfo());
				User author = collection.getUser();//userDao.get(collection.getAuthorId());
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
			Follow follow = null;
			if (userArr != null) {
				Object[] userids = userArr.toArray();
				for (Object oid : userids) {
					Long id = (Long) oid;
					User follower = (User) this.userDao.get(id);
					follow = new Follow();

					log.debug("*** user follow ***" + user.getUsername());
					FollowId pk = new FollowId();
					pk.setUser(user);
					pk.setFollower(follower);
					follow.setPk(pk);
					follow.setCreateTime(new Date());
					Set<Follow> followers = new HashSet<Follow>();
					followers.add(follow);
					user.setFollowers(followers);
					this.userDao.saveOrUpdate(user);
					Notification notification = this.notificationDao.getNotificationByAction(id, loginUserid, 3, 1);
					if (notification != null) {
						notification.setCreate_at(new Date());
						this.notificationDao.update(notification);
					} else {
						notification = new Notification();
						notification.setSenderId(loginUserid);
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
		List<Follow> followList = this.followDao.getFollowingsByUserId(loginUserid);
		if ((followList != null) && (followList.size() > 0)) {
			for (int i = 0; i < followList.size(); i++) {
				followingId = followingId + "," + ((Follow) followList.get(i)).getPk().getFollower().getId();
			}
		}
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
		String ip = request.getRemoteAddr();
		
		if ((!Strings.isNullOrEmpty(phone)) && (!Strings.isNullOrEmpty(zone)) && (!Strings.isNullOrEmpty(code))
				&& (!Strings.isNullOrEmpty(password))) {
			User user = this.userDao.getUserByZoneAndPhone(zone, phone);
			if (user != null) {
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
								user.setPassword(Base64Utils.encodeToString(password.getBytes()));
								this.userDao.update(user);
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
									String raw = user.getId() + timestamp;
									String token = EncryptionUtil.hashMessage(raw);
									JSONObject auth = new JSONObject();
									System.out.println("userId--->" + user.getId());
									auth.put("userid", user.getId());
									auth.put("access_token", token);
									auth.put("token_timestamp", Long.valueOf(Long.parseLong(timestamp)));
									auth.put("token", fbToken);
									auth.put("fbid", centre_id);
									System.out.println(auth.toString());
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
			} else {
				JSONObject j = new JSONObject();
				j.put("status", "验证失败");
				j.put("code", Integer.valueOf(10103));
				j.put("error_message", "服务器内部错误");
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
					int follower_Count = this.followDao.userFollowedCount(userId);
					int following_count = this.followDao.userFollowCount(userId);
					Follow loginFollow = this.followDao.getFollow(loginUserid, userId);
					boolean followed_by_current_user = false;
					boolean is_following_current_user = false;
					if (loginFollow != null) {
						followed_by_current_user = true;
					}
					Follow currentFollow = this.followDao.getFollow(userId, loginUserid);
					if (currentFollow != null) {
						is_following_current_user = true;
					}
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
								coverImageJson,  repostCount, storyCount, follower_Count, following_count,
								website, followed_by_current_user, is_following_current_user,
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
								coverImageJson,  repostCount, storyCount, follower_Count, following_count,
								website, followed_by_current_user, is_following_current_user, 
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
	public Response profile(Long userId, Long loginUserid,String appVersion) {
		UserParentModel upm = get(userId, loginUserid,appVersion);
		User loginUser = null;
		if(loginUserid != null && loginUserid > 0){
			loginUser = userDao.get(loginUserid);
		}
		List<Story> storyList = storyDao.getStoryByFour(userId);
		List<JSONObject> story = new ArrayList<JSONObject>();
		if(storyList != null && storyList.size() > 0){
			for(Story s:storyList){
				JSONObject storyJson = getStoryJson(s,loginUserid,loginUser);
				story.add(storyJson);
			}
		}
		List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost");
		EventModel eventModel = null;
		List<EventModel> eventList = new ArrayList<EventModel>();
		if ((timelineList != null) && (timelineList.size() > 0)){
			for (Timeline t : timelineList) {
				eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
				eventList.add(eventModel);
			}
		}
		ProfileModel profile = new ProfileModel();
		profile.setUser(upm);
		profile.setStory(story);
		profile.setEvent(eventList);
		System.out.println("profile-->"+JSONObject.fromObject(profile));
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
				ci.setId((Long) collection.getId());
				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
				ci.setInfo(collection.getInfo());
				User author = collection.getUser();//userDao.get(collection.getAuthorId());
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

	@Override
	public JSONObject getTimelinesBySlides(Long loginUserid, HttpServletRequest request,HttpServletResponse response,String appVersion) {
		
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
		long start = System.currentTimeMillis();
		String path1 = getClass().getResource("/../../META-INF/image.json").getPath();
		JSONObject json = ParseFile.parseJson(path1);
		String url = json.getString("url");
		String start_time = json.getString("start_time");
		String end_time = json.getString("end_time");
		EventModel em = null;
		if(loginUserid == null || loginUserid == 0l){
			log.debug("*** no login start time ***"+0);
			SlideModel sm = null;
			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				timelineList = timelineDao.getTimelineByRecommandation(count);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModel(tl);
						emList.add(em);
					}
				}
				slideList = slideDao.getSlideList();
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
				timelineList = timelineDao.getTimelineByRecommandation(count);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModel(tl);
						emList.add(em);
					}
				}
				slideList = slideDao.getSlideList();
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
				timelineList = timelineDao.getTimelineByRecommandation(since_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModel(tl);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				timelineList = timelineDao.getTimelineByRecommandation(since_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModel(tl);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineByRecommandation(max_id,count,2);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModel(tl);
						emList.add(em);
					}
				}
				
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineByRecommandation(max_id,count,2);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModel(tl);
						emList.add(em);
					}
				}
				
				homepage.put("events",emList);
			}
			long end = System.currentTimeMillis();
			System.out.println("no login time----->>>"+(end-start));
			log.debug("no login time----->>>"+(end-start));
		}else{
			long start1 = System.currentTimeMillis();
			User loginUser = userDao.get(loginUserid);
			/*String followingId = loginUserid + "";
			List<Follow> followList = this.followDao.getFollowingsByUserId(loginUserid);
			if ((followList != null) && (followList.size() > 0)) {
				for (int i = 0; i < followList.size(); i++) {
					followingId = followingId + "," +  followList.get(i).getPk().getFollower().getId();
				}
			}*/
			SlideModel sm = null;
			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				
				timelineList = timelineDao.getTimelineByHome(count);
				
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				slideList = slideDao.getSlideList();
				
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
				long startTime = System.currentTimeMillis();
				count = Integer.parseInt(countStr);
				timelineList = timelineDao.getTimelineByHome(count);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
						long endTime = System.currentTimeMillis();
						System.out.println("耗时--�?"+(endTime - startTime));
						emList.add(em);
					}
				}
				slideList = slideDao.getSlideList();
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
				timelineList = timelineDao.getTimelineByHome(since_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				timelineList = timelineDao.getTimelineByHome(since_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineByHome(max_id,count,2);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineByHome(max_id,count,2);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				
				homepage.put("events",emList);
			}
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date start_date = sdf.parse(start_time);
				Date end_date = sdf.parse(end_time);
				int start_flag = date.compareTo(start_date);
				int end_flag = date.compareTo(end_date);
				if(start_flag > 0 && end_flag < 0){
					homepage.put("festivatl_image",url);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long end1 = System.currentTimeMillis();
			System.out.println("login user need time----->>>"+(end1-start1));
			log.debug("login user need time----->>>"+(end1-start1));
		}

		FeatureCollection fc = featureCollectionDao.getFeatureCollectionByOne();
		if(fc != null){
			Collection collection = collectionDao.getCollectionById(fc.getCollectionId());
			if(collection != null){
				CollectionIntro ci = new CollectionIntro();
				ci.setId((Long) collection.getId());
				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
				ci.setInfo(collection.getInfo());
				User author = collection.getUser();//userDao.get(collection.getAuthorId());
				JSONObject ajson = new JSONObject();
				ajson.put("id",author.getId());
				ajson.put("username",author.getUsername());
				if(!Strings.isNullOrEmpty(author.getAvatarImage())){
					ajson.put("avatar_image", JSONObject.fromObject(author.getAvatarImage()));
				}
				
				ci.setAuthor(ajson);
				homepage.put("recomandation_collection", ci);
			}
		}
	
	
		
		
		return homepage;
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
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
						ci.setInfo(collection.getInfo());
						User author = collection.getUser(); //userDao.get(collection.getAuthorId());
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
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
						ci.setInfo(collection.getInfo());
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
					Set<Columns> colSet = story.getColumns();
					if(colSet != null && colSet.size() > 0){
						Iterator<Columns> iter = colSet.iterator();
						Columns c = iter.next();
						JSONObject columnsJson = new JSONObject();
						columnsJson.put("id",c.getId());
						columnsJson.put("column_name",c.getColumn_name());
						storyModel.setColumns(columnsJson);
					}else{
						delArray.add("columns");
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
					Set<Columns> colSet = story.getColumns();
					if(colSet != null && colSet.size() > 0){
						Iterator<Columns> iter = colSet.iterator();
						Columns c = iter.next();
						JSONObject columnsJson = new JSONObject();
						columnsJson.put("id",c.getId());
						columnsJson.put("column_name",c.getColumn_name());
						storyModel.setColumns(columnsJson);
					}else{
						delArray.add("columns");
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
					
					Set<Columns> colSet = story.getColumns();
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
					Set<Columns> colSet = story.getColumns();
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
		SlideModel sm = new SlideModel();
		sm.setId(slide.getId());
		String type = slide.getType();
		Long id = slide.getReference_id();
		JSONObject slideJson = new JSONObject();
		if(type.equals("story")){
			Story story = storyDao.get(id);
			JSONObject json = getSlideStoryByStory(story);
			slideJson.put("story", json);
		}else if(type.equals("user")){
			User user = userDao.get(id);
			JSONObject json = new JSONObject();
			json.put("id",user.getId());
			json.put("username", user.getUsername());
			json.put("user_type",user.getUser_type());
			JSONObject avatarImageJson = null;
			if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
				avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
			}
			if (avatarImageJson != null) {
				json.put("avatar_image", avatarImageJson);
			}
			slideJson.put("user",json);
		}else if(type.equals("collection")){
			Collection collection = collectionDao.get(id);
			
			CollectionIntro ci = new CollectionIntro();
 	         ci.setId((Long)collection.getId());
 	         ci.setCollection_name(collection.getCollectionName());
 	         ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
 	         ci.setInfo(collection.getInfo());
			User u = collection.getUser();//.getId()userDao.get(collection.getAuthorId());
			JSONObject author = new JSONObject();
			author.put("id", u.getId());
			author.put("username", u.getUsername());
			if(!Strings.isNullOrEmpty(u.getAvatarImage())){
				author.put("avatar_image",JSONObject.fromObject(u.getAvatarImage()));
			}
			
			ci.setAuthor(author);
			JsonConfig configs = new JsonConfig();
			List<String> delArray = new ArrayList<String>();
			
			
			JSONObject collectionJ = null;
			if ((delArray != null) && (delArray.size() > 0)) {
				configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
				configs.setIgnoreDefaultExcludes(false);
				configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

				collectionJ = JSONObject.fromObject(ci, configs);
			} else {
				collectionJ = JSONObject.fromObject(ci);
			}
			
			
			slideJson.put("collection", collectionJ);
		}else if(type.equals("url")){
			slideJson.put("url",slide.getUrl());
		}
		sm.setSlide(slideJson);
		sm.setType(slide.getType());
		JSONObject slide_image = JSONObject.fromObject(slide.getSlide_image());
		sm.setSlide_image(slide_image);
		return sm;
	}

	@Override
	public Response linkPhone(HttpServletRequest request,Long loginUserid,String appVersion) {
		String ip = request.getRemoteAddr();
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
					UserCentre uc = new UserCentre();
					uc.setCentre_id(centre_id);
					uc.setUser_id(u.getId());
					userCentreDao.save(uc);
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
		if(loginUserid != null && loginUserid != 0){
			User u = userDao.get(loginUserid);
			List<LinkAccounts> laList = linkAccountsDao.getLinkAccountsByUserid(loginUserid);
			if(laList != null && laList.size() > 0){
				if(u != null){
					u.setPhone("");
					u.setZone("");
					userDao.update(u);
					JSONObject json = new JSONObject();
					json.put("status", "success");
					return Response.status(Response.Status.OK).entity(json).build();
				}else{
					JSONObject json = new JSONObject();
					json.put("status", "no_resource");
					json.put("code",10011);
					json.put("error_message", "The user does not exist");
					return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
				}
			}else{
				JSONObject json = new JSONObject();
				json.put("status", "must one bind");
				json.put("code",10109);
				json.put("error_message", "must one bind");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
			
		}else{
			JSONObject json = new JSONObject();
			json.put("status", "no_resource");
			json.put("code",10011);
			json.put("error_message", "The user does not exist");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
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
		Set<Columns> colSet = story.getColumns();
		if(colSet != null && colSet.size() > 0){
			Iterator<Columns> iter = colSet.iterator();
			Columns c = iter.next();
			JSONObject columnsJson = new JSONObject();
			columnsJson.put("id",c.getId());
			columnsJson.put("column_name",c.getColumn_name());
			storyModel.setColumns(columnsJson);
		}else{
			delArray.add("columns");
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
	public JSONObject getStoryByCollection(Long loginUserid, HttpServletRequest request) {


		log.debug("*** Get Home Timeline of the Authenticated User ***");
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<Slide> slideList = null;
		List<SlideModel> smList = new ArrayList<SlideModel>();
		JSONObject homepage = new JSONObject();
		List<JSONObject> spmList = new ArrayList<JSONObject>();
		int count = 20;

		List<Collection> cList = userCollectionDao.getCollectionByUserid(loginUserid);
		String follow_collectionids = "";
		if(cList != null && cList.size() > 0){
			for(Collection c:cList){
				follow_collectionids += c.getId()+",";
			}
			
		}
		SlideModel sm = null;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			if(!Strings.isNullOrEmpty(follow_collectionids)){
				follow_collectionids = follow_collectionids.substring(0,follow_collectionids.length() - 1);
				List<Story> sList = collectionStoryDao.getStoryByCollectionIds(follow_collectionids, count);
				if(sList != null && sList.size() > 0){
					for(Story story:sList){
						JSONObject spm = getStoryEventByStory(story);
						spmList.add(spm);
					}
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
			homepage.put("stories",spmList);
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			if(!Strings.isNullOrEmpty(follow_collectionids)){
				follow_collectionids = follow_collectionids.substring(0,follow_collectionids.length() - 1);
				List<Story> sList = collectionStoryDao.getStoryByCollectionIds(follow_collectionids, count);
				if(sList != null && sList.size() > 0){
					for(Story story:sList){
						JSONObject spm = getStoryEventByStory(story);
						spmList.add(spm);
					}
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
			homepage.put("stories",spmList);
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			if(!Strings.isNullOrEmpty(follow_collectionids)){
				follow_collectionids = follow_collectionids.substring(0,follow_collectionids.length() - 1);
				List<Story> sList = collectionStoryDao.getStoryByCollectionIds(follow_collectionids, count,since_id,1);
				if(sList != null && sList.size() > 0){
					for(Story story:sList){
						JSONObject spm = getStoryEventByStory(story);
						spmList.add(spm);
					}
				}
				
			}
			homepage.put("stories",spmList);
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			if(!Strings.isNullOrEmpty(follow_collectionids)){
				follow_collectionids = follow_collectionids.substring(0,follow_collectionids.length() - 1);
				List<Story> sList = collectionStoryDao.getStoryByCollectionIds(follow_collectionids, count,since_id,1);
				if(sList != null && sList.size() > 0){
					for(Story story:sList){
						JSONObject spm = getStoryEventByStory(story);
						spmList.add(spm);
					}
				}
				
			}
			homepage.put("stories",spmList);
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			if(!Strings.isNullOrEmpty(follow_collectionids)){
				follow_collectionids = follow_collectionids.substring(0,follow_collectionids.length() - 1);
				List<Story> sList = collectionStoryDao.getStoryByCollectionIds(follow_collectionids, count,max_id,2);
				if(sList != null && sList.size() > 0){
					for(Story story:sList){
						JSONObject spm = getStoryEventByStory(story);
						spmList.add(spm);
					}
				}
				
			}
			homepage.put("stories",spmList);
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			if(!Strings.isNullOrEmpty(follow_collectionids)){
				follow_collectionids = follow_collectionids.substring(0,follow_collectionids.length() - 1);
				List<Story> sList = collectionStoryDao.getStoryByCollectionIds(follow_collectionids, count,max_id,2);
				if(sList != null && sList.size() > 0){
					for(Story story:sList){
						JSONObject spm = getStoryEventByStory(story);
						spmList.add(spm);
					}
				}
				
			}
			homepage.put("stories",spmList);
		}
	
		return homepage;
	
	
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
						
						Follow follow = followDao.getFollow(loginUserid, u.getId());
						if(follow != null){
							up.setFollowed_by_current_user(true);
						}else{
							up.setFollowed_by_current_user(false);
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
					Follow loginFollow = this.followDao.getFollow(loginUserid, user.getId());
					boolean followed_by_current_user = false;
					uf.setUsername(user.getUsername());
					uf.setIntroduction(user.getIntroduction());
					uf.setId(user.getId());
					if (loginFollow != null) {
						followed_by_current_user = true;
					}
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
	public Response profile_stories(Long userId,Long loginUserid,HttpServletRequest request,String appVersion) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		long start1 = System.currentTimeMillis();
		long start = System.currentTimeMillis();
		UserParentModel upm = get(userId, loginUserid,appVersion);
		long end = System.currentTimeMillis();
		System.out.println("---get user-->>>>>>>>>>>>>>>>>>>>>>>>>>>"+(end-start));
		User loginUser = null;
		if(loginUserid != null && loginUserid > 0){
			loginUser = userDao.get(loginUserid);
		}
		
		int count = 20;
		ProfileVo profile = new ProfileVo();
		List<Timeline> timelineList = null;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			if(userId.equals(loginUserid)){
				timelineList = timelineDao.getTimelineByUserIdAndType(userId,"post",count);
			}else{
				timelineList = timelineDao.getTimelineByUserIdAndTypeAndStatus(userId, "post", count, "publish");
			}
			
			EventModel eventModel = null;
			List<EventModel> eventList = new ArrayList<EventModel>();
			if ((timelineList != null) && (timelineList.size() > 0)){
				for (Timeline t : timelineList) {
					eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
					eventList.add(eventModel);
				}
			}
			
			profile.setUser(upm);
			profile.setEvent(eventList);
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			if(userId.equals(loginUserid)){
				timelineList = timelineDao.getTimelineByUserIdAndType(userId,"post",count);
			}else{
				timelineList = timelineDao.getTimelineByUserIdAndTypeAndStatus(userId, "post", count, "publish");
			}
			EventModel eventModel = null;
			List<EventModel> eventList = new ArrayList<EventModel>();
			if ((timelineList != null) && (timelineList.size() > 0)){
				for (Timeline t : timelineList) {
					eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
					eventList.add(eventModel);
				}
			}
			
			profile.setUser(upm);
			profile.setEvent(eventList);
			
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			if(userId.equals(loginUserid)){
				timelineList = timelineDao.getTimelineByUserIdAndType(userId,"post",count,since_id,1);
			}else{
				timelineList = timelineDao.getTimelineByUserIdAndTypeAndStatus(userId, "post", count,since_id,1, "publish");
			}
			EventModel eventModel = null;
			List<EventModel> eventList = new ArrayList<EventModel>();
			if ((timelineList != null) && (timelineList.size() > 0)){
				for (Timeline t : timelineList) {
					eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
					eventList.add(eventModel);
				}
			}
			profile.setEvent(eventList);
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			if(userId.equals(loginUserid)){
				timelineList = timelineDao.getTimelineByUserIdAndType(userId,"post",count,since_id,1);
			}else{
				timelineList = timelineDao.getTimelineByUserIdAndTypeAndStatus(userId, "post", count,since_id,1, "publish");
			}
			EventModel eventModel = null;
			List<EventModel> eventList = new ArrayList<EventModel>();
			if ((timelineList != null) && (timelineList.size() > 0)){
				for (Timeline t : timelineList) {
					eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
					eventList.add(eventModel);
				}
			}
			profile.setEvent(eventList);
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			if(userId.equals(loginUserid)){
				timelineList = timelineDao.getTimelineByUserIdAndType(userId,"post",count,max_id,2);
			}else{
				timelineList = timelineDao.getTimelineByUserIdAndTypeAndStatus(userId, "post", count,max_id,2, "publish");
			}
			EventModel eventModel = null;
			List<EventModel> eventList = new ArrayList<EventModel>();
			if ((timelineList != null) && (timelineList.size() > 0)){
				for (Timeline t : timelineList) {
					eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
					eventList.add(eventModel);
				}
			}
			profile.setEvent(eventList);
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			if(userId.equals(loginUserid)){
				timelineList = timelineDao.getTimelineByUserIdAndType(userId,"post",count,max_id,2);
			}else{
				timelineList = timelineDao.getTimelineByUserIdAndTypeAndStatus(userId, "post", count,max_id,2, "publish");
			}
			EventModel eventModel = null;
			List<EventModel> eventList = new ArrayList<EventModel>();
			if ((timelineList != null) && (timelineList.size() > 0)){
				for (Timeline t : timelineList) {
					eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
					eventList.add(eventModel);
				}
			}
			profile.setEvent(eventList);
			
		}
		long end1 = System.currentTimeMillis();
		System.out.println("end------->>>>>>>>>>>"+(end1-start1));
		System.out.println("profile-->"+JSONObject.fromObject(profile));
		return Response.status(Response.Status.OK).entity(profile).build();
	
	}

	@Override
	public Response profile_repost(Long userId,Long loginUserid,HttpServletRequest request,String appVersion) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		
		User loginUser = null;
		if(loginUserid != null && loginUserid > 0){
			loginUser = userDao.get(loginUserid);
		}
		
		/**
		 * 版本控制
		 */
		
		String path1 = getClass().getResource("/../../META-INF/version.json").getPath();
		JSONObject v = ParseFile.parseJson(path1);
		String version = v.getString("version");
		boolean flag = false;
		if(!Strings.isNullOrEmpty(appVersion)){
			if(!Strings.isNullOrEmpty(version)){
				String[] vArr = version.split("\\.");
				String[] vaArr = appVersion.split("\\.");
				if(version.equals(appVersion)){
					flag = false;
				}else{
					if(!vArr[0].equals(vaArr[0])){
						if(Integer.parseInt(vArr[0]) > Integer.parseInt(vaArr[0])){
							flag = false;
						}else{
							flag = true;
						}
					}else{
						if(!vArr[1].equals(vaArr[1])){
							if(Integer.parseInt(vArr[1]) > Integer.parseInt(vaArr[1])){
								flag = false;
							}else{
								flag = true;
							}
						}else{
							if(!vArr[2].equals(vaArr[2])){
								if(Integer.parseInt(vArr[2]) > Integer.parseInt(vaArr[2])){
									flag = false;
								}else{
									flag = true;
								}
							}
						}
					}
				}
			}
		}else{
			flag = true;
		}
		
		
		
		int count = 20;
		ProfileVo profile = new ProfileVo();
		if(flag){
			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				//UserParentModel upm = get(userId, loginUserid, appVersion);
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				//profile.setUser(upm);
				profile.setEvent(eventList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				//UserParentModel upm = get(userId, loginUserid, appVersion);
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				//profile.setUser(upm);
				profile.setEvent(eventList);
				
			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count,since_id,1);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				profile.setEvent(eventList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count,since_id,1);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				profile.setEvent(eventList);
			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count,max_id,2);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				profile.setEvent(eventList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count,max_id,2);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				profile.setEvent(eventList);
			}
		}else{
			UserParentModel upm = get(userId, loginUserid,appVersion);
			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				
				profile.setUser(upm);
				profile.setEvent(eventList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				
				profile.setUser(upm);
				profile.setEvent(eventList);
				
			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count,since_id,1);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				profile.setEvent(eventList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count,since_id,1);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				profile.setEvent(eventList);
			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count,max_id,2);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				profile.setEvent(eventList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<Timeline> timelineList = timelineDao.getTimelineByUserIdAndType(userId,"repost",count,max_id,2);
				EventModel eventModel = null;
				List<EventModel> eventList = new ArrayList<EventModel>();
				if ((timelineList != null) && (timelineList.size() > 0)){
					for (Timeline t : timelineList) {
						eventModel = getEventModelListByLoginid(t, loginUserid,loginUser);
						eventList.add(eventModel);
					}
				}
				profile.setEvent(eventList);
			}
		}
		
		
		return Response.status(Response.Status.OK).entity(profile).build();
	}
	
	@Override
	public Response profile_collection(Long userId, Long loginUserid, HttpServletRequest request,String appVersion) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		int count = 20;
		Long userid = 0l;
		if(userId == loginUserid 
				&& userId.equals(loginUserid)){
			userid = loginUserid;
		}else{
			userid = userId;
		}
		
		
		
		JSONObject json = new JSONObject();

		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Collection> cList = userCollectionDao.getCollectionByUserid(userid, count);
			List<JSONObject> collectionList = new ArrayList<JSONObject>();
			if ((cList != null) && (cList.size() > 0)){
				for (Collection c : cList) {
					JSONObject cJSON = getCollectionJSON(c);
					collectionList.add(cJSON);
				}
			}
			json.put("collections", collectionList);
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			List<Collection> cList = userCollectionDao.getCollectionByUserid(userid, count);
			List<JSONObject> collectionList = new ArrayList<JSONObject>();
			if ((cList != null) && (cList.size() > 0)){
				for (Collection c : cList) {
					JSONObject cJSON = getCollectionJSON(c);
					collectionList.add(cJSON);
				}
			}
			json.put("collections", collectionList);
			
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Collection> cList = userCollectionDao.getCollectionByUserid(userid, count,since_id,1);
			List<JSONObject> collectionList = new ArrayList<JSONObject>();
			if ((cList != null) && (cList.size() > 0)){
				for (Collection c : cList) {
					JSONObject cJSON = getCollectionJSON(c);
					collectionList.add(cJSON);
				}
			}
			json.put("collections", collectionList);
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Collection> cList = userCollectionDao.getCollectionByUserid(userid, count,since_id,1);
			List<JSONObject> collectionList = new ArrayList<JSONObject>();
			if ((cList != null) && (cList.size() > 0)){
				for (Collection c : cList) {
					JSONObject cJSON = getCollectionJSON(c);
					collectionList.add(cJSON);
				}
			}
			json.put("collections", collectionList);
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Collection> cList = userCollectionDao.getCollectionByUserid(userid, count,max_id,2);
			List<JSONObject> collectionList = new ArrayList<JSONObject>();
			if ((cList != null) && (cList.size() > 0)){
				for (Collection c : cList) {
					JSONObject cJSON = getCollectionJSON(c);
					collectionList.add(cJSON);
				}
			}
			json.put("collections", collectionList);
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Collection> cList = userCollectionDao.getCollectionByUserid(userid, count,max_id,2);
			List<JSONObject> collectionList = new ArrayList<JSONObject>();
			if ((cList != null) && (cList.size() > 0)){
				for (Collection c : cList) {
					JSONObject cJSON = getCollectionJSON(c);
					collectionList.add(cJSON);
				}
			}
			json.put("collections", collectionList);
		}
	
		
		
		return Response.status(Response.Status.OK).entity(json).build();
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
		 CollectionIntro ci = new CollectionIntro();
	         ci.setId((Long)collection.getId());
	         ci.setCollection_name(collection.getCollectionName());
	         ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
	         ci.setInfo(collection.getInfo());
				User u = collection.getUser();//userDao.get(collection.getAuthorId());
				JSONObject author = new JSONObject();
				author.put("id", u.getId());
				author.put("username", u.getUsername());
				author.put("avatar_image", JSONObject.fromObject(u.getAvatarImage()));
				ci.setAuthor(author);
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				
				
				JSONObject collectionJ = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					collectionJ = JSONObject.fromObject(ci, configs);
				} else {
					collectionJ = JSONObject.fromObject(ci);
				}
           return collectionJ;
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

	@Override
	public void push_info(JSONObject jsonObject) {
		if(jsonObject != null){
			Long userId = jsonObject.getLong("userId");
			String username = jsonObject.getString("username");
			String content = jsonObject.getString("content");
			List<PushNotification> pnList = pushNotificationDao.getPushNotificationByUserid(userId);
			GetuiModel gm = getGetuiInfo();
			JSONObject json = new JSONObject();
			json.put("user_id", userId);
			content = username + ":" + content;
			try {
				PushNotificationUtil.pushInfo(gm.getAppId(),gm.getAppKey(), 
						gm.getMasterSecret(), pnList, 1, content,json.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

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
		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String appId = jsonObject.getString("appId");
		String appKey = jsonObject.getString("appKey");
		String masterSecret = jsonObject.getString("masterSecret");
		JSONObject jo = new JSONObject();
		if(chat != null && loginUserid > 0){
			Chat chatInfo = new Chat();
			if(chat.containsKey("content") || chat.containsKey("picture")){
				if(chat.containsKey("content") && 
						!Strings.isNullOrEmpty(chat.getString("content"))){
					chatInfo.setContent(chat.getString("content"));
				}
				
				if(chat.containsKey("picture") && 
						!Strings.isNullOrEmpty(chat.getString("picture"))){
					chatInfo.setPicture(chat.getString("picture"));
				}
				
			}
			Long target_user_id = chat.getLong("target_user_id");
			chatInfo.setTarget_user_id(target_user_id);
			chatInfo.setCurrent_user_id(loginUserid);
			List<Chat> cList = chatDao.getAllChat(target_user_id, loginUserid);
			if(cList.size() == 0){
				User target_user = userDao.get(target_user_id);
				User current_user = userDao.get(loginUserid);
				Conversion c = new Conversion();
				/*if(!target_user.getUser_type().equals("admin") 
						&& !target_user.getUser_type().equals("official")){
					c.setTarget_user_id(target_user_id);
				}else if(!current_user.getUser_type().equals("admin") 
						&& !current_user.getUser_type().equals("official")){
					c.setTarget_user_id(loginUserid);
				}*/
				
				if(target_user.getUser_type().equals("normal")){
					c.setTarget_user_id(target_user_id);
				}else if(current_user.getUser_type().equals("normal")){
					c.setTarget_user_id(loginUserid);
				}
				conversionDao.save(c);
				chatInfo.setConversion(c);
			}else{
				Chat chat1 = cList.get(0);
				chatInfo.setConversion(chat1.getConversion());
			}
			
			chatDao.save(chatInfo);
			
			User user = userDao.get(loginUserid);
			/*Notification n = new Notification();
			n.setRecipientId(chat.getLong("target_user_id"));
			n.setSenderId(loginUserid);
			n.setNotificationType(7);
			n.setObjectType(3);
			n.setObjectId(chat.getLong("target_user_id"));
			n.setStatus("enabled");
			n.setRead_already(true);
			notificationDao.save(n);*/
			List<PushNotification> list = this.pushNotificationDao
					.getPushNotificationByUserid(chatInfo.getTarget_user_id());
			try {
				String content = user.getUsername() + ": ";
				if(chat.containsKey("content") || chat.containsKey("picture")){
					if(chat.containsKey("content") && 
							!Strings.isNullOrEmpty(chat.getString("content"))){
						content = content + chat.getString("content");
					}
					
					if(chat.containsKey("picture") && 
							!Strings.isNullOrEmpty(chat.getString("picture"))){
						content = content + "[图片]";
					}
					
				}
				
				
				JSONObject json = new JSONObject();
				json.put("url","privatechat");
				PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, 1, content, json.toString());
			} catch (Exception e) {
				jo.put("status", "request_invalid");
				jo.put("code", Integer.valueOf(10010));
				jo.put("error_message", "request is invalid");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}
			jo.put("status","success");
			return Response.status(Response.Status.OK).entity(jo).build();
			
		}else{
			jo.put("status", "request_invalid");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "request is invalid");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
	}

	@Override
	public Response getAllChat(Long userId, Long loginUserid) {
		JSONObject jo = new JSONObject();
		if(userId != null && loginUserid != null){
			List<Chat> cList = chatDao.getAllChat(userId, loginUserid);
			List<JSONObject> chatList = new ArrayList<JSONObject>();
			JSONObject cJson = null;
			if(cList != null && cList.size() > 0){
				for(Chat c:cList){
					cJson = new JSONObject();
					if(!Strings.isNullOrEmpty(c.getContent())){
						cJson.put("content",c.getContent());
					}
					
					if(!Strings.isNullOrEmpty(c.getPicture())){
						cJson.put("picture", c.getPicture());
					}
					Long current_id = c.getCurrent_user_id(); 
					if(loginUserid != current_id && !loginUserid.equals(current_id)){
						User user = userDao.get(c.getCurrent_user_id());
						JSONObject targetUser = new JSONObject();
						targetUser.put("id",user.getId());
						targetUser.put("username",user.getUsername());
						targetUser.put("avatar_image",JSONObject.fromObject(user.getAvatarImage()));
						cJson.put("target_user",targetUser);
					}
					
					cJson.put("create_time",c.getCreate_time());
					chatList.add(cJson);
				}
			}
			System.out.println("------------->>>>>>>>>>"+chatList.toString());
			
			return Response.status(Response.Status.OK).entity(chatList).build();
		}else{
			jo.put("status", "request_invalid");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "request is invalid");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
	}

	@Override
	public Response get_auth(JSONObject fbInfo,HttpServletRequest request) throws Exception {
		String ip = request.getRemoteAddr();
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
				Object[] obj = linkAccountsDao.getLinkAccountsByUUIDAndService(fbInfo.getString("fb_uid"), "fblife");
				long timestamp = fbInfo.getLong("timestamp");
				if(obj != null){
					LinkAccounts linkAccounts = (LinkAccounts)obj[0];
					Long userId = linkAccounts.getUser_id();
					User user = userDao.get(userId);
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
					LinkAccounts la = new LinkAccounts();
					la.setAuth_token(fbInfo.getString("token"));
					if(fbInfo.containsKey("avatar_url")){
						la.setAvatar_url(fbInfo.getString("avatar_url"));
					}
					la.setDescription(fbInfo.getString("user_name"));
					la.setRefreshed_at(sdf.format(new Date()));
					la.setService("fblife");
					la.setUser_id(u.getId());
					la.setUuid(fbInfo.getString("fb_uid"));
					this.linkAccountsDao.save(la);
				
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
		String ip = request.getRemoteAddr();
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
		String ip = request.getRemoteAddr();
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
		String ip = request.getRemoteAddr();
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
		String ip = request.getRemoteAddr();
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
				UserCentre uc = userCentreDao.getUserCentreByCentreId(centre_id);
				if(uc != null){
					Long userid = uc.getUser_id();
					User user = userDao.get(userid);
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
							if(centre_info.containsKey("nickname")){
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
							UserCentre userCentre = new UserCentre();
							userCentre.setCentre_id(centre_id);
							userCentre.setUser_id(u.getId());
							userCentreDao.save(userCentre);
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
							List<User> officialUser = userDao.getUserByUserType("official");
							if(officialUser != null && officialUser.size() > 0){
								for(User official:officialUser){
									Follow f = new Follow();
									FollowId fid = new FollowId();
									fid.setUser(u);
									fid.setFollower(official);
									f.setPk(fid);
									f.setCreateTime(new Date());
									followDao.save(f);
								}
							}
							List<User> userList = this.userDao.getUserByUserType();
							List<PushNotification> pnList = new ArrayList<PushNotification>();
							Notification n = null;
							List<Notification> notificationList = new ArrayList<Notification>();
							Configuration conf;
							if ((userList != null) && (userList.size() > 0)) {
								for (User admin : userList) {
									n = new Notification();
									n.setRecipientId((Long) admin.getId());
									n.setSenderId((Long) u.getId());
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
								UserCentre userCentre = new UserCentre();
								userCentre.setCentre_id(centre_id);
								userCentre.setUser_id(u.getId());
								userCentreDao.save(uc);
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
								List<User> officialUser = userDao.getUserByUserType("official");
								if(officialUser != null && officialUser.size() > 0){
									for(User official:officialUser){
										Follow f = new Follow();
										FollowId fid = new FollowId();
										fid.setUser(u);
										fid.setFollower(official);
										f.setPk(fid);
										f.setCreateTime(new Date());
										followDao.save(f);
									}
								}
								List<User> userList = this.userDao.getUserByUserType();
								List<PushNotification> pnList = new ArrayList<PushNotification>();
								Notification n = null;
								List<Notification> notificationList = new ArrayList<Notification>();
								Configuration conf;
								if ((userList != null) && (userList.size() > 0)) {
									for (User admin : userList) {
										n = new Notification();
										n.setRecipientId((Long) admin.getId());
										n.setSenderId((Long) u.getId());
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
							UserCentre userCentre = new UserCentre();
							userCentre.setCentre_id(centre_id);
							userCentre.setUser_id(u.getId());
							userCentreDao.save(userCentre);
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
							List<User> officialUser = userDao.getUserByUserType("official");
							if(officialUser != null && officialUser.size() > 0){
								for(User official:officialUser){
									Follow f = new Follow();
									FollowId fid = new FollowId();
									fid.setUser(u);
									fid.setFollower(official);
									f.setPk(fid);
									f.setCreateTime(new Date());
									followDao.save(f);
								}
							}
							List<User> userList = this.userDao.getUserByUserType();
							List<PushNotification> pnList = new ArrayList<PushNotification>();
							Notification n = null;
							List<Notification> notificationList = new ArrayList<Notification>();
							Configuration conf;
							if ((userList != null) && (userList.size() > 0)) {
								for (User admin : userList) {
									n = new Notification();
									n.setRecipientId((Long) admin.getId());
									n.setSenderId((Long) u.getId());
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
				res.put("error_message",  "用户信息不存在r");
				return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
			}
		}else{
			res.put("status", "request_invalid");
			res.put("code", Integer.valueOf(10010));
			res.put("error_message", "request is invalid");
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
		String ip = request.getRemoteAddr();
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
