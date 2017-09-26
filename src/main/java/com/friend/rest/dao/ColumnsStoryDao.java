package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.ColumnsStory;
import com.friend.rest.model.Story;

public interface ColumnsStoryDao extends BaseDao<ColumnsStory, Long> {
	
	public Story getStoryByStoryid(Long storyId);
	
	public List<Story> getStoriesByColumns(Long columns_id, int count,String type);
	  
	public List<Story> getStoriesPageByColumns(Long columns_id, int count, Long storyId, int identifier,String type);
	
	public void deleteColumnsStoryByColumnsIdAndStoryId(Long columns_id,Long storyId);
	
	public ColumnsStory getColumnsStoryByColumnsIdAndStoryId(Long columnsId,Long storyId);
	
	public List<ColumnsStory> getColumnsStoryListByStoryId(Long storyId);
	
	public void deleteColumnsSotryList(String ids);
}
