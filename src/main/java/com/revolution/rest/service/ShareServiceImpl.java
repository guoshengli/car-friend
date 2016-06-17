package com.revolution.rest.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.revolution.rest.dao.CollectionDao;
import com.revolution.rest.dao.CollectionStoryDao;
import com.revolution.rest.dao.CommentDao;
import com.revolution.rest.dao.FollowDao;
import com.revolution.rest.dao.LikesDao;
import com.revolution.rest.dao.RepublishDao;
import com.revolution.rest.dao.StoryDao;
import com.revolution.rest.dao.UserDao;
import com.revolution.rest.model.Collection;
import com.revolution.rest.model.Comment;
import com.revolution.rest.model.PublisherInfo;
import com.revolution.rest.model.Story;
import com.revolution.rest.model.StoryElement;
import com.revolution.rest.model.User;
import com.revolution.rest.service.model.CollectionIntro;
import com.revolution.rest.service.model.CommentSummaryModel;
import com.revolution.rest.service.model.CoverMedia;
import com.revolution.rest.service.model.ImageCover;
import com.revolution.rest.service.model.LinkModel;
import com.revolution.rest.service.model.LinkModels;
import com.revolution.rest.service.model.LocationModel;
import com.revolution.rest.service.model.PublisherInfoModel;
import com.revolution.rest.service.model.StoryCodeModel;
import com.revolution.rest.service.model.StoryEvent;
import com.revolution.rest.service.model.StoryShare;
import com.revolution.rest.service.model.TextCover;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@Transactional
public class ShareServiceImpl implements ShareService {
	@Autowired
	private StoryDao storyDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CommentDao commentDao;

	@Autowired
	private LikesDao likesDao;

	@Autowired
	private RepublishDao republishDao;

	@Autowired
	private FollowDao followDao;

	@Autowired
	private CollectionStoryDao collectionStoryDao;
	
	@Autowired
	private CollectionDao collectionDao;


