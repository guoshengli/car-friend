package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Collection;
import com.revolution.rest.model.CollectionStory;
import com.revolution.rest.model.Story;

public interface CollectionStoryDao extends BaseDao<CollectionStory, Long>
{
  public void delete(Long paramLong1, Long paramLong2);

  public void deleteByCollectionId(String paramString);

  public Story getStoryByCollectionIdAndStoryId(Long paramLong1, Long paramLong2);

  public CollectionStory getCollectionStoryByCollectionIdAndStoryId(Long paramLong1, Long paramLong2);

  public List<CollectionStory> getCollectionStoryByCollectionId(Long paramLong);

  public List<Story> getStoriesPage(Long paramLong, int paramInt, String paramString);

  public List<Story> getStoriesPageByCollectionId(Long paramLong1, int paramInt1, Long paramLong2, int paramInt2, String paramString);

  public List<Story> getStoriesByCollectionId(Long paramLong);

  public Collection getCollectionByStoryId(Long paramLong);

  public void deleteCollectionStoryByCollectionId(Long paramLong);

  public void deleteCollectionStoryByCollectionIdAndStoryId(Long paramLong1, Long paramLong2);

  public List<Story> getFeturedStoriesPage(Long paramLong, int paramInt, String paramString);

  public List<Story> getFeturedStoriesPageByCollectionId(Long paramLong1, int paramInt1, Long paramLong2, int paramInt2, String paramString);
  
  public List<Story> getStoryByRand(int collection);
  
  public List<Story> getStoryByCollectionIds(String ids);
  
  public CollectionStory getCollectionStoryByStoryId(Long storyId);
  
  public Story getStoryByStoryid(Long storyId);
  
  public List<Story> getFeturedStoriesFollow(String ids, int count, String type);
  
  public List<Story> getFeturedStoriesPageByCollections(String ids, int count, Long storyId, int identifier,
			String type);
  
  public List<Story> getStoryByCollectionIds(String ids,int count);
  
  public List<Story> getStoryByCollectionIds(String ids,int count,Long storyId, int identifier);
}

