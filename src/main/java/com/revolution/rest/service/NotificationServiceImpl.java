package com.revolution.rest.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.revolution.rest.dao.CollectionDao;
import com.revolution.rest.dao.CollectionStoryDao;
import com.revolution.rest.dao.CommentDao;
import com.revolution.rest.dao.FollowDao;
import com.revolution.rest.dao.NotificationDao;
import com.revolution.rest.dao.PushNotificationDao;
import com.revolution.rest.dao.StoryDao;
import com.revolution.rest.dao.UserCollectionDao;
import com.revolution.rest.dao.UserDao;
import com.revolution.rest.model.Collection;
import com.revolution.rest.model.CollectionStory;
import com.revolution.rest.model.Comment;
import com.revolution.rest.model.Follow;
import com.revolution.rest.model.Notification;
import com.revolution.rest.model.PushNotification;
import com.revolution.rest.model.Story;
import com.revolution.rest.model.User;
import com.revolution.rest.model.UserCollection;
import com.revolution.rest.service.model.CollectionIntro;
import com.revolution.rest.service.model.CollectionNotification;
import com.revolution.rest.service.model.CommentModel;
import com.revolution.rest.service.model.ContentModel;
import com.revolution.rest.service.model.NotificationModel;
import com.revolution.rest.service.model.StoryIntro;
import com.revolution.rest.service.model.StoryIntroCollection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@Transactional
public class NotificationServiceImpl implements NotificationService {
	private static final Log log = LogFactory.getLog(NotificationServiceImpl.class);

	@Autowired
	private NotificationDao notificationDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CommentDao commentDao;

	@Autowired
	private StoryDao storyDao;

	@Autowired
	private CollectionStoryDao collectionStoryDao;

	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private FollowDao followDao;

	@Autowired
	private UserCollectionDao userCollectionDao;

	/*
	 * @Autowired private ConfigurationDao configurationDao;
	 */

	@Autowired
	private PushNotificationDao pushNotificationDao;

	public Notification getNotification(Long id) {
		return null;
	}

