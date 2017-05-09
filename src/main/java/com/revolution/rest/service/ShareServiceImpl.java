package com.revolution.rest.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import com.google.common.base.Strings;
import com.revolution.rest.common.EncryptionUtil;
import com.revolution.rest.common.ParseFile;
import com.revolution.rest.dao.CollectionDao;
import com.revolution.rest.dao.CollectionStoryDao;
import com.revolution.rest.dao.ColumnsDao;
import com.revolution.rest.dao.ColumnsStoryDao;
import com.revolution.rest.dao.CommentDao;
import com.revolution.rest.dao.ConfigurationDao;
import com.revolution.rest.dao.FeatureCollectionDao;
import com.revolution.rest.dao.FollowDao;
import com.revolution.rest.dao.LikesDao;
import com.revolution.rest.dao.LinkAccountsDao;
import com.revolution.rest.dao.RepublishDao;
import com.revolution.rest.dao.SlideDao;
import com.revolution.rest.dao.StoryDao;
import com.revolution.rest.dao.TimelineDao;
import com.revolution.rest.dao.UserCollectionDao;
import com.revolution.rest.dao.UserDao;
import com.revolution.rest.model.Collection;
import com.revolution.rest.model.Columns;
import com.revolution.rest.model.Comment;
import com.revolution.rest.model.Configuration;
import com.revolution.rest.model.FeatureCollection;
import com.revolution.rest.model.Follow;
import com.revolution.rest.model.FollowId;
import com.revolution.rest.model.LinkAccounts;
import com.revolution.rest.model.PublisherInfo;
import com.revolution.rest.model.Republish;
import com.revolution.rest.model.Slide;
import com.revolution.rest.model.Story;
import com.revolution.rest.model.StoryElement;
import com.revolution.rest.model.Timeline;
import com.revolution.rest.model.User;
import com.revolution.rest.service.model.CollectionIntro;
import com.revolution.rest.service.model.CommentStoryModel;
import com.revolution.rest.service.model.CommentSummaryModel;
import com.revolution.rest.service.model.CoverMedia;
import com.revolution.rest.service.model.EventModel;
import com.revolution.rest.service.model.GetuiModel;
import com.revolution.rest.service.model.IframeCover;
import com.revolution.rest.service.model.ImageCover;
import com.revolution.rest.service.model.LineCover;
import com.revolution.rest.service.model.LinkAccountWebModel;
import com.revolution.rest.service.model.LinkModel;
import com.revolution.rest.service.model.LinkModels;
import com.revolution.rest.service.model.LocationModel;
import com.revolution.rest.service.model.PublisherInfoModel;
import com.revolution.rest.service.model.ReplyCommentModel;
import com.revolution.rest.service.model.SlideModel;
import com.revolution.rest.service.model.StoryEvent;
import com.revolution.rest.service.model.StoryEventNew;
import com.revolution.rest.service.model.StoryHomeCopy;
import com.revolution.rest.service.model.StoryIntros;
import com.revolution.rest.service.model.StoryLastModel;
import com.revolution.rest.service.model.TextCover;
import com.revolution.rest.service.model.UserIntro;
import com.revolution.rest.service.model.VideoCover;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import sun.misc.BASE64Decoder;

@Transactional
public class ShareServiceImpl implements ShareService {
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private StoryDao storyDao;
	
	@Autowired
	private CommentDao commentDao;
	
	@Autowired
	private TimelineDao timelineDao;
	
	@Autowired
	private SlideDao slideDao;
	
	@Autowired
	private FeatureCollectionDao featureCollectionDao;

	@Autowired
	private LikesDao likesDao;
	
	@Autowired
	private ColumnsDao columnsDao;

	@Autowired
	private RepublishDao republishDao;

	@Autowired
	private FollowDao followDao;

	@Autowired
	private CollectionStoryDao collectionStoryDao;
	
	@Autowired
	private CollectionDao collectionDao;
	
	@Autowired
	private ColumnsStoryDao columnsStoryDao;

	@Autowired
	private ConfigurationDao configurationDao;

	@Autowired
	private LinkAccountsDao linkAccountsDao;

