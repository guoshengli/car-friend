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
import com.revolution.rest.dao.UserDao;
import com.revolution.rest.model.Collection;
import com.revolution.rest.model.Columns;
import com.revolution.rest.model.PublisherInfo;
import com.revolution.rest.model.Story;
import com.revolution.rest.model.User;
import com.revolution.rest.service.model.ColumnsModel;
import com.revolution.rest.service.model.CoverMedia;
import com.revolution.rest.service.model.IframeCover;
import com.revolution.rest.service.model.ImageCover;
import com.revolution.rest.service.model.PublisherInfoModel;
import com.revolution.rest.service.model.StoryEventNew;
import com.revolution.rest.service.model.TextCover;
import com.revolution.rest.service.model.VideoCover;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@Transactional
public class ColumnsServiceImpl implements ColumnsService {

	@Autowired
	private CommentDao commentDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ColumnsStoryDao columnsStoryDao;

	@Autowired
	private ColumnsDao columnsDao;

	@Override
	public Response createColumnsStory(Long loginUserid, Long columns_id, Long story_id) {
		return null;
	}

	@Override
	public List<JSONObject> getStoryByColumnsId(Long columns_id, Long loginUserid, HttpServletRequest request) {

		String countStr = request.getParameter("count");
		String sinceIdStr = request.getParameter("since_id");
		String maxIdStr = request.getParameter("max_id");
		List<JSONObject> storyModelList = new ArrayList<JSONObject>();
		int count = 20;
		String type = "publish";
		JSONObject storyModel = null;

		if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
			List<Story> storyList = this.columnsStoryDao.getStoriesByColumns(columns_id, count, type);
			if ((storyList != null) && (storyList.size() > 0))
				for (Story story : storyList) {
					storyModel = getStoryEventByStoryLoginUser(story, loginUserid);
					storyModelList.add(storyModel);
				}
		} else if ((!Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(sinceIdStr))
				&& (Strings.isNullOrEmpty(maxIdStr))) {
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
		return storyModelList;

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

	@Override
	public List<ColumnsModel> getAllColumns() {
		List<Columns> cList = columnsDao.getAllColumns();
		List<ColumnsModel> cmList = new ArrayList<ColumnsModel>();
		ColumnsModel cm = null;
		if (cList != null && cList.size() > 0) {
			for (Columns c : cList) {
				cm = new ColumnsModel();
				cm.setId(c.getId());
				cm.setColumn_name(c.getColumn_name());
				if (!Strings.isNullOrEmpty(c.getDescription())) {
					cm.setDescription(c.getDescription());
				} else {
					cm.setDescription("");
				}

				cm.setCover_media(JSONObject.fromObject(c.getCover_image()));
				cmList.add(cm);
			}
		}
		return cmList;
	}


}
