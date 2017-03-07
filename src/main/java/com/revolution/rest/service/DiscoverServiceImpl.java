package com.revolution.rest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;
import com.revolution.rest.dao.CollectionDao;
import com.revolution.rest.dao.CollectionStoryDao;
import com.revolution.rest.dao.CommentDao;
import com.revolution.rest.dao.FeatureCollectionDao;
import com.revolution.rest.dao.FeatureUserDao;
import com.revolution.rest.dao.FollowDao;
import com.revolution.rest.dao.InterestDao;
import com.revolution.rest.dao.LikesDao;
import com.revolution.rest.dao.RepublishDao;
import com.revolution.rest.dao.SlideDao;
import com.revolution.rest.dao.StoryDao;
import com.revolution.rest.dao.UserCollectionDao;
import com.revolution.rest.dao.UserDao;
import com.revolution.rest.model.Collection;
import com.revolution.rest.model.FeatureCollection;
import com.revolution.rest.model.FeatureUser;
import com.revolution.rest.model.Follow;
import com.revolution.rest.model.Interest;
import com.revolution.rest.model.Likes;
import com.revolution.rest.model.PublisherInfo;
import com.revolution.rest.model.Republish;
import com.revolution.rest.model.Slide;
import com.revolution.rest.model.Story;
import com.revolution.rest.model.StoryElement;
import com.revolution.rest.model.User;
import com.revolution.rest.model.UserCollection;
import com.revolution.rest.service.model.CollectionDiscover;
import com.revolution.rest.service.model.CollectionIntro;
import com.revolution.rest.service.model.CollectionIntros;
import com.revolution.rest.service.model.CollectionModel;
import com.revolution.rest.service.model.CoverMedia;
import com.revolution.rest.service.model.IframeCover;
import com.revolution.rest.service.model.ImageCover;
import com.revolution.rest.service.model.LinkModel;
import com.revolution.rest.service.model.LinkModels;
import com.revolution.rest.service.model.LocationModel;
import com.revolution.rest.service.model.PublisherInfoModel;
import com.revolution.rest.service.model.SlideModel;
import com.revolution.rest.service.model.StoryHome;
import com.revolution.rest.service.model.StoryIntro;
import com.revolution.rest.service.model.StoryModel;
import com.revolution.rest.service.model.TextCover;
import com.revolution.rest.service.model.UserFeatured;
import com.revolution.rest.service.model.UserModel;
import com.revolution.rest.service.model.UserParentModel;
import com.revolution.rest.service.model.UserPublisherModel;
import com.revolution.rest.service.model.VideoCover;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@Transactional
public class DiscoverServiceImpl implements DiscoverService {
	private static final Log log = LogFactory.getLog(DiscoverServiceImpl.class);

	@Autowired
	private StoryDao storyDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private CollectionStoryDao collectionStoryDao;

	@Autowired
	private CommentDao commentDao;

	@Autowired
	private LikesDao likesDao;

	@Autowired
	private RepublishDao republishDao;

	@Autowired
	private FollowDao followDao;

	@Autowired
	private FeatureUserDao featureUserDao;

	@Autowired
	private FeatureCollectionDao featureCollectionDao;

	@Autowired
	private UserCollectionDao userCollectionDao;

	@Autowired
	private SlideDao slideDao;

	@Autowired
	private InterestDao interestDao;

