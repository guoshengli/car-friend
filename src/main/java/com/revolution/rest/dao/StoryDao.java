package com.revolution.rest.dao;

import java.util.List;
import java.util.Map;

import com.revolution.rest.model.Story;

public abstract interface StoryDao extends BaseDao<Story, Long>
{
  public abstract List<Story> getAllByUserId(Long paramLong);

  public abstract List<Story> getAllByCollectionId(Long paramLong);

  public abstract void update(Story paramStory);

  public abstract void save(Story paramStory);

  public abstract List<Story> getStoriesPageByNull(Long paramLong1, int paramInt, String paramString, Long paramLong2);

  public abstract List<Story> getStoriesPageByStoryId(Long paramLong1, int paramInt1, Long paramLong2, int paramInt2, String paramString, Long paramLong3);

  public abstract List<Story> getDraftStories(Long paramLong);

  public abstract int getStoryCount(Long paramLong);

  public abstract Story getStoryByIdAndLoginUserid(Long paramLong1, Long paramLong2);

  public abstract Story getStoryByIdAndStatus(Long paramLong, String paramString1, String paramString2);

  public abstract Story getStoryByIdAndStatus(Long paramLong, String paramString);

  public abstract List<Story> getStoriesByNow(Long paramLong, String paramString);

  public abstract List<Story> getStoriesByRandThree(Long storyId);

  public abstract List<Story> getStoriesByTimeAndNull(int paramInt, String paramString1, String paramString2, String paramString3);

  public abstract List<Story> getStoriesByViewAndNull(int paramInt, String paramString1, String paramString2, String paramString3);

  public abstract List<Story> getStoriesByTime(Long paramLong, int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3);

  public abstract List<Map<String,Object>> getStoriesByView(Long paramLong, int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3);
  
  public Story getStoryByURL(String url);
  
  public List<Story> getStoryByTime(String start,String end);
  
  public List<Story> getStoryByFour(Long userId);
  
  public void updateStoryResource(Long storyId,String resource);
  
  public List<Story> getStoryListByIds(String ids);
  
  public Story getStoryByTidAndPid(Long tid,Long pid);
}