	@Autowired
	private UserCollectionDao userCollectionDao;



	@Override
	public Response getStory(String story_code, Long loginUserid) {

		Story story = this.storyDao.getStoryByURL(story_code);
		StoryLastModel storyModel = new StoryLastModel();

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

			int count = this.commentDao.getCommentCountById((Long) story.getId());
			storyModel.setComment_count(count);
			storyModel.setSummary(story.getSummary());
			
			Set<Story> likeStory = null;
			if (loginUserid != null && loginUserid > 0) {
				User loginUser = userDao.get(loginUserid);
				likeStory = loginUser.getLike_story();
				Republish repost = this.republishDao.getRepostByUserIdAndStoryId(loginUserid, story.getId());
				if (repost != null)
					storyModel.setRepost_by_current_user(true);
				else {
					storyModel.setRepost_by_current_user(false);
				}
			}else{
				storyModel.setRepost_by_current_user(false);
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


			int view_count = story.getViewTimes();
			view_count++;
			story.setViewTimes(view_count);
			this.storyDao.update(story);
			List<Comment> commentList = this.commentDao.getCommentByStoryIdNewThree(story.getId());
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
			
				
//				
//				List<CommentSummaryModel> commentModelList = new ArrayList<CommentSummaryModel>();
//				CommentSummaryModel commentModel = null;
//				for (Comment c : commentList) {
//					commentModel = getCommentSummaryModel(c);
//					commentModelList.add(commentModel);
//				}
//				storyModel.setComments(commentModelList);
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
				System.out.println("------------>>>>>>>>"+JSONObject.fromObject(storyModel).toString());
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
	 			User author = collection.getUser();//userDao.get(collection.getAuthorId());
				JSONObject json = new JSONObject();
				json.put("id",author.getId());
				json.put("username",author.getUsername());
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
	 		}
	     }
			
	 
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
	public JSONObject getTimelinesBySlidesColumns(Long loginUserid, HttpServletRequest request,
			HttpServletResponse response, String appVersion) {

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
				List<Columns> cList = columnsDao.getAllColumns();
				List<JSONObject> cmList = new ArrayList<JSONObject>();
				JSONObject cm = null;
				if (cList != null && cList.size() > 0) {
					for (Columns c : cList) {
						cm = new JSONObject();
						cm.put("id",c.getId());
						cm.put("column_name",c.getColumn_name());
						if (!Strings.isNullOrEmpty(c.getDescription())) {
							cm.put("description",c.getDescription());
						} 

						cm.put("cover_media",JSONObject.fromObject(c.getCover_image()));
						cmList.add(cm);
					}
				}
				homepage.put("slides",smList);
				homepage.put("events",emList);
				homepage.put("columns",cmList);
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
				List<Columns> cList = columnsDao.getAllColumns();
				List<JSONObject> cmList = new ArrayList<JSONObject>();
				JSONObject cm = null;
				if (cList != null && cList.size() > 0) {
					for (Columns c : cList) {
						cm = new JSONObject();
						cm.put("id",c.getId());
						cm.put("column_name",c.getColumn_name());
						if (!Strings.isNullOrEmpty(c.getDescription())) {
							cm.put("description",c.getDescription());
						} 

						cm.put("cover_media",JSONObject.fromObject(c.getCover_image()));
						cmList.add(cm);
					}
				}
				homepage.put("slides",smList);
				homepage.put("events",emList);
				homepage.put("columns",cmList);
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
				timelineList = timelineDao.getTimelineByRecommandation(max_id,count,1);
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
				timelineList = timelineDao.getTimelineByRecommandation(max_id,count,1);
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
		}else{
			long start1 = System.currentTimeMillis();
			User loginUser = userDao.get(loginUserid);
			/*String followingId = loginUserid + "";
			List<Follow> followList = this.followDao.getFollowingsByUserId(loginUserid);
			if ((followList != null) && (followList.size() > 0)) {
				for (int i = 0; i < followList.size(); i++) {
					followingId = followingId + "," +  followList.get(i).getPk().getFollower().getId();
				}
			}*/
			SlideModel sm = null;
			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				
				timelineList = timelineDao.getTimelineByHome(count);
				
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
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
				
				List<Columns> cList = columnsDao.getAllColumns();
				List<JSONObject> cmList = new ArrayList<JSONObject>();
				JSONObject cm = null;
				if (cList != null && cList.size() > 0) {
					for (Columns c : cList) {
						cm = new JSONObject();
						cm.put("id",c.getId());
						cm.put("column_name",c.getColumn_name());
						if (!Strings.isNullOrEmpty(c.getDescription())) {
							cm.put("description",c.getDescription());
						} 

						cm.put("cover_media",JSONObject.fromObject(c.getCover_image()));
						cmList.add(cm);
					}
				}
				homepage.put("columns",cmList);
				homepage.put("slides",smList);
				homepage.put("events",emList);
				
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				long startTime = System.currentTimeMillis();
				count = Integer.parseInt(countStr);
				timelineList = timelineDao.getTimelineByHome(count);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
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
				List<Columns> cList = columnsDao.getAllColumns();
				List<JSONObject> cmList = new ArrayList<JSONObject>();
				JSONObject cm = null;
				if (cList != null && cList.size() > 0) {
					for (Columns c : cList) {
						cm = new JSONObject();
						cm.put("id",c.getId());
						cm.put("column_name",c.getColumn_name());
						if (!Strings.isNullOrEmpty(c.getDescription())) {
							cm.put("description",c.getDescription());
						} 

						cm.put("cover_media",JSONObject.fromObject(c.getCover_image()));
						cmList.add(cm);
					}
				}
				homepage.put("columns",cmList);
			} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				timelineList = timelineDao.getTimelineByHome(since_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
				timelineList = timelineDao.getTimelineByHome(since_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				homepage.put("events",emList);
			} else if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineByHome(max_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
						emList.add(em);
					}
				}
				
