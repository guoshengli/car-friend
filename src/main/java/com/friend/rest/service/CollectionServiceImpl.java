package com.friend.rest.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.friend.rest.common.FBEncryption;
import com.friend.rest.common.HttpUtil;
import com.friend.rest.common.ParseFile;
import com.friend.rest.dao.CarClubDao;
import com.friend.rest.dao.CategoryCollectionDao;
import com.friend.rest.dao.CategoryDao;
import com.friend.rest.dao.CollectionDao;
import com.friend.rest.dao.DistrictsDao;
import com.friend.rest.dao.NavigationDao;
import com.friend.rest.model.CarClub;
import com.friend.rest.model.Category;
import com.friend.rest.model.CategoryCollection;
import com.friend.rest.model.Collection;
import com.friend.rest.model.Districts;
import com.friend.rest.model.Navigation;
import com.google.common.base.Strings;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@Transactional(readOnly=true)
public class CollectionServiceImpl implements CollectionService {
	private final Log log = LogFactory.getLog(CollectionServiceImpl.class);

	@Autowired
	private CollectionDao collectionDao;
	
	@Autowired
	private DistrictsDao districtsDao;

	@Autowired
	private CarClubDao carClubDao;
	
	@Autowired
	private CategoryDao categoryDao;
	
	@Autowired
	private CategoryCollectionDao categoryCollectionDao;
	
