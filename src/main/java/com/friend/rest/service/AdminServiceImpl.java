package com.friend.rest.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.friend.rest.common.FBEncryption;
import com.friend.rest.common.HttpUtil;
import com.friend.rest.common.ParseFile;
import com.friend.rest.common.PushNotificationUtil;
import com.friend.rest.dao.AdminDao;
import com.friend.rest.dao.CarClubDao;
import com.friend.rest.dao.CategoryCollectionDao;
import com.friend.rest.dao.CategoryDao;
import com.friend.rest.dao.CollectionDao;
import com.friend.rest.dao.DistrictsDao;
import com.friend.rest.dao.NavigationDao;
import com.friend.rest.dao.NotificationDao;
import com.friend.rest.dao.PushNotificationDao;
import com.friend.rest.dao.SendMessageDao;
import com.friend.rest.dao.UserDao;
import com.friend.rest.model.Admin;
import com.friend.rest.model.CarClub;
import com.friend.rest.model.Category;
import com.friend.rest.model.CategoryCollection;
import com.friend.rest.model.Collection;
import com.friend.rest.model.Districts;
import com.friend.rest.model.Navigation;
import com.friend.rest.model.Notification;
import com.friend.rest.model.PushNotification;
import com.friend.rest.model.SendMessage;
import com.friend.rest.model.User;
import com.friend.rest.service.model.GetuiModel;
import com.google.common.base.Strings;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@Transactional
public class AdminServiceImpl implements AdminService {
	private static final Log log = LogFactory.getLog(AdminServiceImpl.class);

	@Autowired
	private AdminDao adminDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private NavigationDao navigationDao;
	
	@Autowired
	private CarClubDao carClubDao;
	
	@Autowired
	private CategoryDao categoryDao;
	
	@Autowired
	private CategoryCollectionDao categoryCollectionDao;
	
	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private DistrictsDao districtsDao;
	
	@Autowired
	private SendMessageDao sendMessageDao;
	
	@Autowired
	private NotificationDao notificationDao;
	