				homepage.put("events",emList);
			} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				timelineList = timelineDao.getTimelineByHome(max_id,count,1);
				if(timelineList != null && timelineList.size() > 0){
					for(Timeline tl:timelineList){
						em = getEventModelListByLoginidNew(tl, loginUserid,loginUser);
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
		}

		FeatureCollection fc = featureCollectionDao.getFeatureCollectionByOne();
		if(fc != null){
			Collection collection = collectionDao.getCollectionById(fc.getCollectionId());
			if(collection != null){
				CollectionIntro ci = new CollectionIntro();
				ci.setId((Long) collection.getId());
				ci.setCollection_name(collection.getCollectionName());
				ci.setCover_image(JSONObject.fromObject(collection.getCover_image()));
				ci.setInfo(collection.getInfo());
				User author = collection.getUser();//userDao.get(collection.getAuthorId());
				JSONObject ajson = new JSONObject();
				ajson.put("id",author.getId());
				ajson.put("username",author.getUsername());
				if(!Strings.isNullOrEmpty(author.getAvatarImage())){
					ajson.put("avatar_image", JSONObject.fromObject(author.getAvatarImage()));
				}
				
				ci.setAuthor(ajson);
				homepage.put("recomandation_collection", ci);
			}
		}
	
	
		
		
		return homepage;
	
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

					storyModel.setRecommend_date(story.getRecommend_date());
					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					/*Set<Columns> cSet = story.getColumns();
					if(cSet != null && cSet.size() > 0){
						Iterator<Columns> iter = cSet.iterator();
						if(iter.hasNext()){
							Columns c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("column_name", c.getColumn_name());
							storyModel.setColumns(json);
						}
					}else{
						delArray.add("columns");
					}*/
					
					Set<Collection> cSet = story.getCollections();
					if(cSet != null && cSet.size() > 0){
						Iterator<Collection> iter = cSet.iterator();
						List<JSONObject> collections = new ArrayList<JSONObject>();
						while(iter.hasNext()){
							Collection c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("collection_name", c.getCollectionName());
							collections.add(json);
						}
						storyModel.setCollections(collections);
					}else{
						delArray.add("collections");
					}

					
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
				}

				event.setContent(contentJson);
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
			JSONObject json = getSlideStoryByStory(story);
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
 	         ci.setInfo(collection.getInfo());
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
	
	public EventModel getEventModelListByLoginidNew(Timeline timeline, 
			Long loginUserid,User loginUser) {

		try {
			EventModel event = new EventModel();
			event.setId((Long) timeline.getId());
			event.setEvent_time(timeline.getCreateTime());
			event.setEvent_type(timeline.getType());
			JSONObject contentJson = new JSONObject();
			Story story = timeline.getStory();
			StoryEventNew storyModel = new StoryEventNew();

			if (story != null) {
				Long storyId = story.getId();
				User u = story.getUser();
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
					
					authorJson.put("user_type", u.getUser_type());
					storyModel.setAuthor(authorJson);
					storyModel.setCreated_time(story.getCreated_time());
					Set<User> like_set = story.getLike_users();
					if(like_set != null && like_set.size() > 0){
						storyModel.setLike_count(like_set.size());
					}else{
						storyModel.setLike_count(0);
					}
					
					
					
					
					
					storyModel.setSummary(story.getSummary());

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
					
					/*Set<Columns> colSet = story.getColumns();
					if(colSet != null && colSet.size() > 0){
						Iterator<Columns> iter = colSet.iterator();
						if(iter.hasNext()){
							Columns c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("column_name", c.getColumn_name());
							storyModel.setColumns(json);
						}
					}else{
						delArray.add("columns");
					}*/
					
					Set<Collection> cSet = story.getCollections();
					
					if(cSet != null && cSet.size() > 0){
						Iterator<Collection> iter = cSet.iterator();
						List<JSONObject> collections = new ArrayList<JSONObject>();
						while(iter.hasNext()){
							Collection c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("collection_name", c.getCollectionName());
							collections.add(json);
						}
						storyModel.setCollections(collections);
					}else{
						delArray.add("collections");
					}
					
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

					storyModel.setRecommend_date(story.getRecommend_date());

					JsonConfig configs = new JsonConfig();
					List<String> delArray = new ArrayList<String>();
					/*Set<Columns> colSet = story.getColumns();
					if(colSet != null && colSet.size() > 0){
						Iterator<Columns> iter = colSet.iterator();
						if(iter.hasNext()){
							Columns c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("column_name", c.getColumn_name());
							storyModel.setColumns(json);
						}
					}else{
						delArray.add("columns");
					}*/
					
					Set<Collection> cSet = story.getCollections();
					if(cSet != null && cSet.size() > 0){
						Iterator<Collection> iter = cSet.iterator();
						List<JSONObject> collections = new ArrayList<JSONObject>();
						while(iter.hasNext()){
							Collection c = iter.next();
							JSONObject json = new JSONObject();
							json.put("id",c.getId());
							json.put("collection_name", c.getCollectionName());
							collections.add(json);
						}
						storyModel.setCollections(collections);
					}else{
						delArray.add("collections");
					}
					
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
	
	public JSONObject getSlideStoryByStory(Story story){

		JSONObject storyModel = new JSONObject();
		storyModel.put("id",story.getId());
		storyModel.put("image_count",story.getImage_count());
		storyModel.put("url",story.getTinyURL());
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
		storyModel.put("author",authorJson);


		JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
		String type = jsonObject.getString("type");

		if (type.equals("text")) {
			TextCover coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
			storyModel.put("cover_media",JSONObject.fromObject(coverMedia));
		} else if (type.equals("image")) {
			ImageCover coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
			storyModel.put("cover_media",JSONObject.fromObject(coverMedia));
		} else if (type.equals("multimedia")) {
			storyModel.put("cover_media",jsonObject);
		}


		if (!Strings.isNullOrEmpty(story.getTitle())) {
			storyModel.put("title",story.getTitle());
		}
		
		
		return storyModel;
	
	}
	
	@Override
	public JSONObject getStoryColumnsByColumnsId(Long columns_id, Long loginUserid, HttpServletRequest request) {


		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<JSONObject> storyModelList = new ArrayList<JSONObject>();
		int count = 20;
		String type = "publish";
		JSONObject json = new JSONObject();
		
		JSONObject storyModel = null;

		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Columns> cList = columnsDao.getAllColumns();
			List<JSONObject> columnsList = new ArrayList<JSONObject>();
			JSONObject columnsJson = null;
			if(cList != null && cList.size() > 0){
				for(Columns c:cList){
					columnsJson = new JSONObject();
					columnsJson.put("id",c.getId());
					if(!Strings.isNullOrEmpty(c.getDescription())){
						columnsJson.put("description", c.getDescription());
					}
					columnsJson.put("column_name",c.getColumn_name());
					columnsJson.put("cover_media",JSONObject.fromObject(c.getCover_image()));
					columnsList.add(columnsJson);
				}
				json.put("columns", columnsList);
			}
			List<Story> storyList = this.columnsStoryDao.getStoriesByColumns(columns_id, count, type);
			if ((storyList != null) && (storyList.size() > 0))
				for (Story story : storyList) {
					storyModel = getStoryEventByStoryLoginUser(story, loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Columns> cList = columnsDao.getAllColumns();
			List<JSONObject> columnsList = new ArrayList<JSONObject>();
			JSONObject columnsJson = null;
			if(cList != null && cList.size() > 0){
				for(Columns c:cList){
					columnsJson = new JSONObject();
					columnsJson.put("id",c.getId());
					if(!Strings.isNullOrEmpty(c.getDescription())){
						columnsJson.put("description", c.getDescription());
					}
					columnsJson.put("column_name",c.getColumn_name());
					columnsJson.put("cover_media",JSONObject.fromObject(c.getCover_image()));
					columnsList.add(columnsJson);
				}
				json.put("columns", columnsList);
			}
			count = Integer.parseInt(countStr);
			List<Story> storyList = this.columnsStoryDao.getStoriesByColumns(columns_id, count, type);
			if ((storyList != null) && (storyList.size() > 0))
				for (Story story : storyList) {
					storyModel = getStoryEventByStoryLoginUser(story, loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Story> storyList = this.columnsStoryDao.getStoriesPageByColumns(columns_id, count, since_id, 1, type);
			if ((storyList != null) && (storyList.size() > 0))
				for (Story story : storyList) {
					storyModel = getStoryEventByStoryLoginUser(story, loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (!Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long since_id = Long.valueOf(Long.parseLong(sinceIdStr));
			List<Story> storyList = this.columnsStoryDao.getStoriesPageByColumns(columns_id, count, since_id, 1, type);
			if ((storyList != null) && (storyList.size() > 0))
				for (Story story : storyList) {
					storyModel = getStoryEventByStoryLoginUser(story, loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (!Strings.isNullOrEmpty(maxIdStr))) {
			count = Integer.parseInt(countStr);
			Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
			List<Story> storyList = this.columnsStoryDao.getStoriesPageByColumns(columns_id, count, max_id, 2, type);
			if ((storyList != null) && (storyList.size() > 0)) {
				for (Story story : storyList) {
					storyModel = getStoryEventByStoryLoginUser(story, loginUserid);
					storyModelList.add(storyModel);
				}
			}
		}
		json.put("stories", storyModelList);
		return json;

	
	}
	
	public JSONObject getStoryEventByStoryLoginUser(Story story, Long loginUserid) {
		StoryEventNew storyModel = new StoryEventNew();
		storyModel.setId((Long) story.getId());
		User user = story.getUser();
		storyModel.setImage_count(story.getImage_count());
		storyModel.setUrl(story.getTinyURL());
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
		storyModel.setAuthor(authorJson);
		storyModel.setCreated_time(story.getCreated_time());
		int count = this.commentDao.getCommentCountById((Long) story.getId());
		storyModel.setComment_count(count);
		if (loginUserid != null && loginUserid > 0) {
			User loginUser = userDao.get(loginUserid);
			Set<Story> sSet = loginUser.getRepost_story();
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

			Set<Story> likeStory = loginUser.getLike_story();
			List<Story> lsList = new ArrayList<Story>();
			if (likeStory != null && likeStory.size() > 0) {
				Iterator<Story> it = likeStory.iterator();
				while (it.hasNext()) {
					lsList.add(it.next());
				}
				if (lsList.contains(story)) {
					storyModel.setLiked_by_current_user(true);
					;
				} else {
					storyModel.setLiked_by_current_user(false);
				}
			} else {
				storyModel.setLiked_by_current_user(false);
			}
		}

		Set<User> like_set = story.getLike_users();
		if (like_set != null && like_set.size() > 0) {
			storyModel.setLike_count(like_set.size());
		} else {
			storyModel.setLike_count(0);
		}

		JSONObject jsonObject = JSONObject.fromObject(story.getCover_page());
		String type = jsonObject.getString("type");
		CoverMedia coverMedia = null;
		IframeCover iframeCover = null;
		if (type.equals("text")) {
			coverMedia = (TextCover) JSONObject.toBean(jsonObject, TextCover.class);
			storyModel.setCover_media(JSONObject.fromObject(coverMedia));
		} else if (type.equals("image")) {
			coverMedia = (ImageCover) JSONObject.toBean(jsonObject, ImageCover.class);
			storyModel.setCover_media(JSONObject.fromObject(coverMedia));
		} else if (type.equals("multimedia")) {
			storyModel.setCover_media(jsonObject);
		}

		if (!Strings.isNullOrEmpty(story.getTitle()))
			storyModel.setTitle(story.getTitle());
		else {
			storyModel.setTitle(null);
		}

		if (!Strings.isNullOrEmpty(story.getSummary())) {
			storyModel.setSummary(story.getSummary());
		} else {
			storyModel.setSummary(null);
		}

		storyModel.setRecommend_date(story.getRecommend_date());

		JsonConfig configs = new JsonConfig();
		List<String> delArray = new ArrayList<String>();
		/*Set<Columns> colSet = story.getColumns();
		if (colSet != null && colSet.size() > 0) {
			Iterator<Columns> iter = colSet.iterator();
			Columns c = iter.next();
			JSONObject columnsJson = new JSONObject();
			columnsJson.put("id", c.getId());
			columnsJson.put("column_name", c.getColumn_name());
			storyModel.setColumns(columnsJson);
		} else {
			delArray.add("columns");
		}*/
		
		Set<Collection> cSet = story.getCollections();
		if(cSet != null && cSet.size() > 0){
			Iterator<Collection> iter = cSet.iterator();
			List<JSONObject> collections = new ArrayList<JSONObject>();
			while(iter.hasNext()){
				Collection c = iter.next();
				JSONObject json = new JSONObject();
				json.put("id",c.getId());
				json.put("collection_name", c.getCollectionName());
				collections.add(json);
			}
			storyModel.setCollections(collections);
		}else{
			delArray.add("collections");
		}
		
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

		return storyJson;
	}
	
	public Response create(JSONObject user)throws Exception {
		User u = new User();
		JSONObject jo = new JSONObject();
		
		if (user != null) {
			System.out.println("################user#############"+user);
			String uuid = user.getString("uuid");
			String rsa = user.getString("rsa");
			String path = getClass().getResource("/../../META-INF/pkcs8_rsa_1024_priv.pem").getPath();
			String private_key = getKeyFromFile(path);
			RSAPrivateKey privateKey = loadPrivateKey(private_key);
			String decodeStr = decryptWithBase64(privateKey,rsa);
			String uuidDecode = ""; 
			if(!Strings.isNullOrEmpty(decodeStr)){
				uuidDecode = decodeStr.split(",")[0];
			}
			System.out.println("#######uuid == uuidDecode#########"+uuid+"---"+uuidDecode);
			if(uuid.trim().equals(uuidDecode.trim())){
				

				String chars = "abcde0f12g3hi4jk5l6m7n8o9pqrstuvwxyz";
		    	StringBuffer sb = new StringBuffer();
		    	for(int i=0;i<10;i++){
		    		char c = chars.charAt((int)(Math.random() * 36));
		    		sb.append(c);
		    	}
				String pwd = Base64Utils.encodeToString(sb.toString().getBytes());
				u.setPassword(pwd);

				u.setUsername(user.getString("description"));
				u.setSalt(initSalt().toString());
				u.setStatus("enabled");
				u.setUser_type("normal");
				this.userDao.save(u);
				Configuration c = new Configuration();
				c.setNew_admin_push(true);
				c.setNew_comment_on_your_comment_push(true);
				c.setNew_comment_on_your_story_push(true);
				c.setNew_favorite_from_following_push(true);
				c.setNew_follower_push(true);
				c.setNew_story_from_following_push(true);
				c.setRecommended_my_story_push(true);
				c.setReposted_my_story_push(true);
				
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
				
				LinkAccounts la = new LinkAccounts();
				la.setAuth_token(user.getString("auth_token"));
				if(user.containsKey("avatar_url")){
					la.setAvatar_url(user.getString("avatar_url"));
				}
				la.setDescription(user.getString("description"));
				la.setRefreshed_at(user.getString("refreshed_at"));
				la.setService(user.getString("service"));
				la.setUser_id(u.getId());
				la.setUuid(uuid);
				this.linkAccountsDao.save(la);
				jo.put("userid", u.getId());
				String raw = u.getId() + u.getPassword() + u.getCreated_time();

				String token = EncryptionUtil.hashMessage(raw);
				jo.put("access_token", token);
				jo.put("token_timestamp", u.getCreated_time());
				jo.put("username",u.getUsername());
				jo.put("avatar_image", la.getAvatar_url());
				return Response.status(Response.Status.CREATED).entity(jo).build();
			}
			
			jo.put("status", "invalid request");
			jo.put("code", Integer.valueOf(10107));
			jo.put("error_message", "恶意请求");
			return Response.status(Response.Status.BAD_REQUEST).entity(jo).build();
			
		}
		
		jo.put("status", "invalid_request");
		jo.put("code", Integer.valueOf(10010));
		jo.put("error_message", "Invalid payload parameters");
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
	
	public byte[] initSalt() {
		byte[] b = new byte[8];
		Random random = new Random();
		random.nextBytes(b);
		return b;
	}
	
	public GetuiModel getGetuiInfo(){
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
	
	public Response loginLinkAccounts(LinkAccountWebModel la)throws Exception {
		JSONObject auth = new JSONObject();
		Object[] link = null;
		
		if (la != null) {
			String uuid = la.getUuid();
			String service = la.getService();
			String rsa = la.getRsa();
			String path = getClass().getResource("/../../META-INF/pkcs8_rsa_1024_priv.pem").getPath();
			String private_key = getKeyFromFile(path);
			RSAPrivateKey privateKey = loadPrivateKey(private_key);
//			String privateKeyStr = loadPrivateKeyByFile(path);
//			RSAPrivateKey privateKey = loadPrivateKeyByStr(privateKeyStr);
//			byte[] decode = decrypt(privateKey,rsa.getBytes());
//			String decodeStr = decode.toString();
			String decodeStr = decryptWithBase64(privateKey,rsa);
			String uuidDecode = ""; 
			if(!Strings.isNullOrEmpty(decodeStr)){
				uuidDecode = decodeStr.split(",")[0];
			}
			System.out.println("################decodeStr######"+decodeStr);
			if(uuid.equals(uuidDecode)){
				link = this.linkAccountsDao.getLinkAccountsByUUIDAndService(uuid,service);
				
				if (link != null) {
					User user = (User)link[1];
					String raw = user.getId() + user.getPassword() + user.getCreated_time();
					String token = EncryptionUtil.hashMessage(raw);

					System.out.println("userId--->" + user.getId());
					auth.put("userid", user.getId());
					auth.put("access_token", token);
					auth.put("token_timestamp", user.getCreated_time());
					auth.put("username",user.getUsername());
					auth.put("avatar_image",la.getAvatar_url());
					System.out.println(auth.toString());
					return Response.status(Response.Status.OK).entity(auth).build();
				}
				auth.put("status", "no_user");
				auth.put("code", Integer.valueOf(10080));
				auth.put("error_message", "Without this user.");
			}else{
				auth.put("status", "invalid request");
				auth.put("code", Integer.valueOf(10010));
				auth.put("error_message", "Invalid request payload");
			}
			

			return Response.status(Response.Status.BAD_REQUEST).entity(auth).build();
		}

		auth.put("status", "invalid request");
		auth.put("code", Integer.valueOf(10010));
		auth.put("error_message", "Invalid request payload");
		return Response.status(Response.Status.BAD_REQUEST).entity(auth).build();
	}
	
	
	/** 
     * 从文件中加载私钥 
     *  
     * @param keyFileName 
     *            私钥文件名 
     * @return 是否成功 
     * @throws Exception 
     */  
    public static String loadPrivateKeyByFile(String path) throws Exception {  
        try {  
        	
            BufferedReader br = new BufferedReader(new FileReader(new File(path)  
                    ));  
            String readLine = null;  
            StringBuilder sb = new StringBuilder();  
            while ((readLine = br.readLine()) != null) {  
                sb.append(readLine);  
            }  
            br.close();  
            return sb.toString();  
        } catch (IOException e) {  
            throw new Exception("私钥数据读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("私钥输入流为空");  
        }  
    }  
  
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)  
            throws Exception {  
        try {  
            byte[] buffer = Base64.decodeBase64(privateKeyStr);  
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("私钥非法");  
        } catch (NullPointerException e) {  
            throw new Exception("私钥数据为空");  
        }  
    }  
    
    /** 
     * 私钥解密过程 
     *  
     * @param privateKey 
     *            私钥 
     * @param cipherData 
     *            密文数据 
     * @return 明文 
     * @throws Exception 
     *             解密过程中的异常信息 
     */  
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData)  
            throws Exception {  
        if (privateKey == null) {  
            throw new Exception("解密私钥为空, 请设置");  
        }  
        Cipher cipher = null;  
        try {  
            // 使用默认RSA  
            cipher = Cipher.getInstance("RSA");  
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, privateKey);  
            byte[] output = cipher.doFinal(cipherData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此解密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("解密私钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("密文长度非法");  
        } catch (BadPaddingException e) {  
            throw new Exception("密文数据已损坏");  
        }  
    }
    
    public String getKeyFromFile(String filePath) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        
        String line = null;
        List<String> list = new ArrayList<String>();
        while ((line = bufferedReader.readLine()) != null){
            list.add(line);
        }
        
        // remove the firt line and last line
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < list.size() - 1; i++) {
            stringBuilder.append(list.get(i)).append("\r");
        }
        
        String key = stringBuilder.toString();
        return key;
    }
    
    
    /** 
     * 从文件中加载私钥 
     * @param keyFileName 私钥文件名 
     * @return 是否成功 
     * @throws Exception  
     */  
    public void loadPrivateKey(InputStream in) throws Exception{  
        try {  
            BufferedReader br= new BufferedReader(new InputStreamReader(in));  
            String readLine= null;  
            StringBuilder sb= new StringBuilder();  
            while((readLine= br.readLine())!=null){  
                if(readLine.charAt(0)=='-'){  
                    continue;  
                }else{  
                    sb.append(readLine);  
                    sb.append('\r');  
                }  
            }  
            loadPrivateKey(sb.toString());  
        } catch (IOException e) {  
            throw new Exception("私钥数据读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("私钥输入流为空");  
        }  
    }  
  
    public RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception{  
        try {  
            BASE64Decoder base64Decoder= new BASE64Decoder();  
            byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);  
            PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);  
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
            RSAPrivateKey privateKey= (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
            return privateKey;
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e) {  
            e.printStackTrace();
            throw new Exception("私钥非法");  
        } catch (IOException e) {  
            throw new Exception("私钥数据内容读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("私钥数据为空");  
        }  
    }  

    public String decryptWithBase64(RSAPrivateKey privateKey,String base64String) throws Exception {
        //  http://commons.apache.org/proper/commons-codec/ : org.apache.commons.codec.binary.Base64
        // sun.misc.BASE64Decoder
        byte[] binaryData = decrypt(privateKey, new BASE64Decoder().decodeBuffer(base64String) /*org.apache.commons.codec.binary.Base64.decodeBase64(base46String.getBytes())*/);
        String string = new String(binaryData);
        return string;
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

		return commentModel;
	}
}
