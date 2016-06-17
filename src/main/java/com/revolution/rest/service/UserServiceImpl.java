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
import com.revolution.rest.common.ParseFile;
import com.revolution.rest.common.PushNotificationUtil;
import com.revolution.rest.dao.CollectionDao;
import com.revolution.rest.dao.CollectionStoryDao;
import com.revolution.rest.dao.CommentDao;
import com.revolution.rest.dao.ConfigurationDao;
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
import com.revolution.rest.dao.UserCollectionDao;
import com.revolution.rest.dao.UserDao;
import com.revolution.rest.model.Collection;
import com.revolution.rest.model.CollectionStory;
import com.revolution.rest.model.Comment;
import com.revolution.rest.model.Configuration;
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
import com.revolution.rest.service.model.CollectionIntro;
import com.revolution.rest.service.model.CollectionIntroLast;
import com.revolution.rest.service.model.CollectionIntros;
import com.revolution.rest.service.model.CommentSummaryModel;
import com.revolution.rest.service.model.CoverMedia;
import com.revolution.rest.service.model.CoverPageModel;
import com.revolution.rest.service.model.EventModel;
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

	public Response create(JSONObject user,String appVersion) {
		User u = new User();
		try {
			String path = getClass().getResource("/../../META-INF/getui.json").getPath();
			JSONObject json1 = ParseFile.parseJson(path);
			String appId = json1.getString("appId");
			String appKey = json1.getString("appKey");
			String masterSecret = json1.getString("masterSecret");
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
					String zone = user.get("zone").toString();
					String phone = user.get("phone").toString();
					String code = user.get("code").toString();
					if ((!Strings.isNullOrEmpty(zone)) && (!Strings.isNullOrEmpty(phone))
							&& (!Strings.isNullOrEmpty(code))) {
						User users = this.userDao.getUserByPhoneAndZone(zone, phone);
						if (users != null) {
							JSONObject jo = new JSONObject();
							jo.put("status", "phone_exists");
							jo.put("code", Integer.valueOf(10094));
							jo.put("error_message", "Phone already used");
							return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
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
						String appkey = "";
						/*if(flag){
							appkey = getClass().getResource("/../../META-INF/phone2.json").getPath();
						}else{
							appkey = getClass().getResource("/../../META-INF/phone.json").getPath();
						}*/
						appkey = getClass().getResource("/../../META-INF/phone.json").getPath();
						JSONObject jsonObject = parseJson(appkey);
						String key = jsonObject.getString("appkey");

						String param = "appkey=" + key + "&phone=" + phone + "&zone=" + zone + "&&code=" + code;
						String result = "";
						/*if(flag){
							result = requestData("https://webapi.sms.mob.com/sms/verify", param);
						}else{
							result = requestData("https://web.sms.mob.com/sms/verify", param);
						}*/
						result = requestData("https://web.sms.mob.com/sms/verify", param);
						if (!Strings.isNullOrEmpty(result)) {
							JSONObject json = JSONObject.fromObject(result);
							String status = json.get("status").toString();
							if (status.equals("200")) {
								u.setZone(zone);
								u.setPhone(phone);
							} else {
								if (status.equals("512")) {
									JSONObject j = new JSONObject();
									j.put("status", "验证失败");
									j.put("code", Integer.valueOf(10100));
									j.put("error_message", "服务器拒绝访问，或�?�拒绝操�?");
									return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
								}
								if (status.equals("405")) {
									JSONObject j = new JSONObject();
									j.put("status", "验证失败");
									j.put("code", Integer.valueOf(10101));
									j.put("error_message", "求Appkey不存在或被禁用�??");
									return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
								}
								
								if (status.equals("406")) {
									JSONObject j = new JSONObject();
									j.put("status", "验证失败");
									j.put("code", Integer.valueOf(10101));
									j.put("error_message", "求Appkey不存在或被禁用�??");
									return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
								}
								if (status.equals("514")) {
									JSONObject j = new JSONObject();
									j.put("status", "验证失败");
									j.put("code", Integer.valueOf(10102));
									j.put("error_message", "权限不足");
									return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
								}
								if (status.equals("515")) {
									JSONObject j = new JSONObject();
									j.put("status", "验证失败");
									j.put("code", 10103);
									j.put("error_message", "服务器内部错�?");
									return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
								}
								if (status.equals("517")) {
									JSONObject j = new JSONObject();
									j.put("status", "验证失败");
									j.put("code", Integer.valueOf(10104));
									j.put("error_message", "缺少必要的请求参�?");
									return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
								}
								if (status.equals("518")) {
									JSONObject j = new JSONObject();
									j.put("status", "验证失败");
									j.put("code", Integer.valueOf(10105));
									j.put("error_message", "请求中用户的手机号格式不正确（包括手机的区号�?");
									return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
								}
								if (status.equals("519")) {
									JSONObject j = new JSONObject();
									j.put("status", "验证失败");
									j.put("code", Integer.valueOf(10106));
									j.put("error_message", "请求发�?�验证码次数超出限制");
									return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
								}
								if (status.equals("468")) {
									JSONObject j = new JSONObject();
									j.put("status", "验证失败");
									j.put("code", Integer.valueOf(10107));
									j.put("error_message", "无效验证码�??");
									return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
								}
								
								if (status.equals("467")) {
									JSONObject j = new JSONObject();
									j.put("status", "验证失败");
									j.put("code", Integer.valueOf(10107));
									j.put("error_message", "请求校验验证码频繁�??");
									return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
								}
								
								JSONObject j = new JSONObject();
								j.put("status", "验证失败");
								j.put("code", Integer.valueOf(10107));
								j.put("error_message", "验证失败");
								return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
								
							}
						} else {
							JSONObject j = new JSONObject();
							j.put("status", "短信验证失败");
							j.put("code", Integer.valueOf(10108));
							j.put("error_message", "shareSDK 报错");
							return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
						}
					} else {
						JSONObject jo = new JSONObject();
						jo.put("status", "request_invalid");
						jo.put("code", Integer.valueOf(10010));
						jo.put("error_message", "request is invalid");
						return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
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
					u.setPassword(user.getString("password"));
				}

				u.setUsername(user.getString("username"));
				u.setSalt(initSalt().toString());
				u.setStatus("enabled");
				u.setUser_type("normal");
				this.userDao.save(u);
				Configuration c = new Configuration();
				c.setNew_comment_on_your_comment_push(true);
				c.setNew_comment_on_your_story_push(true);
				c.setNew_favorite_from_following_push(true);
				c.setNew_follower_push(true);
				c.setNew_story_from_following_push(true);
				c.setRecommended_my_story_push(true);
				c.setReposted_my_story_push(true);
				c.setNew_story_from_collection_push(true);
				c.setDelete_story_from_collection_push(true);
				c.setNew_story_from_collection_review_push(true);
				c.setCollection_review_agree_push(true);
				c.setCollection_review_reject_push(true);
				c.setCreate_collection_push(true);
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
				String content = u.getUsername() + "注册了壹�?";
				JSONObject json = new JSONObject();
				json.put("user_id",u.getId());
				PushNotificationUtil.pushInfoAllFollow(appId, appKey, masterSecret, pnList, map, content,json.toString());
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
					this.linkAccountsDao.save(la);
				}

				System.out.println("create successful");
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("status", "invalid request");
			jo.put("code", Integer.valueOf(10107));
			jo.put("error_message", "无效验证码�??");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}

		JSONObject json = new JSONObject();
		json.put("userid", u.getId());
		String raw = u.getId() + u.getPassword() + u.getCreated_time();

		String token = EncryptionUtil.hashMessage(raw);
		json.put("access_token", token);
		json.put("token_timestamp", u.getCreated_time());
		return Response.status(Response.Status.CREATED).entity(json).build();
	}

	public UserParentModel get(Long userId, Long loginUserid,String appVersion) {
		log.debug("****get user function**********");
		log.debug("*** loginUserid ****" + loginUserid + " *** userId" + userId);
		/**
		 * 版本控制
		 */
		
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
								ci.setAvatar_image(JSONObject.fromObject(co.getAvatar_image()));
								ci.setCollection_type(co.getCollection_type());
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
							cis.setAvatar_image(JSONObject.fromObject(collections.getAvatar_image()));
							cis.setCollection_type(collections.getCollection_type());
							if(collections.getCollection_type().equals("activity")){
								cis.setActivity_description(collections.getActivity_description());
							}
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
							cis.setIs_review(collections.isIs_review());
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
							cpm.setFocalpoint(image.getString("focalpoint"));
							cpm.setOriginal_size(image.getString("original_size"));
							cpm.setZoom(image.getString("zoom"));
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
	
	

	public Response userLogin(HttpServletRequest request) {
		User user = null;
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String timestamp = request.getParameter("timestamp");
		String zone = request.getParameter("zone");
		String phone = request.getParameter("phone");

		JSONObject jo = new JSONObject();
		JSONObject auth = new JSONObject();
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
					user = this.userDao.loginUser(email, password);

					if (user == null)
						throw new Exception("invalid password");
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

			String raw = user.getId() + user.getPassword() + timestamp;
			String token = EncryptionUtil.hashMessage(raw);

			System.out.println("userId--->" + user.getId());
			auth.put("userid", user.getId());
			auth.put("access_token", token);
			auth.put("token_timestamp", Long.valueOf(Long.parseLong(timestamp)));
		} else if ((!Strings.isNullOrEmpty(phone)) && (!Strings.isNullOrEmpty(zone))) {
			if (Strings.isNullOrEmpty(password)) {
				jo.put("status", "invalid_password");
				jo.put("code", Integer.valueOf(10007));
				jo.put("error_message", "invalid password");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}
			User phoneUser = this.userDao.getUserByPhoneAndZone(zone, phone);

			if (phoneUser != null) {
				try {
					user = this.userDao.loginByPhone(zone, phone, password);

					if (user == null)
						throw new Exception("invalid password");
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

			String raw = user.getId() + user.getPassword() + timestamp;
			String token = EncryptionUtil.hashMessage(raw);

			System.out.println("userId--->" + user.getId());
			auth.put("userid", user.getId());
			auth.put("access_token", token);
			auth.put("token_timestamp", Long.valueOf(Long.parseLong(timestamp)));
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

	public Response updatePassword(Long loginUserid, Long userId, PasswordModel pwdModel) {
		log.debug("*** update password ***");
		JSONObject json = new JSONObject();
		if (userId.equals(loginUserid)) {
			User user = (User) this.userDao.get(loginUserid);

			if (user.getPassword().toUpperCase().equals(pwdModel.getCurrent_password())) {
				user.setPassword(pwdModel.getNew_password());
				this.userDao.update(user);
				String raw = user.getId() + user.getPassword() + user.getCreated_time();
				String token = EncryptionUtil.hashMessage(raw);

				System.out.println("userId--->" + user.getId());
				json.put("userid", user.getId());
				json.put("access_token", token);
				json.put("token_timestamp", user.getCreated_time());
			} else {
				JSONObject json1 = new JSONObject();
				json1.put("status", "invalid_original_password");
				json1.put("code", Integer.valueOf(10004));
				json1.put("error_message", "The original password input error");
				return Response.status(Response.Status.BAD_REQUEST).entity(json1).build();
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
					/*String path1 = request.getContextPath();    
					String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path1+"/";   
					 File writename = new File(basePath+"WEB-INF/output.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件  
				        BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
				        out.write(ret+"\r\n"); // \r\n即为换行  
				        out.flush(); // 把缓存区内容压入文件  
				        out.close(); // �?后记得关闭文�?  
*/				}

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
				conf.setNew_story_from_collection_push(configuration.isNew_story_from_collection_push());
				conf.setDelete_story_from_collection_push(conf.isDelete_story_from_collection_push());
				conf.setNew_story_from_collection_review_push(configuration.isNew_story_from_collection_review_push());
				conf.setCollection_review_agree_push(configuration.isCollection_review_agree_push());
				conf.setCollection_review_reject_push(configuration.isCollection_review_reject_push());
				conf.setStory_move_to_collection(configuration.isStory_move_to_collection());
				conf.setCollection_review_reject_push(configuration.isCollection_review_reject_push());
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
		jo.put("error_message", "no logged in");
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
		Collection collection = this.collectionStoryDao.getCollectionByStoryId((Long) story.getId());
		if (collection != null) {
			CollectionIntro ci = new CollectionIntro();
			ci.setId((Long) collection.getId());
			ci.setCollection_name(collection.getCollectionName());
			ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
			ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
			ci.setInfo(collection.getInfo());
			ci.setCollection_type(collection.getCollection_type());
			User u = collection.getUser();
			JSONObject author = new JSONObject();
			author.put("id", u.getId());
			author.put("username", u.getUsername());
			ci.setAuthor(author);
			JsonConfig configs = new JsonConfig();
			List<String> delArray = new ArrayList<String>();
			if(!Strings.isNullOrEmpty(collection.getActivity_description())){
				ci.setActivity_description(collection.getActivity_description());
			}else{
				if (Strings.isNullOrEmpty(story.getSubtitle())) {
					delArray.add("activity_description");
				}
			}
			
			
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
		if (!Strings.isNullOrEmpty(story.getSubtitle()))
			storyModel.setSubtitle(story.getSubtitle());
		else {
			storyModel.setSubtitle(null);
		}

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
				intro.setSubtitle(s.getSubtitle());
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
				ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
				ci.setInfo(collection.getInfo());
				User author = collection.getUser();
				JSONObject json = new JSONObject();
				json.put("id",author.getId());
				json.put("username",author.getUsername());
				ci.setAuthor(json);
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				if(!Strings.isNullOrEmpty(collection.getActivity_description())){
					ci.setActivity_description(collection.getActivity_description());
				}else{
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("activity_description");
					}
				}
				
				
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
			if (!Strings.isNullOrEmpty(story.getSubtitle())) {
				storyModel.setSubtitle(story.getSubtitle());
			}

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
			if (Strings.isNullOrEmpty(story.getSubtitle())) {
				delArray.add("subtitle");
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
				if(!Strings.isNullOrEmpty(collection.getActivity_description())){
					ci.setActivity_description(collection.getActivity_description());
				}else{
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("activity_description");
					}
				}
				
				
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
			if (!Strings.isNullOrEmpty(story.getSubtitle()))
				storyModel.setSubtitle(story.getSubtitle());
			else {
				storyModel.setSubtitle(null);
			}

			storyModel.setRecommend_date(story.getRecommend_date());

			JsonConfig configs = new JsonConfig();
			List<String> delArray = new ArrayList<String>();
			if (Strings.isNullOrEmpty(story.getSubtitle())) {
				delArray.add("subtitle");
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
					if (!Strings.isNullOrEmpty(story.getSubtitle())) {
						storyModel.setSubtitle(story.getSubtitle());
					}

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
							intro.setSubtitle(s.getSubtitle());
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
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("subtitle");
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
					if (!Strings.isNullOrEmpty(story.getSubtitle()))
						storyModel.setSubtitle(story.getSubtitle());
					else {
						storyModel.setSubtitle(null);
					}

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
							intro.setSubtitle(s.getSubtitle());
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
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("subtitle");
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
					if (!Strings.isNullOrEmpty(story.getSubtitle()))
						storyModel.setSubtitle(story.getSubtitle());
					else {
						storyModel.setSubtitle(null);
					}

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("subtitle");
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
					/*Collection collection = null;
					Set<Collection> cSet = story.getCollections();
					if(cSet != null && cSet.size() > 0){
						collection = cSet.iterator().next();
					}*/
					
					Collection collection = collectionStoryDao.getCollectionByStoryId(storyId);
					if (collection != null) {
						CollectionIntro ci = new CollectionIntro();
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setInfo(collection.getInfo());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
						ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
						ci.setCollection_type(collection.getCollection_type());
						User u = collection.getUser();
						JSONObject author = new JSONObject();
						author.put("id", u.getId());
						author.put("username", u.getUsername());
						if(!Strings.isNullOrEmpty(u.getAvatarImage())){
							author.put("avatar_image", JSONObject.fromObject(u.getAvatarImage()));
						}
						ci.setAuthor(author);
						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						if(!Strings.isNullOrEmpty(collection.getActivity_description())){
							ci.setActivity_description(collection.getActivity_description());
						}else{
							if (Strings.isNullOrEmpty(story.getSubtitle())) {
								delArray.add("activity_description");
							}
						}
						/*int follow_collection_count = userCollectionDao.getCollectionByCount(collection.getId());
						ci.setFollowers_count(follow_collection_count);
						Set<User> follow_collection = collection.getUsers();
						if(follow_collection != null && follow_collection.size() > 0){
							ci.setFollowers_count(follow_collection.size());
						}else{
							ci.setFollowers_count(0);
						}*/
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
					if (!Strings.isNullOrEmpty(story.getSubtitle()))
						storyModel.setSubtitle(story.getSubtitle());
					else {
						storyModel.setSubtitle(null);
					}

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("subtitle");
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
					if (!Strings.isNullOrEmpty(story.getSubtitle()))
						storyModel.setSubtitle(story.getSubtitle());
					else {
						storyModel.setSubtitle(null);
					}

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("subtitle");
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
					ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
					ci.setInfo(collection.getInfo());
					User author = collection.getUser();
					JSONObject json = new JSONObject();
					json.put("id",author.getId());
					json.put("username",author.getUsername());
					ci.setAuthor(json);
					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if(!Strings.isNullOrEmpty(collection.getActivity_description())){
						ci.setActivity_description(collection.getActivity_description());
					}else{
						if (Strings.isNullOrEmpty(story.getSubtitle())) {
							delArray.add("activity_description");
						}
					}
					
					
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
				if (!Strings.isNullOrEmpty(story.getSubtitle()))
					storyModel.setSubtitle(story.getSubtitle());
				else {
					storyModel.setSubtitle(null);
				}

				storyModel.setRecommend_date(story.getRecommend_date());

				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				if (Strings.isNullOrEmpty(story.getSubtitle())) {
					delArray.add("subtitle");
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

	public Response loginLinkAccounts(LinkAccounts la) {
		JSONObject auth = new JSONObject();
		Object[] link = null;
		if (la != null) {
			String uuid = la.getUuid();
			link = this.linkAccountsDao.getLinkAccountsByUUID(uuid);
			
			if (link != null) {
				User user = (User)link[1];
				String raw = user.getId() + user.getPassword() + user.getCreated_time();
				String token = EncryptionUtil.hashMessage(raw);

				System.out.println("userId--->" + user.getId());
				auth.put("userid", user.getId());
				auth.put("access_token", token);
				auth.put("token_timestamp", user.getCreated_time());
				System.out.println(auth.toString());
				return Response.status(Response.Status.OK).entity(auth).build();
			}
			auth.put("status", "no_user");
			auth.put("code", Integer.valueOf(10080));
			auth.put("error_message", "Without this user.");

			return Response.status(Response.Status.BAD_REQUEST).entity(auth).build();
		}

		auth.put("status", "invalid request");
		auth.put("code", Integer.valueOf(10010));
		auth.put("error_message", "Invalid request payload");
		return Response.status(Response.Status.BAD_REQUEST).entity(auth).build();
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
				ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
				ci.setInfo(collection.getInfo());
				User author = collection.getUser();//userDao.get(collection.getAuthorId());
				JSONObject json = new JSONObject();
				json.put("id",author.getId());
				json.put("username",author.getUsername());
				ci.setAuthor(json);
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				if(!Strings.isNullOrEmpty(collection.getActivity_description())){
					ci.setActivity_description(collection.getActivity_description());
				}else{
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("activity_description");
					}
				}
				
				
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
		storyModel.setSubtitle(story.getSubtitle());

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

	public Response forgetPhone(HttpServletRequest request,String appVersion) throws Exception {
		String phone = request.getParameter("phone");
		String zone = request.getParameter("zone");
		String code = request.getParameter("code");
		String timestamp = request.getParameter("timestamp");
		String password = request.getParameter("password");
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
		String appkey = "";
		/*if(flag){
			appkey = getClass().getResource("/../../META-INF/phone2.json").getPath();
		}else{
			
		}*/
		appkey = getClass().getResource("/../../META-INF/phone.json").getPath();
		JSONObject jsonObject = parseJson(appkey);
		String key = jsonObject.getString("appkey");
		if ((!Strings.isNullOrEmpty(phone)) && (!Strings.isNullOrEmpty(zone)) && (!Strings.isNullOrEmpty(code))
				&& (!Strings.isNullOrEmpty(password))) {
			User user = this.userDao.getUserByZoneAndPhone(zone, phone);
			if (user != null) {
				String param = "appkey=" + key + "&phone=" + phone + "&zone=" + zone + "&&code=" + code;
				String result = "";
				/*if(flag){
					result = requestData("https://webapi.sms.mob.com/sms/verify", param);
				}else{
					
				}*/
				result = requestData("https://web.sms.mob.com/sms/verify", param);
				if (!Strings.isNullOrEmpty(result)) {
					JSONObject json = JSONObject.fromObject(result);
					String status = json.get("status").toString();
					if (status.equals("200")) {
						user.setPassword(password);
						this.userDao.update(user);
						String raw = user.getId() + user.getPassword() + timestamp;
						String token = EncryptionUtil.hashMessage(raw);
						JSONObject auth = new JSONObject();
						System.out.println("userId--->" + user.getId());
						auth.put("userid", user.getId());
						auth.put("access_token", token);
						auth.put("token_timestamp", Long.valueOf(Long.parseLong(timestamp)));
						System.out.println(auth.toString());
						return Response.status(Response.Status.OK).entity(auth).build();
					}
					if (status.equals("512")) {
						JSONObject j = new JSONObject();
						j.put("status", "验证失败");
						j.put("code", Integer.valueOf(10100));
						j.put("error_message", "服务器拒绝访问，或�?�拒绝操�?");
						return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
					}
					if (status.equals("513")) {
						JSONObject j = new JSONObject();
						j.put("status", "验证失败");
						j.put("code", Integer.valueOf(10101));
						j.put("error_message", "求Appkey不存在或被禁用�??");
						return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
					}
					if (status.equals("514")) {
						JSONObject j = new JSONObject();
						j.put("status", "验证失败");
						j.put("code", Integer.valueOf(10102));
						j.put("error_message", "权限不足");
						return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
					}
					if (status.equals("515")) {
						JSONObject j = new JSONObject();
						j.put("status", "验证失败");
						j.put("code", Integer.valueOf(10103));
						j.put("error_message", "服务器内部错�?");
						return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
					}
					if (status.equals("517")) {
						JSONObject j = new JSONObject();
						j.put("status", "验证失败");
						j.put("code", Integer.valueOf(10104));
						j.put("error_message", "缺少必要的请求参�?");
						return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
					}
					if (status.equals("518")) {
						JSONObject j = new JSONObject();
						j.put("status", "验证失败");
						j.put("code", Integer.valueOf(10105));
						j.put("error_message", "请求中用户的手机号格式不正确（包括手机的区号�?");
						return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
					}
					if (status.equals("519")) {
						JSONObject j = new JSONObject();
						j.put("status", "验证失败");
						j.put("code", Integer.valueOf(10106));
						j.put("error_message", "请求发�?�验证码次数超出限制");
						return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
					}
					if (status.equals("520")) {
						JSONObject j = new JSONObject();
						j.put("status", "验证失败");
						j.put("code", Integer.valueOf(10107));
						j.put("error_message", "无效验证码�??");
						return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
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
				j.put("error_message", "服务器内部错�?");
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
				String str1 = result;
				return str1;
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
		String website = null;
		if (!Strings.isNullOrEmpty(username)) {
			try {
				username = new String(username.getBytes("iso-8859-1"), "utf-8");
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
        	/* Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, story.getId());
 			if (repost != null)
 				storyModel.setRepost_by_current_user(true);
 			else {
 				storyModel.setRepost_by_current_user(false);
 			}*/
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
       
        Collection collection = null;//this.collectionStoryDao.getCollectionByStoryId(story.getId());
        Set<Collection> cSet = story.getCollections();
        if(cSet != null && cSet.size() > 0){
        	collection = cSet.iterator().next();
        	if (collection != null) {
				CollectionIntro ci = new CollectionIntro();
				ci.setId((Long) collection.getId());
				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
				ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
				ci.setInfo(collection.getInfo());
				User author = collection.getUser();//userDao.get(collection.getAuthorId());
				JSONObject json = new JSONObject();
				json.put("id",author.getId());
				json.put("username",author.getUsername());
				ci.setAuthor(json);
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				if(!Strings.isNullOrEmpty(collection.getActivity_description())){
					ci.setActivity_description(collection.getActivity_description());
				}else{
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("activity_description");
					}
				}
				
				
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
        if (!Strings.isNullOrEmpty(story.getSubtitle()))
          storyModel.setSubtitle(story.getSubtitle());
        else {
          storyModel.setSubtitle(null);
        }
        storyModel.setSummary(story.getSummary());
        JsonConfig configs = new JsonConfig();
        List<String> delArray1 = new ArrayList<String>();
        if (Strings.isNullOrEmpty(story.getSubtitle())) {
          delArray1.add("subtitle");
        }
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
	public JSONObject getTimelinesBySlides(Long loginUserid, HttpServletRequest request,String appVersion) {
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
			String followingId = loginUserid + "";
			List<Follow> followList = this.followDao.getFollowingsByUserId(loginUserid);
			if ((followList != null) && (followList.size() > 0)) {
				for (int i = 0; i < followList.size(); i++) {
					followingId = followingId + "," +  followList.get(i).getPk().getFollower().getId();
				}
			}
			SlideModel sm = null;
			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				
				timelineList = timelineDao.getTimelineByHome(count, followingId);
				
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginid(tl, loginUserid,loginUser);
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
				timelineList = timelineDao.getTimelineByHome(count, followingId);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginid(tl, loginUserid,loginUser);
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
				timelineList = timelineDao.getTimelineByHome(since_id,count,1,followingId);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginid(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				timelineList = timelineDao.getTimelineByHome(since_id,count,1,followingId);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginid(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineByHome(max_id,count,2,followingId);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginid(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineByHome(max_id,count,2,followingId);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginid(tl, loginUserid,loginUser);
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
				ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
				ci.setInfo(collection.getInfo());
				User author = collection.getUser();//userDao.get(collection.getAuthorId());
				JSONObject ajson = new JSONObject();
				ajson.put("id",author.getId());
				ajson.put("username",author.getUsername());
				if(!Strings.isNullOrEmpty(author.getAvatarImage())){
					ajson.put("avatar_image", JSONObject.fromObject(author.getAvatarImage()));
				}
				
				ci.setAuthor(ajson);
				ci.setCollection_type(collection.getCollection_type());
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
			
			//Story story = this.storyDao.getStoryByIdAndStatus(storyId, "publish", "disabled");
			//Story story = this.storyDao.getStoryByIdAndStatus(storyId, "publish");
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
					/*String theme_color = null;
					if (user.getTheme_id() != null) {
						Theme_color color = (Theme_color) this.themeColorDao.get(user.getTheme_id());
						theme_color = color.getColor();
					}*/
					authorJson.put("id", u.getId());
					authorJson.put("cover_image", coverImageJson);
					authorJson.put("username", u.getUsername());
//					authorJson.put("introduction", user.getIntroduction());
					if (avatarImageJson != null) {
						authorJson.put("avatar_image", avatarImageJson);
					}

//					authorJson.put("theme_color", theme_color);
					authorJson.put("user_type", u.getUser_type());
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
					/*if (!Strings.isNullOrEmpty(user.getWebsite()))
						authorJson.put("website", user.getWebsite());
					else {
						authorJson.put("website", null);
					}*/
					storyModel.setAuthor(authorJson);
					storyModel.setCreated_time(story.getCreated_time());
					Collection collection = cs.getCollection();
					if (collection != null) {
						CollectionIntro ci = new CollectionIntro();
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
						ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
						ci.setInfo(collection.getInfo());
						User author = collection.getUser(); //userDao.get(collection.getAuthorId());
						JSONObject json = new JSONObject();
						json.put("id",author.getId());
						json.put("username",author.getUsername());
						ci.setAuthor(json);
						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						if(!Strings.isNullOrEmpty(collection.getActivity_description())){
							ci.setActivity_description(collection.getActivity_description());
						}else{
							if (Strings.isNullOrEmpty(story.getSubtitle())) {
								delArray.add("activity_description");
							}
						}
						
						
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
					if (!Strings.isNullOrEmpty(story.getSubtitle())) {
						storyModel.setSubtitle(story.getSubtitle());
					}

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
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("subtitle");
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
					/*int count = this.commentDao.getCommentCountById((Long) story.getId());
					storyModel.setComment_count(count);*/
					Collection collection = cs.getCollection();
					if (collection != null) {
						CollectionIntro ci = new CollectionIntro();
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
						ci.setInfo(collection.getInfo());
						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						if(!Strings.isNullOrEmpty(collection.getActivity_description())){
							ci.setActivity_description(collection.getActivity_description());
						}else{
							if (Strings.isNullOrEmpty(story.getSubtitle())) {
								delArray.add("activity_description");
							}
						}
						
						
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
					if (!Strings.isNullOrEmpty(story.getSubtitle()))
						storyModel.setSubtitle(story.getSubtitle());
					else {
						storyModel.setSubtitle(null);
					}

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("subtitle");
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
	
	
	public EventModel getEventModelListByLoginid(Timeline timeline, Long loginUserid,User loginUser) {
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
					
					/*if ((u.getUser_type().equals("publisher")) || (u.getUser_type().equals("media"))) {
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
					}*/
					authorJson.put("user_type", u.getUser_type());
					storyModel.setAuthor(authorJson);
					storyModel.setCreated_time(story.getCreated_time());
					Set<User> like_set = story.getLike_users();
					if(like_set != null && like_set.size() > 0){
						storyModel.setLike_count(like_set.size());
					}else{
						storyModel.setLike_count(0);
					}
					
					collection = collectionStoryDao.getCollectionByStoryId(story.getId());
					
					if (collection != null) {
						CollectionIntro ci = new CollectionIntro();
						ci.setId(collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
						ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
						ci.setInfo(collection.getInfo());
						User author = collection.getUser();
						JSONObject json = new JSONObject();
						json.put("id",author.getId());
						json.put("username",author.getUsername());
						if(!Strings.isNullOrEmpty(author.getAvatarImage())){
							json.put("avatar_image", JSONObject.fromObject(author.getAvatarImage()));
						}
						ci.setAuthor(json);
						ci.setCollection_type(collection.getCollection_type());
						/*int follow_collection_count = userCollectionDao.getCollectionByCount(collection.getId());
						ci.setFollowers_count(follow_collection_count);
						Set<User> follow_collection = collection.getUsers();
						if(follow_collection != null && follow_collection.size() > 0){
							ci.setFollowers_count(follow_collection.size());
						}else{
							ci.setFollowers_count(0);
						}*/
						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						if(!Strings.isNullOrEmpty(collection.getActivity_description())){
							ci.setActivity_description(collection.getActivity_description());
						}else{
							if (Strings.isNullOrEmpty(story.getSubtitle())) {
								delArray.add("activity_description");
							}
						}
						
						
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
					if (!Strings.isNullOrEmpty(story.getSubtitle())) {
						storyModel.setSubtitle(story.getSubtitle());
					}

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
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("subtitle");
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
					//Set<Collection> cSet = story.getCollections();
					
					/*if(cSet != null && cSet.size() > 0){
						Iterator<Collection> it = cSet.iterator();
						collection = it.next();
						
					}*/
					
					collection = collectionStoryDao.getCollectionByStoryId(story.getId());
					if (collection != null) {
						CollectionIntro ci = new CollectionIntro();
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
						ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
						ci.setInfo(collection.getInfo());
						User author = collection.getUser();//userDao.get(collection.getAuthorId());
						JSONObject json = new JSONObject();
						json.put("id",author.getId());
						json.put("username",author.getUsername());
						if(!Strings.isNullOrEmpty(author.getAvatarImage())){
							json.put("avatar_image",JSONObject.fromObject(author.getAvatarImage()));
						}
						/*int follow_collection_count = userCollectionDao.getCollectionByCount(collection.getId());
						ci.setFollowers_count(follow_collection_count);
						Set<User> follow_collection = collection.getUsers();
						if(follow_collection != null && follow_collection.size() > 0){
							ci.setFollowers_count(follow_collection.size());
						}else{
							ci.setFollowers_count(0);
						}*/
						
						ci.setAuthor(json);
						ci.setCollection_type(collection.getCollection_type());
						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();
						if(!Strings.isNullOrEmpty(collection.getActivity_description())){
							ci.setActivity_description(collection.getActivity_description());
						}else{
							if (Strings.isNullOrEmpty(story.getSubtitle())) {
								delArray.add("activity_description");
							}
						}
						
						
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
					if (!Strings.isNullOrEmpty(story.getSubtitle()))
						storyModel.setSubtitle(story.getSubtitle());
					else {
						storyModel.setSubtitle(null);
					}

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("subtitle");
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
					if (!Strings.isNullOrEmpty(story.getSubtitle())) {
						storyModel.setSubtitle(story.getSubtitle());
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
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("subtitle");
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
					if (!Strings.isNullOrEmpty(story.getSubtitle()))
						storyModel.setSubtitle(story.getSubtitle());
					else {
						storyModel.setSubtitle(null);
					}

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getSubtitle())) {
						delArray.add("subtitle");
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
	public SlideModel getSlideModel(Slide slide){
		SlideModel sm = new SlideModel();
		sm.setId(slide.getId());
		String type = slide.getType();
		Long id = slide.getReference_id();
		JSONObject slideJson = new JSONObject();
		if(type.equals("story")){
			Story story = storyDao.get(id);
			JSONObject json = getStoryEventByStory(story);
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
 	         ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
 	         ci.setInfo(collection.getInfo());
 	         ci.setCollection_type(collection.getCollection_type());
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
			if(!Strings.isNullOrEmpty(collection.getActivity_description())){
				ci.setActivity_description(collection.getActivity_description());
			}else{
				if (Strings.isNullOrEmpty(collection.getActivity_description())) {
					delArray.add("activity_description");
				}
			}
			
			
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
		String phone = request.getParameter("phone");
		String zone = request.getParameter("zone");
		String code = request.getParameter("code");
		String timestamp = request.getParameter("token_timestamp");
		String password = request.getParameter("password");
		String key = "";
		if(loginUserid != null){
			User u = userDao.get(loginUserid);
			if(u != null){
				if ((!Strings.isNullOrEmpty(zone)) && (!Strings.isNullOrEmpty(phone))
						&& (!Strings.isNullOrEmpty(code))) {
					User users = this.userDao.getUserByPhoneAndZone(zone, phone);
					if (users != null) {
						JSONObject jo = new JSONObject();
						jo.put("status", "phone_exists");
						jo.put("code", Integer.valueOf(10094));
						jo.put("error_message", "Phone already used");
						return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
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
					String appkey = "";
					/*if(flag){
						appkey = getClass().getResource("/../../META-INF/phone2.json").getPath();
					}else{
						appkey = getClass().getResource("/../../META-INF/phone.json").getPath();
					}*/
					
					appkey = getClass().getResource("/../../META-INF/phone.json").getPath();

					JSONObject jsonObject = parseJson(appkey);
					key = jsonObject.getString("appkey");

					String param = "appkey=" + key + "&phone=" + phone + "&zone=" + zone + "&&code=" + code;
					String result = "";
					/*if(flag){
						result = requestData("https://webapi.sms.mob.com/sms/verify", param);
					}else{
						result = requestData("https://web.sms.mob.com/sms/verify", param);
					}*/
					
					result = requestData("https://web.sms.mob.com/sms/verify", param);
					
					if (!Strings.isNullOrEmpty(result)) {
						JSONObject json = JSONObject.fromObject(result);
						String status = json.get("status").toString();
						if (status.equals("200")) {
							u.setZone(zone);
							u.setPhone(phone);
							u.setPassword(password);
							userDao.update(u);
							String raw = u.getId() + u.getPassword() + timestamp;
							String token = EncryptionUtil.hashMessage(raw);
							
							JSONObject j = new JSONObject();
							j.put("userid", u.getId());
							j.put("access_token", token);
							j.put("token_timestamp", Long.parseLong(timestamp));
							return Response.status(Response.Status.OK).entity(j).build();
						} else {
							if (status.equals("512")) {
								JSONObject j = new JSONObject();
								j.put("status", "验证失败");
								j.put("code", Integer.valueOf(10100));
								j.put("error_message", "服务器拒绝访问，或�?�拒绝操�?");
								return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
							}
							if (status.equals("513")) {
								JSONObject j = new JSONObject();
								j.put("status", "验证失败");
								j.put("code", Integer.valueOf(10101));
								j.put("error_message", "求Appkey不存在或被禁用�??");
								return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
							}
							if (status.equals("514")) {
								JSONObject j = new JSONObject();
								j.put("status", "验证失败");
								j.put("code", Integer.valueOf(10102));
								j.put("error_message", "权限不足");
								return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
							}
							if (status.equals("515")) {
								JSONObject j = new JSONObject();
								j.put("status", "验证失败");
								j.put("code", 10103);
								j.put("error_message", "服务器内部错�?");
								return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
							}
							if (status.equals("517")) {
								JSONObject j = new JSONObject();
								j.put("status", "验证失败");
								j.put("code", Integer.valueOf(10104));
								j.put("error_message", "缺少必要的请求参�?");
								return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
							}
							if (status.equals("518")) {
								JSONObject j = new JSONObject();
								j.put("status", "验证失败");
								j.put("code", Integer.valueOf(10105));
								j.put("error_message", "请求中用户的手机号格式不正确（包括手机的区号�?");
								return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
							}
							if (status.equals("519")) {
								JSONObject j = new JSONObject();
								j.put("status", "验证失败");
								j.put("code", Integer.valueOf(10106));
								j.put("error_message", "请求发�?�验证码次数超出限制");
								return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
							}
							if (status.equals("520")) {
								JSONObject j = new JSONObject();
								j.put("status", "验证失败");
								j.put("code", Integer.valueOf(10107));
								j.put("error_message", "无效验证码�??");
								return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
							}
						}
					} else {
						JSONObject j = new JSONObject();
						j.put("status", "短信验证失败");
						j.put("code", Integer.valueOf(10108));
						j.put("error_message", "shareSDK 报错");
						return Response.status(Response.Status.BAD_REQUEST).entity(j).build();
					}
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
				json.put("code", Integer.valueOf(10011));
				json.put("error_message", "The user does not exist");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
			
		}else{
			JSONObject json = new JSONObject();
			json.put("status", "no_resource");
			json.put("code",10011);
			json.put("error_message", "The user does not exist");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		return null;
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
		StoryEvent storyModel = new StoryEvent();
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
		Collection collection = this.collectionStoryDao.getCollectionByStoryId(story.getId());
		if (collection != null) {
			CollectionIntro ci = new CollectionIntro();
			ci.setId((Long) collection.getId());
			ci.setCollection_name(collection.getCollectionName());
			ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
			ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
			ci.setInfo(collection.getInfo());
			User u = collection.getUser();//userDao.get(collection.getAuthorId());
			JSONObject json = new JSONObject();
			json.put("id",u.getId());
			json.put("username",u.getUsername());
			ci.setAuthor(json);
			JsonConfig configs = new JsonConfig();
			List<String> delArray = new ArrayList<String>();
			if(!Strings.isNullOrEmpty(collection.getActivity_description())){
				ci.setActivity_description(collection.getActivity_description());
			}else{
				if (Strings.isNullOrEmpty(story.getSubtitle())) {
					delArray.add("activity_description");
				}
			}
			
			
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
		if (!Strings.isNullOrEmpty(story.getSubtitle()))
			storyModel.setSubtitle(story.getSubtitle());
		else {
			storyModel.setSubtitle(null);
		}

		storyModel.setRecommend_date(story.getRecommend_date());

		JsonConfig configs = new JsonConfig();
		List<String> delArray = new ArrayList<String>();
		if (Strings.isNullOrEmpty(story.getSubtitle())) {
			delArray.add("subtitle");
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
	         ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
	         ci.setInfo(collection.getInfo());
	         ci.setCollection_type(collection.getCollection_type());
				User u = collection.getUser();//userDao.get(collection.getAuthorId());
				JSONObject author = new JSONObject();
				author.put("id", u.getId());
				author.put("username", u.getUsername());
				author.put("avatar_image", JSONObject.fromObject(u.getAvatarImage()));
				ci.setAuthor(author);
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				if(!Strings.isNullOrEmpty(collection.getActivity_description())){
					ci.setActivity_description(collection.getActivity_description());
				}else{
					if (Strings.isNullOrEmpty(collection.getActivity_description())) {
						delArray.add("activity_description");
					}
				}
				
				
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


}