	public JSONObject getDiscover(Long loginUserid, HttpServletRequest request, String appVersion) {
		JSONObject discover = new JSONObject();
		JSONObject interJson = null;
		/*List<Slide> slideList = slideDao.getSlideByGroups("discover");
		List<SlideModel> slideModelList = new ArrayList<SlideModel>();
		SlideModel slideModel = null;
		JSONObject storyJson;
		if ((slideList != null) && (slideList.size() > 0)) {
			for (Slide slide : slideList) {
				if (slide.getType().equals("url")) {
					slideModel = new SlideModel();
					slideModel.setId((Long) slide.getId());
					slideModel.setType(slide.getType());
					JSONObject urlJson = new JSONObject();
					urlJson.put("url", slide.getUrl());
					slideModel.setSlide(urlJson);
					if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
						slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
					}

					slideModelList.add(slideModel);
				} else if (slide.getType().equals("story")) {
					Story story = this.storyDao.getStoryByIdAndStatus(slide.getReference_id(), "publish");
					if (story != null) {
						slideModel = new SlideModel();
						slideModel.setId(slide.getId());
						slideModel.setType(slide.getType());
						JSONObject json = getStoryEventByLoginUser(story, loginUserid);

						storyJson = new JSONObject();

						storyJson.put("story", json);
						slideModel.setSlide(storyJson);
						if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
							slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
						}

						slideModelList.add(slideModel);
					}
				} else if (slide.getType().equals("user")) {
					slideModel = new SlideModel();
					slideModel.setId((Long) slide.getId());
					slideModel.setType(slide.getType());
					UserParentModel userModel = getUserModel(slide.getReference_id(), loginUserid);
					JSONObject userJson = new JSONObject();
					userJson.put("user", userModel);
					slideModel.setSlide(userJson);
					if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
						slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
					}

					slideModelList.add(slideModel);
				} else if (slide.getType().equals("collection")) {
					slideModel = new SlideModel();
					slideModel.setId((Long) slide.getId());
					slideModel.setType(slide.getType());
					Collection collection = collectionDao.get(slide.getReference_id());

					CollectionIntro ci = new CollectionIntro();
					ci.setId((Long) collection.getId());
					ci.setCollection_name(collection.getCollectionName());
					ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
					ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
					ci.setInfo(collection.getInfo());
					ci.setCollection_type(collection.getCollection_type());
					User u = collection.getUser();// .getId()userDao.get(collection.getAuthorId());
					JSONObject author = new JSONObject();
					author.put("id", u.getId());
					author.put("username", u.getUsername());
					if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
						author.put("avatar_image", JSONObject.fromObject(u.getAvatarImage()));
					}

					ci.setAuthor(author);
					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					if (!Strings.isNullOrEmpty(collection.getActivity_description())) {
						ci.setActivity_description(collection.getActivity_description());
					} else {
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
					JSONObject collectionJson = new JSONObject();
					collectionJson.put("collection", collectionJ);
					slideModel.setSlide(collectionJson);
					if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
						slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
					}

					slideModelList.add(slideModel);
				}

			}

		}
		discover.put("slides", slideModelList);*/

		List<Interest> interestList = interestDao.getInterestListBySequence();
		List<JSONObject> interestJson = new ArrayList<JSONObject>();
		if (interestList != null && interestList.size() > 0) {
			for (Interest interest : interestList) {
				interJson = new JSONObject();
				interJson.put("id", interest.getId());
				interJson.put("interest_name", interest.getInterest_name());
				if(!Strings.isNullOrEmpty(interest.getDescription())){
					interJson.put("description",interest.getDescription());
				}
				
				interJson.put("cover_image",JSONObject.fromObject(interest.getCover_image()));
				List<Collection> cList = interest.getCollections();
				if(cList != null && cList.size() > 0){
					interJson.put("collections_count", cList.size());
				}else{
					interJson.put("collections_count",0);
				}
				
				List<JSONObject> collectionJson = new ArrayList<JSONObject>();
				int follower_count = 0;
				List<Collection> collectionList = interest.getCollections();
				if (collectionList != null && collectionList.size() > 0) {
					for (Collection c : collectionList) {
						JSONObject json = getCollectionInfo(c);
						follower_count += json.getInt("followers_count");
						collectionJson.add(json);
					}
					interJson.put("collections", collectionJson);
					
				}
				
				interJson.put("followers_count", follower_count);

				interestJson.add(interJson);
			}
		}

		discover.put("interests", interestJson);

		System.out.println("discover--->" + discover);
		return discover;
	}

