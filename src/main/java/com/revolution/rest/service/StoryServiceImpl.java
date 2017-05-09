package com.revolution.rest.service;

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
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.revolution.rest.common.FBEncryption;
import com.revolution.rest.common.HttpUtil;
import com.revolution.rest.common.ParseFile;
import com.revolution.rest.common.PushNotificationUtil;
import com.revolution.rest.dao.CollectionDao;
import com.revolution.rest.dao.CollectionStoryDao;
import com.revolution.rest.dao.CommentDao;
import com.revolution.rest.dao.ConfigurationDao;
import com.revolution.rest.dao.FMapDao;
import com.revolution.rest.dao.FollowDao;
import com.revolution.rest.dao.LikesDao;
import com.revolution.rest.dao.LinkAccountsDao;
import com.revolution.rest.dao.NotificationDao;
import com.revolution.rest.dao.PushNotificationDao;
import com.revolution.rest.dao.ReportDao;
import com.revolution.rest.dao.RepublishDao;
import com.revolution.rest.dao.StoryDao;
import com.revolution.rest.dao.StoryElementDao;
import com.revolution.rest.dao.TimelineDao;
import com.revolution.rest.dao.UserCentreDao;
import com.revolution.rest.dao.UserCollectionDao;
import com.revolution.rest.dao.UserDao;
import com.revolution.rest.model.Collection;
import com.revolution.rest.model.CollectionStory;
import com.revolution.rest.model.Columns;
import com.revolution.rest.model.Comment;
import com.revolution.rest.model.Configuration;
import com.revolution.rest.model.FMap;
import com.revolution.rest.model.Follow;
import com.revolution.rest.model.Likes;
import com.revolution.rest.model.LinkAccounts;
import com.revolution.rest.model.Notification;
import com.revolution.rest.model.PublisherInfo;
import com.revolution.rest.model.PushNotification;
import com.revolution.rest.model.Report;
import com.revolution.rest.model.Republish;
import com.revolution.rest.model.Story;
import com.revolution.rest.model.StoryElement;
import com.revolution.rest.model.Timeline;
import com.revolution.rest.model.User;
import com.revolution.rest.model.UserCentre;
import com.revolution.rest.service.model.CollectionIntro;
import com.revolution.rest.service.model.CommentModel;
import com.revolution.rest.service.model.CommentStoryModel;
import com.revolution.rest.service.model.CommentSummaryModel;
import com.revolution.rest.service.model.CoverMedia;
import com.revolution.rest.service.model.EventModel;
import com.revolution.rest.service.model.IframeCover;
import com.revolution.rest.service.model.ImageCover;
import com.revolution.rest.service.model.LineCover;
import com.revolution.rest.service.model.LinkModel;
import com.revolution.rest.service.model.LinkModels;
import com.revolution.rest.service.model.LocationModel;
import com.revolution.rest.service.model.PublisherInfoModel;
import com.revolution.rest.service.model.ReplyCommentModel;
import com.revolution.rest.service.model.StoryElementModel;
import com.revolution.rest.service.model.StoryEvent;
import com.revolution.rest.service.model.StoryIntro;
import com.revolution.rest.service.model.StoryIntros;
import com.revolution.rest.service.model.StoryLastModel;
import com.revolution.rest.service.model.StoryModel;
import com.revolution.rest.service.model.StoryPageModel;
import com.revolution.rest.service.model.TextCover;
import com.revolution.rest.service.model.UserIntro;
import com.revolution.rest.service.model.VideoCover;

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
	private LikesDao likesDao;

	@Autowired
	private RepublishDao republishDao;

	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private FollowDao followDao;

	@Autowired
	private ReportDao reportDao;

	@Autowired
	private ConfigurationDao configurationDao;

	@Autowired
	private PushNotificationDao pushNotificationDao;

	@Autowired
	private CollectionStoryDao collectionStoryDao;

	@Autowired
	private UserCollectionDao userCollectionDao;
	
	@Autowired
	private LinkAccountsDao linkAccountsDao;
	
	@Autowired
	private UserCentreDao userCentreDao;
	
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

	public Response createStory(JSONObject storyModel, Long loginUserid, HttpServletRequest request) {
		log.debug("create story");
		try {
			if (storyModel != null) {
				Story story = new Story();

				if (storyModel.containsKey("title"))
					story.setTitle(storyModel.getString("title"));

				if (storyModel.containsKey("cover_media"))
					story.setCover_page(storyModel.getString("cover_media"));
				else {
					story.setCover_page(null);
				}

				if (storyModel.containsKey("summary"))
					story.setSummary(storyModel.getString("summary"));
				else {
					story.setSummary(null);
				}
				story.setRecommendation(false);

				if (storyModel.containsKey("image_count")) {
					story.setImage_count(storyModel.getInt("image_count"));
				}
				story.setStatus("publish");
				story.setUpdate_time(new Date());
				story.setLast_comment_date(new Date());
				User user = this.userDao.get(loginUserid);
				if (user != null) {
					story.setUser(user);
				}
				JSONArray jsonArray = JSONArray.fromObject(storyModel.getString("elements"));
				List<StoryElement> seSet = new ArrayList<StoryElement>();
				JSONObject jo = null;
				if ((jsonArray != null) && (jsonArray.size() > 0)) {
					StoryElement element = null;
					for (int i = 0; i < jsonArray.size(); i++) {
						element = new StoryElement();
						jo = (JSONObject) jsonArray.get(i);
						element.setGrid_size(jo.getString("grid_size"));
						element.setLayout_type(jo.getString("layout_type"));
						element.setContents(jo.getString("content"));
						element.setStoryinfo(story);
						seSet.add(element);
					}
				}
				story.setElements(seSet);
				
				
				
				
				this.storyDao.save(story);
				log.debug("start add activity$$$$$$$$$$$$$$$$$$$$$$$$$$$" + story.getUser().getId() + " -->"
						+ story.getId());
				story.setTinyURL(makeURL(story.getId()));
				storyDao.update(story);
				
				JSONArray collection_id = null;
				if (storyModel.containsKey("collection_id")) {
					collection_id = storyModel.getJSONArray("collection_id");
					if(collection_id != null && collection_id.size() > 0){
						Object[] ids = collection_id.toArray();
						for(Object id:ids){
							CollectionStory cs = new CollectionStory();
							Collection collection = collectionDao.get(Long.parseLong(id.toString()));
							cs.setStory(story);
							cs.setCollection(collection);
							collectionStoryDao.save(cs);
						}
					}
					

				}
				List<Follow> followList = this.followDao.getFollowersByUserId(story.getUser().getId());
				Timeline timeline = new Timeline();
				timeline.setCreatorId(loginUserid);
				timeline.setTargetUserId(loginUserid);
				timeline.setStory(story);
				timeline.setType("post");
				timeline.setReferenceId((Long) story.getId());
				timeline.setCreateTime(new Date());
				this.timelineDao.save(timeline);
				log.debug("***add activity success***");
				EventModel event = getEventModelListByLoginUserid(timeline, loginUserid, collection_id);
				log.debug("***start add notification***" + JSONObject.fromObject(event));
				if (!user.getUser_type().equals("media")) {
					List<Notification> notificationList = new ArrayList<Notification>();
					Notification n = null;
					if ((followList != null) && (followList.size() > 0)) {
						for (Follow follow : followList) {
							Long recipientId = (Long) follow.getPk().getUser().getId();
							n = new Notification();
							n.setRecipientId(recipientId);
							n.setSenderId((Long) story.getUser().getId());
							n.setNotificationType(4);
							n.setObjectType(1);
							n.setObjectId((Long) story.getId());
							n.setStatus("enabled");
							n.setRead_already(true);
							notificationList.add(n);
						}
					}
					List<User> userList = this.userDao.getUserByUserType();
					if ((userList != null) && (userList.size() > 0)) {
						for (User u : userList) {
							n = new Notification();
							n.setRecipientId((Long) u.getId());
							n.setSenderId((Long) story.getUser().getId());
							n.setNotificationType(4);
							n.setObjectType(1);
							n.setObjectId((Long) story.getId());
							n.setStatus("enabled");
							n.setRead_already(true);
							notificationList.add(n);
						}
					}
					this.notificationDao.saveNotifications(notificationList);

				}

				log.debug("***add notification success***");

				return Response.status(Response.Status.OK).entity(event).build();
			}

			JSONObject jo = new JSONObject();
			jo.put("status", "invalid_request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "Invalid request payload");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		} catch (Exception e) {
			e.printStackTrace();
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

	public Response getStory(Long storyId, Long loginUserid, String appVersion) {

		log.debug("***get story begin *****");
		Story story = this.storyDao.getStoryByIdAndStatus(storyId, "publish");

		StoryLastModel storyModel = new StoryLastModel();

		if (story != null) {
			storyModel.setId(storyId);
			User user = story.getUser();
			if (loginUserid == null || loginUserid < 0) {
				loginUserid = 0l;
			}
			if (loginUserid.equals(story.getUser().getId())) {
				JSONObject avatarImageJson = null;
				if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
					avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
				}

				JSONObject coverImageJson = null;
				if (!Strings.isNullOrEmpty(user.getCoverImage())) {
					coverImageJson = JSONObject.fromObject(user.getCoverImage());
				}
				JSONObject authorJson = new JSONObject();
				authorJson.put("id", user.getId());
				authorJson.put("username", user.getUsername());
				authorJson.put("email", user.getEmail());
				authorJson.put("created_time", user.getCreated_time());
				authorJson.put("status", user.getStatus());
				authorJson.put("introduction", user.getIntroduction());
				authorJson.put("avatar_image", avatarImageJson);
				authorJson.put("cover_image", coverImageJson);
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
				storyModel.setAuthor(authorJson);
			} else {
				Follow loginUserFollowAuthor = null;

				Follow AuthorFollowLoginUser = null;
				if (loginUserid != null && loginUserid > 0) {
					loginUserFollowAuthor = this.followDao.getFollow(loginUserid, story.getUser().getId());
					AuthorFollowLoginUser = this.followDao.getFollow(story.getUser().getId(), loginUserid);
				}

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

				JSONObject authorJson = new JSONObject();
				authorJson.put("id", user.getId());
				authorJson.put("username", user.getUsername());
				authorJson.put("email", user.getEmail());
				authorJson.put("created_time", user.getCreated_time());
				authorJson.put("status", user.getStatus());
				authorJson.put("introduction", user.getIntroduction());
				authorJson.put("avatar_image", avatarImageJson);
				authorJson.put("cover_image", coverImageJson);
				authorJson.put("user_type", user.getUser_type());
				if (!Strings.isNullOrEmpty(user.getWebsite()))
					authorJson.put("website", user.getWebsite());
				else {
					authorJson.put("website", null);
				}
				authorJson.put("followed_by_current_user", followed_by_current_user);
				authorJson.put("is_following_current_user", is_following_current_user);
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
				
			}
			
			Set<Story> likeStory = null;
			if (loginUserid != null && loginUserid > 0) {
				User loginUser = userDao.get(loginUserid);
				likeStory = loginUser.getLike_story();
			}

			List<Story> lsList = new ArrayList<Story>();
			if (likeStory != null && likeStory.size() > 0) {
				Iterator<Story> it = likeStory.iterator();
				while (it.hasNext()) {
					lsList.add(it.next());
				}
				if (lsList.contains(story)) {
					storyModel.setLiked_by_current_user(true);
				} else {
					storyModel.setLiked_by_current_user(false);
				}
			} else {
				storyModel.setLiked_by_current_user(false);
			}
			
			Set<User> like_user = story.getLike_users();
			if(like_user != null && like_user.size() > 0){
				storyModel.setLike_count(like_user.size());
			}else{
				storyModel.setLike_count(0);
			}
			storyModel.setCreated_time(story.getCreated_time());
			storyModel.setUpdate_time(story.getUpdate_time());
			storyModel.setImage_count(story.getImage_count());

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

			List<StoryElementModel> storyElements = new ArrayList<StoryElementModel>();
			List<StoryElement> seSet = story.getElements();
			if ((seSet != null) && (seSet.size() > 0)) {
				StoryElementModel sem = null;
				JSONObject content = null;
				for (StoryElement element : seSet) {
					sem = new StoryElementModel();
					content = JSONObject.fromObject(element.getContents());
					sem.setContent(content);
					sem.setGrid_size(element.getGrid_size());
					sem.setLayout_type(element.getLayout_type());
					storyElements.add(sem);
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

			Set<Collection> cSet = story.getCollections();
			//List<Collection> cList = collectionStoryDao.getCollectionListByStoryId(story.getId());
			List<JSONObject> collectionListJson = new ArrayList<JSONObject>();
			if (cSet != null && cSet.size() > 0) {
				Iterator<Collection> iter = cSet.iterator();
				while(iter.hasNext()){
					Collection collection = iter.next();
					CollectionIntro ci = new CollectionIntro();
					ci.setId((Long) collection.getId());
					ci.setCollection_name(collection.getCollectionName());
					ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
					ci.setInfo(collection.getInfo());
					User u = userDao.get(collection.getUser().getId());
					JSONObject author = new JSONObject();
					author.put("id", u.getId());
					author.put("username", u.getUsername());
					if(!Strings.isNullOrEmpty(u.getAvatarImage())){
						author.put("avatar_image",JSONObject.fromObject(u.getAvatarImage()));
					}
					ci.setAuthor(author);
					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					
					int follow_collection_count = userCollectionDao.getCollectionByCount(collection.getId());
					ci.setFollowers_count(follow_collection_count);
					/*Set<User> uSet = collection.getUsers();
					if(uSet != null && uSet.size() > 0){
						ci.setFollowers_count(uSet.size());
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

					collectionListJson.add(collectionJson);
				}
				
				storyModel.setCollections(collectionListJson);
			}
			

			int count = 0;
			List<Comment> cList = commentDao.getCommentByStoryId(story.getId());
			if (cList != null && cList.size() > 0) {
				count = cList.size();
			}
			storyModel.setComment_count(count);
			storyModel.setSummary(story.getSummary());
			if (loginUserid != null && loginUserid > 0) {
				Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
				if (repost != null)
					storyModel.setRepost_by_current_user(true);
				else {
					storyModel.setRepost_by_current_user(false);
				}
			} else {
				storyModel.setRepost_by_current_user(false);
			}

			int view_count = story.getViewTimes();
			view_count++;
			story.setViewTimes(view_count);
			this.storyDao.update(story);
			log.debug("***get story model***" + JSONObject.fromObject(storyModel));
			List<Comment> commentList = this.commentDao.getComments(storyId, 20);
			if ((commentList != null) && (commentList.size() > 0)) {
				List<JSONObject> commentModelList = new ArrayList<JSONObject>();

				if ((commentList != null) && (commentList.size() > 0)) {
					CommentStoryModel commentModel = null;
					for (Comment comment : commentList) {
						commentModel = getCommentStoryModel(comment);
						List<Comment> replies = this.commentDao.getReplyComments((Long) comment.getId(), 3);
						List<JSONObject> replyCommentModelList = new ArrayList<JSONObject>();
						if ((replies != null) && (replies.size() > 0)) {
							ReplyCommentModel replyCommentModel = null;
							for (Comment reply : replies) {
								replyCommentModel = getReplyCommentModel(reply);
								List<String> delArray = new ArrayList<String>();
								JsonConfig configs = new JsonConfig();

								if (replyCommentModel.getTarget_user() == null) {
									delArray.add("target_user");
								}

								JSONObject commentJson = null;
								if ((delArray != null) && (delArray.size() > 0)) {
									configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
									configs.setIgnoreDefaultExcludes(false);
									configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

									commentJson = JSONObject.fromObject(replyCommentModel, configs);
								} else {
									commentJson = JSONObject.fromObject(replyCommentModel);
								}
								replyCommentModelList.add(commentJson);
							}
						}
						commentModel.setReplies(replyCommentModelList);

						JsonConfig configs = new JsonConfig();
						List<String> delArray = new ArrayList<String>();

						if(Strings.isNullOrEmpty(comment.getComment_image())){
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
						commentModelList.add(commentJson);
					}
				}

				storyModel.setComments(commentModelList);
			}
			List<Story> storyList = this.storyDao.getStoriesByRandThree(story.getId());
			if ((storyList != null) && (storyList.size() > 0)) {
				List<StoryIntros> recommendations = new ArrayList<StoryIntros>();
				StoryIntros intro = null;
				for (Story s : storyList) {
					JSONObject author = new JSONObject();
					intro = new StoryIntros();
					intro.setId((Long) s.getId());
					intro.setTitle(s.getTitle());
					if (!Strings.isNullOrEmpty(s.getCover_page()))
						intro.setCover_media(JSONObject.fromObject(s.getCover_page()));
					else {
						intro.setCover_media(null);
					}
					intro.setImage_count(s.getImage_count());
					intro.setCollectionId(Long.valueOf(1L));
					intro.setCreated_time(s.getCreated_time());
					User us = s.getUser();
					author.put("id", us.getId());
					author.put("username", us.getUsername());
					if(!Strings.isNullOrEmpty(us.getAvatarImage())){
						author.put("avatar_image",JSONObject.fromObject(us.getAvatarImage()));
					}
					intro.setAuthor(author);
					
					Set<User> r_like_user = s.getLike_users();
					if(r_like_user != null && r_like_user.size() > 0){
						intro.setLike_count(r_like_user.size());
					}else{
						intro.setLike_count(0);
					}
					
					Set<Comment> r_comment = s.getComments();
					if(r_comment != null && r_comment.size()> 0){
						intro.setComment_count(r_comment.size());
					}else{
						intro.setComment_count(0);
					}
					
					if(!Strings.isNullOrEmpty(s.getTinyURL())){
						intro.setUrl(s.getTinyURL());
					}
					
					recommendations.add(intro);
				}
				storyModel.setRecommendation(recommendations);

			}
			String resource = story.getResource();

			JsonConfig configs = new JsonConfig();
			List<String> delArray = new ArrayList<String>();
			if (resource != null && !resource.equals("")) {
				storyModel.setResource(resource);
			} else {
				storyModel.setResource(null);
			}
			
			Set<Columns> sSet = story.getColumns();
			if(sSet != null && sSet.size() > 0){
				JSONObject columnsJson = new JSONObject();
				Iterator<Columns> iter = sSet.iterator();
				if(iter.hasNext()){
					Columns c = iter.next();
					columnsJson.put("id",c.getId());
					columnsJson.put("columns_name",c.getColumn_name());
					storyModel.setColumns(columnsJson);
				}
				
			}else{
				delArray.add("columns");
			}

			if (Strings.isNullOrEmpty(story.getResource())) {
				delArray.add("resource");
			}


			if (cSet == null || cSet.size() <= 0) {
				delArray.add("collection");
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
			System.out.println("-----story-->" + storyJson);
			return Response.status(Response.Status.OK).entity(storyJson).build();
		}
	

		JSONObject json = new JSONObject();
		json.put("status", "no_resource");
		json.put("code", "10012");
		json.put("error_message", "The story does not exist");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}

	public List<Story> getAllByCollectionId(Long collectionId) {
		return null;
	}

	public Response updateStory(Long storyId, JSONObject storyModel, Long loginUserid) {
		log.debug("*** start update story ***");
		try {
			if (storyModel != null) {
				Story story = (Story) this.storyDao.get(storyId);
				if (story != null) {
					if (loginUserid.equals(story.getUser().getId())) {
						List<StoryElement> s = story.getElements();
						if ((s != null) && (s.size() > 0)) {
							this.storyElementDao.delete(s);
						}

						log.debug("start add story$$$$$$$$$$$$$$$$$$$$$$$$$$$" + storyModel);
						if (storyModel.containsKey("title"))
							story.setTitle(storyModel.getString("title"));
						else {
							story.setTitle(null);
						}

						if (storyModel.containsKey("image_count"))
							story.setImage_count(storyModel.getInt("image_count"));
						else {
							story.setImage_count(0);
						}

						if (storyModel.containsKey("cover_media"))
							story.setCover_page(storyModel.getString("cover_media"));
						else {
							story.setCover_page(null);
						}

						story.setStatus("publish");
						story.setUpdate_time(new Date());
						User user = story.getUser();
						if (user != null) {
							story.setUser(user);
						}
						log.debug("start add Elements$$$$$$$$$$$$$$$$$$$$$$$$$$$" + storyModel.getString("elements"));
						JSONArray jsonArray = JSONArray.fromObject(storyModel.getString("elements"));
						List<StoryElement> seSet = new ArrayList<StoryElement>();
						if ((jsonArray != null) && (jsonArray.size() > 0)) {
							StoryElement element = null;
							for (int i = 0; i < jsonArray.size(); i++) {
								element = new StoryElement();
								JSONObject jo = (JSONObject) jsonArray.get(i);
								element.setGrid_size(jo.getString("grid_size"));
								element.setLayout_type(jo.getString("layout_type"));
								element.setContents(jo.getString("content"));
								element.setStoryinfo(story);
								seSet.add(element);
							}
						}
						story.setElements(seSet);
						if (storyModel.containsKey("summary"))
							story.setSummary(storyModel.getString("summary"));
						else {
							story.setSummary(null);
						}

						this.storyDao.update(story);
						JSONArray collection_id = null;
						if (storyModel.containsKey("collection_id")) {
							collection_id = storyModel.getJSONArray("collection_id");
							collectionStoryDao.deleteCollectionStoryByStoryId(story.getId());
							if(collection_id != null && collection_id.size() > 0){
								Object[] cArr = collection_id.toArray();
								for(Object c:cArr){
									Long param_cid = Long.parseLong(c.toString());
									
									Collection collection = this.collectionDao.getCollectionById(param_cid);

									CollectionStory cs = new CollectionStory();
									cs.setStory(story);
									cs.setCollection(collection);
									this.collectionStoryDao.save(cs);

								}
							}
							

						}

						Timeline timeline = this.timelineDao.getTimelineByUseridAndStoryIdAndType(loginUserid, storyId,
								"post");
						EventModel event = getEventModelListByLoginUserid(timeline, loginUserid, collection_id);
						log.debug("***update story success ***");
						notificationDao.enableNotification(1, storyId);
						log.debug("***event model ***" + JSONObject.fromObject(event));

						return Response.status(Response.Status.OK).entity(event).build();
					}
					JSONObject jo = new JSONObject();
					jo.put("status", "invalid_request");
					jo.put("code", Integer.valueOf(10010));
					jo.put("error_message", "Invalid request payload");
					return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
				}

				JSONObject jo = new JSONObject();
				jo.put("status", "invalid_story");
				jo.put("code", Integer.valueOf(10012));
				jo.put("error_message", "story does not exist");
				return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			}

			JSONObject jo = new JSONObject();
			jo.put("status", "invalid_request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "Invalid request payload");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("status", "invalid_request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "Invalid request payload");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
	}

	public Response deleteStory(Long storyId, Long loginUserid) {
		JSONObject jo = new JSONObject();
		Story story = this.storyDao.getStoryByIdAndLoginUserid(storyId, loginUserid);
		if (story != null) {
			story.setStatus("removed");
			this.storyDao.update(story);
			List<Comment> commentList = this.commentDao.getCommentByStoryId(storyId);
			if ((commentList != null) && (commentList.size() > 0)) {
				for (Comment com : commentList) {
					com.setStatus("disabled");
					this.commentDao.update(com);
				}
			}
			// this.timelineDao.deleteTimelineByType(loginUserid, storyId,
			// "post");
			timelineDao.deleteTimelineByStoryId(storyId);
			notificationDao.disableNotification(1, storyId);
			jo.put("status", "success");
		} else {
			jo.put("status", "invalid_request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "Invalid request payload");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
		return Response.status(Response.Status.OK).entity(jo).build();
	}

	public Response createComment(JSONObject commentModel, Long storyId, Long loginUserid) {
		log.debug("*** create comment start ***" + JSONObject.fromObject(commentModel));
		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String appId = jsonObject.getString("appId");
		String appKey = jsonObject.getString("appKey");
		String masterSecret = jsonObject.getString("masterSecret");
		User user = (User) this.userDao.get(loginUserid);
		Story story = (Story) this.storyDao.get(storyId);
		if (!Strings.isNullOrEmpty(commentModel.getString("content"))) {
			if (!story.getStatus().equals("removed")) {
				Comment comment = new Comment();

				comment.setContent(commentModel.getString("content"));
				comment.setStory(story);
				comment.setUser(user);
				comment.setStatus("enabled");
				if(commentModel.containsKey("comment_image")){
					comment.setComment_image(commentModel.getString("comment_image"));
				}
				
				this.commentDao.save(comment);
				story.setLast_comment_date(new Date());
				storyDao.update(story);
				if (!loginUserid.equals(story.getUser().getId())) {
					Notification notification = new Notification();
					notification.setSenderId(loginUserid);
					notification.setRecipientId((Long) story.getUser().getId());
					notification.setNotificationType(5);
					notification.setObjectType(2);
					notification.setObjectId((Long) comment.getId());
					notification.setRead_already(false);
					notification.setStatus("enabled");
					this.notificationDao.save(notification);
					Configuration conf = this.configurationDao.getConfByUserId((Long) story.getUser().getId());
					if (conf.isNew_comment_on_your_story_push()) {
						int count = this.notificationDao.getNotificationByRecipientId((Long) story.getUser().getId());
						List<PushNotification> list = this.pushNotificationDao
								.getPushNotificationByUserid((Long) story.getUser().getId());
						try {
							String content = user.getUsername() + "评论了我的故事";
							JSONObject json = new JSONObject();
							json.put("story_id", story.getId());
							json.put("comment_id", comment.getId());
							PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, count, content,
									json.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				CommentModel commentModels = new CommentModel();
				if (comment != null) {
					commentModels = getCommentModel(comment);
				}
				log.debug("*** comment information ***" + JSONObject.fromObject(commentModels));
				return Response.status(Response.Status.CREATED).entity(commentModels).build();
			}
			JSONObject json = new JSONObject();
			json.put("status", "invalid_story");
			json.put("code", Integer.valueOf(10051));
			json.put("error_message", "The story has been deleted, can't comment");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}

		JSONObject json = new JSONObject();
		json.put("status", "invalid_request");
		json.put("code", Integer.valueOf(10010));
		json.put("error_message", "Invalid payload parameters");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}

	public Response createReplyComment(JSONObject replyComment, Long storyId, Long commentId, Long loginUserid) {
		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String appId = jsonObject.getString("appId");
		String appKey = jsonObject.getString("appKey");
		String masterSecret = jsonObject.getString("masterSecret");
		Story story = (Story) this.storyDao.get(storyId);
		User user = (User) this.userDao.get(loginUserid);
		if (!Strings.isNullOrEmpty(replyComment.getString("content"))) {
			Comment comment = new Comment();

			Comment com = this.commentDao.get(commentId);

			if (!story.getStatus().equals("removed")) {
				if (!com.getStatus().equals("disabled")) {
					if (replyComment.containsKey("target_user_id") ) {
						comment.setContent(replyComment.getString("content"));
						comment.setStory(story);
						comment.setUser(user);
						comment.setTarget_user_id(replyComment.getLong("target_user_id"));
						comment.setStatus("enabled");
						comment.setTarget_comment_id(commentId);
						if(replyComment.containsKey("comment_image")){
							comment.setComment_image(replyComment.getString("comment_image"));
						}
						
					} else {
						comment.setContent(replyComment.getString("content"));
						comment.setStory(story);
						comment.setUser(user);
						comment.setStatus("enabled");
						comment.setTarget_comment_id(commentId);
						if(replyComment.containsKey("comment_image")){
							comment.setComment_image(replyComment.getString("comment_image"));
						}
						
					}

					this.commentDao.save(comment);
					story.setLast_comment_date(new Date());
					storyDao.update(story);
					Notification notification = new Notification();
					notification.setSenderId(loginUserid);
					if (comment.getTarget_user_id() != null) {
						notification.setRecipientId(comment.getTarget_user_id());
					} else {
						notification.setRecipientId(com.getUser().getId());
					}

					notification.setNotificationType(6);
					notification.setObjectType(2);
					notification.setObjectId((Long) comment.getId());
					notification.setRead_already(false);
					notification.setStatus("enabled");
					this.notificationDao.save(notification);
					Configuration conf = this.configurationDao.getConfByUserId(com.getUser().getId());
					if (conf.isNew_comment_on_your_comment_push()) {
						int count = 0;
						List<PushNotification> list = null;
						if (comment.getTarget_user_id() != null) {
							count = this.notificationDao.getNotificationByRecipientId(comment.getTarget_user_id());
							list = this.pushNotificationDao.getPushNotificationByUserid(comment.getTarget_user_id());
						} else {
							count = this.notificationDao.getNotificationByRecipientId(com.getUser().getId());
							list = this.pushNotificationDao.getPushNotificationByUserid(com.getUser().getId());
						}
						try {
							String content = user.getUsername() + "回复了你";
							JSONObject json = new JSONObject();
							json.put("story_id", story.getId());
							json.put("comment_id", comment.getId());
							PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, count, content,
									json.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					ReplyCommentModel commentModels = new ReplyCommentModel();
					if (comment != null) {
						commentModels = getReplyCommentModel(comment);
					}
					log.debug("*** comment information ***" + JSONObject.fromObject(commentModels));
					return Response.status(Response.Status.CREATED).entity(commentModels).build();
				}
				JSONObject json = new JSONObject();
				json.put("status", "invalid_comment");
				json.put("code", Integer.valueOf(10052));
				json.put("error_message", "Comment has been deleted, can't reply");
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}

			JSONObject json = new JSONObject();
			json.put("status", "invalid_story");
			json.put("code", Integer.valueOf(10050));
			json.put("error_message", "The story has been deleted, can't comment");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}

		JSONObject json = new JSONObject();
		json.put("status", "invalid_request");
		json.put("code", Integer.valueOf(10010));
		json.put("error_message", "Invalid payload parameters");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
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

	public List<CommentModel> getComments(Long storyId, HttpServletRequest request) {
		log.debug("**** get comments ****" + storyId);
		String countStr = request.getParameter("count");

		String maxIdStr = request.getParameter("max_id");
		List<CommentModel> commentModelList = new ArrayList<CommentModel>();
		int count = 20;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
			List<Comment> commentList = this.commentDao.getComments(storyId, count);
			if ((commentList != null) && (commentList.size() > 0)) {
				CommentModel commentModel = null;
				for (Comment comment : commentList) {
					commentModel = getCommentModel(comment);
					List<Comment> replies = this.commentDao.getReplyComments((Long) comment.getId(), 3);
					List<ReplyCommentModel> replyCommentModelList = new ArrayList<ReplyCommentModel>();
					if ((replies != null) && (replies.size() > 0)) {
						ReplyCommentModel replyCommentModel = null;
						for (Comment reply : replies) {
							replyCommentModel = getReplyCommentModel(reply);
							replyCommentModelList.add(replyCommentModel);
						}
					}
					commentModel.setReplies(replyCommentModelList);
					commentModelList.add(commentModel);
				}
			}
		} else {
			if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				List<Comment> commentList = this.commentDao.getComments(storyId, count);
				if ((commentList != null) && (commentList.size() > 0)) {
					CommentModel commentModel = null;
					List<Comment> replies = null;
					for (Comment comment : commentList) {
						commentModel = getCommentModel(comment);
						replies = this.commentDao.getReplyComments((Long) comment.getId(), 3);
						List<ReplyCommentModel> replyCommentModelList = new ArrayList<ReplyCommentModel>();
						if ((replies != null) && (replies.size() > 0)) {
							ReplyCommentModel replyCommentModel = null;
							for (Comment reply : replies) {
								replyCommentModel = getReplyCommentModel(reply);
								replyCommentModelList.add(replyCommentModel);
							}
						}
						commentModel.setReplies(replyCommentModelList);
						commentModelList.add(commentModel);
					}
				}
			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<Comment> commentList = this.commentDao.getComments(storyId, max_id, count);
				if ((commentList != null) && (commentList.size() > 0)) {
					CommentModel commentModel = null;
					for (Comment comment : commentList) {
						commentModel = getCommentModel(comment);
						List<Comment> replies = this.commentDao.getReplyComments((Long) comment.getId(), 3);
						List<ReplyCommentModel> replyCommentModelList = new ArrayList<ReplyCommentModel>();
						if ((replies != null) && (replies.size() > 0)) {
							ReplyCommentModel replyCommentModel = null;
							for (Comment reply : replies) {
								replyCommentModel = getReplyCommentModel(reply);
								replyCommentModelList.add(replyCommentModel);
							}
						}
						commentModel.setReplies(replyCommentModelList);
						commentModelList.add(commentModel);
					}
				}
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<Comment> commentList = this.commentDao.getComments(storyId, max_id, count);
				if ((commentList != null) && (commentList.size() > 0)) {
					CommentModel commentModel = null;
					for (Comment comment : commentList) {
						commentModel = getCommentModel(comment);
						List<Comment> replies = this.commentDao.getReplyComments((Long) comment.getId(), 3);
						List<ReplyCommentModel> replyCommentModelList = new ArrayList<ReplyCommentModel>();
						if ((replies != null) && (replies.size() > 0)) {
							ReplyCommentModel replyCommentModel = null;
							for (Comment reply : replies) {
								replyCommentModel = getReplyCommentModel(reply);
								replyCommentModelList.add(replyCommentModel);
							}
						}
						commentModel.setReplies(replyCommentModelList);
						commentModelList.add(commentModel);
					}
				}
			}
		}
		log.debug("***** get comment list *****" + JSONArray.fromObject(commentModelList));
		return commentModelList;
	}

	public List<ReplyCommentModel> getRepliesComments(Long storyId, Long commentId, HttpServletRequest request) {
		log.debug("**** get reply comments ****" + storyId);
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		List<ReplyCommentModel> commentModelList = new ArrayList<ReplyCommentModel>();
		int count = 20;
		ReplyCommentModel commentModel = null;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
			List<Comment> commentList = this.commentDao.getReplyComments(commentId, count);
			if ((commentList != null) && (commentList.size() > 0))
				for (Comment comment : commentList) {
					commentModel = getReplyCommentModel(comment);
					commentModelList.add(commentModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			List<Comment> commentList = this.commentDao.getReplyComments(commentId, count);
			if ((commentList != null) && (commentList.size() > 0))
				for (Comment comment : commentList) {
					commentModel = getReplyCommentModel(comment);
					commentModelList.add(commentModel);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Comment> commentList = this.commentDao.getReplyComments(commentId, max_id, count);
			if ((commentList != null) && (commentList.size() > 0))
				for (Comment comment : commentList) {
					commentModel = getReplyCommentModel(comment);
					commentModelList.add(commentModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Comment> commentList = this.commentDao.getReplyComments(commentId, max_id, count);
			if ((commentList != null) && (commentList.size() > 0)) {
				for (Comment comment : commentList) {
					commentModel = getReplyCommentModel(comment);
					commentModelList.add(commentModel);
				}
			}
		}
		log.debug("***** get reply comment list *****" + JSONArray.fromObject(commentModelList));
		return commentModelList;
	}

	public Response deleteComment(Long storyId, Long commentId, Long loginUserid) {
		Comment comment = this.commentDao.getCommentByIdAndLoginUserid(commentId, loginUserid);
		int count = 0;
		if (comment != null) {
			if ((comment.getTarget_comment_id() == null) && (comment.getTarget_user_id() == null)) {
				List<Comment> commentList = this.commentDao.getReplyCommentsById((Long) comment.getId());
				if ((commentList != null) && (commentList.size() > 0)) {
					for (Comment c : commentList) {
						c.setStatus("disabled");
						this.commentDao.update(c);
					}
				}
			}

			comment.setStatus("disabled");
			this.commentDao.deleteComment(comment);
			count = this.commentDao.getCommentCountById(storyId);
			notificationDao.disableNotification(2, commentId);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("status", "invalid_request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
		JSONObject json = new JSONObject();
		json.put("comment_count", Integer.valueOf(count));
		return Response.status(Response.Status.OK).entity(json).build();
	}

	public Response createLikes(Long storyId, Long loginUserid,HttpServletRequest request) {
		log.debug("*** create likes ***");
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObj = parseJson(urlkey);
		String url = jsonObj.getString("fmap_url");

		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String appId = jsonObject.getString("appId");
		String appKey = jsonObject.getString("appKey");
		String masterSecret = jsonObject.getString("masterSecret");
		User user = (User) this.userDao.get(loginUserid);
		Story story = (Story) this.storyDao.get(storyId);
		Likes likes = new Likes();
		likes.setCreateTime(new Date());
		likes.setLike_users(user);
		likes.setLike_story(story);
		this.likesDao.save(likes);
		Notification notification = this.notificationDao.getNotificationByAction(storyId, loginUserid, 1, 2);
		if (notification != null) {
			notification.setCreate_at(new Date());
			this.notificationDao.update(notification);
		} else {
			notification = new Notification();
			notification.setSenderId(loginUserid);
			notification.setRecipientId((Long) story.getUser().getId());
			notification.setNotificationType(2);
			notification.setObjectType(1);
			notification.setObjectId(storyId);
			notification.setRead_already(false);
			notification.setStatus("enabled");
			this.notificationDao.save(notification);
		}
		Configuration conf = this.configurationDao.getConfByUserId((Long) story.getUser().getId());
		if(conf != null){
			if (conf.isNew_favorite_from_following_push()) {
				int counts = this.notificationDao.getNotificationByRecipientId((Long) story.getUser().getId());
				List<PushNotification> list = this.pushNotificationDao
						.getPushNotificationByUserid((Long) story.getUser().getId());
				try {
					String content = user.getUsername() + "喜欢了我的故事";
					JSONObject json = new JSONObject();
					json.put("story_id", story.getId());
					PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content, json.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		int count = this.likesDao.likeStoryCount(storyId);
		JSONObject jo = new JSONObject();
		jo.put("like_count", Integer.valueOf(count));
		
		List<FMap> fmapList = fmapDao.getFMapList(storyId);
		if(fmapList != null && fmapList.size() > 0){
			UserCentre uc = userCentreDao.getUserCentreByUserId(loginUserid);
			int centre_id = 0;
			if(uc != null){
				centre_id = uc.getCentre_id();
			}else{
				LinkAccounts la = linkAccountsDao.getLinkAccountsByUseridAndService(loginUserid, "fblife");
				centre_id = Integer.parseInt(la.getUuid());
			}
			if(centre_id > 0){
				Map<String,String> map = new HashMap<String, String>();
				map.put("user_id", String.valueOf(centre_id));
				map.put("story_id", String.valueOf(storyId));
				map.put("recognition_type", "10");
				map.put("ip",ip);
				String params = "";
				try {
					params = publicParam(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
				HttpUtil.sendPostStr(url+"/v1/info/info-wiki/add-wiki-recognition", params);
			}
			
		}
		return Response.status(Response.Status.CREATED).entity(jo).build();
	}

	public Response deleteLike(Long storyId, Long loginUserid) {
		this.likesDao.deleteLike(loginUserid, storyId);

		int count = this.likesDao.likeStoryCount(storyId);
		JSONObject jo = new JSONObject();
		jo.put("like_count", Integer.valueOf(count));
		return Response.status(Response.Status.OK).entity(jo).build();
	}

	public Response createRepost(Long storyId, Long loginUserid) {
		log.debug("*** repost story ***");
		Republish report = republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
		JSONObject json1 = new JSONObject();
		if (report != null) {
			json1.put("status", "success");
			return Response.status(Response.Status.OK).entity(json1).build();
		}
		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String appId = jsonObject.getString("appId");
		String appKey = jsonObject.getString("appKey");
		String masterSecret = jsonObject.getString("masterSecret");
		int count = 0;
		/*
		 * Republish r = new Republish(); r.setCreateTime(new Date());
		 * r.setUserId(loginUserid); r.setStoryId(storyId); r.setType("repost");
		 * this.republishDao.save(r);
		 */
		User user = (User) this.userDao.get(loginUserid);
		Story story = (Story) this.storyDao.get(storyId);
		Republish r = new Republish();
		r.setCreateTime(new Date());
		r.setRepost_users(user);
		r.setRepost_story(story);
		r.setType("repost");
		this.republishDao.save(r);
		Timeline timeline = new Timeline();
		timeline.setCreatorId(loginUserid);
		timeline.setTargetUserId(loginUserid);
		timeline.setStory(story);
		timeline.setType("repost");
		timeline.setReferenceId(storyId);
		timeline.setCreateTime(new Date());
		this.timelineDao.save(timeline);
		Notification notification = this.notificationDao.getNotificationByAction(storyId, loginUserid, 1, 3);
		if (notification != null) {
			notification.setCreate_at(new Date());
			this.notificationDao.update(notification);
		} else {
			notification = new Notification();
			notification.setSenderId(loginUserid);
			notification.setRecipientId((Long) story.getUser().getId());
			notification.setNotificationType(3);
			notification.setObjectType(1);
			notification.setObjectId(storyId);
			notification.setRead_already(false);
			notification.setStatus("enabled");
			this.notificationDao.save(notification);
		}

		Configuration conf = this.configurationDao.getConfByUserId((Long) story.getUser().getId());
		if (conf.isReposted_my_story_push()) {
			int counts = this.notificationDao.getNotificationByRecipientId((Long) story.getUser().getId());
			List<PushNotification> list = this.pushNotificationDao
					.getPushNotificationByUserid((Long) story.getUser().getId());
			try {
				String content = user.getUsername() + " 收藏了这壹頁";
				JSONObject json = new JSONObject();
				json.put("user_id", user.getId());
				PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content, json.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		JSONObject jo = new JSONObject();
		Set<User> suser = story.getRepost_users();
		if (suser != null && suser.size() > 0) {
			count = suser.size();
		}
		// count = this.republishDao.count(storyId);
		jo.put("repost_count", Integer.valueOf(count));
		log.debug("*** repost story success ***");
		return Response.status(Response.Status.OK).entity(jo).build();
	}

	public Response deleteRepost(Long storyId, Long loginUserid) {
		this.republishDao.deleteRepublish(loginUserid, storyId);
		this.timelineDao.deleteTimelineByType(loginUserid, storyId, "repost");
		int count = this.republishDao.count(storyId);
		JSONObject jo = new JSONObject();
		jo.put("repost_count", Integer.valueOf(count));
		return Response.status(Response.Status.OK).entity(jo).build();
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
			if(c.getUser().getId().equals(user.getId()) 
					&& c.getUser().getId() == user.getId()){
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
			if(c.getUser().getId().equals(user.getId()) 
					&& c.getUser().getId() == user.getId()){
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
			if(c.getUser().getId().equals(user.getId()) 
					&& c.getUser().getId() == user.getId()){
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
		int likesCount = this.likesDao.userLikesCount((Long) story.getUser().getId());
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

	public EventModel getEventModelListByLoginUserid(Timeline timeline, Long loginUserid, JSONArray collection_id) {
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
				Object config;
				Object storyJson;
				if (((Long) story.getUser().getId()).equals(loginUserid)) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setSummary(story.getSummary());
					/*
					 * int likesCount = this.likesDao.userLikesCount(
					 * (Long)story.getUser() .getId()); int repostStoryCount =
					 * this.republishDao.userRepostCount( (Long)story
					 * .getUser().getId());
					 */
					User user = story.getUser();
					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
					}
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(user.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(user.getCoverImage());
					}

					JSONObject authorJson = new JSONObject();
					authorJson.put("id", user.getId());
					authorJson.put("username", user.getUsername());
					authorJson.put("email", user.getEmail());
					authorJson.put("created_time", user.getCreated_time());
					authorJson.put("status", user.getStatus());
					authorJson.put("introduction", user.getIntroduction());
					authorJson.put("avatar_image", avatarImageJson);
					authorJson.put("cover_image", coverImageJson);
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
					boolean flag_collection = false;
					JSONArray collectArr = new JSONArray();
					if (collection_id != null) {
						Object[] cArr = collection_id.toArray();
						if(cArr != null && cArr.length > 0){
							for(Object cid:cArr){
								Long param_cid = Long.parseLong(cid.toString());
								Collection collect = collectionDao.getCollectionById(param_cid);
								CollectionIntro ci = new CollectionIntro();
								ci.setId((Long) collect.getId());
								ci.setCollection_name(collect.getCollectionName());
								ci.setCover_image(JSONObject.fromObject(collect.getCover_image()));
								
								User u = userDao.get(collect.getUser().getId());
								JSONObject author = new JSONObject();
								author.put("id", u.getId());
								author.put("username", u.getUsername());
								ci.setAuthor(author);
								
								JsonConfig configs = new JsonConfig();
								List<String> delArray = new ArrayList<String>();
								if(!Strings.isNullOrEmpty(collect.getInfo())){
									ci.setInfo(collect.getInfo());
								}else{
									delArray.add("info");
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
								
								collectArr.add(collectionJson);
							}
						}
						
						
					} else {
						storyModel.setCollection(null);
					}

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
					}else if(type.equals("line")){
						LineCover coverMedia = (LineCover) JSONObject.toBean(jsonObject, LineCover.class);
						storyModel.setCover_media(JSONObject.fromObject(coverMedia));
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
								LocationModel locationModel = (LocationModel) JSONObject.toBean(content,
										LocationModel.class);
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

							}else if (types.equals("video")){
								JSONObject media = content.getJSONObject("media");
								if(media.containsKey("iframe_code")){
									IframeCover iframeMedia = (IframeCover) JSONObject.toBean(content, IframeCover.class);
									element.setContent(iframeMedia);
								}else{
									VideoCover videoMedia = (VideoCover) JSONObject.toBean(content, VideoCover.class);
									element.setContent(videoMedia);
								}
								
							}else if (types.equals("line")){
								LineCover lineMedia = (LineCover) JSONObject.toBean(content, LineCover.class);
								element.setContent(lineMedia);
							}
							storyElements.add(element);
						}
					}

					config = new JsonConfig();
					((JsonConfig) config).setExcludes(new String[] { "storyinfo", "contents" });
					((JsonConfig) config).setIgnoreDefaultExcludes(false);
					((JsonConfig) config).setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
					log.debug("***get Elements *****" + JSONArray.fromObject(story.getElements(), (JsonConfig) config));
					if ((storyElements != null) && (storyElements.size() > 0)) {
						storyModel.setElements(JSONArray.fromObject(storyElements, (JsonConfig) config));
					}

					storyModel.setCommnents_enables(story.getComments_enabled());
					if (!Strings.isNullOrEmpty(story.getTinyURL())) {
						storyModel.setUrl(story.getTinyURL());
					}
					storyModel.setView_count(story.getViewTimes());
					storyModel.setTitle(story.getTitle());

					int count = 0;
					Set<Comment> cSet = story.getComments();
					if (cSet != null && cSet.size() > 0) {
						count = cSet.size();
					}
					storyModel.setComment_count(count);

					List<Comment> commentList = this.commentDao.getCommentByStoryIdNewThree((Long) story.getId());
					if ((commentList != null) && (commentList.size() > 0)) {
						List<JSONObject> commentModelList = new ArrayList<JSONObject>();
						JSONObject commentModel = null;
						for (Comment c : commentList) {
							commentModel = getCommentSummaryModel(c);
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
					
					if (Strings.isNullOrEmpty(story.getResource())) {
						delArray.add("resource");
					}
					if (Strings.isNullOrEmpty(story.getTinyURL())) {
						delArray.add("url");
					}
					if (collection_id == null) {
						delArray.add("collection");
					}
					
					if(!flag_collection){
						delArray.add("collection");
					}
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					storyJson = null;
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
					/*
					 * int likesCount = this.likesDao.userLikesCount(
					 * (Long)story .getUser().getId()); int repostStoryCount =
					 * this.republishDao
					 * .userRepostCount((Long)story.getUser().getId());
					 */
					User user = story.getUser();
					JSONObject avatarImageJson = null;
					if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
						avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
					}
					JSONObject coverImageJson = null;
					if (!Strings.isNullOrEmpty(user.getCoverImage())) {
						coverImageJson = JSONObject.fromObject(user.getCoverImage());
					}
					/*
					 * int storyCount =
					 * this.storyDao.getStoryCount((Long)user.getId()); int
					 * follower_Count = this.followDao.userFollowedCount(
					 * (Long)user .getId()); int following_count =
					 * this.followDao.userFollowCount( (Long)user .getId());
					 */

					JSONObject authorJson = new JSONObject();
					authorJson.put("id", user.getId());
					authorJson.put("username", user.getUsername());
					authorJson.put("email", user.getEmail());
					authorJson.put("created_time", user.getCreated_time());
					authorJson.put("status", user.getStatus());
					authorJson.put("introduction", user.getIntroduction());
					authorJson.put("avatar_image", avatarImageJson);
					authorJson.put("cover_image", coverImageJson);
					/*
					 * authorJson.put("likes_count",
					 * Integer.valueOf(likesCount));
					 * authorJson.put("reposts_count",
					 * Integer.valueOf(repostStoryCount));
					 * authorJson.put("stories_count",
					 * Integer.valueOf(storyCount));
					 * authorJson.put("followers_count",
					 * Integer.valueOf(follower_Count));
					 * authorJson.put("following_count",
					 * Integer.valueOf(following_count));
					 */
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
							} else if (types.equals("location")) {
								LocationModel locationModel = (LocationModel) JSONObject.toBean(content,
										LocationModel.class);
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

							} else if (types.equals("video")){
								

								JSONObject media = content.getJSONObject("media");
								if(media.containsKey("iframe_code")){
									IframeCover iframeMedia = (IframeCover) JSONObject.toBean(content, IframeCover.class);
									element.setContent(iframeMedia);
								}else{
									VideoCover videoMedia = (VideoCover) JSONObject.toBean(content, VideoCover.class);
									element.setContent(videoMedia);
								}
								
							} else if (types.equals("line")){
								LineCover lineMedia = (LineCover) JSONObject.toBean(content, LineCover.class);
								element.setContent(lineMedia);
							}
							storyElements.add(element);
						}
					}

					JsonConfig config1 = new JsonConfig();
					config1.setExcludes(new String[] { "storyinfo", "contents" });
					config1.setIgnoreDefaultExcludes(false);
					config1.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
					if ((storyElements != null) && (storyElements.size() > 0)) {
						storyModel.setElements(JSONArray.fromObject(storyElements, config1));
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
					if (cSet != null && cSet.size() > 0) {
						count = cSet.size();
					}
					storyModel.setComment_count(count);
					/*
					 * Likes likes1 = this.likesDao.getLikeByUserIdAndStoryId(
					 * loginUserid, storyId); if (likes1 != null)
					 * storyModel.setLiked_by_current_user(true); else {
					 * storyModel.setLiked_by_current_user(false); } int
					 * likeCount = this.likesDao.likeStoryCount(storyId);
					 * storyModel.setLike_count(likeCount); int repostCount =
					 * this.republishDao.count(storyId);
					 * storyModel.setRepost_count(repostCount);
					 */
					Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
					if (repost != null)
						storyModel.setRepost_by_current_user(true);
					else {
						storyModel.setRepost_by_current_user(false);
					}

					List<Comment> commentList = this.commentDao.getCommentByStoryIdNewThree((Long) story.getId());
					if ((commentList != null) && (commentList.size() > 0)) {
						List<JSONObject> commentModelList = new ArrayList<JSONObject>();
						JSONObject commentModel = null;
						for (Comment c : commentList) {
							commentModel = getCommentSummaryModel(c);
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
					if (Strings.isNullOrEmpty(story.getResource())) {
						delArray.add("resource");
					}
					if (Strings.isNullOrEmpty(story.getTinyURL())) {
						delArray.add("url");
					}
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray.add("title");
					}
					if ((storyElements == null) || (storyElements.size() == 0)) {
						delArray.add("elements");
					}
					JSONObject storyJson1 = null;
					if ((delArray != null) && (delArray.size() > 0)) {
						configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
						configs.setIgnoreDefaultExcludes(false);
						configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						storyJson1 = JSONObject.fromObject(storyModel, configs);
					} else {
						storyJson1 = JSONObject.fromObject(storyModel);
					}
					contentJson.put("story", storyJson1);
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
						userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
						userIntro.setUser_type(user.getUser_type());
						contentJson.put("repost_by", userIntro);
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
						userIntro.setUser_type(user.getUser_type());
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

	public EventModel getEventModelListByLoginid(Timeline timeline, Long loginUserid) {
		try {
			EventModel event = new EventModel();
			event.setId((Long) timeline.getId());
			event.setEvent_time(timeline.getCreateTime());
			event.setEvent_type(timeline.getType());
			JSONObject contentJson = new JSONObject();
			Long storyId = (Long) timeline.getStory().getId();
			Story story = this.storyDao.getStoryByIdAndStatus(storyId, "publish", "disabled");
			StoryEvent storyModel = new StoryEvent();

			if (story != null) {
				if (((Long) story.getUser().getId()).equals(loginUserid)) {
					storyModel.setId(storyId);
					storyModel.setImage_count(story.getImage_count());
					storyModel.setUrl(story.getTinyURL());
					storyModel.setResource(story.getResource());
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
					int repost_count = republishDao.count(story.getId());
					storyModel.setRepost_count(repost_count);
					int count = this.commentDao.getCommentCountById(story.getId());
					storyModel.setComment_count(count);
					storyModel.setCreated_time(story.getCreated_time());
					/*Collection collection = this.collectionStoryDao.getCollectionByStoryId(storyId);
					if (collection != null) {
						CollectionIntro ci = new CollectionIntro();
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
						ci.setInfo(collection.getInfo());
						User author = userDao.get(collection.getUser().getId());
						JSONObject json = new JSONObject();
						json.put("id", author.getId());
						json.put("username", author.getUsername());
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
					}*/
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
					storyModel.setSummary(story.getSummary());
					Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
					if (repost != null)
						storyModel.setRepost_by_current_user(true);
					else {
						storyModel.setRepost_by_current_user(false);
					}
					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();

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
					storyModel.setResource(story.getResource());
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
					int count = this.commentDao.getCommentCountById((Long) story.getId());
					int repost_count = republishDao.count(story.getId());
					storyModel.setRepost_count(repost_count);
					storyModel.setComment_count(count);
					storyModel.setCreated_time(story.getCreated_time());
					Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, storyId);
					if (repost != null)
						storyModel.setRepost_by_current_user(true);
					else {
						storyModel.setRepost_by_current_user(false);
					}
					/*Collection collection = this.collectionStoryDao.getCollectionByStoryId(storyId);
					if (collection != null) {
						CollectionIntro ci = new CollectionIntro();
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
						ci.setInfo(collection.getInfo());

						User author = userDao.get(collection.getUser().getId());
						JSONObject json = new JSONObject();
						json.put("id", author.getId());
						json.put("username", author.getUsername());
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
					}*/
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
					storyModel.setSummary(story.getSummary());
					JsonConfig configs = new JsonConfig();
					List<String> delArray1 = new ArrayList<String>();
					if (Strings.isNullOrEmpty(story.getTitle())) {
						delArray1.add("title");
					}

					if (Strings.isNullOrEmpty(story.getSummary())) {
						delArray1.add("summary");
					}
					JSONObject storyJson = null;
					if ((delArray1 != null) && (delArray1.size() > 0)) {
						configs.setExcludes((String[]) delArray1.toArray(new String[delArray1.size()]));
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
						userIntro.setAvatar_image(JSONObject.fromObject(user.getAvatarImage()));
						userIntro.setUser_type(user.getUser_type());
						contentJson.put("repost_by", userIntro);
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

	public Response reportStory(Long storyId, Long loginUserid) {
		Story story = (Story) this.storyDao.get(storyId);
		Report report = this.reportDao.getReportByStoryIdAndUserId(storyId, loginUserid);
		JSONObject json = new JSONObject();
		if (report != null) {
			json.put("status", "success");
			return Response.status(Response.Status.OK).entity(json).build();
		}
		if (story != null) {
			Report r = new Report();
			r.setSender_id(loginUserid);
			r.setRecipient_id((Long) story.getUser().getId());
			r.setType("report_story");
			r.setObject_type(1);
			r.setObject_id((Long) story.getId());
			r.setStatus("new");
			this.reportDao.save(r);
			json.put("status", "success");
			return Response.status(Response.Status.OK).entity(json).build();
		}
		json.put("status", "no_resource");
		json.put("code", Integer.valueOf(10012));
		json.put("error_message", "The story does not exist");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}

	public List<UserIntro> getLikeStoryUserByStoryId(Long storyId, HttpServletRequest request, Long loginUserid) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");

		List<UserIntro> userLikeList = new ArrayList<UserIntro>();
		UserIntro userIntro = null;
		int count = 20;

		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<User> userList = this.userDao.getUsersByStoryIdAndNull(storyId, count);
			if ((userList != null) && (userList.size() > 0))
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userLikeList.add(userIntro);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			List<User> userList = this.userDao.getUsersByStoryIdAndNull(storyId, count);
			if ((userList != null) && (userList.size() > 0))
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userLikeList.add(userIntro);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			Likes like = this.likesDao.getLikeByUserIdAndStoryId(since_id, storyId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date(like.getCreateTime().longValue()));
			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) like.getId(), createTime,
					count, 1);
			if ((userList != null) && (userList.size() > 0))
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userLikeList.add(userIntro);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			Likes like = this.likesDao.getLikeByUserIdAndStoryId(since_id, storyId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date(like.getCreateTime().longValue()));
			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) like.getId(), createTime,
					count, 1);
			if ((userList != null) && (userList.size() > 0))
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userLikeList.add(userIntro);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			Likes like = this.likesDao.getLikeByUserIdAndStoryId(max_id, storyId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date(like.getCreateTime().longValue()));
			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) like.getId(), createTime,
					count, 2);
			if ((userList != null) && (userList.size() > 0))
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userLikeList.add(userIntro);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			Likes like = this.likesDao.getLikeByUserIdAndStoryId(max_id, storyId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date(like.getCreateTime().longValue()));
			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) like.getId(), createTime,
					count, 2);
			if ((userList != null) && (userList.size() > 0)) {
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userLikeList.add(userIntro);
				}

			}

		}

		return userLikeList;
	}

	public List<UserIntro> getRepostStoryUserByStoryId(Long storyId, HttpServletRequest request, Long loginUserid) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<UserIntro> userRepostList = new ArrayList<UserIntro>();
		UserIntro userIntro = null;
		int count = 20;

		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<User> userList = this.userDao.getRepostUsersByStoryId(storyId, count);
			if ((userList != null) && (userList.size() > 0))
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userRepostList.add(userIntro);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			List<User> userList = this.userDao.getRepostUsersByStoryId(storyId, count);
			if ((userList != null) && (userList.size() > 0))
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userRepostList.add(userIntro);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			Republish r = this.republishDao.getRepostByUserIdAndStoryId(since_id, storyId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date(r.getCreateTime().longValue()));
			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) r.getId(), createTime, count,
					1);
			if ((userList != null) && (userList.size() > 0))
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userRepostList.add(userIntro);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			Republish r = this.republishDao.getRepostByUserIdAndStoryId(since_id, storyId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date(r.getCreateTime().longValue()));
			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) r.getId(), createTime, count,
					1);
			if ((userList != null) && (userList.size() > 0))
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userRepostList.add(userIntro);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			Republish r = this.republishDao.getRepostByUserIdAndStoryId(max_id, storyId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date(r.getCreateTime().longValue()));
			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) r.getId(), createTime, count,
					2);
			if ((userList != null) && (userList.size() > 0))
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userRepostList.add(userIntro);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			Republish r = this.republishDao.getRepostByUserIdAndStoryId(max_id, storyId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date(r.getCreateTime().longValue()));
			List<User> userList = this.userDao.getUsersByStoryIdAndUserId(storyId, (Long) r.getId(), createTime, count,
					2);
			if ((userList != null) && (userList.size() > 0)) {
				for (User u : userList) {
					userIntro = getUserIntro(u, loginUserid);
					userRepostList.add(userIntro);
				}
			}
		}

		return userRepostList;
	}

	public Response reportComment(Long commentId, Long storyId, Long loginUserid) {
		Comment comment = (Comment) this.commentDao.get(commentId);
		Report report = this.reportDao.getReportByCommentIdAndUserId(commentId, loginUserid);
		JSONObject json = new JSONObject();
		if (report != null) {
			json.put("status", "success");
			return Response.status(Response.Status.OK).entity(json).build();
		}
		if (comment != null) {
			Report r = new Report();
			r.setSender_id(loginUserid);
			r.setRecipient_id((Long) comment.getUser().getId());
			r.setType("report_comment");
			r.setObject_type(2);
			r.setObject_id((Long) comment.getId());
			r.setStatus("new");
			this.reportDao.save(r);
			json.put("status", "success");
			return Response.status(Response.Status.OK).entity(json).build();
		}
		json.put("status", "no_resource");
		json.put("code", Integer.valueOf(10053));
		json.put("error_message", "The comment does not exist");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}

	public UserIntro getUserIntro(User u, Long loginUserid) {
		UserIntro userIntro = new UserIntro();
		userIntro.setId((Long) u.getId());
		userIntro.setIntroduction(u.getIntroduction());
		userIntro.setUsername(u.getUsername());
		userIntro.setUser_type(u.getUser_type());
		if (!Strings.isNullOrEmpty(u.getAvatarImage()))
			userIntro.setAvatar_image(JSONObject.fromObject(u.getAvatarImage()));
		else {
			userIntro.setAvatar_image(null);
		}
		Follow f = this.followDao.getFollow((Long) u.getId(), loginUserid);
		if (f != null)
			userIntro.setIs_following_current_user(true);
		else {
			userIntro.setIs_following_current_user(false);
		}
		Follow followed = this.followDao.getFollow(loginUserid, (Long) u.getId());
		if (followed != null)
			userIntro.setFollowed_by_current_user(true);
		else {
			userIntro.setFollowed_by_current_user(false);
		}

		return userIntro;
	}

	@Override
	public JSONObject getEventModelsRand(Long loginUserid) {
		String appkey = getClass().getResource("/../../META-INF/random.json").getPath();
		JSONObject result = new JSONObject();
		JSONObject jsonObject = parseJson(appkey);
		int recommand = jsonObject.getInt("recommand");
		int collection = jsonObject.getInt("collection");
		List<EventModel> eventList = new ArrayList<EventModel>();
		List<Timeline> list = timelineDao.getTimelineByRecommandAndRand(recommand);
		List<Story> sList = collectionStoryDao.getStoryByRand(collection);
		StringBuffer sb = new StringBuffer();
		if (sList != null && sList.size() > 0) {
			for (Story s : sList) {
				sb.append(s.getId() + ",");
			}
		}
		String ids = sb.toString();
		if (!Strings.isNullOrEmpty(ids)) {
			ids = ids.substring(0, ids.length() - 1);
			List<Timeline> list1 = timelineDao.getTimelineByStoryIdAndType(ids, "post");
			list.addAll(list1);
		}

		EventModel eventModel = null;
		if (list != null && list.size() > 0) {
			for (Timeline timeline : list) {
				eventModel = getEventModelListByLoginid(timeline, loginUserid);
				if (eventModel.getContent() != null)
					eventList.add(eventModel);
			}

		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String start = sdf.format(new Date());
		String end = lastWeek();

		List<Story> storyList = storyDao.getStoryByTime(start, end);
		List<JSONObject> spmList = new ArrayList<JSONObject>();
		StoryIntro intro = null;
		if (storyList != null && storyList.size() > 0) {
			for (Story s : storyList) {
				intro = new StoryIntro();
				intro.setId((Long) s.getId());
				intro.setTitle(s.getTitle());
				if (!Strings.isNullOrEmpty(s.getCover_page()))
					intro.setCover_media(JSONObject.fromObject(s.getCover_page()));
				else {
					intro.setCover_media(null);
				}
				intro.setImage_count(s.getImage_count());
				intro.setCollectionId(Long.valueOf(1L));
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				if (Strings.isNullOrEmpty(s.getTitle())) {
					delArray.add("title");
				}

				JSONObject storyJson = null;
				if ((delArray != null) && (delArray.size() > 0)) {
					configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
					configs.setIgnoreDefaultExcludes(false);
					configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					storyJson = JSONObject.fromObject(intro, configs);
				} else {
					storyJson = JSONObject.fromObject(intro);
				}

				if (!Strings.isNullOrEmpty(s.getSummary())) {
					storyJson.put("summary", s.getSummary());
				}
				User user = userDao.get(s.getUser().getId());
				JSONObject author = new JSONObject();
				author.put("id", user.getId());
				author.put("username", user.getUsername());
				author.put("user_type", user.getUser_type());
				String avatar_image = user.getAvatarImage();
				if (!Strings.isNullOrEmpty(avatar_image)) {
					author.put("avatar_image", JSONObject.fromObject(avatar_image));
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

					author.put("publisher_info", publisherList);
				}
				storyJson.put("author", author);
				spmList.add(storyJson);
			}
		}

		result.put("events", eventList);
		result.put("top_stories", spmList);
		System.out.println("discover--->" + result.toString());

		return result;
	}

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

	public JSONObject getStoryModelByStoryLoginUser(Story story, Long loginUserid) {
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
		List<Collection> collectionList = this.collectionStoryDao.getCollectionListByStoryId((Long) story.getId());
		JSONArray collectArr = new JSONArray();
		if (collectionList != null && collectionList.size() > 0) {

			for(Collection collect:collectionList){
				CollectionIntro ci = new CollectionIntro();
				ci.setId((Long) collect.getId());
				ci.setCollection_name(collect.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collect.getCover_image()));
				
				User u = userDao.get(collect.getUser().getId());
				JSONObject author = new JSONObject();
				author.put("id", u.getId());
				author.put("username", u.getUsername());
				ci.setAuthor(author);
				
				JsonConfig configs = new JsonConfig();
				List<String> delArray = new ArrayList<String>();
				if(!Strings.isNullOrEmpty(collect.getInfo())){
					ci.setInfo(collect.getInfo());
				}else{
					delArray.add("info");
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
				
				collectArr.add(collectionJson);
			}
		
			
		} else {
			storyModel.setCollection(null);
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

				}else if(type.equals("video")){

					JSONObject media = content.getJSONObject("media");
					if(media.containsKey("iframe_code")){
						IframeCover iframeMedia = (IframeCover) JSONObject.toBean(content, IframeCover.class);
						element.setContent(iframeMedia);
					}else{
						VideoCover videoMedia = (VideoCover) JSONObject.toBean(content, VideoCover.class);
						element.setContent(videoMedia);
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
		if (cSet != null && cSet.size() > 0) {
			count = cSet.size();
		}
		storyModel.setComment_count(count);
		/*
		 * Likes likes = this.likesDao.getLikeByUserIdAndStoryId(loginUserid,
		 * (Long) story.getId()); if (likes != null)
		 * storyModel.setLiked_by_current_user(true); else {
		 * storyModel.setLiked_by_current_user(false); } int likeCount =
		 * this.likesDao.likeStoryCount((Long) story.getId());
		 * storyModel.setLike_count(likeCount); int repostCount =
		 * this.republishDao.count((Long) story.getId());
		 * storyModel.setRepost_count(repostCount);
		 */
		Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, (Long) story.getId());
		if (repost != null)
			storyModel.setRepost_by_current_user(true);
		else {
			storyModel.setRepost_by_current_user(false);
		}

		List<Comment> commentList = this.commentDao.getCommentByStoryIdNewThree((Long) story.getId());
		if ((commentList != null) && (commentList.size() > 0)) {
			List<JSONObject> commentModelList = new ArrayList<JSONObject>();
			JSONObject commentModel = null;
			for (Comment c : commentList) {
				commentModel = getCommentSummaryModel(c);
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

		JsonConfig configs = new JsonConfig();
		List<String> delArray = new ArrayList<String>();
		if (Strings.isNullOrEmpty(story.getTitle())) {
			delArray.add("title");
		}
		if (collectionList == null) {
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
	public Response sendNotification(Long loginUserid, Long storyId) {
		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String appId = jsonObject.getString("appId");
		String appKey = jsonObject.getString("appKey");
		String masterSecret = jsonObject.getString("masterSecret");
		User user = userDao.get(loginUserid);
		JSONObject json = new JSONObject();
		try {
			if (!user.getUser_type().equals("media")) {
				List<PushNotification> pnList = new ArrayList<PushNotification>();
				List<Follow> followList = this.followDao.getFollowersByUserId(loginUserid);
				if ((followList != null) && (followList.size() > 0)) {
					for (Follow follow : followList) {
						Long recipientId = (Long) follow.getPk().getUser().getId();
						Configuration conf = this.configurationDao.getConfByUserId(recipientId);
						if (conf.isNew_story_from_following_push()) {
							List<PushNotification> list = this.pushNotificationDao
									.getPushNotificationByUserid(recipientId);
							pnList.addAll(list);
						}
					}
				}
				List<User> userList = this.userDao.getUserByUserType();
				Configuration conf = null;
				if ((userList != null) && (userList.size() > 0)) {
					for (User u : userList) {
						conf = this.configurationDao.getConfByUserId((Long) u.getId());
						if (conf.isNew_admin_push()) {
							List<PushNotification> list = this.pushNotificationDao
									.getPushNotificationByUserid((Long) u.getId());
							pnList.addAll(list);
						}
					}
				}
				Map<String, Integer> map = new HashMap<String, Integer>();
				if ((pnList != null) && (pnList.size() > 0)) {
					for (PushNotification pn : pnList) {
						int count = this.notificationDao.getNotificationByRecipientId(pn.getUserId());
						map.put(pn.getClientId(), count);
					}
				}
				String content = user.getUsername() + "发布了新的故事";
				JSONObject j = new JSONObject();
				j.put("story_id", storyId);

				PushNotificationUtil.pushInfoAllFollow(appId, appKey, masterSecret, pnList, map, content, j.toString());

			}
		} catch (Exception e) {
			json.put("status", "invalid_request");
			json.put("code", 10010);
			json.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		json.put("status", "success");
		return Response.status(Response.Status.OK).entity(json).build();
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

	@Override
	public Response getFBTag(Long loginUserid,JSONObject tag)throws Exception {
		String content = getClass().getResource("/../../META-INF/fb.json").getPath();
		JSONObject contentJson = ParseFile.parseJson(content);
		String url = contentJson.getString("url");
		JSONObject json = new JSONObject();
		List<LinkAccounts> lam = linkAccountsDao.getLinkAccountsByUserid(loginUserid);
		if(lam != null && lam.size() > 0){
			LinkAccounts la = lam.get(0);
			String authcode = la.getAuth_token();
			tag.put("serviceName", "fd_getforums");
			JSONObject tag_copy = JSONObject.fromObject(tag.toString());
			System.out.println("tag-->"+tag);
			String splitSign = HttpUtil.splitJSON(tag);
			String privateKey = HttpUtil.getPrivateKey(new Date());
			splitSign = splitSign + "&private_key=" +privateKey;
			System.out.println("splitSign-->"+splitSign);
			String sign = HttpUtil.getMD5Str(splitSign);
			System.out.println("sign-->"+sign);
			tag_copy.put("authcode", authcode);
			tag_copy.put("sign",sign);
			System.out.println("tag-->>>>>"+tag_copy.toString());
			String responseData = HttpUtil.sendPost(url+"bbsapinew/fd_getforums.php", tag_copy);
			return Response.status(Response.Status.OK).entity(responseData).build();
		}else{
			json.put("status", "invalid_request");
			json.put("code", 10010);
			json.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.OK).entity(json).build();
		}
		
	}

	@Override
	public Response synchroniseStory(Long loginUserid,String fbToken, JSONObject invitation,Long storyId) throws Exception {
		
		JSONObject json = new JSONObject();
		User user = userDao.get(loginUserid);
		//List<LinkAccounts> lam = linkAccountsDao.getLinkAccountsByUserid(loginUserid);
		if(user != null){
			//e族域名url
			String content = getClass().getResource("/../../META-INF/fb.json").getPath();
			JSONObject contentJson = ParseFile.parseJson(content);
			String url = contentJson.getString("url");
			
			//七牛信息
			String qiniu = getClass().getResource("/../../META-INF/qiniu.json").getPath();
			JSONObject qiniuJson = ParseFile.parseJson(qiniu);
			JSONObject media = JSONObject.fromObject(qiniuJson.getString("meta"));
			String image_url = media.getString("get_img_url");
			String video_url = media.getString("get_video_url");
			image_url = image_url.substring(0, image_url.lastIndexOf("/")+1);
			video_url = video_url.substring(0, video_url.lastIndexOf("/")+1);
			Story story = storyDao.get(storyId);
			String title = story.getTitle();
			List<StoryElement> seList = story.getElements();
			JSONObject message = new JSONObject();
			String cover_page = story.getCover_page();
			List<JSONObject> elementJsonList = new ArrayList<JSONObject>();
			if(!Strings.isNullOrEmpty(cover_page)){
				JSONObject cover_json = new JSONObject();
				JSONObject coverJson = JSONObject.fromObject(cover_page);
				String type = coverJson.getString("type");
				if(type.equals("image")){
					JSONObject mediaJson = JSONObject.fromObject(coverJson.get("media"));
					String cover_url = mediaJson.getString("name");
					cover_url = image_url+cover_url;
					mediaJson.put("url", cover_url);
					mediaJson.put("aid", "0");
					mediaJson.put("comment", "");
					coverJson.put("media", mediaJson);
					cover_json.put("content",coverJson);
					cover_json.put("grid_size", "{2,2}");
					cover_json.put("layout_type", "block");
					elementJsonList.add(cover_json);
				}
			}
			
			
			if(seList != null && seList.size() > 0){
				for(StoryElement se:seList){
					if(se.getContents() != null){
						JSONObject eleJson = new JSONObject();
						JSONObject cmJson = JSONObject.fromObject(se.getContents());
						String type = cmJson.getString("type");
						if(type.equals("image")){
							JSONObject mediaJson = JSONObject.fromObject(cmJson.getString("media"));
							String image = mediaJson.getString("name");
							String imagePath = image_url+image;
							mediaJson.put("url",imagePath);
							mediaJson.put("aid", "0");
							if(!mediaJson.containsKey("comment")){
								mediaJson.put("comment", "");
							}
							cmJson.put("media", mediaJson);
						}else if(type.equals("video")){
							JSONObject mediaJson = JSONObject.fromObject(cmJson.getString("media"));
							if(mediaJson.containsKey("name")){
								String videoUrl = video_url+mediaJson.getString("name");
								mediaJson.put("url_play", videoUrl);
								mediaJson.put("vid", "0");
								if(!mediaJson.containsKey("comment")){
									mediaJson.put("comment", "");
								}
								cmJson.put("media", mediaJson);
							}else{
								mediaJson.put("vid", "0");
								if(!mediaJson.containsKey("comment")){
									mediaJson.put("comment", "");
								}
								cmJson.put("media", mediaJson);
							}
						}
						
						eleJson.put("content", cmJson);
						
						eleJson.put("grid_size", se.getGrid_size());
						eleJson.put("layout_type", se.getLayout_type());
						elementJsonList.add(eleJson);
					}
				}
			}
			
			message.put("elements", elementJsonList);
			String authcode = fbToken;
			invitation.put("serviceName", "fd_postthread");
			invitation.put("authcode", authcode);
			invitation.put("subject", title);
			invitation.put("reqTime", new Date().getTime());
			//			invitation.put("message", message);
			JSONObject invitation_copy = JSONObject.fromObject(invitation.toString());
			System.out.println("tag-->"+invitation);
			String splitSign = HttpUtil.splitJSONCopy(invitation);
			String privateKey = HttpUtil.getPrivateKey(new Date());
			splitSign = splitSign + "&private_key=" +privateKey;
			System.out.println("splitSign-->"+splitSign);
			String sign = HttpUtil.getMD5Str(splitSign);
			System.out.println("sign-->"+sign);
//			invitation_copy.put("authcode", authcode);
			invitation_copy.put("sign",sign);
//			invitation_copy.put("subject", title);
			invitation_copy.put("message", message);
			System.out.println("tag-->>>>>"+invitation_copy.toString());
			String responseData = HttpUtil.sendPostStr(url+"bbsapinew/fd_postthread.php", invitation_copy.toString());
			JSONObject responseJson = JSONObject.fromObject(responseData);
			JSONObject rspInfo = responseJson.getJSONObject("rspInfo");
			if(rspInfo.getInt("rspCode") == 1000){
				JSONObject rspData = JSONObject.fromObject(responseJson.get("rspData"));
				story.setTid(Long.parseLong(rspData.getString("tid")));
				story.setFid(Long.parseLong(rspData.getString("fid")));
				story.setPid(Long.parseLong(rspData.getString("pid")));
				storyDao.update(story);
				json.put("status","success");
				return Response.status(Response.Status.OK).entity(json).build();
			}else{
				json.put("status", rspInfo.getString("rspType"));
				json.put("code", rspInfo.getString("rspCode"));
				json.put("error_message", rspInfo.getString("rspDesc"));
				return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
			}
			
		}else{
			json.put("status", "invalid_request");
			json.put("code", 10010);
			json.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
		
	}

	@Override
	public Response synchroniseEditStory(Long loginUserid,String fbToken, JSONObject invitation) throws Exception {
		JSONObject json = new JSONObject();

		System.out.println("update------->>>>>>>>>>>>>>>>");
		
		//e族域名url
		String content = getClass().getResource("/../../META-INF/fb.json").getPath();
		JSONObject contentJson = ParseFile.parseJson(content);
		String url = contentJson.getString("url");
		
		//七牛信息
		String qiniu = getClass().getResource("/../../META-INF/qiniu.json").getPath();
		JSONObject qiniuJson = ParseFile.parseJson(qiniu);
		JSONObject media = qiniuJson.getJSONObject("meta");
		String image_url = media.getString("get_img_url");
		image_url = image_url.substring(0, image_url.lastIndexOf("/")+1);
		String video_url = media.getString("get_video_url");
		video_url = video_url.substring(0, video_url.lastIndexOf("/")+1);
		
		JSONObject storyModel = JSONObject.fromObject(invitation.get("storyDraftModel"));
		
		String title = storyModel.getString("title");
		JSONObject message = new JSONObject();
		String cover_page = storyModel.getString("cover_media");
		List<JSONObject> elementJsonList = new ArrayList<JSONObject>();
		if(!Strings.isNullOrEmpty(cover_page)){
			JSONObject cover_json = new JSONObject();
			JSONObject coverJson = JSONObject.fromObject(cover_page);
			String type = coverJson.getString("type");
			if(type.equals("image")){
				JSONObject mediaJson = JSONObject.fromObject(coverJson.get("media"));
				String cover_url = mediaJson.getString("name");
				cover_url = image_url+cover_url;
				mediaJson.put("url", cover_url);
				mediaJson.put("aid", "0");
				mediaJson.put("comment", "");
				coverJson.put("media", mediaJson);
				cover_json.put("content",coverJson);
				cover_json.put("grid_size", "{2,2}");
				cover_json.put("layout_type", "block");
				elementJsonList.add(cover_json);
			}
		}
		
		JSONArray seList = JSONArray.fromObject(storyModel.getString("elements"));
		if(seList != null && seList.size() > 0){
			for(Object se:seList){
				JSONObject ele = JSONObject.fromObject(se);
				if(ele.getString("content") != null){
					JSONObject eleJson = new JSONObject();
					JSONObject cmJson = JSONObject.fromObject(ele.getString("content"));
					String type = cmJson.getString("type");
					if(type.equals("image")){
						JSONObject mediaJson = JSONObject.fromObject(cmJson.getString("media"));
						if(mediaJson.containsKey("name")){
							String image = mediaJson.getString("name");
							if(!Strings.isNullOrEmpty(image)){
								String imagePath = image_url+image;
								mediaJson.put("url",imagePath);
							}
						}else{
							String image = mediaJson.getString("url");
							if(!Strings.isNullOrEmpty(image)){
								mediaJson.put("url",image);
							}
						}
						
						mediaJson.put("aid", "0");
						if(!mediaJson.containsKey("comment")){
							mediaJson.put("comment", "");
						}
						cmJson.put("media", mediaJson);
					}else if(type.equals("video")){
						JSONObject mediaJson = JSONObject.fromObject(cmJson.getString("media"));
						if(mediaJson.containsKey("name")){
							String videoUrl = video_url+mediaJson.getString("name");
							mediaJson.put("url_play", videoUrl);
							mediaJson.put("vid", "0");
							if(!mediaJson.containsKey("comment")){
								mediaJson.put("comment", "");
							}
							cmJson.put("media", mediaJson);
						}else{
							mediaJson.put("vid", "0");
							if(!mediaJson.containsKey("comment")){
								mediaJson.put("comment", "");
							}
							cmJson.put("media", mediaJson);
						}
					}
					
					eleJson.put("content", cmJson);
					
					eleJson.put("grid_size", ele.getString("grid_size"));
					eleJson.put("layout_type", ele.getString("layout_type"));
					elementJsonList.add(eleJson);
				}
			}
		}
		
		message.put("elements", elementJsonList);
		String authcode = fbToken;
		invitation.remove("storyDraftModel");
		invitation.put("reqTime", new Date().getTime());
		invitation.put("serviceName", "fd_editthread");
		invitation.put("authcode", authcode);
		invitation.put("subject", title);
		invitation.put("tid",storyModel.getInt("tid"));
		invitation.put("pid",storyModel.getInt("pid"));
		invitation.put("fid",storyModel.getInt("fid"));
		JSONObject invitation_copy = JSONObject.fromObject(invitation.toString());
		System.out.println("tag-->"+invitation);
		String splitSign = HttpUtil.splitJSONCopy(invitation);
		String privateKey = HttpUtil.getPrivateKey(new Date());
		splitSign = splitSign + "&private_key=" +privateKey;
		System.out.println("splitSign-->"+splitSign);
		String sign = HttpUtil.getMD5Str(splitSign);
		System.out.println("sign-->"+sign);
//		invitation_copy.put("authcode", authcode);
		invitation_copy.put("sign",sign);
//		invitation_copy.put("subject", title);
		invitation_copy.put("message", message.toString());
//		invitation_copy.put("tid",storyModel.getInt("tid"));
//		invitation_copy.put("pid",storyModel.getInt("pid"));
//		invitation_copy.put("fid",storyModel.getInt("fid"));
		System.out.println("tag-->>>>>"+invitation_copy.toString());
		String responseData = HttpUtil.sendPostStr(url+"bbsapinew/fd_editthread.php", invitation_copy.toString());
		JSONObject responseJson = JSONObject.fromObject(responseData);
		JSONObject rspInfo = responseJson.getJSONObject("rspInfo");
		if(rspInfo.getInt("rspCode") == 1000){
			json.put("status","success");
			System.out.println("update story-->>>>success");
			return Response.status(Response.Status.OK).entity(json).build();
		}else{
			json.put("status", rspInfo.getString("rspType"));
			json.put("code", rspInfo.getString("rspCode"));
			json.put("error_message", rspInfo.getString("rspDesc"));
			return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
		}
	
	
	}

	@Override
	public Response getVideo(JSONObject video) throws Exception {
		JSONObject returnJson = new JSONObject();
		if(video != null){
			String url = video.getString("url");
			if(url.contains("youku.com")){
				try{
					String iframe = HttpUtil.getYouKuVideo(url);
					if(!Strings.isNullOrEmpty(iframe)){
						returnJson.put("iframe_code",iframe);
						returnJson.put("url", url);
						return Response.status(Response.Status.OK).entity(returnJson).build();
					}else{
						returnJson.put("status","invalid_url");
						returnJson.put("code",10111);
						returnJson.put("error_message", "Invalid url parameters");
						return Response.status(Response.Status.BAD_GATEWAY).entity(returnJson).build();
					}
					
				}catch(Exception e){
					returnJson.put("status","invalid_url");
					returnJson.put("code",10111);
					returnJson.put("error_message", "Invalid url parameters");
					return Response.status(Response.Status.BAD_GATEWAY).entity(returnJson).build();
				}
				
			}
		}else{
			returnJson.put("status", "invalid_request");
			returnJson.put("code", 10010);
			returnJson.put("error_message", "Invalid payload parameters");
			return Response.status(Response.Status.BAD_REQUEST).entity(returnJson).build();
		}
		
		return null;
	}

	@Override
	public Response addStory(JSONObject storyModel, Long loginUserid, HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if(Strings.isNullOrEmpty(ip)){
			ip = request.getRemoteAddr();
		}
		String urlkey = getClass().getResource("/../../META-INF/user_centre.json").getPath();
		JSONObject jsonObject = parseJson(urlkey);
		String url = jsonObject.getString("fmap_url");
		String params = "";
		String result = "";
		log.debug("create story");
		try {

			Story story = new Story();

			if (storyModel.containsKey("title"))
				story.setTitle(storyModel.getString("title"));

			if (storyModel.containsKey("cover_media"))
				story.setCover_page(storyModel.getString("cover_media"));
			else {
				story.setCover_page(null);
			}

			if (storyModel.containsKey("summary"))
				story.setSummary(storyModel.getString("summary"));
			else {
				story.setSummary(null);
			}
			story.setRecommendation(false);

			if (storyModel.containsKey("image_count")) {
				story.setImage_count(storyModel.getInt("image_count"));
			}
			story.setStatus("publish");
			story.setUpdate_time(new Date());
			story.setLast_comment_date(new Date());
			User user = this.userDao.get(loginUserid);
			if (user != null) {
				story.setUser(user);
			}
			JSONArray jsonArray = JSONArray.fromObject(storyModel.getString("elements"));
			List<StoryElement> seSet = new ArrayList<StoryElement>();
			JSONObject jo = null;
			if ((jsonArray != null) && (jsonArray.size() > 0)) {
				StoryElement element = null;
				for (int i = 0; i < jsonArray.size(); i++) {
					element = new StoryElement();
					jo = (JSONObject) jsonArray.get(i);
					element.setGrid_size(jo.getString("grid_size"));
					element.setLayout_type(jo.getString("layout_type"));
					element.setContents(jo.getString("content"));
					element.setStoryinfo(story);
					seSet.add(element);
				}
			}
			story.setElements(seSet);
			
			
			
			
			this.storyDao.save(story);
			log.debug("start add activity$$$$$$$$$$$$$$$$$$$$$$$$$$$" + story.getUser().getId() + " -->"
					+ story.getId());
			story.setTinyURL(makeURL(story.getId()));
			storyDao.update(story);
			UserCentre uc = userCentreDao.getUserCentreByUserId(loginUserid);
			int centre_id = 0;
			if(uc != null){
				centre_id = uc.getCentre_id();
			}else{
				LinkAccounts la = linkAccountsDao.getLinkAccountsByUseridAndService(loginUserid, "fblife");
				centre_id = Integer.parseInt(la.getUuid());
			}
			
			if(centre_id > 0){
				storyModel.put("story_id", story.getId());
				storyModel.put("author_id", centre_id);
				storyModel.put("tiny_url", story.getTinyURL());
				List<StoryElement> seList = story.getElements();
				JsonConfig config = new JsonConfig();
				config.setExcludes(new String[] { "storyinfo", "grid_size", "layout_type", "content" });
				config.setIgnoreDefaultExcludes(false);
				config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
				JSONArray ja = JSONArray.fromObject(seList,config);
				if(storyModel.containsKey("elements")){
					storyModel.remove("elements");
					storyModel.put("elements",ja);
				}else{
					storyModel.put("elements",ja);
				}
				Map<String,String> param = new HashMap<String,String>();
				param.put("user_id",String.valueOf(centre_id));
				param.put("data",storyModel.toString());
				param.put("ip", ip);
				params = publicParam(param);
				result = HttpUtil.sendPostStr(url+"/v1/info/info-wiki/create-wiki",params);
				
				if (!Strings.isNullOrEmpty(result)) {
					JSONObject json = JSONObject.fromObject(result);
					int status = json.getInt("code");
					if (status == 10000) {
						JSONArray collection_id = null;
						if (storyModel.containsKey("collection_id")) {
							collection_id = storyModel.getJSONArray("collection_id");
							if(collection_id != null && collection_id.size() > 0){
								Object[] ids = collection_id.toArray();
								for(Object id:ids){
									CollectionStory cs = new CollectionStory();
									Collection collection = collectionDao.get(Long.parseLong(id.toString()));
									cs.setStory(story);
									cs.setCollection(collection);
									collectionStoryDao.save(cs);
								}
							}
							

						}
						List<Follow> followList = this.followDao.getFollowersByUserId(story.getUser().getId());
						Timeline timeline = new Timeline();
						timeline.setCreatorId(loginUserid);
						timeline.setTargetUserId(loginUserid);
						timeline.setStory(story);
						timeline.setType("post");
						timeline.setReferenceId((Long) story.getId());
						timeline.setCreateTime(new Date());
						this.timelineDao.save(timeline);
						log.debug("***add activity success***");
						EventModel event = getEventModelListByLoginUserid(timeline, loginUserid, collection_id);
						log.debug("***start add notification***" + JSONObject.fromObject(event));
						if (!user.getUser_type().equals("media")) {
							List<Notification> notificationList = new ArrayList<Notification>();
							Notification n = null;
							if ((followList != null) && (followList.size() > 0)) {
								for (Follow follow : followList) {
									Long recipientId = (Long) follow.getPk().getUser().getId();
									n = new Notification();
									n.setRecipientId(recipientId);
									n.setSenderId((Long) story.getUser().getId());
									n.setNotificationType(4);
									n.setObjectType(1);
									n.setObjectId((Long) story.getId());
									n.setStatus("enabled");
									n.setRead_already(true);
									notificationList.add(n);
								}
							}
							List<User> userList = this.userDao.getUserByUserType();
							if ((userList != null) && (userList.size() > 0)) {
								for (User u : userList) {
									n = new Notification();
									n.setRecipientId((Long) u.getId());
									n.setSenderId((Long) story.getUser().getId());
									n.setNotificationType(4);
									n.setObjectType(1);
									n.setObjectId((Long) story.getId());
									n.setStatus("enabled");
									n.setRead_already(true);
									notificationList.add(n);
								}
							}
							this.notificationDao.saveNotifications(notificationList);

						}

						log.debug("***add notification success***");
						return Response.status(Response.Status.OK).entity(storyModel).build();
					}else{
						JSONObject result_json = new JSONObject();
						result_json.put("status", "发布失败，请重试");
						result_json.put("code", 10670);
						result_json.put("error_message", "发布失败，请重试");

						return Response.status(Response.Status.OK).entity(result_json).build();
					}
				}else{
					JSONObject result_json = new JSONObject();
					result_json.put("status", "用户中心没有数据返回"+params);
					result_json.put("code", 10678);
					result_json.put("error_message", "用户中心没有数据返回"+result);
					return Response.status(Response.Status.BAD_REQUEST).entity(result_json).build();
				}
			}else{
				JSONObject result_json = new JSONObject();
				result_json.put("status", "发布失败，请重试");
				result_json.put("code", 10670);
				result_json.put("error_message", "发布失败，请重试");
				return Response.status(Response.Status.OK).entity(result_json).build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("status", "invalid_request");
			jo.put("code", Integer.valueOf(10010));
			jo.put("error_message", "Invalid request payload");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
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
	public Response getFbInvitation(Long loginUserid, String fbToken,JSONObject invitation) throws Exception {
		//e族域名url
		String content = getClass().getResource("/../../META-INF/fb.json").getPath();
		JSONObject contentJson = ParseFile.parseJson(content);
		String url = contentJson.getString("url");
		
		String authcode = fbToken;
		Long tid = invitation.getLong("tid");
		Long pid = invitation.getLong("pid");
		invitation.put("authcode", authcode);
		invitation.put("reqTime",new Date().getTime());
		invitation.put("serviceName", "fd_viewthread");
		JSONObject invitation_copy = JSONObject.fromObject(invitation.toString());
		System.out.println("tag-->"+invitation);
		String splitSign = HttpUtil.splitJSONCopy(invitation);
		String privateKey = HttpUtil.getPrivateKey(new Date());
		splitSign = splitSign + "&private_key=" +privateKey;
		System.out.println("splitSign-->"+splitSign);
		String sign = HttpUtil.getMD5Str(splitSign);
		System.out.println("sign-->"+sign);
		invitation_copy.put("sign", sign);
		System.out.println("tag-->>>>>"+invitation_copy.toString());
		String responseData = HttpUtil.sendPostStr(url+"bbsapinew/fd_viewthread.php", invitation_copy.toString());
		JSONObject responseJson = JSONObject.fromObject(responseData);
		JSONObject rspInfo = responseJson.getJSONObject("rspInfo");
		
		if(rspInfo.getInt("rspCode") == 1000){
			JSONObject story = new JSONObject();
			Story storyObj = storyDao.getStoryByTidAndPid(tid,pid);
			JSONObject rspData = responseJson.getJSONObject("rspData");
			JSONObject postinfo = rspData.getJSONObject("postinfo");
			if(postinfo.containsKey("subject")){
				String subject = postinfo.getString("subject");
				if(!Strings.isNullOrEmpty(subject)){
					story.put("title", subject);
				}
			}
			if(postinfo.containsKey("new_message")){
				Object obj = postinfo.get("new_message");
				if(obj != null){
					JSONObject elementJson = JSONObject.fromObject(obj);
					JSONArray elementArr = JSONArray.fromObject(elementJson.get("elements"));
					JSONObject cover = JSONObject.fromObject(storyObj.getCover_page());
					if(cover.getString("type").equals("image")){
						JSONObject coverJson = JSONObject.fromObject(elementArr.get(0));
						story.put("cover_media", JSONObject.fromObject(coverJson.get("content")));
						elementArr.remove(0);
						story.put("elements", elementArr);
					}else{
						story.put("cover_media", JSONObject.fromObject(storyObj.getCover_page()));
						story.put("elements", elementArr);
					}
					
				}
			}
			
			story.put("id", storyObj.getId());
			story.put("tid", rspData.getInt("tid"));
			story.put("pid", rspData.getInt("pid"));
			story.put("fid", rspData.getInt("fid"));
			return Response.status(Response.Status.OK).entity(story).build();
		}else{
			JSONObject jo = new JSONObject();
			jo.put("status", rspInfo.getString("rspType"));
			jo.put("code", rspInfo.getString("rspCode"));
			jo.put("error_message", rspInfo.getString("rspDesc"));
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
	}

	@Override
	public Response createLike(JSONObject json, HttpServletRequest request) {
		if(json != null){
			Long story_id = json.getLong("story_id");
			int centre_id = json.getInt("user_id");
			String recognition_type = json.getString("recognition_type");
			UserCentre uc = userCentreDao.getUserCentreByCentreId(centre_id);
			Long user_id = 0l;
			if(uc == null){
				User u = new User();
				String chars = "abcde0f12g3hi4jk5l6m7n8o9pqrstuvwxyz";
		    	StringBuffer sb = new StringBuffer();
		    	for(int i=0;i<10;i++){
		    		char c = chars.charAt((int)(Math.random() * 10));
		    		sb.append(c);
		    	}
		    	u.setPassword(sb.toString());
		    	u.setUsername("username");
		    	u.setSalt(initSalt().toString());
		    	u.setStatus("enabled");
		    	u.setUser_type("normal");
				
		    	this.userDao.save(u);
		    	user_id = u.getId();
		    	UserCentre userc = new UserCentre();
		    	userc.setCentre_id(centre_id);
		    	userc.setUser_id(u.getId());
				userCentreDao.save(userc);
			}else{
				user_id = uc.getUser_id();
			}
			
			
			if(recognition_type.equals("-1")){
				likesDao.deleteLike(user_id, story_id);
				JSONObject jo = new JSONObject();
				jo.put("code", 10000);
				jo.put("msg", "成功");
				
				return Response.status(Response.Status.OK).entity(jo).build();
			}else if(recognition_type.equals("10")){
				String path = getClass().getResource("/../../META-INF/getui.json").getPath();
				JSONObject jsonObject = ParseFile.parseJson(path);
				String appId = jsonObject.getString("appId");
				String appKey = jsonObject.getString("appKey");
				String masterSecret = jsonObject.getString("masterSecret");
				User user = (User) this.userDao.get(user_id);
				Story story = (Story) this.storyDao.get(story_id);
				Likes likes = new Likes();
				likes.setCreateTime(new Date());
				likes.setLike_users(user);
				likes.setLike_story(story);
				this.likesDao.save(likes);
				Notification notification = this.notificationDao.getNotificationByAction(story_id, user_id, 1, 2);
				if (notification != null) {
					notification.setCreate_at(new Date());
					this.notificationDao.update(notification);
				} else {
					notification = new Notification();
					notification.setSenderId(user_id);
					notification.setRecipientId((Long) story.getUser().getId());
					notification.setNotificationType(2);
					notification.setObjectType(1);
					notification.setObjectId(story_id);
					notification.setRead_already(false);
					notification.setStatus("enabled");
					this.notificationDao.save(notification);
				}
				Configuration conf = this.configurationDao.getConfByUserId((Long) story.getUser().getId());
				if (conf.isNew_favorite_from_following_push()) {
					int counts = this.notificationDao.getNotificationByRecipientId((Long) story.getUser().getId());
					List<PushNotification> list = this.pushNotificationDao
							.getPushNotificationByUserid((Long) story.getUser().getId());
					try {
						String content = user.getUsername() + "喜欢了我的故事";
						JSONObject json1 = new JSONObject();
						json1.put("story_id", story.getId());
						PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content, json1.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				JSONObject jo = new JSONObject();
				jo.put("code", 10000);
				jo.put("msg", "成功");
				
				return Response.status(Response.Status.CREATED).entity(jo).build();
			}else{
				return null;
			}
		}else{
			JSONObject jo = new JSONObject();
			jo.put("code", 10003);
			jo.put("msg", "参数错误");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
		}
		
		
	
	}

	public byte[] initSalt() {
		byte[] b = new byte[8];
		Random random = new Random();
		random.nextBytes(b);
		return b;
	}
}
