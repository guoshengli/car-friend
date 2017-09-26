package com.friend.rest.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.friend.rest.dao.FMapDao;
import com.friend.rest.dao.StoryDao;
import com.friend.rest.model.FMap;
import com.friend.rest.model.Story;
import com.friend.rest.model.User;
import com.google.common.base.Strings;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Transactional
public class FMapServiceImpl implements FMapService {
	
	@Autowired
	private FMapDao fmapDao;
	
	@Autowired
	private StoryDao storyDao;
	
	@Override
	public Response saveFmap(JSONObject param) {
		JSONObject result = new JSONObject();
		System.out.println("fmap-----?>>>>>>>>>>>>"+param.toString());
		if(param != null){
			FMap fmap = null;
			String type = param.getString("type");
			int ssm_id = param.getInt("ssm_id");
			JSONArray wiki = param.getJSONArray("wiki_id");
			Object[] obj = wiki.toArray();
			for(Object o:obj){
				fmap = new FMap();
				fmap.setType(type);
				fmap.setSsm_id(ssm_id);
				fmap.setWiki_id(Long.parseLong(o.toString()));
				fmapDao.save(fmap);
			}
			result.put("status", "success");
			return Response.status(Response.Status.OK).entity(result).build();
		}else{
			result.put("status", "invalid_param");
			result.put("code", 10010);
			result.put("message", "���Ϸ��Ĳ���");
			return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
		}
		
	}

	@Override
	public Response timeline(String type, int ssm_id, HttpServletRequest request) {
		String countStr = request.getParameter("count");
		String maxIdStr = request.getParameter("max_id");
		List<FMap> fmapList = null;
		int count = 20;
		JSONObject storyJson = null;
		JSONObject result = new JSONObject();
		List<JSONObject> sjsonList = new ArrayList<JSONObject>();
		if(!Strings.isNullOrEmpty(type)){
			if ((Strings.isNullOrEmpty(countStr)) && (Strings.isNullOrEmpty(maxIdStr))) {
				fmapList = fmapDao.getFMapList(count,type,ssm_id);
				if(fmapList != null && fmapList.size() > 0){
					for(FMap fmap:fmapList){
						storyJson = new JSONObject();
						Long storyId = fmap.getWiki_id();
						Story story = storyDao.get(storyId);
						storyJson.put("id",story.getId());
						storyJson.put("ssm_wiki_id",fmap.getId());
						storyJson.put("title",story.getTitle());
						storyJson.put("update_time", story.getCreated_time());
						storyJson.put("cover_image",story.getCover_page());
						storyJson.put("summary",story.getSummary());
						User user = story.getUser();
						JSONObject author = new JSONObject();
						author.put("id",user.getId());
						author.put("user_name",user.getUsername());
						author.put("avatar",user.getAvatarImage());
						storyJson.put("author", author);
						sjsonList.add(storyJson);
					}
				}
			} else if ((!Strings.isNullOrEmpty(countStr))
					&& (Strings.isNullOrEmpty(maxIdStr))) {

				fmapList = fmapDao.getFMapList(count,type,ssm_id);
				if(fmapList != null && fmapList.size() > 0){
					for(FMap fmap:fmapList){
						storyJson = new JSONObject();
						Long storyId = fmap.getWiki_id();
						Story story = storyDao.get(storyId);
						storyJson.put("id",story.getId());
						
						storyJson.put("ssm_wiki_id",fmap.getId());
						storyJson.put("title",story.getTitle());
						storyJson.put("update_time", story.getCreated_time());
						storyJson.put("cover_image",story.getCover_page());
						storyJson.put("summary",story.getSummary());
						User user = story.getUser();
						JSONObject author = new JSONObject();
						author.put("id",user.getId());
						author.put("user_name",user.getUsername());
						author.put("avatar",user.getAvatarImage());
						storyJson.put("author", author);
						sjsonList.add(storyJson);
					}
				}
			
			} else if ((Strings.isNullOrEmpty(countStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				fmapList = fmapDao.getFMapList(count, max_id,type,ssm_id);
				if(fmapList != null && fmapList.size() > 0){
					for(FMap fmap:fmapList){
						storyJson = new JSONObject();
						Long storyId = fmap.getWiki_id();
						Story story = storyDao.get(storyId);
						storyJson.put("id",story.getId());
						storyJson.put("ssm_wiki_id",fmap.getId());
						storyJson.put("title",story.getTitle());
						storyJson.put("update_time", story.getCreated_time());
						storyJson.put("cover_image",story.getCover_page());
						storyJson.put("summary",story.getSummary());
						User user = story.getUser();
						JSONObject author = new JSONObject();
						author.put("id",user.getId());
						author.put("user_name",user.getUsername());
						author.put("avatar",user.getAvatarImage());
						storyJson.put("author", author);
						sjsonList.add(storyJson);
					}
				}
			} else if ((!Strings.isNullOrEmpty(countStr))
					&& (!Strings.isNullOrEmpty(maxIdStr))) {
				count = Integer.parseInt(countStr);
				Long max_id = Long.valueOf(Long.parseLong(maxIdStr));
				fmapList = fmapDao.getFMapList(count, max_id,type,ssm_id);
				if(fmapList != null && fmapList.size() > 0){
					for(FMap fmap:fmapList){
						storyJson = new JSONObject();
						Long storyId = fmap.getWiki_id();
						Story story = storyDao.get(storyId);
						storyJson.put("id",story.getId());
						storyJson.put("ssm_wiki_id",fmap.getId());
						storyJson.put("title",story.getTitle());
						storyJson.put("update_time", story.getCreated_time());
						storyJson.put("cover_image",story.getCover_page());
						storyJson.put("summary",story.getSummary());
						User user = story.getUser();
						JSONObject author = new JSONObject();
						author.put("id",user.getId());
						author.put("user_name",user.getUsername());
						author.put("avatar",user.getAvatarImage());
						storyJson.put("author", author);
						sjsonList.add(storyJson);
					}
				}
			}
			return Response.status(Response.Status.OK).entity(sjsonList).build();
		}else{
			result.put("status", "invalid_param");
			result.put("code", 10010);
			result.put("message", "���Ϸ��Ĳ���");
			return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
		}
		
		
	}

	@Override
	public Response wiki_summary(HttpServletRequest request) {
		String storyIds = request.getParameter("wiki_ids");
		JSONObject storyJson = null;
		JSONObject result = new JSONObject();
		List<JSONObject> sjsonList = new ArrayList<JSONObject>();
		if(!Strings.isNullOrEmpty(storyIds)){
			List<Story> storyList = storyDao.getStoryListByIds(storyIds);
			if(storyList != null && storyList.size() > 0){
				for(Story story:storyList){
					storyJson = new JSONObject();
					storyJson.put("id",story.getId());
					storyJson.put("title",story.getTitle());
					storyJson.put("update_time", story.getCreated_time());
					storyJson.put("cover_image",story.getCover_page());
					storyJson.put("summary",story.getSummary());
					User user = story.getUser();
					JSONObject author = new JSONObject();
					author.put("id",user.getId());
					author.put("user_name",user.getUsername());
					author.put("avatar",user.getAvatarImage());
					storyJson.put("author", author);
					sjsonList.add(storyJson);
				}
			}
			return Response.status(Response.Status.OK).entity(sjsonList).build();
		}else{
			result.put("status", "invalid_param");
			result.put("code", 10010);
			result.put("message", "���Ϸ��Ĳ���");
			return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
		}
	}
	
}
