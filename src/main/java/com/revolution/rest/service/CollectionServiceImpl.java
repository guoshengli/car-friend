 package com.revolution.rest.service;
 
 import java.util.ArrayList;
import java.util.Date;
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
import com.revolution.rest.common.ParseFile;
import com.revolution.rest.common.PushNotificationUtil;
import com.revolution.rest.dao.CollectionDao;
import com.revolution.rest.dao.CollectionStoryDao;
import com.revolution.rest.dao.CommentDao;
import com.revolution.rest.dao.ConfigurationDao;
import com.revolution.rest.dao.FollowDao;
import com.revolution.rest.dao.LikesDao;
import com.revolution.rest.dao.NotificationDao;
import com.revolution.rest.dao.PushNotificationDao;
import com.revolution.rest.dao.RepublishDao;
import com.revolution.rest.dao.StoryDao;
import com.revolution.rest.dao.UserCollectionDao;
import com.revolution.rest.dao.UserDao;
import com.revolution.rest.model.Collection;
import com.revolution.rest.model.CollectionStory;
import com.revolution.rest.model.Comment;
import com.revolution.rest.model.Configuration;
import com.revolution.rest.model.Follow;
import com.revolution.rest.model.Likes;
import com.revolution.rest.model.Notification;
import com.revolution.rest.model.PublisherInfo;
import com.revolution.rest.model.PushNotification;
import com.revolution.rest.model.Republish;
import com.revolution.rest.model.Story;
import com.revolution.rest.model.StoryElement;
import com.revolution.rest.model.User;
import com.revolution.rest.model.UserCollection;
import com.revolution.rest.service.model.CollectionIntroLast;
import com.revolution.rest.service.model.CollectionModel;
import com.revolution.rest.service.model.CoverMedia;
import com.revolution.rest.service.model.ImageCover;
import com.revolution.rest.service.model.LinkModel;
import com.revolution.rest.service.model.LinkModels;
import com.revolution.rest.service.model.LocationModel;
import com.revolution.rest.service.model.PublisherInfoModel;
import com.revolution.rest.service.model.StoryEvent;
import com.revolution.rest.service.model.StoryHome;
import com.revolution.rest.service.model.StoryModel;
import com.revolution.rest.service.model.TextCover;
import com.revolution.rest.service.model.UserIntro;
import com.revolution.rest.service.model.UserModel;
import com.revolution.rest.service.model.UserParentModel;
import com.revolution.rest.service.model.UserPublisherModel;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
 
 @Transactional
 public class CollectionServiceImpl
   implements CollectionService
 {
   private final Log log = LogFactory.getLog(CollectionServiceImpl.class);
 
   @Autowired
   private CollectionDao collectionDao;
 
   @Autowired
   private CollectionStoryDao collectionStoryDao;
 
   @Autowired
   private CommentDao commentDao;
 
   @Autowired
   private StoryDao storyDao;
 
   @Autowired
   private RepublishDao republishDao;
 
   @Autowired
   private LikesDao likesDao;
 
 
   @Autowired
   private FollowDao followDao;
 
   
   @Autowired
   private UserCollectionDao userCollectionDao;
   
   @Autowired
   private UserDao userDao;
   
   @Autowired
   private ConfigurationDao configurationDao;
   
   @Autowired
   private PushNotificationDao pushNotificationDao;
   
   @Autowired
   private NotificationDao notificationDao;
 
   public Response create(Long loginUserid, JSONObject collection) {

	     JSONObject json = new JSONObject();
	      Collection c = new Collection();
	       if (collection != null) {
	         String collectionName = collection.getString("collection_name");
	         if (!Strings.isNullOrEmpty(collectionName)) {
	           Collection test_c = this.collectionDao
	            .getCollectionByCollectionName(collectionName);
	           if (test_c == null) {
	             c.setCollectionName(collectionName);
	 
	            if (!Strings.isNullOrEmpty(collection
	              .getString("cover_image"))) {
	               c.setCover_image(collection
	                 .getString("cover_image"));
	             }
	            
	            if (!Strings.isNullOrEmpty(collection
	  	              .getString("avatar_image"))) {
	  	               c.setAvatar_image(collection
	  	                 .getString("avatar_image"));
	  	         }
	            
	            User user = userDao.get(loginUserid);
	            
	             c.setStatus("enabled");
	             c.setUser(user);
	             c.setNumber(Long.valueOf(1L));
	             if (collection.containsKey("info")) {
	               c.setInfo(collection.getString("info"));
	             }
	             boolean is_review = collection.getBoolean("is_review");
	             c.setIs_review(is_review);
	             c.setCollection_type(collection.getString("collection_type"));
	             if(collection.containsKey("activity_description")){
	            	 c.setActivity_description(collection.getString("activity_description"));
	             }
	             this.collectionDao.save(c);
	             c.setNumber((Long)c.getId());
	             this.collectionDao.update(c);
	             
	             UserCollection uc = new UserCollection();
	             uc.setCollections(c);
	             uc.setUsers(user);
	             uc.setCreate_time(new Date());
	             Set<User> uSet = c.getUsers();
	             int num = 0;
	             if(uSet != null && uSet.size() > 0){
	            	 num = uSet.size();
	             }
	             
	             uc.setSequence(num+1);
	             userCollectionDao.save(uc);
	             List<Notification> notificationList = new ArrayList<Notification>();
	             List<Follow> followList = this.followDao.getFollowersByUserId(loginUserid);
	             Notification n = null;
					if ((followList != null) && (followList.size() > 0)) {
						for (Follow follow : followList) {
							n = new Notification();
							n.setRecipientId(follow.getPk().getUser().getId());
							n.setSenderId(loginUserid);
							n.setNotificationType(14);
							n.setObjectType(4);
							n.setObjectId(c.getId());
							n.setStatus("enabled");
							n.setRead_already(true);
							notificationList.add(n);
						}
						this.notificationDao.saveNotifications(notificationList);
					}
					
	             CollectionModel collectionModel = new CollectionModel();
	             collectionModel.setCollection_name(c
	               .getCollectionName());
	             collectionModel.setId((Long)c.getId());
	             collectionModel.setInfo(c.getInfo());
	             collectionModel.setCover_image(JSONObject.fromObject(c
	               .getCover_image()));
	             collectionModel.setAvatar_image(JSONObject.fromObject(c
	  	               .getAvatar_image()));
	             return Response.status(Response.Status.CREATED)
	               .entity(collectionModel).build();
	           }else{
	        	   json.put("status", "repetition_collection_name");
		           json.put("code", Integer.valueOf(10036));
		           json.put("error_message", "collection name is repetition");
		           return Response.status(Response.Status.BAD_REQUEST)
		             .entity(json).build();  
	           }
	           
	         }else{
	        	 json.put("status", "invalid_request");
		         json.put("code", Integer.valueOf(10010));
		         json.put("error_message", "Invalid payload parameters");
		         return Response.status(Response.Status.BAD_REQUEST)
		           .entity(json).build(); 
	         }
	 
	         
	       }
	 
	       json.put("status", "invalid_request");
	       json.put("code", Integer.valueOf(10010));
	       json.put("error_message", "Invalid payload parameters");
	       return Response.status(Response.Status.BAD_REQUEST).entity(json)
	         .build();
	 
	   
   }
 
   public Response updateCollection(Long collectionId, JSONObject collection)
   {
     if(collectionId != null && collectionId > 0){
    	Collection c = collectionDao.getCollectionById(collectionId);
    	if(collection.containsKey("collection_name")){
    		if(!Strings.isNullOrEmpty(collection.getString("collection_name"))){
    			c.setCollectionName(collection.getString("collection_name"));
    		}
    		
    	}
    	
    	if(collection.containsKey("info")){
    		c.setInfo(collection.getString("info"));
    	}
    	
    	if(collection.containsKey("activity_description")){
    		c.setActivity_description(collection.getString("activity_description"));
    	}
    	
    	if(collection.containsKey("cover_image")){
    		if(!Strings.isNullOrEmpty(collection.getString("cover_image"))){
    			c.setCover_image(collection.getString("cover_image"));
    		}
    		
    	}
    	
    	if(collection.containsKey("avatar_image")){
    		if(!Strings.isNullOrEmpty(collection.getString("avatar_image"))){
    			c.setAvatar_image(collection.getString("avatar_image"));
    		}
    		
    	}
    	
    	collectionDao.update(c);
    	CollectionIntroLast cis = new CollectionIntroLast();
		cis.setId(c.getId());
		cis.setCollection_name(c.getCollectionName());
		cis.setCover_image(JSONObject.fromObject(c.getCover_image()));
		cis.setInfo(c.getInfo());
		cis.setAvatar_image(JSONObject.fromObject(c.getAvatar_image()));
		cis.setCollection_type(c.getCollection_type());
		if(c.getCollection_type().equals("activity")){
			cis.setActivity_description(c.getActivity_description());
		}
		Set<User> uSet = c.getUsers();
		Iterator<User> its = uSet.iterator();
		if(its.hasNext()){
			boolean flag = false;
			while(its.hasNext()){
				User u = its.next();
				if(u.getId().equals(c.getUser().getId()) 
						&& u.getId() == c.getUser().getId()){
					flag = true;
					break;
				}
			}
			cis.setIs_followed_by_current_user(flag);
		}else{
			cis.setIs_followed_by_current_user(false);
		}
		User u = c.getUser();
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
		cis.setIs_review(c.isIs_review());
    	JSONObject json = JSONObject.fromObject(cis);
    	return Response.status(Response.Status.OK).entity(json).build();
     }else{

 		JSONObject json = new JSONObject();
 		json.put("status", "no_resource");
 		json.put("code",10011);
 		json.put("error_message", "The user does not exist");
 		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
 	
     }
   }
 
   public List<JSONObject> getTimelinesByCollecionId(Long collectionId, HttpServletRequest request, Long loginUserid)
   {
     String countStr = request.getParameter("count");
     String sinceIdStr = request.getParameter("since_id");
     String maxIdStr = request.getParameter("max_id");
     List<JSONObject> storyModelList = new ArrayList<JSONObject>();
     int count = 20;
     User loginUser = null;
     if(loginUserid != null && loginUserid > 0){
    	 loginUser = userDao.get(loginUserid);
     }
     String type = "publish";
     if ((Strings.isNullOrEmpty(countStr)) && 
       (Strings.isNullOrEmpty(sinceIdStr)) && 
       (Strings.isNullOrEmpty(maxIdStr)))
     {
       List<Story> storyList = this.collectionStoryDao.getStoriesPage(
         collectionId, count, type);
       if ((storyList != null) && (storyList.size() > 0))
         for (Story story : storyList) {
        	 JSONObject storyModel = getStoryEventByStoryLoginUser(
             story, loginUserid,loginUser);
           storyModelList.add(storyModel);
         }
     }
     else
     {
    	 JSONObject storyModel = null;
       if ((!Strings.isNullOrEmpty(countStr)) && 
         (Strings.isNullOrEmpty(sinceIdStr)) && 
         (Strings.isNullOrEmpty(maxIdStr))) {
         count = Integer.parseInt(countStr);
         List<Story> storyList = this.collectionStoryDao.getStoriesPage(
           collectionId, count, type);
         if ((storyList != null) && (storyList.size() > 0))
           for (Story story : storyList) {
             storyModel = getStoryEventByStoryLoginUser(
               story, loginUserid,loginUser);
             storyModelList.add(storyModel);
           }
       }
       else if ((!Strings.isNullOrEmpty(countStr)) && 
         (!Strings.isNullOrEmpty(sinceIdStr)) && 
         (Strings.isNullOrEmpty(maxIdStr))) {
         count = Integer.parseInt(countStr);
         Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
         List<Story> storyList = this.collectionStoryDao
           .getStoriesPageByCollectionId(collectionId, count, 
           since_id, 1, type);
         if ((storyList != null) && (storyList.size() > 0))
           for (Story story : storyList) {
             storyModel = getStoryEventByStoryLoginUser(
               story, loginUserid,loginUser);
             storyModelList.add(storyModel);
           }
       }
       else if ((!Strings.isNullOrEmpty(countStr)) && 
         (Strings.isNullOrEmpty(sinceIdStr)) && 
         (!Strings.isNullOrEmpty(maxIdStr))) {
         count = Integer.parseInt(countStr);
         Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
         List<Story> storyList = this.collectionStoryDao
           .getStoriesPageByCollectionId(collectionId, count, max_id, 
           2, type);
         if ((storyList != null) && (storyList.size() > 0))
           for (Story story : storyList) {
             storyModel = getStoryEventByStoryLoginUser(
               story, loginUserid,loginUser);
             storyModelList.add(storyModel);
           }
       }
     }
     this.log.debug("*** get stories list***" + 
       JSONArray.fromObject(storyModelList));
     return storyModelList;
   }
 
   public StoryModel getStoryModelByStoryLoginUser(Story story, Long loginUserid)
   {
     StoryModel storyModel = new StoryModel();
     storyModel.setId((Long)story.getId());
     int repostStoryCount = this.republishDao.userRepostCount(
       (Long)story.getUser()
       .getId());
     User user = story.getUser();
 
     Follow loginUserFollowAuthor = this.followDao.getFollow(loginUserid, 
       story.getUser().getId());
     Follow AuthorFollowLoginUser = this.followDao.getFollow(
       (Long)story.getUser()
       .getId(), loginUserid);
 
     boolean followed_by_current_user = false;
     boolean is_following_current_user = false;
     if (loginUserFollowAuthor != null) {
       followed_by_current_user = true;
     }
 
     if (AuthorFollowLoginUser != null) {
       is_following_current_user = true;
     }
     JSONObject avatarImageJson = null;
     if ((user.getAvatarImage() != null) && (!user.getAvatarImage().equals("")))
       avatarImageJson = JSONObject.fromObject(user
         .getAvatarImage());
     else {
       avatarImageJson = JSONObject.fromObject("{}");
     }
 
     JSONObject coverImageJson = null;
     if ((user.getCoverImage() != null) && (!user.getCoverImage().equals("")))
       coverImageJson = JSONObject.fromObject(user.getCoverImage());
     else {
       coverImageJson = JSONObject.fromObject("{}");
     }
     int storyCount = this.storyDao.getStoryCount((Long)user.getId());
     int follower_Count = this.followDao.userFollowedCount((Long)user.getId());
     int following_count = this.followDao.userFollowCount((Long)user.getId());
     UserParentModel userModel = null;
     if ((user.getUser_type().equals("normal")) || 
       (user.getUser_type().equals("admin")) || 
       (user.getUser_type().equals("super_admin"))||
       (user.getUser_type().equals("vip"))||
       (user.getUser_type().equals("official"))) {
       userModel = new UserModel((Long)user.getId(), 
         user.getUsername(), user.getEmail(), 
         user.getCreated_time(), user.getStatus(), 
         user.getIntroduction(), avatarImageJson, 
         coverImageJson,  repostStoryCount, 
         storyCount, follower_Count, following_count, 
         user.getWebsite(), followed_by_current_user, 
         is_following_current_user, user.getUser_type(),user.getGender());
     } else if ((user.getUser_type().equals("publisher")) || (user.getUser_type().equals("media"))) {
       Set<PublisherInfo> publisherSet = user.getPublisherInfos();
       List<PublisherInfoModel> publisherList = new ArrayList<PublisherInfoModel>();
       PublisherInfoModel pim = null;
       if ((publisherSet != null) && (publisherSet.size() > 0)) {
         for (PublisherInfo pi : publisherSet) {
           pim = new PublisherInfoModel();
           pim.setType(pi.getType());
           pim.setContent(pi.getContent());
           publisherList.add(pim);
         }
       }
       userModel = new UserPublisherModel((Long)user.getId(), 
         user.getUsername(), user.getEmail(), 
         user.getCreated_time(), user.getStatus(), 
         user.getIntroduction(), avatarImageJson, 
         coverImageJson,  repostStoryCount, 
         storyCount, follower_Count, following_count, 
         user.getWebsite(), followed_by_current_user, 
         is_following_current_user, user.getUser_type(), publisherList,user.getGender());
     }
 
     storyModel.setAuthor(JSONObject.fromObject(userModel));
     storyModel.setCreated_time(story.getCreated_time());
     storyModel.setUpdate_time(story.getUpdate_time());
 
     JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
     String type = jsonObject.getString("type");
     CoverMedia coverMedia = null;
     if (type.equals("text"))
       coverMedia = (TextCover)JSONObject.toBean(jsonObject, 
         TextCover.class);
     else if (type.equals("image")) {
       coverMedia = (ImageCover)JSONObject.toBean(jsonObject, 
         ImageCover.class);
     }
 
     storyModel.setCover_media(JSONObject.fromObject(coverMedia));
 
     List<StoryElement> storyElements = new ArrayList<StoryElement>();
     List<StoryElement> seSet = story.getElements();
     if ((seSet != null) && (seSet.size() > 0)) {
       for (StoryElement element : seSet) {
         JSONObject content = JSONObject.fromObject(element
           .getContents());
 
         String types = content.getString("type");
         if (types.equals("text")) {
           TextCover textCover = (TextCover)JSONObject.toBean(
             content, TextCover.class);
           this.log.debug("*** element TextCover type ***" + 
             textCover.getType());
           element.setContent(textCover);
         } else if (types.equals("image")) {
           ImageCover imageCover = (ImageCover)JSONObject.toBean(
             content, ImageCover.class);
           this.log.debug("*** element ImageCover type ***" + 
             imageCover.getType());
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
     this.log.debug("***get Elements *****" + 
       JSONArray.fromObject(story.getElements(), config));
     int count = 0;
     Set<Comment> cSet = story.getComments();
     if(cSet != null && cSet.size() > 0){
  	   count = cSet.size();
     }
     storyModel.setElements(JSONArray.fromObject(storyElements, config));
 
     storyModel.setCommnents_enables(story.getComments_enabled());
     storyModel.setUrl(story.getTinyURL());
     storyModel.setView_count(story.getViewTimes());
     storyModel.setTitle(story.getTitle());
     storyModel.setSubtitle(story.getSubtitle());
     storyModel.setComment_count(count);
     Likes likes = this.likesDao.getLikeByUserIdAndStoryId(loginUserid, 
       (Long)story.getId());
     if (likes != null)
       storyModel.setLiked_by_current_user(true);
     else {
       storyModel.setLiked_by_current_user(false);
     }
     int likeCount = this.likesDao.likeStoryCount((Long)story.getId());
     storyModel.setLike_count(likeCount);
     int repostCount = this.republishDao.count((Long)story.getId());
     storyModel.setRepost_count(repostCount);
     Republish repost = this.republishDao.getRepostByUserIdAndStoryId(
       loginUserid, (Long)story.getId());
     if (repost != null)
       storyModel.setRepost_by_current_user(true);
     else {
       storyModel.setRepost_by_current_user(false);
     }
 
     return storyModel;
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
     }
     
     Set<User> like_set = story.getLike_users();
		if(like_set != null && like_set.size() > 0){
			storyModel.setLike_count(like_set.size());
		}else{
			storyModel.setLike_count(0);
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
     /* Set<Collection> cSet = story.getCollections();
     if(cSet != null && cSet.size() > 0){
    	 collection = cSet.iterator().next();
    	 if (collection != null) {
 			CollectionIntro ci = new CollectionIntro();
 			ci.setId((Long) collection.getId());
 			ci.setCollection_name(collection.getCollectionName());
 			ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
 			ci.setInfo(collection.getInfo());
 			ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
 			User author = collection.getUser();
			JSONObject json = new JSONObject();
			json.put("id",author.getId());
			json.put("username",author.getUsername());
			if(!Strings.isNullOrEmpty(author.getAvatarImage())){
				json.put("avatar_image",JSONObject.fromObject(author.getAvatarImage()));
			}
			ci.setAuthor(json);
			Set<User> follow_collection = collection.getUsers();
			if(follow_collection != null && follow_collection.size() > 0){
				ci.setFollowers_count(follow_collection.size());
			}else{
				ci.setFollowers_count(0);
			}
			JsonConfig configs = new JsonConfig();
			List<String> delArray = new ArrayList<String>();
			if(!Strings.isNullOrEmpty(collection.getActivity_description())){
				ci.setActivity_description(collection.getActivity_description());
			}else{
				if (Strings.isNullOrEmpty(collection.getActivity_description())) {
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
     }*/
		
 
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
   
   public StoryHome getStoryEventByStory(Story story)
   {
	   StoryHome storyModel = new StoryHome();
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
 
     String theme_color = null;
     authorJson.put("theme_color", theme_color);
     if (!Strings.isNullOrEmpty(user.getWebsite()))
       authorJson.put("website", user.getWebsite());
     else {
       authorJson.put("website", null);
     }
     storyModel.setAuthor(authorJson);
     storyModel.setCreated_time(story.getCreated_time());
    /* int count = this.commentDao.getCommentCountById((Long)story.getId());
     storyModel.setComment_count(count);*/
     
 
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
 
     return storyModel;
   }
 
   public List<JSONObject> getFeaturedStoriesByCollection_id(Long collectionId, HttpServletRequest request, Long loginUserid)
   {
     String countStr = request.getParameter("count");
     String sinceIdStr = request.getParameter("since_id");
     String maxIdStr = request.getParameter("max_id");
     List<JSONObject> storyModelList = new ArrayList<JSONObject>();
     int count = 20;
     String type = "publish";
     JSONObject storyModel = null;
     User loginUser = userDao.get(loginUserid);
     if ((Strings.isNullOrEmpty(countStr)) && 
       (Strings.isNullOrEmpty(sinceIdStr)) && 
       (Strings.isNullOrEmpty(maxIdStr)))
     {
       List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPage(
         collectionId, count, type);
       if ((storyList != null) && (storyList.size() > 0))
         for (Story story : storyList) {
           storyModel = getStoryEventByStoryLoginUser(
             story, loginUserid,loginUser);
           storyModelList.add(storyModel);
         }
     }
     else if ((!Strings.isNullOrEmpty(countStr)) && 
       (Strings.isNullOrEmpty(sinceIdStr)) && 
       (Strings.isNullOrEmpty(maxIdStr))) {
       count = Integer.parseInt(countStr);
       List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPage(
         collectionId, count, type);
       if ((storyList != null) && (storyList.size() > 0))
         for (Story story : storyList) {
           storyModel = getStoryEventByStoryLoginUser(
             story, loginUserid,loginUser);
           storyModelList.add(storyModel);
         }
     }
     else if ((!Strings.isNullOrEmpty(countStr)) && 
       (!Strings.isNullOrEmpty(sinceIdStr)) && 
       (Strings.isNullOrEmpty(maxIdStr))) {
       count = Integer.parseInt(countStr);
       Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
       List<Story> storyList = this.collectionStoryDao
         .getFeturedStoriesPageByCollectionId(collectionId, count, 
         since_id, 1, type);
       if ((storyList != null) && (storyList.size() > 0))
         for (Story story : storyList) {
           storyModel = getStoryEventByStoryLoginUser(
             story, loginUserid,loginUser);
           storyModelList.add(storyModel);
         }
     }
     else if ((!Strings.isNullOrEmpty(countStr)) && 
       (Strings.isNullOrEmpty(sinceIdStr)) && 
       (!Strings.isNullOrEmpty(maxIdStr))) {
       count = Integer.parseInt(countStr);
  Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
      List<Story> storyList = this.collectionStoryDao
         .getFeturedStoriesPageByCollectionId(collectionId, count, max_id, 
        2, type);
       if ((storyList != null) && (storyList.size() > 0)) {
         for (Story story : storyList) {
           storyModel = getStoryEventByStoryLoginUser(
             story, loginUserid,loginUser);
           storyModelList.add(storyModel);
         }
       }
     }
     this.log.debug("*** get stories list***" + 
       JSONArray.fromObject(storyModelList));
     return storyModelList;
   }

@Override
public List<JSONObject> getFeaturedStoriesByCollections(HttpServletRequest request,
		Long loginUserid) {

    String countStr = request.getParameter("count");
    String sinceIdStr = request.getParameter("since_id");
    String maxIdStr = request.getParameter("max_id");
    List<JSONObject> storyModelList = new ArrayList<JSONObject>();
    int count = 20;
    String type = "publish";
    JSONObject storyModel = null;
    String ids = "";
    User loginUser = userDao.get(loginUserid);
    List<Collection> collectionList = userCollectionDao.getCollectionByUserid(loginUserid);
    if(collectionList != null && collectionList.size() > 0){
    	for(Collection c:collectionList){
    		ids += c.getId()+",";
    	}
    	if(!Strings.isNullOrEmpty(ids)){
    		ids = ids.substring(0,ids.length()-1);
    	}
    	
    }
    if ((Strings.isNullOrEmpty(countStr)) && 
      (Strings.isNullOrEmpty(sinceIdStr)) && 
      (Strings.isNullOrEmpty(maxIdStr)))
    {
      List<Story> storyList = this.collectionStoryDao.getFeturedStoriesFollow(
    		  ids, count, type);
      if ((storyList != null) && (storyList.size() > 0))
        for (Story story : storyList) {
          storyModel = getStoryEventByStoryLoginUser(
            story, loginUserid,loginUser);
          storyModelList.add(storyModel);
        }
    }
    else if ((!Strings.isNullOrEmpty(countStr)) && 
      (Strings.isNullOrEmpty(sinceIdStr)) && 
      (Strings.isNullOrEmpty(maxIdStr))) {
      count = Integer.parseInt(countStr);
      List<Story> storyList = this.collectionStoryDao.getFeturedStoriesFollow(
        ids, count, type);
      if ((storyList != null) && (storyList.size() > 0))
        for (Story story : storyList) {
          storyModel = getStoryEventByStoryLoginUser(
            story, loginUserid,loginUser);
          storyModelList.add(storyModel);
        }
    }else if ((Strings.isNullOrEmpty(countStr)) && 
    	      (!Strings.isNullOrEmpty(sinceIdStr)) && 
    	      (Strings.isNullOrEmpty(maxIdStr))) {
    	      Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
    	      List<Story> storyList = this.collectionStoryDao
    	        .getFeturedStoriesPageByCollections(ids, count, 
    	        since_id, 1, type);
    	      if ((storyList != null) && (storyList.size() > 0))
    	        for (Story story : storyList) {
    	          storyModel = getStoryEventByStoryLoginUser(
    	            story, loginUserid,loginUser);
    	          storyModelList.add(storyModel);
    	        }
    	    }
    else if ((!Strings.isNullOrEmpty(countStr)) && 
      (!Strings.isNullOrEmpty(sinceIdStr)) && 
      (Strings.isNullOrEmpty(maxIdStr))) {
      count = Integer.parseInt(countStr);
      Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
      List<Story> storyList = this.collectionStoryDao
        .getFeturedStoriesPageByCollections(ids, count, 
        since_id, 1, type);
      if ((storyList != null) && (storyList.size() > 0))
        for (Story story : storyList) {
          storyModel = getStoryEventByStoryLoginUser(
            story, loginUserid,loginUser);
          storyModelList.add(storyModel);
        }
    }
    else if ((!Strings.isNullOrEmpty(countStr)) && 
      (Strings.isNullOrEmpty(sinceIdStr)) && 
      (!Strings.isNullOrEmpty(maxIdStr))) {
      count = Integer.parseInt(countStr);
 Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
     List<Story> storyList = this.collectionStoryDao
        .getFeturedStoriesPageByCollections(ids, count, max_id, 
       2, type);
      if ((storyList != null) && (storyList.size() > 0)) {
        for (Story story : storyList) {
          storyModel = getStoryEventByStoryLoginUser(
            story, loginUserid,loginUser);
          storyModelList.add(storyModel);
        }
      }
    }
    return storyModelList;
  
}

@Override
public Response followCollection(Long loginUserid,JSONObject collectionIds) {
	User user = userDao.get(loginUserid);
	UserCollection uc = null;
	if(user != null){
		JSONArray ja = JSONArray.fromObject(collectionIds.getString("collection_id"));
		Object[] strArr = ja.toArray();
		if(strArr != null && strArr.length > 0){
			for(Object s:strArr){
				UserCollection uCollection = userCollectionDao.getUserCollectionByCollectionId(Long.parseLong(s.toString()),loginUserid);
				if(uCollection == null){
					uc = new UserCollection();
					Collection c = collectionDao.get(Long.parseLong(s.toString()));
					uc.setCollections(c);
					uc.setUsers(user);
					uc.setCreate_time(new Date());
					userCollectionDao.save(uc);
				}
				
			}
			int count = userCollectionDao.getCollectionCountByUserid(loginUserid);
			JSONObject json = new JSONObject();
			json.put("followers_count",count);
			return Response.status(Response.Status.OK).entity(json).build();
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
public Response unfollowCollection(Long loginUserid, Long collection_id) {
	JSONObject json = new JSONObject();
	if(loginUserid != null && collection_id != null){
		userCollectionDao.deleteUserCollection(loginUserid, collection_id);
		int count = userCollectionDao.getCollectionCountByUserid(loginUserid);
		json.put("collections_count",count);
		return Response.status(Response.Status.OK).entity(json).build();
	}else{
		
		json.put("status", "no_resource");
		json.put("code",10011);
		json.put("error_message", "The user does not exist");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}
}

@Override
public JSONObject getFeaturedStoriesByFollowing(Long collectionId, HttpServletRequest request,
		Long loginUserid) {

    String countStr = request.getParameter("count");
    String sinceIdStr = request.getParameter("since_id");
    String maxIdStr = request.getParameter("max_id");
    List<JSONObject> storyModelList = new ArrayList<JSONObject>();
    int count = 20;
    String type = "publish";
    JSONObject storyModel = null;
    User loginUser = null;
    if(loginUserid != null && loginUserid > 0){
    	loginUser = userDao.get(loginUserid);
    }
    
    if ((Strings.isNullOrEmpty(countStr)) && 
      (Strings.isNullOrEmpty(sinceIdStr)) && 
      (Strings.isNullOrEmpty(maxIdStr)))
    {
      List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPage(
        collectionId, count, type);
      if ((storyList != null) && (storyList.size() > 0))
        for (Story story : storyList) {
          storyModel = getStoryEventByStoryLoginUser(
            story, loginUserid,loginUser);
          storyModelList.add(storyModel);
        }
    }
    else if ((!Strings.isNullOrEmpty(countStr)) && 
      (Strings.isNullOrEmpty(sinceIdStr)) && 
      (Strings.isNullOrEmpty(maxIdStr))) {
      count = Integer.parseInt(countStr);
      List<Story> storyList = this.collectionStoryDao.getFeturedStoriesPage(
        collectionId, count, type);
      if ((storyList != null) && (storyList.size() > 0))
        for (Story story : storyList) {
          storyModel = getStoryEventByStoryLoginUser(
            story, loginUserid,loginUser);
          storyModelList.add(storyModel);
        }
    }
    else if ((!Strings.isNullOrEmpty(countStr)) && 
      (!Strings.isNullOrEmpty(sinceIdStr)) && 
      (Strings.isNullOrEmpty(maxIdStr))) {
      count = Integer.parseInt(countStr);
      Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
      List<Story> storyList = this.collectionStoryDao
        .getFeturedStoriesPageByCollectionId(collectionId, count, 
        since_id, 1, type);
      if ((storyList != null) && (storyList.size() > 0))
        for (Story story : storyList) {
          storyModel = getStoryEventByStoryLoginUser(
            story, loginUserid,loginUser);
          storyModelList.add(storyModel);
        }
    }
    else if ((!Strings.isNullOrEmpty(countStr)) && 
      (Strings.isNullOrEmpty(sinceIdStr)) && 
      (!Strings.isNullOrEmpty(maxIdStr))) {
      count = Integer.parseInt(countStr);
      Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
     List<Story> storyList = this.collectionStoryDao
        .getFeturedStoriesPageByCollectionId(collectionId, count, max_id, 
       2, type);
      if ((storyList != null) && (storyList.size() > 0)) {
        for (Story story : storyList) {
          storyModel = getStoryEventByStoryLoginUser(
            story, loginUserid,loginUser);
          storyModelList.add(storyModel);
        }
      }
    }
    JSONObject json = new JSONObject();
    if(loginUserid != null && loginUserid > 0){
    	UserCollection uc = userCollectionDao.getUserCollectionByCollectionId(collectionId,loginUserid);
        if(uc != null){
        	json.put("followed_by_current_user", true);
        }else{
        	json.put("followed_by_current_user", false);
        }
    }else{
    	json.put("followed_by_current_user", false);
    }
    
    json.put("stories", storyModelList);
    return json;
  
}

@Override
public Response updateUserCollection(Long loginUserid, JSONObject user_collection) {
	JSONObject json = new JSONObject();
	if(user_collection != null){
		Object obj = user_collection.get("collection_list");
		JSONArray ja = JSONArray.fromObject(obj);
		if(ja != null && ja.size() > 0){
			for(Object o:ja){
				JSONObject collection_json = JSONObject.fromObject(o);
				Long id = collection_json.getLong("id");
				int index = collection_json.getInt("collection_index");
				userCollectionDao.updateUserCollectionSequenceByUserIdAndCollectionId(loginUserid, id,index);
			}
		}
		
		json.put("status", "success");
		return Response.status(Response.Status.OK).entity(json).build();
		
	}else{
		json.put("status","invalid_request");
		json.put("code",10010);
		json.put("error_message","Invalid payload parameters");
		return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	}
}

	@Override
	public Response deleteCollectionStory(Long collection_id, Long story_id)throws Exception {
		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String appId = jsonObject.getString("appId");
		String appKey = jsonObject.getString("appKey");
		String masterSecret = jsonObject.getString("masterSecret");
	     CollectionStory cs = this.collectionStoryDao
	       .getCollectionStoryByCollectionIdAndStoryId(collection_id,story_id);
	     JSONObject json = new JSONObject();
	     if (cs != null) {
	    	 Story story = cs.getStory();
	    	User user = story.getUser();
	    	Long story_userid = user.getId();
	    	Collection c = cs.getCollection();
	    	Long collection_userid = c.getUser().getId();
	    	User collection_user = userDao.get(collection_userid);
	    	 if (c != null) {
               String type = c.getCollection_type();
               Notification notification = null;
               if(type.equals("interest")){
              	 	notification = notificationDao.getNotificationByAction(story.getId(), story.getUser().getId(), 1, 11);
				}else{
					notification = notificationDao.getNotificationByAction(story.getId(), story.getUser().getId(), 1, 9);
				}
               if(notification != null){
            	   notificationDao.delete(notification.getId());
               }
               
               
             }
	       this.collectionStoryDao.delete(cs.getId());
	       
	      
	       
	       Notification notification = this.notificationDao.getNotificationByAction(
	    		   story_userid, c.getUser().getId(), 1,10);
	    	     if (notification != null) {
	    	       notificationDao.delete(notification.getId());
	    	     } else {
	    	       notification = new Notification();
	    	       notification.setSenderId(c.getUser().getId());
	    	       notification.setRecipientId(story.getUser().getId());
	    	       notification.setNotificationType(10);
	    	       notification.setObjectType(1);
	    	       notification.setObjectId(story.getId());
	    	       notification.setRead_already(false);
	    	       notification.setStatus("enabled");
	    	       this.notificationDao.save(notification);
	    	     }
	        Configuration conf = this.configurationDao.getConfByUserId(user.getId());
			if (conf.isDelete_story_from_collection_push()) {
				List<PushNotification> list = pushNotificationDao.getPushNotificationByUserid(user.getId());
				int counts = notificationDao.getNotificationByRecipientId(user.getId());
				String content = collection_user.getUsername() + " ɾ���㷢���� "+c.getCollectionName() +" �еĹ���";
				JSONObject j = new JSONObject();
				j.put("story_id",story.getId());
				PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content,j.toString());
			}
	       json.put("status", "success");
	       return Response.status(Response.Status.OK).entity(json).build();
	     }
	     json.put("status", "invalid_request");
	     json.put("code",10010);
	     json.put("error_message", "Invalid payload parameters");
	     return Response.status(Response.Status.BAD_REQUEST).entity(json)
	       .build();
	   
	}

	@Override
	public Response updateCollectionStory(Long collection_id, Long story_id,JSONObject status) throws Exception {
		
		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
		JSONObject jsonObject = ParseFile.parseJson(path);
		String appId = jsonObject.getString("appId");
		String appKey = jsonObject.getString("appKey");
		String masterSecret = jsonObject.getString("masterSecret");
	     CollectionStory cs = this.collectionStoryDao
	       .getCollectionStoryByCollectionIdAndStoryId(collection_id,story_id);
	     JSONObject json = new JSONObject();
	     
	     int audit = 0;
	     if(status.containsKey("status")){
	    	 audit = status.getInt("status");
	     }
	     if (cs != null) {
	    	User user = cs.getStory().getUser();
	    	Collection c = cs.getCollection();
	    	Long collection_userid = c.getUser().getId();
	    	User collection_user = userDao.get(collection_userid);
	    	cs.setAudit(audit);
	       this.collectionStoryDao.update(cs);
	       if(audit == 1){
	    	   Notification notification = this.notificationDao.getNotificationByAction(
		    		   user.getId(), c.getUser().getId(), 1,12);
		    	     if (notification != null) {
		    	       notification.setCreate_at(new Date());
		    	       this.notificationDao.update(notification);
		    	     } else {
		    	       notification = new Notification();
		    	       notification.setSenderId(c.getUser().getId());
		    	       notification.setRecipientId(user.getId());
		    	       notification.setNotificationType(12);
		    	       notification.setObjectType(1);
		    	       notification.setObjectId(cs.getStory().getId());
		    	       notification.setRead_already(false);
		    	       notification.setStatus("enabled");
		    	       this.notificationDao.save(notification);
		    	     }
			} else if (audit == 2) {
				Notification notification = this.notificationDao.getNotificationByAction(user.getId(),
						c.getUser().getId(), 1, 13);
				if (notification != null) {
					notification.setCreate_at(new Date());
					this.notificationDao.update(notification);
				} else {
					notification = new Notification();
					notification.setSenderId(c.getUser().getId());
					notification.setRecipientId(user.getId());
					notification.setNotificationType(13);
					notification.setObjectType(1);
					notification.setObjectId(cs.getStory().getId());
					notification.setRead_already(false);
					notification.setStatus("enabled");
					this.notificationDao.save(notification);
				}
			}
	       Configuration conf = this.configurationDao.getConfByUserId(user.getId());
			if (conf.isDelete_story_from_collection_push()) {
				List<PushNotification> list = pushNotificationDao.getPushNotificationByUserid(user.getId());
				int counts = notificationDao.getNotificationByRecipientId(user.getId());
				String content = "";
				if(audit == 1){
					
					content = "վ�� "+collection_user.getUsername() + " �������Ͷ��";
				}else if(audit == 2){
					content = "վ�� "+collection_user.getUsername() + " �ܾ����Ͷ��";
				}
				JSONObject j = new JSONObject();
				j.put("story_id",cs.getStory().getId());
				PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content,j.toString());
			}
	       json.put("status", "success");
	       return Response.status(Response.Status.OK).entity(json).build();
	     }
	     json.put("status", "remove_story");
	     json.put("code",10210);
	     json.put("error_message", "Author remove story from collection");
	     return Response.status(Response.Status.BAD_REQUEST).entity(json)
	       .build();
	   
	
	}

	@Override
	public Response getCollections(Long loginUserid) {
		List<CollectionIntroLast> collection_info = new ArrayList<CollectionIntroLast>();
		CollectionIntroLast ci = null;
		List<Collection> cList = null;
		cList = this.collectionDao.getCollectionBytype("interest");
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
		return Response.status(Response.Status.OK).entity(collection_info).build();
		
	}
	
	@Override
	public Response getCollectionsAll(Long loginUserid) {
		List<CollectionIntroLast> collection_info = new ArrayList<CollectionIntroLast>();
		CollectionIntroLast ci = null;
		List<Collection> cList = null;
		cList = collectionDao.getCollections();
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
		return Response.status(Response.Status.OK).entity(collection_info).build();
		
	}

	@Override
	public List<UserIntro> getFollowingByCollectionId(Long collection_id, HttpServletRequest request,
			Long loginUserid) {

		log.debug("***get user_id ��ע��user info");
		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<UserIntro> userIntroList = new ArrayList<UserIntro>();
		int count = 20;
		Follow f = null;
		Follow followed = null;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<User> userList = userCollectionDao.getUserByCollectionId(collection_id, count);
			if ((userList != null) && (userList.size() > 0))
				for (User user : userList) {

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
					f = this.followDao.getFollow(user.getId(), loginUserid);
					if (f != null)
						userIntro.setIs_following_current_user(true);
					else {
						userIntro.setIs_following_current_user(false);
					}
					followed = this.followDao.getFollow(loginUserid, user.getId());
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

				List<User> userList = userCollectionDao.getUserByCollectionId(collection_id, count);
				if ((userList != null) && (userList.size() > 0))
					for (User user : userList) {

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
						f = this.followDao.getFollow(user.getId(), loginUserid);
						if (f != null)
							userIntro.setIs_following_current_user(true);
						else {
							userIntro.setIs_following_current_user(false);
						}
						followed = this.followDao.getFollow(loginUserid, user.getId());
						if (followed != null)
							userIntro.setFollowed_by_current_user(true);
						else {
							userIntro.setFollowed_by_current_user(false);
						}
						userIntro.setUser_type(user.getUser_type());
						userIntroList.add(userIntro);
					}
			
			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				List<User> userList = userCollectionDao.getUserByCollectionIdPage(collection_id, count,since_id,1);
				if ((userList != null) && (userList.size() > 0))
					for (User user : userList) {

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
						f = this.followDao.getFollow(user.getId(), loginUserid);
						if (f != null)
							userIntro.setIs_following_current_user(true);
						else {
							userIntro.setIs_following_current_user(false);
						}
						followed = this.followDao.getFollow(loginUserid, user.getId());
						if (followed != null)
							userIntro.setFollowed_by_current_user(true);
						else {
							userIntro.setFollowed_by_current_user(false);
						}
						userIntro.setUser_type(user.getUser_type());
						userIntroList.add(userIntro);
					}
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				List<User> userList = userCollectionDao.getUserByCollectionIdPage(collection_id, count,since_id,1);
				if ((userList != null) && (userList.size() > 0))
					for (User user : userList) {

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
						f = this.followDao.getFollow(user.getId(), loginUserid);
						if (f != null)
							userIntro.setIs_following_current_user(true);
						else {
							userIntro.setIs_following_current_user(false);
						}
						followed = this.followDao.getFollow(loginUserid, user.getId());
						if (followed != null)
							userIntro.setFollowed_by_current_user(true);
						else {
							userIntro.setFollowed_by_current_user(false);
						}
						userIntro.setUser_type(user.getUser_type());
						userIntroList.add(userIntro);
					}
			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<User> userList = userCollectionDao.getUserByCollectionIdPage(collection_id, count,max_id,2);
				if ((userList != null) && (userList.size() > 0))
					for (User user : userList) {

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
						f = this.followDao.getFollow(user.getId(), loginUserid);
						if (f != null)
							userIntro.setIs_following_current_user(true);
						else {
							userIntro.setIs_following_current_user(false);
						}
						followed = this.followDao.getFollow(loginUserid, user.getId());
						if (followed != null)
							userIntro.setFollowed_by_current_user(true);
						else {
							userIntro.setFollowed_by_current_user(false);
						}
						userIntro.setUser_type(user.getUser_type());
						userIntroList.add(userIntro);
					}
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				List<User> userList = userCollectionDao.getUserByCollectionIdPage(collection_id, count,max_id,2);
				if ((userList != null) && (userList.size() > 0))
					for (User user : userList) {

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
						f = this.followDao.getFollow(user.getId(), loginUserid);
						if (f != null)
							userIntro.setIs_following_current_user(true);
						else {
							userIntro.setIs_following_current_user(false);
						}
						followed = this.followDao.getFollow(loginUserid, user.getId());
						if (followed != null)
							userIntro.setFollowed_by_current_user(true);
						else {
							userIntro.setFollowed_by_current_user(false);
						}
						userIntro.setUser_type(user.getUser_type());
						userIntroList.add(userIntro);
					}
				
				}
		
		return userIntroList;
	
	}
 }