	@Override
	public Response getStory(String story_code) {

		Story story = this.storyDao.getStoryByURL(story_code);
		StoryCodeModel storyModel = new StoryCodeModel();

		if (story != null) {
			storyModel.setId(story.getId());
			int likesCount = this.likesDao.userLikesCount((Long) story.getUser().getId());
			int repostStoryCount = this.republishDao.userRepostCount((Long) story.getUser().getId());
			User user = story.getUser();
			/*
			 * Follow loginUserFollowAuthor =
			 * this.followDao.getFollow(loginUserid,
			 * (Long)story.getUser().getId()); Follow AuthorFollowLoginUser =
			 * this.followDao.getFollow( (Long)story .getUser().getId(),
			 * loginUserid);
			 */

			boolean followed_by_current_user = false;
			boolean is_following_current_user = false;
			/*
			 * if (loginUserFollowAuthor != null) { followed_by_current_user =
			 * true; }
			 * 
			 * if (AuthorFollowLoginUser != null) { is_following_current_user =
			 * true; }
			 */
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
			storyModel.setImage_count(story.getImage_count());
			storyModel.setResource(story.getResource());
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
			// log.debug("***get Elements *****" +
			// JSONArray.fromObject(story.getElements(), config));
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

			Collection collection = this.collectionStoryDao.getCollectionByStoryId((Long) story.getId());
			if (collection != null) {
				CollectionIntro ci = new CollectionIntro();
				ci.setId((Long) collection.getId());
				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
				ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
				ci.setInfo(collection.getInfo());

				storyModel.setCollection(ci);
			}

			int count = this.commentDao.getCommentCountById((Long) story.getId());
			storyModel.setComment_count(count);
			storyModel.setSummary(story.getSummary());
			/*
			 * Likes likes =
			 * this.likesDao.getLikeByUserIdAndStoryId(loginUserid, storyId); if
			 * (likes != null) storyModel.setLiked_by_current_user(true); else {
			 * storyModel.setLiked_by_current_user(false); }
			 */

			storyModel.setLiked_by_current_user(false);

			int likeCount = this.likesDao.likeStoryCount(story.getId());
			storyModel.setLike_count(likeCount);
			int repostCount = this.republishDao.count(story.getId());
			storyModel.setRepost_count(repostCount);
			/*
			 * Republish repost = this.republishDao.getRepostByUserIdAndStoryId(
			 * loginUserid, storyId); if (repost != null)
			 * storyModel.setRepost_by_current_user(true); else {
			 * storyModel.setRepost_by_current_user(false); }
			 */
			storyModel.setRepost_by_current_user(false);
			int view_count = story.getViewTimes();
			view_count++;
			story.setViewTimes(view_count);
			this.storyDao.update(story);
			// log.debug("***get story model***" +
			// JSONObject.fromObject(storyModel));
			List<Comment> commentList = this.commentDao.getCommentByStoryIdNewThree(story.getId());
			if ((commentList != null) && (commentList.size() > 0)) {
				List<CommentSummaryModel> commentModelList = new ArrayList<CommentSummaryModel>();
				CommentSummaryModel commentModel = null;
				for (Comment c : commentList) {
					commentModel = getCommentSummaryModel(c);
					commentModelList.add(commentModel);
				}
				storyModel.setComments(commentModelList);
			}
			List<Story> storyList = this.storyDao.getStoriesByRandThree(story.getId());
			if ((storyList != null) && (storyList.size() > 0)) {
				List<StoryShare> recommendations = new ArrayList<StoryShare>();
				StoryShare intro = null;
				for (Story s : storyList) {
					intro = new StoryShare();
					intro.setId((Long) s.getId());
					intro.setTitle(s.getTitle());
					intro.setSubtitle(s.getSubtitle());
					if (!Strings.isNullOrEmpty(s.getCover_page()))
						intro.setCover_media(JSONObject.fromObject(s.getCover_page()));
					else {
						intro.setCover_media(null);
					}
					intro.setImage_count(s.getImage_count());
					intro.setCollectionId(Long.valueOf(1L));
					intro.setUrl(s.getTinyURL());
					recommendations.add(intro);
				}
				storyModel.setRecommendation(recommendations);
			}
			return Response.status(Response.Status.OK).entity(storyModel).build();
		}
		JSONObject json = new JSONObject();
		json.put("status", "no_resource");
		json.put("code", "10012");
		json.put("error_message", "The story does not exist");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();

	}

	public CommentSummaryModel getCommentSummaryModel(Comment comment) {
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

		return commentModel;
	}

