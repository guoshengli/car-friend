 package com.revolution.rest.service;
 
 import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;
import com.revolution.rest.common.ParseFile;
import com.revolution.rest.common.PushNotificationUtil;
import com.revolution.rest.dao.CollectionDao;
import com.revolution.rest.dao.CollectionStoryDao;
import com.revolution.rest.dao.CommentDao;
import com.revolution.rest.dao.ConfigurationDao;
import com.revolution.rest.dao.FeatureCollectionDao;
import com.revolution.rest.dao.FeatureUserDao;
import com.revolution.rest.dao.FeedbackDao;
import com.revolution.rest.dao.FollowDao;
import com.revolution.rest.dao.LikesDao;
import com.revolution.rest.dao.NotificationDao;
import com.revolution.rest.dao.PublisherInfoDao;
import com.revolution.rest.dao.PushNotificationDao;
import com.revolution.rest.dao.ReportDao;
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
import com.revolution.rest.model.FeatureCollection;
import com.revolution.rest.model.FeatureUser;
import com.revolution.rest.model.Feedback;
import com.revolution.rest.model.Follow;
import com.revolution.rest.model.Likes;
import com.revolution.rest.model.Notification;
import com.revolution.rest.model.PublisherInfo;
import com.revolution.rest.model.PushNotification;
import com.revolution.rest.model.Report;
import com.revolution.rest.model.Republish;
import com.revolution.rest.model.Slide;
import com.revolution.rest.model.Story;
import com.revolution.rest.model.StoryElement;
import com.revolution.rest.model.Timeline;
import com.revolution.rest.model.User;
import com.revolution.rest.service.model.CollectionIntro;
import com.revolution.rest.service.model.CollectionModel;
import com.revolution.rest.service.model.CommentModel;
import com.revolution.rest.service.model.CoverMedia;
import com.revolution.rest.service.model.FeedbackModel;
import com.revolution.rest.service.model.ImageCover;
import com.revolution.rest.service.model.ImageMedia;
import com.revolution.rest.service.model.LinkModel;
import com.revolution.rest.service.model.LinkModels;
import com.revolution.rest.service.model.LocationModel;
import com.revolution.rest.service.model.ReportModel;
import com.revolution.rest.service.model.StoryIntro;
import com.revolution.rest.service.model.StoryModel;
import com.revolution.rest.service.model.TextCover;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
 
 @Transactional
 public class AdminServiceImpl
   implements AdminService
 {
   private static final Log log = LogFactory.getLog(AdminServiceImpl.class);
 
   @Autowired
   private CollectionDao collectionDao;
 
   @Autowired
   private StoryDao storyDao;
 
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
   private SlideDao slideDao;
 
   @Autowired
   private ReportDao reportDao;
 
   @Autowired
   private UserDao userDao;
 
   @Autowired
   private TimelineDao timelineDao;
 
   @Autowired
   private FeatureUserDao featureUserDao;
 
   @Autowired
   private FeatureCollectionDao featureCollectionDao;
 
   @Autowired
   private PublisherInfoDao publisherInfoDao;
   
   @Autowired
   private FeedbackDao feedbackDao; 
   
   @Autowired
   private PushNotificationDao pushNotificationDao;
   
   @Autowired
   private NotificationDao notificationDao;
   
   @Autowired
   private ConfigurationDao configurationDao;
   
   @Autowired
   private UserCollectionDao userCollectionDao;
   
   @Autowired    
   private JobService jobService; 
 
   public Response reset(Long loginUserid) throws Exception { log.debug("**reset start ****");
     JSONObject json = new JSONObject();
     User user = (User)this.userDao.get(loginUserid);
 
     if (user.getUser_type().equals("admin"))
     {
       Properties p = new Properties();
       p.load(new FileInputStream(
         new File(
         "/app/tomcat/webapps/tella-webservice/META-INF/database.properties")));
 
      importSql(p);
       json.put("status", "success");
       log.debug("***success****");
       return Response.status(Response.Status.OK).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json)
       .build();
   }
 
   public static void importSql(Properties properties) throws IOException
   {
     System.out.println("start import");
     Runtime runtime = Runtime.getRuntime();
 
     String[] command = getImportCommand(properties);
     Process process = runtime.exec(command[0]);
 
     OutputStream os = process.getOutputStream();
     OutputStreamWriter out = new OutputStreamWriter(os);
     out.write(command[1] + "\r\n" + command[2]);
     out.flush();
     out.close();
     os.close();
   }
 
   public static String[] getImportCommand(Properties properties) {
     String username = properties.getProperty("jdbc.username");
     String password = properties.getProperty("jdbc.password");
     String host = properties.getProperty("jdbc.host");
     String port = properties.getProperty("jdbc.port");
     String importDatabaseName = properties
      .getProperty("jdbc.importDatabaseName");
     String importPath = properties.getProperty("jdbc.importPath");
     System.out.println(username + "-->" + password + "-->" + host + "-->" + 
       importDatabaseName + "-->" + importPath);
 
    String loginCommand = "/app/mysql/bin/mysql -u" + username + " -p" + 
       password + " -h" + host + " -P" + port;
     System.out.println("loginCommand-->" + loginCommand);
 
    String switchCommand = "use " + importDatabaseName;
     System.out.println("switchCommand" + switchCommand);
 
    String importCommand = "source " + importPath;
    System.out.println("importCommand" + importCommand);
     String[] strArr = { loginCommand, switchCommand, importCommand };
     return strArr;
   }
 
   public Response createCollection(Long loginUserid, JSONObject collection)
   {
     JSONObject json = new JSONObject();
     User user = (User)this.userDao.get(loginUserid);
   if ((user.getUser_type().equals("admin")) || (user.getUser_type().equals("super_admin"))) {
      Collection c = new Collection();
       if (collection != null) {
         String collectionName = collection.getString("collection_name");
         if (!Strings.isNullOrEmpty(collectionName)) {
           int size = this.collectionDao
            .getCollectionCountByName(collectionName);
           if (size == 0) {
             c.setCollectionName(collectionName);
 
            if (!Strings.isNullOrEmpty(collection
              .getString("cover_image"))) {
               c.setCover_image(collection
                 .getString("cover_image"));
             }
             c.setStatus("enabled");
             c.setUser(user);
             c.setNumber(Long.valueOf(1L));
             if (collection.containsKey("info")) {
               c.setInfo(collection.getString("info"));
             }
             this.collectionDao.save(c);
             c.setNumber((Long)c.getId());
             this.collectionDao.update(c);
             CollectionModel collectionModel = new CollectionModel();
             collectionModel.setCollection_name(c
               .getCollectionName());
             collectionModel.setId((Long)c.getId());
             collectionModel.setInfo(c.getInfo());
             collectionModel.setCover_image(JSONObject.fromObject(c
               .getCover_image()));
             return Response.status(Response.Status.CREATED)
               .entity(collectionModel).build();
           }
           json.put("status", "repetition_collection_name");
           json.put("code", Integer.valueOf(10036));
           json.put("error_message", "collection name is repetition");
           return Response.status(Response.Status.BAD_REQUEST)
             .entity(json).build();
         }
 
         json.put("status", "invalid_request");
         json.put("code", Integer.valueOf(10010));
         json.put("error_message", "Invalid payload parameters");
         return Response.status(Response.Status.BAD_REQUEST)
           .entity(json).build();
       }
 
       json.put("status", "invalid_request");
       json.put("code", Integer.valueOf(10010));
       json.put("error_message", "Invalid payload parameters");
       return Response.status(Response.Status.BAD_REQUEST).entity(json)
         .build();
     }
 
     json.put("status", "invalid_permission");
     json.put("code", Integer.valueOf(10054));
     json.put("error_message", "user has no permission");
     return Response.status(Response.Status.BAD_REQUEST).entity(json)
       .build();
   }
 
   public Response deleteCollection(Long collection_id) {
     Collection collection = 
       (Collection)this.collectionDao
       .get(collection_id);
     JSONObject json = new JSONObject();
     if (collection != null) {
       this.collectionStoryDao.deleteCollectionStoryByCollectionId(collection_id);
       this.collectionDao.delete(collection_id);
       userCollectionDao.delUserCollectionByCollectionId(collection_id);
       FeatureCollection fc = featureCollectionDao.getFeatureCollectionByCollectionId(collection_id);
       if(fc != null){
    	  featureCollectionDao.delFeatureCollection(collection_id);
       }
       json.put("status", "success");
       return Response.status(Response.Status.OK).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json)
       .build();
   }
 
   public Response createCollectionStory(Long collectionId, Long storyId,Long loginUserid) {
	   String path = getClass().getResource("/../../META-INF/getui.json")
		       .getPath();
		     JSONObject jsonObject = ParseFile.parseJson(path);
		     String appId = jsonObject.getString("appId");
		     String appKey = jsonObject.getString("appKey");
		     String masterSecret = jsonObject.getString("masterSecret");
	 CollectionStory csFlag = collectionStoryDao.getCollectionStoryByCollectionIdAndStoryId(collectionId, storyId);
     Collection collection = 
       collectionDao
       .get(collectionId);
     JSONObject json = new JSONObject();
     if(csFlag != null){
    	 String type = collection.getCollection_type();
         Notification notification = null;
         if(type.equals("interest")){
        	 notification = notificationDao.getNotificationByAction(csFlag.getStory().getId(), csFlag.getStory().getUser().getId(), 1, 11);
		}else{
			 notification = notificationDao.getNotificationByAction(csFlag.getStory().getId(), csFlag.getStory().getUser().getId(), 1, 9);
		}
         notificationDao.delete(notification.getId());
         
    	 collectionStoryDao.delete(csFlag.getId());
     }
    	 if (collection != null) {
    	       Story story = storyDao.get(storyId);
    	 
    	       Collection c = this.collectionStoryDao.getCollectionByStoryId(storyId);
    	       if (c == null) {
    	         if ((story != null) && (collection != null)) {
    	           CollectionStory cs = new CollectionStory();
    	           cs.setCollection(collection);
    	           cs.setStory(story);
    	           cs.setAudit(1);
					this.collectionStoryDao.save(cs);
					Notification n = new Notification();
					n.setRecipientId(story.getUser().getId());
					n.setSenderId(loginUserid);
					n.setNotificationType(15);
					n.setObjectType(1);
					n.setObjectId(story.getId());
					n.setStatus("enabled");
					n.setRead_already(true);
					notificationDao.save(n);
					Configuration conf = this.configurationDao.getConfByUserId(story.getUser().getId());
					if (n.getNotificationType() == 15) {
						if (conf.isStory_move_to_collection()) {
							int counts = this.notificationDao
									.getNotificationByRecipientId(story.getUser().getId());
							List<PushNotification> list = this.pushNotificationDao
									.getPushNotificationByUserid(story.getUser().getId());
							try {
								String content = "";
								JSONObject json1 = new JSONObject();

								content = "您发布的壹页入选 " + collection.getCollectionName()
										+ " 小站";
								json1.put("story_id", story.getId());

								PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content,
										json1.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
    	           this.collectionStoryDao.save(cs);
    	           json.put("status", "success");
    	           return Response.status(Response.Status.CREATED).entity(json)
    	             .build();
    	         }
    	 
    	         json.put("status", "invalid_request");
    	         json.put("code", Integer.valueOf(10010));
    	         json.put("error_message", "Invalid payload parameters");
    	         return Response.status(Response.Status.BAD_REQUEST).entity(json)
    	           .build();
    	       }
    	 
    	       json.put("status", "exist_collection_story");
    	       json.put("code", Integer.valueOf(10083));
    	       json.put("error_message", "This story is in this collection.");
    	       return Response.status(Response.Status.BAD_REQUEST).entity(json)
    	         .build();
    	     }
     
     
 
     json.put("status", "collection_deleted");
     json.put("code", Integer.valueOf(10088));
     json.put("error_message", "The collection is already deleted");
     return Response.status(Response.Status.BAD_REQUEST).entity(json)
       .build();
   }
 
   public Response deleteCollectionStory(Long collectionId, Long storyId)
   {
     CollectionStory cs = this.collectionStoryDao
       .getCollectionStoryByCollectionIdAndStoryId(collectionId, 
       storyId);
     JSONObject json = new JSONObject();
     if (cs != null) {
       this.collectionStoryDao.delete((Long)cs.getId());
       json.put("status", "success");
       return Response.status(Response.Status.OK).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json)
       .build();
   }
 
   public List<CollectionModel> getCollections(Long storyId) {
     List<Collection> collectionList = this.collectionDao.getCollections();
     List<CollectionModel> collectionModelList = new ArrayList<CollectionModel>();
     if ((collectionList != null) && (collectionList.size() > 0)) {
       CollectionModel cm = null;
       for (Collection c : collectionList) {
         Set<Story> storySet = c.getStories();
         List<Long> ids = new ArrayList<Long>();
         if ((storySet != null) && (storySet.size() > 0)) {
           for (Story s : storySet) {
             ids.add(s.getId());
           }
         }
 
         cm = new CollectionModel();
         cm.setId((Long)c.getId());
         cm.setCollection_name(c.getCollectionName());
         if (!Strings.isNullOrEmpty(c.getCover_image()))
           cm.setCover_image(JSONObject.fromObject(c.getCover_image()));
         else {
           cm.setCover_image(null);
         }
         
         if (!Strings.isNullOrEmpty(c.getAvatar_image()))
             cm.setAvatar_image(JSONObject.fromObject(c.getAvatar_image()));
           else {
             cm.setAvatar_image(null);
           }
         
         if (ids.contains(storyId))
           cm.setIs_story_in_collection(true);
         else {
           cm.setIs_story_in_collection(false);
         }
         cm.setInfo(c.getInfo());
         collectionModelList.add(cm);
       }
     }
     return collectionModelList;
   }
 
   public List<StoryModel> getStoryByTime(Long loginUserid, HttpServletRequest request)
   {
     String since_date = request.getParameter("since_date");
     String max_date = request.getParameter("max_date");
     String countStr = request.getParameter("count");
     String sinceIdStr = request.getParameter("since_id");
     String maxIdStr = request.getParameter("max_id");
     String filter_type = request.getParameter("filter_type");
     int count = 20;
     String type = "publish";
     List<StoryModel> storyModelList = new ArrayList<StoryModel>();
     StoryModel storyModel = null;
     if (filter_type.equals("time")) {
       if ((Strings.isNullOrEmpty(countStr)) && 
         (Strings.isNullOrEmpty(sinceIdStr)) && 
         (Strings.isNullOrEmpty(maxIdStr))) {
         List<Story> storyList = this.storyDao.getStoriesByTimeAndNull(
           count, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0))
           for (Story story : storyList) {
             storyModel = getStoryModelByStoryLoginUser(story, 
               loginUserid);
             storyModelList.add(storyModel);
           }
       } else if ((!Strings.isNullOrEmpty(countStr)) && 
         (Strings.isNullOrEmpty(sinceIdStr)) && 
         (Strings.isNullOrEmpty(maxIdStr))) {
         count = Integer.parseInt(countStr);
         List<Story> storyList = this.storyDao.getStoriesByTimeAndNull(
           count, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0))
           for (Story story : storyList) {
             storyModel = getStoryModelByStoryLoginUser(story, 
               loginUserid);
             storyModelList.add(storyModel);
           }
       } else if ((Strings.isNullOrEmpty(countStr)) && 
         (!Strings.isNullOrEmpty(sinceIdStr)) && 
         (Strings.isNullOrEmpty(maxIdStr))) {
         Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
         List<Story> storyList = this.storyDao.getStoriesByTime(
           since_id, count, 1, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0))
           for (Story story : storyList) {
             storyModel = getStoryModelByStoryLoginUser(story, 
               loginUserid);
             storyModelList.add(storyModel);
           }
       } else if ((!Strings.isNullOrEmpty(countStr)) && 
         (!Strings.isNullOrEmpty(sinceIdStr)) && 
         (Strings.isNullOrEmpty(maxIdStr))) {
         count = Integer.parseInt(countStr);
         Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
         List<Story> storyList = this.storyDao.getStoriesByTime(
           since_id, count, 1, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0))
           for (Story story : storyList) {
             storyModel = getStoryModelByStoryLoginUser(story, 
               loginUserid);
             storyModelList.add(storyModel);
           }
       } else if ((Strings.isNullOrEmpty(countStr)) && 
         (Strings.isNullOrEmpty(sinceIdStr)) && 
         (!Strings.isNullOrEmpty(maxIdStr))) {
         Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
         List<Story> storyList = this.storyDao.getStoriesByTime(max_id, 
           count, 2, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0))
           for (Story story : storyList) {
             storyModel = getStoryModelByStoryLoginUser(story, 
               loginUserid);
             storyModelList.add(storyModel);
           }
       } else if ((!Strings.isNullOrEmpty(countStr)) && 
         (Strings.isNullOrEmpty(sinceIdStr)) && 
         (!Strings.isNullOrEmpty(maxIdStr))) {
         count = Integer.parseInt(countStr);
         Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
         List<Story> storyList = this.storyDao.getStoriesByTime(max_id, 
           count, 2, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0))
           for (Story s:storyList) { 
             storyModel = getStoryModelByStoryLoginUser(s, loginUserid);
             storyModelList.add(storyModel); 
           }
       }
     }
     else if (filter_type.equals("view_times")) {
       if ((Strings.isNullOrEmpty(countStr)) && 
         (Strings.isNullOrEmpty(sinceIdStr)) && 
         (Strings.isNullOrEmpty(maxIdStr))) {
         List<Story> storyList = this.storyDao.getStoriesByViewAndNull(
           count, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0))
           for (Story story:storyList) { 
             storyModel = getStoryModelByStoryLoginUser(story, 
               loginUserid);
             storyModelList.add(storyModel); }
       }
       else if ((!Strings.isNullOrEmpty(countStr)) && 
         (Strings.isNullOrEmpty(sinceIdStr)) && 
         (Strings.isNullOrEmpty(maxIdStr))) {
         count = Integer.parseInt(countStr);
         List<Story> storyList = this.storyDao.getStoriesByViewAndNull(
           count, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0))
           for (Story story:storyList) {
             storyModel = getStoryModelByStoryLoginUser(story, 
               loginUserid);
             storyModelList.add(storyModel); }
       }
       else if ((Strings.isNullOrEmpty(countStr)) && 
         (!Strings.isNullOrEmpty(sinceIdStr)) && 
         (Strings.isNullOrEmpty(maxIdStr))) {
         Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
         List<Map<String,Object>> storyList = this.storyDao.getStoriesByView(
           since_id, count, 1, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0))
           for (int i = 0; i < storyList.size(); i++) {
             Map<String,Object> map = (Map<String,Object>)storyList.get(i);
             storyModel = getStoryModelByStoryLoginUser(map, 
               loginUserid);
             storyModelList.add(storyModel);
           }
       } else if ((!Strings.isNullOrEmpty(countStr)) && 
         (!Strings.isNullOrEmpty(sinceIdStr)) && 
         (Strings.isNullOrEmpty(maxIdStr))) {
         count = Integer.parseInt(countStr);
         Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
         List<Map<String,Object>> storyList = this.storyDao.getStoriesByView(
           since_id, count, 1, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0))
           for (int i = 0; i < storyList.size(); i++) {
        	   Map<String,Object> map = (Map<String,Object>)storyList.get(i);
             storyModel = getStoryModelByStoryLoginUser(map, 
               loginUserid);
             storyModelList.add(storyModel);
           }
       } else if ((Strings.isNullOrEmpty(countStr)) && 
         (Strings.isNullOrEmpty(sinceIdStr)) && 
         (!Strings.isNullOrEmpty(maxIdStr))) {
         Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
         List<Map<String,Object>> storyList = this.storyDao.getStoriesByView(max_id, 
           count, 2, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0))
           for (int i = 0; i < storyList.size(); i++) {
             Map<String,Object> map = storyList.get(i);
             storyModel = getStoryModelByStoryLoginUser(map, 
               loginUserid);
             storyModelList.add(storyModel);
           }
       } else if ((!Strings.isNullOrEmpty(countStr)) && 
         (Strings.isNullOrEmpty(sinceIdStr)) && 
         (!Strings.isNullOrEmpty(maxIdStr))) {
         count = Integer.parseInt(countStr);
         Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
 
         List<Map<String,Object>> storyList = this.storyDao.getStoriesByView(max_id, 
           count, 2, type, since_date, max_date);
         if ((storyList != null) && (storyList.size() > 0)) {
           for (int i = 0; i < storyList.size(); i++) {
             Map<String,Object> map = (Map<String,Object>)storyList.get(i);
             storyModel = getStoryModelByStoryLoginUser(map, 
               loginUserid);
             storyModelList.add(storyModel);
           }
         }
       }
     }
     log.debug("*** get stories list***" + 
       JSONArray.fromObject(storyModelList));
 
     return storyModelList;
   }
 
   public StoryModel getStoryModelByStoryLoginUser(Story story, Long loginUserid)
   {
     StoryModel storyModel = new StoryModel();
     List<StoryElement> storyElements = new ArrayList<StoryElement>();
     storyModel.setId((Long)story.getId());
     storyModel.setImage_count(story.getImage_count());
     int likesCount = this.likesDao.userLikesCount(
       (Long)story.getUser()
       .getId());
     int repostStoryCount = this.republishDao.userRepostCount(
       (Long)story
       .getUser().getId());
     User user = story.getUser();
 
     Follow loginUserFollowAuthor = this.followDao.getFollow(loginUserid, 
       (Long)story.getUser().getId());
     Follow AuthorFollowLoginUser = this.followDao.getFollow(
       (Long)story
       .getUser().getId(), loginUserid);
 
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
     int storyCount = this.storyDao.getStoryCount((Long)user.getId());
     int follower_Count = this.followDao.userFollowedCount(
       (Long)user
       .getId());
     int following_count = this.followDao.userFollowCount(
       user
       .getId());
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
 
     authorJson.put("followed_by_current_user", 
       Boolean.valueOf(followed_by_current_user));
     authorJson.put("is_following_current_user", 
       Boolean.valueOf(is_following_current_user));
     Collection collection = this.collectionStoryDao.getCollectionByStoryId((Long)story.getId());
     if (collection != null) {
       CollectionIntro ci = new CollectionIntro();
       ci.setId((Long)collection.getId());
       ci.setCollection_name(collection.getCollectionName());
       ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
       ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
       ci.setInfo(collection.getInfo());
 
       storyModel.setCollection(ci);
     }
     storyModel.setSummary(story.getSummary());
     storyModel.setAuthor(authorJson);
     storyModel.setCreated_time(story.getCreated_time());
     storyModel.setUpdate_time(story.getUpdate_time());
 
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
 
     List<StoryElement> seSet = story.getElements();
     if ((seSet != null) && (seSet.size() > 0)) {
       JSONObject content = null;
       String types = "";
       TextCover textCover = null;
       ImageCover imageCover = null;
       for (StoryElement element : seSet) {
         content = JSONObject.fromObject(element.getContents());
 
         types = content.getString("type");
         if (types.equals("text")) {
           textCover = (TextCover)JSONObject.toBean(content, 
             TextCover.class);
           log.debug("*** element TextCover type ***" + 
             textCover.getType());
           element.setContent(textCover);
         } else if (types.equals("image")) {
           imageCover = (ImageCover)JSONObject.toBean(content, 
             ImageCover.class);
           log.debug("*** element ImageCover type ***" + 
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
     log.debug("***get Elements *****" + 
       JSONArray.fromObject(story.getElements(), config));
     storyModel.setElements(JSONArray.fromObject(storyElements, config));
 
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
 
     int count = this.commentDao.getCommentCountById((Long)story.getId());
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
 
   public StoryModel getStoryModelByStoryLoginUser(Map<String,Object> map, Long loginUserid)
   {
     StoryModel storyModel = new StoryModel();
     List<StoryElement> storyElements = new ArrayList<StoryElement>();
     Story story = this.storyDao.getStoryByIdAndStatus(Long.valueOf(map.get("id").toString()), "publish");
     storyModel.setId(Long.valueOf(map.get("id").toString()));
     storyModel.setImage_count(Integer.valueOf(map.get("image_count").toString()).intValue());
     int likesCount = this.likesDao.userLikesCount(Long.valueOf(map.get("author_id").toString()));
     int repostStoryCount = this.republishDao.userRepostCount(Long.valueOf(map.get("author_id").toString()));
     User user = (User)this.userDao.get(Long.valueOf(map.get("author_id").toString()));
 
     Follow loginUserFollowAuthor = this.followDao.getFollow(loginUserid, 
       Long.valueOf(map.get("author_id").toString()));
     Follow AuthorFollowLoginUser = this.followDao.getFollow(Long.valueOf(map.get("author_id").toString()), loginUserid);
 
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
     int storyCount = this.storyDao.getStoryCount((Long)user.getId());
     int follower_Count = this.followDao.userFollowedCount(
       (Long)user
       .getId());
     int following_count = this.followDao.userFollowCount(
       (Long)user
       .getId());
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
 
     authorJson.put("followed_by_current_user", 
       Boolean.valueOf(followed_by_current_user));
     authorJson.put("is_following_current_user", 
       Boolean.valueOf(is_following_current_user));
     Collection collection = this.collectionStoryDao.getCollectionByStoryId((Long)story.getId());
     if (collection != null) {
       CollectionIntro ci = new CollectionIntro();
       ci.setId((Long)collection.getId());
       ci.setCollection_name(collection.getCollectionName());
       ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
       ci.setAvatar_image(JSONObject.fromObject(collection.getAvatar_image()));
       ci.setInfo(collection.getInfo());
 
       storyModel.setCollection(ci);
     }
     storyModel.setSummary(story.getSummary());
     storyModel.setAuthor(authorJson);
     storyModel.setCreated_time(story.getCreated_time());
     storyModel.setUpdate_time(story.getUpdate_time());
 
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
 
     List<StoryElement> seSet = story.getElements();
     if ((seSet != null) && (seSet.size() > 0)) {
       JSONObject content = null;
       String types = "";
       TextCover textCover = null;
       ImageCover imageCover = null;
       for (StoryElement element : seSet) {
         content = JSONObject.fromObject(element.getContents());
 
         types = content.getString("type");
         if (types.equals("text")) {
           textCover = (TextCover)JSONObject.toBean(content, 
             TextCover.class);
           log.debug("*** element TextCover type ***" + 
             textCover.getType());
           element.setContent(textCover);
         } else if (types.equals("image")) {
           imageCover = (ImageCover)JSONObject.toBean(content, 
             ImageCover.class);
           log.debug("*** element ImageCover type ***" + 
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
     log.debug("***get Elements *****" + 
       JSONArray.fromObject(story.getElements(), config));
     storyModel.setElements(JSONArray.fromObject(storyElements, config));
 
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
 
     int count = this.commentDao.getCommentCountById((Long)story.getId());
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
 
   public Response createSlide(Long loginUserid, JSONObject slide)throws Exception {
     Slide slideModel = new Slide();
     String type = slide.getString("type");
     JSONObject json = new JSONObject();
     /*String path = getClass().getResource("/../../META-INF/getui.json").getPath();
	 JSONObject json1 = ParseFile.parseJson(path);
		String appId = json1.getString("appId");
		String appKey = json1.getString("appKey");
		String masterSecret = json1.getString("masterSecret");*/
		Configuration conf = null;
		Story s = null;
		List<PushNotification> pnList = new ArrayList<PushNotification>();
     if (!Strings.isNullOrEmpty(type)) {
       if (type.equals("url")) {
         slideModel.setAuthorId(loginUserid);
         slideModel.setType(type);
         slideModel.setSlide_image(slide.getString("slide_image"));
         JSONObject url = 
           JSONObject.fromObject(slide.getString("slide"));
         slideModel.setUrl(url.getString("url"));
         slideModel.setGroup(slide.getString("group"));
         slideModel.setSequence(slide.getInt("sequence"));
         slideModel.setStatus("enabled");
         this.slideDao.save(slideModel);
       } else if (type.equals("story")) {
    	  
         slideModel.setAuthorId(loginUserid);
         JSONObject story = JSONObject.fromObject(slide
           .getString("slide"));
         slideModel.setReference_id(story
           .getLong("story_id"));
         
         slideModel.setSlide_image(slide.getString("slide_image"));
         slideModel.setGroup(slide.getString("group"));
         slideModel.setType(type);
         slideModel.setSequence(slide.getInt("sequence"));
         slideModel.setStatus("enabled");
         this.slideDao.save(slideModel);
         s = storyDao.get(story.getLong("story_id"));
    	 conf = configurationDao.getConfByUserId(s.getUser().getId());
    	 if (conf.isRecommended_my_story_push()) {
				List<PushNotification> list = this.pushNotificationDao
						.getPushNotificationByUserid(s.getUser().getId());
				pnList.addAll(list);
			}
    	 Map<String, Integer> map = new HashMap<String, Integer>();
			if ((pnList != null) && (pnList.size() > 0)) {
				for (PushNotification pn : pnList) {
					int count = this.notificationDao.getNotificationByRecipientId(pn.getUserId());
					map.put(pn.getClientId(), Integer.valueOf(count));
				}
			}
			/*String content = "";
			if(slide.getString("group").equals("homepage")){
  				content = "您的故事已被推荐至首页";
  			}else if(slide.getString("group").equals("discover")){
  				content = "您的故事已被推荐至发现页";
  			}else if(slide.getString("group").equals("time_square")){
  				content = "您的故事已被推荐至广场";
  			}
			JSONObject j = new JSONObject();
			j.put("story_id",s.getId());
			PushNotificationUtil.pushInfoAllFollow(appId, appKey, masterSecret, pnList, map, content,j.toString());*/
       }else if (type.equals("collection")) {
     	  
           slideModel.setAuthorId(loginUserid);
           JSONObject collection = JSONObject.fromObject(slide
             .getString("slide"));
           slideModel.setReference_id(collection
             .getLong("collection_id"));
           
           slideModel.setSlide_image(slide.getString("slide_image"));
           slideModel.setGroup(slide.getString("group"));
           slideModel.setType(type);
           slideModel.setSequence(slide.getInt("sequence"));
           slideModel.setStatus("enabled");
           this.slideDao.save(slideModel);
           Collection c = collectionDao.get(collection.getLong("collection_id"));
      	 conf = configurationDao.getConfByUserId(c.getUser().getId());
      	 if (conf.isRecommended_my_story_push()) {
  				List<PushNotification> list = this.pushNotificationDao
  						.getPushNotificationByUserid(c.getUser().getId());
  				pnList.addAll(list);
  			}
      	 Map<String, Integer> map = new HashMap<String, Integer>();
  			if ((pnList != null) && (pnList.size() > 0)) {
  				for (PushNotification pn : pnList) {
  					int count = this.notificationDao.getNotificationByRecipientId(pn.getUserId());
  					map.put(pn.getClientId(), Integer.valueOf(count));
  				}
  			}
  			/*String content = "";
  			if(slide.getString("group").equals("homepage")){
  				content = "您的小站已被推荐至首页";
  			}else if(slide.getString("group").equals("discover")){
  				content = "您的小站已被推荐至发现页";
  			}else if(slide.getString("group").equals("time_square")){
  				content = "您的小站已被推荐至广场";
  			}
  			
  			JSONObject j = new JSONObject();
  			j.put("collection_id",c.getId());
  			PushNotificationUtil.pushInfoAllFollow(appId, appKey, masterSecret, pnList, map, content,j.toString());*/
         }
       
     } else {
       json.put("status", "invalid_request");
       json.put("code", Integer.valueOf(10010));
       json.put("error_message", "Invalid payload parameters");
       return Response.status(Response.Status.BAD_REQUEST).entity(json)
         .build();
     }
     json.put("status", "success");
     return Response.status(Response.Status.OK).entity(json).build();
   }
 
   public Response reportStory(Long storyId, Long loginUserid) {
     Story story = (Story)this.storyDao.get(storyId);
     JSONObject json = new JSONObject();
     if (story != null) {
       story.setStatus("disabled");
       notificationDao.disableNotification(1,storyId);
       this.storyDao.update(story);
       this.timelineDao.deleteTimelineByStoryIdAndType(storyId);
       Report report = this.reportDao.getReportByStoryId(storyId);
       if (report != null) {
         report.setStatus("confirmed");
         report.setOperator_id(loginUserid);
         this.reportDao.update(report);
       }
       json.put("status", "success");
       return Response.status(Response.Status.OK).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json)
       .build();
   }
 
   public Response reportComment(Long commentId, Long loginUserid) {
     Comment comment = (Comment)this.commentDao.get(commentId);
     JSONObject json = new JSONObject();
     if (comment != null) {
       comment.setStatus("disabled");
       this.commentDao.update(comment);
       Report report = this.reportDao.getReportByCommentId(commentId);
       if (report != null) {
         report.setStatus("confirmed");
         report.setOperator_id(loginUserid);
         this.reportDao.update(report);
       } else {
         json.put("status", "invalid_request");
         json.put("code", Integer.valueOf(10010));
         json.put("error_message", "Invalid payload parameters");
         return Response.status(Response.Status.BAD_REQUEST)
           .entity(json).build();
       }
 
       json.put("status", "success");
       return Response.status(Response.Status.OK).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json)
       .build();
   }
 
   public Response confirmReport(Long reportId, Long loginUserid) {
     Report report = (Report)this.reportDao.get(reportId);
     JSONObject json = new JSONObject();
     Story story = null;
     Comment comment = null;
     if (report != null) {
       if (report.getType().equals("report_story")) {
         story = (Story)this.storyDao.get(report.getObject_id());
         story.setStatus("disabled");
         this.storyDao.update(story);
         this.timelineDao.deleteTimelineByStoryId((Long)story.getId());
       } else if (report.getType().equals("report_comment")) {
         comment = (Comment)this.commentDao.get(report.getObject_id());
         comment.setStatus("disabled");
         this.commentDao.update(comment);
       }
       this.reportDao.handleReport(report.getObject_id(), report.getType(), loginUserid, "confirmed");
       json.put("status", "success");
       return Response.status(Response.Status.OK).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json)
       .build();
   }
 
   public Response ignoreReport(Long reportId, Long loginUserid) {
     Report report = (Report)this.reportDao.get(reportId);
     JSONObject json = new JSONObject();
     if (report != null) {
       this.reportDao.handleReport(report.getObject_id(), report.getType(), loginUserid, "ignored");
       json.put("status", "success");
       return Response.status(Response.Status.OK).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json)
       .build();
   }
 
   public List<ReportModel> getReports(HttpServletRequest request) {
     String countStr = request.getParameter("count");
     String sinceIdStr = request.getParameter("since_id");
     String maxIdStr = request.getParameter("max_id");
     String filter_type = request.getParameter("filter_type");
 
     int count = 20;
     List<ReportModel > reportList = new ArrayList<ReportModel >();
     ReportModel rm = null;
     if ((Strings.isNullOrEmpty(countStr)) && 
       (Strings.isNullOrEmpty(sinceIdStr)) && 
       (Strings.isNullOrEmpty(maxIdStr))) {
       List<Report> list = this.reportDao.getReportsPage(count, 
         filter_type);
       if ((list != null) && (list.size() > 0))
         for (Report r : list) {
           rm = getReportModel(r);
           reportList.add(rm);
         }
     } else if ((!Strings.isNullOrEmpty(countStr)) && 
       (Strings.isNullOrEmpty(sinceIdStr)) && 
       (Strings.isNullOrEmpty(maxIdStr))) {
       count = Integer.parseInt(countStr);
       List<Report> list = this.reportDao.getReportsPage(count, 
         filter_type);
       if ((list != null) && (list.size() > 0))
         for (Report r : list) {
           rm = getReportModel(r);
           reportList.add(rm);
         }
     } else if ((Strings.isNullOrEmpty(countStr)) && 
       (!Strings.isNullOrEmpty(sinceIdStr)) && 
       (Strings.isNullOrEmpty(maxIdStr))) {
       Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
       List<Report> list = this.reportDao.getReportsPage(count, 
         filter_type, since_id, 1);
       if ((list != null) && (list.size() > 0))
         for (Report r : list) {
           rm = getReportModel(r);
           reportList.add(rm);
         }
     } else if ((!Strings.isNullOrEmpty(countStr)) && 
       (!Strings.isNullOrEmpty(sinceIdStr)) && 
       (Strings.isNullOrEmpty(maxIdStr))) {
       count = Integer.parseInt(countStr);
       Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
       List<Report> list = this.reportDao.getReportsPage(count, 
         filter_type, since_id, 1);
       if ((list != null) && (list.size() > 0))
         for (Report r : list) {
           rm = getReportModel(r);
           reportList.add(rm);
         }
     } else if ((Strings.isNullOrEmpty(countStr)) && 
       (Strings.isNullOrEmpty(sinceIdStr)) && 
       (!Strings.isNullOrEmpty(maxIdStr))) {
       Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
       List<Report> list = this.reportDao.getReportsPage(count, 
         filter_type, max_id, 2);
       if ((list != null) && (list.size() > 0))
         for (Report r : list) {
           rm = getReportModel(r);
           reportList.add(rm);
         }
     } else if ((!Strings.isNullOrEmpty(countStr)) && 
       (Strings.isNullOrEmpty(sinceIdStr)) && 
       (!Strings.isNullOrEmpty(maxIdStr))) {
       count = Integer.parseInt(countStr);
       Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
       List<Report> list = this.reportDao.getReportsPage(count, 
         filter_type, max_id, 2);
       if ((list != null) && (list.size() > 0)) {
         for (Report r : list) {
           rm = getReportModel(r);
           reportList.add(rm);
         }
       }
     }
     return reportList;
   }
 
   public ReportModel getReportModel(Report report) {
     ReportModel rm = new ReportModel();
     if (report != null) {
       rm.setId((Long)report.getId());
       rm.setCreated_at(report.getCreate_time());
       rm.setOperator_id(rm.getOperator_id());
       rm.setStatus(report.getStatus());
       rm.setType(report.getType());
       JSONObject content = new JSONObject();
       if (report.getObject_type() == 2) {
         Comment comment = (Comment)this.commentDao.get(report
           .getObject_id());
         CommentModel commentModel = getCommentModel(comment);
         JSONObject commentJSON = JSONObject.fromObject(commentModel);
         if (commentModel.getTarget_user() == null) {
           commentJSON.remove("target_user");
         }
         content.put("comment", commentJSON);
         Story story = comment.getStory();
         StoryIntro si = new StoryIntro();
         si.setId((Long)story.getId());
         si.setCollectionId(Long.valueOf(1L));
         si.setTitle(story.getTitle());
         si.setSubtitle(story.getSubtitle());
         if (!Strings.isNullOrEmpty(story.getCover_page()))
           si.setCover_media(JSONObject.fromObject(story
             .getCover_page()));
         else {
           si.setCover_media(null);
         }
         content.put("story", si);
       } else if (report.getObject_type() == 1) {
         StoryIntro si = new StoryIntro();
         Story story = (Story)this.storyDao.get(report.getObject_id());
         si.setId((Long)story.getId());
         si.setCollectionId(Long.valueOf(1L));
         si.setTitle(story.getTitle());
         si.setSubtitle(story.getSubtitle());
         if (!Strings.isNullOrEmpty(story.getCover_page()))
           si.setCover_media(JSONObject.fromObject(story
             .getCover_page()));
         else {
           si.setCover_media(null);
         }
         content.put("story", si);
       }
       rm.setContent(content);
     }
 
     return rm;
   }
 
   public CommentModel getCommentModel(Comment comment) {
     CommentModel commentModel = new CommentModel();
     JSONObject userIntro = new JSONObject();
     commentModel.setId((Long)comment.getId());
     commentModel.setContent(comment.getContent());
     commentModel.setCreated_time(comment.getCreated_time());
     commentModel.setStory_id((Long)comment.getStory().getId());
     User user = comment.getUser();
 
     userIntro.put("id", user.getId());
     userIntro.put("username", user.getUsername());
     JSONObject avatarJson = null;
     if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
       avatarJson = JSONObject.fromObject(user.getAvatarImage());
     }
     userIntro.put("avatar_image", avatarJson);
     commentModel.setAuthor(userIntro);
     if (comment.getTarget_user_id() != null) {
       User targetUser = (User)this.userDao.get(comment
         .getTarget_user_id());
       JSONObject targetUserJson = new JSONObject();
       targetUserJson.put("id", targetUser.getId());
       targetUserJson.put("username", targetUser.getUsername());
       commentModel.setTarget_user(targetUserJson);
     }
 
     return commentModel;
   }
 
   public Response revokeReport(Long reportId, Long loginUserid) {
     Report report = (Report)this.reportDao.get(reportId);
     JSONObject json = new JSONObject();
     if (report != null)
     {
       this.reportDao.handleReport(report.getObject_id(), report.getType(), loginUserid, "new");
       Story story = (Story)this.storyDao.get(report.getObject_id());
       story.setStatus("publish");
       this.storyDao.update(story);
       json.put("status", "success");
       return Response.status(Response.Status.OK).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json)
       .build();
   }
 
   public List<CollectionIntro> getCollections()
   {
     List<Collection> collectionList = this.collectionDao.getCollections();
     List<CollectionIntro> collectionModelList = new ArrayList<CollectionIntro>();
     if ((collectionList != null) && (collectionList.size() > 0)) {
       CollectionIntro cm = null;
       for (Collection c : collectionList) {
         cm = new CollectionIntro();
         cm.setId((Long)c.getId());
         cm.setCollection_name(c.getCollectionName());
         if (!Strings.isNullOrEmpty(c.getCover_image()))
           cm.setCover_image(JSONObject.fromObject(c.getCover_image()));
         else {
           cm.setCover_image(null);
         }
         
         if (!Strings.isNullOrEmpty(c.getAvatar_image()))
             cm.setAvatar_image(JSONObject.fromObject(c.getAvatar_image()));
           else {
             cm.setAvatar_image(null);
           }
         cm.setInfo(c.getInfo());
         Set<Story> sSet = c.getStories();
         cm.setStory_count(sSet.size());
         collectionModelList.add(cm);
       }
     }
     return collectionModelList;
   }
 
   public Response addFeaturedUser(Long userId)
   {
     JSONObject json = new JSONObject();
     if (userId != null) {
       FeatureUser featureUser = this.featureUserDao.getFeatureUserByUserid(userId);
       if (featureUser == null) {
         FeatureUser fu = new FeatureUser();
         fu.setUserId(userId);
         this.featureUserDao.save(fu);
         json.put("status", "success");
         return Response.status(Response.Status.CREATED).entity(json).build();
       }
       json.put("status", "resource_exsist");
       json.put("code", Integer.valueOf(10089));
       json.put("error_message", "The resource is exsist");
       return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
     }
 
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
   }
 
   public Response removeFeaturedUser(Long userId)
   {
     JSONObject json = new JSONObject();
     if (userId != null) {
       FeatureUser featureUser = this.featureUserDao.getFeatureUserByUserid(userId);
       if (featureUser == null) {
         json.put("status", "resource_not_exsist");
         json.put("code", Integer.valueOf(10092));
         json.put("error_message", "The resource is not exsist");
         return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
       }
       this.featureUserDao.deleteFeatureUser(userId);
       json.put("status", "success");
       return Response.status(Response.Status.OK).entity(json).build();
     }
 
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
   }
 
   public Response recommendStory(Long loginUserid,Long story_id, HttpServletRequest request)
   {
     String date = request.getParameter("date");
     JSONObject json = new JSONObject();
     Story story = this.storyDao.getStoryByIdAndStatus(story_id, "publish");
     Timeline timeline = timelineDao.getTimelineByStoryIdAndType(story_id, "recommandation");
 	String path = getClass().getResource("/../../META-INF/getui.json")
		       .getPath();
		     JSONObject jsonObject = ParseFile.parseJson(path);
		     String appId = jsonObject.getString("appId");
		     String appKey = jsonObject.getString("appKey");
		     String masterSecret = jsonObject.getString("masterSecret");
     if (story != null) {
       if (!Strings.isNullOrEmpty(date)) {
         story.setRecommendation(true);
         story.setRecommend_date(date);
       } else {
         story.setRecommendation(true);
       }
 
       this.storyDao.update(story);
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
       Date d = null;
       try {
		d = sdf.parse(date);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       if(timeline == null){
    	   Timeline t = new Timeline();
    	   t.setCreatorId(loginUserid);
    	   t.setStory(story);
    	   t.setTargetUserId(loginUserid);
    	   t.setReferenceId(story_id);
    	   t.setType("recommandation");
    	   t.setCreateTime(d);
    	   timelineDao.save(t);
       }else{
    	   timeline.setCreateTime(d);
    	   timelineDao.update(timeline);
       }
       Notification n = new Notification();
		n.setRecipientId(story.getUser().getId());
		n.setSenderId(loginUserid);
		n.setNotificationType(7);
		n.setObjectType(1);
		n.setObjectId(story.getId());
		n.setStatus("enabled");
		n.setRead_already(true);
		notificationDao.save(n);
		
		Configuration conf = this.configurationDao.getConfByUserId(story.getUser().getId());
			if (conf.isRecommended_my_story_push()) {
				int counts = 1;
				List<PushNotification> list = this.pushNotificationDao
						.getPushNotificationByUserid(story.getUser().getId());
				try {
					String content = "";
					JSONObject json1 = new JSONObject();

					content = "您的故事被推荐到首页";
					json1.put("story_id", story.getId());

					PushNotificationUtil.pushInfo(appId, appKey, masterSecret, list, counts, content,
							json1.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
 
       json.put("status", "success");
       return Response.status(Response.Status.OK).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
   }
 
   public Response updateUserType(Long user_id, HttpServletRequest request)
   {
     String user_type = request.getParameter("user_type");
     JSONObject json = new JSONObject();
     if (!Strings.isNullOrEmpty(user_type)) {
       this.userDao.updateUserByUserType(user_id, user_type);
       json.put("status", "success");
       return Response.status(Response.Status.OK).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
   }
 
   public Response createPublisherInfo(Long user_id, JSONObject publisher)
   {
     JSONObject json = new JSONObject();
     User user = (User)this.userDao.get(user_id);
     if (user != null) {
       PublisherInfo pi = new PublisherInfo();
       String type = null;
       if (publisher.containsKey("type")) {
         type = publisher.getString("type");
       }
       if (!Strings.isNullOrEmpty(type)) {
         this.publisherInfoDao.deletePublisherInfo(user.getId(), type);
         if (publisher.containsKey("content")) {
           pi.setContent(publisher.getString("content"));
         }
         pi.setUser(user);
         pi.setType(type);
         this.publisherInfoDao.save(pi);
       }
 
       json.put("status", "success");
       return Response.status(Response.Status.CREATED).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
   }
 
   public Response removePublisher(Long publisher_info_id)
   {
     PublisherInfo pi = (PublisherInfo)this.publisherInfoDao.get(publisher_info_id);
     JSONObject json = new JSONObject();
     if (pi != null) {
       this.publisherInfoDao.delete(publisher_info_id);
       json.put("status", "success");
       return Response.status(Response.Status.OK).entity(json).build();
     }
     json.put("status", "invalid_request");
     json.put("code", Integer.valueOf(10010));
     json.put("error_message", "Invalid payload parameters");
     return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
   }

	@Override
	public List<JSONObject> getFeedback(HttpServletRequest request) {
		List<JSONObject> feedbackList = new ArrayList<JSONObject>();
		String countStr = request.getParameter("count");
	    String sinceIdStr = request.getParameter("since_id");
	    String maxIdStr = request.getParameter("max_id");
	    int count = 20;
		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Feedback> feedList = feedbackDao.getFeedbackList(count);
			FeedbackModel fm = null;
			User user = null;
			JSONObject feedJson = null;
			if(feedList != null && feedList.size() > 0){
				for(Feedback feed:feedList){
					if(feed.getUser_id() > 0){
						user = userDao.get(feed.getUser_id());
						fm = new FeedbackModel();
						fm.setUser_id(user.getId());
						fm.setUser_name(user.getUsername());
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}else{
						fm = new FeedbackModel();
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						delArray.add("user_id");
						delArray.add("user_name");
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}
					
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);

			List<Feedback> feedList = feedbackDao.getFeedbackList(count);
			FeedbackModel fm = null;
			User user = null;
			JSONObject feedJson = null;
			if(feedList != null && feedList.size() > 0){
				for(Feedback feed:feedList){
					if(feed.getUser_id() > 0){
						user = userDao.get(feed.getUser_id());
						fm = new FeedbackModel();
						fm.setId(feed.getId());
						fm.setUser_id(user.getId());
						fm.setUser_name(user.getUsername());
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}else{
						fm = new FeedbackModel();
						fm.setId(feed.getId());
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						delArray.add("user_id");
						delArray.add("user_name");
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}
					
				}
			}
		
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));

			List<Feedback> feedList = feedbackDao.getFeedbackList(count, since_id, 1);
			FeedbackModel fm = null;
			User user = null;
			JSONObject feedJson = null;
			if(feedList != null && feedList.size() > 0){
				for(Feedback feed:feedList){
					if(feed.getUser_id() > 0){
						user = userDao.get(feed.getUser_id());
						fm.setId(feed.getId());
						fm = new FeedbackModel();
						fm.setUser_id(user.getId());
						fm.setUser_name(user.getUsername());
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}else{
						fm = new FeedbackModel();
						fm.setId(feed.getId());
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						delArray.add("user_id");
						delArray.add("user_name");
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}
					
				}
			}
		
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));

			List<Feedback> feedList = feedbackDao.getFeedbackList(count, since_id, 1);
			FeedbackModel fm = null;
			User user = null;
			JSONObject feedJson = null;
			if(feedList != null && feedList.size() > 0){
				for(Feedback feed:feedList){
					if(feed.getUser_id() > 0){
						user = userDao.get(feed.getUser_id());
						fm = new FeedbackModel();
						fm.setId(feed.getId());
						fm.setUser_id(user.getId());
						fm.setUser_name(user.getUsername());
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}else{
						fm = new FeedbackModel();
						fm.setId(feed.getId());
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						delArray.add("user_id");
						delArray.add("user_name");
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}
					
				}
			}
		} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));

			List<Feedback> feedList = feedbackDao.getFeedbackList(count, max_id, 1);
			FeedbackModel fm = null;
			User user = null;
			JSONObject feedJson = null;
			if(feedList != null && feedList.size() > 0){
				for(Feedback feed:feedList){
					if(feed.getUser_id() > 0){
						user = userDao.get(feed.getUser_id());
						fm = new FeedbackModel();
						fm.setId(feed.getId());
						fm.setUser_id(user.getId());
						fm.setUser_name(user.getUsername());
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}else{
						fm = new FeedbackModel();
						fm.setId(feed.getId());
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						delArray.add("user_id");
						delArray.add("user_name");
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}
					
				}
			}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Feedback> feedList = feedbackDao.getFeedbackList(count, max_id, 1);
			FeedbackModel fm = null;
			User user = null;
			JSONObject feedJson = null;
			if(feedList != null && feedList.size() > 0){
				for(Feedback feed:feedList){
					if(feed.getUser_id() > 0){
						user = userDao.get(feed.getUser_id());
						fm = new FeedbackModel();
						fm.setId(feed.getId());
						fm.setId(feed.getId());
						fm.setUser_id(user.getId());
						fm.setUser_name(user.getUsername());
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}else{
						fm = new FeedbackModel();
						fm.setId(feed.getId());
						fm.setId(feed.getId());
						fm.setCreate_time(feed.getCreated_time());
						JsonConfig configs = new JsonConfig();
				        List<String> delArray = new ArrayList<String>();
						
						if(!Strings.isNullOrEmpty(feed.getCover_image())){
							JSONObject json = JSONObject.fromObject(feed.getCover_image());
							ImageMedia im = new ImageMedia();
							im.setFocalpoint(json.getString("focalpoint"));
							im.setName(json.getString("name"));
							im.setOriginal_size(json.getString("original_size"));
							im.setZoom(Float.valueOf(json.get("zoom").toString()));
							fm.setCover_image(im);
						}else{
							delArray.add("cover_image");
						}
						
						if(!Strings.isNullOrEmpty(feed.getInfo())){
							fm.setInfo(feed.getInfo());
						}else{
							delArray.add("info");
						}
						delArray.add("user_id");
						delArray.add("user_name");
				           
				           
			           if ((delArray != null) && (delArray.size() > 0)) {
			             configs.setExcludes(
			               (String[])delArray
			               .toArray(new String[delArray.size()]));
			             configs.setIgnoreDefaultExcludes(false);
			             configs.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			 
			             feedJson = JSONObject.fromObject(fm, configs);
			           } else {
			        	 feedJson = JSONObject.fromObject(fm);
			           }
			           
			           feedbackList.add(feedJson);
					}
					
				}
			}
		}
	     
		return feedbackList;
	}

	@Override
	public Response allusernotification(JSONObject json) {
		String path = getClass().getResource("/../../META-INF/getui.json").getPath();
		JSONObject json1 = ParseFile.parseJson(path);
		String appId = json1.getString("appId");
		String appKey = json1.getString("appKey");
		String masterSecret = json1.getString("masterSecret");
		if(json != null){
			List<PushNotification> pnList = pushNotificationDao.getPushNotification();
			String content = json.getString("content");
			JSONObject json2 = new JSONObject();
			if(json.containsKey("url")){
				json2.put("url",json.getString("url"));
			}else if(json.containsKey("story_id")){
				String storyId = json.getString("story_id");
				Story story = storyDao.get(Long.parseLong(storyId));
				JSONObject storyJson = new JSONObject();
				storyJson.put("id",story.getId());
				storyJson.put("title",story.getTitle());
				storyJson.put("subtitle",story.getSubtitle());
				storyJson.put("summary",story.getSummary());
				User user = story.getUser();
				 JSONObject avatarImageJson = null;
			     if (!Strings.isNullOrEmpty(user.getAvatarImage())) {
			       avatarImageJson = JSONObject.fromObject(user.getAvatarImage());
			     }
			 
				JSONObject authorJson = new JSONObject();
			     authorJson.put("id", user.getId());
			     authorJson.put("username", user.getUsername());
			     authorJson.put("introduction", user.getIntroduction());
			     authorJson.put("avatar_image", avatarImageJson);
			     authorJson.put("user_type", user.getUser_type());
			    storyJson.put("author",authorJson);
			    JSONObject coverJson = JSONObject.fromObject(story.getCover_page());
			    String type =coverJson.getString("type");
			    if(type.equals("image")){
			    	storyJson.put("cover_media", story.getCover_page());
			    }else if(type.equals("multimedia")){
			    	JSONObject contentJson = JSONObject.fromObject(coverJson.get("media"));
			    	JSONArray contentArr = JSONArray.fromObject(contentJson.get("contents"));
			    	Object obj = contentArr.get(0);
			    	storyJson.put("cover_media", obj);
			    }
			    
				json2.put("story",storyJson);
			}else if(json.containsKey("user_id")){
				json2.put("user_id",json.getString("user_id"));
			}
			jobService.run(appId, appKey, masterSecret, pnList, content, json2.toString());
			JSONObject successJSON = new JSONObject();
			successJSON.put("status", "success");
			return Response.status(Response.Status.OK).entity(successJSON).build();
		}else{
			JSONObject j = new JSONObject();
			j.put("status","invalid_request");
			j.put("code",10010);
			j.put("error_message","Invalid payload parameters");
			return Response.status(Response.Status.OK).entity(j).build();
		}
		
		
	}

	@Override
	public Response delSlide(Long id) {
		JSONObject json = new JSONObject();
		if(id != null && id > 0){
			slideDao.delete(id);
			json.put("status", "success");
			return Response.status(Response.Status.OK).entity(json).build();
		}else{
			json.put("status","invalid_request");
			json.put("code",10010);
			json.put("error_message","Invalid payload parameters");
			return Response.status(Response.Status.OK).entity(json).build();
		}
	}

	@Override
	public Response updateCollection(Long collection_id) {
		JSONObject json = new JSONObject();
		if(collection_id != null){
			collectionDao.disableCollection(collection_id);
			FeatureCollection featureCollection = this.featureCollectionDao.getFeatureCollectionByCollectionId(collection_id);
	       if (featureCollection != null) {
	    	   this.featureCollectionDao.delFeatureCollection(collection_id);
	       }
	       this.collectionStoryDao.deleteCollectionStoryByCollectionId(collection_id);
	       userCollectionDao.delUserCollectionByCollectionId(collection_id);
		       
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
	public Response addFeaturedCollection(Long collectionId) {

	     JSONObject json = new JSONObject();
	     if (collectionId != null) {
	       FeatureCollection featureCollection = this.featureCollectionDao.getFeatureCollectionByCollectionId(collectionId);
	       if (featureCollection == null) {
    	   	 FeatureCollection fc = new FeatureCollection();
	         fc.setCollectionId(collectionId);
	         fc.setSequnce(1);
	         this.featureCollectionDao.save(fc);
	         json.put("status", "success");
	         return Response.status(Response.Status.CREATED).entity(json).build();
	       }
	       json.put("status", "resource_exsist");
	       json.put("code", Integer.valueOf(10089));
	       json.put("error_message", "The resource is exsist");
	       return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	     }
	 
	     json.put("status", "invalid_request");
	     json.put("code", Integer.valueOf(10010));
	     json.put("error_message", "Invalid payload parameters");
	     return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	   
	}

	@Override
	public Response removeFeaturedCollection(Long collectionId) {

	     JSONObject json = new JSONObject();
	     if (collectionId != null) {
	       FeatureCollection featureCollection = this.featureCollectionDao.getFeatureCollectionByCollectionId(collectionId);
	       if (featureCollection == null) {
	         json.put("status", "resource_not_exsist");
	         json.put("code", Integer.valueOf(10092));
	         json.put("error_message", "The resource is not exsist");
	         return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	       }
	       this.featureCollectionDao.delFeatureCollection(collectionId);
	       json.put("status", "success");
	       return Response.status(Response.Status.OK).entity(json).build();
	     }
	 
	     json.put("status", "invalid_request");
	     json.put("code", Integer.valueOf(10010));
	     json.put("error_message", "Invalid payload parameters");
	     return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
	   
	}

	@Override
	public void createException() {
		try {
			String name = null;
			byte[] b = name.getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 }