	@Autowired
	private NavigationDao navigationDao;


////	public Response create(Long loginUserid, JSONObject collection) {
////
////		JSONObject json = new JSONObject();
////		Collection c = new Collection();
////		if (collection != null) {
////			String collectionName = collection.getString("collection_name");
////			if (!Strings.isNullOrEmpty(collectionName)) {
////				Collection test_c = this.collectionDao.getCollectionByCollectionName(collectionName);
////				if (test_c == null) {
////					c.setCollectionName(collectionName);
////
////					if (!Strings.isNullOrEmpty(collection.getString("cover_image"))) {
////						c.setCover_image(collection.getString("cover_image"));
////					}
////
////					User user = userDao.get(loginUserid);
////
////					c.setStatus("enabled");
////					c.setUser(user);
////					c.setNumber(Long.valueOf(1L));
////					if (collection.containsKey("info")) {
////						c.setInfo(collection.getString("info"));
////					}
////					Interest interest = interestDao.get(collection.getLong("interest_id"));
////					c.setInterest(interest);
////					this.collectionDao.save(c);
////					c.setNumber((Long) c.getId());
////					this.collectionDao.update(c);
////
////					UserCollection uc = new UserCollection();
////					uc.setCollections(c);
////					uc.setUsers(user);
////					uc.setCreate_time(new Date());
////					Set<User> uSet = c.getUsers();
////					int num = 0;
////					if (uSet != null && uSet.size() > 0) {
////						num = uSet.size();
////					}
////
////					uc.setSequence(num + 1);
////					userCollectionDao.save(uc);
////					List<Notification> notificationList = new ArrayList<Notification>();
////					List<Follow> followList = this.followDao.getFollowersByUserId(loginUserid);
////					Notification n = null;
////					if ((followList != null) && (followList.size() > 0)) {
////						for (Follow follow : followList) {
////							n = new Notification();
////							n.setRecipientId(follow.getPk().getUser().getId());
////							n.setSenderId(loginUserid);
////							n.setNotificationType(14);
////							n.setObjectType(4);
////							n.setObjectId(c.getId());
////							n.setStatus("enabled");
////							n.setRead_already(true);
////							notificationList.add(n);
////						}
////						this.notificationDao.saveNotifications(notificationList);
////					}
////
////					CollectionModel collectionModel = new CollectionModel();
////					collectionModel.setCollection_name(c.getCollectionName());
////					collectionModel.setId((Long) c.getId());
////					collectionModel.setInfo(c.getInfo());
////					collectionModel.setCover_image(JSONObject.fromObject(c.getCover_image()));
////					return Response.status(Response.Status.CREATED).entity(collectionModel).build();
////				} else {
////					json.put("status", "repetition_collection_name");
////					json.put("code", Integer.valueOf(10036));
////					json.put("error_message", "collection name is repetition");
////					return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
////				}
////
////			} else {
////				json.put("status", "invalid_request");
////				json.put("code", Integer.valueOf(10010));
////				json.put("error_message", "Invalid payload parameters");
////				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
////			}
////
////		}
////
////		json.put("status", "invalid_request");
////		json.put("code", Integer.valueOf(10010));
////		json.put("error_message", "Invalid payload parameters");
////		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
////
////	}
//
////	public Response updateCollection(Long collectionId, JSONObject collection) {
////		if (collectionId != null && collectionId > 0) {
////			Collection c = collectionDao.getCollectionById(collectionId);
////			if (collection.containsKey("collection_name")) {
////				if (!Strings.isNullOrEmpty(collection.getString("collection_name"))) {
////					c.setCollectionName(collection.getString("collection_name"));
////				}
////
////			}
////
////			if (collection.containsKey("info")) {
////				c.setInfo(collection.getString("info"));
////			}
////
////			if (collection.containsKey("cover_image")) {
////				if (!Strings.isNullOrEmpty(collection.getString("cover_image"))) {
////					c.setCover_image(collection.getString("cover_image"));
////				}
////
////			}
////
////			collectionDao.update(c);
////			CollectionIntroLast cis = new CollectionIntroLast();
////			cis.setId(c.getId());
////			cis.setCollection_name(c.getCollectionName());
////			cis.setCover_image(JSONObject.fromObject(c.getCover_image()));
////			cis.setInfo(c.getInfo());
////			Set<User> uSet = c.getUsers();
////			Iterator<User> its = uSet.iterator();
////			if (its.hasNext()) {
////				boolean flag = false;
////				while (its.hasNext()) {
////					User u = its.next();
////					if (u.getId().equals(c.getUser().getId()) && u.getId() == c.getUser().getId()) {
////						flag = true;
////						break;
////					}
////				}
////				cis.setIs_followed_by_current_user(flag);
////			} else {
////				cis.setIs_followed_by_current_user(false);
////			}
////			User u = c.getUser();
////			JSONObject aiJson = null;
////			if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
////				aiJson = JSONObject.fromObject(u.getAvatarImage());
////			}
////
////			JSONObject ciJson = null;
////			if (!Strings.isNullOrEmpty(u.getCoverImage())) {
////				ciJson = JSONObject.fromObject(u.getCoverImage());
////			}
////			JSONObject authorJson = new JSONObject();
////			authorJson.put("id", u.getId());
////			authorJson.put("username", u.getUsername());
////			authorJson.put("email", u.getEmail());
////			authorJson.put("created_time", u.getCreated_time());
////			authorJson.put("status", u.getStatus());
////			authorJson.put("introduction", u.getIntroduction());
////			authorJson.put("avatar_image", aiJson);
////			authorJson.put("cover_image", ciJson);
////			cis.setAuthor(authorJson);
////			JSONObject json = JSONObject.fromObject(cis);
////			return Response.status(Response.Status.OK).entity(json).build();
////		} else {
////
////			JSONObject json = new JSONObject();
////			json.put("status", "no_resource");
////			json.put("code", 10011);
////			json.put("error_message", "The user does not exist");
////			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
////
////		}
////	}
//
	public Response getTimelinesByCollecionId(int collectionId, HttpServletRequest request, Long loginUserid) {
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		int count = 20;
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("car_url");
		Collection collection = collectionDao.getCollectionById(collectionId);
		StringBuffer sb = new StringBuffer();
		int type = collection.getType();
		sb.append("data_type="+type+"&");
		sb.append("data_id="+collectionId+"&");
		sb.append("relation_type="+20+"&");
		sb.append("is_highlighted="+1);
		String result = "";
		if ((Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			sb.append("&page_count="+count);
			result = HttpUtil.sendGetStr(url+"/content/get-list", sb.toString());
			
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			sb.append("&page_count="+count);
			result = HttpUtil.sendGetStr(url+"/content/get-list", sb.toString());
		}else if ((Strings.isNullOrEmpty(countStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			int max_id = Integer.parseInt(maxIdStr);
			sb.append("&page_count="+count);
			sb.append("&prev_id="+max_id);
			result = HttpUtil.sendGetStr(url+"/content/get-list", sb.toString());
		} else if ((!Strings.isNullOrEmpty(countStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			int max_id = Integer.parseInt(maxIdStr);
			sb.append("&page_count="+count);
			sb.append("&prev_id="+max_id);
			result = HttpUtil.sendGetStr(url+"/content/get-list", sb.toString());
		}
		
		
		JSONObject resp = new JSONObject();
		if (!Strings.isNullOrEmpty(result)) {
			JSONObject json = JSONObject.fromObject(result);
			int status = json.getInt("code");
			if (status == 10000) {
				Object data = json.get("data");
				return Response.status(Response.Status.OK).entity(data).build();
			} else if(status == 10001) {	
				resp.put("status", "缺少参数");
				resp.put("code", 10600);
				resp.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 14005) {	
				resp.put("status", "关系对象类型错误");
				resp.put("code", 10846);
				resp.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 14016) {	
				resp.put("status", "车款不存在");
				resp.put("code", 10847);
				resp.put("error_message", "车款不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}else if(status == 14017) {	
				resp.put("status", "栏目不存在");
				resp.put("code", 10848);
				resp.put("error_message", "栏目不存在");
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
//
//	public StoryModel getStoryModelByStoryLoginUser(Story story, Long loginUserid) {
//		StoryModel storyModel = new StoryModel();
//		storyModel.setId((Long) story.getId());
//		int repostStoryCount = this.republishDao.userRepostCount((Long) story.getUser().getId());
//		User user = story.getUser();
//
//		Follow loginUserFollowAuthor = this.followDao.getFollow(loginUserid, story.getUser().getId());
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
//		if ((user.getAvatarImage() != null) && (!user.getAvatarImage().equals("")))
//			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
//		else {
//			avatarImageJson = JSONObject.fromObject("{}");
//		}
//
//		JSONObject coverImageJson = null;
//		if ((user.getCoverImage() != null) && (!user.getCoverImage().equals("")))
//			coverImageJson = JSONObject.fromObject(user.getCoverImage());
//		else {
//			coverImageJson = JSONObject.fromObject("{}");
//		}
//		int storyCount = this.storyDao.getStoryCount((Long) user.getId());
//		int follower_Count = this.followDao.userFollowedCount((Long) user.getId());
//		int following_count = this.followDao.userFollowCount((Long) user.getId());
//		UserParentModel userModel = null;
//		if ((user.getUser_type().equals("normal")) || (user.getUser_type().equals("admin"))
//				|| (user.getUser_type().equals("super_admin")) || (user.getUser_type().equals("vip"))
//				|| (user.getUser_type().equals("official"))) {
//			userModel = new UserModel((Long) user.getId(), user.getUsername(), user.getEmail(), user.getCreated_time(),
//					user.getStatus(), user.getIntroduction(), avatarImageJson, coverImageJson, repostStoryCount,
//					storyCount, follower_Count, following_count, user.getWebsite(), followed_by_current_user,
//					is_following_current_user, user.getUser_type(), user.getGender());
//		} else if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
//			Set<PublisherInfo> publisherSet = user.getPublisherInfos();
//			List<PublisherInfoModel> publisherList = new ArrayList<PublisherInfoModel>();
//			PublisherInfoModel pim = null;
//			if ((publisherSet != null) && (publisherSet.size() > 0)) {
//				for (PublisherInfo pi : publisherSet) {
//					pim = new PublisherInfoModel();
//					pim.setType(pi.getType());
//					pim.setContent(pi.getContent());
//					publisherList.add(pim);
//				}
//			}
//			userModel = new UserPublisherModel((Long) user.getId(), user.getUsername(), user.getEmail(),
//					user.getCreated_time(), user.getStatus(), user.getIntroduction(), avatarImageJson, coverImageJson,
//					repostStoryCount, storyCount, follower_Count, following_count, user.getWebsite(),
//					followed_by_current_user, is_following_current_user, user.getUser_type(), publisherList,
//					user.getGender());
//		}
//
//		storyModel.setAuthor(JSONObject.fromObject(userModel));
//		storyModel.setCreated_time(story.getCreated_time());
//		storyModel.setUpdate_time(story.getUpdate_time());
//
//		JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
//		String type = jsonObject.getString("type");
//		CoverMedia coverMedia = null;
//		if (type.equals("text"))
//			coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
//		else if (type.equals("image")) {
//			coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
//		}
//
//		storyModel.setCover_media(JSONObject.fromObject(coverMedia));
//
//		List<StoryElement> storyElements = new ArrayList<StoryElement>();
//		List<StoryElement> seSet = story.getElements();
//		if ((seSet != null) && (seSet.size() > 0)) {
//			for (StoryElement element : seSet) {
//				JSONObject content = JSONObject.fromObject(element.getContents());
//
//				String types = content.getString("type");
//				if (types.equals("text")) {
//					TextCover textCover = (TextCover) JSONObject.toBean(content, TextCover.class);
//					this.log.debug("*** element TextCover type ***" + textCover.getType());
//					element.setContent(textCover);
//				} else if (types.equals("image")) {
//					ImageCover imageCover = (ImageCover) JSONObject.toBean(content, ImageCover.class);
//					this.log.debug("*** element ImageCover type ***" + imageCover.getType());
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
//
//				storyElements.add(element);
//			}
//		}
//
//		JsonConfig config = new JsonConfig();
//		config.setExcludes(new String[] { "storyinfo", "contents" });
//		config.setIgnoreDefaultExcludes(false);
//		config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//		this.log.debug("***get Elements *****" + JSONArray.fromObject(story.getElements(), config));
//		int count = 0;
//		Set<Comment> cSet = story.getComments();
//		if (cSet != null && cSet.size() > 0) {
//			count = cSet.size();
//		}
//		storyModel.setElements(JSONArray.fromObject(storyElements, config));
//
//		storyModel.setCommnents_enables(story.getComments_enabled());
//		storyModel.setUrl(story.getTinyURL());
//		storyModel.setView_count(story.getViewTimes());
//		storyModel.setTitle(story.getTitle());
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
//	public JSONObject getStoryEventByStoryLoginUser(Story story, Long loginUserid, User loginUser) {
//		StoryEvent storyModel = new StoryEvent();
//		storyModel.setId((Long) story.getId());
//		User user = story.getUser();
//		storyModel.setImage_count(story.getImage_count());
//		storyModel.setUrl(story.getTinyURL());
//		JSONObject authorJson = new JSONObject();
//		JSONObject coverImageJson = null;
//		if (!Strings.isNullOrEmpty(user.getCoverImage())) {
//			coverImageJson = JSONObject.fromObject(user.getCoverImage());
//		}
//
//		JSONObject avatarImageJson = null;
//		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
//		}
//		authorJson.put("id", user.getId());
//		authorJson.put("cover_image", coverImageJson);
//		authorJson.put("username", user.getUsername());
//		authorJson.put("introduction", user.getIntroduction());
//		authorJson.put("avatar_image", avatarImageJson);
//		authorJson.put("user_type", user.getUser_type());
//		if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
//			Set<PublisherInfo> publisherSet = user.getPublisherInfos();
//			List<PublisherInfoModel> publisherList = null;
//			PublisherInfoModel pim = null;
//			if ((publisherSet != null) && (publisherSet.size() > 0)) {
//				publisherList = new ArrayList<PublisherInfoModel>();
//				for (PublisherInfo pi : publisherSet) {
//					pim = new PublisherInfoModel();
//					pim.setType(pi.getType());
//					pim.setContent(pi.getContent());
//					publisherList.add(pim);
//				}
//			}
//
//			authorJson.put("publisher_info", publisherList);
//		}
//
//		/*
//		 * String theme_color = null; if (user.getTheme_id() != null) {
//		 * Theme_color color =
//		 * (Theme_color)this.themeColorDao.get(user.getTheme_id()); theme_color
//		 * = color.getColor(); } authorJson.put("theme_color", theme_color);
//		 */
//		if (!Strings.isNullOrEmpty(user.getWebsite()))
//			authorJson.put("website", user.getWebsite());
//		else {
//			authorJson.put("website", null);
//		}
//		storyModel.setAuthor(authorJson);
//		storyModel.setCreated_time(story.getCreated_time());
//		int count = this.commentDao.getCommentCountById((Long) story.getId());
//		storyModel.setComment_count(count);
//		if (loginUserid != null && loginUserid > 0) {
//			Set<Story> sSet = loginUser.getRepost_story();
//			List<Story> rsList = new ArrayList<Story>();
//			if (sSet != null && sSet.size() > 0) {
//				Iterator<Story> it = sSet.iterator();
//				while (it.hasNext()) {
//					rsList.add(it.next());
//				}
//				if (rsList.contains(story)) {
//					storyModel.setRepost_by_current_user(true);
//				} else {
//					storyModel.setRepost_by_current_user(false);
//				}
//			} else {
//				storyModel.setRepost_by_current_user(false);
//			}
//
//			Set<Story> likeStory = loginUser.getLike_story();
//			List<Story> lsList = new ArrayList<Story>();
//			if (likeStory != null && likeStory.size() > 0) {
//				Iterator<Story> it = likeStory.iterator();
//				while (it.hasNext()) {
//					lsList.add(it.next());
//				}
//				if (lsList.contains(story)) {
//					storyModel.setLiked_by_current_user(true);
//					;
//				} else {
//					storyModel.setLiked_by_current_user(false);
//				}
//			} else {
//				storyModel.setLiked_by_current_user(false);
//			}
//		}
//
//		Set<User> like_set = story.getLike_users();
//		if (like_set != null && like_set.size() > 0) {
//			storyModel.setLike_count(like_set.size());
//		} else {
//			storyModel.setLike_count(0);
//		}
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
//		Collection collection = null;
//		/*
//		 * Set<Collection> cSet = story.getCollections(); if(cSet != null &&
//		 * cSet.size() > 0){ collection = cSet.iterator().next(); if (collection
//		 * != null) { CollectionIntro ci = new CollectionIntro();
//		 * ci.setId((Long) collection.getId());
//		 * ci.setCollection_name(collection.getCollectionName());
//		 * ci.setCover_image(JSONObject.fromObject(collection.getCover_image()))
//		 * ; ci.setInfo(collection.getInfo());
//		 * ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()
//		 * )); User author = collection.getUser(); JSONObject json = new
//		 * JSONObject(); json.put("id",author.getId());
//		 * json.put("username",author.getUsername());
//		 * if(!Strings.isNullOrEmpty(author.getAvatarImage())){
//		 * json.put("avatar_image",JSONObject.fromObject(author.getAvatarImage()
//		 * )); } ci.setAuthor(json); Set<User> follow_collection =
//		 * collection.getUsers(); if(follow_collection != null &&
//		 * follow_collection.size() > 0){
//		 * ci.setFollowers_count(follow_collection.size()); }else{
//		 * ci.setFollowers_count(0); } JsonConfig configs = new JsonConfig();
//		 * List<String> delArray = new ArrayList<String>();
//		 * if(!Strings.isNullOrEmpty(collection.getActivity_description())){
//		 * ci.setActivity_description(collection.getActivity_description());
//		 * }else{ if
//		 * (Strings.isNullOrEmpty(collection.getActivity_description())) {
//		 * delArray.add("activity_description"); } }
//		 * 
//		 * 
//		 * JSONObject collectionJson = null; if ((delArray != null) &&
//		 * (delArray.size() > 0)) { configs.setExcludes((String[])
//		 * delArray.toArray(new String[delArray.size()]));
//		 * configs.setIgnoreDefaultExcludes(false);
//		 * configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//		 * 
//		 * collectionJson = JSONObject.fromObject(ci, configs); } else {
//		 * collectionJson = JSONObject.fromObject(ci); }
//		 * 
//		 * storyModel.setCollection(collectionJson); } }
//		 */
//
//		if (!Strings.isNullOrEmpty(story.getTitle()))
//			storyModel.setTitle(story.getTitle());
//		else {
//			storyModel.setTitle(null);
//		}
//
//		if (!Strings.isNullOrEmpty(story.getSummary())) {
//			storyModel.setSummary(story.getSummary());
//		} else {
//			storyModel.setSummary(null);
//		}
//
//		storyModel.setRecommend_date(story.getRecommend_date());
//
//		JsonConfig configs = new JsonConfig();
//		List<String> delArray = new ArrayList<String>();
//		if (Strings.isNullOrEmpty(story.getTitle())) {
//			delArray.add("title");
//		}
//		if (collection == null) {
//			delArray.add("collection");
//		}
//		if (Strings.isNullOrEmpty(story.getSummary())) {
//			delArray.add("summary");
//		}
//		JSONObject storyJson = null;
//		if ((delArray != null) && (delArray.size() > 0)) {
//			configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
//			configs.setIgnoreDefaultExcludes(false);
//			configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//			storyJson = JSONObject.fromObject(storyModel, configs);
//		} else {
//			storyJson = JSONObject.fromObject(storyModel);
//		}
//
//		return storyJson;
//	}
//
//	public StoryHome getStoryEventByStory(Story story) {
//		StoryHome storyModel = new StoryHome();
//		storyModel.setId((Long) story.getId());
//		User user = story.getUser();
//		storyModel.setImage_count(story.getImage_count());
//		storyModel.setUrl(story.getTinyURL());
//		JSONObject authorJson = new JSONObject();
//		JSONObject coverImageJson = null;
//		if (!Strings.isNullOrEmpty(user.getCoverImage())) {
//			coverImageJson = JSONObject.fromObject(user.getCoverImage());
//		}
//
//		JSONObject avatarImageJson = null;
//		if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
//		}
//		authorJson.put("id", user.getId());
//		authorJson.put("cover_image", coverImageJson);
//		authorJson.put("username", user.getUsername());
//		authorJson.put("introduction", user.getIntroduction());
//		authorJson.put("avatar_image", avatarImageJson);
//		authorJson.put("user_type", user.getUser_type());
//		if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
//			Set<PublisherInfo> publisherSet = user.getPublisherInfos();
//			List<PublisherInfoModel> publisherList = null;
//			PublisherInfoModel pim = null;
//			if ((publisherSet != null) && (publisherSet.size() > 0)) {
//				publisherList = new ArrayList<PublisherInfoModel>();
//				for (PublisherInfo pi : publisherSet) {
//					pim = new PublisherInfoModel();
//					pim.setType(pi.getType());
//					pim.setContent(pi.getContent());
//					publisherList.add(pim);
//				}
//			}
//
//			authorJson.put("publisher_info", publisherList);
//		}
//
//		String theme_color = null;
//		authorJson.put("theme_color", theme_color);
//		if (!Strings.isNullOrEmpty(user.getWebsite()))
//			authorJson.put("website", user.getWebsite());
//		else {
//			authorJson.put("website", null);
//		}
//		storyModel.setAuthor(authorJson);
//		storyModel.setCreated_time(story.getCreated_time());
//		/*
//		 * int count = this.commentDao.getCommentCountById((Long)story.getId());
//		 * storyModel.setComment_count(count);
//		 */
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
//		if (!Strings.isNullOrEmpty(story.getTitle()))
//			storyModel.setTitle(story.getTitle());
//		else {
//			storyModel.setTitle(null);
//		}
//
//		if (!Strings.isNullOrEmpty(story.getSummary())) {
//			storyModel.setSummary(story.getSummary());
//		} else {
//			storyModel.setSummary(null);
//		}
//
//		storyModel.setRecommend_date(story.getRecommend_date());
//
//		return storyModel;
//	}
//
//	@Override
//	public List<JSONObject> getHotStoriesByCollection_id(Long collectionId, HttpServletRequest request,
//			Long loginUserid) {
//
//		String countStr = request.getParameter("count");
//		String sinceIdStr = request.getParameter("since_id");
//		String maxIdStr = request.getParameter("max_id");
//		List<JSONObject> storyModelList = new ArrayList<JSONObject>();
//		int count = 20;
//		String type = "publish";
//		JSONObject storyModel = null;
//		User loginUser = null;
//		if (loginUserid != null && loginUserid > 0) {
//			loginUser = userDao.get(loginUserid);
//		}
//		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			List<Story> storyList = this.collectionStoryDao.getStoriesByCollectionIdAndHot(collectionId, count, type);
//			if ((storyList != null) && (storyList.size() > 0))
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			List<Story> storyList = this.collectionStoryDao.getStoriesByCollectionIdAndHot(collectionId, count, type);
//			if ((storyList != null) && (storyList.size() > 0))
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			List<Story> storyList = this.collectionStoryDao.getStoriesByCollectionIdAndHot(collectionId, since_id,
//					count, 1, type);
//			if ((storyList != null) && (storyList.size() > 0))
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			List<Story> storyList = this.collectionStoryDao.getStoriesByCollectionIdAndHot(collectionId, max_id, count,
//					2, type);
//			if ((storyList != null) && (storyList.size() > 0)) {
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//			}
//		}
//		this.log.debug("*** get stories list***" + JSONArray.fromObject(storyModelList));
//		return storyModelList;
//
//	}
//
	public Response getFeaturedStoriesByCollection_id(Long collectionId, HttpServletRequest request,
			Long loginUserid)throws Exception {
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		JSONObject resp = new JSONObject();
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
		int count = 20;
		JSONObject data = new JSONObject();
		Collection co = collectionDao.getCollectionById(collectionId);
		
		Map<String,String> cMap = new HashMap<String,String>();
		cMap.put("ip", ip);
		cMap.put("device", device);
		cMap.put("collection_id", String.valueOf(co.getId()));
		if(loginUserid != null && loginUserid > 0){
			cMap.put("user_id", loginUserid.toString());
		}
		
		String cparams = carPublicParam(cMap);
		String cResult = HttpUtil.sendGetStr(car_url+"/collection/get", cparams);
		
		if(co != null){
			JSONObject ci = new JSONObject();
			
			ci.put("id",co.getId());
			ci.put("collection_name",co.getCollection_name());
			ci.put("cover_image",JSONObject.fromObject(co.getCover_image()));
			String desc = co.getDescription();
			if(!Strings.isNullOrEmpty(desc)){
				ci.put("description", desc);
			}
			int type = co.getType();
			Collection collection = null;
			List<JSONObject> clubJsonList = new ArrayList<JSONObject>();
			List<JSONObject> carJsonList = new ArrayList<JSONObject>();
			JSONObject clubJson = null;
			JSONObject carsJson = null;
			JSONObject areaJson = null;
			if(type == 30){
				List<CarClub> ccList = carClubDao.getCarClubListByCar_id(co.getId());
				if(ccList != null && ccList.size() > 0){
					for(CarClub cc:ccList){
						clubJson = new JSONObject();
						areaJson = new JSONObject();
						collection = collectionDao.get(cc.getClub_id());
						clubJson.put("id", collection.getId());
						clubJson.put("club_name", collection.getCollection_name());
						clubJson.put("cover_image", JSONObject.fromObject(collection.getCover_image()));
						clubJson.put("logo", collection.getLogo());
						Districts d = collection.getDistricts();
						Long area_id = d.getId();
						Long parent_id = d.getParent_id();
						Districts parent = districtsDao.get(parent_id);
						areaJson.put("id", area_id);
						areaJson.put("area_name", parent.getName()+d.getName());
						clubJson.put("area",areaJson);
						clubJsonList.add(clubJson);
					}
					ci.put("clubs", clubJsonList);
				}
				
				if(!Strings.isNullOrEmpty(co.getCar_url())){
					ci.put("car_url",co.getCar_url());
				}
				
			}else if(type == 60){
				List<CarClub> ccList = carClubDao.getCarClubListByClub_id(co.getId());
				if(ccList != null && ccList.size() > 0){
					for(CarClub cc:ccList){
						collection = collectionDao.get(cc.getCar_id());
						carsJson = new JSONObject();
						carsJson.put("id", collection.getId());
						carsJson.put("car_name", collection.getCollection_name());
						carJsonList.add(carsJson);
					}
					ci.put("cars", carJsonList);
				}
				ci.put("logo", JSONObject.fromObject(co.getLogo()));
				Districts d = co.getDistricts();
				Long area_id = d.getId();
				Districts parent = districtsDao.get(d.getParent_id());
				areaJson = new JSONObject();
				areaJson.put("id", area_id);
				areaJson.put("area_name", parent.getName()+d.getName());
				ci.put("area",areaJson);
				
//				ci.put("area", co.getArea());
			}
			ci.put("type", co.getType());
			JSONObject coJson = JSONObject.fromObject(cResult);
			int codes = coJson.getInt("code");
			if(codes == 10000){
				JSONObject dJson = coJson.getJSONObject("data");
				int follow_count = dJson.getInt("follow_count");
				ci.put("followers_count",follow_count);
				int content_count = dJson.getInt("content_count");
				ci.put("story_count",content_count);
				if(dJson.containsKey("is_follow")){
					boolean bool = dJson.getBoolean("is_follow");
					ci.put("is_followed_by_current_user",bool);
				}else{
					ci.put("is_followed_by_current_user",false);
				}
			}else if(codes == 16002){
				resp.put("status", "栏目不存在");
				resp.put("code", 10506);
				resp.put("error_message", "栏目不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
			
			data.put("collection", ci);
			
			Map<String,String> map = new HashMap<String, String>();
			
			map.put("relation_type", "20");
			map.put("ip", ip);
			map.put("device", device);
			map.put("is_highlighted", "1");
			map.put("data_type", "50");
			map.put("data_id", String.valueOf(co.getId()));
			if(loginUserid != null && loginUserid > 0){
				map.put("user_id", loginUserid.toString());
			}
			
			if ((Strings.isNullOrEmpty(countStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				map.put("page_count", String.valueOf(count));
			} else if ((!Strings.isNullOrEmpty(countStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				map.put("page_count", String.valueOf(count));
			}	else if ((Strings.isNullOrEmpty(countStr))
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
			List<JSONObject> cJsonList = new ArrayList<JSONObject>();
			if(!Strings.isNullOrEmpty(result)){
				JSONObject resJson = JSONObject.fromObject(result);
				int code = resJson.getInt("code");
				if(code == 10000){
					
					JSONArray arr = resJson.getJSONArray("data");
					JSONObject contentJson = null;
					
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
						
					}
				
			
				}else if (code == 14005){
					resp.put("status", "关系对象类型错误");
					resp.put("code", 10504);
					resp.put("error_message", "关系对象类型错误");
					return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
				}
			}
			data.put("stories", cJsonList);
		}else{
			resp.put("status", "数据错误");
			resp.put("code", 10540);
			resp.put("error_message", "数据错误");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
		
		return Response.status(Response.Status.OK).entity(data).build();
	}
//
//	@Override
//	public List<JSONObject> getFeaturedStoriesByCollections(HttpServletRequest request, Long loginUserid) {
//
//		String countStr = request.getParameter("count");
//		String sinceIdStr = request.getParameter("since_id");
//		String maxIdStr = request.getParameter("max_id");
//		List<JSONObject> storyModelList = new ArrayList<JSONObject>();
//		int count = 20;
//		String type = "publish";
//		JSONObject storyModel = null;
//		String ids = "";
//		User loginUser = userDao.get(loginUserid);
//		List<Collection> collectionList = userCollectionDao.getCollectionByUserid(loginUserid);
//		if (collectionList != null && collectionList.size() > 0) {
//			for (Collection c : collectionList) {
//				ids += c.getId() + ",";
//			}
//			if (!Strings.isNullOrEmpty(ids)) {
//				ids = ids.substring(0, ids.length() - 1);
//			}
//
//		}
//		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			List<Story> storyList = this.collectionStoryDao.getFeturedStoriesFollow(ids, count, type);
//			if ((storyList != null) && (storyList.size() > 0))
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			List<Story> storyList = this.collectionStoryDao.getFeturedStoriesFollow(ids, count, type);
//			if ((storyList != null) && (storyList.size() > 0))
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPageByCollections(ids, count, since_id, 1,
//					type);
//			if ((storyList != null) && (storyList.size() > 0))
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPageByCollections(ids, count, since_id, 1,
//					type);
//			if ((storyList != null) && (storyList.size() > 0))
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPageByCollections(ids, count, max_id, 2,
//					type);
//			if ((storyList != null) && (storyList.size() > 0)) {
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//			}
//		}
//		return storyModelList;
//
//	}
//
//	@Override
	public Response followCollection(Long loginUserid, JSONObject collectionIds,HttpServletRequest request)throws Exception {
		JSONObject resp = new JSONObject();
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
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("car_url");
		Map<String,String> userJson = new HashMap<String,String>();
		userJson.put("user_id", loginUserid.toString());
		userJson.put("ip", ip);
		userJson.put("device", device);
		userJson.put("data_type", "50");
		userJson.put("follow_type", "1");//-1-无关，10-熟悉，20-仰慕

		JSONArray ja = JSONArray.fromObject(collectionIds.getString("collection_id"));
		Object[] strArr = ja.toArray();
		if (strArr != null && strArr.length > 0) {
			int collectionId = Integer.parseInt(strArr[0].toString());
			userJson.put("data_id", String.valueOf(collectionId));
			String params = carPublicParam(userJson);
			String result = HttpUtil.sendPostStr(url+"/member/follow-data", params);
			if(!Strings.isNullOrEmpty(result)){
				JSONObject resJson = JSONObject.fromObject(result);
				int status = resJson.getInt("code");
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
			}
			
			JSONObject json = new JSONObject();
			json.put("status", "success");
			return Response.status(Response.Status.OK).entity(json).build();
		} else {
			JSONObject json = new JSONObject();
			json.put("status", "no_resource");
			json.put("code", 10011);
			json.put("error_message", "The user does not exist");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
	}
	
	
//
	@Override
	public Response unfollowCollection(Long loginUserid, int collection_id,HttpServletRequest request)throws Exception {
		JSONObject resp = new JSONObject();
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
		if(Strings.isNullOrEmpty(device)){
			device = "20";
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("car_url");
		Map<String,String> userJson = new HashMap<String,String>();
		userJson.put("user_id", loginUserid.toString());
		userJson.put("ip", ip);
		userJson.put("device", device);
		userJson.put("data_type", "50");
		userJson.put("follow_type", "-1");//-1-无关，10-熟悉，20-仰慕
		

		userJson.put("data_id", String.valueOf(collection_id));
		String params = carPublicParam(userJson);
		String result = HttpUtil.sendPostStr(url+"/member/follow-data", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int status = resJson.getInt("code");
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
		}
		
		JSONObject json = new JSONObject();
		json.put("status", "success");
		return Response.status(Response.Status.OK).entity(json).build();
	}
//
//	@Override
//	public JSONObject getFeaturedStoriesByFollowing(Long collectionId, HttpServletRequest request, Long loginUserid) {
//
//		String countStr = request.getParameter("count");
//		String sinceIdStr = request.getParameter("since_id");
//		String maxIdStr = request.getParameter("max_id");
//		List<JSONObject> storyModelList = new ArrayList<JSONObject>();
//		int count = 20;
//		String type = "publish";
//		JSONObject storyModel = null;
//		User loginUser = null;
//		if (loginUserid != null && loginUserid > 0) {
//			loginUser = userDao.get(loginUserid);
//		}
//
//		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPage(collectionId, count, type);
//			if ((storyList != null) && (storyList.size() > 0))
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPage(collectionId, count, type);
//			if ((storyList != null) && (storyList.size() > 0))
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPageByCollectionId(collectionId, count,
//					since_id, 1, type);
//			if ((storyList != null) && (storyList.size() > 0))
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPageByCollectionId(collectionId, count,
//					max_id, 2, type);
//			if ((storyList != null) && (storyList.size() > 0)) {
//				for (Story story : storyList) {
//					storyModel = getStoryEventByStoryLoginUser(story, loginUserid, loginUser);
//					storyModelList.add(storyModel);
//				}
//			}
//		}
//		JSONObject json = new JSONObject();
//		if (loginUserid != null && loginUserid > 0) {
//			UserCollection uc = userCollectionDao.getUserCollectionByCollectionId(collectionId, loginUserid);
//			if (uc != null) {
//				json.put("followed_by_current_user", true);
//			} else {
//				json.put("followed_by_current_user", false);
//			}
//		} else {
//			json.put("followed_by_current_user", false);
//		}
//
//		json.put("stories", storyModelList);
//		return json;
//
//	}
//
//	@Override
//	public Response updateUserCollection(Long loginUserid, JSONObject user_collection) {
//		JSONObject json = new JSONObject();
//		if (user_collection != null) {
//			Object obj = user_collection.get("collection_list");
//			JSONArray ja = JSONArray.fromObject(obj);
//			if (ja != null && ja.size() > 0) {
//				for (Object o : ja) {
//					JSONObject collection_json = JSONObject.fromObject(o);
//					Long id = collection_json.getLong("id");
//					int index = collection_json.getInt("collection_index");
//					userCollectionDao.updateUserCollectionSequenceByUserIdAndCollectionId(loginUserid, id, index);
//				}
//			}
//
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//
//		} else {
//			json.put("status", "invalid_request");
//			json.put("code", 10010);
//			json.put("error_message", "Invalid payload parameters");
//			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//		}
//	}
//
//	@Override
//	public Response deleteCollectionStory(Long collection_id, Long story_id) throws Exception {
//		CollectionStory cs = this.collectionStoryDao.getCollectionStoryByCollectionIdAndStoryId(collection_id,
//				story_id);
//		JSONObject json = new JSONObject();
//		if (cs != null) {
//			this.collectionStoryDao.delete(cs.getId());
//
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "invalid_request");
//		json.put("code", 10010);
//		json.put("error_message", "Invalid payload parameters");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//
//	}
//
//	@Override
//	public Response updateCollectionStory(Long collection_id, Long story_id, JSONObject status) throws Exception {
//		CollectionStory cs = this.collectionStoryDao.getCollectionStoryByCollectionIdAndStoryId(collection_id,
//				story_id);
//		JSONObject json = new JSONObject();
//
//		if (cs != null) {
//			this.collectionStoryDao.update(cs);
//
//			json.put("status", "success");
//			return Response.status(Response.Status.OK).entity(json).build();
//		}
//		json.put("status", "remove_story");
//		json.put("code", 10210);
//		json.put("error_message", "Author remove story from collection");
//		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
//
//	}
//
//	@Override
//	public Response getCollections(Long loginUserid) {
//		List<CollectionIntroLast> collection_info = new ArrayList<CollectionIntroLast>();
//		CollectionIntroLast ci = null;
//		List<Collection> cList = null;
//		cList = this.collectionDao.getCollectionBytype("interest");
//		// this.collectionDao.getCollections();
//		if ((cList != null) && (cList.size() > 0)) {
//			for (Collection co : cList) {
//				ci = new CollectionIntroLast();
////				ci.setId(0);
////				ci.setCollection_name(co.getCollectionName());
//				ci.setCover_image(JSONObject.fromObject(co.getCover_image()));
////				ci.setInfo(co.getInfo());
//				Set<User> uSet = null;//co.getUsers();
//				Iterator<User> it = uSet.iterator();
//				if (it.hasNext()) {
//					boolean flag = false;
//					while (it.hasNext()) {
//						User u = it.next();
//						if (u.getId().equals(loginUserid) && u.getId() == loginUserid) {
//							flag = true;
//							break;
//						}
//					}
//					ci.setIs_followed_by_current_user(flag);
//				} else {
//					ci.setIs_followed_by_current_user(false);
//				}
//
//				Set<Story> storySet = null;//co.getStories();
//				int story_count = storySet.size();
//				int followers_count = uSet.size();
//				ci.setStory_count(story_count);
//				ci.setFollowers_count(followers_count);
//				User u = null;//co.getUser();
//				JSONObject aiJson = null;
//				if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
//					aiJson = JSONObject.fromObject(u.getAvatarImage());
//				}
//
//				JSONObject ciJson = null;
//				if (!Strings.isNullOrEmpty(u.getCoverImage())) {
//					ciJson = JSONObject.fromObject(u.getCoverImage());
//				}
//				JSONObject authorJson = new JSONObject();
//				authorJson.put("id", u.getId());
//				authorJson.put("username", u.getUsername());
//				authorJson.put("email", u.getEmail());
//				authorJson.put("created_time", u.getCreated_time());
//				authorJson.put("status", u.getStatus());
//				authorJson.put("introduction", u.getIntroduction());
//				authorJson.put("avatar_image", aiJson);
//				authorJson.put("cover_image", ciJson);
//				ci.setAuthor(authorJson);
//				collection_info.add(ci);
//			}
//		}
//		return Response.status(Response.Status.OK).entity(collection_info).build();
//
//	}
//
	@Override
	public Response getCollectionsAll(Long loginUserid) {
		List<JSONObject> collection_info = new ArrayList<JSONObject>();
		List<Collection> cList = null;
		cList = collectionDao.getCollections();
		JSONObject coJson = null;
		if ((cList != null) && (cList.size() > 0)) {
			for (Collection co : cList) {
				coJson = new JSONObject();
				coJson.put("id", co.getId());
				coJson.put("collection_name", co.getCollection_name());
//				coJson.put("cover_image", JSONObject.fromObject(co.getCover_image()));
				coJson.put("type",co.getType());
				coJson.put("category",20);
				collection_info.add(coJson);
			}
		}
		return Response.status(Response.Status.OK).entity(collection_info).build();

	}
//
//	@Override
//	public List<UserIntro> getFollowingByCollectionId(Long collection_id, HttpServletRequest request,
//			Long loginUserid) {
//
//		log.debug("***get user_id ��ע��user info");
//		String countStr = request.getParameter("count");
//		String sinceIdStr = request.getParameter("since_id");
//		String maxIdStr = request.getParameter("max_id");
//		List<UserIntro> userIntroList = new ArrayList<UserIntro>();
//		int count = 20;
//		Follow f = null;
//		Follow followed = null;
//		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			List<User> userList = userCollectionDao.getUserByCollectionId(collection_id, count);
//			if ((userList != null) && (userList.size() > 0))
//				for (User user : userList) {
//
//					UserIntro userIntro = new UserIntro();
//					userIntro.setId((Long) user.getId());
//					userIntro.setIntroduction(user.getIntroduction());
//					userIntro.setUsername(user.getUsername());
//					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//						JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
//						userIntro.setAvatar_image(avatarJson);
//					} else {
//						userIntro.setAvatar_image(null);
//					}
//					if(loginUserid != null && loginUserid > 0){
//						f = this.followDao.getFollow(user.getId(), loginUserid);
//						if (f != null)
//							userIntro.setIs_following_current_user(true);
//						else {
//							userIntro.setIs_following_current_user(false);
//						}
//						followed = this.followDao.getFollow(loginUserid, user.getId());
//						if (followed != null)
//							userIntro.setFollowed_by_current_user(true);
//						else {
//							userIntro.setFollowed_by_current_user(false);
//						}
//					}else{
//						userIntro.setIs_following_current_user(false);
//						userIntro.setFollowed_by_current_user(false);
//					}
//					
//					userIntro.setUser_type(user.getUser_type());
//					userIntroList.add(userIntro);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//
//			List<User> userList = userCollectionDao.getUserByCollectionId(collection_id, count);
//			if ((userList != null) && (userList.size() > 0))
//				for (User user : userList) {
//
//					UserIntro userIntro = new UserIntro();
//					userIntro.setId((Long) user.getId());
//					userIntro.setIntroduction(user.getIntroduction());
//					userIntro.setUsername(user.getUsername());
//					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//						JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
//						userIntro.setAvatar_image(avatarJson);
//					} else {
//						userIntro.setAvatar_image(null);
//					}
//					if(loginUserid != null && loginUserid > 0){
//						f = this.followDao.getFollow(user.getId(), loginUserid);
//						if (f != null)
//							userIntro.setIs_following_current_user(true);
//						else {
//							userIntro.setIs_following_current_user(false);
//						}
//						followed = this.followDao.getFollow(loginUserid, user.getId());
//						if (followed != null)
//							userIntro.setFollowed_by_current_user(true);
//						else {
//							userIntro.setFollowed_by_current_user(false);
//						}
//					}else{
//						userIntro.setIs_following_current_user(false);
//						userIntro.setFollowed_by_current_user(false);
//					}
//					userIntro.setUser_type(user.getUser_type());
//					userIntroList.add(userIntro);
//				}
//
//		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			List<User> userList = userCollectionDao.getUserByCollectionIdPage(collection_id, count, since_id, 1);
//			if ((userList != null) && (userList.size() > 0))
//				for (User user : userList) {
//
//					UserIntro userIntro = new UserIntro();
//					userIntro.setId((Long) user.getId());
//					userIntro.setIntroduction(user.getIntroduction());
//					userIntro.setUsername(user.getUsername());
//					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//						JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
//						userIntro.setAvatar_image(avatarJson);
//					} else {
//						userIntro.setAvatar_image(null);
//					}
//					if(loginUserid != null && loginUserid > 0){
//						f = this.followDao.getFollow(user.getId(), loginUserid);
//						if (f != null)
//							userIntro.setIs_following_current_user(true);
//						else {
//							userIntro.setIs_following_current_user(false);
//						}
//						followed = this.followDao.getFollow(loginUserid, user.getId());
//						if (followed != null)
//							userIntro.setFollowed_by_current_user(true);
//						else {
//							userIntro.setFollowed_by_current_user(false);
//						}
//					}else{
//						userIntro.setIs_following_current_user(false);
//						userIntro.setFollowed_by_current_user(false);
//					}
//					userIntro.setUser_type(user.getUser_type());
//					userIntroList.add(userIntro);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
//				&& (Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
//			List<User> userList = userCollectionDao.getUserByCollectionIdPage(collection_id, count, since_id, 1);
//			if ((userList != null) && (userList.size() > 0))
//				for (User user : userList) {
//
//					UserIntro userIntro = new UserIntro();
//					userIntro.setId((Long) user.getId());
//					userIntro.setIntroduction(user.getIntroduction());
//					userIntro.setUsername(user.getUsername());
//					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//						JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
//						userIntro.setAvatar_image(avatarJson);
//					} else {
//						userIntro.setAvatar_image(null);
//					}
//					if(loginUserid != null && loginUserid > 0){
//						f = this.followDao.getFollow(user.getId(), loginUserid);
//						if (f != null)
//							userIntro.setIs_following_current_user(true);
//						else {
//							userIntro.setIs_following_current_user(false);
//						}
//						followed = this.followDao.getFollow(loginUserid, user.getId());
//						if (followed != null)
//							userIntro.setFollowed_by_current_user(true);
//						else {
//							userIntro.setFollowed_by_current_user(false);
//						}
//					}else{
//						userIntro.setIs_following_current_user(false);
//						userIntro.setFollowed_by_current_user(false);
//					}
//					userIntro.setUser_type(user.getUser_type());
//					userIntroList.add(userIntro);
//				}
//		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			List<User> userList = userCollectionDao.getUserByCollectionIdPage(collection_id, count, max_id, 2);
//			if ((userList != null) && (userList.size() > 0))
//				for (User user : userList) {
//
//					UserIntro userIntro = new UserIntro();
//					userIntro.setId((Long) user.getId());
//					userIntro.setIntroduction(user.getIntroduction());
//					userIntro.setUsername(user.getUsername());
//					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//						JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
//						userIntro.setAvatar_image(avatarJson);
//					} else {
//						userIntro.setAvatar_image(null);
//					}
//					if(loginUserid != null && loginUserid > 0){
//						f = this.followDao.getFollow(user.getId(), loginUserid);
//						if (f != null)
//							userIntro.setIs_following_current_user(true);
//						else {
//							userIntro.setIs_following_current_user(false);
//						}
//						followed = this.followDao.getFollow(loginUserid, user.getId());
//						if (followed != null)
//							userIntro.setFollowed_by_current_user(true);
//						else {
//							userIntro.setFollowed_by_current_user(false);
//						}
//					}else{
//						userIntro.setIs_following_current_user(false);
//						userIntro.setFollowed_by_current_user(false);
//					}
//					userIntro.setUser_type(user.getUser_type());
//					userIntroList.add(userIntro);
//				}
//		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
//				&& (!Strings.isNullOrEmpty(maxIdStr))) {
//			count = Integer.parseInt(countStr);
//			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
//			List<User> userList = userCollectionDao.getUserByCollectionIdPage(collection_id, count, max_id, 2);
//			if ((userList != null) && (userList.size() > 0))
//				for (User user : userList) {
//
//					UserIntro userIntro = new UserIntro();
//					userIntro.setId((Long) user.getId());
//					userIntro.setIntroduction(user.getIntroduction());
//					userIntro.setUsername(user.getUsername());
//					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
//						JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
//						userIntro.setAvatar_image(avatarJson);
//					} else {
//						userIntro.setAvatar_image(null);
//					}
//					if(loginUserid != null && loginUserid > 0){
//						f = this.followDao.getFollow(user.getId(), loginUserid);
//						if (f != null)
//							userIntro.setIs_following_current_user(true);
//						else {
//							userIntro.setIs_following_current_user(false);
//						}
//						followed = this.followDao.getFollow(loginUserid, user.getId());
//						if (followed != null)
//							userIntro.setFollowed_by_current_user(true);
//						else {
//							userIntro.setFollowed_by_current_user(false);
//						}
//					}else{
//						userIntro.setIs_following_current_user(false);
//						userIntro.setFollowed_by_current_user(false);
//					}
//					userIntro.setUser_type(user.getUser_type());
//					userIntroList.add(userIntro);
//				}
//
//		}
//
//		return userIntroList;
//
//	}
//
	@Override
	public Response getCollectionByFollow(Long user_id, HttpServletRequest request) {
		
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		int count = 20;
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
		map.put("user_id", user_id.toString());
		map.put("data_type", "50");
		map.put("ip",ip);
		map.put("device",device);
		List<Collection> cList = null;
		if ((Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			map.put("page_count", String.valueOf(count));
		} else if ((!Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			map.put("page_count", String.valueOf(count));
		}else if ((Strings.isNullOrEmpty(countStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			int max_id = Integer.parseInt(maxIdStr);
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", String.valueOf(max_id));
		} else if ((!Strings.isNullOrEmpty(countStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			map.put("page_count", String.valueOf(count));
			map.put("prev_id", String.valueOf(max_id));
		}
		String params = "";
		try {
			params = carPublicParam(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = HttpUtil.sendGetStr(car_url+"/member/follow-data-list", params);
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			JSONObject idJson = null;
			if(code == 10000){
				JSONArray arr = resJson.getJSONArray("data");
				List<JSONObject> coJsonList = new ArrayList<JSONObject>();
				JSONObject coJson = null;
				if(arr != null && arr.size() > 0){
					for(Object o:arr){
						idJson = JSONObject.fromObject(o);
						int collection_id = idJson.getInt("data_id");
						Collection c = collectionDao.get(collection_id);
						coJson = new JSONObject();
						coJson.put("id", c.getId());
						coJson.put("collection_name", c.getCollection_name());
						coJson.put("cover_image", JSONObject.fromObject(c.getCover_image()));
						coJson.put("type",c.getType());
						coJsonList.add(coJson);
					}
				}
				
				return Response.status(Response.Status.OK).entity(coJsonList).build();
			}else if(code == 10001){
				JSONObject json = new JSONObject();
				json.put("status", "缺少参数");
				json.put("code", 10810);
				json.put("error_message", "缺少参数");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
		}
		return null;
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
	public Response getCollectionByCollection_id(Long collectionId, HttpServletRequest request,
			Long loginUserid)throws Exception {
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
		Collection co = collectionDao.getCollectionById(collectionId);
		
		Map<String,String> cMap = new HashMap<String,String>();
		cMap.put("ip", ip);
		cMap.put("device", device);
		cMap.put("collection_id", String.valueOf(co.getId()));
		if(loginUserid != null && loginUserid > 0){
			cMap.put("user_id", loginUserid.toString());
		}
		
		String cparams = carPublicParam(cMap);
		String cResult = HttpUtil.sendGetStr(car_url+"/collection/get", cparams);
		JSONObject ci = null;
		if(co != null){
			ci = new JSONObject();
			
			ci.put("id",co.getId());
			ci.put("collection_name",co.getCollection_name());
			ci.put("cover_image",JSONObject.fromObject(co.getCover_image()));
			ci.put("description", co.getDescription());
			int type = co.getType();
			Collection collection = null;
			List<JSONObject> clubJsonList = new ArrayList<JSONObject>();
			List<JSONObject> carJsonList = new ArrayList<JSONObject>();
			JSONObject clubJson = null;
			JSONObject carsJson = null;
			JSONObject areaJson = null;
			if(type == 30){
				List<CarClub> ccList = carClubDao.getCarClubListByCar_id(co.getId());
				if(ccList != null && ccList.size() > 0){
					for(CarClub cc:ccList){
						clubJson = new JSONObject();
						collection = collectionDao.get(cc.getClub_id());
						clubJson.put("id", collection.getId());
						clubJson.put("club_name", collection.getCollection_name());
						clubJson.put("cover_image", JSONObject.fromObject(collection.getCover_image()));
						clubJson.put("logo", collection.getLogo());
						areaJson = new JSONObject();
						Districts d = collection.getDistricts();
						Long area_id = d.getId();
						Long parent_id = d.getParent_id();
						Districts parent = districtsDao.get(parent_id);
						areaJson.put("id", area_id);
						areaJson.put("area_name", parent.getName()+d.getName());
						clubJson.put("area",areaJson);
						
						clubJsonList.add(clubJson);
					}
					ci.put("clubs", clubJsonList);
				}
				ci.put("car_url", co.getCar_url());
			}else if(type == 60){
				List<CarClub> ccList = carClubDao.getCarClubListByClub_id(co.getId());
				if(ccList != null && ccList.size() > 0){
					for(CarClub cc:ccList){
						collection = collectionDao.get(cc.getCar_id());
						carsJson = new JSONObject();
						carsJson.put("id", collection.getId());
						carsJson.put("car_name", collection.getCollection_name());
						carJsonList.add(carsJson);
					}
					ci.put("cars", clubJsonList);
				}
				Districts d = co.getDistricts();
				Long parent_id = d.getParent_id();
				areaJson = new JSONObject();
				Districts di = districtsDao.get(parent_id);
				areaJson.put("id", d.getId());
				areaJson.put("area_name", di.getName()+d.getName());
				ci.put("area",areaJson);
				
				
			}
			JSONObject coJson = JSONObject.fromObject(cResult);
			int codes = coJson.getInt("code");
			if(codes == 10000){
				JSONObject dJson = coJson.getJSONObject("data");
				int follow_count = dJson.getInt("follow_count");
				ci.put("followers_count",follow_count);
				int content_count = dJson.getInt("content_count");
				ci.put("story_count",content_count);
				if(dJson.containsKey("is_follow")){
					boolean bool = dJson.getBoolean("is_follow");
					ci.put("is_followed_by_current_user",bool);
				}else{
					ci.put("is_followed_by_current_user",false);
				}
				
			}else if(codes == 16002){
				resp.put("status", "栏目不存在");
				resp.put("code", 10506);
				resp.put("error_message", "栏目不存在");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
			return Response.status(Response.Status.OK).entity(ci).build();
		}else{
			resp.put("status", "数据错误");
			resp.put("code", 10540);
			resp.put("error_message", "数据错误");
			return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
		}
		
		
	
	}
	@Override
	public Response getAllStoriesByCollection_id(Long collectionId, HttpServletRequest request, Long loginUserid)
			throws Exception {
		Object obj = request.getAttribute("X-Tella-Request-Userid");
		if(obj != null){
			String loginUserids = obj.toString();
			loginUserid = Long.parseLong(loginUserids);
		}
		JSONObject resp = new JSONObject();
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
		int count = 20;
		
		
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("relation_type", "20");
		map.put("ip", ip);
		map.put("device", device);
		map.put("is_highlighted", "0");
		map.put("data_type", "50");
		map.put("data_id", String.valueOf(collectionId));
		if ((Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			map.put("page_count", String.valueOf(count));
		} else if ((!Strings.isNullOrEmpty(countStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			map.put("page_count", String.valueOf(count));
		}	else if ((Strings.isNullOrEmpty(countStr))
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
		if(loginUserid != null && loginUserid > 0){
			map.put("user_id", loginUserid.toString());
		}
		String params = carPublicParam(map);
		String result = HttpUtil.sendGetStr(car_url+"/content/get-list", params);
		List<JSONObject> cJsonList = new ArrayList<JSONObject>();
		if(!Strings.isNullOrEmpty(result)){
			JSONObject resJson = JSONObject.fromObject(result);
			int code = resJson.getInt("code");
			if(code == 10000){
				
				JSONArray arr = resJson.getJSONArray("data");
				JSONObject contentJson = null;
				
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
					
				}
			
		
			}else if (code == 14005){
				resp.put("status", "关系对象类型错误");
				resp.put("code", 10504);
				resp.put("error_message", "关系对象类型错误");
				return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
			}
		}
	
		
		return Response.status(Response.Status.OK).entity(cJsonList).build();
	}
	@Override
	public Response getAreaInfo(HttpServletRequest request) {
		List<Districts> hotList = districtsDao.getDistrictsByHot(1);
		List<Districts> provinceList = districtsDao.getDistrictsByParentId(0l);
		JSONObject areaJson = new JSONObject();
		List<JSONObject> hotJsonList = new ArrayList<JSONObject>();
		List<JSONObject> provinceJsonList = new ArrayList<JSONObject>();
		JSONObject hotJson = null;
		JSONObject provinceJson = null;
		if(hotList != null && hotList.size() > 0){
			for(Districts d:hotList){
				hotJson = new JSONObject();
				hotJson.put("id", d.getId());
				hotJson.put("name", d.getName());
				hotJsonList.add(hotJson);
			}
			areaJson.put("hot",hotJsonList);
		}
		
		if(provinceList != null && provinceList.size() > 0){
			for(Districts d:provinceList){
				provinceJson = new JSONObject();
				provinceJson.put("id", d.getId());
				provinceJson.put("name", d.getName());
				provinceJsonList.add(provinceJson);
			}
			areaJson.put("province",provinceJsonList);
		}
		return Response.status(Response.Status.OK).entity(areaJson).build();
	}
	@Override
	public Response getAreaById(Long area_id) {
		List<Districts> provinceList = districtsDao.getDistrictsByParentId(area_id);
		List<JSONObject> provinceJsonList = new ArrayList<JSONObject>();
		JSONObject provinceJson = null;
		
		if(provinceList != null && provinceList.size() > 0){
			for(Districts d:provinceList){
				provinceJson = new JSONObject();
				provinceJson.put("id", d.getId());
				provinceJson.put("name", d.getName());
				provinceJsonList.add(provinceJson);
			}
		}
		return Response.status(Response.Status.OK).entity(provinceJsonList).build();
	}
	@Override
	public Response getClubByCar(int car_id,HttpServletRequest request) {
		List<CarClub> ccList = carClubDao.getCarClubListByCar_id(car_id);
		Collection collection = null;
		List<JSONObject> clubJsonList = new ArrayList<JSONObject>();
		Districts d = null;
		Districts d1 = null;
		JSONObject clubJson = null;
		JSONObject areaJson = null;
		if(ccList != null && ccList.size() > 0){
			for(CarClub cc:ccList){
				clubJson = new JSONObject();
				areaJson = new JSONObject();
				collection = collectionDao.getCollectionById(cc.getClub_id());
				clubJson.put("id", collection.getId());
				clubJson.put("cover_image",JSONObject.fromObject(collection.getCover_image()));
				clubJson.put("club_name", collection.getCollection_name());
				clubJson.put("logo", JSONObject.fromObject(collection.getLogo()));
				d = collection.getDistricts();
				d1 = districtsDao.get(d.getParent_id());
				areaJson.put("id",d.getId());
				areaJson.put("area_name", d1.getName()+" "+d.getName());
				clubJson.put("area", areaJson);
				clubJsonList.add(clubJson);
			}
		}
		return Response.status(Response.Status.OK).entity(clubJsonList).build();
	}
	@Override
	public Response getClubByName(HttpServletRequest request) {
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		String districtsId = request.getParameter("districts_id");
		String collection_name = request.getParameter("collection_name");
		int count = 20;
		List<Collection> coList = null;
		Districts di = districtsDao.get(Long.parseLong(districtsId));
		if(!Strings.isNullOrEmpty(collection_name)){
			if ((Strings.isNullOrEmpty(countStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				coList = collectionDao.getCollectionListByParams(count,di.getParent_id(),collection_name);
			} else if ((!Strings.isNullOrEmpty(countStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				coList = collectionDao.getCollectionListByParams(count,di.getParent_id(),collection_name);
			}	else if ((Strings.isNullOrEmpty(countStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				int max_id = Integer.parseInt(maxIdStr);
				coList = collectionDao.getCollectionListByParams(count,di.getParent_id(),max_id,collection_name);
			} else if ((!Strings.isNullOrEmpty(countStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				int max_id = Integer.parseInt(maxIdStr);
				coList = collectionDao.getCollectionListByParams(count,di.getParent_id(),max_id,collection_name);
			}
		}else{
			if ((Strings.isNullOrEmpty(countStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				coList = collectionDao.getCollectionListByParams(count,di.getParent_id());
			} else if ((!Strings.isNullOrEmpty(countStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				coList = collectionDao.getCollectionListByParams(count,di.getParent_id());
			}	else if ((Strings.isNullOrEmpty(countStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				int max_id = Integer.parseInt(maxIdStr);
				coList = collectionDao.getCollectionListByParams(count,di.getParent_id(),max_id);
			} else if ((!Strings.isNullOrEmpty(countStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				int max_id = Integer.parseInt(maxIdStr);
				coList = collectionDao.getCollectionListByParams(count,di.getParent_id(),max_id);
			}
		}
		
		List<JSONObject> clubJsonList = new ArrayList<JSONObject>();
		Districts d = null;
		Districts d1 = null;
		JSONObject clubJson = null;
		JSONObject areaJson = null;
		if(coList != null && coList.size() > 0){
			for(Collection collection:coList){
				clubJson = new JSONObject();
				areaJson = new JSONObject();
				clubJson.put("id", collection.getId());
				clubJson.put("cover_image",JSONObject.fromObject(collection.getCover_image()));
				clubJson.put("club_name", collection.getCollection_name());
				clubJson.put("logo", JSONObject.fromObject(collection.getLogo()));
				d = collection.getDistricts();
				d1 = districtsDao.get(d.getParent_id());
				areaJson.put("id",d.getId());
				areaJson.put("area_name", d1.getName()+" "+d.getName());
				clubJson.put("area", areaJson);
				clubJsonList.add(clubJson);
			}
		}
		return Response.status(Response.Status.OK).entity(clubJsonList).build();
	}
	@Override
	public Response getCategoryCollection(HttpServletRequest request) {
		List<CategoryCollection> ccList = categoryCollectionDao.getCategoryCollectionList();
		List<Category> cList = categoryCollectionDao.getCategoryList();
		List<JSONObject> ccJsonList = new ArrayList<JSONObject>();
		JSONObject ccJson = null;
		List<JSONObject> collectionJsonList = new ArrayList<JSONObject>();
		JSONObject collectionJson = null;
		Collection collection = null;
		if(cList != null && cList.size() > 0){
			for(Category cate:cList){
				ccJson = new JSONObject();
				ccJson.put("category_name",cate.getName());
				if(ccList != null && ccList.size() > 0){
					for(CategoryCollection cc:ccList){
						if(cc.getCategory() == cate){
							collection = cc.getCollection();
							collectionJson = new JSONObject();
							collectionJson.put("id", collection.getId());
							collectionJson.put("collection_name", collection.getCollection_name());
							collectionJson.put("type", collection.getType());
							collectionJsonList.add(collectionJson);
						}
					}
				}
				
				ccJson.put("collections", collectionJsonList);
				ccJsonList.add(ccJson);
			}
		}
		return Response.status(Response.Status.OK).entity(ccJsonList).build();
	}
	
	@Override
	public Response getCategoryCollectionDetail(HttpServletRequest request) {
		List<CategoryCollection> ccList = categoryCollectionDao.getCategoryCollectionList();
		List<Category> cList = categoryCollectionDao.getCategoryList();
		List<JSONObject> ccJsonList = new ArrayList<JSONObject>();
		JSONObject ccJson = null;
		List<JSONObject> collectionJsonList = null;
		JSONObject collectionJson = null;
		Collection collection = null;
		if(cList != null && cList.size() > 0){
			for(Category cate:cList){
				ccJson = new JSONObject();
				ccJson.put("category_name",cate.getName());
				collectionJsonList = new ArrayList<JSONObject>();
				if(ccList != null && ccList.size() > 0){
					for(CategoryCollection cc:ccList){
						if(cc.getCategory() == cate){
							collection = cc.getCollection();
							collectionJson = new JSONObject();
							collectionJson.put("id", collection.getId());
							collectionJson.put("collection_name", collection.getCollection_name());
							int type = collection.getType();
							collectionJson.put("type", type);
							if(type == 30 || type == 50){
								collectionJson.put("cover_image", JSONObject.fromObject(collection.getThumbnail()));
								if(!Strings.isNullOrEmpty(collection.getDescription())){
									collectionJson.put("description", collection.getDescription());
								}
							}else if(type == 60){
								collectionJson.put("cover_image", JSONObject.fromObject(collection.getThumbnail()));
								collectionJson.put("logo",JSONObject.fromObject(collection.getLogo()));
								Districts d = collection.getDistricts();
								Districts d1 = districtsDao.get(d.getParent_id());
								JSONObject areaJson = new JSONObject();
								areaJson.put("id",d.getId());
								areaJson.put("area_name", d1.getName()+" "+d.getName());
								collectionJson.put("area",areaJson);
							}
							collectionJsonList.add(collectionJson);
						}
					}
				}
				
				ccJson.put("collections", collectionJsonList);
				ccJsonList.add(ccJson);
			}
		}
		return Response.status(Response.Status.OK).entity(ccJsonList).build();
	}
	
	@Override
	public Response getCollectionsNav() {

		List<JSONObject> navJsonList = new ArrayList<JSONObject>();
		JSONObject navJson = null;
		List<Navigation> navList = navigationDao.getNavigationBySequence();
		Collection c = null;
		if(navList != null && navList.size() > 0){
			for(Navigation nav:navList){
				navJson = new JSONObject();
//				navJson.put("id", nav.getId());
				navJson.put("id", nav.getCollection_id());
				c = collectionDao.get(nav.getCollection_id());
				navJson.put("collection_name", c.getCollection_name());
				navJson.put("type",c.getType());
				navJson.put("sequence", nav.getSequence());
				navJson.put("category",20);
				navJsonList.add(navJson);
			}
		}
		return Response.status(Response.Status.OK).entity(navJsonList).build();
	
	}
}