	public StoryModel getStoryModelByStoryLoginUser(Story story, Long loginUserid) {
		StoryModel storyModel = new StoryModel();
		storyModel.setId((Long) story.getId());
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
		JSONObject content = null;
		if ((seSet != null) && (seSet.size() > 0)) {
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

				}
				storyElements.add(element);
			}
		}

		JsonConfig config = new JsonConfig();
		config.setExcludes(new String[] { "storyinfo", "contents" });
		config.setIgnoreDefaultExcludes(false);
		config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		log.debug("***get Elements *****" + JSONArray.fromObject(story.getElements(), config));
		storyModel.setElements(JSONArray.fromObject(storyElements, config));

		storyModel.setCommnents_enables(story.getComments_enabled());
		if (!Strings.isNullOrEmpty(story.getTinyURL())) {
			storyModel.setUrl(story.getTinyURL());
		}

		storyModel.setView_count(story.getViewTimes());
		storyModel.setTitle(story.getTitle());

		int count = this.commentDao.getCommentCountById((Long) story.getId());
		storyModel.setComment_count(count);
		Likes likes = this.likesDao.getLikeByUserIdAndStoryId(loginUserid, story.getId());
		if (likes != null)
			storyModel.setLiked_by_current_user(true);
		else {
			storyModel.setLiked_by_current_user(false);
		}
		int repostCount = this.republishDao.count((Long) story.getId());
		storyModel.setRepost_count(repostCount);
		Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, (Long) story.getId());
		if (repost != null)
			storyModel.setRepost_by_current_user(true);
		else {
			storyModel.setRepost_by_current_user(false);
		}

		return storyModel;
	}

	public JSONObject getStoryEventByLoginUser(Story story, Long loginUserid) {
		StoryHome storyModel = new StoryHome();
		JSONObject storyJson = new JSONObject();
		if ((story != null) && (story.getStatus().equals("publish"))) {
			storyModel.setId((Long) story.getId());
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
			/*
			 * String theme_color = null; if (user.getTheme_id() != null) {
			 * Theme_color color =
			 * (Theme_color)this.themeColorDao.get(user.getTheme_id());
			 * theme_color = color.getColor(); } authorJson.put("theme_color",
			 * theme_color); if (!Strings.isNullOrEmpty(user.getWebsite()))
			 * authorJson.put("website", user.getWebsite()); else {
			 * authorJson.put("website", null); }
			 */
			/*
			 * if(loginUserid != null && loginUserid > 0){ Republish repost =
			 * this.republishDao .getRepostByUserIdAndStoryId(loginUserid,
			 * story.getId()); if (repost != null)
			 * storyModel.setRepost_by_current_user(true); else {
			 * storyModel.setRepost_by_current_user(false); } }else{
			 * storyModel.setRepost_by_current_user(false); }
			 */

			storyModel.setAuthor(authorJson);
			/*
			 * int count = this.commentDao.getCommentCountById(story.getId());
			 * storyModel.setComment_count(count);
			 */
			Collection collection = this.collectionStoryDao.getCollectionByStoryId(story.getId());
			if (collection != null) {
				CollectionIntros ci = new CollectionIntros();
				ci.setId((Long) collection.getId());
				ci.setCollection_name(collection.getCollectionName());
				// ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
				ci.setInfo(collection.getInfo());
				// Theme_color tc =
				// (Theme_color)this.themeColorDao.get(collection.getTheme_id());
				// ci.setTheme_color(tc.getColor());
				storyModel.setCollection(ci);
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

	public UserParentModel getUserModel(Long userId, Long loginUserid) {
		int repostCount = this.republishDao.userRepostCount(userId);
		int storyCount = this.storyDao.getStoryCount(userId);
		int follower_Count = this.followDao.userFollowedCount(userId);
		int following_count = this.followDao.userFollowCount(userId);
		// Follow loginFollow = this.followDao.getFollow(loginUserid, userId);
		// Follow currentFollow = this.followDao.getFollow(userId, loginUserid);
		Follow loginFollow = null;
		Follow currentFollow = null;
		if (loginUserid != null && loginUserid > 0) {
			loginFollow = this.followDao.getFollow(loginUserid, userId);
			currentFollow = this.followDao.getFollow(userId, loginUserid);
		}

		boolean followed_by_current_user = false;
		boolean is_following_current_user = false;
		if (loginFollow != null) {
			followed_by_current_user = true;
		}

		if (currentFollow != null) {
			is_following_current_user = true;
		}
		JSONObject avatarImageJson = null;
		User user = (User) this.userDao.get(userId);
		if ((user.getAvatarImage() != null) && (!user.getAvatarImage().equals(""))) {
			avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
		}

		JSONObject coverImageJson = null;
		if ((user.getCoverImage() != null) && (!user.getCoverImage().equals(""))) {
			coverImageJson = JSONObject.fromObject(user.getCoverImage());
		}
		String website = null;
		if (!Strings.isNullOrEmpty(user.getWebsite())) {
			website = user.getWebsite();
		}
		UserParentModel userModel = null;
		if ((user.getUser_type().equals("normal")) || (user.getUser_type().equals("admin"))
				|| (user.getUser_type().equals("super_admin")) || (user.getUser_type().equals("vip"))
				|| (user.getUser_type().equals("official"))) {
			userModel = new UserModel((Long) user.getId(), user.getUsername(), user.getEmail(), user.getCreated_time(),
					user.getStatus(), user.getIntroduction(), avatarImageJson, coverImageJson, repostCount, storyCount,
					follower_Count, following_count, website, followed_by_current_user, is_following_current_user,
					user.getUser_type(), user.getGender());
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
					user.getCreated_time(), user.getStatus(), user.getIntroduction(), avatarImageJson, coverImageJson,
					repostCount, storyCount, follower_Count, following_count, website, followed_by_current_user,
					is_following_current_user, user.getUser_type(), publisherList, user.getGender());
		}

		log.debug("****get user**********" + JSONObject.fromObject(userModel));
		return userModel;
	}

	public CollectionModel getCollectionModel(Collection collection) {

		CollectionModel collectionModel = new CollectionModel();
		if (collection != null) {
			collectionModel.setId(collection.getId());
			collectionModel.setCollection_name(collection.getCollectionName());
			if (!Strings.isNullOrEmpty(collection.getCover_image())) {
				collectionModel.setCover_image(JSONObject.fromObject(collection.getCover_image()));
			}
			collectionModel.setInfo(collection.getInfo());
		}
		return collectionModel;
	}

	public List<UserFeatured> getUserFeature() {
		List<FeatureUser> list = this.featureUserDao.getAll();
		// List<FeatureUser> list = this.featureUserDao.getRandomFeatureUser();
		List<UserFeatured> ufList = new ArrayList<UserFeatured>();
		UserFeatured uf = null;
		StoryIntro storyIntro = null;
		User user = null;
		if ((list != null) && (list.size() > 0)) {
			for (FeatureUser fu : list) {
				user = (User) this.userDao.get(fu.getUserId());
				uf = new UserFeatured();
				uf.setId((Long) user.getId());
				uf.setUsername(user.getUsername());
				uf.setIntroduction(user.getIntroduction());
				if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
					JSONObject avatarJson = JSONObject.fromObject(user.getAvatarImage());
					uf.setAvatar_image(avatarJson);
				} else {
					uf.setAvatar_image(null);
				}

				List<Story> storyList = this.storyDao.getStoriesByNow((Long) user.getId(), "publish");
				List<StoryIntro> storyIntroList = new ArrayList<StoryIntro>();
				if ((storyList != null) && (storyList.size() > 0)) {
					for (Story s : storyList) {
						storyIntro = new StoryIntro();
						storyIntro.setId((Long) s.getId());
						storyIntro.setTitle(s.getTitle());
						storyIntro.setCollectionId(Long.valueOf(1L));

						JSONObject jsonObject = JSONObject.fromObject(s.getCover_page());
						log.debug("***story.getCover_page()***" + jsonObject);
						String type = jsonObject.getString("type");
						if (type.equals("text")) {
							TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
							log.debug("****get cover media **********" + JSONObject.fromObject(coverMedia));
							storyIntro.setCover_media(JSONObject.fromObject(coverMedia));
						} else if (type.equals("image")) {
							ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
							storyIntro.setCover_media(JSONObject.fromObject(coverMedia));
						} else if (type.equals("multimedia")) {
							storyIntro.setCover_media(jsonObject);
						}
						storyIntroList.add(storyIntro);
					}
				}
				uf.setStories(storyIntroList);
				ufList.add(uf);
			}
		}
		return ufList;
	}

	@Override
	public List<JSONObject> getRandomFive(Long loginUserid) {
		List<JSONObject> summaryList = new ArrayList<JSONObject>();
		List<Collection> cList = collectionDao.getCollectionByRanTen();
		if (loginUserid != null && loginUserid > 0) {
			if (cList != null && cList.size() > 0) {
				for (Collection c : cList) {
					JSONObject cjson = getCollectionInfo(c);
					// Set<User> uSet = c.getUsers();
					boolean is_followed_by_current_user = false;
					/*
					 * if(uSet != null && uSet.size() > 0){ Iterator<User> iter
					 * = uSet.iterator(); while(iter.hasNext()){ User u =
					 * iter.next(); if(u.getId().equals(loginUserid) &&
					 * u.getId() == loginUserid){ is_followed_by_current_user =
					 * true; break; } } }
					 */
					UserCollection uc = userCollectionDao.getUserCollectionByCollectionId(c.getId(), loginUserid);
					if (uc != null) {
						is_followed_by_current_user = true;
					}
					cjson.put("is_followed_by_current_user", is_followed_by_current_user);
					JSONObject json = new JSONObject();
					json.put("collection", cjson);
					summaryList.add(json);
				}
			}
		} else {
			if (cList != null && cList.size() > 0) {
				for (Collection c : cList) {
					JSONObject cjson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cjson);
					summaryList.add(json);
				}
			}
		}
		return summaryList;
	}

	@Override
	public List<JSONObject> getCollectionByRecommand(Long loginUserid, HttpServletRequest request) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");

		int count = 20;
		List<JSONObject> recommandCollectionList = new ArrayList<JSONObject>();
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<FeatureCollection> fcList = featureCollectionDao.getFeatureCollection(count);
			if (fcList != null && fcList.size() > 0) {
				for (FeatureCollection fc : fcList) {
					Collection c = collectionDao.get(fc.getCollectionId());
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					recommandCollectionList.add(json);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);

			List<FeatureCollection> fcList = featureCollectionDao.getFeatureCollection(count);
			if (fcList != null && fcList.size() > 0) {
				for (FeatureCollection fc : fcList) {
					Collection c = collectionDao.get(fc.getCollectionId());
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					recommandCollectionList.add(json);
				}
			}

		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));

			List<FeatureCollection> fcList = featureCollectionDao.getFeatureCollectionByPage(since_id, count, 1);
			if (fcList != null && fcList.size() > 0) {
				for (FeatureCollection fc : fcList) {
					Collection c = collectionDao.get(fc.getCollectionId());
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					recommandCollectionList.add(json);
				}
			}

		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<FeatureCollection> fcList = featureCollectionDao.getFeatureCollectionByPage(since_id, count, 1);
			if (fcList != null && fcList.size() > 0) {
				for (FeatureCollection fc : fcList) {
					Collection c = collectionDao.get(fc.getCollectionId());
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					recommandCollectionList.add(json);
				}
			}
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<FeatureCollection> fcList = featureCollectionDao.getFeatureCollectionByPage(max_id, count, 2);
			if (fcList != null && fcList.size() > 0) {
				for (FeatureCollection fc : fcList) {
					Collection c = collectionDao.get(fc.getCollectionId());
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					recommandCollectionList.add(json);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<FeatureCollection> fcList = featureCollectionDao.getFeatureCollectionByPage(max_id, count, 2);
			if (fcList != null && fcList.size() > 0) {
				for (FeatureCollection fc : fcList) {
					Collection c = collectionDao.get(fc.getCollectionId());
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					recommandCollectionList.add(json);
				}
			}

		}

		return recommandCollectionList;
	}

	@Override
	public List<JSONObject> getCollectionByHot(Long loginUserid, HttpServletRequest request) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");

		int count = 20;

		List<JSONObject> hotCollectionList = new ArrayList<JSONObject>();

		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Object[]> hotList = userCollectionDao.getCollectionByHot(count);
			if (hotList != null && hotList.size() > 0) {
				for (Object[] o : hotList) {
					Collection c = (Collection) o[1];
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					hotCollectionList.add(json);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);

			List<Object[]> hotList = userCollectionDao.getCollectionByHot(count);
			if (hotList != null && hotList.size() > 0) {
				for (Object[] o : hotList) {
					Collection c = (Collection) o[1];
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					hotCollectionList.add(json);
				}
			}

		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			int num = userCollectionDao.getCollectionByCount(since_id);
			List<Object[]> hotList = userCollectionDao.getCollectionByHot(num, since_id, count, 1);
			if (hotList != null && hotList.size() > 0) {
				for (Object[] o : hotList) {
					Collection c = (Collection) o[1];
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					hotCollectionList.add(json);
				}
			}

		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			int num = userCollectionDao.getCollectionByCount(since_id);
			List<Object[]> hotList = userCollectionDao.getCollectionByHot(num, since_id, count, 1);
			if (hotList != null && hotList.size() > 0) {
				for (Object[] o : hotList) {
					Collection c = (Collection) o[1];
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					hotCollectionList.add(json);
				}
			}
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			int num = userCollectionDao.getCollectionByCount(max_id);
			List<Object[]> hotList = userCollectionDao.getCollectionByHot(num, max_id, count, 2);
			if (hotList != null && hotList.size() > 0) {
				for (Object[] o : hotList) {
					Collection c = (Collection) o[1];
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					hotCollectionList.add(json);
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			int num = userCollectionDao.getCollectionByCount(max_id);
			List<Object[]> hotList = userCollectionDao.getCollectionByHot(num, max_id, count, 2);
			if (hotList != null && hotList.size() > 0) {
				for (Object[] o : hotList) {
					Collection c = (Collection) o[1];
					JSONObject cJson = getCollectionInfo(c);
					JSONObject json = new JSONObject();
					json.put("collection", cJson);
					hotCollectionList.add(json);
				}
			}

		}

		return hotCollectionList;
	}

	public JSONObject getCollectionInfo(Collection c) {
		CollectionDiscover cd = new CollectionDiscover();
		cd.setId(c.getId());
		cd.setCollection_name(c.getCollectionName());
		cd.setCover_image(JSONObject.fromObject(c.getCover_image()));
		cd.setIs_followed_by_current_user(false);
		cd.setView_count(c.getView_count());
		cd.setInfo(c.getInfo());

		User u = c.getUser();// userDao.get(c.getAuthorId());
		JSONObject author = new JSONObject();
		author.put("id", u.getId());
		author.put("username", u.getUsername());
		if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
			author.put("avatar_image", JSONObject.fromObject(u.getAvatarImage()));
		}
		cd.setAuthor(author);

		JsonConfig configs = new JsonConfig();
		List<String> delArray = new ArrayList<String>();

		JSONObject collectionJ = null;
		if ((delArray != null) && (delArray.size() > 0)) {
			configs.setExcludes((String[]) delArray.toArray(new String[delArray.size()]));
			configs.setIgnoreDefaultExcludes(false);
			configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

			collectionJ = JSONObject.fromObject(cd, configs);
		} else {
			collectionJ = JSONObject.fromObject(cd);
		}

		Set<User> uSet = c.getUsers();
		if (uSet != null && uSet.size() > 0) {
			collectionJ.put("followers_count", uSet.size());
		} else {
			collectionJ.put("followers_count", 0);
		}
		
		int count = collectionStoryDao.getStoriesByCount(c.getId());
		collectionJ.put("story_count",count);
		return collectionJ;
	}

	@Override
	public JSONObject getNewDiscover(Long loginUserid, HttpServletRequest request, String version) {
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");

		JSONObject discover = new JSONObject();
		int count = 20;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Slide> slideList = slideDao.getSlideByGroups("discover");
			List<SlideModel> slideModelList = new ArrayList<SlideModel>();
			SlideModel slideModel = null;
			JSONObject storyJson;
			if ((slideList != null) && (slideList.size() > 0)) {
				for (Slide slide : slideList) {
					if (slide.getType().equals("url")) {
						slideModel = new SlideModel();
						slideModel.setId((Long) slide.getId());
						slideModel.setType(slide.getType());
						JSONObject urlJson = new JSONObject();
						urlJson.put("url", slide.getUrl());
						slideModel.setSlide(urlJson);
						if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
							slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
						}

						slideModelList.add(slideModel);
					} else if (slide.getType().equals("story")) {
						Story story = this.storyDao.getStoryByIdAndStatus(slide.getReference_id(), "publish");
						if (story != null) {
							slideModel = new SlideModel();
							slideModel.setId(slide.getId());
							slideModel.setType(slide.getType());
							JSONObject json = getStoryEventByLoginUser(story, loginUserid);

							storyJson = new JSONObject();

							storyJson.put("story", json);
							slideModel.setSlide(storyJson);
							if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
								slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
							}

							slideModelList.add(slideModel);
						}
					} else if (slide.getType().equals("user")) {
						slideModel = new SlideModel();
						slideModel.setId(slide.getId());
						slideModel.setType(slide.getType());
						UserParentModel userModel = getUserModel(slide.getReference_id(), loginUserid);
						JSONObject userJson = new JSONObject();
						userJson.put("user", userModel);
						slideModel.setSlide(userJson);
						if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
							slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
						}

						slideModelList.add(slideModel);
					} else if (slide.getType().equals("collection")) {
						slideModel = new SlideModel();
						slideModel.setId((Long) slide.getId());
						slideModel.setType(slide.getType());
						Collection collection = collectionDao.get(slide.getReference_id());

						CollectionIntro ci = new CollectionIntro();
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
						ci.setInfo(collection.getInfo());
						Set<User> uSet = collection.getUsers();
						if (uSet != null && uSet.size() > 0) {
							ci.setFollowers_count(uSet.size());
						} else {
							ci.setFollowers_count(0);
						}
						User u = collection.getUser();// userDao.get(collection.getAuthorId());
						JSONObject author = new JSONObject();
						author.put("id", u.getId());
						author.put("username", u.getUsername());
						if (!Strings.isNullOrEmpty(u.getAvatarImage())) {
							author.put("avatar_image", JSONObject.fromObject(u.getAvatarImage()));
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
						JSONObject collectionJson = new JSONObject();
						collectionJson.put("collection", collectionJ);
						slideModel.setSlide(collectionJson);
						if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
							slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
						}

						slideModelList.add(slideModel);
					}

				}

			}
			discover.put("slides", slideModelList);
			List<Collection> aList = collectionDao.getCollectionBytype("activity", count);
			List<JSONObject> activityList = new ArrayList<JSONObject>();
			if (aList != null && aList.size() > 0) {
				for (Collection c : aList) {
					JSONObject json = getCollectionInfo(c);
					JSONObject collectionJ = new JSONObject();
					collectionJ.put("collection", json);
					activityList.add(collectionJ);
				}
			}
			discover.put("collection_activity", activityList);
			List<Collection> iList = collectionDao.getCollectionBytype("interest", count);
			List<JSONObject> interestList = new ArrayList<JSONObject>();
			if (iList != null && iList.size() > 0) {
				for (Collection c : iList) {
					JSONObject json = getCollectionInfo(c);
					JSONObject collectionJ = new JSONObject();
					collectionJ.put("collection", json);
					interestList.add(collectionJ);
				}
			}
			discover.put("collection_interest", interestList);
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			List<Slide> slideList = slideDao.getSlideByGroups("discover");
			List<SlideModel> slideModelList = new ArrayList<SlideModel>();
			SlideModel slideModel = null;
			JSONObject storyJson;
			if ((slideList != null) && (slideList.size() > 0)) {
				for (Slide slide : slideList) {
					if (slide.getType().equals("url")) {
						slideModel = new SlideModel();
						slideModel.setId((Long) slide.getId());
						slideModel.setType(slide.getType());
						JSONObject urlJson = new JSONObject();
						urlJson.put("url", slide.getUrl());
						slideModel.setSlide(urlJson);
						if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
							slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
						}

						slideModelList.add(slideModel);
					} else if (slide.getType().equals("story")) {
						Story story = this.storyDao.getStoryByIdAndStatus(slide.getReference_id(), "publish");
						if (story != null) {
							slideModel = new SlideModel();
							slideModel.setId(slide.getId());
							slideModel.setType(slide.getType());
							JSONObject json = getStoryEventByLoginUser(story, loginUserid);

							storyJson = new JSONObject();

							storyJson.put("story", json);
							slideModel.setSlide(storyJson);
							if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
								slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
							}

							slideModelList.add(slideModel);
						}
					} else if (slide.getType().equals("user")) {
						slideModel = new SlideModel();
						slideModel.setId((Long) slide.getId());
						slideModel.setType(slide.getType());
						UserParentModel userModel = getUserModel(slide.getReference_id(), loginUserid);
						JSONObject userJson = new JSONObject();
						userJson.put("user", userModel);
						slideModel.setSlide(userJson);
						if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
							slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
						}

						slideModelList.add(slideModel);
					} else if (slide.getType().equals("collection")) {
						slideModel = new SlideModel();
						slideModel.setId((Long) slide.getId());
						slideModel.setType(slide.getType());
						Collection collection = collectionDao.get(slide.getReference_id());

						CollectionIntro ci = new CollectionIntro();
						ci.setId((Long) collection.getId());
						ci.setCollection_name(collection.getCollectionName());
						ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
						ci.setInfo(collection.getInfo());
						User u = collection.getUser();// userDao.get(collection.getAuthorId());
						JSONObject author = new JSONObject();
						author.put("id", u.getId());
						author.put("username", u.getUsername());
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
						JSONObject collectionJson = new JSONObject();
						collectionJson.put("collection", collectionJ);
						slideModel.setSlide(collectionJson);
						if (!Strings.isNullOrEmpty(slide.getSlide_image())) {
							slideModel.setSlide_image(JSONObject.fromObject(slide.getSlide_image()));
						}

						slideModelList.add(slideModel);
					}

				}

			}
			discover.put("slides", slideModelList);
			List<Collection> aList = collectionDao.getCollectionBytype("activity", count);
			List<JSONObject> activityList = new ArrayList<JSONObject>();
			if (aList != null && aList.size() > 0) {
				for (Collection c : aList) {
					JSONObject json = getCollectionInfo(c);
					JSONObject collectionJ = new JSONObject();
					collectionJ.put("collection", json);
					activityList.add(collectionJ);
				}
			}
			discover.put("collection_activity", activityList);
			List<Collection> iList = collectionDao.getCollectionBytype("interest", count);
			List<JSONObject> interestList = new ArrayList<JSONObject>();
			if (iList != null && iList.size() > 0) {
				for (Collection c : iList) {
					JSONObject json = getCollectionInfo(c);
					JSONObject collectionJ = new JSONObject();
					collectionJ.put("collection", json);
					interestList.add(collectionJ);
				}
			}
			discover.put("collection_interest", interestList);
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));

			List<Collection> aList = collectionDao.getCollectionBytype("activity", count, since_id, 1);
			List<JSONObject> activityList = new ArrayList<JSONObject>();
			if (aList != null && aList.size() > 0) {
				for (Collection c : aList) {
					JSONObject json = getCollectionInfo(c);
					JSONObject collectionJ = new JSONObject();
					collectionJ.put("collection", json);
					activityList.add(collectionJ);
				}
			}
			discover.put("collection_activity", activityList);

		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Collection> aList = collectionDao.getCollectionBytype("activity", count, since_id, 1);
			List<JSONObject> activityList = new ArrayList<JSONObject>();
			if (aList != null && aList.size() > 0) {
				for (Collection c : aList) {
					JSONObject json = getCollectionInfo(c);
					JSONObject collectionJ = new JSONObject();
					collectionJ.put("collection", json);
					activityList.add(collectionJ);
				}
			}
			discover.put("collection_activity", activityList);
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Collection> aList = collectionDao.getCollectionBytype("activity", count, max_id, 2);
			List<JSONObject> activityList = new ArrayList<JSONObject>();
			if (aList != null && aList.size() > 0) {
				for (Collection c : aList) {
					JSONObject json = getCollectionInfo(c);
					JSONObject collectionJ = new JSONObject();
					collectionJ.put("collection", json);
					activityList.add(collectionJ);
				}
			}
			discover.put("collection_activity", activityList);
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Collection> aList = collectionDao.getCollectionBytype("activity", count, max_id, 2);
			List<JSONObject> activityList = new ArrayList<JSONObject>();
			if (aList != null && aList.size() > 0) {
				for (Collection c : aList) {
					JSONObject json = getCollectionInfo(c);
					JSONObject collectionJ = new JSONObject();
					collectionJ.put("collection", json);
					activityList.add(collectionJ);
				}
			}
			discover.put("collection_activity", activityList);
		}

		System.out.println("discover--->" + discover);
		return discover;

	}

}