	@Override
	public Response getCollectionStory(Long collection_id,HttpServletRequest request) {

		List<JSONObject> storyModelList = new ArrayList<JSONObject>();
		int count = 20;
		String type = "publish";
	     String countStr = request.getParameter("count");
	     String maxIdStr = request.getParameter("max_id");
	     JSONObject json = new JSONObject();
	     JSONObject storyModel = null;
	     if ((Strings.isNullOrEmpty(countStr)) && 
	       (Strings.isNullOrEmpty(maxIdStr)))
	     {
	    	 Collection collection = collectionDao.getCollectionById(collection_id);
	 		List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPage(collection_id, count, type);
	 		if ((storyList != null) && (storyList.size() > 0))
	 			for (Story story : storyList) {
	 				storyModel = getStoryEventByStoryLoginUser(story, 0l, null);
	 				storyModelList.add(storyModel);
	 			}
	 		if (collection != null) {
	 			CollectionIntro ci = new CollectionIntro();
	 			ci.setId((Long) collection.getId());
	 			ci.setCollection_name(collection.getCollectionName());
	 			ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
	 			ci.setInfo(collection.getInfo());
	 			ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
	 			JSONObject ciJson = JSONObject.fromObject(ci);
	 			User user = collection.getUser();//userDao.get(collection.getAuthorId());
	 			JSONObject author = new JSONObject();
	 			author.put("username", user.getUsername());
	 			author.put("avatar_image", JSONObject.fromObject(user.getAvatarImage()));
	 			ciJson.put("author", author);
	 			ciJson.put("follow_count", collection.getUsers().size());
	 			json.put("collection", ciJson);
	 		}

	 		json.put("stories", storyModelList);
	     }
	     else if ((!Strings.isNullOrEmpty(countStr)) && 
	       (Strings.isNullOrEmpty(maxIdStr))) {
	       count = Integer.parseInt(countStr);
			List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPage(collection_id, count, type);
			if ((storyList != null) && (storyList.size() > 0))
				for (Story story : storyList) {
					storyModel = getStoryEventByStoryLoginUser(story, 0l, null);
					storyModelList.add(storyModel);
				}
	     }
	    
	     else if ((!Strings.isNullOrEmpty(countStr)) && 
	       (!Strings.isNullOrEmpty(maxIdStr))) {
	    	 count = Integer.parseInt(countStr);
	    	 
	    	 List<Story> storyList = collectionStoryDao.getFeturedStoriesPageByCollectionId(collection_id, count, Long.parseLong(maxIdStr),2, type);
	    	 if ((storyList != null) && (storyList.size() > 0))
					for (Story story : storyList) {
						storyModel = getStoryEventByStoryLoginUser(story, 0l, null);
						storyModelList.add(storyModel);
					}
	     }
	   

		json.put("stories", storyModelList);

		return Response.status(Response.Status.OK).entity(json).build();
	}
	
	public JSONObject getStoryEventByStoryLoginUser(Story story, Long loginUserid,User loginUser)
	   {
	     StoryEvent storyModel = new StoryEvent();
	     storyModel.setId((Long)story.getId());
	     User user = story.getUser();
	     storyModel.setImage_count(story.getImage_count());
	     storyModel.setUrl(story.getTinyURL());
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
	 
	    /* String theme_color = null;
	     if (user.getTheme_id() != null) {
	       Theme_color color = (Theme_color)this.themeColorDao.get(user.getTheme_id());
	       theme_color = color.getColor();
	     }
	     authorJson.put("theme_color", theme_color);*/
	     if (!Strings.isNullOrEmpty(user.getWebsite()))
	       authorJson.put("website", user.getWebsite());
	     else {
	       authorJson.put("website", null);
	     }
	     storyModel.setAuthor(authorJson);
	     storyModel.setCreated_time(story.getCreated_time());
	     int count = this.commentDao.getCommentCountById((Long)story.getId());
	     storyModel.setComment_count(count);
	     if(loginUserid != null && loginUserid > 0){
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
	     }
	     
	 
	     JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
	     String type = jsonObject.getString("type");
	     CoverMedia coverMedia = null;
	     if (type.equals("text")) {
	       coverMedia = (TextCover)JSONObject.toBean(jsonObject, 
	         TextCover.class);
	       storyModel.setCover_media(JSONObject.fromObject(coverMedia));
	     } else if (type.equals("image")) {
	       coverMedia = (ImageCover)JSONObject.toBean(jsonObject, 
	         ImageCover.class);
	       storyModel.setCover_media(JSONObject.fromObject(coverMedia));
	     } else if (type.equals("multimedia")) {
	       storyModel.setCover_media(jsonObject);
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
	 			ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
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
			
	 
	     if (!Strings.isNullOrEmpty(story.getTitle()))
	       storyModel.setTitle(story.getTitle());
	     else {
	       storyModel.setTitle(null);
	     }
	 
	     if (!Strings.isNullOrEmpty(story.getSubtitle()))
	       storyModel.setSubtitle(story.getSubtitle());
	     else {
	       storyModel.setSubtitle(null);
	     }
	     
	     if(!Strings.isNullOrEmpty(story.getSummary())){
	    	 storyModel.setSummary(story.getSummary());
	     }else{
	    	 storyModel.setSummary(null); 
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

}
