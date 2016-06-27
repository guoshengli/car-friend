package com.revolution.rest.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;
import com.revolution.rest.dao.ColumnsDao;
import com.revolution.rest.dao.ColumnsStoryDao;
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
import com.revolution.rest.model.Columns;
import com.revolution.rest.model.PublisherInfo;
import com.revolution.rest.model.Story;
import com.revolution.rest.model.User;
import com.revolution.rest.service.model.ColumnsModel;
import com.revolution.rest.service.model.CoverMedia;
import com.revolution.rest.service.model.ImageCover;
import com.revolution.rest.service.model.PublisherInfoModel;
import com.revolution.rest.service.model.StoryEvent;
import com.revolution.rest.service.model.TextCover;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
@Transactional
public class ColumnsServiceImpl implements ColumnsService {
	
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
	   
	   @Autowired
	   private ColumnsStoryDao columnsStoryDao;
	   
	   @Autowired
	   private ColumnsDao columnsDao;
	   

	@Override
	public Response createColumnsStory(Long loginUserid, Long columns_id, Long story_id) {
		return null;
	}

	@Override
	public List<JSONObject> getStoryByColumnsId(Long columns_id,Long loginUserid,HttpServletRequest request) {


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
	      List<Story> storyList = this.columnsStoryDao.getStoriesByColumns(columns_id, count,type);
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
	      List<Story> storyList = this.columnsStoryDao.getStoriesByColumns(columns_id, count,type);
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
	    	      List<Story> storyList = this.columnsStoryDao
	    	        .getStoriesPageByColumns(columns_id, count, since_id, 1,type);
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
	      List<Story> storyList = this.columnsStoryDao
	    	        .getStoriesPageByColumns(columns_id, count, since_id, 1,type);
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
	     List<Story> storyList = this.columnsStoryDao
	    	        .getStoriesPageByColumns(columns_id, count, max_id, 2,type);
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
	 
	     if(!Strings.isNullOrEmpty(story.getSummary())){
	    	 storyModel.setSummary(story.getSummary());
	     }else{
	    	 storyModel.setSummary(null); 
	     }
	 
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
	 
	     return storyJson;
	   }

	@Override
	public List<ColumnsModel> getAllColumns() {
		List<Columns> cList = columnsDao.getAllColumns();
		List<ColumnsModel> cmList = new ArrayList<ColumnsModel>();
		ColumnsModel cm = null;
		if(cList != null && cList.size() > 0){
			for(Columns c:cList){
				cm = new ColumnsModel();
				cm.setId(c.getId());
				cm.setColumn_name(c.getColumn_name());
				cmList.add(cm);
			}
		}
		return cmList;
	}

}