	@Autowired
	private PushNotificationDao pushNotificationDao;

//	public Response reset(Long loginUserid) throws Exception {
//		log.debug("**reset start ****");
//		JSONObject json = new JSONObject();
//		User user = (User) this.userDao.get(loginUserid);
//
//		if (user.getUser_type().equals("admin")) {
//			Properties p = new Properties();
//			p.load(new FileInputStream(new File("/app/tomcat/webapps/tella-webservice/META-INF/database.properties")));
//
//			importSql(p);
//			json.put("status", "success");
//			log.debug("***success****");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public static void importSql(Properties properties) throws IOException {
//		System.out.println("start import");
//		Runtime runtime = Runtime.getRuntime();
//
//		String[] command = getImportCommand(properties);
//		Process process = runtime.exec(command[0]);
//
//		OutputStream os = process.getOutputStream();
//		OutputStreamWriter out = new OutputStreamWriter(os);
//		out.write(command[1] + "\r\n" + command[2]);
//		out.flush();
//		out.close();
//		os.close();
//	}
//
//	public static String[] getImportCommand(Properties properties) {
//		String username = properties.getProperty("jdbc.username");
//		String password = properties.getProperty("jdbc.password");
//		String host = properties.getProperty("jdbc.host");
//		String port = properties.getProperty("jdbc.port");
//		String importDatabaseName = properties.getProperty("jdbc.importDatabaseName");
//		String importPath = properties.getProperty("jdbc.importPath");
//		System.out
//				.println(username + "-->" + password + "-->" + host + "-->" + importDatabaseName + "-->" + importPath);
//
//		String loginCommand = "/app/mysql/bin/mysql -u" + username + " -p" + password + " -h" + host + " -P" + port;
//		System.out.println("loginCommand-->" + loginCommand);
//
//		String switchCommand = "use " + importDatabaseName;
//		System.out.println("switchCommand" + switchCommand);
//
//		String importCommand = "source " + importPath;
//		System.out.println("importCommand" + importCommand);
//		String[] strArr = { loginCommand, switchCommand, importCommand };
//		return strArr;
//	}
//
//	public Response createCollection(Long loginUserid, JSONObject collection) {
//		JSONObject json = new JSONObject();
//		User user = (User) this.userDao.get(loginUserid);
//		if ((user.getUser_type().equals("admin")) || (user.getUser_type().equals("super_admin"))) {
//			Collection c = new Collection();
//			if (collection != null) {
//				String collectionName = collection.getString("collection_name");
//				if (!Strings.isNullOrEmpty(collectionName)) {
//					int size = this.collectionDao.getCollectionCountByName(collectionName);
//					if (size == 0) {
////						c.setCollectionName(collectionName);
//
//						if (!Strings.isNullOrEmpty(collection.getString("cover_image"))) {
//							c.setCover_image(collection.getString("cover_image"));
//						}
//						c.setStatus("enabled");
////						c.setUser(user);
//						c.setNumber(Long.valueOf(1L));
////						if (collection.containsKey("info")) {
////							c.setInfo(collection.getString("info"));
////						}
//						Interest interest = interestDao.get(collection.getLong("interest_id"));
////						c.setInterest(interest);
//						this.collectionDao.save(c);
////						c.setNumber((Long) c.getId());
//						this.collectionDao.update(c);
//						CollectionModel collectionModel = new CollectionModel();
////						collectionModel.setCollection_name(c.getCollectionName());
////						collectionModel.setId((Long) c.getId());
////						collectionModel.setInfo(c.getInfo());
//						collectionModel.setCover_image(JSONObject.fromObject(c.getCover_image()));
//						return Response.status(Response.Status.CREATED).entity(collectionModel).build();
//					}
//					json.put("status", "repetition_collection_name");
//					json.put("code", Integer.valueOf(10036));
//					json.put("error_message", "collection name is repetition");
//					return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//				}
//
//				json.put("status", "invalid_request");
//				json.put("code", Integer.valueOf(10010));
//				json.put("error_message", "Invalid payload parameters");
//				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//			}
//
//			json.put("status", "invalid_request");
//			json.put("code", Integer.valueOf(10010));
//			json.put("error_message", "Invalid payload parameters");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//
//		json.put("status", "invalid_permission");
//		json.put("code", Integer.valueOf(10054));
//		json.put("error_message", "user has no permission");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public Response deleteCollection(Long collection_id) {
//		Collection collection = (Collection) this.collectionDao.get(0);
//		JSONObject json = new JSONObject();
//		if (collection != null) {
//			this.collectionStoryDao.deleteCollectionStoryByCollectionId(collection_id);
//			this.collectionDao.delete(0);
//			userCollectionDao.delUserCollectionByCollectionId(collection_id);
//			FeatureCollection fc = featureCollectionDao.getFeatureCollectionByCollectionId(collection_id);
//			if (fc != null) {
//				featureCollectionDao.delFeatureCollection(collection_id);
//			}
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public Response createCollectionStory(Long storyId,JSONObject collectionIds, Long loginUserid) {
//		Story story = storyDao.get(storyId);
//		List<CollectionStory> csList = collectionStoryDao.getCollectionStorysByStoryId(storyId);
//		JSONObject json = new JSONObject();
//		if(collectionIds != null){
//			JSONArray ja = collectionIds.getJSONArray("colectionsIDs");
//			Object[] objArr = ja.toArray();
//			for(Object obj:objArr){
//				Long c_id = Long.parseLong(obj.toString());
//				
//				CollectionStory collectionStory = collectionStoryDao.getCollectionStoryByCollectionIdAndStoryId(c_id, storyId);
//				if(collectionStory == null){
//					Collection c = collectionDao.get(0);
//					CollectionStory cs = new CollectionStory();
//					cs.setCollection(c);
//					cs.setStory(story);
//					this.collectionStoryDao.save(cs);
//
//				}else{
//					if(!csList.contains(collectionStory)){
//						collectionStoryDao.deleteCollectionStoryByCollectionIdAndStoryId(c_id, storyId);
//					}
//				}
//			}
//			
//			json.put("status", "success");
//			return Response.status(Response.Status.CREATED).entity(json).build();
//		}
//		
//
//		json.put("status", "collection_deleted");
//		json.put("code", Integer.valueOf(10088));
//		json.put("error_message", "The collection is already deleted");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public Response deleteCollectionStory(Long collectionId, Long storyId) {
//		CollectionStory cs = this.collectionStoryDao.getCollectionStoryByCollectionIdAndStoryId(collectionId, storyId);
//		JSONObject json = new JSONObject();
//		if (cs != null) {
//			this.collectionStoryDao.delete((Long) cs.getId());
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public List<CollectionModel> getCollections(Long storyId) {
//		List<Collection> collectionList = this.collectionDao.getCollections();
//		List<CollectionModel> collectionModelList = new ArrayList<CollectionModel>();
//		if ((collectionList != null) && (collectionList.size() > 0)) {
//			CollectionModel cm = null;
//			for (Collection c : collectionList) {
//				Set<Story> storySet = null;//c.getStories();
//				List<Long> ids = new ArrayList<Long>();
//				if ((storySet != null) && (storySet.size() > 0)) {
//					for (Story s : storySet) {
//						ids.add(s.getId());
//					}
//				}
//
//				cm = new CollectionModel();
////				cm.setId((Long) c.getId());
////				cm.setCollection_name(c.getCollectionName());
//				if (!Strings.isNullOrEmpty(c.getCover_image()))
//					cm.setCover_image(JSONObject.fromObject(c.getCover_image()));
//				else {
//					cm.setCover_image(null);
//				}
//
//				if (ids.contains(storyId))
//					cm.setIs_story_in_collection(true);
//				else {
//					cm.setIs_story_in_collection(false);
//				}
////				cm.setInfo(c.getInfo());
//				collectionModelList.add(cm);
//			}
//		}
//		return collectionModelList;
//	}
//
//	public List<StoryModel> getStoryByTime(Long loginUserid, HttpServletRequest request) {
//		String since_date = request.getParameter("since_date");
//		String max_date = request.getParameter("max_date");
//		String countStr = request.getParameter("count");
//		String sinceIdStr = request.getParameter("since_id");
//		String maxIdStr = request.getParameter("max_id");
//		String filter_type = request.getParameter("filter_type");
//		int count = 20;
//		String type = "publish";
//		List<StoryModel> storyModelList = new ArrayList<StoryModel>();
//		StoryModel storyModel = null;
//		if (filter_type.equals("time")) {
//			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//					&& (Strings.isNullOrEmpty(maxIdStr))) {
//				List<Story> storyList = this.storyDao.getStoriesByTimeAndNull(count, type, since_date, max_date);
//				if ((storyList != null) && (storyList.size() > 0))
//					for (Story story : storyList) {
//						storyModel = getStoryModelByStoryLoginUser(story, loginUserid);
//						storyModelList.add(storyModel);
//					}
//			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//					&& (Strings.isNullOrEmpty(maxIdStr))) {
//				count = Integer.parseInt(countStr);
//				List<Story> storyList = this.storyDao.getStoriesByTimeAndNull(count, type, since_date, max_date);
//				if ((storyList != null) && (storyList.size() > 0))
//					for (Story story : storyList) {
//						storyModel = getStoryModelByStoryLoginUser(story, loginUserid);
//						storyModelList.add(storyModel);
//					}
//			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//					&& (Strings.isNullOrEmpty(maxIdStr))) {
//				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//				List<Story> storyList = this.storyDao.getStoriesByTime(since_id, count, 1, type, since_date, max_date);
//				if ((storyList != null) && (storyList.size() > 0))
//					for (Story story : storyList) {
//						storyModel = getStoryModelByStoryLoginUser(story, loginUserid);
//						storyModelList.add(storyModel);
//					}
//			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//					&& (Strings.isNullOrEmpty(maxIdStr))) {
//				count = Integer.parseInt(countStr);
//				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//				List<Story> storyList = this.storyDao.getStoriesByTime(since_id, count, 1, type, since_date, max_date);
//				if ((storyList != null) && (storyList.size() > 0))
//					for (Story story : storyList) {
//						storyModel = getStoryModelByStoryLoginUser(story, loginUserid);
//						storyModelList.add(storyModel);
//					}
//			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//					&& (!Strings.isNullOrEmpty(maxIdStr))) {
//				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//				List<Story> storyList = this.storyDao.getStoriesByTime(max_id, count, 2, type, since_date, max_date);
//				if ((storyList != null) && (storyList.size() > 0))
//					for (Story story : storyList) {
//						storyModel = getStoryModelByStoryLoginUser(story, loginUserid);
//						storyModelList.add(storyModel);
//					}
//			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//					&& (!Strings.isNullOrEmpty(maxIdStr))) {
//				count = Integer.parseInt(countStr);
//				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//				List<Story> storyList = this.storyDao.getStoriesByTime(max_id, count, 2, type, since_date, max_date);
//				if ((storyList != null) && (storyList.size() > 0))
//					for (Story s : storyList) {
//						storyModel = getStoryModelByStoryLoginUser(s, loginUserid);
//						storyModelList.add(storyModel);
//					}
//			}
//		} else if (filter_type.equals("view_times")) {
//			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//					&& (Strings.isNullOrEmpty(maxIdStr))) {
//				List<Story> storyList = this.storyDao.getStoriesByViewAndNull(count, type, since_date, max_date);
//				if ((storyList != null) && (storyList.size() > 0))
//					for (Story story : storyList) {
//						storyModel = getStoryModelByStoryLoginUser(story, loginUserid);
//						storyModelList.add(storyModel);
//					}
//			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//					&& (Strings.isNullOrEmpty(maxIdStr))) {
//				count = Integer.parseInt(countStr);
//				List<Story> storyList = this.storyDao.getStoriesByViewAndNull(count, type, since_date, max_date);
//				if ((storyList != null) && (storyList.size() > 0))
//					for (Story story : storyList) {
//						storyModel = getStoryModelByStoryLoginUser(story, loginUserid);
//						storyModelList.add(storyModel);
//					}
//			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//					&& (Strings.isNullOrEmpty(maxIdStr))) {
//				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//				List<Map<String, Object>> storyList = this.storyDao.getStoriesByView(since_id, count, 1, type,
//						since_date, max_date);
//				if ((storyList != null) && (storyList.size() > 0))
//					for (int i = 0; i < storyList.size(); i++) {
//						Map<String, Object> map = (Map<String, Object>) storyList.get(i);
//						storyModel = getStoryModelByStoryLoginUser(map, loginUserid);
//						storyModelList.add(storyModel);
//					}
//			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//					&& (Strings.isNullOrEmpty(maxIdStr))) {
//				count = Integer.parseInt(countStr);
//				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//				List<Map<String, Object>> storyList = this.storyDao.getStoriesByView(since_id, count, 1, type,
//						since_date, max_date);
//				if ((storyList != null) && (storyList.size() > 0))
//					for (int i = 0; i < storyList.size(); i++) {
//						Map<String, Object> map = (Map<String, Object>) storyList.get(i);
//						storyModel = getStoryModelByStoryLoginUser(map, loginUserid);
//						storyModelList.add(storyModel);
//					}
//			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//					&& (!Strings.isNullOrEmpty(maxIdStr))) {
//				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//				List<Map<String, Object>> storyList = this.storyDao.getStoriesByView(max_id, count, 2, type, since_date,
//						max_date);
//				if ((storyList != null) && (storyList.size() > 0))
//					for (int i = 0; i < storyList.size(); i++) {
//						Map<String, Object> map = storyList.get(i);
//						storyModel = getStoryModelByStoryLoginUser(map, loginUserid);
//						storyModelList.add(storyModel);
//					}
//			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//					&& (!Strings.isNullOrEmpty(maxIdStr))) {
//				count = Integer.parseInt(countStr);
//				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//
//				List<Map<String, Object>> storyList = this.storyDao.getStoriesByView(max_id, count, 2, type, since_date,
//						max_date);
//				if ((storyList != null) && (storyList.size() > 0)) {
//					for (int i = 0; i < storyList.size(); i++) {
//						Map<String, Object> map = (Map<String, Object>) storyList.get(i);
//						storyModel = getStoryModelByStoryLoginUser(map, loginUserid);
//						storyModelList.add(storyModel);
//					}
//				}
//			}
//		}
//		log.debug("*** get stories list***" + JSONArray.fromObject(storyModelList));
//
//		return storyModelList;
//	}
//
//	public StoryModel getStoryModelByStoryLoginUser(Story story, Long loginUserid) {
//		StoryModel storyModel = new StoryModel();
//		List<StoryElement> storyElements = new ArrayList<StoryElement>();
//		storyModel.setId((Long) story.getId());
//		storyModel.setImage_count(story.getImage_count());
//		int likesCount = this.likesDao.userLikesCount((Long) story.getUser().getId());
//		int repostStoryCount = this.republishDao.userRepostCount((Long) story.getUser().getId());
//		User user = story.getUser();
//
//		Follow loginUserFollowAuthor = this.followDao.getFollow(loginUserid, (Long) story.getUser().getId());
//		Follow AuthorFollowLoginUser = this.followDao.getFollow((Long) story.getUser().getId(), loginUserid);
//
//		boolean followed_by_current_user = false;
//		boolean is_following_current_user = false;
//		if (loginUserFollowAuthor != null) {
//			followed_by_current_user = true;
//		}
//
//		if (AuthorFollowLoginUser != null) {
//			is_following_current_user = true;
//		}
//		JSONObject avatarImageJson = null;
//		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
//		}
//
//		JSONObject coverImageJson = null;
//		if (!Strings.isNullOrEmpty(user.getCoverImage())) {
//			coverImageJson = JSONObject.fromObject(user.getCoverImage());
//		}
//		int storyCount = this.storyDao.getStoryCount((Long) user.getId());
//		int follower_Count = this.followDao.userFollowedCount((Long) user.getId());
//		int following_count = this.followDao.userFollowCount(user.getId());
//		JSONObject authorJson = new JSONObject();
//		authorJson.put("id", user.getId());
//		authorJson.put("username", user.getUsername());
//		authorJson.put("email", user.getEmail());
//		authorJson.put("created_time", user.getCreated_time());
//		authorJson.put("status", user.getStatus());
//		authorJson.put("introduction", user.getIntroduction());
//		authorJson.put("avatar_image", avatarImageJson);
//		authorJson.put("cover_image", coverImageJson);
//		authorJson.put("likes_count", Integer.valueOf(likesCount));
//		authorJson.put("reposts_count", Integer.valueOf(repostStoryCount));
//		authorJson.put("stories_count", Integer.valueOf(storyCount));
//		authorJson.put("followers_count", Integer.valueOf(follower_Count));
//		authorJson.put("following_count", Integer.valueOf(following_count));
//		if (!Strings.isNullOrEmpty(user.getWebsite()))
//			authorJson.put("website", user.getWebsite());
//		else {
//			authorJson.put("website", null);
//		}
//
//		authorJson.put("followed_by_current_user", Boolean.valueOf(followed_by_current_user));
//		authorJson.put("is_following_current_user", Boolean.valueOf(is_following_current_user));
//
//		if (!Strings.isNullOrEmpty(story.getSummary())) {
//			storyModel.setSummary(story.getSummary());
//		} else {
//			storyModel.setSummary("");
//		}
//
//		storyModel.setAuthor(authorJson);
//		storyModel.setCreated_time(story.getCreated_time());
//		storyModel.setUpdate_time(story.getUpdate_time());
//
//		JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
//		String type = jsonObject.getString("type");
//		CoverMedia coverMedia = null;
//		if (type.equals("text")) {
//			coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
//			storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//		} else if (type.equals("image")) {
//			coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
//			storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//		} else if (type.equals("multimedia")) {
//			storyModel.setCover_media(jsonObject);
//		}
//
//		List<StoryElement> seSet = story.getElements();
//		if ((seSet != null) && (seSet.size() > 0)) {
//			JSONObject content = null;
//			String types = "";
//			TextCover textCover = null;
//			ImageCover imageCover = null;
//			for (StoryElement element : seSet) {
//				content = JSONObject.fromObject(element.getContents());
//
//				types = content.getString("type");
//				if (types.equals("text")) {
//					textCover = (TextCover) JSONObject.toBean(content, TextCover.class);
//					log.debug("*** element TextCover type ***" + textCover.getType());
//					element.setContent(textCover);
//				} else if (types.equals("image")) {
//					imageCover = (ImageCover) JSONObject.toBean(content, ImageCover.class);
//					log.debug("*** element ImageCover type ***" + imageCover.getType());
//					element.setContent(imageCover);
//				} else if (types.equals("location")) {
//					LocationModel locationModel = (LocationModel) JSONObject.toBean(content, LocationModel.class);
//					element.setContent(locationModel);
//				} else if (types.equals("link")) {
//					String media = content.getString("media");
//					JSONObject mediaJSON = JSONObject.fromObject(media);
//					if (mediaJSON.containsKey("image")) {
//						LinkModel linkModel = (LinkModel) JSONObject.toBean(content, LinkModel.class);
//						element.setContent(linkModel);
//					} else {
//						LinkModels linkModel = (LinkModels) JSONObject.toBean(content, LinkModels.class);
//						element.setContent(linkModel);
//					}
//
//				} else if (types.equals("video")){
//					VideoCover videoMedia = (VideoCover) JSONObject.toBean(content, VideoCover.class);
//					element.setContent(videoMedia);
//				} else if (types.equals("line")){
//					LineCover lineMedia = (LineCover) JSONObject.toBean(content, LineCover.class);
//					element.setContent(lineMedia);
//				}
//				storyElements.add(element);
//			}
//		}
//
//		JsonConfig config = new JsonConfig();
//		config.setExcludes(new String[] { "storyinfo", "contents" });
//		config.setIgnoreDefaultExcludes(false);
//		config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//		log.debug("***get Elements *****" + JSONArray.fromObject(story.getElements(), config));
//		storyModel.setElements(JSONArray.fromObject(storyElements, config));
//
//		storyModel.setCommnents_enables(story.getComments_enabled());
//		if (!Strings.isNullOrEmpty(story.getTinyURL()))
//			storyModel.setUrl(story.getTinyURL());
//		else {
//			storyModel.setUrl(null);
//		}
//
//		storyModel.setView_count(story.getViewTimes());
//		storyModel.setTitle(story.getTitle());
//
//		int count = this.commentDao.getCommentCountById((Long) story.getId());
//		storyModel.setComment_count(count);
//		Likes likes = this.likesDao.getLikeByUserIdAndStoryId(loginUserid, (Long) story.getId());
//		if (likes != null)
//			storyModel.setLiked_by_current_user(true);
//		else {
//			storyModel.setLiked_by_current_user(false);
//		}
//		int likeCount = this.likesDao.likeStoryCount((Long) story.getId());
//		storyModel.setLike_count(likeCount);
//		int repostCount = this.republishDao.count((Long) story.getId());
//		storyModel.setRepost_count(repostCount);
//		Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, (Long) story.getId());
//		if (repost != null)
//			storyModel.setRepost_by_current_user(true);
//		else {
//			storyModel.setRepost_by_current_user(false);
//		}
//
//		return storyModel;
//	}
//
//	public StoryModel getStoryModelByStoryLoginUser(Map<String, Object> map, Long loginUserid) {
//		StoryModel storyModel = new StoryModel();
//		List<StoryElement> storyElements = new ArrayList<StoryElement>();
//		Story story = this.storyDao.getStoryByIdAndStatus(Long.valueOf(map.get("id").toString()), "publish");
//		storyModel.setId(Long.valueOf(map.get("id").toString()));
//		storyModel.setImage_count(Integer.valueOf(map.get("image_count").toString()).intValue());
//		int likesCount = this.likesDao.userLikesCount(Long.valueOf(map.get("author_id").toString()));
//		int repostStoryCount = this.republishDao.userRepostCount(Long.valueOf(map.get("author_id").toString()));
//		User user = (User) this.userDao.get(Long.valueOf(map.get("author_id").toString()));
//
//		Follow loginUserFollowAuthor = this.followDao.getFollow(loginUserid,
//				Long.valueOf(map.get("author_id").toString()));
//		Follow AuthorFollowLoginUser = this.followDao.getFollow(Long.valueOf(map.get("author_id").toString()),
//				loginUserid);
//
//		boolean followed_by_current_user = false;
//		boolean is_following_current_user = false;
//		if (loginUserFollowAuthor != null) {
//			followed_by_current_user = true;
//		}
//
//		if (AuthorFollowLoginUser != null) {
//			is_following_current_user = true;
//		}
//		JSONObject avatarImageJson = null;
//		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
//		}
//
//		JSONObject coverImageJson = null;
//		if (!Strings.isNullOrEmpty(user.getCoverImage())) {
//			coverImageJson = JSONObject.fromObject(user.getCoverImage());
//		}
//		int storyCount = this.storyDao.getStoryCount((Long) user.getId());
//		int follower_Count = this.followDao.userFollowedCount((Long) user.getId());
//		int following_count = this.followDao.userFollowCount((Long) user.getId());
//		JSONObject authorJson = new JSONObject();
//		authorJson.put("id", user.getId());
//		authorJson.put("username", user.getUsername());
//		authorJson.put("email", user.getEmail());
//		authorJson.put("created_time", user.getCreated_time());
//		authorJson.put("status", user.getStatus());
//		authorJson.put("introduction", user.getIntroduction());
//		authorJson.put("avatar_image", avatarImageJson);
//		authorJson.put("cover_image", coverImageJson);
//		authorJson.put("likes_count", Integer.valueOf(likesCount));
//		authorJson.put("reposts_count", Integer.valueOf(repostStoryCount));
//		authorJson.put("stories_count", Integer.valueOf(storyCount));
//		authorJson.put("followers_count", Integer.valueOf(follower_Count));
//		authorJson.put("following_count", Integer.valueOf(following_count));
//		if (!Strings.isNullOrEmpty(user.getWebsite()))
//			authorJson.put("website", user.getWebsite());
//		else {
//			authorJson.put("website", null);
//		}
//
//		authorJson.put("followed_by_current_user", Boolean.valueOf(followed_by_current_user));
//		authorJson.put("is_following_current_user", Boolean.valueOf(is_following_current_user));
//
//		storyModel.setSummary(story.getSummary());
//		storyModel.setAuthor(authorJson);
//		storyModel.setCreated_time(story.getCreated_time());
//		storyModel.setUpdate_time(story.getUpdate_time());
//
//		JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
//		String type = jsonObject.getString("type");
//		CoverMedia coverMedia = null;
//		if (type.equals("text")) {
//			coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
//			storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//		} else if (type.equals("image")) {
//			coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
//			storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//		} else if (type.equals("multimedia")) {
//			storyModel.setCover_media(jsonObject);
//		}
//
//		List<StoryElement> seSet = story.getElements();
//		if ((seSet != null) && (seSet.size() > 0)) {
//			JSONObject content = null;
//			String types = "";
//			TextCover textCover = null;
//			ImageCover imageCover = null;
//			for (StoryElement element : seSet) {
//				content = JSONObject.fromObject(element.getContents());
//
//				types = content.getString("type");
//				if (types.equals("text")) {
//					textCover = (TextCover) JSONObject.toBean(content, TextCover.class);
//					log.debug("*** element TextCover type ***" + textCover.getType());
//					element.setContent(textCover);
//				} else if (types.equals("image")) {
//					imageCover = (ImageCover) JSONObject.toBean(content, ImageCover.class);
//					log.debug("*** element ImageCover type ***" + imageCover.getType());
//					element.setContent(imageCover);
//				} else if (types.equals("location")) {
//					LocationModel locationModel = (LocationModel) JSONObject.toBean(content, LocationModel.class);
//					element.setContent(locationModel);
//				} else if (types.equals("link")) {
//					String media = content.getString("media");
//					JSONObject mediaJSON = JSONObject.fromObject(media);
//					if (mediaJSON.containsKey("image")) {
//						LinkModel linkModel = (LinkModel) JSONObject.toBean(content, LinkModel.class);
//						element.setContent(linkModel);
//					} else {
//						LinkModels linkModel = (LinkModels) JSONObject.toBean(content, LinkModels.class);
//						element.setContent(linkModel);
//					}
//
//				}
//				storyElements.add(element);
//			}
//		}
//
//		JsonConfig config = new JsonConfig();
//		config.setExcludes(new String[] { "storyinfo", "contents" });
//		config.setIgnoreDefaultExcludes(false);
//		config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//		log.debug("***get Elements *****" + JSONArray.fromObject(story.getElements(), config));
//		storyModel.setElements(JSONArray.fromObject(storyElements, config));
//
//		storyModel.setCommnents_enables(story.getComments_enabled());
//		if (!Strings.isNullOrEmpty(story.getTinyURL()))
//			storyModel.setUrl(story.getTinyURL());
//		else {
//			storyModel.setUrl(null);
//		}
//
//		storyModel.setView_count(story.getViewTimes());
//		storyModel.setTitle(story.getTitle());
//
//		int count = this.commentDao.getCommentCountById((Long) story.getId());
//		storyModel.setComment_count(count);
//		Likes likes = this.likesDao.getLikeByUserIdAndStoryId(loginUserid, (Long) story.getId());
//		if (likes != null)
//			storyModel.setLiked_by_current_user(true);
//		else {
//			storyModel.setLiked_by_current_user(false);
//		}
//		int likeCount = this.likesDao.likeStoryCount((Long) story.getId());
//		storyModel.setLike_count(likeCount);
//		int repostCount = this.republishDao.count((Long) story.getId());
//		storyModel.setRepost_count(repostCount);
//		Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, (Long) story.getId());
//		if (repost != null)
//			storyModel.setRepost_by_current_user(true);
//		else {
//			storyModel.setRepost_by_current_user(false);
//		}
//
//		return storyModel;
//	}
//
//	public Response createSlide(Long loginUserid, JSONObject slide) throws Exception {
//		Slide slideModel = new Slide();
//		String type = slide.getString("type");
//		JSONObject json = new JSONObject();
//		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
//		JSONObject json1 = ParseFile.parseJson(path); 
//		String appId = json1.getString("appId"); 
//		String appKey = json1.getString("appKey");
//		String masterSecret = json1.getString("masterSecret");
//		Configuration conf = null;
//		Story s = null;
//		List<PushNotification> pnList = new ArrayList<PushNotification>();
//		if (!Strings.isNullOrEmpty(type)) {
//			if (type.equals("url")) {
//				slideModel.setAuthorId(loginUserid);
//				slideModel.setType(type);
//				slideModel.setSlide_image(slide.getString("slide_image"));
//				JSONObject url = JSONObject.fromObject(slide.getString("slide"));
//				slideModel.setUrl(url.getString("url"));
//				slideModel.setGroup(slide.getString("group"));
//				slideModel.setSequence(slide.getInt("sequence"));
//				slideModel.setStatus("enabled");
//				this.slideDao.save(slideModel);
//			} else if (type.equals("story")) {
//
//				slideModel.setAuthorId(loginUserid);
//				JSONObject story = JSONObject.fromObject(slide.getString("slide"));
//				slideModel.setReference_id(story.getLong("story_id"));
//
//				slideModel.setSlide_image(slide.getString("slide_image"));
//				slideModel.setGroup(slide.getString("group"));
//				slideModel.setType(type);
//				slideModel.setSequence(slide.getInt("sequence"));
//				slideModel.setStatus("enabled");
//				this.slideDao.save(slideModel);
//				s = storyDao.get(story.getLong("story_id"));
//				conf = configurationDao.getConfByUserId(s.getUser().getId());
//				if (conf.isRecommended_my_story_push()) {
//					List<PushNotification> list = this.pushNotificationDao
//							.getPushNotificationByUserid(s.getUser().getId());
//					pnList.addAll(list);
//				}
//				Map<String, Integer> map = new HashMap<String, Integer>();
//				if ((pnList != null) && (pnList.size() > 0)) {
//					for (PushNotification pn : pnList) {
//						int count = this.notificationDao.getNotificationByRecipientId(pn.getUserId());
//						map.put(pn.getClientId(), Integer.valueOf(count));
//					}
//				}
//				
//				Notification n = new Notification();
//				n.setRecipientId(s.getUser().getId());
//				n.setSenderId(loginUserid);
//				n.setNotificationType(16);//�Ƽ����ֲ�ͼ
//				n.setObjectType(1);
//				n.setObjectId(s.getId());
//				n.setStatus("enabled");
//				n.setRead_already(true);
//				notificationDao.save(n);
//				
//				conf = this.configurationDao.getConfByUserId(s.getUser().getId());
//				if (conf.isRecommended_my_story_slide_push()) {
//					int counts = 1;
//					List<PushNotification> list = this.pushNotificationDao
//							.getPushNotificationByUserid(s.getUser().getId());
//					try {
//						JSONObject j = new JSONObject();
//
//						String content = "���Ĺ��±��Ƽ����ֲ�ͼ";
//						j.put("story_id", s.getId());
//
//						PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content, j.toString());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				/*
//				 * String content = "";
//				 * if(slide.getString("group").equals("homepage")){ content =
//				 * "���Ĺ����ѱ��Ƽ�����ҳ"; }else
//				 * if(slide.getString("group").equals("discover")){ content =
//				 * "���Ĺ����ѱ��Ƽ�������ҳ"; }else
//				 * if(slide.getString("group").equals("time_square")){ content =
//				 * "���Ĺ����ѱ��Ƽ����㳡"; } JSONObject j = new JSONObject();
//				 * j.put("story_id",s.getId());
//				 * PushNotificationUtil.pushInfoAllFollow(appId, appKey,
//				 * masterSecret, pnList, map, content,j.toString());
//				 */
//			} else if (type.equals("collection")) {
//
//				slideModel.setAuthorId(loginUserid);
//				JSONObject collection = JSONObject.fromObject(slide.getString("slide"));
//				slideModel.setReference_id(collection.getLong("collection_id"));
//
//				slideModel.setSlide_image(slide.getString("slide_image"));
//				slideModel.setGroup(slide.getString("group"));
//				slideModel.setType(type);
//				slideModel.setSequence(slide.getInt("sequence"));
//				slideModel.setStatus("enabled");
//				this.slideDao.save(slideModel);
//				Collection c = null;//collectionDao.get(collection.getLong("collection_id"));
//				conf = configurationDao.getConfByUserId(0L);
//				if (conf.isRecommended_my_story_push()) {
//					List<PushNotification> list = this.pushNotificationDao
//							.getPushNotificationByUserid(0L);
//					pnList.addAll(list);
//				}
//				Map<String, Integer> map = new HashMap<String, Integer>();
//				if ((pnList != null) && (pnList.size() > 0)) {
//					for (PushNotification pn : pnList) {
//						int count = this.notificationDao.getNotificationByRecipientId(pn.getUserId());
//						map.put(pn.getClientId(), Integer.valueOf(count));
//					}
//				}
//				/*
//				 * String content = "";
//				 * if(slide.getString("group").equals("homepage")){ content =
//				 * "����Сվ�ѱ��Ƽ�����ҳ"; }else
//				 * if(slide.getString("group").equals("discover")){ content =
//				 * "����Сվ�ѱ��Ƽ�������ҳ"; }else
//				 * if(slide.getString("group").equals("time_square")){ content =
//				 * "����Сվ�ѱ��Ƽ����㳡"; }
//				 * 
//				 * JSONObject j = new JSONObject();
//				 * j.put("collection_id",c.getId());
//				 * PushNotificationUtil.pushInfoAllFollow(appId, appKey,
//				 * masterSecret, pnList, map, content,j.toString());
//				 */
//			}
//
//		} else {
//			json.put("status", "invalid_request");
//			json.put("code", Integer.valueOf(10010));
//			json.put("error_message", "Invalid payload parameters");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//		json.put("status", "success");
//		return Response.status(Response.Status.OK).entity(json).build();
//	}
//
//	public Response reportStory(Long storyId, Long loginUserid) {
//		Story story = (Story) this.storyDao.get(storyId);
//		JSONObject json = new JSONObject();
//		if (story != null) {
//			story.setStatus("disabled");
//			notificationDao.disableNotification(1, storyId);
//			this.storyDao.update(story);
//			this.timelineDao.deleteTimelineByStoryIdAndType(storyId);
//			Report report = this.reportDao.getReportByStoryId(storyId);
//			if (report != null) {
//				report.setStatus("confirmed");
//				report.setOperator_id(loginUserid);
//				this.reportDao.update(report);
//			}
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public Response reportComment(Long commentId, Long loginUserid) {
//		Comment comment = (Comment) this.commentDao.get(commentId);
//		JSONObject json = new JSONObject();
//		if (comment != null) {
//			comment.setStatus("disabled");
//			this.commentDao.update(comment);
//			Report report = this.reportDao.getReportByCommentId(commentId);
//			if (report != null) {
//				report.setStatus("confirmed");
//				report.setOperator_id(loginUserid);
//				this.reportDao.update(report);
//			} else {
//				json.put("status", "invalid_request");
//				json.put("code", Integer.valueOf(10010));
//				json.put("error_message", "Invalid payload parameters");
//				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//			}
//
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public Response confirmReport(Long reportId, Long loginUserid) {
//		Report report = (Report) this.reportDao.get(reportId);
//		JSONObject json = new JSONObject();
//		Story story = null;
//		Comment comment = null;
//		if (report != null) {
//			if (report.getType().equals("report_story")) {
//				story = (Story) this.storyDao.get(report.getObject_id());
//				story.setStatus("disabled");
//				this.storyDao.update(story);
//				this.timelineDao.deleteTimelineByStoryId((Long) story.getId());
//			} else if (report.getType().equals("report_comment")) {
//				comment = (Comment) this.commentDao.get(report.getObject_id());
//				comment.setStatus("disabled");
//				this.commentDao.update(comment);
//			}
//			this.reportDao.handleReport(report.getObject_id(), report.getType(), loginUserid, "confirmed");
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public Response ignoreReport(Long reportId, Long loginUserid) {
//		Report report = (Report) this.reportDao.get(reportId);
//		JSONObject json = new JSONObject();
//		if (report != null) {
//			this.reportDao.handleReport(report.getObject_id(), report.getType(), loginUserid, "ignored");
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public List<ReportModel> getReports(HttpServletRequest request) {
//		String countStr = request.getParameter("count");
//		String sinceIdStr = request.getParameter("since_id");
//		String maxIdStr = request.getParameter("max_id");
//		String filter_type = request.getParameter("filter_type");
//
//		int count = 20;
//		List<ReportModel> reportList = new ArrayList<ReportModel>();
//		ReportModel rm = null;
//		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			List<Report> list = this.reportDao.getReportsPage(count, filter_type);
//			if ((list != null) && (list.size() > 0))
//				for (Report r : list) {
//					rm = getReportModel(r);
//					reportList.add(rm);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			List<Report> list = this.reportDao.getReportsPage(count, filter_type);
//			if ((list != null) && (list.size() > 0))
//				for (Report r : list) {
//					rm = getReportModel(r);
//					reportList.add(rm);
//				}
//		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			List<Report> list = this.reportDao.getReportsPage(count, filter_type, since_id, 1);
//			if ((list != null) && (list.size() > 0))
//				for (Report r : list) {
//					rm = getReportModel(r);
//					reportList.add(rm);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			List<Report> list = this.reportDao.getReportsPage(count, filter_type, since_id, 1);
//			if ((list != null) && (list.size() > 0))
//				for (Report r : list) {
//					rm = getReportModel(r);
//					reportList.add(rm);
//				}
//		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			List<Report> list = this.reportDao.getReportsPage(count, filter_type, max_id, 2);
//			if ((list != null) && (list.size() > 0))
//				for (Report r : list) {
//					rm = getReportModel(r);
//					reportList.add(rm);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			List<Report> list = this.reportDao.getReportsPage(count, filter_type, max_id, 2);
//			if ((list != null) && (list.size() > 0)) {
//				for (Report r : list) {
//					rm = getReportModel(r);
//					reportList.add(rm);
//				}
//			}
//		}
//		return reportList;
//	}
//
//	public ReportModel getReportModel(Report report) {
//		ReportModel rm = new ReportModel();
//		if (report != null) {
//			rm.setId((Long) report.getId());
//			rm.setCreated_at(report.getCreate_time());
//			rm.setOperator_id(rm.getOperator_id());
//			rm.setStatus(report.getStatus());
//			rm.setType(report.getType());
//			JSONObject content = new JSONObject();
//			if (report.getObject_type() == 2) {
//				Comment comment = (Comment) this.commentDao.get(report.getObject_id());
//				CommentModel commentModel = getCommentModel(comment);
//				JSONObject commentJSON = JSONObject.fromObject(commentModel);
//				if (commentModel.getTarget_user() == null) {
//					commentJSON.remove("target_user");
//				}
//				content.put("comment", commentJSON);
//				Story story = comment.getStory();
//				StoryIntro si = new StoryIntro();
//				si.setId((Long) story.getId());
//				si.setCollectionId(Long.valueOf(1L));
//				si.setTitle(story.getTitle());
//				if (!Strings.isNullOrEmpty(story.getCover_page()))
//					si.setCover_media(JSONObject.fromObject(story.getCover_page()));
//				else {
//					si.setCover_media(null);
//				}
//				content.put("story", si);
//			} else if (report.getObject_type() == 1) {
//				StoryIntro si = new StoryIntro();
//				Story story = (Story) this.storyDao.get(report.getObject_id());
//				si.setId((Long) story.getId());
//				si.setCollectionId(Long.valueOf(1L));
//				si.setTitle(story.getTitle());
//				if (!Strings.isNullOrEmpty(story.getCover_page()))
//					si.setCover_media(JSONObject.fromObject(story.getCover_page()));
//				else {
//					si.setCover_media(null);
//				}
//				content.put("story", si);
//			}
//			rm.setContent(content);
//		}
//
//		return rm;
//	}
//
//	public CommentModel getCommentModel(Comment comment) {
//		CommentModel commentModel = new CommentModel();
//		JSONObject userIntro = new JSONObject();
//		commentModel.setId((Long) comment.getId());
//		commentModel.setContent(comment.getContent());
//		commentModel.setCreated_time(comment.getCreated_time());
//		commentModel.setStory_id((Long) comment.getStory().getId());
//		User user = comment.getUser();
//
//		userIntro.put("id", user.getId());
//		userIntro.put("username", user.getUsername());
//		JSONObject avatarJson = null;
//		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//			avatarJson = JSONObject.fromObject(user.getAvatarImage());
//		}
//		userIntro.put("avatar_image", avatarJson);
//		commentModel.setAuthor(userIntro);
//		if (comment.getTarget_user_id() != null) {
//			User targetUser = (User) this.userDao.get(comment.getTarget_user_id());
//			JSONObject targetUserJson = new JSONObject();
//			targetUserJson.put("id", targetUser.getId());
//			targetUserJson.put("username", targetUser.getUsername());
//			commentModel.setTarget_user(targetUserJson);
//		}
//
//		return commentModel;
//	}
//
//	public Response revokeReport(Long reportId, Long loginUserid) {
//		Report report = (Report) this.reportDao.get(reportId);
//		JSONObject json = new JSONObject();
//		if (report != null) {
//			this.reportDao.handleReport(report.getObject_id(), report.getType(), loginUserid, "new");
//			Story story = (Story) this.storyDao.get(report.getObject_id());
//			story.setStatus("publish");
//			this.storyDao.update(story);
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public List<CollectionIntro> getCollections() {
//		List<Collection> collectionList = this.collectionDao.getCollections();
//		List<CollectionIntro> collectionModelList = new ArrayList<CollectionIntro>();
//		if ((collectionList != null) && (collectionList.size() > 0)) {
//			CollectionIntro cm = null;
//			for (Collection c : collectionList) {
//				cm = new CollectionIntro();
////				cm.setId((Long) c.getId());
////				cm.setCollection_name(c.getCollectionName());
//				if (!Strings.isNullOrEmpty(c.getCover_image()))
//					cm.setCover_image(JSONObject.fromObject(c.getCover_image()));
//				else {
//					cm.setCover_image(null);
//				}
//
////				cm.setInfo(c.getInfo());
//				Set<Story> sSet = null;//c.getStories();
//				cm.setStory_count(sSet.size());
//				collectionModelList.add(cm);
//			}
//		}
//		return collectionModelList;
//	}
//
//	public Response addFeaturedUser(Long userId) {
//		JSONObject json = new JSONObject();
//		if (userId != null) {
//			FeatureUser featureUser = this.featureUserDao.getFeatureUserByUserid(userId);
//			if (featureUser == null) {
//				FeatureUser fu = new FeatureUser();
//				fu.setUserId(userId);
//				this.featureUserDao.save(fu);
//				json.put("status", "success");
//				return Response.status(Response.Status.CREATED).entity(json).build();
//			}
//			json.put("status", "resource_exsist");
//			json.put("code", Integer.valueOf(10089));
//			json.put("error_message", "The resource is exsist");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public Response removeFeaturedUser(Long userId) {
//		JSONObject json = new JSONObject();
//		if (userId != null) {
//			FeatureUser featureUser = this.featureUserDao.getFeatureUserByUserid(userId);
//			if (featureUser == null) {
//				json.put("status", "resource_not_exsist");
//				json.put("code", Integer.valueOf(10092));
//				json.put("error_message", "The resource is not exsist");
//				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//			}
//			this.featureUserDao.deleteFeatureUser(userId);
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public Response recommendStory(Long loginUserid, Long story_id, HttpServletRequest request) {
//		JSONObject json = new JSONObject();
//		Story story = this.storyDao.getStoryByIdAndStatus(story_id, "publish");
//		Timeline timeline = timelineDao.getTimelineByStoryIdAndType(story_id, "recommandation");
//		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
//		JSONObject jsonObject = ParseFile.parseJson(path);
//		String appId = jsonObject.getString("appId");
//		String appKey = jsonObject.getString("appKey");
//		String masterSecret = jsonObject.getString("masterSecret");
//		if (story != null) {
//			story.setRecommendation(true);
//
//			this.storyDao.update(story);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String date = sdf.format(new Date());
//			Date d = null;
//			try {
//				d = sdf.parse(date);
//			} catch (ParseException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			if (timeline == null) {
//				Timeline t = new Timeline();
//				t.setCreatorId(loginUserid);
//				t.setStory(story);
//				t.setTargetUserId(loginUserid);
//				t.setReferenceId(story_id);
//				t.setType("recommandation");
//				t.setCreateTime(d);
//				timelineDao.save(t);
//			} else {
//				timeline.setCreateTime(d);
//				timelineDao.update(timeline);
//			}
//			Notification n = new Notification();
//			n.setRecipientId(story.getUser().getId());
//			n.setSenderId(loginUserid);
//			n.setNotificationType(7);
//			n.setObjectType(1);
//			n.setObjectId(story.getId());
//			n.setStatus("enabled");
//			n.setRead_already(true);
//			notificationDao.save(n);
//
//			Configuration conf = this.configurationDao.getConfByUserId(story.getUser().getId());
//			if (conf.isRecommended_my_story_push()) {
//				int counts = 1;
//				List<PushNotification> list = this.pushNotificationDao
//						.getPushNotificationByUserid(story.getUser().getId());
//				try {
//					String content = "";
//					JSONObject json1 = new JSONObject();
//
//					content = "���Ĺ��±��Ƽ�����ҳ";
//					json1.put("story_id", story.getId());
//
//					PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content, json1.toString());
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//	
//	
//	@Override
//	public Response unrecommendStory(Long loginUserid, Long story_id, HttpServletRequest paramHttpServletRequest) {
//		JSONObject json = new JSONObject();
//		Timeline timeline = timelineDao.getTimelineByStoryIdAndType(story_id, "recommandation");
//		if(timeline != null){
//			timelineDao.delete(timeline.getId());
//		}
//		notificationDao.deleteNotificationByAction(story_id,loginUserid,1,7);
//		
//		json.put("status","success");
//		return Response.status(Response.Status.OK).entity(json).build();
//	}
//
//	public Response updateUserType(Long user_id, HttpServletRequest request) {
//		String user_type = request.getParameter("user_type");
//		JSONObject json = new JSONObject();
//		if (!Strings.isNullOrEmpty(user_type)) {
//			this.userDao.updateUserByUserType(user_id, user_type);
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public Response createPublisherInfo(Long user_id, JSONObject publisher) {
//		JSONObject json = new JSONObject();
//		User user = (User) this.userDao.get(user_id);
//		if (user != null) {
//			PublisherInfo pi = new PublisherInfo();
//			String type = null;
//			if (publisher.containsKey("type")) {
//				type = publisher.getString("type");
//			}
//			if (!Strings.isNullOrEmpty(type)) {
//				this.publisherInfoDao.deletePublisherInfo(user.getId(), type);
//				if (publisher.containsKey("content")) {
//					pi.setContent(publisher.getString("content"));
//				}
//				pi.setUser(user);
//				pi.setType(type);
//				this.publisherInfoDao.save(pi);
//			}
//
//			json.put("status", "success");
//			return Response.status(Response.Status.CREATED).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	public Response removePublisher(Long publisher_info_id) {
//		PublisherInfo pi = (PublisherInfo) this.publisherInfoDao.get(publisher_info_id);
//		JSONObject json = new JSONObject();
//		if (pi != null) {
//			this.publisherInfoDao.delete(publisher_info_id);
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//	}
//
//	@Override
//	public List<JSONObject> getFeedback(HttpServletRequest request) {
//		List<JSONObject> feedbackList = new ArrayList<JSONObject>();
//		String countStr = request.getParameter("count");
//		String sinceIdStr = request.getParameter("since_id");
//		String maxIdStr = request.getParameter("max_id");
//		int count = 20;
//		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			List<Feedback> feedList = feedbackDao.getFeedbackList(count);
//			FeedbackModel fm = null;
//			User user = null;
//			JSONObject feedJson = null;
//			if (feedList != null && feedList.size() > 0) {
//				for (Feedback feed : feedList) {
//					if (feed.getUser_id() > 0) {
//						user = userDao.get(feed.getUser_id());
//						fm = new FeedbackModel();
//						fm.setUser_id(user.getId());
//						fm.setUser_name(user.getUsername());
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					} else {
//						fm = new FeedbackModel();
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//						delArray.add("user_id");
//						delArray.add("user_name");
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					}
//
//				}
//			}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//
//			List<Feedback> feedList = feedbackDao.getFeedbackList(count);
//			FeedbackModel fm = null;
//			User user = null;
//			JSONObject feedJson = null;
//			if (feedList != null && feedList.size() > 0) {
//				for (Feedback feed : feedList) {
//					if (feed.getUser_id() > 0) {
//						user = userDao.get(feed.getUser_id());
//						fm = new FeedbackModel();
//						fm.setId(feed.getId());
//						fm.setUser_id(user.getId());
//						fm.setUser_name(user.getUsername());
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					} else {
//						fm = new FeedbackModel();
//						fm.setId(feed.getId());
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//						delArray.add("user_id");
//						delArray.add("user_name");
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					}
//
//				}
//			}
//
//		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//
//			List<Feedback> feedList = feedbackDao.getFeedbackList(count, since_id, 1);
//			FeedbackModel fm = null;
//			User user = null;
//			JSONObject feedJson = null;
//			if (feedList != null && feedList.size() > 0) {
//				for (Feedback feed : feedList) {
//					if (feed.getUser_id() > 0) {
//						user = userDao.get(feed.getUser_id());
//						fm.setId(feed.getId());
//						fm = new FeedbackModel();
//						fm.setUser_id(user.getId());
//						fm.setUser_name(user.getUsername());
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					} else {
//						fm = new FeedbackModel();
//						fm.setId(feed.getId());
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//						delArray.add("user_id");
//						delArray.add("user_name");
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					}
//
//				}
//			}
//
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//
//			List<Feedback> feedList = feedbackDao.getFeedbackList(count, since_id, 1);
//			FeedbackModel fm = null;
//			User user = null;
//			JSONObject feedJson = null;
//			if (feedList != null && feedList.size() > 0) {
//				for (Feedback feed : feedList) {
//					if (feed.getUser_id() > 0) {
//						user = userDao.get(feed.getUser_id());
//						fm = new FeedbackModel();
//						fm.setId(feed.getId());
//						fm.setUser_id(user.getId());
//						fm.setUser_name(user.getUsername());
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					} else {
//						fm = new FeedbackModel();
//						fm.setId(feed.getId());
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//						delArray.add("user_id");
//						delArray.add("user_name");
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					}
//
//				}
//			}
//		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//
//			List<Feedback> feedList = feedbackDao.getFeedbackList(count, max_id, 1);
//			FeedbackModel fm = null;
//			User user = null;
//			JSONObject feedJson = null;
//			if (feedList != null && feedList.size() > 0) {
//				for (Feedback feed : feedList) {
//					if (feed.getUser_id() > 0) {
//						user = userDao.get(feed.getUser_id());
//						fm = new FeedbackModel();
//						fm.setId(feed.getId());
//						fm.setUser_id(user.getId());
//						fm.setUser_name(user.getUsername());
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					} else {
//						fm = new FeedbackModel();
//						fm.setId(feed.getId());
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//						delArray.add("user_id");
//						delArray.add("user_name");
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					}
//
//				}
//			}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			List<Feedback> feedList = feedbackDao.getFeedbackList(count, max_id, 1);
//			FeedbackModel fm = null;
//			User user = null;
//			JSONObject feedJson = null;
//			if (feedList != null && feedList.size() > 0) {
//				for (Feedback feed : feedList) {
//					if (feed.getUser_id() > 0) {
//						user = userDao.get(feed.getUser_id());
//						fm = new FeedbackModel();
//						fm.setId(feed.getId());
//						fm.setId(feed.getId());
//						fm.setUser_id(user.getId());
//						fm.setUser_name(user.getUsername());
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					} else {
//						fm = new FeedbackModel();
//						fm.setId(feed.getId());
//						fm.setId(feed.getId());
//						fm.setCreate_time(feed.getCreated_time());
//						JsonConfig configs = new JsonConfig();
//						List<String> delArray = new ArrayList<String>();
//
//						if (!Strings.isNullOrEmpty(feed.getCover_image())) {
//							JSONObject json = JSONObject.fromObject(feed.getCover_image());
//							ImageMedia im = new ImageMedia();
//							im.setName(json.getString("name"));
//							im.setOriginal_size(json.getString("original_size"));
//							fm.setCover_image(im);
//						} else {
//							delArray.add("cover_image");
//						}
//
//						if (!Strings.isNullOrEmpty(feed.getInfo())) {
//							fm.setInfo(feed.getInfo());
//						} else {
//							delArray.add("info");
//						}
//						delArray.add("user_id");
//						delArray.add("user_name");
//
//						if ((delArray != null) && (delArray.size() > 0)) {
//							configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//							configs.setIgnoreDefaultExcludes(false);
//							configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//							feedJson = JSONObject.fromObject(fm, configs);
//						} else {
//							feedJson = JSONObject.fromObject(fm);
//						}
//
//						feedbackList.add(feedJson);
//					}
//
//				}
//			}
//		}
//
//		return feedbackList;
//	}
//
//	@Override
//	public Response allusernotification(JSONObject json) {
//		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
//		JSONObject json1 = ParseFile.parseJson(path);
//		String appId = json1.getString("appId");
//		String appKey = json1.getString("appKey");
//		String masterSecret = json1.getString("masterSecret");
//		if (json != null) {
//			List<PushNotification> pnList = pushNotificationDao.getPushNotification();
//			String content = json.getString("content");
//			JSONObject json2 = new JSONObject();
//			if (json.containsKey("url")) {
//				json2.put("url", json.getString("url"));
//			} else if (json.containsKey("story_id")) {
//				String storyId = json.getString("story_id");
//				Story story = storyDao.get(Long.parseLong(storyId));
//				JSONObject storyJson = new JSONObject();
//				storyJson.put("id", story.getId());
//				storyJson.put("title", story.getTitle());
//				storyJson.put("summary", story.getSummary());
//				User user = story.getUser();
//				JSONObject avatarImageJson = null;
//				if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//					avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
//				}
//
//				JSONObject authorJson = new JSONObject();
//				authorJson.put("id", user.getId());
//				authorJson.put("username", user.getUsername());
//				authorJson.put("introduction", user.getIntroduction());
//				authorJson.put("avatar_image", avatarImageJson);
//				authorJson.put("user_type", user.getUser_type());
//				storyJson.put("author", authorJson);
//				JSONObject coverJson = JSONObject.fromObject(story.getCover_page());
//				String type = coverJson.getString("type");
//				if (type.equals("image")) {
//					storyJson.put("cover_media", story.getCover_page());
//				} else if (type.equals("multimedia")) {
//					JSONObject contentJson = JSONObject.fromObject(coverJson.get("media"));
//					JSONArray contentArr = JSONArray.fromObject(contentJson.get("contents"));
//					Object obj = contentArr.get(0);
//					storyJson.put("cover_media", obj);
//				} else if (type.equals("video")) {
//					storyJson.put("cover_media", story.getCover_page());
//				}
//
//				json2.put("story", storyJson);
//			} else if (json.containsKey("user_id")) {
//				json2.put("user_id", json.getString("user_id"));
//			}
//			jobService.run(appId, appKey, masterSecret, pnList, content, json2.toString());
//			JSONObject successJSON = new JSONObject();
//			successJSON.put("status", "success");
//			return Response.status(Response.Status.OK).entity(successJSON).build();
//		} else {
//			JSONObject j = new JSONObject();
//			j.put("status", "invalid_request");
//			j.put("code", 10010);
//			j.put("error_message", "Invalid payload parameters");
//			return Response.status(Response.Status.OK).entity(j).build();
//		}
//
//	}
//
//	@Override
//	public Response delSlide(Long id) {
//		JSONObject json = new JSONObject();
//		if (id != null && id > 0) {
//			slideDao.delete(id);
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		} else {
//			json.put("status", "invalid_request");
//			json.put("code", 10010);
//			json.put("error_message", "Invalid payload parameters");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//	}
//
//	@Override
//	public Response updateCollection(Long collection_id) {
//		JSONObject json = new JSONObject();
//		if (collection_id != null) {
//			collectionDao.disableCollection(collection_id);
//			FeatureCollection featureCollection = this.featureCollectionDao
//					.getFeatureCollectionByCollectionId(collection_id);
//			if (featureCollection != null) {
//				this.featureCollectionDao.delFeatureCollection(collection_id);
//			}
//			this.collectionStoryDao.deleteCollectionStoryByCollectionId(collection_id);
//			userCollectionDao.delUserCollectionByCollectionId(collection_id);
//
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		} else {
//			json.put("status", "invalid_request");
//			json.put("code", 10010);
//			json.put("error_message", "Invalid payload parameters");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//	}
//
//	@Override
//	public Response addFeaturedCollection(Long collectionId) {
//
//		JSONObject json = new JSONObject();
//		if (collectionId != null) {
//			FeatureCollection featureCollection = this.featureCollectionDao
//					.getFeatureCollectionByCollectionId(collectionId);
//			if (featureCollection == null) {
//				FeatureCollection fc = new FeatureCollection();
//				fc.setCollectionId(collectionId);
//				fc.setSequnce(1);
//				this.featureCollectionDao.save(fc);
//				json.put("status", "success");
//				return Response.status(Response.Status.CREATED).entity(json).build();
//			}
//			json.put("status", "resource_exsist");
//			json.put("code", Integer.valueOf(10089));
//			json.put("error_message", "The resource is exsist");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//
//	}
//
//	@Override
//	public Response removeFeaturedCollection(Long collectionId) {
//
//		JSONObject json = new JSONObject();
//		if (collectionId != null) {
//			FeatureCollection featureCollection = this.featureCollectionDao
//					.getFeatureCollectionByCollectionId(collectionId);
//			if (featureCollection == null) {
//				json.put("status", "resource_not_exsist");
//				json.put("code", Integer.valueOf(10092));
//				json.put("error_message", "The resource is not exsist");
//				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//			}
//			this.featureCollectionDao.delFeatureCollection(collectionId);
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//
//		json.put("status", "invalid_request");
//		json.put("code", Integer.valueOf(10010));
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//
//	}
//
//	@Override
//	public void createException() {
//		try {
//			String name = null;
//			byte[] b = name.getBytes();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public Response createInterest(JSONObject json) {
//		if (json != null) {
//			Interest i = new Interest();
//			i.setInterest_name(json.getString("interest_name"));
//			if (!Strings.isNullOrEmpty(json.getString("cover_image"))) {
//				i.setCover_image(json.getString("cover_image"));
//			}
//
//			i.setDescription(json.getString("description"));
//			interestDao.save(i);
//			InterestModel im = new InterestModel();
//			im.setId(i.getId());
//			im.setCover_image(JSONObject.fromObject(i.getCover_image()));
//			im.setDescription(i.getDescription());
//			im.setInterest_name(i.getInterest_name());
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("status", "success");
//			return Response.status(Response.Status.OK).entity(jsonObject).build();
//		}
//		JSONObject resultError = new JSONObject();
//		resultError.put("status", "repetition_interest_name");
//		resultError.put("code", Integer.valueOf(10110));
//		resultError.put("error_message", "interest name is repetition");
//		return Response.status(Response.Status.BAD_REQUEST).entity(resultError).build();
//	}
//
//	@Override
//	public Response createColumns(Long loginUserid, JSONObject json) {
//
//		if (json != null) {
//			Columns c = new Columns();
//			c.setColumn_name(json.getString("column_name"));
//			c.setDescription(json.getString("description"));
//			if (!Strings.isNullOrEmpty(json.getString("cover_media"))) {
//				c.setCover_image(json.getString("cover_media"));
//			}
//
//			columnsDao.save(c);
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("status", "success");
//			return Response.status(Response.Status.OK).entity(jsonObject).build();
//		}
//		JSONObject resultError = new JSONObject();
//		resultError.put("status", "repetition_interest_name");
//		resultError.put("code", Integer.valueOf(10110));
//		resultError.put("error_message", "interest name is repetition");
//		return Response.status(Response.Status.BAD_REQUEST).entity(resultError).build();
//
//	}
//
//	@Override
//	public List<JSONObject> getAllInterest() {
//		List<Interest> iList = interestDao.getAll();
//		List<JSONObject> jsonList = new ArrayList<JSONObject>();
//		if (iList != null && iList.size() > 0) {
//			for (Interest i : iList) {
//				JSONObject iJson = new JSONObject();
//				iJson.put("id", i.getId());
//				iJson.put("interest_name", i.getInterest_name());
//				jsonList.add(iJson);
//			}
//		}
//		return jsonList;
//	}
//
//	@Override
//	public Response updateColumns(Long columns_id, JSONObject columns) {
//
//		if (columns_id != null && columns_id > 0) {
//			Columns c = columnsDao.get(columns_id);
//			if (columns.containsKey("column_name")) {
//				if (!Strings.isNullOrEmpty(columns.getString("column_name"))) {
//					c.setColumn_name(columns.getString("column_name"));
//				}
//
//			}
//
//			if (columns.containsKey("description")) {
//				c.setDescription(columns.getString("description"));
//			}
//
//			if (columns.containsKey("cover_media")) {
//				if (!Strings.isNullOrEmpty(columns.getString("cover_media"))) {
//					c.setCover_image(columns.getString("cover_media"));
//				}
//
//			}
//
//			columnsDao.update(c);
//			ColumnsModel cis = new ColumnsModel();
//			cis.setId(c.getId());
//			cis.setColumn_name(c.getColumn_name());
//			cis.setCover_media(JSONObject.fromObject(c.getCover_image()));
//			cis.setDescription(c.getDescription());
//			JSONObject json = JSONObject.fromObject(cis);
//			return Response.status(Response.Status.OK).entity(json).build();
//		} else {
//
//			JSONObject json = new JSONObject();
//			json.put("status", "no_resource");
//			json.put("code", 10011);
//			json.put("error_message", "The user does not exist");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//
//		}
//
//	}
//
//	@Override
//	public Response updateInterest(Long interest_id, JSONObject interest) {
//		JSONObject json = new JSONObject();
//		if (interest_id != null && interest_id > 0) {
//			Interest in = interestDao.get(interest_id);
//			if (interest.containsKey("interest_name")) {
//				if (!Strings.isNullOrEmpty(interest.getString("interest_name"))) {
//					in.setInterest_name(interest.getString("interest_name"));
//				}
//
//			}
//
//			if (interest.containsKey("description")) {
//				in.setDescription(interest.getString("description"));
//			}
//
//			if (interest.containsKey("cover_image")) {
//				if (!Strings.isNullOrEmpty(interest.getString("cover_image"))) {
//					in.setCover_image(interest.getString("cover_image"));
//				}
//
//			}
//
//			interestDao.update(in);
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		} else {
//
//			
//			json.put("status", "no_resource");
//			json.put("code", 10011);
//			json.put("error_message", "The user does not exist");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//
//		}
//
//	}
//
//	@Override
//	public Response createColumnsStory(Long columnsId, Long storyId,Long loginUserid) {
//		JSONObject json = new JSONObject();
//		if((columnsId != null && columnsId > 0) 
//				&& (storyId != null && storyId > 0)){
//			ColumnsStory cs = new ColumnsStory();
//			Columns c = columnsDao.get(columnsId);
//			Story s = storyDao.get(storyId);
//			cs.setColumns(c);
//			cs.setStory(s);
//			//cs.setCreate_time(new Date());
//			cs.setOperator(loginUserid);
//			columnsStoryDao.save(cs);
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}else{
//			json.put("status", "no_resource");
//			json.put("code", 10081);
//			json.put("error_message", "The data does not exist");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//	}
//
//	@Override
//	public Response deleteColumnsStory(Long columnsId, Long storyId) {
//		JSONObject json = new JSONObject();
//		if((columnsId != null && columnsId > 0) 
//				&& (storyId != null && storyId > 0)){
//			columnsStoryDao.deleteColumnsStoryByColumnsIdAndStoryId(columnsId, storyId);
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}else{
//			json.put("status", "no_resource");
//			json.put("code", 10081);
//			json.put("error_message", "The data does not exist");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//	}
//
//	@Override
//	public Response updateColumnsStory(Long columnsId, Long storyId,Long loginUserid) {
//		JSONObject json = new JSONObject();
//		if((columnsId != null && columnsId > 0) 
//				&& (storyId != null && storyId > 0)){
//			//ColumnsStory cStory = columnsStoryDao.getColumnsStoryByColumnsIdAndStoryId(columnsId,storyId);
//			List<ColumnsStory> csList = columnsStoryDao.getColumnsStoryListByStoryId(storyId);
//			ColumnsStory cs = new ColumnsStory();
//			if(csList != null && csList.size() > 0){
//				String ids = "";
//				Story story = csList.get(0).getStory();
//				for(ColumnsStory cs_ids:csList){
//					ids += cs_ids.getId()+",";
//				}
//				ids = ids.substring(0,ids.length()-1);
//				columnsStoryDao.deleteColumnsSotryList(ids);
//				
//				Columns c = columnsDao.get(columnsId);
//				cs.setColumns(c);
//				cs.setStory(story);
//				//cs.setCreate_time(new Date());
//				cs.setOperator(loginUserid);
//				columnsStoryDao.save(cs);
//			}else{
//				Columns c = columnsDao.get(columnsId);
//				Story s = storyDao.get(storyId);
//				cs.setColumns(c);
//				cs.setStory(s);
//				//cs.setCreate_time(new Date());
//				cs.setOperator(loginUserid);
//				columnsStoryDao.save(cs);
//			}
//			
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}else{
//			json.put("status", "no_resource");
//			json.put("code", 10081);
//			json.put("error_message", "The data does not exist");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//	}
//
//	@Override
//	public Response getConversion(HttpServletRequest request) {
//		return null;
//	}
//
//	@Override
//	public Response getLottery(Long storyId,int count) {
//		return null;
//	}
//
//	@Override
//	public Response adminLogin(JSONObject userJson, HttpServletRequest request) {
//		return null;
//	}

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
	public Response add_user(JSONObject user,HttpServletRequest request) {

		Admin u = new Admin();
		int centre_id = 0;
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = parseJson(urlkey);
		String url = jsonObject.getString("url");
		String device = request.getHeader("X-Tella-Request-Device");
		String fbToken = "";
		String fbname = "";
		try {
			if (user != null) {
				if (Strings.isNullOrEmpty(user.getString("username").trim())) {
					JSONObject jo = new JSONObject();
					jo.put("status", "username_null");
					jo.put("code",10032);
					jo.put("error_message", "Username is not null");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}

				JSONObject resp = new JSONObject();
				String username = user.getString("username");
				String password = user.getString("password");

				
			
				
				Map<String,String> registerParam = new HashMap<String, String>();
				registerParam.put("username", username);
				registerParam.put("password", password);
				registerParam.put("ip",ip);
				registerParam.put("device",device);
				registerParam.put("remember_me","0");
				
				String params = publicParam(registerParam);
				String register_result = HttpUtil.sendPostStr(url+"/customer/account/login-without-question", params);
				JSONObject reg_res_json = JSONObject.fromObject(register_result);
				int res_code = reg_res_json.getInt("code");
				if(res_code == 10000){
					JSONObject data = reg_res_json.getJSONObject("data");
					centre_id = data.getInt("userid");
					fbToken = data.getString("token");
					fbname = data.getString("username");
					
				}else if(res_code == 10001) {	
					resp.put("status", "缺少参数");
					resp.put("code", 10600);
					resp.put("error_message", "缺少参数");
					return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
				}else if(res_code == 10114) {	
					resp.put("status", "用户名已存在");
					resp.put("code", 10628);
					resp.put("error_message", "用户名已存在");
					return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
				}
				

				u.setUsername(user.getString("username"));
				u.setStatus("enable");
				
				if(centre_id > 0){
					u.setFb_id(centre_id);
					u.setType("admin");
					this.adminDao.save(u);
				}else{
					JSONObject jo = new JSONObject();
					jo.put("status", "注册失败，请验证后再试");
					jo.put("code", 10609);
					jo.put("error_message", "注册失败，请验证后再试");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
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
		json.put("id", u.getId());
		json.put("type", u.getType());
		json.put("username",fbname);
		return Response.status(Response.Status.CREATED).entity(json).build();
	
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
	public Response delete_user(Long admin_id) {
		JSONObject resp = new JSONObject();
		adminDao.delete(admin_id);
		resp.put("admin_id", admin_id);
		return Response.status(Response.Status.OK).entity(resp).build();
		
	}

	@Override
	public Response search(HttpServletRequest request) {
		String collection_name = request.getParameter("collection_name");
		String type = request.getParameter("type");
		String category_id = request.getParameter("category_id");
		List<JSONObject> collectionJsonList = new ArrayList<JSONObject>();
		List<Collection> collectionList = null;
		collectionList = collectionDao.getCollectionListByCollectionName(collection_name);
		if(collectionList != null && collectionList.size() > 0){
			for(Collection c:collectionList){
				JSONObject collectionJson = new JSONObject();
				if(!Strings.isNullOrEmpty(type) 
						&& type.equals("navigation")){
					Navigation nav = navigationDao.getNavigationByCollectionId(c.getId());
					if(nav != null){
						collectionJson.put("exist", 1);
					}else{
						collectionJson.put("exist", 0);
					}
				}else if(!Strings.isNullOrEmpty(category_id)){
					CategoryCollection cc = categoryCollectionDao.getCategoryCollectionByCategoryIdAndCollectionId(Long.parseLong(category_id), c.getId());
					if(cc != null){
						collectionJson.put("exist", 1);
					}else{
						collectionJson.put("exist", 0);
					}
				}
				
				collectionJson.put("id", c.getId());
				collectionJson.put("collection_name", c.getCollection_name());
				collectionJson.put("cover_image", JSONObject.fromObject(c.getCover_image()));
				collectionJson.put("type", c.getType());
				collectionJsonList.add(collectionJson);
			}
		}
		return Response.status(Response.Status.OK).entity(collectionJsonList).build();
	}

	@Override
	public Response add_collection_nav(JSONObject json) {
		JSONObject resp = new JSONObject();
		if(json != null){
			int collection_id = json.getInt("collection_id");
			Navigation nav = navigationDao.getNavigationByCollectionId(collection_id);
			if(nav != null){
				resp.put("status", "该栏目已存在导航栏中");
				resp.put("code", 10805);
				resp.put("error_message", "该栏目已存在导航栏中");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else{
				Navigation navigation = new Navigation(); 
				navigation.setCollection_id(collection_id);
				navigation.setSequence(1);
				navigationDao.save(navigation);
				Collection c = collectionDao.get(collection_id);
				JSONObject cJson = new JSONObject();
				cJson.put("id", c.getId());
				cJson.put("collection_name", c.getCollection_name());
				cJson.put("cover_image",JSONObject.fromObject(c.getCover_image()));
				cJson.put("exist", 1);
				cJson.put("type",c.getType());
				return Response.status(Response.Status.CREATED).entity(cJson).build();
			}
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response del_collection_nav(Long nav_id) {
		JSONObject resp = new JSONObject();
		if(nav_id != null){
			navigationDao.delete(nav_id);
			resp.put("nav_id", nav_id);
			return Response.status(Response.Status.OK).entity(resp).build();
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response add_car(JSONObject json,HttpServletRequest request)throws Exception {
		JSONObject resp = new JSONObject();
		if(json != null){
			String ip = request.getHeader("X-Real-IP");
			if(Strings.isNullOrEmpty(ip)){
				ip = request.getRemoteAddr();
			}
			
			String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
			JSONObject jsonObj = parseJson(urlkey);
			String url = jsonObj.getString("car_url");
			String device = request.getHeader("X-Tella-Request-Device");
			Map<String,String> collJson = new HashMap<String,String>();
			collJson.put("collection_name", json.getString("collection_name"));
			collJson.put("ip", ip);
			collJson.put("device", device);
			String param = carPublicParam(collJson);
			String result = HttpUtil.sendPostStr(url+"/collection/create", param);
			if(!Strings.isNullOrEmpty(result)){
				JSONObject res = JSONObject.fromObject(result);
				int code = res.getInt("code");
				if(code == 10000){
					JSONObject data = res.getJSONObject("data");
					int id = data.getInt("collection_id");
					Collection c = new Collection();
					c.setId(id);
					c.setCollection_name(json.getString("collection_name"));
					if(json.containsKey("description") 
							&& !Strings.isNullOrEmpty(json.getString("description"))){
						c.setDescription(json.getString("description"));
					}
					
					if(json.containsKey("car_url") 
							&& !Strings.isNullOrEmpty(json.getString("car_url"))){
						c.setCar_url(json.getString("car_url"));
					}
					
					c.setCover_image(json.getString("cover_image"));
					c.setThumbnail(json.getString("thumbnail"));
					c.setType(30);
					c.setStatus("enable");
					
					collectionDao.save(c);
					if(json.containsKey("clubs")){
						JSONArray arr = JSONArray.fromObject(json.get("clubs"));
						CarClub cc = null;
						if(arr != null && arr.size() > 0){
							for(Object o:arr){
								cc = new CarClub();
								int club_id = (Integer)o;
								cc.setCar_id(id);
								cc.setClub_id(club_id);
								carClubDao.save(cc);
							}
						}
					}
					
					resp.put("id", c.getId());
					resp.put("collection_name", c.getCollection_name());
					resp.put("type", c.getType());
					resp.put("car_url", c.getCar_url());
					resp.put("cover_image", JSONObject.fromObject(c.getCover_image()));
					resp.put("thumbnail", JSONObject.fromObject(c.getThumbnail()));
					return Response.status(Response.Status.CREATED).entity(resp).build();
				}else if(code == 16001){
					resp.put("status", "创建失败");
					resp.put("code", 10831);
					resp.put("error_message", "创建失败");
					return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
				}
			}else{
				resp.put("status", "请求失败");
				resp.put("code", 10832);
				resp.put("error_message", "请求失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
			
			return null;
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response add_club(JSONObject json,HttpServletRequest request)throws Exception {
		JSONObject resp = new JSONObject();
		if(json != null){
			String ip = request.getHeader("X-Real-IP");
			if(Strings.isNullOrEmpty(ip)){
				ip = request.getRemoteAddr();
			}
			String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
			JSONObject jsonObj = parseJson(urlkey);
			String url = jsonObj.getString("car_url");
			String device = request.getHeader("X-Tella-Request-Device");
			Map<String,String> collJson = new HashMap<String,String>();
			collJson.put("collection_name", json.getString("collection_name"));
			collJson.put("ip", ip);
			collJson.put("device", device);
			String param = carPublicParam(collJson);
			String result = HttpUtil.sendPostStr(url+"/collection/create", param);
			if(!Strings.isNullOrEmpty(result)){
				JSONObject res = JSONObject.fromObject(result);
				int code = res.getInt("code");
				if(code == 10000){
					JSONObject data = res.getJSONObject("data");
					int id = data.getInt("collection_id");
					Collection c = new Collection();
					c.setId(id);
					c.setCollection_name(json.getString("collection_name"));
					c.setDescription(json.getString("description"));
					c.setCover_image(json.getString("cover_image"));
					c.setThumbnail(json.getString("thumbnail"));
					c.setType(60);
					c.setStatus("enable");
					c.setLogo(json.getString("logo"));
					Districts d = districtsDao.get(json.getLong("area"));
					c.setDistricts(d);
					collectionDao.save(c);
					return Response.status(Response.Status.CREATED).entity(c).build();
				}else if(code == 16001){
					resp.put("status", "创建失败");
					resp.put("code", 10831);
					resp.put("error_message", "创建失败");
					return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
				}
			}else{
				resp.put("status", "请求失败");
				resp.put("code", 10832);
				resp.put("error_message", "请求失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
			
			return null;
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response nav_list(HttpServletRequest request) {
		List<JSONObject> navJsonList = new ArrayList<JSONObject>();
		JSONObject navJson = null;
		List<Navigation> navList = navigationDao.getNavigationBySequence();
		Collection c = null;
		if(navList != null && navList.size() > 0){
			for(Navigation nav:navList){
				navJson = new JSONObject();
				navJson.put("id", nav.getId());
				navJson.put("collection_id", nav.getCollection_id());
				c = collectionDao.get(nav.getCollection_id());
				navJson.put("collection_name", c.getCollection_name());
				navJson.put("collection_type",c.getType());
				navJson.put("cover_image", JSONObject.fromObject(c.getCover_image()));
				navJson.put("sequence", nav.getSequence());
				navJsonList.add(navJson);
			}
		}
		return Response.status(Response.Status.OK).entity(navJsonList).build();
	}

	@Override
	public Response nav_sequence(JSONArray arr) {
		JSONObject resp = new JSONObject();
		if(arr != null && arr.size() > 0){
			for(Object o:arr){
				JSONObject json = JSONObject.fromObject(o);
				Long id = json.getLong("id");
				int seq = json.getInt("sequence");
				navigationDao.updateNavigationSequence(id, seq);
			}
			resp.put("status", "success");
			return Response.status(Response.Status.CREATED).entity(resp).build();
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response add_column(JSONObject json,HttpServletRequest request)throws Exception {
		JSONObject resp = new JSONObject();
		if(json != null){
			String ip = request.getHeader("X-Real-IP");
			if(Strings.isNullOrEmpty(ip)){
				ip = request.getRemoteAddr();
			}
			String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
			JSONObject jsonObj = parseJson(urlkey);
			String url = jsonObj.getString("car_url");
			String device = request.getHeader("X-Tella-Request-Device");
			Map<String,String> collJson = new HashMap<String,String>();
			collJson.put("collection_name", json.getString("collection_name"));
			collJson.put("ip", ip);
			collJson.put("device", device);
			String param = carPublicParam(collJson);
			String result = HttpUtil.sendPostStr(url+"/collection/create", param);
			if(!Strings.isNullOrEmpty(result)){
				JSONObject res = JSONObject.fromObject(result);
				int code = res.getInt("code");
				if(code == 10000){
					JSONObject data = res.getJSONObject("data");
					int id = data.getInt("collection_id");
					Collection c = new Collection();
					c.setId(id);
					c.setCollection_name(json.getString("collection_name"));
					c.setDescription(json.getString("description"));
					c.setCover_image(json.getString("cover_image"));
					c.setThumbnail(json.getString("thumbnail"));
					c.setType(50);
					c.setStatus("enable");
					collectionDao.save(c);
//					resp.put("status", "success");
					return Response.status(Response.Status.CREATED).entity(c).build();
				}else if(code == 16001){
					resp.put("status", "创建失败");
					resp.put("code", 10831);
					resp.put("error_message", "创建失败");
					return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
				}
			}else{
				resp.put("status", "请求失败");
				resp.put("code", 10832);
				resp.put("error_message", "请求失败");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
			
			return null;
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response update_car(JSONObject json,HttpServletRequest request)throws Exception {
		JSONObject resp = new JSONObject();
		if(json != null){
			int id = json.getInt("id");
			List<JSONObject> clubJsonList = new ArrayList<JSONObject>();
			Collection car = collectionDao.get(id);
			if(json.containsKey("status")){
				car.setStatus("disable");
			}else{
				car.setCollection_name(json.getString("collection_name"));
				
				if(json.containsKey("description") 
						&& !Strings.isNullOrEmpty(json.getString("description"))){
					car.setDescription(json.getString("description"));
				}
				
				if(json.containsKey("car_url") 
						&& !Strings.isNullOrEmpty(json.getString("car_url"))){
					car.setCar_url(json.getString("car_url"));
				}
				car.setCover_image(json.getString("cover_image"));
				car.setThumbnail(json.getString("thumbnail"));
				carClubDao.deleteCarClubByCar_id(id);
				JSONArray arr = JSONArray.fromObject(json.get("clubs"));
				CarClub cc = null;
				
				JSONObject clubJson = null;
				if(arr != null && arr.size() > 0){
					for(Object o:arr){
						clubJson = new JSONObject();
						cc = new CarClub();
						int club_id = Integer.parseInt(o.toString());
						cc.setCar_id(id);
						cc.setClub_id(club_id);
						carClubDao.save(cc);
						Collection c = collectionDao.get(club_id);
						clubJson.put("id", c.getId());
						clubJson.put("collection_name", c.getCollection_name());
						clubJsonList.add(clubJson);
					}
				}
			}
			collectionDao.update(car);
//			resp.put("status", "success");
			resp.put("id", car.getId());
			resp.put("collection_name", car.getCollection_name());
			resp.put("cover_image", JSONObject.fromObject(car.getCover_image()));
			resp.put("type", car.getType());
			resp.put("description", car.getDescription());
			resp.put("status", car.getStatus());
			resp.put("car_url", car.getCar_url());
			resp.put("clubs", clubJsonList);
			return Response.status(Response.Status.CREATED).entity(resp).build();
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response update_club(JSONObject json,HttpServletRequest request) {
		JSONObject resp = new JSONObject();
		if(json != null){
			int id = json.getInt("id");
			Collection club = collectionDao.get(id);
			if(json.containsKey("status")){
				club.setStatus("disable");
			}else{
				club.setCollection_name(json.getString("collection_name"));
				club.setDescription(json.getString("description"));
				club.setCover_image(json.getString("cover_image"));
				club.setThumbnail(json.getString("thumbnail"));
				club.setLogo(json.getString("logo"));
				if(json.containsKey("area")){
					Districts d = districtsDao.get(json.getLong("area"));
					club.setDistricts(d);
				}
				
			}
			
			collectionDao.update(club);
			resp.put("id", club.getId());
			resp.put("collection_name", club.getCollection_name());
			resp.put("cover_image", JSONObject.fromObject(club.getCover_image()));
			resp.put("type", club.getType());
			resp.put("description", club.getDescription());
			resp.put("status", club.getStatus());
			Districts d = club.getDistricts();
			Districts di = districtsDao.get(d.getParent_id());
			JSONObject area = new JSONObject();
			area.put("id", d.getId());
			area.put("area_name", di.getName()+d.getName());
			resp.put("area", area);
			resp.put("logo", club.getLogo());
			return Response.status(Response.Status.CREATED).entity(resp).build();
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response update_column(JSONObject json,HttpServletRequest request) {
		JSONObject resp = new JSONObject();
		if(json != null){
			int id = json.getInt("id");
			Collection collection = collectionDao.get(id);
			if(json.containsKey("status")){
				collection.setStatus("disable");
			}else{
				collection.setCollection_name(json.getString("collection_name"));
				collection.setDescription(json.getString("description"));
				collection.setCover_image(json.getString("cover_image"));
				collection.setThumbnail(json.getString("thumbnail"));
			}
			
			collectionDao.update(collection);
			resp.put("status", collection.getStatus());
			resp.put("id", collection.getId());
			resp.put("collection_name", collection.getCollection_name());
			resp.put("cover_image", JSONObject.fromObject(collection.getCover_image()));
			resp.put("thumbnail", JSONObject.fromObject(collection.getThumbnail()));
			resp.put("description", collection.getDescription());
			resp.put("type",collection.getType());
			
			return Response.status(Response.Status.CREATED).entity(resp).build();
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response add_category(JSONObject json) {
		JSONObject resp = new JSONObject();
		if(json != null){
			Category category = new Category();
			category.setName(json.getString("name"));
			category.setSequence(1);
			category.setStatus("enable");
			categoryDao.save(category);
			return Response.status(Response.Status.CREATED).entity(category).build();
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response update_category(JSONObject json) {
		JSONObject resp = new JSONObject();
		if(json != null){
			Long id = json.getLong("id");
			Category category = categoryDao.get(id);
			if(json.containsKey("status")){
				category.setStatus(json.getString("status"));
			}else if(json.containsKey("name")){
				category.setName(json.getString("name"));
			}else if(json.containsKey("is_online")){
				boolean is_online = json.getBoolean("is_online");
				category.setIs_online(is_online);
			}
			categoryDao.update(category);
			resp.put("id", category.getId());
			resp.put("name", category.getName());
			resp.put("is_online", category.isIs_online());
			resp.put("sequence", category.getSequence());
			return Response.status(Response.Status.OK).entity(resp).build();
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response update_sequence(JSONArray arr) {
		JSONObject resp = new JSONObject();
		if(arr != null && arr.size() > 0){
			for(Object o:arr){
				JSONObject json = JSONObject.fromObject(o);
				if(json != null){
					Long id = json.getLong("id");
					int sequence = json.getInt("sequence");
					categoryDao.updateCategorySequence(id, sequence);
				}
			}
			resp.put("status", "success");
			return Response.status(Response.Status.OK).entity(resp).build();
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response add_category_collection(JSONObject json) {

		JSONObject resp = new JSONObject();
		if(json != null){
			Long category_id = json.getLong("category_id");
			int collection_id = json.getInt("collection_id");
			String type = json.getString("type");
			CategoryCollection cc = new CategoryCollection();
			Category category = categoryDao.get(category_id);
			Collection collection = collectionDao.get(collection_id);
			cc.setCategory(category);
			cc.setCollection(collection);
			cc.setSequence(1);
			cc.setType(type);
			categoryCollectionDao.save(cc);
			Collection c = collectionDao.get(collection_id);
			return Response.status(Response.Status.CREATED).entity(c).build();
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	
	}

	@Override
	public Response update_category_collection_sequence(JSONArray arr) {
		JSONObject resp = new JSONObject();
		if(arr != null && arr.size() > 0){
			for(Object o:arr){
				JSONObject json = JSONObject.fromObject(o);
				if(json != null){
					Long category_id = json.getLong("category_id");
					int collection_id = json.getInt("collection_id");
					int sequence = json.getInt("sequence");
					categoryCollectionDao.updateCategoryCollectionSequence(category_id, sequence,collection_id);
				}
			}
			resp.put("status", "success");
			return Response.status(Response.Status.OK).entity(resp).build();
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response delete_category_collection(JSONObject json) {
		JSONObject resp = new JSONObject();
		if(json != null){
			int collection_id = json.getInt("collection_id");
			Long category_id = json.getLong("category_id");
			CategoryCollection cc = categoryCollectionDao.getCategoryCollectionByCategoryIdAndCollectionId(category_id, collection_id);
			categoryCollectionDao.delete(cc.getId());
			resp.put("collection_id", collection_id);
			return Response.status(Response.Status.OK).entity(resp).build();
		}else{
			resp.put("status", "请传入参数");
			resp.put("code", 10806);
			resp.put("error_message", "请传入参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response get_collection_content(JSONObject json,HttpServletRequest request)throws Exception {
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = parseJson(urlkey);
		String url = jsonObject.getString("url");
		String device = request.getHeader("X-Tella-Request-Device");
		Map<String,String> param = new HashMap<String, String>();
		param.put("ip", ip);
		JSONObject resp = new JSONObject();
		if(json != null){
			param.put("username", "");
			param.put("password", "");
			param.put("device", String.valueOf(""));
			if(device.equals("10")){
				param.put("remember_me", "1");
			}
			try {
				String params = publicParam(param);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Response get_collection_feature_content(JSONObject json, HttpServletRequest request) throws Exception {
		return null;
	}

	@Override
	public Response get_collection_all() {
		List<Collection> collectionList = collectionDao.getAll();
		List<JSONObject> collectionJsonList = new ArrayList<JSONObject>();
		JSONObject collectionJson = null;
		if(collectionList != null && collectionList.size() > 0){
			for(Collection c:collectionList){
				collectionJson = new JSONObject();
				collectionJson.put("id", c.getId());
				collectionJson.put("collection_name", c.getCollection_name());
				String cover_image = c.getCover_image();
				if(!Strings.isNullOrEmpty(cover_image)){
					collectionJson.put("cover_image", JSONObject.fromObject(cover_image));
				}
				
				String thumbnail = c.getThumbnail();
				if(!Strings.isNullOrEmpty(thumbnail)){
					collectionJson.put("thumbnail", JSONObject.fromObject(thumbnail));
				}
				
				collectionJson.put("sequence", c.getNumber());
				collectionJson.put("type", c.getType());
				collectionJson.put("status", c.getStatus());
				collectionJsonList.add(collectionJson);
			}
		}
		return Response.status(Response.Status.OK).entity(collectionJsonList).build();
	}

	@Override
	public Response adminLogin(JSONObject adminJson, HttpServletRequest request) {

		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		String username = adminJson.containsKey("username")?adminJson.getString("username"):null;
		String password = adminJson.containsKey("password")?adminJson.getString("password"):null;
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String url = jsonObject.getString("url");
		String car_url = jsonObject.getString("car_url");
		JSONObject jo = new JSONObject();
		JSONObject auth = new JSONObject();
		int centre_id = 0;
		String fbToken = "";
		String user_type = "";
		
		Map<String,String> param = new HashMap<String, String>();
		param.put("ip", ip);
		if(!Strings.isNullOrEmpty(username)){

			if (Strings.isNullOrEmpty(password)) {
				jo.put("status", "invalid_password");
				jo.put("code", Integer.valueOf(10007));
				jo.put("error_message", "invalid password");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}

			


			try {
				param.put("username", username);
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
					String fbusername = data.getString("username");
					Map<String,String> createMap = new HashMap<String, String>();
					createMap.put("ip", ip);
					createMap.put("device", device);
					createMap.put("user_id", String.valueOf(centre_id));
					createMap.put("user_name", fbusername);
					String createParam = carPublicParam(createMap);
					String createResult = HttpUtil.sendPostStr(car_url+"/member/create", createParam);
					Admin admin = adminDao.getAdminByFbid(centre_id);
					
					if(admin.getStatus().equals("enable")){
						user_type = admin.getType();
					}else{
						jo.put("status", "用户不存在");
						jo.put("code", 10608);
						jo.put("error_message", "用户不存在");
						return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
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

			} catch (Exception e) {
				jo.put("status", "invalid_password");
				jo.put("code", Integer.valueOf(10007));
				jo.put("error_message", "invalid password");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}
		
		
			
			//---

			auth.put("userid", centre_id);
			auth.put("access_token", fbToken);
			auth.put("username", username);
			auth.put("type", user_type);
		}

		System.out.println(auth.toString());
		return Response.status(Response.Status.OK).entity(auth).build();
	
	}

	@Override
	public Response get_all_user() {
		List<Admin> adminList = adminDao.getAll();
		return Response.status(Response.Status.OK).entity(adminList).build();
	}
	
	@Override
	public Response content_status(JSONObject json, Long loginUserid, HttpServletRequest request) throws Exception {
		JSONObject jo = new JSONObject();
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String url = jsonObject.getString("car_url");
		if(loginUserid != null && loginUserid > 0){
			String content_id = json.getString("content_id");
			int status = json.getInt("status");
			Map<String,String> map = new HashMap<String,String>();
			map.put("user_id", loginUserid.toString());
			map.put("content_id", content_id);
			map.put("from_channel", "30");
			map.put("ip", ip);
			map.put("device", device);
			String param = carPublicParam(map);
			String result = "";
			if(status == 0){
				result = HttpUtil.sendPostStr(url+"/content/delete", param);
			}else if (status == 1){
				result = HttpUtil.sendPostStr(url+"/content/recover", param);
			}
			if(!Strings.isNullOrEmpty(result)){
				JSONObject resultJson = JSONObject.fromObject(result);
				int code = resultJson.getInt("code");
				if(code == 10000){
					jo.put("status", "success");
					return Response.status(Response.Status.OK).entity(jo).build();
				}else if(code == 10001){
					jo.put("status", "缺少参数");
					jo.put("code", 10600);
					jo.put("error_message", "缺少参数");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 14019){
					jo.put("status", "对象关系不存在");
					jo.put("code", 10650);
					jo.put("error_message", "对象关系不存在");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 14021){
					jo.put("status", "操作失败");
					jo.put("code", 10651);
					jo.put("error_message", "操作失败");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}
			}
		}else{
			jo.put("status", "操作失败");
			jo.put("code", 10651);
			jo.put("error_message", "操作失败");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
		return null;
	}

	@Override
	public Response add_feature(int content_id,Long loginUserid,HttpServletRequest request)throws Exception {
		JSONObject jo = new JSONObject();
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String url = jsonObject.getString("car_url");
		if(loginUserid != null && loginUserid > 0){
			Map<String,String> json = new HashMap<String,String>();
			json.put("user_id", loginUserid.toString());
			json.put("content_id", String.valueOf(content_id));
			json.put("ip", ip);
			json.put("device", device);
			String param = carPublicParam(json);
			String result = HttpUtil.sendPostStr(url+"/content/highlight", param);
			if(!Strings.isNullOrEmpty(result)){
				JSONObject resultJson = JSONObject.fromObject(result);
				int code = resultJson.getInt("code");
				if(code == 10000){
					jo.put("status", "success");
					return Response.status(Response.Status.OK).entity(jo).build();
				}else if(code == 10001){
					jo.put("status", "缺少参数");
					jo.put("code", 10600);
					jo.put("error_message", "缺少参数");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 14019){
					jo.put("status", "对象关系不存在");
					jo.put("code", 10650);
					jo.put("error_message", "对象关系不存在");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 14021){
					jo.put("status", "操作失败");
					jo.put("code", 10651);
					jo.put("error_message", "操作失败");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}else if(code == 14010){
					jo.put("status", "内容不存在");
					jo.put("code", 10652);
					jo.put("error_message", "内容不存在");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}
			}
		}else{
			jo.put("status", "操作失败");
			jo.put("code", 10651);
			jo.put("error_message", "操作失败");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
		return null;
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
	public Response category_list() {
		List<Category> list = categoryDao.getCategoryListBySequence();
		return Response.status(Response.Status.OK).entity(list).build();
	}

	@Override
	public Response category_collections(Long categoryId) {
		List<CategoryCollection> ccList = categoryCollectionDao.getCategoryCollectionByCategoryId(categoryId);
		List<JSONObject> cList = new ArrayList<JSONObject>();
		Collection collection = null;
		JSONObject collectionJson = null;
		if(ccList != null && ccList.size() > 0){
			for(CategoryCollection c:ccList){
				collection = collectionDao.get(c.getCollection().getId());
				collectionJson = new JSONObject();
				collectionJson.put("id", collection.getId());
				collectionJson.put("collection_name", collection.getCollection_name());
				int type = collection.getType();
				collectionJson.put("type", type);
				collectionJson.put("description", collection.getDescription());
				collectionJson.put("cover_image", JSONObject.fromObject(collection.getCover_image()));
				
				if(type == 30 || type == 50){
					if(!Strings.isNullOrEmpty(collection.getDescription())){
						collectionJson.put("description", collection.getDescription());
					}
				}else if(type == 60){
					collectionJson.put("logo",JSONObject.fromObject(collection.getLogo()));
					Districts d = collection.getDistricts();
					Districts d1 = districtsDao.get(d.getParent_id());
					JSONObject areaJson = new JSONObject();
					areaJson.put("id",d.getId());
					areaJson.put("area_name", d1.getName()+" "+d.getName());
					collectionJson.put("area",areaJson);
				}
				cList.add(collectionJson);
			}
		}
		return Response.status(Response.Status.OK).entity(cList).build();
	}

	@Override
	public Response get_collection_category(Long category_id) {
		List<Collection> coList = collectionDao.getCollections();
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		JSONObject collectionJson = null;
		if(coList != null && coList.size() > 0){
			for(Collection c:coList){
				collectionJson = new JSONObject();
				CategoryCollection cc = categoryCollectionDao.getCategoryCollectionByCategoryIdAndCollectionId(category_id, c.getId());
				if(cc != null){
					collectionJson.put("exist", 1);
				}else{
					collectionJson.put("exist", 0);
				}
				collectionJson.put("id", c.getId());
				collectionJson.put("collection_name", c.getCollection_name());
				collectionJson.put("type", c.getType());
				collectionJson.put("cover_image", JSONObject.fromObject(c.getCover_image()));
				jsonList.add(collectionJson);
			}
		}
		return Response.status(Response.Status.OK).entity(jsonList).build();
	}

	@Override
	public Response nav_collection_list() {

		List<Collection> coList = collectionDao.getCollections();
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		JSONObject collectionJson = null;
		Navigation nav = null;
		if(coList != null && coList.size() > 0){
			for(Collection c:coList){
				collectionJson = new JSONObject();
				nav = navigationDao.getNavigationByCollectionId(c.getId());
				if(nav != null){
					collectionJson.put("exist", 1);
				}else{
					collectionJson.put("exist", 0);
				}
				collectionJson.put("id", c.getId());
				collectionJson.put("collection_name", c.getCollection_name());
				collectionJson.put("type", c.getType());
				collectionJson.put("cover_image", JSONObject.fromObject(c.getCover_image()));
				jsonList.add(collectionJson);
			}
		}
		return Response.status(Response.Status.OK).entity(jsonList).build();
	
	}

	@Override
	public Response all_content(HttpServletRequest request) throws Exception {
		JSONObject resp = new JSONObject();
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("page_size");
		int count = 20;
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("ip", ip);
		map.put("device", device);
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
			map.put("page_num", String.valueOf(max_id));
		} else if ((!Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("page_num", String.valueOf(max_id));
		}
		String params = carPublicParam(map);
		String result = HttpUtil.sendGetStr(car_url+"/content/get-all-list", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){
				JSONObject newData = new JSONObject();
				JSONObject data = resJson.getJSONObject("data");
				newData.put("total", data.getString("total"));
				JSONArray arr = data.getJSONArray("list");
				JSONObject contentJson = null;
				List<JSONObject> cJsonList = new ArrayList<JSONObject>();
				if(arr != null && arr.size() > 0){
					for(Object o:arr){
						contentJson = JSONObject.fromObject(o);
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
						cJsonList.add(cJson);
					}
					
					newData.put("list", cJsonList);
				}
				System.out.println(newData.toString());
				return Response.status(Response.Status.OK).entity(newData).build();
			}else if (code == 14005){
				resp.put("status", "关系对象类型错误");
				resp.put("code", 10504);
				resp.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
		return null;
	}
	
	@Override
	public Response collection_content_all(HttpServletRequest request) throws Exception {

		JSONObject resp = new JSONObject();
		String collectionId = request.getParameter("collection_id");
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("page_size");
		String is_highlighted = request.getParameter("is_highlighted");
		int count = 20;
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		Map<String,String> map = new HashMap<String, String>();
		if(!Strings.isNullOrEmpty(collectionId)){
			map.put("data_id", collectionId);
			map.put("data_type", "50");
		}else{
			resp.put("status", "栏目数据错误");
			resp.put("code", 10534);
			resp.put("error_message", "栏目数据错误");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
		map.put("ip", ip);
		map.put("device", device);
		map.put("is_highlighted",is_highlighted);
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
			map.put("page_num", String.valueOf(max_id));
		} else if ((!Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("page_num", String.valueOf(max_id));
		}
		String params = carPublicParam(map);
		String result = HttpUtil.sendGetStr(car_url+"/content/get-all-list", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){
				JSONObject newData = new JSONObject();
				JSONObject data = resJson.getJSONObject("data");
				newData.put("total", data.getString("total"));
				JSONArray arr = data.getJSONArray("list");
				JSONObject contentJson = null;
				List<JSONObject> cJsonList = new ArrayList<JSONObject>();
				if(arr != null && arr.size() > 0){
					for(Object o:arr){
						contentJson = JSONObject.fromObject(o);
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
						cJsonList.add(cJson);
					}
					
					newData.put("list", cJsonList);
				}
				System.out.println(newData.toString());
				return Response.status(Response.Status.OK).entity(newData).build();
			}else if (code == 14005){
				resp.put("status", "关系对象类型错误");
				resp.put("code", 10504);
				resp.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
		return null;
	
	}


	@Override
	public Response feature_content(HttpServletRequest request) throws Exception {
		JSONObject resp = new JSONObject();
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("page_size");
		int count = 20;
		String path = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject carJson = ParseFile.parseJson(path);
		String car_url = carJson.getString("car_url");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String device = request.getHeader("X-Tella-Request-Device");
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("ip", ip);
		map.put("device", device);
		map.put("is_highlighted", "1");
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
			map.put("page_num", String.valueOf(max_id));
		} else if ((!Strings.isNullOrEmpty(countStr)) 
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("page_num", String.valueOf(max_id));
		}
		String params = carPublicParam(map);
		String result = HttpUtil.sendGetStr(car_url+"/content/get-all-list", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){

				JSONObject newData = new JSONObject();
				JSONObject data = resJson.getJSONObject("data");
				newData.put("total", data.getString("total"));
				JSONArray arr = data.getJSONArray("list");
				JSONObject contentJson = null;
				List<JSONObject> cJsonList = new ArrayList<JSONObject>();
				if(arr != null && arr.size() > 0){
					for(Object o:arr){
						contentJson = JSONObject.fromObject(o);
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
						cJsonList.add(cJson);
					}
					
					newData.put("list", cJsonList);
				}
				System.out.println(newData.toString());
				return Response.status(Response.Status.OK).entity(newData).build();
			}else if (code == 14005){
				resp.put("status", "关系对象类型错误");
				resp.put("code", 10504);
				resp.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
		return null;
	}

	@Override
	public Response update_user(Long admin_id, JSONObject user) {
		JSONObject resp = new JSONObject();
		Admin admin = adminDao.get(admin_id);
		if(admin != null){
			String type = user.getString("type");
			admin.setType(type);
			adminDao.update(admin);
			resp.put("status", "success");
			return Response.status(Response.Status.OK).entity(resp).build();
		}else{
			resp.put("status", "对象不存在");
			resp.put("code", 10536);
			resp.put("error_message", "对象不存在");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response car_club(JSONObject car_club, HttpServletRequest request) throws Exception {
		JSONObject resp = new JSONObject();
		if(car_club != null){
			CarClub cc = new CarClub();
			int car_id = car_club.getInt("car_id");
			int club_id = car_club.getInt("club_id");
			cc.setCar_id(car_id);
			cc.setClub_id(club_id);
			carClubDao.save(cc);
			resp.put("status", "success");
			return Response.status(Response.Status.OK).entity(resp).build();
		}else{
			resp.put("status", "参数不存在");
			resp.put("code", 10538);
			resp.put("error_message", "参数不存在");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
	}

	@Override
	public Response get_club(int car_id) throws Exception {
		List<Collection> cList = collectionDao.getCollectionBytype(60);
		JSONObject resp = new JSONObject();
		List<JSONObject> cJsonList = new ArrayList<JSONObject>();
		JSONObject cJson = null;
		List<Integer> idList = new ArrayList<Integer>();
		List<CarClub> carClub = carClubDao.getCarClubListByCar_id(car_id);
		if(carClub != null && carClub.size() > 0){
			for(CarClub cc:carClub){
				int id = cc.getClub_id();
				idList.add(id);
			}
		}
		resp.put("clubs",idList);
		if(cList != null && cList.size() > 0){
			for(Collection c:cList){
				cJson = new JSONObject();
				CarClub cc = carClubDao.getCarClubByClub_idAndCar_id(c.getId(), car_id);
				cJson.put("id", c.getId());
				cJson.put("collection_name",c.getCollection_name());
				cJson.put("logo",JSONObject.fromObject(c.getLogo()));
				if(cc != null){
					cJson.put("exists", 1);
				}else{
					cJson.put("exists", 0);
				}
				cJsonList.add(cJson);
			}
		}
		resp.put("club_obj", cJsonList);
		return Response.status(Response.Status.OK).entity(resp).build();
	}

	@Override
	public Response get_all_collection(int type) throws Exception {

		List<Collection> cList = collectionDao.getCollectionBytype(type);
		List<JSONObject> cJsonList = new ArrayList<JSONObject>();
		JSONObject cJson = null;
		JSONObject areaJson = null;
		if(cList != null && cList.size() > 0){
			for(Collection c:cList){
				cJson = new JSONObject();
				cJson.put("id", c.getId());
				cJson.put("collection_name",c.getCollection_name());
				if(!Strings.isNullOrEmpty(c.getDescription())){
					cJson.put("description", c.getDescription());
				}
				cJson.put("type", c.getType());
				cJson.put("cover_image",JSONObject.fromObject(c.getCover_image()));
				cJson.put("thumbnail",JSONObject.fromObject(c.getThumbnail()));
				if(c.getType() == 60){
					cJson.put("logo",JSONObject.fromObject(c.getLogo()));
					Districts d = c.getDistricts();
					if(d != null){
						Districts di = districtsDao.get(d.getParent_id());
						areaJson = new JSONObject();
						areaJson.put("id", d.getId());
						areaJson.put("area_name", di.getName()+d.getName());
						cJson.put("area",areaJson);
					}
					
				}else if (c.getType() == 30){
					if(!Strings.isNullOrEmpty(c.getCar_url())){
						cJson.put("car_url", c.getCar_url());
					}
					
				}
				cJsonList.add(cJson);
			}
		}
		return Response.status(Response.Status.OK).entity(cJsonList).build();
	
	}
	
	@Override
	public Response districts_list(HttpServletRequest request) {
		String parent_id = request.getParameter("parent_id");
		JSONObject dJson = null;
		List<JSONObject> dJsonList = new ArrayList<JSONObject>();
		if(!Strings.isNullOrEmpty(parent_id)){
			List<Districts> dList = districtsDao.getDistrictsByParentId(Long.parseLong(parent_id));
			if(dList != null && dList.size() > 0){
				for(Districts d:dList){
					dJson = new JSONObject();
					dJson.put("id", d.getId());
					dJson.put("parent_id", d.getParent_id());
					dJson.put("name", d.getName());
					dJsonList.add(dJson);
				}
			}
		}else{
			List<Districts> dList = districtsDao.getDistrictsByParentId(0l);
			if(dList != null && dList.size() > 0){
				for(Districts d:dList){
					dJson = new JSONObject();
					dJson.put("id", d.getId());
					dJson.put("parent_id", d.getParent_id());
					dJson.put("name", d.getName());
					dJsonList.add(dJson);
				}
			}
		}
		return Response.status(Response.Status.OK).entity(dJsonList).build();
	}

	@Override
	public Response add_message(JSONObject content, Long loginUserid,HttpServletRequest request) throws Exception {

		JSONObject resp = new JSONObject();
		SendMessage se = new SendMessage();
		if (content != null) {
			if (content.containsKey("type") && !Strings.isNullOrEmpty(content.getString("type"))) {
				String type = content.getString("type");
				se.setType(type);
				if (type.equals("h5") || type.equals("content") || type.equals("carfriend")) {
					se.setUrl(content.getString("url"));
				} else if (type.equals("news") || type.equals("bbs")) {
					se.setReference_id(content.getLong("reference_id"));
				}
			}
			se.setAdmin_id(loginUserid);
			se.setTitle(content.getString("title"));
			int send_type = content.getInt("send_type");
			if (send_type == 0) {
				se.setSend_type(send_type);
			} else {
				se.setSend_type(send_type);
				if (content.containsKey("send_time") && !Strings.isNullOrEmpty(content.getString("send_time"))) {
					se.setSend_time(content.getString("send_time"));
				}

			}

			if (content.containsKey("description") && !Strings.isNullOrEmpty(content.getString("description"))) {
				se.setDescription(content.getString("description"));
			}
			se.setCreate_time(new Date());
			sendMessageDao.save(se);
			String type = se.getType();
//			List<User> userList = userDao.getAll();
			final List<PushNotification> pnList = new ArrayList<PushNotification>();
			Notification n = null;

			n = new Notification();
			n.setRecipientId(0l);
			n.setNotificationType(0);
			n.setTitle(se.getTitle());
			if (type.equals("h5")) {
				n.setObjectType(2);
				n.setReference_url(se.getUrl());
			} else if (type.equals("content")) {
				n.setObjectType(4);
				n.setReference_url(se.getUrl());
			} else if (type.equals("carfriend")) {
				n.setObjectType(3);
				n.setReference_url(se.getUrl());
			} else if (type.equals("news")) {
				n.setObjectType(5);
				n.setObjectId(se.getReference_id());
			}else if (type.equals("bbs")) {
				n.setObjectType(6);
				n.setObjectId(se.getReference_id());
			}
			n.setRemark(se.getDescription());
			n.setStatus("enable");
			n.setRead_already(false);
			n.setCreate_time(new Date());
		
			this.notificationDao.save(n);;
			List<PushNotification> list = this.pushNotificationDao.getPushNotification();
			pnList.addAll(list);
			final String content_info = se.getTitle();
			final JSONObject json = new JSONObject();
			if (type.equals("h5") || type.equals("content") || type.equals("carfriend")) {
				json.put("type", se.getType());
				json.put("url", se.getUrl());
			} else if (type.equals("news")) {
				json.put("type", "news");
				json.put("news_id", se.getReference_id());
			} else if (type.equals("bbs")) {
				json.put("type", "bbs");
				json.put("bbs_id", se.getReference_id());
			} 
			json.put("title", se.getTitle());
			if (!Strings.isNullOrEmpty(se.getDescription())) {
				json.put("description", se.getDescription());
			}
			final GetuiModel gm = getGetuiInfo();
			if (send_type == 0) {

				PushNotificationUtil.pushInfoAll(gm.getAppId(), gm.getAppKey(), gm.getMasterSecret(), pnList,
						content_info, json.toString());
			} else if (send_type == 1) {
				String date = se.getSend_time();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date d = (Date) sdf.parse(date);
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						try {
							System.out.println("getui------->>>" + gm.getAppId() + "---" + content_info);
							PushNotificationUtil.pushInfoAll(gm.getAppId(), gm.getAppKey(), gm.getMasterSecret(),
									pnList, content_info, json.toString());

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, d);
			}
			resp = getSendMessageJson(se, loginUserid,request);
			return Response.status(Response.Status.OK).entity(resp).build();
		} else {
			resp.put("status", "缺少参数");
			resp.put("code", 10001);
			resp.put("error_message", "缺少参数");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}

	
	}

	@Override
	public Response message_list(HttpServletRequest request, Long loginUserid) {


		String title = request.getParameter("title");
		String countStr = request.getParameter("count");
		String pageStr = request.getParameter("page");
		int count = 10;
		int page = 0;
		JSONObject error = new JSONObject();
		if (loginUserid != null && loginUserid > 0) {
			List<SendMessage> seList = null;
			JSONObject result = new JSONObject();
			List<JSONObject> seJSONList = new ArrayList<JSONObject>();

			if (!Strings.isNullOrEmpty(title)) {
				if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(pageStr))) {
					seList = sendMessageDao.getSendMessageListByTitle(count, page, title);
					int total = sendMessageDao.getSendMessageCountByTitle(title);
					result.put("total", total);
				} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(pageStr))) {
					count = Integer.parseInt(countStr);
					seList = sendMessageDao.getSendMessageListByTitle(count, page, title);
					int total = sendMessageDao.getSendMessageCountByTitle(title);
					result.put("total", total);
				} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(pageStr))) {
					page = Integer.parseInt(pageStr);
					seList = sendMessageDao.getSendMessageListByTitle(count, page, title);
				} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(pageStr))) {
					count = Integer.parseInt(countStr);
					page = Integer.parseInt(pageStr);
					seList = sendMessageDao.getSendMessageListByTitle(count, page, title);
				}
			} else {
				if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(pageStr))) {
					seList = sendMessageDao.getSendMessageList(count, page);
					int total = sendMessageDao.getSendMessageCount();
					result.put("total", total);
				} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(pageStr))) {
					count = Integer.parseInt(countStr);
					seList = sendMessageDao.getSendMessageList(count, page);
					int total = sendMessageDao.getSendMessageCount();
					result.put("total", total);
				} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(pageStr))) {
					page = Integer.parseInt(pageStr);
					seList = sendMessageDao.getSendMessageList(count, page);
				} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(pageStr))) {
					count = Integer.parseInt(countStr);
					page = Integer.parseInt(pageStr);
					seList = sendMessageDao.getSendMessageList(count, page);
				}
			}

			if (seList != null && seList.size() > 0) {
				JSONObject seJson = null;
				for (SendMessage se : seList) {
					seJson = getSendMessageJson(se, loginUserid,request);
					seJSONList.add(seJson);
				}
			}
			result.put("sendMessages", seJSONList);
			return Response.status(Response.Status.OK).entity(result).build();
		} else {
			error.put("status", "用户未登录");
			error.put("code", Integer.valueOf(10010));
			error.put("error_message", "用户未登录");
			return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
		}

	
	}


	public GetuiModel getGetuiInfo() {
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
	
	public JSONObject getSendMessageJson(SendMessage se, Long loginUserid,HttpServletRequest request) {
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
		
		JSONObject resp = new JSONObject();
		Admin admin = adminDao.getAdminByFbid(Integer.parseInt(se.getAdmin_id().toString()));
		resp.put("id", se.getId());

		resp.put("title", se.getTitle());
		int send_type = se.getSend_type();
		if (send_type == 0) {
			resp.put("send_type", se.getSend_type());
			resp.put("create_time", se.getCreate_time());
		} else {
			resp.put("send_type", se.getSend_type());
			resp.put("send_time", se.getSend_time());
		}

		String type = se.getType();
		if (type.equals("url")) {
			resp.put("url", se.getUrl());
			resp.put("info", se.getUrl());
		} else if (type.equals("content")) {
//			Map<String,String> storyJson = new HashMap<String,String>();
//			storyJson.put("content_id", se.getReference_id().toString());
//			
//			storyJson.put("ip", ip);
//			String params = "";
//			try {
//				params = carPublicParam(storyJson);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			String result = HttpUtil.sendGetStr(url+"/content/get",params);
//			if(!Strings.isNullOrEmpty(result)){
//				JSONObject resJson = JSONObject.fromObject(result);
//				int code_res = resJson.getInt("code");
//				if(code_res == 10000){
//					JSONObject data = resJson.getJSONObject("data");
//					Iterator<String> iter = data.keys();
//					JsonConfig configs = new JsonConfig();
//					List<String> delArray = new ArrayList<String>();
//					while(iter.hasNext()){
//						String key = iter.next();
//						String val = data.getString(key);
//						if(Strings.isNullOrEmpty(val) || val.equals("null")){
//							delArray.add(key);
//						}
//						
//						
//					}
//				}
//			
//			
//
//			}
			resp.put("info", "");
			resp.put("reference_id", se.getReference_id());
		}
		resp.put("type", type);
		resp.put("author", admin.getUsername());

		return resp;

	}

	


}