	public List<NotificationModel> getNotifications(Long userId, HttpServletRequest request) {
		log.debug("*** start get notifications ***");
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		int count = 20;
		List<Notification> nList = null;
		if (Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr) && Strings.isNullOrEmpty(maxIdStr)) {
			nList = this.notificationDao.getNotifications(userId, count);
		} else if (!Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr)
				&& Strings.isNullOrEmpty(maxIdStr)) {
			count = Integer.parseInt(countStr);
			nList = this.notificationDao.getNotifications(userId, count);
		} else if (Strings.isNullOrEmpty(countStr) && !Strings.isNullOrEmpty(sinceIdStr)
				&& Strings.isNullOrEmpty(maxIdStr)) {
			nList = this.notificationDao.getNotificationsByPage(userId, count, Long.parseLong(sinceIdStr), 1);
		} else if (!Strings.isNullOrEmpty(countStr) && !Strings.isNullOrEmpty(sinceIdStr)
				&& Strings.isNullOrEmpty(maxIdStr)) {
			count = Integer.parseInt(countStr);
			nList = this.notificationDao.getNotificationsByPage(userId, count, Long.parseLong(sinceIdStr), 1);
		} else if (Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr)
				&& !Strings.isNullOrEmpty(maxIdStr)) {
			nList = this.notificationDao.getNotificationsByPage(userId, count, Long.parseLong(maxIdStr), 2);
		} else if (!Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr)
				&& !Strings.isNullOrEmpty(maxIdStr)) {
			count = Integer.parseInt(countStr);
			nList = this.notificationDao.getNotificationsByPage(userId, count, Long.parseLong(maxIdStr), 2);
		}
		List<NotificationModel> notificationModelList = new ArrayList<NotificationModel>();
		// Configuration conf = this.configurationDao.getConfByUserId(userId);
		if ((nList != null) && (nList.size() > 0)) {
			this.notificationDao.updateNotificationByLoginUserid(userId);
			for (Notification n : nList) {
				NotificationModel notificationModel = new NotificationModel();
				notificationModel.setId((Long) n.getId());
				notificationModel.setCreated_at(n.getCreate_at());
				notificationModel.setRecipient_id(n.getRecipientId());
				notificationModel.setRead_already(n.getRead_already());
				ContentModel contentModel = new ContentModel();
				JSONObject contentJson = new JSONObject();
				User user = (User) this.userDao.get(n.getSenderId());
				JSONObject sender = new JSONObject();
				sender.put("id", user.getId());
				sender.put("username", user.getUsername());
				sender.put("introduction", user.getIntroduction());
				if (!Strings.isNullOrEmpty(user.getAvatarImage()))
					sender.put("avatar_image", JSONObject.fromObject(user.getAvatarImage()));

				if (n.getNotificationType() == 1) {

					notificationModel.setNotification_type("new_follower_push");
					Follow follow = followDao.getFollow(userId, n.getSenderId());
					if (follow != null) {
						sender.put("followed_by_current_user", true);
					} else {
						sender.put("followed_by_current_user", false);
					}
					contentModel.setSender(sender);
					contentJson = JSONObject.fromObject(contentModel);
					JSONObject send = JSONObject.fromObject(contentJson.get("sender"));
					if (send.get("avatar_image") == null) {
						send.remove("avatar_image");
						contentJson.remove("sender");
						contentJson.put("sender", send);
					}
					contentJson.remove("comment");
					contentJson.remove("story");
					contentJson.remove("collection");

					/*
					 * if (conf.isNew_follower_push()) {} else {
					 * notificationModel = null; }
					 */
				} else if (n.getNotificationType() == 2) {

					List<String> delArr = new ArrayList<String>();
					notificationModel.setNotification_type("new_favorite_from_following_push");
					contentModel.setSender(sender);
					StoryIntro storyIntro = new StoryIntro();
					Story story = null;
					if (n.getObjectType() == 1) {
						story = this.storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
						if (story != null) {
							storyIntro.setId((Long) story.getId());
							storyIntro.setTitle(story.getTitle());
							storyIntro.setCollectionId(Long.valueOf(1L));
							if (!Strings.isNullOrEmpty(story.getCover_page()))
								storyIntro.setCover_media(JSONObject.fromObject(story.getCover_page()));
							else {
								storyIntro.setCover_media(null);
							}
							storyIntro.setSummary(story.getSummary());
							if (Strings.isNullOrEmpty(story.getTitle())) {
								delArr.add("title");
							}

							if (Strings.isNullOrEmpty(story.getSummary()))
								delArr.add("summary");
						} else {
							notificationModel = null;
						}

					}

					if (story != null) {
						JsonConfig config = new JsonConfig();
						config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
						config.setIgnoreDefaultExcludes(false);
						config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						if (storyIntro.getCover_media() != null) {
							contentModel.setStory(JSONObject.fromObject(storyIntro, config));
						}
						contentJson = JSONObject.fromObject(contentModel);
						contentJson.remove("comment");
						contentJson.remove("collection");
					} else {
						notificationModel = null;
					}

					/*
					 * if (conf.isNew_favorite_from_following_push()) {} else {
					 * notificationModel = null; }
					 */
				} else if (n.getNotificationType() == 3) {

					notificationModel.setNotification_type("reposted_my_story_push");
					contentModel.setSender(sender);
					StoryIntro storyIntro = new StoryIntro();
					List<String> delArr = new ArrayList<String>();
					Story story = null;
					if (n.getObjectType() == 1) {
						story = this.storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
						if (story != null) {
							storyIntro.setId((Long) story.getId());
							storyIntro.setTitle(story.getTitle());
							storyIntro.setCollectionId(Long.valueOf(1L));
							if (!Strings.isNullOrEmpty(story.getCover_page()))
								storyIntro.setCover_media(JSONObject.fromObject(story.getCover_page()));
							else {
								storyIntro.setCover_media(null);
							}
							storyIntro.setSummary(story.getSummary());
							if (Strings.isNullOrEmpty(story.getTitle())) {
								delArr.add("title");
							}

							if (Strings.isNullOrEmpty(story.getSummary()))
								delArr.add("summary");
						} else {
							notificationModel = null;
						}

					}

					if (story != null) {
						JsonConfig config = new JsonConfig();
						config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
						config.setIgnoreDefaultExcludes(false);
						config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						if (storyIntro.getCover_media() != null) {
							contentModel.setStory(JSONObject.fromObject(storyIntro, config));
						}
						contentJson = JSONObject.fromObject(contentModel);
						contentJson.remove("comment");
						contentJson.remove("collection");
					}

					/*
					 * if (conf.isReposted_my_story_push()) {} else {
					 * notificationModel = null; }
					 */
				} else if (n.getNotificationType() == 4) {

					notificationModel.setNotification_type("new_story_from_following_push");
					contentModel.setSender(sender);
					StoryIntro storyIntro = new StoryIntro();
					List<String> delArr = new ArrayList<String>();
					if (n.getObjectType() == 1) {
						Story story = this.storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
						if (story != null) {
							storyIntro.setId((Long) story.getId());
							storyIntro.setTitle(story.getTitle());
							storyIntro.setCollectionId(Long.valueOf(1L));

							if (!Strings.isNullOrEmpty(story.getCover_page()))
								storyIntro.setCover_media(JSONObject.fromObject(story.getCover_page()));
							else {
								storyIntro.setCover_media(null);
							}

							storyIntro.setSummary(story.getSummary());

							if (Strings.isNullOrEmpty(story.getTitle())) {
								delArr.add("title");
							}

							if (Strings.isNullOrEmpty(story.getSummary())) {
								delArr.add("summary");
							}

							JsonConfig config = new JsonConfig();
							config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
							config.setIgnoreDefaultExcludes(false);
							config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							if (storyIntro.getCover_media() != null) {
								contentModel.setStory(JSONObject.fromObject(storyIntro, config));
							}
							contentJson = JSONObject.fromObject(contentModel);
							contentJson.remove("comment");
							contentJson.remove("collection");
						} else {
							notificationModel = null;
						}
					}

					/*
					 * if (conf.isNew_story_from_following_push()) {} else {
					 * notificationModel = null; }
					 */
				} else if (n.getNotificationType() == 5) {

					notificationModel.setNotification_type("new_comment_on_your_story_push");
					contentModel.setSender(sender);
					Comment comment = this.commentDao.getCommentByIdAndStatus(n.getObjectId(), "enabled");
					List<String> delArr = new ArrayList<String>();
					if (comment != null) {
						CommentModel cm = getCommentModel(comment);
						List<String> delArr_comment = new ArrayList<String>();
						if (cm.getComment_image() == null) {
							delArr_comment.add("comment_image");
						}
						JsonConfig config_comment = new JsonConfig();
						config_comment
								.setExcludes((String[]) delArr_comment.toArray(new String[delArr_comment.size()]));
						config_comment.setIgnoreDefaultExcludes(false);
						config_comment.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						JSONObject commentJson = JSONObject.fromObject(cm, config_comment);
						if (commentJson.getJSONObject("target_user") == null) {
							commentJson.remove("target_user");
						}

						if (commentJson.getJSONObject("comment_image") == null) {
							commentJson.remove("comment_image");
						}

						contentModel.setComment(commentJson);
						Story s = comment.getStory();
						StoryIntro storyIntro = new StoryIntro();

						storyIntro.setId((Long) s.getId());
						storyIntro.setTitle(s.getTitle());
						storyIntro.setCollectionId(Long.valueOf(1L));
						storyIntro.setCover_media(JSONObject.fromObject(s.getCover_page()));
						storyIntro.setSummary(s.getSummary());
						if (Strings.isNullOrEmpty(s.getTitle())) {
							delArr.add("title");
						}

						if (Strings.isNullOrEmpty(s.getSummary())) {
							delArr.add("summary");
						}

						JsonConfig config = new JsonConfig();
						config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
						config.setIgnoreDefaultExcludes(false);
						config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						if (storyIntro.getCover_media() != null) {
							contentModel.setStory(JSONObject.fromObject(storyIntro, config));
						}

						contentJson = JSONObject.fromObject(contentModel);
						contentJson.remove("collection");
					} else {
						notificationModel = null;
					}

					/*
					 * if (conf.isNew_comment_on_your_story_push()) {} else {
					 * notificationModel = null; }
					 */
				} else if (n.getNotificationType() == 6) {

					notificationModel.setNotification_type("new_comment_on_your_comment_push");
					contentModel.setSender(sender);
					List<String> delArr = new ArrayList<String>();
					Comment comment = (Comment) this.commentDao.get(n.getObjectId());
					CommentModel cm = getCommentModel(comment);
					List<String> delArr_comment = new ArrayList<String>();
					if (cm.getComment_image() == null) {
						delArr_comment.add("comment_image");
					}
					JsonConfig config_comment = new JsonConfig();
					config_comment.setExcludes((String[]) delArr_comment.toArray(new String[delArr_comment.size()]));
					config_comment.setIgnoreDefaultExcludes(false);
					config_comment.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

					JSONObject commentJson = JSONObject.fromObject(cm, config_comment);
					if (commentJson.getJSONObject("target_user") == null) {
						commentJson.remove("target_user");
					}

					contentModel.setComment(commentJson);
					Story s = comment.getStory();
					if (s.getStatus().equals("publish")) {
						StoryIntro storyIntro = new StoryIntro();

						storyIntro.setId((Long) s.getId());
						storyIntro.setTitle(s.getTitle());
						storyIntro.setCollectionId(Long.valueOf(1L));
						storyIntro.setCover_media(JSONObject.fromObject(s.getCover_page()));
						storyIntro.setSummary(s.getSummary());

						if (Strings.isNullOrEmpty(s.getTitle())) {
							delArr.add("title");
						}

						if (Strings.isNullOrEmpty(s.getSummary())) {
							delArr.add("summary");
						}

						JsonConfig config = new JsonConfig();
						config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
						config.setIgnoreDefaultExcludes(false);
						config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

						if (storyIntro.getCover_media() != null) {
							contentModel.setStory(JSONObject.fromObject(storyIntro, config));
						}
						contentJson = JSONObject.fromObject(contentModel);
						contentJson.remove("collection");
					} else {
						notificationModel = null;
					}

					/*
					 * if (conf.isNew_comment_on_your_comment_push()) {} else {
					 * notificationModel = null; }
					 */

				} else if (n.getNotificationType() == 7) {

					notificationModel.setNotification_type("recommended_my_story_push");
					contentModel.setSender(sender);
					StoryIntro storyIntro = new StoryIntro();
					List<String> delArr = new ArrayList<String>();
					JsonConfig config = new JsonConfig();
					if (n.getObjectType() == 1) {
						Story story = this.storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
						if (story != null) {
							storyIntro.setId((Long) story.getId());
							storyIntro.setTitle(story.getTitle());
							storyIntro.setCollectionId(Long.valueOf(1L));
							storyIntro.setCover_media(JSONObject.fromObject(story.getCover_page()));
							storyIntro.setSummary(story.getSummary());
							if (Strings.isNullOrEmpty(story.getTitle())) {
								delArr.add("title");
							}

							if (Strings.isNullOrEmpty(story.getSummary())) {
								delArr.add("summary");
							}

							config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
							config.setIgnoreDefaultExcludes(false);
							config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
							JSONObject sJson = JSONObject.fromObject(storyIntro, config);
							contentModel.setStory(sJson);
							contentJson = JSONObject.fromObject(contentModel);
							contentJson.remove("collection");
							contentJson.remove("comment");
							contentJson.remove("sender");
						} else {
							notificationModel = null;
						}
					}

					/*
					 * if (conf.isRecommended_my_story_push()) {} else {
					 * notificationModel = null; }
					 */
				}
				if (n.getNotificationType() == 8) {

					notificationModel.setNotification_type("new_user_push");
					Follow follow = followDao.getFollow(userId, n.getSenderId());
					if (follow != null) {
						sender.put("followed_by_current_user", true);
					} else {
						sender.put("followed_by_current_user", false);
					}
					contentModel.setSender(sender);
					contentJson = JSONObject.fromObject(contentModel);
					contentJson.remove("comment");
					contentJson.remove("story");
					contentJson.remove("collection");

					/*
					 * if (conf.isNew_follower_push()) {} else {
					 * notificationModel = null; }
					 */
				}

				if (n.getNotificationType() == 15) {

					notificationModel.setNotification_type("story_move_to_collection");
					Follow follow = followDao.getFollow(userId, n.getSenderId());
					if (follow != null) {
						sender.put("followed_by_current_user", true);
					} else {
						sender.put("followed_by_current_user", false);
					}
					List<String> delArr = new ArrayList<String>();
					contentModel.setSender(sender);
					CollectionStory cs = collectionStoryDao.getCollectionStoryByStoryId(n.getObjectId());
					Story s = storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
					if (n.getObjectType() == 1) {
						if (cs != null && s != null) {
							Collection c = cs.getCollection();
							StoryIntro storyIntro = new StoryIntro();
							storyIntro.setId((Long) s.getId());
							storyIntro.setTitle(s.getTitle());
							storyIntro.setCollectionId(Long.valueOf(1L));
							storyIntro.setCover_media(JSONObject.fromObject(s.getCover_page()));
							storyIntro.setSummary(s.getSummary());
							if (Strings.isNullOrEmpty(s.getTitle())) {
								delArr.add("title");
							}

							if (Strings.isNullOrEmpty(s.getSummary())) {
								delArr.add("summary");
							}

							JsonConfig config = new JsonConfig();
							config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
							config.setIgnoreDefaultExcludes(false);
							config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							if (storyIntro.getCover_media() != null) {
								contentModel.setStory(JSONObject.fromObject(storyIntro, config));
							}

							JSONObject collectionJson = getCollectionInfo(c, userId);
							contentModel.setCollection(collectionJson);
						} else {
							notificationModel = null;
						}
					}

					contentJson = JSONObject.fromObject(contentModel);
					contentJson.remove("comment");
					/*
					 * if (conf.isNew_follower_push()) {} else {
					 * notificationModel = null; }
					 */

				}

				if (n.getNotificationType() == 16) {

					notificationModel.setNotification_type("recommended_my_story_slide_push");

					contentModel.setSender(sender);
					StoryIntro storyIntro = new StoryIntro();
					List<String> delArr = new ArrayList<String>();
					JsonConfig config = new JsonConfig();
					if (n.getObjectType() == 1) {
						Story story = this.storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
						if (story != null) {
							storyIntro.setId((Long) story.getId());
							storyIntro.setTitle(story.getTitle());
							storyIntro.setCollectionId(Long.valueOf(1L));
							storyIntro.setCover_media(JSONObject.fromObject(story.getCover_page()));
							storyIntro.setSummary(story.getSummary());
							if (Strings.isNullOrEmpty(story.getTitle())) {
								delArr.add("title");
							}

							if (Strings.isNullOrEmpty(story.getSummary())) {
								delArr.add("summary");
							}

							config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
							config.setIgnoreDefaultExcludes(false);
							config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
							JSONObject sJson = JSONObject.fromObject(storyIntro, config);
							contentModel.setStory(sJson);
							contentJson = JSONObject.fromObject(contentModel);
							contentJson.remove("collection");
							contentJson.remove("comment");
							contentJson.remove("sender");
						} else {
							notificationModel = null;
						}
					}

					/*
					 * if (conf.isRecommended_my_story_push()) {} else {
					 * notificationModel = null; }
					 */

				}

				if (notificationModel != null) {
					notificationModel.setContent(contentJson);
					notificationModelList.add(notificationModel);
				}
			}
		}

		log.debug("*** notification list ***" + JSONArray.fromObject(notificationModelList));
		return notificationModelList;
	}

	public CommentModel getCommentModel(Comment comment) {
		CommentModel commentModel = new CommentModel();
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
		if (!Strings.isNullOrEmpty(comment.getComment_image())) {
			commentModel.setComment_image(JSONObject.fromObject(comment.getComment_image()));
		} else {
			commentModel.setComment_image(null);
		}

		if (comment.getTarget_user_id() != null) {
			User targetUser = (User) this.userDao.get(comment.getTarget_user_id());
			JSONObject targetUserJson = new JSONObject();
			targetUserJson.put("id", targetUser.getId());
			targetUserJson.put("username", targetUser.getUsername());
			commentModel.setTarget_user(targetUserJson);
		} else {
			JSONObject json = JSONObject.fromObject("{}");
			commentModel.setTarget_user(json);
		}

		return commentModel;
	}

	public Response notificationInfo(Long loginUserid, JSONObject json) {
		String client_id = json.getString("client_id");
		String device_type = json.getString("device_type");
		String device_token = null;
		if (json.containsKey("device_token")) {
			device_token = json.getString("device_token");
		}
		JSONObject returnJson = new JSONObject();

		if ((!Strings.isNullOrEmpty(client_id)) && (!Strings.isNullOrEmpty(device_type))
				&& (!Strings.isNullOrEmpty(device_token))) {
			PushNotification pushNotification = new PushNotification();
			if (loginUserid != null && loginUserid > 0) {
				pushNotificationDao.delPushNotificationByUserId(loginUserid);
				PushNotification pn = pushNotificationDao.getPushNotificationByDeviceToken(device_token);
				if (pn != null) {
					pn.setClientId(client_id);
					pn.setDeviceToken(device_token);
					pn.setUserId(loginUserid);
					pn.setDeviceType(device_type);
					pushNotificationDao.update(pn);
				} else {
					pushNotification.setUserId(loginUserid);
					pushNotification.setClientId(client_id);
					pushNotification.setDeviceType(device_type);
					if (json.containsKey("device_token")) {
						pushNotification.setDeviceToken(device_token);
					}

					this.pushNotificationDao.save(pushNotification);
				}

			} else {
				pushNotification.setUserId(0l);
				pushNotification.setClientId(client_id);
				pushNotification.setDeviceType(device_type);
				if (json.containsKey("device_token")) {
					pushNotification.setDeviceToken(device_token);
				}

				this.pushNotificationDao.save(pushNotification);
			}
			/*
			 * if (pn != null) { //this.pushNotificationDao.delete((Long)
			 * pn.getId()); if(loginUserid != null && loginUserid > 0){
			 * pn.setUserId(loginUserid); }else{ pn.setUserId(0l); }
			 * 
			 * //pushNotification.setClientId(client_id);
			 * if(json.containsKey("device_token")){
			 * pn.setDeviceToken(device_token); }
			 * 
			 * this.pushNotificationDao.update(pn); } else { if(loginUserid !=
			 * null && loginUserid > 0){
			 * pushNotification.setUserId(loginUserid); }else{
			 * pushNotification.setUserId(0l); }
			 * pushNotification.setClientId(client_id);
			 * pushNotification.setDeviceType(device_type);
			 * if(json.containsKey("device_token")){
			 * pushNotification.setDeviceToken(device_token); }
			 * 
			 * this.pushNotificationDao.save(pushNotification); }
			 */
			returnJson.put("status", "success");
			return Response.status(Response.Status.CREATED).entity(returnJson).build();
		}
		returnJson.put("status", "invalid_request");
		returnJson.put("code", Integer.valueOf(10010));
		returnJson.put("error_message", "Invalid payload parameters");
		return Response.status(Response.Status.BAD_REQUEST).entity(returnJson).build();
	}

	public Response deleteNotificationInfo(Long loginUserid, JSONObject json) {
		String client_id = json.getString("client_id");
		String device_type = json.getString("device_type");
		JSONObject returnJson = new JSONObject();
		PushNotification pn = this.pushNotificationDao.getPushNotificationByClientidAndDevice(client_id, device_type);
		if (pn != null) {
			this.pushNotificationDao.delete((Long) pn.getId());
			returnJson.put("status", "success");
			return Response.status(Response.Status.CREATED).entity(returnJson).build();
		}
		returnJson.put("status", "invalid_request");
		returnJson.put("code", Integer.valueOf(10010));
		returnJson.put("error_message", "Invalid payload parameters");
		return Response.status(Response.Status.BAD_REQUEST).entity(returnJson).build();
	}

	@Override
	public List<NotificationModel> getNotificationsByCollection(Long userId, HttpServletRequest request) {

		log.debug("*** start get notifications ***");
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		int count = 20;
		List<Notification> nList = null;
		if (Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr) && Strings.isNullOrEmpty(maxIdStr)) {
			nList = this.notificationDao.getNotificationsCollection(userId, count);
		} else if (!Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr)
				&& Strings.isNullOrEmpty(maxIdStr)) {
			count = Integer.parseInt(countStr);
			nList = this.notificationDao.getNotificationsCollection(userId, count);
		} else if (Strings.isNullOrEmpty(countStr) && !Strings.isNullOrEmpty(sinceIdStr)
				&& Strings.isNullOrEmpty(maxIdStr)) {
			nList = this.notificationDao.getNotificationsByPageCollection(userId, count, Long.parseLong(sinceIdStr), 1);
		} else if (!Strings.isNullOrEmpty(countStr) && !Strings.isNullOrEmpty(sinceIdStr)
				&& Strings.isNullOrEmpty(maxIdStr)) {
			count = Integer.parseInt(countStr);
			nList = this.notificationDao.getNotificationsByPageCollection(userId, count, Long.parseLong(sinceIdStr), 1);
		} else if (Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr)
				&& !Strings.isNullOrEmpty(maxIdStr)) {
			nList = this.notificationDao.getNotificationsByPageCollection(userId, count, Long.parseLong(maxIdStr), 2);
		} else if (!Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr)
				&& !Strings.isNullOrEmpty(maxIdStr)) {
			count = Integer.parseInt(countStr);
			nList = this.notificationDao.getNotificationsByPageCollection(userId, count, Long.parseLong(maxIdStr), 2);
		}
		List<NotificationModel> notificationModelList = new ArrayList<NotificationModel>();
		// Configuration conf = this.configurationDao.getConfByUserId(userId);
		if ((nList != null) && (nList.size() > 0)) {
			this.notificationDao.updateNotificationByLoginUserid(userId);
			for (Notification n : nList) {
				NotificationModel notificationModel = new NotificationModel();
				notificationModel.setId((Long) n.getId());
				notificationModel.setCreated_at(n.getCreate_at());
				notificationModel.setRecipient_id(n.getRecipientId());
				notificationModel.setRead_already(n.getRead_already());
				ContentModel contentModel = new ContentModel();
				JSONObject contentJson = new JSONObject();
				User user = (User) this.userDao.get(n.getSenderId());
				JSONObject sender = new JSONObject();
				sender.put("id", user.getId());
				sender.put("username", user.getUsername());
				sender.put("introduction", user.getIntroduction());
				if (!Strings.isNullOrEmpty(user.getAvatarImage()))
					sender.put("avatar_image", JSONObject.fromObject(user.getAvatarImage()));
				if (n.getNotificationType() == 9) {

					notificationModel.setNotification_type("new_story_from_collection_push");
					Follow follow = followDao.getFollow(userId, n.getSenderId());
					if (follow != null) {
						sender.put("followed_by_current_user", true);
					} else {
						sender.put("followed_by_current_user", false);
					}
					List<String> delArr = new ArrayList<String>();
					contentModel.setSender(sender);
					CollectionStory cs = collectionStoryDao.getCollectionStoryByStoryId(n.getObjectId());
					Story s = storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
					String audit = "";
					if (n.getObjectType() == 1) {
						if (cs != null && s != null) {
							Collection c = cs.getCollection();
							StoryIntro storyIntro = new StoryIntro();
							storyIntro.setId((Long) s.getId());
							storyIntro.setTitle(s.getTitle());
							storyIntro.setCollectionId(Long.valueOf(1L));
							storyIntro.setCover_media(JSONObject.fromObject(s.getCover_page()));
							storyIntro.setSummary(s.getSummary());
							if (Strings.isNullOrEmpty(s.getTitle())) {
								delArr.add("title");
							}

							if (Strings.isNullOrEmpty(s.getSummary())) {
								delArr.add("summary");
							}

							JsonConfig config = new JsonConfig();
							config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
							config.setIgnoreDefaultExcludes(false);
							config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							if (storyIntro.getCover_media() != null) {
								contentModel.setStory(JSONObject.fromObject(storyIntro, config));
							}

							JSONObject collectionJson = getCollectionInfo(c, userId);
							contentModel.setCollection(collectionJson);
						} else {
							notificationModel = null;
						}
					}

					contentJson = JSONObject.fromObject(contentModel);
					contentJson.remove("comment");
					if (!Strings.isNullOrEmpty(audit)) {
						contentJson.put("audit", audit);
					}
					/*
					 * if (conf.isNew_follower_push()) {} else {
					 * notificationModel = null; }
					 */
				}

				if (n.getNotificationType() == 10) {

					notificationModel.setNotification_type("delete_story_from_collection_push");
					Follow follow = followDao.getFollow(userId, n.getSenderId());
					if (follow != null) {
						sender.put("followed_by_current_user", true);
					} else {
						sender.put("followed_by_current_user", false);
					}
					List<String> delArr = new ArrayList<String>();
					contentModel.setSender(sender);
					Story s = storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
					if (n.getObjectType() == 1) {
						if (s != null) {

							StoryIntro storyIntro = new StoryIntro();
							storyIntro.setId((Long) s.getId());
							storyIntro.setTitle(s.getTitle());
							storyIntro.setCollectionId(Long.valueOf(1L));
							storyIntro.setCover_media(JSONObject.fromObject(s.getCover_page()));
							storyIntro.setSummary(s.getSummary());
							if (Strings.isNullOrEmpty(s.getTitle())) {
								delArr.add("title");
							}

							if (Strings.isNullOrEmpty(s.getSummary())) {
								delArr.add("summary");
							}

							JsonConfig config = new JsonConfig();
							config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
							config.setIgnoreDefaultExcludes(false);
							config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							if (storyIntro.getCover_media() != null) {
								contentModel.setStory(JSONObject.fromObject(storyIntro, config));
							}

						} else {
							notificationModel = null;
						}
					}

					contentJson = JSONObject.fromObject(contentModel);
					contentJson.remove("comment");
					contentJson.remove("collection");
					/*
					 * if (conf.isNew_follower_push()) {} else {
					 * notificationModel = null; }
					 */
				}

				if (n.getNotificationType() == 11) {

					notificationModel.setNotification_type("new_story_from_collection_review_push");
					Follow follow = followDao.getFollow(userId, n.getSenderId());
					if (follow != null) {
						sender.put("followed_by_current_user", true);
					} else {
						sender.put("followed_by_current_user", false);
					}
					List<String> delArr = new ArrayList<String>();
					contentModel.setSender(sender);
					CollectionStory cs = collectionStoryDao.getCollectionStoryByStoryId(n.getObjectId());
					Story s = storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
					String audit = "";
					if (n.getObjectType() == 1) {
						if (cs != null && s != null) {
							Collection c = cs.getCollection();
							StoryIntro storyIntro = new StoryIntro();
							storyIntro.setId((Long) s.getId());
							storyIntro.setTitle(s.getTitle());
							storyIntro.setCollectionId(Long.valueOf(1L));
							storyIntro.setCover_media(JSONObject.fromObject(s.getCover_page()));
							storyIntro.setSummary(s.getSummary());
							if (Strings.isNullOrEmpty(s.getTitle())) {
								delArr.add("title");
							}

							if (Strings.isNullOrEmpty(s.getSummary())) {
								delArr.add("summary");
							}

							JsonConfig config = new JsonConfig();
							config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
							config.setIgnoreDefaultExcludes(false);
							config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							if (storyIntro.getCover_media() != null) {
								contentModel.setStory(JSONObject.fromObject(storyIntro, config));
							}

							JSONObject collectionJson = getCollectionInfo(c, userId);
							contentModel.setCollection(collectionJson);
						} else {
							notificationModel = null;
						}
					}

					contentJson = JSONObject.fromObject(contentModel);
					contentJson.remove("comment");
					if (!Strings.isNullOrEmpty(audit)) {
						contentJson.put("audit", audit);
					}
					/*
					 * if (conf.isNew_follower_push()) {} else {
					 * notificationModel = null; }
					 */
				}

				if (n.getNotificationType() == 12) {

					notificationModel.setNotification_type("collection_review_agree_push");
					Follow follow = followDao.getFollow(userId, n.getSenderId());
					if (follow != null) {
						sender.put("followed_by_current_user", true);
					} else {
						sender.put("followed_by_current_user", false);
					}
					List<String> delArr = new ArrayList<String>();
					contentModel.setSender(sender);
					CollectionStory cs = collectionStoryDao.getCollectionStoryByStoryId(n.getObjectId());
					Story s = storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
					String audit = "";
					if (n.getObjectType() == 1) {
						if (cs != null && s != null) {
							StoryIntro storyIntro = new StoryIntro();
							storyIntro.setId((Long) s.getId());
							storyIntro.setTitle(s.getTitle());
							storyIntro.setCollectionId(Long.valueOf(1L));
							storyIntro.setCover_media(JSONObject.fromObject(s.getCover_page()));
							storyIntro.setSummary(s.getSummary());
							if (Strings.isNullOrEmpty(s.getTitle())) {
								delArr.add("title");
							}

							if (Strings.isNullOrEmpty(s.getSummary())) {
								delArr.add("summary");
							}

							JsonConfig config = new JsonConfig();
							config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
							config.setIgnoreDefaultExcludes(false);
							config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							if (storyIntro.getCover_media() != null) {
								contentModel.setStory(JSONObject.fromObject(storyIntro, config));
							}

						} else {
							notificationModel = null;
						}
					}

					contentJson = JSONObject.fromObject(contentModel);
					contentJson.remove("collection");
					contentJson.remove("comment");
					if (!Strings.isNullOrEmpty(audit)) {
						contentJson.put("audit", audit);
					}
					/*
					 * if (conf.isNew_follower_push()) {} else {
					 * notificationModel = null; }
					 */
				}

				if (n.getNotificationType() == 13) {

					notificationModel.setNotification_type("collection_review_reject_push");
					Follow follow = followDao.getFollow(userId, n.getSenderId());
					if (follow != null) {
						sender.put("followed_by_current_user", true);
					} else {
						sender.put("followed_by_current_user", false);
					}
					List<String> delArr = new ArrayList<String>();
					contentModel.setSender(sender);
					CollectionStory cs = collectionStoryDao.getCollectionStoryByStoryId(n.getObjectId());
					Story s = storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
					String audit = "";
					if (n.getObjectType() == 1) {
						if (cs != null && s != null) {
							Collection c = cs.getCollection();
							StoryIntro storyIntro = new StoryIntro();
							storyIntro.setId((Long) s.getId());
							storyIntro.setTitle(s.getTitle());
							storyIntro.setCollectionId(Long.valueOf(1L));
							storyIntro.setCover_media(JSONObject.fromObject(s.getCover_page()));
							storyIntro.setSummary(s.getSummary());
							if (Strings.isNullOrEmpty(s.getTitle())) {
								delArr.add("title");
							}

							if (Strings.isNullOrEmpty(s.getSummary())) {
								delArr.add("summary");
							}

							JsonConfig config = new JsonConfig();
							config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
							config.setIgnoreDefaultExcludes(false);
							config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							if (storyIntro.getCover_media() != null) {
								contentModel.setStory(JSONObject.fromObject(storyIntro, config));
							}

							CollectionNotification cn = new CollectionNotification();
							cn.setId(c.getId());
							cn.setCollection_name(c.getCollectionName());
							JSONObject collectionJson = JSONObject.fromObject(cn);
							contentModel.setCollection(collectionJson);
						} else {
							notificationModel = null;
						}
					}

					contentJson = JSONObject.fromObject(contentModel);
					contentJson.remove("collection");
					contentJson.remove("comment");
					if (!Strings.isNullOrEmpty(audit)) {
						contentJson.put("audit", audit);
					}
					/*
					 * if (conf.isNew_follower_push()) {} else {
					 * notificationModel = null; }
					 */
				}

				if (notificationModel != null) {
					notificationModel.setContent(contentJson);
					notificationModelList.add(notificationModel);
				}
			}
		}

		log.debug("*** notification list ***" + JSONArray.fromObject(notificationModelList));
		return notificationModelList;

	}

	public JSONObject getCollectionInfo(Collection collection, Long loginUserid) {

		CollectionNotification ci = new CollectionNotification();
		ci.setId((Long) collection.getId());
		ci.setCollection_name(collection.getCollectionName());
		ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
		ci.setInfo(collection.getInfo());
		User author = collection.getUser();// userDao.get(collection.getAuthorId());
		JSONObject json = new JSONObject();
		json.put("id", author.getId());
		json.put("username", author.getUsername());
		ci.setAuthor(json);
		JsonConfig configs = new JsonConfig();
		List<String> delArray = new ArrayList<String>();

		UserCollection uc = userCollectionDao.getUserCollectionByCollectionId(collection.getId(), loginUserid);
		if (uc != null) {
			ci.setIs_followed_by_current_user(true);
		} else {
			ci.setIs_followed_by_current_user(false);
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

		return collectionJson;
	}

	@Override
	public List<NotificationModel> friend_dynamics(Long loginUserid, HttpServletRequest request) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		int count = 20;
		List<Notification> nList = null;
		List<Follow> follows = followDao.getFollowingsByUserId(loginUserid);
		String userids = "";
		List<NotificationModel> notificationModelList = new ArrayList<NotificationModel>();
		if (follows != null && follows.size() > 0) {
			for (Follow f : follows) {
				userids += f.getPk().getFollower().getId() + ",";
			}
			userids = userids.substring(0, userids.length() - 1);
			if (Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr)
					&& Strings.isNullOrEmpty(maxIdStr)) {
				nList = this.notificationDao.getNotificationsFollowers(userids, count);
			} else if (!Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr)
					&& Strings.isNullOrEmpty(maxIdStr)) {
				count = Integer.parseInt(countStr);
				nList = this.notificationDao.getNotificationsFollowers(userids, count);
			} else if (Strings.isNullOrEmpty(countStr) && !Strings.isNullOrEmpty(sinceIdStr)
					&& Strings.isNullOrEmpty(maxIdStr)) {
				nList = this.notificationDao.getNotificationsFollowers(userids, count, Long.parseLong(sinceIdStr), 1);
			} else if (!Strings.isNullOrEmpty(countStr) && !Strings.isNullOrEmpty(sinceIdStr)
					&& Strings.isNullOrEmpty(maxIdStr)) {
				count = Integer.parseInt(countStr);
				nList = this.notificationDao.getNotificationsFollowers(userids, count, Long.parseLong(sinceIdStr), 1);
			} else if (Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr)
					&& !Strings.isNullOrEmpty(maxIdStr)) {
				nList = this.notificationDao.getNotificationsFollowers(userids, count, Long.parseLong(maxIdStr), 2);
			} else if (!Strings.isNullOrEmpty(countStr) && Strings.isNullOrEmpty(sinceIdStr)
					&& !Strings.isNullOrEmpty(maxIdStr)) {
				count = Integer.parseInt(countStr);
				nList = this.notificationDao.getNotificationsFollowers(userids, count, Long.parseLong(maxIdStr), 2);
			}

			// Configuration conf =
			// this.configurationDao.getConfByUserId(userId);
			if ((nList != null) && (nList.size() > 0)) {
				for (Notification n : nList) {
					NotificationModel notificationModel = new NotificationModel();
					notificationModel.setId((Long) n.getId());
					notificationModel.setCreated_at(n.getCreate_at());
					notificationModel.setRecipient_id(n.getRecipientId());
					notificationModel.setRead_already(n.getRead_already());
					ContentModel contentModel = new ContentModel();
					JSONObject contentJson = new JSONObject();
					User user = (User) this.userDao.get(n.getSenderId());
					JSONObject sender = new JSONObject();
					sender.put("id", user.getId());
					sender.put("username", user.getUsername());
					sender.put("introduction", user.getIntroduction());
					if (!Strings.isNullOrEmpty(user.getAvatarImage()))
						sender.put("avatar_image", JSONObject.fromObject(user.getAvatarImage()));

					if (n.getNotificationType() == 3) {

						notificationModel.setNotification_type("reposted_my_story_push");
						contentModel.setSender(sender);
						StoryIntroCollection storyIntro = new StoryIntroCollection();
						List<String> delArr = new ArrayList<String>();
						Story story = null;
						if (n.getObjectType() == 1) {
							story = this.storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
							if (story != null) {
								storyIntro.setId((Long) story.getId());
								storyIntro.setTitle(story.getTitle());
								Set<Collection> cSet = story.getCollections();
								if (cSet != null && cSet.size() > 0) {
									List<JSONObject> collectionListJson = new ArrayList<JSONObject>();
									if (cSet != null && cSet.size() > 0) {
										Iterator<Collection> iter = cSet.iterator();
										while (iter.hasNext()) {
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
											if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
												author.put("avatar_image", JSONObject.fromObject(u.getAvatarImage()));
											}
											ci.setAuthor(author);
											JsonConfig configs = new JsonConfig();
											List<String> delArray = new ArrayList<String>();

											int follow_collection_count = userCollectionDao
													.getCollectionByCount(collection.getId());
											ci.setFollowers_count(follow_collection_count);
											/*
											 * Set<User> uSet =
											 * collection.getUsers(); if(uSet !=
											 * null && uSet.size() > 0){
											 * ci.setFollowers_count(uSet.size()
											 * ); }else{
											 * ci.setFollowers_count(0); }
											 */

											JSONObject collectionJson = null;
											if ((delArray != null) && (delArray.size() > 0)) {
												configs.setExcludes(
														(String[]) delArray.toArray(new String[delArray.size()]));
												configs.setIgnoreDefaultExcludes(false);
												configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

												collectionJson = JSONObject.fromObject(ci, configs);
											} else {
												collectionJson = JSONObject.fromObject(ci);
											}

											collectionListJson.add(collectionJson);
										}

										storyIntro.setCollection(collectionListJson);
									}
								} else {
									delArr.add("collection");
								}
								if (!Strings.isNullOrEmpty(story.getCover_page()))
									storyIntro.setCover_media(JSONObject.fromObject(story.getCover_page()));
								else {
									storyIntro.setCover_media(null);
								}
								storyIntro.setSummary(story.getSummary());
								if (Strings.isNullOrEmpty(story.getTitle())) {
									delArr.add("title");
								}

								if (Strings.isNullOrEmpty(story.getSummary()))
									delArr.add("summary");
							} else {
								notificationModel = null;
							}

						}

						if (story != null) {
							JsonConfig config = new JsonConfig();
							config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
							config.setIgnoreDefaultExcludes(false);
							config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

							if (storyIntro.getCover_media() != null) {
								contentModel.setStory(JSONObject.fromObject(storyIntro, config));
							}
							contentJson = JSONObject.fromObject(contentModel);
							contentJson.remove("comment");
							contentJson.remove("collection");
						}

						/*
						 * if (conf.isReposted_my_story_push()) {} else {
						 * notificationModel = null; }
						 */
					} else if (n.getNotificationType() == 4) {

						notificationModel.setNotification_type("new_story_from_following_push");
						contentModel.setSender(sender);
						StoryIntroCollection storyIntro = new StoryIntroCollection();
						List<String> delArr = new ArrayList<String>();
						if (n.getObjectType() == 1) {
							Story story = this.storyDao.getStoryByIdAndStatus(n.getObjectId(), "publish");
							if (story != null) {
								storyIntro.setId((Long) story.getId());
								storyIntro.setTitle(story.getTitle());
								Set<Collection> cSet = story.getCollections();
								if (cSet != null && cSet.size() > 0) {
									List<JSONObject> collectionListJson = new ArrayList<JSONObject>();
									Iterator<Collection> iter = cSet.iterator();
									while (iter.hasNext()) {
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
										if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
											author.put("avatar_image", JSONObject.fromObject(u.getAvatarImage()));
										}
										ci.setAuthor(author);
										JsonConfig configs = new JsonConfig();
										List<String> delArray = new ArrayList<String>();

										int follow_collection_count = userCollectionDao
												.getCollectionByCount(collection.getId());
										ci.setFollowers_count(follow_collection_count);
										/*
										 * Set<User> uSet =
										 * collection.getUsers(); if(uSet !=
										 * null && uSet.size() > 0){
										 * ci.setFollowers_count(uSet.size());
										 * }else{ ci.setFollowers_count(0); }
										 */

										JSONObject collectionJson = null;
										if ((delArray != null) && (delArray.size() > 0)) {
											configs.setExcludes(
													(String[]) delArray.toArray(new String[delArray.size()]));
											configs.setIgnoreDefaultExcludes(false);
											configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

											collectionJson = JSONObject.fromObject(ci, configs);
										} else {
											collectionJson = JSONObject.fromObject(ci);
										}

										collectionListJson.add(collectionJson);
									}

									storyIntro.setCollection(collectionListJson);

								} else {
									delArr.add("collection");
								}

								if (!Strings.isNullOrEmpty(story.getCover_page()))
									storyIntro.setCover_media(JSONObject.fromObject(story.getCover_page()));
								else {
									storyIntro.setCover_media(null);
								}

								storyIntro.setSummary(story.getSummary());

								if (Strings.isNullOrEmpty(story.getTitle())) {
									delArr.add("title");
								}

								if (Strings.isNullOrEmpty(story.getSummary())) {
									delArr.add("summary");
								}

								JsonConfig config = new JsonConfig();
								config.setExcludes((String[]) delArr.toArray(new String[delArr.size()]));
								config.setIgnoreDefaultExcludes(false);
								config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

								if (storyIntro.getCover_media() != null) {
									contentModel.setStory(JSONObject.fromObject(storyIntro, config));
								}
								contentJson = JSONObject.fromObject(contentModel);
								contentJson.remove("comment");
								contentJson.remove("collection");
							} else {
								notificationModel = null;
							}
						}

						/*
						 * if (conf.isNew_story_from_following_push()) {} else {
						 * notificationModel = null; }
						 */
					} else if (n.getNotificationType() == 14) {

						notificationModel.setNotification_type("create_collection_push");
						contentModel.setSender(sender);
						Collection c = collectionDao.getCollectionById(n.getObjectId());
						if (c != null) {
							JSONObject cJ = getCollectionJSON(c);
							contentModel.setCollection(cJ);
							contentJson = JSONObject.fromObject(contentModel);
							contentJson.remove("story");
							contentJson.remove("comment");
						} else {
							notificationModel = null;
						}

					}

					if (notificationModel != null) {
						notificationModel.setContent(contentJson);
						notificationModelList.add(notificationModel);
					}
				}
			}
		}
		return notificationModelList;

	}

	@Override
	public Response friend_dynamics_lastid(Long loginUserid, HttpServletRequest request) {
		List<Follow> follows = followDao.getFollowingsByUserId(loginUserid);
		String userids = "";
		JSONObject json = new JSONObject();
		if (follows != null && follows.size() > 0) {
			for (Follow f : follows) {
				userids += f.getPk().getFollower().getId() + ",";
			}
			userids = userids.substring(0, userids.length() - 1);
			Notification n = notificationDao.getNotificationsFollowers(userids);
			if (n != null) {

				json.put("notification_id", n.getId());
				return Response.status(Response.Status.OK).entity(json).build();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public JSONObject getCollectionJSON(Collection collection) {
		CollectionIntro ci = new CollectionIntro();
		ci.setId((Long) collection.getId());
		ci.setCollection_name(collection.getCollectionName());
		ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
		ci.setInfo(collection.getInfo());
		User u = collection.getUser();// userDao.get(collection.getAuthorId());
		JSONObject author = new JSONObject();
		author.put("id", u.getId());
		author.put("username", u.getUsername());
		if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
			author.put("avatar_image", u.getAvatarImage());
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
		return collectionJ;
	}

	@Override
	public Response collection_dynamics_lastid(Long loginUserid, HttpServletRequest request) {
		Notification n = notificationDao.getNotificationsCollectionLastId(loginUserid, 1);
		JSONObject json = new JSONObject();
		if (n != null) {
			json.put("notification_id", n.getId());
			return Response.status(Response.Status.OK).entity(json).build();
		} else {
			json.put("notification_id", "");
			return Response.status(Response.Status.OK).entity(json).build();
		}
	}
}
