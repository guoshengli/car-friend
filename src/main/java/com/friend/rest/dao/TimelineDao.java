package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.Timeline;

public abstract interface TimelineDao extends BaseDao<Timeline, Long>
{
  public abstract List<Timeline> getTimelinesByUserId(Long paramLong);

  public abstract List<Timeline> getTimelinesPageByUserId(Long paramLong, int paramInt, String paramString);

  public abstract List<Timeline> getTimelinesPageByUserId(Long paramLong1, Long paramLong2, int paramInt1, int paramInt2, String paramString);

  public abstract void saveTimelines(List<Timeline> paramList);

  public abstract void deleteTimelines(Long paramLong1, Long paramLong2);

  public abstract void deleteTimelineByType(Long paramLong1, Long paramLong2, String paramString);

  public abstract Timeline getTimelineByUseridAndStoryIdAndType(Long paramLong1, Long paramLong2, String paramString);

  public abstract void deleteTimelineByStoryId(Long paramLong);
  
  public abstract void deleteTimelineByStoryIdAndType(Long paramLong);

  public abstract List<Timeline> getTimelinesPageByRecommand(Long paramLong, int paramInt, String paramString,String dates,String date_before);

  public abstract List<Timeline> getTimelinesPageByRecommand(Long paramLong1, Long paramLong2, int paramInt1, int paramInt2, String paramString,String dates,String date_before);
  
  public List<Timeline> getTimelineByRecommandAndRand(int recommand);
  
  public List<Timeline> getTimelineByStoryIdAndType(String storyIds, String type);
  
  public List<Timeline> getTimelineByUserIdAndType(Long userId,String type);
  
  public Timeline getTimelineByStoryIdAndType(Long storyId,String type);
  
  public List<Timeline> getTimelineByRecommandation(int count);
  
  public List<Timeline> getTimelineByRecommandation(Long timelineId, int count, int identify);
  
  public List<Timeline> getTimelineBySquare(int count);
  
  public List<Timeline> getTimelineBySquare(Long timelineId, int count, int identify);
  
  public List<Timeline> getTimelineByHome(int count);
  
  public List<Timeline> getTimelineByHome(Long timelineId, int count, int identify);
  
  public List<Timeline> getTimelineByUserIdAndType(Long userId, String type,int count);
  
  public List<Timeline> getTimelineByUserIdAndType(Long userId, String type,int count,Long id,int identify);
  
  
  public List<Timeline> getTimelineByUserIdAndTypeAndStatus(Long userId, String type,int count,String status);
  
  public List<Timeline> getTimelineByUserIdAndTypeAndStatus(Long userId, String type,int count,Long id,int identify,String status);
  
}

